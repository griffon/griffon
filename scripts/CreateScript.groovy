/*
 * Copyright 2004-2010 the original author or authors.
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
 * Gant script that creates a Griffon Gant script
 *
 * @author Graeme Rocher (Grails 0.4)
 */

import griffon.util.GriffonUtil

includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("_GriffonCreateArtifacts")

target (default : "Creates a Griffon Gant Script") {
    depends(checkVersion, parseArguments)

    def type = "Script"
    promptForName(type: type)
    argsMap.skipPackagePrompt = true
    def (pkg, name) = extractArtifactName(argsMap["params"][0])

    createArtifact(
        name: name,
        suffix: "",
        type: type,
        path: "scripts")
    createArtifact(
        name: name,
        suffix: "Tests",
        type: "ScriptTests",
        path: "test/cli")

    className = GriffonUtil.getClassNameRepresentation(name)
    artifactFile = "${basedir}/test/cli/${className}Tests.groovy"

    ant.replace(file: artifactFile) {
        replacefilter(token: "@script.name@", value: name )
        replacefilter(token: "${className}Tests", value: className )
    }
}
