/*
 * Copyright 2012-2014 the original author or authors.
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
 * @author Andres Almiray
 */

target(name: 'release', description: 'Releases a Griffon project according to its type', prehook: null, posthook: null) {
    if(griffonSettings.isPluginProject()) {
        includeTargets << griffonScript('ReleasePlugin')
        depends(checkVersion, releasePlugin)
        return
    } else if(griffonSettings.isArchetypeProject()) {
        includeTargets << griffonScript('ReleaseArchetype')
        depends(checkVersion, releaseArchetype)
        return
    } else {
        event 'StatusError', ['Cannot create a release for an application project']
        exit 1
    }
}

setDefaultTarget(release)