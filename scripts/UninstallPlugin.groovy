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
 * Gant script that handles the installation of Griffon plugins
 *
 * @author Graeme Rocher
 * @author Peter Ledbrook
 * @author Danno Ferrin
 *
 */

defaultTarget("Uninstalls a plug-in for a given name") {
    uninstallPlugin()
}

includeTargets << griffonScript("Clean")

globalInstall = false

target(uninstallPlugin:"Uninstalls a plug-in for a given name") {
    depends(checkVersion, parseArguments, clean)

    if(argsMap['global']) {
        globalInstall = true
    }

    def pluginArgs = argsMap['params']
    if(pluginArgs) {

        def pluginName = pluginArgs[0]
        def pluginRelease = pluginArgs[1]

        String pluginKey = "plugins.$pluginName"
        metadata.remove(pluginKey)
        metadataFile.withOutputStream { out ->
            metadata.store out,'utf-8'
        }


        def pluginDir
        if(pluginName && pluginRelease) {
            pluginDir = new File("${pluginsDirPath}/$pluginName-$pluginRelease")
        }
        else {
            pluginDir = getPluginDirForName(pluginName)?.file
        }
        if(pluginDir?.exists()) {

            def uninstallScript = new File("${pluginDir}/scripts/_Uninstall.groovy")
            runPluginScript(uninstallScript, pluginDir.name, "uninstall script")

            Ant.delete(dir:pluginDir, failonerror:true)

        }
        else {
            event("StatusError", ["No plug-in [$pluginName${pluginRelease ? '-' + pluginRelease : ''}] installed, cannot uninstall"])
        }
    }
    else {
        event("StatusError", ["You need to specify the plug-in name and (optional) version, e.g. \"griffon uninstall-plugin feeds 1.0\""])
    }

}

