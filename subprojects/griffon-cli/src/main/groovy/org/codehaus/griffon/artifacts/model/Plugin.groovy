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

package org.codehaus.griffon.artifacts.model

import groovy.json.JsonBuilder

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class Plugin extends Artifact {
    static final String TYPE = 'plugin'
    static final String DEFAULT_GROUP = 'org.codehaus.griffon.plugins'

    String group = DEFAULT_GROUP
    List<Toolkit> toolkits = []
    List<Platform> platforms = []
    boolean framework

    String toString() {
        super.toString() + [
            toolkits: toolkits,
            platforms: platforms,
            framework: framework
        ]
    }

    def toJSON() {
        JsonBuilder builder = new JsonBuilder()
        builder.call(asMap())
        builder
    }

    Map asMap(boolean includeReleases = true) {
        Map map = [
            type: type,
            name: name,
            group: group,
            title: title,
            license: license,
            source: source ?: '',
            documentation: documentation ?: '',
            toolkits: toolkits*.getLowercaseName(),
            platforms: platforms*.getLowercaseName(),
            authors: authors*.asMap(),
            framework: framework,
            description: description
        ]
        if (includeReleases) {
            map.releases = releases*.asMap()
        }
        map
    }
}
