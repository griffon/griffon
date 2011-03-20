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

import org.codehaus.griffon.plugins.GriffonPluginInfo
import org.codehaus.griffon.resolve.PluginResolveEngine
import griffon.util.BuildSettings
import griffon.util.Metadata

/**
 * Gant script that handles the installation of Griffon plugins
 *
 * @author Graeme Rocher (Grails 0.4)
 * @author Sergey Nebolsin (Grails 0.4)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_plugins_called")) return true
_griffon_plugins_called = true

includeTargets << griffonScript("_GriffonClean")
includeTargets << griffonScript("_GriffonPackage")
includeTargets << griffonScript("_PluginDependencies")
includeTargets << griffonScript("ReleasePlugin")

ERROR_MESSAGE = """
You need to specify either the direct URL of the plugin or the name and version
of a distributed Griffon plugin found at http://griffon.codehaus.org/plugins
For example:
'griffon install-plugin swingx-builder 0.1'
or
'griffon install-plugin http://svn.codehaus.org/griffon/plugins/griffon-swingx-builder/trunk/griffon-swingx-builder-0.1.zip"""

globalInstall = false

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

            ant.mkdir(dir: pluginsBase)

            def pluginFile = new File(pluginArgs[0])
            def urlPattern = ~"^[a-zA-Z][a-zA-Z0-9\\-\\.\\+]*://"
            if(pluginArgs[0] =~ urlPattern) {
                def url = new URL(pluginArgs[0])
                doInstallPluginFromURL(url)
            } else if( pluginFile.exists() && pluginFile.name.startsWith("griffon-") && pluginFile.name.endsWith(".zip" )) {
                doInstallPluginZip(pluginFile)
            } else {
                // The first argument is the plugin name, the second
                // (if provided) is the plugin version.
                doInstallPlugin(pluginArgs[0], pluginArgs[1])
            }
        } else {
            event("StatusError", [ERROR_MESSAGE])
        }
    }
    catch(Exception e) {
        logError("Error installing plugin: ${e.message}", e)
        exit(1)
    }
}

installPluginExternal = { Metadata md, pluginName, pluginVersion = null ->
    parseArguments()
    configureProxy()
    try {
        if(pluginName) {
            if(argsMap['global']) {
                globalInstall = true
            }

            ant.mkdir(dir: pluginsBase)

            def pluginFile = new File(pluginName)
            def urlPattern = ~"^[a-zA-Z][a-zA-Z0-9\\-\\.\\+]*://"
            if(pluginName =~ urlPattern) {
                def url = new URL(pluginName)
                doInstallPluginFromURL(url, md)
            } else if( pluginFile.exists() && pluginFile.name.startsWith("griffon-") && pluginFile.name.endsWith(".zip" )) {
                doInstallPluginZip(pluginFile, md)
            } else {
                // The first argument is the plugin name, the second
                // (if provided) is the plugin version.
                doInstallPlugin(pluginName, pluginVersion, md)
            }
        } else {
            event("StatusError", [ERROR_MESSAGE])
        }
    } catch(Exception e) {
        logError("Error installing plugin: ${e.message}", e)
        exit(1)
    }
}

target(uninstallPlugin:"Uninstalls a plug-in for a given name") {
    depends(checkVersion, parseArguments, clean)

    if(argsMap['global']) {
        globalInstall = true
    }

    def pluginArgs = argsMap['params']
    if(pluginArgs) {
        def pluginName = pluginArgs[0]
        def pluginRelease = pluginArgs[1]


        uninstallPluginForName(pluginName, pluginRelease)

        event("PluginUninstalled", ["The plugin ${pluginName}-${pluginRelease} has been uninstalled from the current application"])
    } else {
        event("StatusError", ["You need to specify the plug-in name and (optional) version, e.g. \"griffon uninstall-plugin feeds 1.0\""])
    }
}

target(listPlugins: "Implementation target") {
    depends(parseArguments, configureProxy)

    def repository = argsMap.repository
    if (repository) {
        eachRepository { name, url ->
            if(name == repository) {
                printRemotePluginList(repository)
                printInstalledPlugins()
            }
        }
    } else if (argsMap.installed) {
        printInstalledPlugins()
    } else {
        eachRepository { name, url ->
            printRemotePluginList(name)
            return true
        }
        printInstalledPlugins()
    }


    println '''
To find more info about plugin type 'griffon plugin-info [NAME]'

To install type 'griffon install-plugin [NAME] [VERSION]'

For further info visit http://griffon.codehaus.org/Plugins
'''
}

private printInstalledPlugins() {
  println '''
Plug-ins you currently have installed are listed below:
-----------------------------------------------------------------------
'''

  def installedPlugins = []
  def pluginInfos = pluginSettings.getPluginInfos()
  for (GriffonPluginInfo info in pluginInfos) {
    installedPlugins << formatPluginForPrint(info.name, info.version, info.title)
  }

  if (installedPlugins) {
    installedPlugins.sort()
    installedPlugins.each { println it }
  } else {
    println "You do not have any plugins installed."
  }
}

private printRemotePluginList(name) {
    println """
Plug-ins available in the $name repository are listed below:
-----------------------------------------------------------------------
"""
    def plugins = []
    use(DOMCategory) {
        pluginsList?.'plugin'.each {plugin ->
            def version
            def pluginLine = plugin.'@name'
            def versionLine = "<no releases>"
            def title = "No description available"
            if (plugin.'@latest-release') {
                version = plugin.'@latest-release'
                versionLine = "<${version}>"
            } else if (plugin.'release'.size() > 0) {
                // determine latest release by comparing version names in lexicografic order
                version = plugin.'release'[0].'@version'
                plugin.'release'.each {
                    if (!"${it.'@version'}".endsWith("SNAPSHOT") && "${it.'@version'}" > version) version = "${it.'@version'}"
                }
                versionLine = "<${version} (?)>\t"
            }
            def release = plugin.'release'.find {rel -> rel.'@version' == version}
            if (release?.'title') {
                title = release?.'title'.text()
            }
            plugins << formatPluginForPrint(pluginLine, versionLine, title)
        }
    }

    // Sort plugin descriptions
    if (plugins) {
        plugins.sort()
        plugins.each {println it}
        println ""
    } else {
        println "No plugins found in repository: ${name}. This may be because the repository is behind an HTTP proxy."
    }
}

formatPluginForPrint = { pluginName, pluginVersion, pluginTitle ->
    "${pluginName.toString().padRight(30, " ")}${pluginVersion.toString().padRight(16, " ")} --  ${pluginTitle}"
}

def displayHeader = {
    println '''
--------------------------------------------------------------------------
Information about Griffon plugin
--------------------------------------------------------------------------\
'''
}

def displayPluginInfo = { pluginName, version ->
    BuildSettings settings = griffonSettings
    def pluginResolveEngine = new PluginResolveEngine(settings.dependencyManager, settings)
    pluginXml = pluginResolveEngine.resolvePluginMetadata(pluginName, version)
    if(!pluginXml) {
        event("StatusError", ["Plugin with name '${pluginName}' was not found in the configured repositories"])
        exit 1
    }

    def plugin = pluginXml
    if(plugin == null) {
        event("StatusError", ["Plugin with name '${pluginName}' was not found in the configured repositories"])
        exit 1
    }

    def line = "Name: ${pluginName}"
    line += "\t| Latest release: ${plugin.@version}"
    println line
    println '--------------------------------------------------------------------------'
    def release = pluginXml
    if( release ) {
        if( release.'title'.text() ) {
            println "${release.'title'.text()}"
        } else {
            println "No info about this plugin available"
        }
        println '--------------------------------------------------------------------------'
        if( release.'author'.text() ) {
            println "Author: ${release.'author'.text()}"
            // println '--------------------------------------------------------------------------'
        }
        if( release.'authorEmail'.text() ) {
            println "Author's e-mail: ${release.'authorEmail'.text()}"
            // println '--------------------------------------------------------------------------'
        }
        if( release.'license'.text() ) {
            println "License: ${release.'license'.text()}"
            //println '--------------------------------------------------------------------------'
        } else {
            println "License: <UNKNOWN>"
            //println '--------------------------------------------------------------------------'
        }
        if( release.'documentation'.text() ) {
            println "Find more info here: ${release.'documentation'.text()}"
            // println '--------------------------------------------------------------------------'
        }
        if( release.'description'.text() ) {
            println "${release.'description'.text()}"
            println '--------------------------------------------------------------------------'
        }
        if( release.'toolkits'.text() ) {
            println "This plugin works with: ${release.'toolkits'.text()}"
            //println '--------------------------------------------------------------------------'
        } else {
            println "This plugin works with all toolkits."
            //println '--------------------------------------------------------------------------'
        }
        if( release.'platforms'.text() ) {
            println "This plugin works in: ${release.'platforms'.text()}"
            //println '--------------------------------------------------------------------------'
        } else {
            println "This plugin works in all platforms."
            //println '--------------------------------------------------------------------------'
        }
    } else {
        println "<release ${releaseVersion} not found for this plugin>"
        println '--------------------------------------------------------------------------'
    }
}

def displayFooter = {
    println '''
To get info about specific release of plugin 'griffon plugin-info [NAME] [VERSION]'

To get list of all plugins type 'griffon list-plugins'

To install latest version of plugin type 'griffon install-plugin [NAME]'

To install specific version of plugin type 'griffon install-plugin [NAME] [VERSION]'

For further info visit http://griffon.codehaus.org/Plugins
'''
}

target(pluginInfo:"Displays information on a Griffon plugin") {
    depends(parseArguments)

    if( argsMap.params ) {
        displayHeader()
        def pluginName = argsMap.params[0]
        def version = argsMap.params.size() > 1 ? argsMap.params[1] : null

        displayPluginInfo( pluginName, version )
        displayFooter()
    } else {
        event("StatusError", ["Usage: griffon plugin-info <plugin-name> [version]"])
    }
}
