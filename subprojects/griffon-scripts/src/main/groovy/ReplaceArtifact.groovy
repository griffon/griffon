/*
 * Copyright 2010-2012 the original author or authors.
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
 * Gant script that replaces one artifact with anothee template
 *
 * @author Andres Almiray
 * @since 0.9.1
 */

import griffon.util.GriffonNameUtils

includeTargets << griffonScript('_GriffonCreateArtifacts')

target(replaceArtifact: "Replaces an artifact file using another template") {
    if (!argsMap.type) {
        ant.input(addProperty: "artifact.type", message: "Artifact type not specified. Please enter:")
        argsMap.type = ant.antProject.properties."artifact.type"
    }
    argsMap.type = GriffonNameUtils.capitalize(argsMap.type)
    path = (GriffonNameUtils.uncapitalize(argsMap.type) + 's')

    ant.mkdir(dir: "${basedir}/griffon-app/${path}")

    def type = argsMap.type
    promptForName(type: type)

    def name = argsMap["params"][0]

    argsMap['file-type'] = argsMap['file-type'] ?: argsMap.fileType
    if (!argsMap['file-type']) {
        ant.input(addProperty: "artifact.fileType", message: "Artifact file type not specified. Please enter:")
        argsMap['file-type'] = GriffonNameUtils.uncapitalize(ant.antProject.properties."artifact.fileType")
    }

    replaceNonag = true
    createArtifact(name: name,
            suffix: type,
            type:   type,
            path:   "griffon-app/${path}")
}

setDefaultTarget(replaceArtifact)
