/*
* Copyright 2012 the original author or authors.
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

import griffon.util.GriffonUtil
import org.springframework.core.io.Resource

import static org.apache.commons.lang.WordUtils.wrap

/**
 * Displays help description for Gant scripts
 *
 * @author Andres Almiray
 */

includeTargets << griffonScript('_GriffonResolveDependencies')

target(name: 'help', description: 'Displays this help or help about a command',
        prehook: null, posthook: null) {
    String command = argsMap.command ?: argsMap.params[0]
    if (command) {
        int maxwidth = 72i
        String prefix = '        '
        String mainDescription = getMainDescription(command)
        String detailedDescription = getDetailedDescription(command)

        if (mainDescription) {
            println ' '
            println mainDescription
            if (detailedDescription) {
                println 'DETAILS'
                detailedDescription.eachLine { line ->
                    println(prefix + wrap(line, maxwidth, '\n' + prefix, true))
                }
            }
        } else {
            println """
            |  There's no help available for command $command
            |""".stripMargin()
        }
    } else {
        List<File> scripts = pluginSettings.availableScripts.collect {it.file}

        println '''
        |  Usage (optionals marked with *):
        |     griffon [environment]* [target] [arguments]*
        |
        |  Examples:
        |     griffon dev run-app
        |     griffon create-app books
        |
        |  Available Targets (type griffon help 'target-name' for more info):
        |'''.stripMargin()

        scripts.unique {it.name}.sort {it.name}.each { File file ->
            println "  griffon ${GriffonUtil.getScriptName(file.name)}"
        }
    }
}

setDefaultTarget(help)

getMainDescription = { String command ->
    Resource[] commandInfo = resolveResources("classpath*:/org/codehaus/griffon/cli/shell/help/${command}.txt")
    if (commandInfo) {
        try {
            return commandInfo[0].getURL().text
        } catch (Exception e) {
            // can't retrieve information from classpath
        }
    }
    return null
}

getDetailedDescription = { String command ->
    Resource[] commandInfo = resolveResources("classpath*:/org/codehaus/griffon/cli/shell/command/${command}.txt")
    if (commandInfo) {
        try {
            return commandInfo[0].getURL().text
        } catch (Exception e) {
            // can't retrieve information from classpath
        }
    }
    return null
}
