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

import griffon.util.ArtifactSettings
import griffon.util.GriffonExceptionHandler
import griffon.util.MD5
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.io.FileUtils
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Release
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import static griffon.util.ArtifactSettings.parseArtifactFromJSON
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class LocalArtifactRepository extends AbstractArtifactRepository {
    private static final Logger LOG = LoggerFactory.getLogger(LocalArtifactRepository)

    String path
    final String type = LOCAL

    final boolean local = true
    final boolean remote = false
    final boolean legacy = false

    LocalArtifactRepository() {
        setPath(null)
    }

    String toString() {
        "${name} ($path)"
    }

    void setPath(String path) {
        if (isBlank(path)) {
            path = DEFAULT_LOCAL_LOCATION
        }
        this.path = path
    }

    List<Artifact> listArtifacts(String type) {
        List<Artifact> artifacts = []
        File releasePath = new File(path, "${type}s")
        if (!releasePath.exists()) return artifacts

        releasePath.eachDir { dir ->
            Artifact artifact = findArtifact(type, dir.name)
            if (artifact) artifacts << artifact
        }

        artifacts
    }

    Artifact findArtifact(String type, String name) {
        File releasePath = new File(path, "${type}s/${name}")
        String fileName = "${type}.json"

        File artifactFile = new File(releasePath, fileName)
        if (artifactFile.exists()) {
            def json = new JsonSlurper().parseText(artifactFile.text)
            return parseArtifactFromJSON(type, json)
        }
        null
    }

    Artifact findArtifact(String type, String name, String version) {
        File releasePath = new File(path, "${type}s/${name}/${version}")
        String fileName = "${type}.json"

        File artifactFile = new File(releasePath, fileName)
        if (artifactFile.exists()) {
            def json = new JsonSlurper().parseText(artifactFile.text)
            return parseArtifactFromJSON(type, json)
        }
        null
    }

    File downloadFile(String type, String name, String version, String username) {
        File releasePath = new File(path, "${type}s/${name}/${version}")
        String fileName = "griffon-${name}-${version}.zip"

        File releaseFile = new File(releasePath, fileName)
        if (releaseFile.exists()) {
            return releaseFile
        }

        null
    }

    boolean uploadRelease(Release release, String username, String password) {
        File file = release.file
        release.date = new Date()
        try {
            File releasePath = new File(path, "${release.artifact.type}s/${release.artifact.name}/${release.version}/")
            releasePath.mkdirs()

            ZipFile zipFile = new ZipFile(file)
            String descriptorName = "${release.artifact.type}.json"
            String fileName = "griffon-${release.artifact.name}-${release.version}.zip"
            ZipEntry artifactFileEntry = zipFile.getEntry(fileName)
            ZipEntry md5ChecksumEntry = zipFile.getEntry("${fileName}.md5")

            if (artifactFileEntry == null) {
                throw new IOException("[${this.name}] Release does not contain expected zip entry ${fileName}")
            }
            if (md5ChecksumEntry == null) {
                throw new IOException("[${this.name}] Release does not contain expected zip entry ${fileName}.md5")
            }

            byte[] bytes = zipFile.getInputStream(artifactFileEntry).bytes
            String computedHash = MD5.encode(bytes)
            String releaseHash = zipFile.getInputStream(md5ChecksumEntry).text

            if (computedHash.trim() != releaseHash.trim()) {
                throw new IOException("[${this.name}] Wrong checksum for ${fileName}")
            }

            OutputStream os = new FileOutputStream("${releasePath}/${fileName}")
            os.bytes = zipFile.getInputStream(artifactFileEntry).bytes
            os = new FileOutputStream("${releasePath}/${fileName}.md5")
            os.bytes = zipFile.getInputStream(md5ChecksumEntry).bytes

            Map artifactAsMap = release.artifact.asMap()
            release.checksum = computedHash
            Map releaseAsMap = release.asMap()

            artifactAsMap.remove('releases')
            artifactAsMap.release = releaseAsMap

            os = new FileOutputStream("${releasePath}/${descriptorName}")
            JsonBuilder builder = new JsonBuilder()
            builder.call(artifactAsMap)
            os << builder.toString()

            artifactAsMap.remove('release')
            artifactAsMap.releases = [releaseAsMap]
            File out = new File("${releasePath.parentFile.absolutePath}/${descriptorName}")
            if (out.exists()) {
                def json = new JsonSlurper().parseText(out.text)
                json.releases?.each { rel ->
                    if (rel.version == release.version) return
                    artifactAsMap.releases << ArtifactSettings.parseReleaseFromJSON(rel).asMap()
                }
                artifactAsMap.releases.sort {a, b -> b.version <=> a.version}
            }

            os = new FileOutputStream("${releasePath.parentFile.absolutePath}/${descriptorName}")
            builder = new JsonBuilder()
            builder.call(artifactAsMap)
            os << builder.toString()

            File packageFile = new File("${releasePath}/package/${file.name}")
            packageFile.parentFile.mkdirs()
            FileUtils.copyFile(file, packageFile)
        } catch (Exception e) {
            GriffonExceptionHandler.sanitize(e)
            if (LOG.warnEnabled) LOG.warn("[${this.name}] Could not upload artifact ${file}", e)
            throw e
        }

        true
    }
}
