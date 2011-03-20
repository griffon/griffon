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

import griffon.util.GriffonUtil
import griffon.util.Metadata

/**
 * Gant script that handles the creation of Griffon applications
 *
 * @author Graeme Rocher (Griffon 0.4)
 */

includeTargets << griffonScript("_GriffonPlugins")
includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("CreateMvc" )
includeTargets << griffonScript("Package")
includeTargets << griffonScript("IntegrateWith")

griffonAppName = ""
projectType = "app"

target(createApp: "Creates a Griffon application for the given name")  {
    depends(parseArguments, appName)

    loadArchetypeFor 'application'
    createApplicationProject()

    // Set the default version number for the application
    updateMetadata("app.version": griffonAppVersion ?: "0.1")
    def cfg = new File("${basedir}/griffon-app/conf/BuildConfig.groovy")
    cfg.append("""
app.archetype = '$archetype'
app.fileType = '$fileType'
""")

    event("StatusFinal", ["Created Griffon Application at $basedir"])
}

createProjectWithDefaults = {
    metadataFile = new File("${basedir}/application.properties")
    initProject()
    ant.replace(dir:"${basedir}/griffon-app/conf", includes:"**/*.*") {
        replacefilter(token: "@griffon.app.class.name@", value:appClassName )
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.project.key@", value: griffonAppName.replaceAll( /\s/, '.' ).toLowerCase())
    }

    ant.touch(file: metadataFile)

    // Create a message bundle to get the user started.
    ant.touch(file: "${basedir}/griffon-app/i18n/messages.properties")

    argsMap["params"][0] = griffonAppName
}

resetBaseDirectory = { String basedir ->
    // Update the build settings and reload the build configuration.
    griffonSettings.baseDir = new File(basedir)
    griffonSettings.loadConfig()

    // Reload the application metadata.
    metadataFile = new File("$basedir/${Metadata.FILE}")
    metadata = Metadata.getInstance(metadataFile)

    applicationConfig = new ConfigObject()
    applicationConfigFile = new File(basedir, 'griffon-app/conf/Application.groovy')
    if(applicationConfigFile.exists()) applicationConfig = configSlurper.parse(applicationConfigFile.text)
    builderConfig = new ConfigObject()
    builderConfigFile = new File(basedir, 'griffon-app/conf/Builder.groovy')
    if(builderConfigFile.exists()) builderConfig = configSlurper.parse(builderConfigFile.text)
    config = new ConfigObject()
    configFile = new File(basedir, 'griffon-app/conf/Config.groovy')
    if(configFile.exists()) config = configSlurper.parse(configFile.text)


    // Reset the plugin stuff.
    pluginSettings.clearCache()
    pluginsHome = griffonSettings.projectPluginsDir.path
}

target(createPlugin: "The implementation target")  {
    depends(parseArguments, appName)
    metadataFile = new File("${basedir}/application.properties")
    projectType = "plugin"
    initProject()

    // Rename the plugin descriptor.
    pluginName = GriffonUtil.getNameFromScript(griffonAppName)
    if(!(pluginName ==~ /[a-zA-Z][a-zA-Z0-9-]*/)) {
        println "Error: Specified plugin name [$griffonAppName] is invalid. Plugin names can only contain word characters separated by hyphens."
        exit 1
    }
    ant.move(
            file: "${basedir}/GriffonPlugin.groovy",
            tofile: "${basedir}/${pluginName}GriffonPlugin.groovy",
            overwrite: true)

    // Insert the name of the plugin into whatever files need it.
    ant.replace(dir:"${basedir}") {
        include(name: "*GriffonPlugin.groovy")
        include(name: "scripts/*")
        replacefilter(token: "@plugin.name@", value: pluginName)
        replacefilter(token: "@plugin.short.name@", value: GriffonUtil.getScriptName(pluginName))
        replacefilter(token: "@plugin.version@", value: griffonAppVersion ?: "0.1")
        replacefilter(token: "@griffon.version@", value: griffonVersion)
    }

    event("StatusFinal", [ "Created plugin ${pluginName}" ])
}

target(initProject: "Initialise an application or plugin project") {
    depends(createStructure, updateAppProperties)

    griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
    griffonUnpack(dest: basedir, src: "griffon-$projectType-files.jar")
    // integrateEclipse()
    // integrateAnt()
    // integrateTextmate()
    // integrateIntellij()

    // make sure Griffon central repo is prepped for default plugin set installation
    griffonSettings.dependencyManager.parseDependencies {
        repositories {
            griffonCentral()
        }
    }
}

target(appName : "Evaluates the application name") {
    if(argsMap["params"]) {
        griffonAppName = argsMap["params"].join(" ")
    } else {
        String type = scriptName.toLowerCase().indexOf('plugin') > -1 ? 'Plugin' : 'Application'
        ant.input(message:"$type name not specified. Please enter:",
                  addProperty:"griffon.app.name")
        griffonAppName = ant.antProject.properties."griffon.app.name"
    }

    if (!argsMap["inplace"]) {
        basedir = "${basedir}/${griffonAppName}"
        resetBaseDirectory(basedir)
    }

    if (argsMap["appVersion"]) {
        griffonAppVersion = argsMap["appVersion"]
    }

    appClassName = GriffonUtil.getClassNameRepresentation(griffonAppName)
}
