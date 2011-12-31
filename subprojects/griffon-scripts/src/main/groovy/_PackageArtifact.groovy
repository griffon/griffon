/*
* Copyright 2010-2011 the original author or authors.
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

import org.codehaus.griffon.artifacts.ArtifactUtils
import org.codehaus.griffon.artifacts.model.Plugin
import org.springframework.core.io.Resource

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_package_artifact_called')) return
_package_artifact_called = true

includeTargets << griffonScript('Init')

checkLicense = { String type ->
    if (!(new File("${basedir}/LICENSE").exists()) && !(new File("${basedir}/LICENSE.txt").exists())) {
        println "No LICENSE.txt file for ${type} found. Please provide a license file containing the appropriate software licensing information (eg. Apache 2.0, GPL etc.)"
        exit(1)
    }
}

loadArtifactInfo = { String type, Resource artifactDescriptor ->
    def descriptorInstance = loadArtifactDescriptorClass(artifactDescriptor.file.name)

    Map map = [
            name: ArtifactUtils.getArchetypeNameFromDescriptor(artifactDescriptor),
            title: descriptorInstance.title,
            license: descriptorInstance.license,
            version: descriptorInstance.version,
            griffonVersion: descriptorInstance.griffonVersion,
            description: descriptorInstance.description.trim(),
            authors: descriptorInstance.authors
    ]

    if (type == Plugin.TYPE) {
        map + [
                toolkits: descriptorInstance.toolkits,
                platforms: descriptorInstance.platforms,
                dependencies: descriptorInstance.dependsOn.collect([]) { entry ->
                    [name: entry.key, version: entry.value]
                }
        ]
    }

    map
}
