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

/**
 * Gant script that creates a new Griffon Model-View-Controller triads
 *
 * @author Danno Ferrin
 *
 */

import org.codehaus.griffon.commons.GriffonClassUtils as GCU

includeTargets << griffonScript("Init")
includeTargets << griffonScript("CreateIntegrationTest")

target('default': "Creates a new MVC Group") {
    createMVC()
}

target(createMVC: "Creates a new MVC Group") {
    depends(checkVersion, parseArguments)

    if (isPluginProject && !isAddonPlugin) {
        println """You must create an Addon descriptor first.
Type in griffon create-addon then execute this command again."""
        System.exit(1)
    }

    promptForName(type: "MVC Group")
    def (pkg, name) = extractArtifactName(argsMap['params'][0])

    mvcPackageName = pkg ? pkg : ''
    mvcClassName = GCU.getClassNameRepresentation(name)
    mvcFullQualifiedClassName = "${pkg ? pkg : ''}${pkg ? '.' : ''}$mvcClassName"

    String modelTemplate      = 'Model'
    String viewTemplate       = 'View'
    String controllerTemplate = 'Controller'
    if (argsMap.group) {
        modelTemplate      = argsMap.group + modelTemplate
        viewTemplate       = argsMap.group + viewTemplate
        controllerTemplate = argsMap.group + controllerTemplate
    }

    if (!argsMap.skipModel && !argsMap.withModel) {
        createArtifact(
                name: mvcFullQualifiedClassName,
                suffix: 'Model',
                type: 'Model',
                template: modelTemplate,
                path: 'griffon-app/models')
    }

    if (!argsMap.skipView && !argsMap.withView) {
        createArtifact(
                name: mvcFullQualifiedClassName,
                suffix: 'View',
                type: 'View',
                template: viewTemplate,
                path: 'griffon-app/views')
    }

    if (!argsMap.skipController && !argsMap.withController) {
        createArtifact(
                name: mvcFullQualifiedClassName,
                suffix: 'Controller',
                type: 'Controller',
                template: controllerTemplate,
                path: 'griffon-app/controllers')

        createIntegrationTest(
                name: mvcFullQualifiedClassName,
                suffix: '')
    }

    if (isAddonPlugin) {
        // create mvcGroup in a plugin
        def isJava = isAddonPlugin.absolutePath.endsWith('.java')
        def addonFile = isAddonPlugin
        def addonText = addonFile.text

        if(isJava) {
            if (!(addonText =~ /\s*public Map<String, Map<String, String>>\s*getMvcGroups\(\)\s*\{/)) {
                            addonText = addonText.replaceAll(/\}\s*\z/, """
                public Map<String, Map<String, String>> getMvcGroups() {
                    Map<String, Map<String, String>> groups = new LinkedHashMap<String, Map<String, String>>();
                    return groups;
                }
            }
            """)
                        }

                        List parts = []
                        if (!argsMap.skipModel)      parts << """            {"model",      "${(argsMap.withModel ?: mvcFullQualifiedClassName + 'Model')}"}"""
                        if (!argsMap.skipView)       parts << """            {"view",       "${(argsMap.withView ?: mvcFullQualifiedClassName + 'View')}"}"""
                        if (!argsMap.skipController) parts << """            {"controller", "${(argsMap.withController ?: mvcFullQualifiedClassName + 'Controller')}"}"""

                        addonFile.withWriter {
                            it.write addonText.replaceAll(/\s*Map<String, Map<String, String>> groups = new LinkedHashMap<String, Map<String, String>>\(\);/, """
                    Map<String, Map<String, String>> groups = new LinkedHashMap<String, Map<String, String>>();
                    // MVC Group for "$name"
                    groups.put("$name", groupDef(new String[][]{
            ${parts.join(',\n')}
                    }));""")
                        }

        } else {

            if (!(addonText =~ /\s*def\s*mvcGroups\s*=\s*\[/)) {
                addonText = addonText.replaceAll(/\}\s*\z/, """
    def mvcGroups = [
    ]
}
""")
            }
            List parts = []
            if (!argsMap.skipModel)      parts << "            model     : '${(argsMap.withModel ?: mvcFullQualifiedClassName + 'Model')}'"
            if (!argsMap.skipView)       parts << "            view      : '${(argsMap.withView ?: mvcFullQualifiedClassName + 'View')}'"
            if (!argsMap.skipController) parts << "            controller: '${(argsMap.withController ?: mvcFullQualifiedClassName + 'Controller')}'"

            addonFile.withWriter {
                it.write addonText.replaceAll(/\s*def\s*mvcGroups\s*=\s*\[/, """
    def mvcGroups = [
        // MVC Group for "$name"
        '$name': [
${parts.join(',\n')}
        ],
    """)
            }
        }
    } else {
        // create mvcGroup in an application
        def applicationConfigFile = new File("${basedir}/griffon-app/conf/Application.groovy")
        def configText = applicationConfigFile.text
        if (!(configText =~ /\s*mvcGroups\s*\{/)) {
            configText += """
mvcGroups {
}
"""
        }

        List parts = []
        if (!argsMap.skipModel)      parts << "        model      = '${(argsMap.withModel ?: mvcFullQualifiedClassName + 'Model')}'"
        if (!argsMap.skipView)       parts << "        view       = '${(argsMap.withView ?: mvcFullQualifiedClassName + 'View')}'"
        if (!argsMap.skipController) parts << "        controller = '${(argsMap.withController ?: mvcFullQualifiedClassName + 'Controller')}'"

        applicationConfigFile.withWriter {
            it.write configText.replaceAll(/\s*mvcGroups\s*\{/, """
mvcGroups {
    // MVC Group for "$name"
    '$name' {
${parts.join('\n')}
    }
""")
        }
    }
}
