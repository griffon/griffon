/*
* Copyright 2009 the original author or authors.
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
 * Gant script that packages a Griffon addon inside of a plugin
 *
 * @author Danno Ferrin
 *
 */
includeTargets << griffonScript("_GriffonPackage")

target ('default': "Packages a Griffon addon. Note: to pasckakage a plugin use 'griffon package-plugin'") {
    depends(checkVersion, createStructure, packagePlugins, pluginConfig)

    if (!isAddonPlugin) return;

    try {
        profile("compile") {
            compile()
        }
    }
    catch(Exception e) {
        logError("Compilation error",e)
        exit(1)
    }
    profile("creating config") {
        createConfig()
    }


    addonJarDir = ant.antProject.replaceProperties(config.griffon.jars.destDir)
    ant.mkdir(dir:addonJarDir)

    addonJarName = ( config.griffon?.jars?.jarName
        ? "${config.griffon.jars.jarName}"
        : "griffon-${pluginName}-addon-${plugin.version}.jar" )
    ant.jar(destfile:"$addonJarDir/$addonJarName") {
        fileset(dir:classesDirPath) {
            exclude(name:'Config*.class')
            exclude(name:'*GriffonPlugin*.class')
        }
    }


    //TODO copy it to lib with version #
}
