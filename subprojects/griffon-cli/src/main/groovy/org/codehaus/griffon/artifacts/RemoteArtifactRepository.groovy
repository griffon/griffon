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

import com.jcraft.jsch.Channel
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import griffon.util.GriffonUtil
import groovy.transform.Synchronized
import groovyx.net.http.HttpURLClient
import groovyx.net.http.Status
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Release
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static griffon.util.ArtifactSettings.parseArtifactFromJSON
import static griffon.util.GriffonExceptionHandler.sanitize
import static griffon.util.GriffonNameUtils.isBlank
import static groovyx.net.http.ContentType.JSON

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class RemoteArtifactRepository extends AbstractArtifactRepository {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteArtifactRepository)

    String url
    String username
    String password
    int port
    int timeout = 30000i
    final String type = REMOTE

    final boolean local = false
    final boolean remote = true
    final boolean legacy = false

    private HttpURLClient http

    RemoteArtifactRepository() {
        port = 2222
        setUrl(null)
    }

    String toString() {
        "$name ($url)"
    }

    void setTimeout(int timeout) {
        if (timeout < 1000) timeout = timeout * 1000
        this.timeout = timeout
    }

    void setUrl(String url) {
        if (isBlank(url)) {
            url = DEFAULT_REMOTE_LOCATION
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
            def response = http().request(path: "api/${type}s", contentType: JSON, timeout: timeout)
            if (response.status == 200) {
                response.data.collect(artifacts) { json ->
                    parseArtifactFromJSON(type, json)
                }
            }
        } catch (Exception e) {
            if (LOG.debugEnabled) LOG.debug("[${this.name}] Could not list artifacts of type ${type}", sanitize(e))
        }
        artifacts
    }

    Artifact findArtifact(String type, String name) {
        Artifact artifact = null
        try {
            def response = http().request(path: "api/${type}s/${name}", contentType: JSON, timeout: timeout)
            if (response.status == 200) {
                artifact = parseArtifactFromJSON(type, response.data)
            }
        } catch (Exception e) {
            if (LOG.debugEnabled) LOG.debug("[${this.name}] Could not locate artifact ${type}:${name}", sanitize(e))
        }
        artifact
    }

    Artifact findArtifact(String type, String name, String version) {
        Artifact artifact = null
        try {
            def response = http().request(path: "api/${type}s/${name}/${version}", contentType: JSON, timeout: timeout)
            if (response.status == 200) {
                artifact = parseArtifactFromJSON(type, response.data)
            }
        } catch (Exception e) {
            if (LOG.debugEnabled) LOG.debug("[${this.name}] Could not locate artifact ${type}:${name}:${version}", sanitize(e))
        }
        artifact
    }

    File downloadFile(String type, String name, String version, String username) {
        File file = null
        try {
            Map<String, String> headers = getDefaultHeaders()
            if (username) headers.'X-Username' = username

            println "Downloading ${http().url}api/${type}s/${name}/${version}/download ..."

            def response = http().request(path: "api/${type}s/${name}/${version}/download", headers: headers, timeout: timeout)
            if (response.status == 200) {
                file = File.createTempFile("griffon-${name}-${version}-", '.zip')
                file.deleteOnExit()
                file.bytes = response.data.bytes
            }
        } catch (Exception e) {
            if (LOG.debugEnabled) LOG.debug("[${this.name}] Could not download artifact ${type}:${name}:${version}", sanitize(e))
            throw e
        }
        file
    }

    boolean uploadRelease(Release release, String username, String password) {
        File file = release.file
        try {
            // adapted from http://www.jcraft.com/jsch/examples/ScpTo.java
            JSch jsch = new JSch()
            Session session = jsch.getSession(username, getHostname(), port)
            session.userInfo = new SimpleUserInfo(username, password)
            session.connect()

            String command = 'scp -p -t /tmp/' + file.name
            Channel channel = session.openChannel('exec')
            channel.command = command

            // get I/O streams for remote scp
            OutputStream out = channel.outputStream
            InputStream is = channel.inputStream

            channel.connect()

            if (checkAck(is) != 0) {
                return false
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = file.length()
            command = "C0644 " + filesize + " "
            if (file.name.lastIndexOf('/') > 0) {
                command += file.name.substring(file.name.lastIndexOf('/') + 1)
            }
            else {
                command += file.name
            }
            command += "\n"
            out.write(command.bytes)
            out.flush()
            if (checkAck(is) != 0) {
                return false
            }

            out << new FileInputStream(file)

            // send '\0'
            byte[] buf = new byte[1]
            buf[0] = 0; out.write(buf, 0, 1); out.flush();
            if (checkAck(is) != 0) {
                return false
            }
            out.close()

            channel.disconnect()
            session.disconnect()
        } catch (Exception e) {
            sanitize(e)
            if (LOG.debugEnabled) LOG.debug("[${this.name}] Could not upload artifact ${file}", e)
            throw e
        }
        true
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
            if (LOG.debugEnabled) LOG.debug("Could not download artifact form URL ${url}", sanitize(e))
            throw e
        }
        file
    }

    private String getHostname() {
        url.toURL().getHost()
    }

    private static Map<String, String> getDefaultHeaders() {
        [
                'User-Agent': 'HTTPBuilder 0.5.2',
                'X-Griffon-Version': GriffonUtil.getGriffonVersion(),
                'X-Java-Version': System.getProperty('java.version'),
                'X-Os-Name': System.getProperty('os.name'),
                'X-Os-Version': System.getProperty('os.version'),
                'X-Os-Arch': System.getProperty('os.arch'),
                'X-Java-Vm-Version': System.getProperty('java.vm.version'),
                'X-Java-Vm-Name': System.getProperty('java.vm.name')
        ]
    }

    private static int checkAck(InputStream is) throws IOException {
        int b = is.read()
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b
        if (b == -1) return b

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer()
            int c
            while (c != '\n') {
                c = is.read()
                sb.append((char) c)

            }
            if (b == 1) { // error
                System.out.print(sb.toString())
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString())
            }
        }
        return b
    }

    private static class SimpleUserInfo implements UserInfo {
        final String username
        final String password

        SimpleUserInfo(String username, String password) {
            this.username = username
            this.password = password
        }

        String getPassphrase() { null }

        boolean promptPassword(String message) { true }

        boolean promptPassphrase(String message) { true }

        boolean promptYesNo(String message) { true }

        void showMessage(String message) { }
    }
}
