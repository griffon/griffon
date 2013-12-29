/*
 * Copyright 2011-2014 the original author or authors.
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

import org.codehaus.griffon.artifacts.model.Plugin

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('PackagePlugin')

target(name: 'releasePlugin', description: 'Publishes a Griffon plugin release',
        prehook: null, posthook: null) {
    packageForRelease = true
    packagePlugin()
    createArtifactRelease(Plugin.TYPE, artifactInfo)

    if (argsMap['package-only']) exit(0)

    selectArtifactRepository()
    setupCredentials()
    event 'StatusUpdate', ["Contacting repository ${artifactRepository}"]
    try {
        if (artifactRepository.uploadRelease(release, username, password)) {
            event 'StatusFinal', ["Successfully published ${artifactInfo.name}-${artifactInfo.version} to ${artifactRepository.name}"]
        } else {
            event 'StatusError', ["Could not publish ${artifactInfo.name}-${artifactInfo.version} to ${artifactRepository.name}"]
        }
    } catch (x) {
        event 'StatusError', ["Could not publish ${artifactInfo.name}-${artifactInfo.version} to ${artifactRepository.name} => ${x}"]
    }
}

setDefaultTarget(releasePlugin)