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
 * Gant script that handles the installation of Griffon plugins
 *
 * @author Graeme Rocher
 * @author Sergey Nebolsin
 *
 * @since 0.4
 */
appName = ""

defaultTarget("Installs a plug-in for the given URL or name and version") {
    installPlugin()
}

includeTargets << griffonScript("ListPlugins")
includeTargets << griffonScript("Clean")
includeTargets << griffonScript("Package")


ERROR_MESSAGE = """
You need to specify either the direct URL of the plugin or the name and version
of a distributed Griffon plugin found at ${DEFAULT_PLUGIN_DIST}
For example:
'griffon install-plugin swingx-builder 0.1'
or
'griffon install-plugin ${BINARY_PLUGIN_DIST}/griffon-swingx-builder-0.1.zip"""

globalInstall = false




target(resolveDependencies:"Resolves Griffon' plug-in dependencies") {
    profile( "Resolving plug-in dependencies" ) {
        def plugins = metadata.findAll { k,v-> k.startsWith("plugins.") }
        for(p in plugins) {
            def name = p.key[8..-1]
            def version = p.value

            println "Resolving dep $name"
        }
    }
}


target(cachePlugin:"Implementation target") {
    depends(configureProxy)
    fullPluginName = cacheKnownPlugin(pluginName, pluginRelease)
}

target(installPlugin:"Installs a plug-in for the given URL or name and version") {
    depends(checkVersion, parseArguments, configureProxy)
    try {
        def pluginArgs = argsMap['params']

        // fix for Windows-style path with backslashes

        if(pluginArgs) {
            if(argsMap['global']) {
                globalInstall = true
            }

            ant.mkdir(dir:pluginsBase)

            def pluginFile = new File(pluginArgs[0])

            if(pluginArgs[0].startsWith("http://")) {
                def url = new URL(pluginArgs[0])
                fullPluginName = downloadRemotePlugin(url, pluginsBase)
            }
            else if( pluginFile.exists() && pluginFile.name.startsWith("griffon-") && pluginFile.name.endsWith(".zip" )) {
                cacheLocalPlugin(pluginFile)
            }
            else {
                // The first argument is the plugin name, the second
                // (if provided) is the plugin version.
                fullPluginName = cacheKnownPlugin(pluginArgs[0], pluginArgs[1])
            }

            classpath()
            println "Installing plug-in $fullPluginName"

            installPluginForName(fullPluginName)
        }
        else {
            event("StatusError", [ ERROR_MESSAGE])
        }

    }
    catch(Exception e) {
        logError("Error installing plugin: ${e.message}", e)
        exit(1)
    }
}
