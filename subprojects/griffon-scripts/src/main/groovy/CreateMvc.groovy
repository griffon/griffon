/*
 * Copyright 2004-2012 the original author or authors.
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

import griffon.util.GriffonNameUtils
import griffon.util.GriffonUtil

includeTargets << griffonScript('CreateIntegrationTest')


target(createMVC: "Creates a new MVC Group") {
    if (isPluginProject && !isAddonPlugin) {
        println """You must create an Addon descriptor first.
Type in griffon create-addon then execute this command again."""
        System.exit(1)
    }

    promptForName(type: "MVC Group")
    def (pkg, name) = extractArtifactName(argsMap['params'][0])

    mvcPackageName = pkg ? pkg : ''
    mvcClassName = GriffonUtil.getClassNameRepresentation(name)
    mvcFullQualifiedClassName = "${pkg ? pkg : ''}${pkg ? '.' : ''}$mvcClassName"

    // -- compatibility
    argsMap['with-model']      = argsMap['with-model']      ?: argsMap.withModel
    argsMap['with-view']       = argsMap['with-view']       ?: argsMap.withView
    argsMap['with-controller'] = argsMap['with-controller'] ?: argsMap.withController
    argsMap['skip-model']      = argsMap['skip-model']      ?: argsMap.skipModel
    argsMap['skip-view']       = argsMap['skip-view']       ?: argsMap.skipView
    argsMap['skip-controller'] = argsMap['skip-controller'] ?: argsMap.skipController
    // -- compatibility

    String modelTemplate      = 'Model'
    String viewTemplate       = 'View'
    String controllerTemplate = 'Controller'
    if (argsMap.group) {
        modelTemplate      = argsMap.group + modelTemplate
        viewTemplate       = argsMap.group + viewTemplate
        controllerTemplate = argsMap.group + controllerTemplate
    }

    String modelClassName = ''
    if (!argsMap['skip-model'] && !argsMap['with-model']) {
        createArtifact(
                name:     mvcFullQualifiedClassName,
                suffix:   'Model',
                type:     'Model',
                template: modelTemplate,
                path:     'griffon-app/models')
        modelClassName = fullyQualifiedClassName
    }

    String viewClassName = ''
    if (!argsMap['skip-view'] && !argsMap['with-view']) {
        createArtifact(
                name:     mvcFullQualifiedClassName,
                suffix:   'View',
                type:     'View',
                template: viewTemplate,
                path:     'griffon-app/views')
        viewClassName = fullyQualifiedClassName
    }

    String controllerClassName = ''
    if (!argsMap['skip-controller'] && !argsMap['with-controller']) {
        createArtifact(
                name:     mvcFullQualifiedClassName,
                suffix:   'Controller',
                type:     'Controller',
                template: controllerTemplate,
                path:     'griffon-app/controllers')
        controllerClassName = fullyQualifiedClassName

        doCreateIntegrationTest(
                name:   mvcFullQualifiedClassName,
                suffix: '')
    }

    name = GriffonNameUtils.getPropertyName(name)

    if (isAddonPlugin) {
        // create mvcGroup in a plugin
        def isJava = isAddonPlugin.absolutePath.endsWith('.java')
        def addonFile = isAddonPlugin
        def addonText = addonFile.text

        if (isJava) {
            if (!(addonText =~ /\s*public Map<String, Map<String, String>>\s*getMvcGroups\(\)\s*\{/)) {
                addonText = addonText.replaceAll(/\}\s*\z/, """
                public Map<String, Map<String, Object>> getMvcGroups() {
                    Map<String, Map<String, Object>> groups = new LinkedHashMap<String, Map<String, Object>>();
                    return groups;
                }
            }
            """)
            }

            List parts = []
            if (!argsMap['skip-model'])      parts << """            {"model",      "${(argsMap['with-model'] ?: modelClassName)}"}"""
            if (!argsMap['skip-view'])       parts << """            {"view",       "${(argsMap['with-view'] ?: viewClassName)}"}"""
            if (!argsMap['skip-controller']) parts << """            {"controller", "${(argsMap['with-controller'] ?: controllerClassName)}"}"""

            addonFile.withWriter {
                it.write addonText.replaceAll(/\s*Map<String, Map<String, String>> groups = new LinkedHashMap<String, Map<String, String>>\(\);/, """
                    Map<String, Map<String, Object>> groups = new LinkedHashMap<String, Map<String, Object>>();
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
            if (!argsMap['skip-model'])      parts << "            model     : '${(argsMap['with-model'] ?: modelClassName)}'"
            if (!argsMap['skip-view'])       parts << "            view      : '${(argsMap['with-view'] ?: viewClassName)}'"
            if (!argsMap['skip-controller']) parts << "            controller: '${(argsMap['with-controller'] ?: controllerClassName)}'"

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
        if (!argsMap['skip-model'])      parts << "        model      = '${(argsMap['with-model'] ?: modelClassName)}'"
        if (!argsMap['skip-view'])       parts << "        view       = '${(argsMap['with-view'] ?: viewClassName)}'"
        if (!argsMap['skip-controller']) parts << "        controller = '${(argsMap['with-controller'] ?: controllerClassName)}'"

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

setDefaultTarget(createMVC)