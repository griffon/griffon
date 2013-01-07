/*
 * Copyright 2012-2013 the original author or authors.
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


import groovy.json.JsonSlurper
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * @author Andres Almiray
 * @since 1.2.0
 */

includeTargets << griffonScript('_GriffonPackageArtifact')

target(name: 'uploadRelease', description: 'Uploads a release to an artifact repository', prehook: null, posthook: null) {
    String filename = argsMap.params[0]
    if (!filename) {
        event 'StatusError', ['Must specify release package filename']
        exit 1
    }

    File releaseFile = new File(filename)
    if (!releaseFile.exists()) {
        event 'StatusError', ["Release package ${releaseFile.canonicalPath} does not exist"]
        exit 1
    }


    if (!releaseFile.name.startsWith('griffon-') && !releaseFile.name.endsWith('-release.zip')) {
        event 'StatusError', ["Release package ${releaseFile.canonicalPath} appears to be an invalid package file"]
        exit 1
    }

    ZipFile zipFile = null
    ZipEntry artifactEntry = null
    String artifactType = null

    try {
        zipFile = new ZipFile(releaseFile.absolutePath)
    } catch(Exception e) {
        event 'StatusError', ["Release package ${releaseFile.canonicalPath} appears to be an invalid package file.\n$e"]
        exit 1
    }
    for(type in [Plugin.TYPE, Archetype.TYPE]) {
        artifactEntry = zipFile.getEntry(type + '.json')
        artifactType = type
        if (artifactEntry != null) break
    }

    if (!artifactEntry) {
        event 'StatusError', ["Release package ${releaseFile.canonicalPath} appears to be an invalid package file"]
        exit 1
    }

    def json = new JsonSlurper().parseText(zipFile.getInputStream(artifactEntry).text)
    Release release = Release.makeFromJSON(artifactType, json)
    release.file = releaseFile

    selectArtifactRepository()
    setupCredentials()
    event 'StatusUpdate', ["Contacting repository ${artifactRepository}"]
    try {
        if (artifactRepository.uploadRelease(release, username, password)) {
            event 'StatusFinal', ["Successfully published ${release.artifact.name}-${release.version} to ${artifactRepository.name}"]
        } else {
            event 'StatusError', ["Could not publish ${release.artifact.name}-${release.version} to ${artifactRepository.name}"]
        }
    } catch (x) {
        event 'StatusError', ["Could not publish ${release.artifact.name}-${release.version} to ${artifactRepository.name} => ${x}"]
    }
}

setDefaultTarget(uploadRelease)