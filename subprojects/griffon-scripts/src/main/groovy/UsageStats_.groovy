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

import org.codehaus.griffon.cli.GriffonUsageStats

/**
 * @author Andres Almiray
 * @since 1.2.0
 */

target(name: 'usageStats', description: 'Queries or updates the status of usage stats gathering',
    prehook: null, posthook: null) {
    if (argsMap.containsKey('enabled')) {
        def enabled = argsMap.enabled
        if (enabled instanceof Boolean) {
            enabled = enabled.booleanValue()
        } else if (enabled instanceof CharSequence) {
            enabled = Boolean.parseBoolean(enabled.toString().toLowerCase())
        } else {
            enabled = false
        }
        GriffonUsageStats.setEnabled(griffonSettings, enabled)
    }
    println """
Gathering of usage stats is currently ${GriffonUsageStats.isEnabled(griffonSettings) ? 'ENABLED' : 'DISABLED'}
"""
}

setDefaultTarget(usageStats)
