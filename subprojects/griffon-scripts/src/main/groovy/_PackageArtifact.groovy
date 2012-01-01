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

import griffon.util.GriffonNameUtils
import griffon.util.GriffonUtil
import org.codehaus.griffon.artifacts.model.Plugin
import org.springframework.core.io.Resource

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_package_artifact_called')) return
_package_artifact_called = true

includeTargets << griffonScript('Init')

packageForRelease = false

checkLicense = { String type ->
    if (!(new File("${basedir}/LICENSE").exists()) && !(new File("${basedir}/LICENSE.txt").exists())) {
        println "No LICENSE.txt file for ${type} found. Please provide a license file containing the appropriate software licensing information (eg. Apache 2.0, GPL etc.)"
        exit(1)
    }
}

loadArtifactInfo = { String type, Resource artifactDescriptor ->
    def descriptorInstance = loadArtifactDescriptorClass(artifactDescriptor.file.name)

    String name = artifactDescriptor.file.name - "Griffon${GriffonNameUtils.capitalize(type)}.groovy"
    name = GriffonNameUtils.getShortName(name)

    if (packageForRelease) {
        if (descriptorInstance.license == '<UNKNOWN>') {
            println "No suitable license chosen. Please provide a license name (eg. Apache 2.0, GPL etc.)"
            exit(1)
        }
        List authors = [
                [
                        name: 'Your Name',
                        email: 'your@email.com'
                ]
        ]

        if (descriptorInstance.authors == authors) {
            println "Please update the artifact's autorship information before releasing."
            exit(1)
        }

        if (descriptorInstance.title == "${GriffonNameUtils.capitalize(type)} summary/headline") {
            println "Please update the artifact's title before releasing."
            exit(1)
        }

        String description = """
Brief description of ${name}.

Usage
----
Lorem ipsum

Configuration
-------------
Lorem ipsum
""".trim()
        if (descriptorInstance.description.trim() == description) {
            println "Please update the artifact's description before releasing."
            exit(1)
        }
    }

    Map map = [
            name: GriffonUtil.getHyphenatedName(name),
            title: descriptorInstance.title,
            license: descriptorInstance.license,
            version: descriptorInstance.version,
            griffonVersion: descriptorInstance.griffonVersion,
            description: descriptorInstance.description.trim(),
            authors: descriptorInstance.authors,
            dependencies: []
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
