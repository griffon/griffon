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
import org.codehaus.griffon.artifacts.ArtifactUtils
import static org.codehaus.griffon.artifacts.ArtifactUtils.*

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class Release {
    String version = ''
    String griffonVersion = ''
    String comment = ''
    String checksum = ''
    Date date = new Date()
    Artifact artifact
    List<Map<String, String>> dependencies = []
    File file

    String toString() {
        [
                version: version,
                griffonVersion: griffonVersion,
                comment: comment,
                checksum: checksum,
                date: date,
                artifact: artifact,
                dependencies: dependencies,
                file: file
        ]
    }

    def toJSON() {
        JsonBuilder builder = new JsonBuilder()
        builder.call(asMap())
        builder
    }

    Map asMap() {
        [
                version: version,
                griffonVersion: griffonVersion,
                comment: comment,
                checksum: checksum,
                date: date.format(ArtifactUtils.TIMESTAMP_FORMAT),
                dependencies: dependencies
        ]
    }

    static Release makeFromJSON(String type, json) {
        Release release = parseReleaseFromJSON(json)
        switch (type) {
            case Plugin.TYPE:
                release.artifact = parsePluginFromJSON(json)
                break
            case Archetype.TYPE:
                release.artifact = parseArchetypeFromJSON(json)
                break
        }

        release
    }
}
