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

import org.codehaus.griffon.artifacts.model.Plugin

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('_GriffonListArtifacts')

target(name: 'pluginInfo', description: 'Displays information on a Griffon plugin', prehook: null, posthook: null) {
    if (argsMap.params) {
        def name = argsMap.params[0]
        def version = argsMap.params.size() > 1 ? argsMap.params[1] : null

        displayArtifact(Plugin.TYPE, name, version)
    } else {
        event('StatusError', ['Usage: griffon plugin-info <plugin-name> [version]'])
    }
}

setDefaultTarget(pluginInfo)
