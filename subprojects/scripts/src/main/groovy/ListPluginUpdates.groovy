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
import groovy.xml.dom.DOMCategory
import griffon.util.Metadata

includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("_GriffonPlugins")
includeTargets << griffonScript("_PluginDependencies")

def getAvailablePluginVersions = {
    def plugins = [:]
    eachRepository {repo, url ->
        use(DOMCategory) {
            pluginsList.'plugin'.each {plugin ->
                def name = plugin.'@name'
                def version
                if (plugin.'@latest-release') {
                    version = plugin.'@latest-release'
                }
                else if (plugin.'release'.size() > 0) {
                    // determine latest release by comparing version names in lexicografic order
                    version = plugin.'release'[0].'@version'
                    plugin.'release'.each {
                        if (!"${it.'@version'}".endsWith("SNAPSHOT") && "${it.'@version'}" > version) version = "${it.'@version'}"
                    }
                }
                plugins."$name" = version
            }
        }
    }
    return plugins
}

def getInstalledPluginVersions = {
    def plugins = [:]
    def pluginXmls = readAllPluginXmlMetadata()
    for (p in pluginXmls) {
        def name = p.@name.text()
        def version = p.@version.text()
        plugins."$name" = version
    }
    return plugins
}

target('default': "Checks installed plugin versions against latest releases on repositories") {
    depends(parseArguments, resolveDependencies)

    def availablePluginVersions = getAvailablePluginVersions()
    def installedPluginVersions = getInstalledPluginVersions()
    def outdatedPlugins = [:]

    boolean headerDisplayed = false
    if (installedPluginVersions) {
        installedPluginVersions.each {name, version ->
            def availableVersion = availablePluginVersions."$name"
            if (availableVersion != version && availableVersion != null) {
                if (!headerDisplayed) {
                    println """
Plugins with available updates are listed below:
-----------------------------------------------------------------------
<Plugin>                      <Current>         <Available>"""
                    headerDisplayed = true
                }
                println "${name.padRight(30, " ")}${version.padRight(16, " ")}  ${availableVersion}"
                outdatedPlugins[name.toString()] = availableVersion.toString()
            }
        }
        if (!headerDisplayed) {
            println "\nAll plugins are up to date."
        }
        if(argsMap.install && outdatedPlugins) {
            println ''
            if(confirmInput("Proceed with plugin upgrades?", "plugin.upgrade")) {
                wasInteractive = isInteractive
                isInteractive = false
                try {
                    System.setProperty('griffon.plugin.force.updates', 'true')
                    outdatedPlugins.each { pluginName, pluginVersion ->
                        // skip if pluginName-pluginVersion has been installed already because
                        // it is a dependency of another plugin that was upgraded in  a previous
                        // iteration
                        if(Metadata.current['plugins.'+pluginName] == pluginVersion) return
                        installPluginForName(pluginName)
                    }
                } finally {
                    isInteractive = wasInteractive
                    System.setProperty('griffon.plugin.force.updates', 'false')
                }
            }
        }
    } else {
        println "\nYou do not have any plugins installed."
    }
}
