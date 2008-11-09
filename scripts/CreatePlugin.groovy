import org.codehaus.griffon.commons.GriffonClassUtils as GCU

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

/**
 * Gant script that handles the creation of Griffon plugins
 * 
 * @author Graeme Rocher
 * @author Sergey Nebolsin
 *
 * @since 0.4
 */
appName = ""

defaultTarget("Creates a Griffon plug-in project, including the necessary directory structure and commons files") {
   createPlugin()
}

includeTargets << griffonScript("CreateApp" )

target( createPlugin: "The implementation target")  {
    depends( appName, createStructure, updateAppProperties, copyBasics, createIDESupportFiles )

//    Ant.copy(file:"${griffonHome}/src/griffon/griffon-app/conf/UrlMappings.groovy", todir:"${basedir}/griffon-app/conf")
//    Ant.copy(file:"${griffonHome}/src/griffon/griffon-app/conf/DataSource.groovy", todir:"${basedir}/griffon-app/conf")
    
    pluginName = GCU.getNameFromScript(griffonAppName)
     new File("${basedir}/${pluginName}GriffonPlugin.groovy") <<
"""\
class ${pluginName}GriffonPlugin {
    def version = 0.1
    def dependsOn = [:]

    // TODO Fill in these fields
    def author = "Your name"
    def authorEmail = ""
    def title = "Plugin summary/headline"
    def description = '''\\
Brief description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://griffon.codehaus.org/${pluginName}+Plugin"

    def onInitialize = { app ->
        // TODO Implement (optional)
    }

    def onStartup = { app ->
        // TODO Implement (optional)
    }

    def onReady = { app ->
        // TODO Implement (optional)
    }

    def onShutdown = { app ->
        // TODO Implement (optional)
    }
}
"""
    new File("${basedir}/scripts/_Install.groovy") <<
"""\
//
// This script is executed by Griffon after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
// Ant.mkdir(dir:"${basedir}/griffon-app/jobs")
//

Ant.property(environment:"env")
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"

"""
    new File("${basedir}/scripts/_Upgrade.groovy") <<
"""\
//
// This script is executed by Griffon during application upgrade ('griffon upgrade' command).
// This script is a Gant script so you can use all special variables
// provided by Gant (such as 'baseDir' which points on project base dir).
// You can use 'Ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
// Ant.mkdir(dir:"${basedir}/griffon-app/jobs")
//

Ant.property(environment:"env")
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"

"""
    event("StatusFinal", [ "Created plugin ${pluginName}"])
}
