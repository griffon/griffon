
/*
 * Copyright 2004-2005 the original author or authors.
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

import groovy.xml.MarkupBuilder
//import org.codehaus.griffon.compiler.support.GriffonResourceLoaderHolder
import org.codehaus.griffon.util.GriffonNameUtils

/**
 * Gant script that deals with those tasks required for plugin developers
 * (as opposed to plugin users).
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonPackage")
includeTargets << griffonScript("_GriffonDocs")

pluginIncludes = [
	metadataFile.name,
	"*GriffonPlugin.groovy",
    "plugin.xml",
	"griffon-app/**",
	"lib/**",
    "scripts/**",
//	"web-app/**",
	"src/**",
//	"docs/api/**",
//	"docs/gapi/**"
]

pluginExcludes = [
//	"web-app/WEB-INF/**",
//	"web-app/plugins/**",
//   "griffon-app/conf/spring/resources.groovy",
//	"griffon-app/conf/*DataSource.groovy",
    "griffon-app/conf/Application.groovy",
//    "griffon-app/conf/BootStrap.groovy",
//    "griffon-app/conf/BuildSettings.groovy",
    "griffon-app/conf/Builder.groovy",
    "griffon-app/conf/Config.groovy",
//    "griffon-app/conf/UrlMappings.groovy",
	"**/.svn/**",
	"test/**",
	"**/CVS/**"
]

target(packagePlugin:"Implementation target") {
    depends (checkVersion, /*packageApp,*/ docs) //FIXME package-plugin

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
        GriffonUtil.deepSanitize(t)
        t.printStackTrace(System.out)
        ant.fail("Cannot instantiate plugin file")
    }
    pluginName = GriffonNameUtils.getScriptName(GriffonNameUtils.getLogicalName(pluginClass, "GriffonPlugin"))
    event("PackagePluginStart", [pluginName,plugin])
    
    // Generate plugin.xml descriptor from info in *GriffonPlugin.groovy
    new File("${basedir}/plugin.xml").delete()
    def writer = new IndentPrinter( new PrintWriter( new FileWriter("${basedir}/plugin.xml")))
    def xml = new MarkupBuilder(writer)
    def props = ['author','authorEmail','title','description','documentation']
    //def resourceList = GriffonResourceLoaderHolder.resourceLoader.getResources()
    def jdk = plugin.properties['jdk'] ?: "1.4"
    xml.plugin(name:"${pluginName}",version:"${plugin.version}",jdk:"$jdk") {
              props.each {
            if( plugin.properties[it] ) "${it}"(plugin.properties[it])
        }
//        resources {
//            for(r in resourceList) {
//                 def matcher = r.URL.toString() =~ artefactPattern
//                 def name = matcher[0][1].replaceAll('/', /\./)
//                 resource(name)
//            }
//        }
        dependencies {
            if(plugin.metaClass.hasProperty(plugin,'dependsOn')) {
                for(d in plugin.dependsOn) {
                    plugin(name:d.key, version:d.value)
                }
            }
        }
    }

    // Package plugin's zip distribution
    pluginZip = "${basedir}/griffon-${pluginName}-${plugin.version}.zip"
    ant.delete(file:pluginZip)
    def includesList = pluginIncludes.join(",")
    def excludesList = pluginExcludes.join(",")
    ant.zip(basedir:"${basedir}", destfile:pluginZip, includes:includesList, excludes:excludesList, filesonly:true)
    event("PackagePluginEnd", [pluginName,plugin])
}
