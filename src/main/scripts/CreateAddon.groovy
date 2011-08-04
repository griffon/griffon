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

includeTargets << griffonScript("Init")
includeTargets << griffonScript("CreateIntegrationTest")

/**
 * Stuff this addon does:
 * * Creates a <name>GriffonAddon.groovy file form templates
 * * tweaks griffon-app/conf/BuildConfig.groovy to have griffon.jars.destDir set (dont' change)
 * * tweaks griffon-app/conf/BuildConfig.groovy to have griffon.jars.jarName set (dont' change)
 * * Adds copy libs events for the destDir
 * * Adds install hooks to wire in addon to griffon-app/conf/Builder.groovy
 * * Adds uninstall hooks to remove the addon from griffon-app/conf/Builder.groovy
 */
target ('default' : "Creates a new Addon for a plugin") {
    depends(checkVersion, parseArguments)
    promptForName(type: "Addon")
    argsMap.skipPackagePrompt = true
    def (pkg, name) = extractArtifactName(argsMap["params"][0])
//    if (pkg) logErrorAndExit("Addons cannot have package names currently", new RuntimeException())
    def fqn = "${pkg?pkg:''}${pkg?'.':''}${GriffonUtil.getClassNameRepresentation(name)}"

    createArtifact(
        name: fqn,
        suffix: "GriffonAddon",
        type: "GriffonAddon",
        path: ".")
    fqn += 'GriffonAddon'
    name = "${GriffonUtil.getClassNameRepresentation(name)}GriffonAddon"

    def pluginConfigFile = new File('griffon-app/conf/BuildConfig.groovy')
    if (!pluginConfigFile.exists()) {
        pluginConfigFile.text = "griffon {}\n"
    }
    ConfigSlurper slurper = new ConfigSlurper()
    def pluginConfig = slurper.parse(pluginConfigFile.toURL())

    if (!pluginConfig.griffon?.jars?.destDir) {
        pluginConfigFile << "\ngriffon.jars.destDir=\'target/addon\'\n"
    }
    if (!pluginConfig.griffon?.jars?.jarName) {
        pluginConfigFile << "\n//griffon.jars.jarName='${name}.jar'\n"
    }
    
    def eventsFile = new File("scripts/_Events.groovy")
    if (!eventsFile.exists()) {
        eventsFile.text = "\n"
    }

    def eventsText = eventsFile.text
    def classpathTempVar = generateTempVar(eventsText, "eventClosure")
    def pluginName = GriffonUtil.getScriptName(griffonAppName)
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

    def installFile = new File("scripts/_Install.groovy")
    // all plugins should have an install, no need to insure
    String installText = installFile.text
    def flagVar = generateTempVar(installText, 'addonIsSet')

    //TODO we should slurp the config, tweak it in place, and re-write instead of append
    installFile << """
// check to see if we already have a $name
boolean $flagVar
builderConfig = configSlurper.parse(builderConfigFile.text)
builderConfig.each() { prefix, v ->
    v.each { builder, views ->
        $flagVar = $flagVar || '$fqn' == builder
    }
}

if (!$flagVar) {
    println 'Adding $name to Builder.groovy'
    builderConfigFile.append('''
root.'$fqn'.addon=true
''')
}"""

    def uninstallFile = new File("scripts/_Uninstall.groovy")
    // all plugins should have an install, no need to insure
    String uninstallText = uninstallFile.text
    flagVar = generateTempVar(uninstallText, 'addonIsSet')

    //TODO we should slurp the config, tweak it in place, and re-write instead of append
    uninstallFile << """
// check to see if we already have a $name
boolean $flagVar
builderConfig = configSlurper.parse(builderConfigFile.text)
builderConfig.each() { prefix, v ->
    v.each { builder, views ->
        $flagVar = $flagVar || '$fqn' == builder
    }
}

if ($flagVar) {
    println 'Removing $name from Builder.groovy'
    builderConfigFile.text = builderConfigFile.text - "root.'$fqn'.addon=true\\n"
}
"""
}

def generateTempVar(String textToSearch, String prefix = "tmp", String suffix = "") {
    int i = 1;
    while (textToSearch =~ "\\W$prefix$i$suffix\\W") i++
    return "$prefix$i$suffix"
}
