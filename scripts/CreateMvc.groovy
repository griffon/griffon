/*
 * Copyright 2004-2008 the original author or authors.
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
 * @author Graeme Rocher
 *
 */

import org.codehaus.griffon.commons.GriffonClassUtils as GCU

Ant.property(environment:"env")
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"

includeTargets << new File ( "${griffonHome}/scripts/Init.groovy" )
includeTargets << new File( "${griffonHome}/scripts/CreateIntegrationTest.groovy")

target ('default': "Creates a new MVC triad") {
    depends(checkVersion)
    createMVC()
}

target (createMVC : "Creates a new MVC Triad") {

    typeName = "Model"
    artifactName = "Model"
    artifactPath = "griffon-app/models"
    createArtifact()

    typeName = "View"
    artifactName = "View"
    artifactPath = "griffon-app/views"
    createArtifact()

    typeName = "Controller"
    artifactName = "Controller"
    artifactPath = "griffon-app/controllers"
    createArtifact()
    createTestSuite()

    def (pkg, name) = extractArtifactName(args)
    def fqn = "${pkg?pkg:''}${pkg?'.':''}${GCU.getClassNameRepresentation(name)}"

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
    // MVC Group for "$args"
    $name {
        model = '${fqn}Model'
        view = '${fqn}View'
        controller = '${fqn}Controller'
    }
""") }


}