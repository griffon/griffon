/*
 * Copyright 2004-2013 the original author or authors.
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
target(name: 'createAddon', description: "Creates an Addon for a plugin", prehook: null, posthook: null) {
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


    addonName = "${GriffonUtil.getClassNameRepresentation(pluginName)}GriffonAddon"

    def installFile = new File("${basedir}/scripts/_Install.groovy")
    //TODO we should slurp the config, tweak it in place, and re-write instead of append
    installFile << """
// Update the following configuration if your addon
// requires a different prefix or exposes nodes in
// a different way.
// Remember to apply the reverse changes in _Uninstall.groovy
//
// check to see if we already have a $addonName
// def configText = '''root.'$addonName'.addon=true'''
// if(!(builderConfigFile.text.contains(configText))) {
//     println 'Adding $addonName to Builder.groovy'
//     builderConfigFile.append(\"\"\"
// \$configText
// \"\"\")
// }"""

    def uninstallFile = new File("${basedir}/scripts/_Uninstall.groovy")
    //TODO we should slurp the config, tweak it in place, and re-write instead of append
    uninstallFile << """
// Update the following configuration if your addon
// requires a different prefix or exposes nodes in
// a different way.
// Remember to apply the reverse changes in _Install.groovy
//
// check to see if we already have a $addonName
// def configText = '''root.'$addonName'.addon=true'''
// if(builderConfigFile.text.contains(configText)) {
//     println 'Removing $addonName from Builder.groovy'
//     builderConfigFile.text -= configText
// }"""

}
setDefaultTarget(createAddon)
