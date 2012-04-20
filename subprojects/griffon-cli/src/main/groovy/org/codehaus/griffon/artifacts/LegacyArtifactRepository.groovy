/*
 * Copyright 2011-2012 the original author or authors.
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

package org.codehaus.griffon.artifacts

import griffon.util.GriffonExceptionHandler
import groovyx.net.http.HttpURLClient
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Release
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static griffon.util.ArtifactSettings.parseArtifactFromJSON
import static griffon.util.ArtifactSettings.parseReleaseFromJSON
import static groovyx.net.http.ContentType.JSON

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class LegacyArtifactRepository extends AbstractArtifactRepository {
    private static final Logger LOG = LoggerFactory.getLogger(LegacyArtifactRepository)

    final String type = 'legacy'
    final String name = 'griffon-legacy'
    final String url = 'http://svn.codehaus.org/griffon/plugins/'
    final int timeout = 30000i

    final boolean local = false
    final boolean remote = false
    final boolean legacy = true

    private final HttpURLClient http

    LegacyArtifactRepository() {
        http = new HttpURLClient(url: url)
        http.parsers.'application/zip' = { response ->
            response.entity.content
        }
    }

    List<Artifact> listArtifacts(String type) {
        List<Artifact> artifacts = []
        if (type == Archetype.TYPE) return artifacts

        try {
            def response = http.request(path: 'repository.json', contentType: JSON, timeout: timeout)
            if (response.status == 200) {
                response.data.collect(artifacts) { json ->
                    parseArtifactFromJSON(type, json)
                }
            }
        } catch (Exception e) {
            if (LOG.warnEnabled) LOG.warn("[${this.name}] Could not list artifacts of type ${type}", GriffonExceptionHandler.sanitize(e))
        }

        artifacts
    }

    Artifact findArtifact(String type, String name) {
        if (LOG.debugEnabled) {
            LOG.debug("${name}: searching for ${type}:${name}")
        }
        Artifact artifact = null
        if (type == Archetype.TYPE) return artifact

        try {
            def response = http.request(path: "griffon-${name}/tags/LATEST_RELEASE/plugin.json", contentType: JSON, timeout: timeout)
            if (response.status == 200) {
                artifact = parseArtifactFromJSON(type, response.data)
            }
        } catch (Exception e) {
            if (LOG.warnEnabled) LOG.warn("[${this.name}] Could not locate artifact ${type}:${name}", GriffonExceptionHandler.sanitize(e))
        }

        artifact
    }

    Artifact findArtifact(String type, String name, String version) {
        if (LOG.debugEnabled) {
            LOG.debug("${name}: searching for ${type}:${name}:${version}")
        }
        Artifact artifact = null
        if (type == Archetype.TYPE) return artifact

        try {
            String v = version.replaceAll('-', '_')
            v = v.replaceAll('\\.', '_')
            def response = http.request(path: "griffon-${name}/tags/RELEASE_${v}/plugin.json", contentType: JSON, timeout: timeout)
            if (response.status == 200) {
                artifact = parseArtifactFromJSON(type, response.data)
                Release release = parseReleaseFromJSON(response.data)
                release.artifact = artifact
                artifact.releases.clear()
                artifact.releases << release
            }
        } catch (Exception e) {
            if (LOG.warnEnabled) LOG.warn("[${this.name}] Could not locate artifact ${type}:${name}:${version}", GriffonExceptionHandler.sanitize(e))
        }

        artifact
    }

    File downloadFile(String type, String name, String version, String username) {
        if (type == Archetype.TYPE) return null

        File file = null
        try {
            String formattedVersion = version.replace('.', '_')
            String path = "griffon-${name}/tags/RELEASE_${formattedVersion}/griffon-${name}-${version}.zip"
            println "Downloading ${url}${path} ..."

            def response = http.request(path: path)
            if (response.status == 200) {
                file = File.createTempFile("griffon-${name}-${version}-", '.zip')
                file.deleteOnExit()
                file.bytes = response.data.bytes
            }
        } catch (Exception e) {
            if (LOG.warnEnabled) LOG.warn("[${this.name}] Could not download artifact ${type}:${name}:${version}", GriffonExceptionHandler.sanitize(e))
            throw e
        }
        file
    }

    boolean uploadRelease(Release release, String username, String password) {
        false
    }
}
