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

import griffon.util.ConfigUtils
import griffon.util.Metadata
import groovy.io.FileType
import org.springframework.core.io.Resource

/**
 * @author Andres Almiray
 * @since 1.2.0
 */

target(name: 'listTemplates', description: 'Lists all artifacts templates available to the current project', prehook: null, posthook: null) {
    if (isPluginProject || isApplicationProject) {
        Map templates = [plugins: [:], archetypes: [:]]
        File applicationTemplatesDir = new File("${basedir}/src/templates/artifacts")
        if (applicationTemplatesDir.exists()) {
            templates.application = doListTemplates(applicationTemplatesDir)
        }

        for (Resource pluginDir : pluginSettings.frameworkPluginDirectories) {
            File templateDir = new File("${pluginDir.file.absolutePath}/src/templates/artifacts")
            if (templateDir.exists()) {
                templates.plugins[pluginDir.file.name] = doListTemplates(templateDir)
            }
        }

        for (Resource pluginDir : pluginSettings.projectPluginDirectories) {
            File templateDir = new File("${pluginDir.file.absolutePath}/src/templates/artifacts")
            if (templateDir.exists()) {
                templates.plugins[pluginDir.file.name] = doListTemplates(templateDir)
            }
        }

        if (isApplicationProject) {
            Map archetypeInfo = Metadata.current.getArchetype()
            File archetypeTemplatesDir = new File("${griffonWorkDir}/archetypes/${archetypeInfo.key}-${archetypeInfo.value}/templates/artifacts")
            if (archetypeTemplatesDir.exists()) {
                templates.archetypes[archetypeInfo.key] = doListTemplates(archetypeTemplatesDir)
            }
            templates.archetypes.'default' = resolveResources("classpath:archetypes/default/templates/artifacts/*").inject([:]) { Map map, Resource resource ->
                List fileTypes = map.get(ConfigUtils.stripFilenameExtension(resource.filename), [])
                fileTypes << ConfigUtils.getFilenameExtension(resource.filename)
                map
            }
        }

        println """Available Templates are listed below:
${'-' * 80}
${'Location'.padRight(25, ' ')}${'Name'.padRight(30, ' ')}FileType
${'-' * 80}"""
        if (templates.application) {
            printTemplateInfo('application', templates.application)
        } else {
            println "${'application'.padRight(25, ' ')}<none>"
        }
        println '-- Plugins '.padRight(80, '-')
        templates.plugins.sort().each { String plugin, Map templateInfo ->
            printTemplateInfo(plugin, templateInfo)
        }
        println '-- Archetypes '.padRight(80, '-')
        templates.archetypes.sort().each { String archetype, Map templateInfo ->
            printTemplateInfo(archetype, templateInfo)
        }

    } else {
        event 'StatusError', ['Artifact templates are not available for this type of project.']
        exit 1
    }
}

setDefaultTarget(listTemplates)

private Map doListTemplates(File directory) {
    Map templates = [:]
    directory.eachFile(FileType.FILES) { File file ->
        List fileTypes = templates.get(ConfigUtils.stripFilenameExtension(file.name), [])
        fileTypes << ConfigUtils.getFilenameExtension(file.name)
    }
    templates
}

private void printTemplateInfo(String location, Map templates) {
    int i = 0
    templates.sort().each { String name, List fileTypes ->
        println "${(i++ > 0? '' : location).padRight(25, ' ')}${name.padRight(30, ' ')}${fileTypes.sort().join(', ')}"
    }
}