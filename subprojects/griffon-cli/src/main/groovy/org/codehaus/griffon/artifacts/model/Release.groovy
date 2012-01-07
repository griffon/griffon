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
import groovy.json.JsonSlurper
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

    static Release makeFromFile(String type, File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Cannot create Release based on file $file")
        }

        String fileType = file.name[file.name.lastIndexOf('.')..-1]

        // TODO LEGACY - remove this code before 1.0
        if (!(fileType in ['.json', '.xml'])) {
            throw new IllegalArgumentException("Cannot create Release based on file $file")
        }

        switch (type) {
            case Plugin.TYPE:
            case Archetype.TYPE:
                switch (fileType) {
                    case '.json':
                        return makeFromJSON(type, new JsonSlurper().parseText(file.text))
                        break
                    case '.xml':
                        // TODO LEGACY - remove this code before 1.0
                        return makeFromXML(type, new XmlSlurper().parse(file))
                        break
                }
                break
            default:
                throw new IllegalArgumentException("Cannot create Release based on file $file")
        }
    }

    // TODO LEGACY - remove this code before 1.0
    static Release makeFromXML(String type, xml) {
        switch (type) {
            case Plugin.TYPE:
                Release release = parseReleaseFromXML(xml)
                release.artifact = parsePluginFromXML(xml)
                return release
                break
            case Archetype.TYPE:
                break
        }

        null
    }
}
