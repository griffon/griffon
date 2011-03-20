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

target (createMVC : "Creates a new MVC Group") {
    depends(checkVersion, parseArguments)

    if(isPluginProject && !isAddonPlugin) {
        println """You must create an Addon descriptor first.
Type in griffon create-addon then execute this command again."""
        System.exit(1)        
    }

    promptForName(type: "MVC Group")
    def (pkg, name) = extractArtifactName(argsMap['params'][0])

    mvcPackageName = pkg ? pkg : ''
    mvcClassName = GCU.getClassNameRepresentation(name)
    mvcFullQualifiedClassName = "${pkg?pkg:''}${pkg?'.':''}$mvcClassName"

    createArtifact(
        name: mvcFullQualifiedClassName,
        suffix: "Model",
        type: "Model",
        path: "griffon-app/models")

    createArtifact(
        name: mvcFullQualifiedClassName,
        suffix: "View",
        type: "View",
        path: "griffon-app/views")

    createArtifact(
        name: mvcFullQualifiedClassName,
        suffix: "Controller",
        type: "Controller",
        path: "griffon-app/controllers")

    createIntegrationTest(
        name: mvcFullQualifiedClassName,
        suffix: "")

    if (isAddonPlugin) {
        // create mvcGroup in a plugin
        def addonFile = isAddonPlugin
        def addonText = addonFile.text

        if (!(addonText =~ /\s*def\s*mvcGroups\s*=\s*\[/)) {
            addonText = addonText.replaceAll(/\}\s*\z/, """
    def mvcGroups = [
    ]
}
""")
        }
        addonFile.withWriter { it.write addonText.replaceAll(/\s*def\s*mvcGroups\s*=\s*\[/, """
    def mvcGroups = [
        // MVC Group for "$args"
        '$name' : [
            model : '${mvcFullQualifiedClassName}Model',
            view : '${mvcFullQualifiedClassName}View',
            controller : '${mvcFullQualifiedClassName}Controller'
        ]
    """) }


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
        applicationConfigFile.withWriter { it.write configText.replaceAll(/\s*mvcGroups\s*\{/, """
mvcGroups {
    // MVC Group for "$name"
    '$name' {
        model = '${mvcFullQualifiedClassName}Model'
        controller = '${mvcFullQualifiedClassName}Controller'
        view = '${mvcFullQualifiedClassName}View'
    }
""") }
    }
}
