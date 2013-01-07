/*
* Copyright 2004-2013 the original author or authors.
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

includeTargets << griffonScript('_GriffonListArtifacts')

target(name: 'listPlugins', description: 'Lists all plugins available in an artifact repository', prehook: null, posthook: null) {
    listArtifacts(Plugin.TYPE)
}

setDefaultTarget(listPlugins)