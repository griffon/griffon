import groovy.xml.dom.DOMCategory

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
 * Gant script that handles the listing of Griffon plugins
 *
 * @author Sergey Nebolsin
 *
 * @since 0.5.5
 */

defaultTarget("Lists plug-ins that are hosted by the Griffon server") {
    listPlugins()
}

includeTargets << griffonScript("_PluginDependencies" )


Ant.mkdir(dir: "${pluginsHome}")



target(listPlugins: "Implementation target") {
    depends(updatePluginsList)


    println '''
Plug-ins available in the Griffon repository are listed below:
-------------------------------------------------------------
'''
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
    plugins.sort()
    plugins.each {println it}

    println '''
Plug-ins you currently have installed are listed below:
-------------------------------------------------------------
'''

    def installedPlugins = []
    def pluginXmls = readAllPluginXmlMetadata()
    for(p in pluginXmls) {
        installedPlugins << formatPluginForPrint(p.@name.text(), p.@version.text(), p.title.text() )
    }

    installedPlugins.sort()
    installedPlugins.each { println it }

    println '''
To find more info about plugin type 'griffon plugin-info [NAME]'

To install type 'griffon install-plugin [NAME] [VERSION]'

For further info visit http://griffon.codehaus.org/Plugins
'''
}

formatPluginForPrint = { pluginName, pluginVersion, pluginTitle ->
    "${pluginName.padRight(20, " ")}${pluginVersion.padRight(16, " ")} --  ${pluginTitle}"
}
