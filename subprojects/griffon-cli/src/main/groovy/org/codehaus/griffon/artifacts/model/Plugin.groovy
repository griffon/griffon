/*
 * Copyright 2011 the original author or authors.
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

    List<Toolkit> toolkits = []
    List<Platform> platforms = []
    Map<String, String> dependencies = [:]

    String toString() {
        super.toString() + [
                toolkits: toolkits,
                platforms: platforms,
                dependencies: dependencies
        ]
    }

    def toJSON() {
        JsonBuilder builder = new JsonBuilder()
        builder.call(asMap())
        builder
    }

    Map asMap() {
        [
                type: type,
                name: name,
                title: title,
                license: license,
                source: source ?: '',
                toolkits: toolkits*.getLowercaseName(),
                platforms: platforms*.getLowercaseName(),
                dependencies: dependencies.collect([]) {dep ->
                    [name: dep.key, version: dep.value]
                },
                authors: authors*.asMap(),
                releases: releases*.asMap()
        ]
    }
}
