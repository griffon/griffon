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

import griffon.util.GriffonNameUtils
import griffon.util.GriffonUtil
import griffon.util.MD5
import groovy.json.JsonBuilder
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.springframework.core.io.Resource

import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_package_artifact_called')) return
_griffon_package_artifact_called = true

includeTargets << griffonScript('_GriffonArtifacts')

packageForRelease = false

loadArtifactInfo = { String type, Resource artifactDescriptor ->
    descriptorInstance = loadArtifactDescriptorClass(artifactDescriptor.file.name)

    String name = artifactDescriptor.file.name - "Griffon${GriffonNameUtils.capitalize(type)}.groovy"
    name = GriffonNameUtils.getShortName(name)

    if (packageForRelease) {
        if (!(new File("${basedir}/LICENSE").exists()) && !(new File("${basedir}/LICENSE.txt").exists())) {
            println "No LICENSE.txt file for ${type} found. Please provide a license file containing the appropriate software licensing information (eg. Apache 2.0, BSD, LGPL etc.)"
            exit(1)
        }

        if (descriptorInstance.license == '<UNKNOWN>') {
            println "No suitable license chosen. Please provide a license name (eg. Apache 2.0, BSD, LGPL etc.)"
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
            type: type,
            name: GriffonUtil.getHyphenatedName(name),
            title: descriptorInstance.title,
            license: descriptorInstance.license,
            version: descriptorInstance.version,
            source: descriptorInstance.source,
            documentation: descriptorInstance.documentation,
            griffonVersion: descriptorInstance.griffonVersion,
            description: descriptorInstance.description.trim(),
            authors: descriptorInstance.authors,
            dependencies: []
    ]

    if (type == Plugin.TYPE) {
        map += [
                toolkits: descriptorInstance.toolkits,
                platforms: descriptorInstance.platforms,
                dependencies: descriptorInstance.dependsOn.collect([]) { entry ->
                    [name: entry.key, version: entry.value]
                },
                framework: false
        ]
        try {
            map.framework = descriptorInstance.framework
        } catch(MissingPropertyException mpe) {
            // ignore
        }
    }

    map
}

target(name: 'packageArtifact', description: '',
        prehook: null, posthook: null) {
    artifactPackageDirPath = "${projectTargetDir}/package"
    ant.delete(dir: artifactPackageDirPath, quiet: true, failOnError: false)
    ant.mkdir(dir: artifactPackageDirPath)

    createArtifactDescriptor(artifactInfo, artifactPackageDirPath)
    depends("package_${artifactInfo.type}")

    artifactZipFileName = "griffon-${artifactInfo.name}-${artifactInfo.version}.zip"
    artifactReleaseZipFileName = "griffon-${artifactInfo.name}-${artifactInfo.version}-release.zip"
    ant.delete(file: "${artifactPackageDirPath}/${artifactZipFileName}", quiet: true, failOnError: false)
    ant.zip(destfile: "${artifactPackageDirPath}/${artifactZipFileName}", basedir: artifactPackageDirPath)

    depends("post_package_${artifactInfo.type}")
}

createArtifactDescriptor = { Map artifactInfo, String path ->
    JsonBuilder builder = new JsonBuilder()
    builder.call(artifactInfo)
    new File(path, "${artifactInfo.type}.json").text = builder.toString()
}

createArtifactRelease = { String type, Map artifactInfo ->
    artifactReleaseDirPath = "${projectTargetDir}/release"
    ant.delete(dir: artifactReleaseDirPath, quiet: true, failOnError: false)
    ant.mkdir(dir: artifactReleaseDirPath)
    String artifactZipChecksumFileName = new File("${artifactReleaseDirPath}/${artifactZipFileName}.md5")
    String checksum = MD5.encode(new File("${artifactPackageDirPath}/${artifactZipFileName}").bytes)
    new File(artifactZipChecksumFileName).text = checksum
    artifactInfo.checksum = checksum
    artifactInfo.type = type
    artifactInfo.comment = resolveCommitMessage()

    File releaseNotes = new File("${basedir}/release_notes.md")
    if (!releaseNotes.exists() && !argsMap['no-release-notes']) {
        println "No release notes were found for ${artifactInfo.name}-${artifactInfo.version}. Did you forget to create a release_notes.md file?"
        if (!confirmInput("Would you like to continue with the release without adding release notes?")) {
            exit 1
        }
    } else {
        ant.copy(file: releaseNotes, todir: artifactReleaseDirPath, failOnError: false)
    }

    JsonBuilder builder = new JsonBuilder()
    builder.call(artifactInfo)
    new File(artifactReleaseDirPath, "${type}.json").text = builder.toString()

    releaseFile = new File("${artifactReleaseDirPath}/${artifactReleaseZipFileName}")

    ant.delete(file: releaseFile, quiet: true, failOnError: false)

    ant.zip(destfile: releaseFile, filesonly: true) {
        fileset(dir: artifactReleaseDirPath)
        zipfileset(dir: artifactPackageDirPath,
                includes: artifactZipFileName,
                fullpath: artifactZipFileName)
    }

    release = Release.makeFromJSON(type, artifactInfo)
    release.file = releaseFile
}

setupCredentials = {
    username = ''
    password = ''
    if (!artifactRepository.remote) return

    username = resolveCredential('username')
    password = resolveCredential('password')
}

resolveCredential = { String key ->
    String value = artifactRepository[key]

    if (isBlank(value)) {
        value = argsMap[key]
    }

    if (isBlank(value)) {
        String prop = "credential.${key}".toString()
        ant.input(message: "Please enter your ${key}:", addproperty: prop) {
            if (key == 'password') handler(type: 'secure')
        }
        value = ant.antProject.getProperty(prop)
    }

    if (isBlank(value)) {
        event('StatusError', ["You must provide a value for your ${key} when releasing artifacts to ${repository.name}."])
        exit(1)
    }

    value
}

resolveCommitMessage = {
    commitMessage = argsMap.message

    if (isBlank(commitMessage)) {
        ant.input(message: 'Enter a commit message: ', addproperty: 'commit.message')
        commitMessage = ant.antProject.properties.'commit.message'
    }

    if (isBlank(commitMessage)) {
        event('StatusError', ["You must provide a commit message when releasing artifacts."])
        exit(1)
    }

    commitMessage
}
