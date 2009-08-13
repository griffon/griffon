/*
 * Copyright 2004-2008 the original author or authors.
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
 *
 */

import org.codehaus.griffon.commons.GriffonClassUtils as GCU

includeTargets << griffonScript("Init")
includeTargets << griffonScript("CreateIntegrationTest")

/**
 * Stuff this addon does:
 * * Creates a <name>GriffonAddon.groovy file form templates
 * * tweaks griffon-app/conf/Config.groovy to have griffon.jars.destDir set (dont' cahnge)
 * * tweaks griffon-app/conf/Config.groovy to have griffon.jars.jarName set (dont' cahnge)
 * * Adds copy libs events for the destDir
 * * Adds install hooks to wire in addon to griffon-app/conf/Builder.groovy
 */
target (createMVC : "Creates a new Addon for a plugin") {
    depends(checkVersion, parseArguments)
    promptForName(type: "Addon")
    def (pkg, name) = extractArtifactName(argsMap["params"][0])
//    if (pkg) logErrorAndExit("Addons cannot have package names currently", new RuntimeException())
    def fqn = "${pkg?pkg:''}${pkg?'.':''}${GCU.getClassNameRepresentation(name)}"

    createArtifact(
        name: fqn,
        suffix: "GriffonAddon",
        type: "GriffonAddon",
        path: ".")
    fqn += 'GriffonAddon'
    name = "${GCU.getClassNameRepresentation(name)}GriffonAddon"

    def pluginConfigFile = new File('griffon-app/conf/Config.groovy')
    if (!pluginConfigFile.exists()) {
        pluginConfigFile.text = "griffon {}\n"
    }
    ConfigSlurper slurper = new ConfigSlurper()
    def pluginConfig = slurper.parse(pluginConfigFile.toURL())

    if (!pluginConfig.griffon?.jars?.destDir) {
        pluginConfigFile << "\ngriffon.jars.destDir='lib'\n"
    }
    if (!pluginConfig.griffon?.jars?.jarName) {
        pluginConfigFile << "\ngriffon.jars.jarName='${name}.jar'\n"
    }
    
    def eventsFile = new File("scripts/_Events.groovy")
    if (!eventsFile.exists()) {
        eventsFile.text = "\n"
    }

    def eventsText = eventsFile.text
    def libTempVar = generateTempVar(eventsText, "eventClosure")
    eventsFile << """
def $libTempVar = binding.variables.containsKey('eventCopyLibsEnd') ? eventCopyLibsEnd : {jardir->}
eventCopyLibsEnd = { jardir ->
    $libTempVar(jardir)
    if (!isPluginProject) {
        ant.fileset(dir:"\${getPluginDirForName('$griffonAppName').file}/lib/", includes:"*.jar").each {
            griffonCopyDist(it.toString(), jardir)
        }
    }
}

"""

    def installFile = new File("scripts/_Install.groovy")
    // all plugins shoudl have an install, no need to insure
    String installText = installFile.text
    def slurperVar = generateTempVar(installText, 'configSlurper')
    def flagVar = generateTempVar(installText, 'addonIsSet')
    def configVar = generateTempVar(installText, 'slurpedBuilder')

    installFile << """
// check to see if we already have a $name
ConfigSlurper $slurperVar = new ConfigSlurper()
def $configVar = ${slurperVar}.parse(new File("\$basedir/griffon-app/conf/Builder.groovy").toURL())
boolean $flagVar
${configVar}.each() { prefix, v ->
    v.each { builder, views ->
        $flagVar = $flagVar || '$fqn' == builder
    }
}

if (!$flagVar) {
    println 'Adding $name to Builders.groovy'
    new File("\$basedir/griffon-app/conf/Builder.groovy").append('''
root.'$fqn' { }
''')
}"""


}

def generateTempVar(String textToSearch, String prefix = "tmp", String suffix = "") {
    int i = 1;
    while (textToSearch =~ "\\W$prefix$i$suffix\\W") i++
    return "$prefix$i$suffix"

}

setDefaultTarget(createMVC)

