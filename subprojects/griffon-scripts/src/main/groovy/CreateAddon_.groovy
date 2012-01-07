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

/**
 * Gant script that creates a new Griffon Addon inside of a Plugin Project
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 *
 */

import griffon.util.GriffonUtil

includeTargets << griffonScript('CreateIntegrationTest')

/**
 * Stuff this addon does:
 * * Creates a <name>GriffonAddon.groovy file form templates
 * * tweaks griffon-app/conf/BuildConfig.groovy to have griffon.jars.destDir set (don't change)
 * * tweaks griffon-app/conf/BuildConfig.groovy to have griffon.jars.jarName set (don't change)
 * * Adds copy libs events for the destDir
 */
target(createAddon: "Creates an Addon for a plugin") {
    if (metadataFile.exists()) {
        if (!isPluginProject) {
            event('StatusFinal', ['Cannot create an Addon in a non-plugin project.'])
            exit(1)
        } else {
            checkVersion()
            pluginName = isPluginProject.name - 'GriffonPlugin.groovy'
        }
    } else {
        includeTargets << griffonScript('_GriffonCreateProject')
        projectType = 'plugin'
        createPlugin()
    }

    argsMap.skipPackagePrompt = true
    createArtifact(
            name:   pluginName,
            suffix: 'GriffonAddon',
            type:   'GriffonAddon',
            path:   '.')

    /*
    addonName = "${GriffonUtil.getClassNameRepresentation(pluginName)}GriffonAddon"
    def pluginConfigFile = new File("${basedir}/griffon-app/conf/BuildConfig.groovy")
    if (!pluginConfigFile.exists()) {
        pluginConfigFile.text = "griffon {}\n"
    }
    ConfigSlurper slurper = new ConfigSlurper()
    def pluginConfig = slurper.parse(pluginConfigFile.toURL())

    if (!pluginConfig.griffon?.jars?.destDir) {
        pluginConfigFile << "\ngriffon.jars.destDir=\'target/addon\'\n"
    }
    if (!pluginConfig.griffon?.jars?.jarName) {
        pluginConfigFile << "\n//griffon.jars.jarName='${addonName}.jar'\n"
    }

    def eventsFile = new File("${basedir}/scripts/_Events.groovy")
    if (!eventsFile.exists()) {
        eventsFile.text = "\n"
    }

    def eventsText = eventsFile.text
    def classpathTempVar = generateTempVar(eventsText, "eventClosure")
    pluginName = GriffonUtil.getScriptName(pluginName)
    def pluginName2 = GriffonUtil.getPropertyNameForLowerCaseHyphenSeparatedName(griffonAppName)
    eventsFile << """
def $classpathTempVar = binding.variables.containsKey('eventSetClasspath') ? eventSetClasspath : {cl->}
eventSetClasspath = { cl ->
    $classpathTempVar(cl)
    if(compilingPlugin('$pluginName')) return
    griffonSettings.dependencyManager.flatDirResolver name: 'griffon-${pluginName}-plugin', dirs: "\${${pluginName2}PluginDir}/addon"
    griffonSettings.dependencyManager.addPluginDependency('$pluginName', [
        conf: 'compile',
        name: 'griffon-${pluginName}-addon',
        group: 'org.codehaus.griffon.plugins',
        version: ${pluginName2}PluginVersion
    ])
}
"""
    */
}
setDefaultTarget(createAddon)

def generateTempVar(String textToSearch, String prefix = "tmp", String suffix = "") {
    int i = 1;
    while (textToSearch =~ "\\W$prefix$i$suffix\\W") i++
    return "$prefix$i$suffix"
}
