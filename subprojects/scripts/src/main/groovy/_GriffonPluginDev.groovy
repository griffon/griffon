/*
 * Copyright 2004-2011 the original author or authors.
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

import griffon.util.PluginBuildSettings

import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.codehaus.griffon.plugins.PluginManagerHolder
import org.codehaus.griffon.resolve.IvyDependencyManager
import org.apache.ivy.core.report.ArtifactDownloadReport

/**
 * Gant script that deals with those tasks required for plugin developers
 * (as opposed to plugin users).
 *
 * @author Graeme Rocher (Grails 0.4)
 */

includeTargets << griffonScript("_GriffonPackage")
includeTargets << griffonScript("PackageAddon")
includeTargets << griffonScript("_GriffonDocs")

pluginIncludes = [
    "*GriffonPlugin.groovy",
    "plugin.xml",
    "dependencies.groovy",
    "griffon-app/conf/**",
    "lib/**",
    "scripts/**",
    "src/templates/**",
    "LICENSE*",
    "README*"
]

pluginExcludes = PluginBuildSettings.EXCLUDED_RESOURCES

target(pluginConfig:"setup the plugin config"){
    depends(checkVersion, createStructure, compile)

    def pluginFile
    new File("${basedir}").eachFile {
        if(it.name.endsWith("GriffonPlugin.groovy")) {
            pluginFile = it
        }
    }

    if(!pluginFile) ant.fail("Plugin file not found for plugin project")
    plugin = generatePluginXml(pluginFile)
    generateDependencyDescriptor()
}

target(pluginDocs: "Generates and packages plugin documentation") {
    depends(pluginConfig, packageAddon)

    pluginDocDir = "${projectTargetDir}/docs"
    ant.mkdir(dir: pluginDocDir)

    // copy 'raw' docs if they exists
    ant.copy(todir: pluginDocDir, failonerror: false) {
        fileset(dir: "${basedir}/src/docs/misc")
    }

    // package sources
    def srcMainDir = new File("${basedir}/src/main")
    def testSharedDir = new File("${basedir}/src/test")
    def testSharedDirPath = new File(griffonSettings.testClassesDir, 'shared')

    boolean hasSrcMain = srcMainDir.exists() ? ant.fileset(dir: srcMainDir, includes: '**/*.groovy, **/*.java').size() > 0 : false
    boolean hasTestShared = testSharedDir.exists() ? ant.fileset(dir: testSharedDir, includes: '**/*.groovy, **/*.java').size() > 0 : false
    List sources = []
    List excludedPaths = ['resources', 'i18n', 'conf']
    for(dir in new File("${basedir}/griffon-app").listFiles()) {
        if(!excludedPaths.contains(dir.name) && dir.isDirectory() &&
           ant.fileset(dir: dir, includes: '**/*.groovy, **/*.java').size() > 0) {
           sources << dir.absolutePath
        }
    }
    buildConfig.griffon?.plugin?.pack?.additional?.sources?.each { source ->
        File dir = new File("${basedir}/${source}")
        if(dir.isDirectory() && ant.fileset(dir: dir, excludes: '**/CVS/**, **/.svn/**').size() > 0) {
            sources << dir.absolutePath
        }
    }

    if(isAddonPlugin || hasSrcMain || hasTestShared || sources) {
        def jarFileName = "${projectTargetDir}/griffon-${pluginName}-${plugin.version}-sources.jar"

        ant.uptodate(property: 'pluginSourceJarUpToDate', targetfile: jarFileName) {
            sources.each { d ->
                srcfiles(dir: d, excludes: "**/CVS/**, **/.svn/**")
            }
            srcfiles(dir: basedir, includes: "*GriffonAddon.groovy")
            if(hasSrcMain) srcfiles(dir: srcMainDir, includes: "**/*")
            if(hasTestShared) srcfiles(dir: testSharedDir, includes: "**/*")
            srcfiles(dir: classesDirPath, includes: "**/*")
            if(hasTestShared) srcfiles(dir: testSharedDirPath, includes: "**/*")
        }
        boolean uptodate = ant.antProject.properties.pluginSourceJarUpToDate
        if(!uptodate) {
            ant.jar(destfile: jarFileName) {
                sources.each { d -> fileset(dir: d, excludes: '**/CVS/**, **/.svn/**') }
                fileset(dir: basedir, includes: '*GriffonAddon.groovy')
                if(hasSrcMain) fileset(dir: srcMainDir, includes: '**/*.groovy, **/*.java')
                if(hasTestShared) fileset(dir: testSharedDir, includes: '**/*.groovy, **/*.java')
            }
        }

        List groovydocSources = []
        sources.each { source ->
            File dir = new File(source)
            if(ant.fileset(dir: dir, includes: '**/*.groovy, **/*.java').size() > 0) {
                groovydocSources << dir
            }
        }

        if(!argsMap.nodoc && (hasSrcMain || hasTestShared || groovydocSources)) {
            def javadocDir = "${projectTargetDir}/docs/api"
            invokeGroovydoc(destdir: javadocDir,
                sourcepath: [srcMainDir, testSharedDir] + groovydocSources,
                windowtitle: "${pluginName} ${plugin.version}",
                doctitle: "${pluginName} ${plugin.version}")
            jarFileName = "${projectTargetDir}/griffon-${pluginName}-${plugin.version}-javadoc.jar"
            ant.jar(destfile: jarFileName) {
                fileset(dir: javadocDir)
            }
            ant.delete(dir: javadocDir, quiet: true)
        }
    }
}

