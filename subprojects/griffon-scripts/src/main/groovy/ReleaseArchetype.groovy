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

import griffon.util.MD5
import groovy.json.JsonBuilder
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Release

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('_GriffonArtifacts')
includeTargets << griffonScript('PackageArchetype')

target(releaseArchetype: 'Publishes a Griffon archetype release') {
    depends(configureArtifactRepositories)

    packageForRelelease = true
    packageArchetype()
    createArtifactRelease(Archetype.TYPE, archetypeInfo)
    selectArtifactRepository()
    setupCredentials()
    event 'StatusUpdate', ["Contacting repository ${artifactRepository}"]
    try {
        if (artifactRepository.uploadRelease(release, username, password)) {
            event 'StatusFinal', ["Successfully published ${archetypeInfo.name}-${archetypeInfo.version} to ${artifactRepository.name}"]
        } else {
            event 'StatusError', ["Could not publish ${archetypeInfo.name}-${archetypeInfo.version} to ${artifactRepository.name}"]
        }
    } catch (x) {
        event 'StatusError', ["Could not publish ${archetypeInfo.name}-${archetypeInfo.version} to ${artifactRepository.name} => ${x}"]
    }
}

setDefaultTarget(releaseArchetype)

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

    releaseFile = new File("${artifactReleaseDirPath}/${artifactZipFileName}")

    ant.delete(file: releaseFile, quiet: true, failOnError: false)

    ant.zip(destfile: releaseFile, filesonly: true) {
        fileset(dir: artifactReleaseDirPath)
        zipfileset(dir: artifactPackageDirPath,
                includes: artifactZipFileName,
                fullpath: artifactZipFileName)
    }

    release = Release.make(type, artifactInfo)
    release.file = releaseFile
}
