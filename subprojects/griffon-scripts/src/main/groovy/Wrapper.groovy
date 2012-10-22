/*
* Copyright 2012 the original author or authors.
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

import static griffon.util.GriffonNameUtils.capitalize

/**
 * @author Andres Almiray
 * @since 1.2.0
 */

target(name: 'wrapper', description: 'Creates or updates the Griffon wrapper files', prehook: null, posthook: null) {
    if(isPluginProject || isApplicationProject || isArchetypeProject) {
        griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
        ant.unzip(src: "${basedir}/griffon-wrapper-files.zip", dest: basedir)
        ant.delete(file: "${basedir}/griffon-wrapper-files.zip", quiet: true)
        ant.replace(dir: "${basedir}/griffon-app/conf", includes: "**/*.*") {
            replacefilter(token: "@griffonAppName@", value: capitalize(griffonAppName))
            replacefilter(token: "@griffonAppVersion@", value: griffonAppVersion ?: "0.1")
        }
    } else {
        event 'StatusError', ['Cannot create wroaoer files in a non Griffon project']
        exit 1
    }
}

setDefaultTarget(wrapper)