private generateDependencyDescriptor() {
    ant.delete(dir:"$projectWorkDir/plugin-info", failonerror:false)
    if(griffonSettings.dependencyManager.hasApplicationDependencies()) {
        ant.mkdir(dir:"$projectWorkDir/plugin-info")
        ant.copy(file:"$basedir/griffon-app/conf/BuildConfig.groovy", tofile:"$projectWorkDir/plugin-info/dependencies.groovy", failonerror:false)
    }
}

// private def loadBasePlugin() {
//     PluginManagerHolder.pluginManager?.allPlugins?.find { it.basePlugin }
// }

target(packagePlugin:"Packages a Griffon plugin") {
    depends (pluginDocs)

    event("PackagePluginStart", [pluginName])
    
    // Package plugin's zip distribution
    pluginZip = "${basedir}/griffon-${pluginName}-${plugin.version}.zip"
    ant.delete(file:pluginZip)

    def testSharedDir = new File("${basedir}/src/test")
    def testSharedDirPath = new File(griffonSettings.testClassesDir, 'shared')
    def testResourcesDir = new File("${basedir}/test/resources")
    def testResourcesDirPath = griffonSettings.testResourcesDir

    boolean hasTestShared = testSharedDir.exists() ? ant.fileset(dir: testSharedDir, includes: '**/*.groovy, **/*.java').size() > 0 : false
    boolean hasTestResources = testResourcesDir.exists() ? ant.fileset(dir: testResourcesDir, excludes: '**/*.svn/**, **/CVS/**').size() > 0 : false

    if(hasTestShared || hasTestResources) {
        def jarFileName = "${projectTargetDir}/griffon-${pluginName}-${plugin.version}-test.jar"

        ant.uptodate(property: 'pluginTestJarUpToDate', targetfile: jarFileName) {
            if(hasTestShared) {
                srcfiles(dir: testSharedDir, includes: "**/*")
                srcfiles(dir: testSharedDirPath, includes: "**/*")
            }
            if(hasTestResources) {
                srcfiles(dir: testResourcesDir, includes: "**/*")
                srcfiles(dir: testResourcesDirPath, includes: "**/*")
            }
        }
        boolean uptodate = ant.antProject.properties.pluginTestJarUpToDate
        if(!uptodate) {
            ant.jar(destfile: jarFileName) {
                if(hasTestShared) fileset(dir: testSharedDirPath, includes: '**/*.class')
                if(hasTestResources) fileset(dir: testResourcesDir)
            }
        }
    }

    // def plugin = loadBasePlugin()
    // if(plugin?.pluginExcludes) {
    //     pluginExcludes.addAll(plugin?.pluginExcludes)
    // }

    // def includesList = pluginIncludes.join(",")
    // def excludesList = pluginExcludes.join(",")
    def libsDir = new File("${projectWorkDir}/tmp-libs")
    ant.delete(dir:libsDir, failonerror:false)
    def lowerVersion = GriffonPluginUtils.getLowerVersion(pluginGriffonVersion)

    boolean supportsAtLeastVersion
    try {
        supportsAtLeastVersion = GriffonPluginUtils.supportsAtLeastVersion(lowerVersion, "0.9")
    } catch (e) {
        println "Error: Plugin specified an invalid version range: ${pluginGriffonVersion}"
        exit 1 
    }
    if(!supportsAtLeastVersion) {
        IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
        def deps = dependencyManager.resolveExportedDependencies()
        if(dependencyManager.resolveErrors) {
            println "Error: There was an error resolving plugin JAR dependencies"
            exit 1
        }

        if(deps) {
            ant.mkdir(dir:"${libsDir}/lib")
            ant.copy(todir:"${libsDir}/lib") {
                for(ArtifactDownloadReport dep in deps) {
                    def file = dep.localFile
                    fileset(dir:file.parentFile, includes:file.name)
                }
            }
        }
    }

    def dependencyInfoDir = new File("$projectWorkDir/plugin-info")
    ant.zip(destfile:pluginZip, filesonly:true) {
        fileset(dir:"${basedir}") {
            pluginIncludes.each {
                include(name:it)
            }
            pluginExcludes.each {
                exclude(name:it)
            }
        }
        if(dependencyInfoDir.exists()) {
            fileset(dir:dependencyInfoDir)
        }
        if(libsDir.exists()) {
            fileset(dir:libsDir)
        }

        zipfileset(dir: "${projectTargetDir}", includes: "*.jar", prefix: "dist")
        zipfileset(dir: "${projectTargetDir}/docs", prefix: "docs")

        if (isAddonPlugin)  {
            new File(addonJarDir).eachFileMatch(~/.*\.jar/) { addonJar ->
                zipfileset(dir: addonJarDir, includes: addonJar.name,
                           fullpath: "addon/${addonJar.name}")
            }
        }
    }

    if(plugin.metaClass.hasProperty(plugin, 'pluginIncludes')) {
        def additionalIncludes = plugin.pluginIncludes
        if(additionalIncludes) {
            ant.zip(destfile: pluginZip, filesonly: true, update: true) {
                zipfileset(dir: basedir) {
                    additionalIncludes.each { f ->
                        include(name: f)
                    }
                }
            }
        }
    }

    event("PackagePluginEnd", [pluginName])
}
