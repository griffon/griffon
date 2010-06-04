/*
 * Copyright 2004-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_plugin_dev_called")) return
_griffon_plugin_dev_called = true

import groovy.xml.MarkupBuilder
import griffon.util.GriffonUtil

/**
 * Gant script that deals with those tasks required for plugin developers
 * (as opposed to plugin users).
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonPackage")
includeTargets << griffonScript("PackageAddon")
includeTargets << griffonScript("_GriffonDocs")

pluginIncludes = [
    metadataFile.name,
    "*GriffonPlugin.groovy",
    "plugin.xml",
    "griffon-app/conf/**",
    "lib/**",
    "scripts/**",
//    "src/**",
    "LICENSE*",
    "README*"
]

pluginExcludes = [
    "griffon-app/conf/Application.groovy",
    "griffon-app/conf/BuildConfig.groovy",
    "griffon-app/conf/Builder.groovy",
    "griffon-app/conf/Config.groovy",
    "griffon-app/conf/metainf/**",
    "**/.svn/**",
    "test/**",
    "**/CVS/**"
]

target(pluginConfig:"setup the plugin config"){
    depends(checkVersion, createStructure, compile, packagePlugins)

    def pluginFile
    new File("${basedir}").eachFile {
        if(it.name.endsWith("GriffonPlugin.groovy")) {
            pluginFile = it
        }
    }

    if(!pluginFile) ant.fail("Plugin file not found for plugin project")

    try {
        pluginClass = classLoader.loadClass(pluginFile.name[0..-8])
        plugin = pluginClass.newInstance()
    }
    catch(Throwable t) {
        event("StatusError", [ t.message])
//        GriffonUtil.deepSanitize(t)
        t.printStackTrace(System.out)
        ant.fail("Cannot instantiate plugin file")
    }
    pluginName = GriffonUtil.getScriptName(GriffonUtil.getLogicalName(pluginClass, "GriffonPlugin"))
}

target(pluginDocs: "Generates and packages plugin documentation") {
    depends(checkVersion, parseArguments, pluginConfig, packageAddon)

    pluginDocDir = "${projectTargetDir}/docs"
    ant.mkdir(dir: pluginDocDir)

    // copy 'raw' docs if they exists
    ant.copy(todir: pluginDocDir, failonerror: false) {
        fileset(dir: "${basedir}/src/doc")
    }

    // package sources
    def srcMainDir = new File("${basedir}/src/main")
    def testSharedDir = new File("${basedir}/test/shared")
    def testSharedDirPath = new File(griffonSettings.testClassesDir, 'shared')

    if(srcMainDir.list() || testSharedDir.list()) {
        def jarFileName = "${pluginDocDir}/griffon-${pluginName}-${plugin.version}-sources.jar"

        ant.uptodate(property: 'pluginSourceJarUpToDate', targetfile: jarFileName) {
            srcfiles(dir: srcMainDir, includes: "**/*")
            srcfiles(dir: testSharedDir, includes: "**/*")
            srcfiles(dir: classesDirPath, includes: "**/*")
            srcfiles(dir: testSharedDirPath, includes: "**/*")
        }
        boolean uptodate = ant.antProject.properties.pluginSourceJarUpToDate
        if(!uptodate) {
            ant.jar(destfile: jarFileName) {
                if(srcMainDir.list()) fileset(dir: srcMainDir, includes: '**/*.groovy, **/*.java')
                if(testSharedDir.list()) fileset(dir: testSharedDir, includes: '**/*.groovy, **/*.java')
            }
        }

        if(!argsMap.nodoc) {
            def javadocDir = "${projectTargetDir}/docs/api"
            invokeGroovydoc(destdir: javadocDir,
                sourcepath: [srcMainDir, testSharedDir],
                windowtitle: "${pluginName} ${plugin.version}",
                doctitle: "${pluginName} ${plugin.version}")
            jarFileName = "${pluginDocDir}/griffon-${pluginName}-${plugin.version}-javadoc.jar"
            ant.jar(destfile: jarFileName) {
                fileset(dir: javadocDir)
            }
            ant.delete(dir: javadocDir, quiet: true)
        }
    }
}

target(packagePlugin:"Packages a Griffon plugin") {
    depends (pluginDocs)
    event("PackagePluginStart", [pluginName,plugin])
    
    // Remove the existing 'plugin.xml' if there is one.
    def pluginXml = new File(basedir, "plugin.xml")
    pluginXml.delete()

    // Use MarkupBuilder with indenting to generate the file.
    def writer = new IndentPrinter(new PrintWriter(new FileWriter(pluginXml)))
    def xml = new MarkupBuilder(writer)

    // Write the content!
    def props = ['author','authorEmail','title','documentation']
    def jdk = plugin.properties['jdk'] ?: "1.5"

    pluginGriffonVersion = "${griffonVersion} > *"
    if(plugin.metaClass.hasProperty(plugin, "griffonVersion")) {
        pluginGriffonVersion = plugin.griffonVersion
    }

    xml.'plugin'(name:"${pluginName}", version:"${plugin.version}", griffonVersion: pluginGriffonVersion, jdk:"${jdk}") {
        props.each {
            if(plugin.properties[it]) "${it}"(plugin.properties[it].toString().trim())
        }
        ['toolkits', 'platforms'].each {
            if(plugin.properties[it]) "${it}"(plugin.properties[it].join(','))
        }
        if(plugin.description) synopsis(plugin.description.trim())
        license(plugin.properties['license'] ?: '<UNKNOWN>')
        dependencies {
            if(plugin.metaClass.hasProperty(plugin,'dependsOn')) {
                for(d in plugin.dependsOn) {
                    xml.plugin(name:d.key, version:d.value)
                }
            }
        }
    }

    // Package plugin's zip distribution
    pluginZip = "${basedir}/griffon-${pluginName}-${plugin.version}.zip"
    ant.delete(file:pluginZip)

    ant.zip(destfile:pluginZip, filesonly:true) {
        fileset(dir:"${basedir}") {
            pluginIncludes.each {
                include(name:it)
            }
            pluginExcludes.each {
                exclude(name:it)
            }
        }

        zipfileset(dir: "${projectTargetDir}/docs", includes: "*.jar", prefix: "dist")
        zipfileset(dir: "${projectTargetDir}/docs", excludes: "*.jar", prefix: "docs")

        if (isAddonPlugin)  {
            zipfileset(dir:addonJarDir, includes: addonJarName,
                       fullpath: "lib/$addonJarName")
        }
    }

    ant.zip(destfile: pluginZip, filesonly: true, update: true) {
        fileset(dir:"${basedir}") {
            ['test/shared/**', 'test/resources/**'].each { f ->
                include(name: f)
            }
        }
        if(plugin.metaClass.hasProperty(plugin, 'pluginIncludes')) {
            plugin.pluginIncludes.each { f ->
                include(name: f)
            }
        }
    }

    event("PackagePluginEnd", [pluginName,plugin])
}
