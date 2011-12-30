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

package org.codehaus.griffon.artifacts

import griffon.util.GriffonExceptionHandler
import griffon.util.GriffonUtil
import groovy.transform.Synchronized
import groovyx.net.http.HttpResponseException
import groovyx.net.http.HttpURLClient
import groovyx.net.http.Status
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Release
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static griffon.util.GriffonNameUtils.isBlank
import static org.codehaus.griffon.artifacts.ArtifactUtils.parseArtifact

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class RemoteArtifactRepository extends AbstractArtifactRepository {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteArtifactRepository)

    String url
    String username
    String password

    private HttpURLClient http

    RemoteArtifactRepository() {
        setUrl(null)
    }

    void setUrl(String url) {
        if (isBlank(url)) {
            url = 'http://localhost:8080/griffon-artifact-portal/'
        }
        if (!url.endsWith('/')) url += '/'
        this.url = url
        http(url)
    }

    @Synchronized
    private HttpURLClient http() {
        if (http == null) {
            http = newHttp(url)
        }
        http.headers = [:]
        http
    }

    @Synchronized
    private HttpURLClient http(String url) {
        if (http != null) {
            http.url = url
        } else {
            http = newHttp(url)
        }
        http.headers = [:]
        http
    }

    private HttpURLClient newHttp(String url) {
        HttpURLClient client = new HttpURLClient(url: url)
        client.parsers.'application/zip' = { response ->
            response.entity.content
        }
        client
    }

    List<Artifact> listArtifacts(String type) {
        List<Artifact> artifacts = []
        try {
            def response = http().request(path: "api/${type}s")
            if (response.status == 200) {
                response.data.collect(artifacts) { json ->
                    parseArtifact(type, json)
                }
            }
        } catch (HttpResponseException httpre) {
            LOG.trace("Could not list artifacts of type ${type}", GriffonExceptionHandler.sanitize(httpre))
        }
        artifacts
    }

    Artifact findArtifact(String type, String name) {
        Artifact artifact = null
        try {
            def response = http().request(path: "api/${type}s/${name}")
            if (response.status == 200) {
                artifact = parseArtifact(type, response.data)
            }
        } catch (HttpResponseException httpre) {
            LOG.trace("Could not locate artifact ${type}:${name}", GriffonExceptionHandler.sanitize(httpre))
        }
        artifact
    }

    File downloadFile(String type, String name, String version, String username) {
        File file = null
        try {
            Map<String, String> headers = getDefaultHeaders()
            if (username) headers.'X-Username' = username

            println "Downloading ${http().url}api/${type}s/${name}/download/${version} ..."

            def response = http().request(path: "api/${type}s/${name}/download/${version}", headers: headers)
            if (response.status == 200) {
                file = File.createTempFile("griffon-${name}-${version}-", '.zip')
                file.deleteOnExit()
                file.bytes = response.data.bytes
            }
        } catch (HttpResponseException httpre) {
            LOG.trace("Could not download artifact ${type}:${name}:${version}", GriffonExceptionHandler.sanitize(httpre))
            throw httpre
        }
        file
    }

    boolean uploadRelease(Release release, String username, String password) {
        false
    }

    static File downloadFromURL(String url) {
        downloadFromURL(url.toURL())
    }

    static File downloadFromURL(URL url) {
        File file = null
        Map<String, String> headers = getDefaultHeaders()
        try {
            println "Downloading ${url} ..."

            URLConnection conn = url.openConnection()
            headers.each { key, value -> conn.addRequestProperty(key, value) }
            conn.connect()
            if (Status.find(conn.responseCode) == Status.SUCCESS/* && conn.contentType == 'application/zip'*/) {
                String str = url.toString()
                String fileName = str[(str.lastIndexOf('/') + 1)..-5]
                file = File.createTempFile(fileName + '-', '.zip')
                file.deleteOnExit()
                file.bytes = conn.inputStream.bytes
            }
        } catch (Exception e) {
            LOG.trace("Could not download artifact form URL ${url}", GriffonExceptionHandler.sanitize(e))
            throw e
        }
        file
    }

    private static Map<String, String> getDefaultHeaders() {
        [
                'X-Griffon-Version': GriffonUtil.getGriffonVersion(),
                'X-Java-Version': System.getProperty('java.version'),
                'X-Os-Name': System.getProperty('os.name'),
                'X-Os-Version': System.getProperty('os.version'),
                'X-Os-Arch': System.getProperty('os.arch'),
                'X-Java-Vm-Version': System.getProperty('java.vm.version'),
                'X-Java-Vm-Name': System.getProperty('java.vm.name')
        ]
    }
}
