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

import groovy.xml.dom.DOMCategory

/**
 * Gant script that handles the installation of Griffon plugins
 *
 * @author Graeme Rocher
 * @author Sergey Nebolsin
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonClean")
includeTargets << griffonScript("_GriffonPackage")

ERROR_MESSAGE = """
You need to specify either the direct URL of the plugin or the name and version
of a distributed Griffon plugin found at ${pluginSVN}
For example:
'griffon install-plugin swingx-builder 0.1'
or
'griffon install-plugin ${pluginBinaryDistURL}/griffon-swingx-builder-0.1.zip"""

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
    }
    else {
        event("StatusError", ["You need to specify the plug-in name and (optional) version, e.g. \"griffon uninstall-plugin feeds 1.0\""])
    }

}

target(listPlugins: "Implementation target") {
    depends(parseArguments)

    if(argsMap.repository) {
       configureRepositoryForName(argsMap.repository)
       updatePluginsList()
       printRemotePluginList(argsMap.repository)
       printInstalledPlugins()
    }
    else if(argsMap.installed) {
      printInstalledPlugins()
    }
    else {
      updatePluginsList()
      printRemotePluginList("Griffon.codehaus.org")
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
-------------------------------------------------------------
'''

  def installedPlugins = []
  def pluginXmls = readAllPluginXmlMetadata()
  for (p in pluginXmls) {
    installedPlugins << formatPluginForPrint(p.@name.text(), p.@version.text(), p.title.text())
  }

  if (installedPlugins) {
    installedPlugins.sort()
    installedPlugins.each { println it }
  }
  else {
    println "You do not have any plugins installed."
  }
}

private printRemotePluginList(name) {
  println """
Plug-ins available in the $name repository are listed below:
-------------------------------------------------------------
"""
  def plugins = []
  use(DOMCategory) {
    pluginsList.'plugin'.each {plugin ->
      def version
      def pluginLine = plugin.'@name'
      def versionLine = "<no releases>"
      def title = "No description available"
      if (plugin.'@latest-release') {
        version = plugin.'@latest-release'
        versionLine = "<${version}>"
      }
      else if (plugin.'release'.size() > 0) {
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
  }
  else {
    println "No plugins found in repository: ${pluginSVN}"
  }
}

formatPluginForPrint = { pluginName, pluginVersion, pluginTitle ->
    "${pluginName.padRight(20, " ")}${pluginVersion.padRight(16, " ")} --  ${pluginTitle}"
}


def displayHeader = {
    println '''
--------------------------------------------------------------------------
Information about Griffon plugin
--------------------------------------------------------------------------\
'''
}

def displayPluginInfo = { pluginName, version ->
    use(DOMCategory) {
        def plugin = findPlugin(pluginName)
        if( plugin == null ) {
            event("StatusError", ["Plugin with name '${pluginName}' was not found in the configured repositories"])
            System.exit(1)
        } else {
            def line = "Name: ${pluginName}"
            def releaseVersion = null
            if( !version ) {
                releaseVersion = plugin.'@latest-release'
                def naturalVersion = releaseVersion
                if( ! releaseVersion ) {
                    plugin.'release'.each {
                        if( !releaseVersion || (!"${it.'@version'}".endsWith("SNAPSHOT") && "${it.'@version'}" > releaseVersion )) releaseVersion = "${it.'@version'}"
                    }
                    if( releaseVersion ) naturalVersion = "${releaseVersion} (?)"
                    else naturalVersion = '<no info available>'
                }
                line += "\t| Latest release: ${naturalVersion}"
            } else {
                releaseVersion = version
                line += "\t| Release: ${releaseVersion}"
            }
            println line
            println '--------------------------------------------------------------------------'
            if( releaseVersion ) {
                def release = plugin.'release'.find{ rel -> rel.'@version' == releaseVersion }
                if( release ) {
                    if( release.'title'.text() ) {
                        println "${release.'title'.text()}"
                    } else {
                        println "No info about this plugin available"
                    }
                    println '--------------------------------------------------------------------------'
                    if( release.'author'.text() ) {
                        println "Author: ${release.'author'.text()}"
                        println '--------------------------------------------------------------------------'
                    }
                    if( release.'authorEmail'.text() ) {
                        println "Author's e-mail: ${release.'authorEmail'.text()}"
                        println '--------------------------------------------------------------------------'
                    }
                    if( release.'documentation'.text() ) {
                        println "Find more info here: ${release.'documentation'.text()}"
                        println '--------------------------------------------------------------------------'
                    }
                    if( release.'description'.text() ) {
                        println "${release.'description'.text()}"
                        println '--------------------------------------------------------------------------'
                    }
                } else {
                    println "<release ${releaseVersion} not found for this plugin>"
                    println '--------------------------------------------------------------------------'
                }
           }

            def releases = ""
            plugin.'release'.findAll{ it.'@type' == 'svn'}.each {
                releases += " ${it.'@version'}"
            }
            def zipReleases = ""
            plugin.'release'.findAll{ it.'@type' == 'zip'}.each {
                zipReleases += " ${it.'@version'}"
            }
            if( releases ) {
                println "Available full releases: ${releases}"
            } else {
                println "Available full releases: <no full releases available for this plugin now>"
            }
            if( zipReleases ) {
                println "Available zip releases:  ${zipReleases}"
            }
        }
    }
}

def displayFullPluginInfo = { pluginName ->
    use(DOMCategory) {
        pluginsList.'plugin'.each { plugin ->
            def pluginLine = plugin.'@name'
            def version = "unknown"
            def title = "No description available"
            if( plugin.'@latest-release' ) {
                version = plugin.'@latest-release'
                def release = plugin.'release'.find{ rel -> rel.'@version' == plugin.'@latest-release' }
                if( release?.'title' ) {
                    title = release?.'title'.text()
                }
            }
            pluginLine += "${spacesFormatter[pluginLine.length()..-1]}<${version}>"
            pluginLine += "\t--\t${title}"
            plugins << pluginLine
        }
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

target(pluginInfo:"Implementation target") {
    depends(parseArguments)

    if( argsMap.params ) {
        depends(updatePluginsList)
        displayHeader()
        def pluginName = argsMap.params[0]
        def version = argsMap.params.size() > 1 ? argsMap.params[1] : null
        displayPluginInfo( pluginName, version )
        displayFooter()
    } else {
        event("StatusError", ["Usage: griffon plugin-info <plugin-name> [version]"])
    }
}
