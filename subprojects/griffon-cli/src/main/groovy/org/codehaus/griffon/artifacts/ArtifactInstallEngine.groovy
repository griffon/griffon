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

import groovy.json.JsonException
import groovy.json.JsonSlurper
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.codehaus.griffon.cli.CommandLineHelper
import org.codehaus.griffon.cli.ScriptExitException
import griffon.util.*
import static org.codehaus.griffon.artifacts.ArtifactUtils.*

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class ArtifactInstallEngine {
    private final BuildSettings settings
    private final Metadata metadata
    private final AntBuilder ant
    private CommandLineHelper commandLineHelper = new CommandLineHelper(System.out)

    final List installedArtifacts = []

    Closure errorHandler = { String msg -> throw new ScriptExitException(msg) }
    Closure eventHandler = { String name, String msg -> println msg }
    Closure pluginScriptRunner

    ArtifactInstallEngine(BuildSettings settings, Metadata metadata, AntBuilder ant) {
        this.settings = settings
        this.ant = ant
        this.metadata = metadata
    }

    void installFromFile(String type, File file) {
        Release release = inspectArtifactRelease(type, file)

        // check against release.griffonVersion
        if (!isValidVersion(release.griffonVersion, GriffonUtil.getGriffonVersion())) {
            eventHandler 'StatusError', "${release.artifact.capitalizedType} ${release.artifact.name}-${release.version} could not be installed because it does not meet version requirements. Current version: ${GriffonUtil.getGriffonVersion()}; Expected version ${release.griffonVersion}"
            errorHandler "Installation of ${release.artifact.name}-${release.version} aborted."
        }

        String releaseName = "${release.artifact.name}-${release.version}"
        String artifactInstallPath = null

        switch (type) {
            case Plugin.TYPE:
                // TODO resolve dependencies
                artifactInstallPath = "${pluginsBase()}/${releaseName}"
                break
            case Archetype.TYPE:
                artifactInstallPath = "${archetypesBase()}/${releaseName}"
                break
        }

        if (new File(artifactInstallPath).exists()) {
            if (!commandLineHelper.confirmInput("${GriffonNameUtils.capitalize(type)} '${releaseName}' is already installed. Overwrite?")) {
                return
            }
        }

        eventHandler 'StatusUpdate', "${release.artifact.capitalizedType} license for ${releaseName} is '${release.artifact.license}'"

        for (dir in findAllArtifactDirsForName(type, release.artifact.name)) {
            ant.delete(dir: dir, failonerror: false)
        }
        ant.mkdir(dir: artifactInstallPath)
        ant.unzip(dest: artifactInstallPath, src: file)

        if (!installedArtifacts.contains(artifactInstallPath)) {
            installedArtifacts << artifactInstallPath
        }

        if (settings.isGriffonProject() && !settings.isArchetypeProject()) {
            switch (type) {
                case Plugin.TYPE:
                    metadata["${type}s." + release.artifact.name] = release.version
                    break
                case Archetype.TYPE:
                    metadata["${type}." + release.artifact.name] = release.version
                    break
            }
            metadata.persist()
        }

        eventHandler 'StatusFinal', "Installed ${type} '${releaseName}' in ${artifactInstallPath}"
    }

    void uninstall(String type, String name, String version = null) {
        String metadataKey = ''
        File artifactDir = null

        try {
            if (name && version) {
                artifactDir = new File("${artifactBase(type)}/${name}-${version}")
            } else {
                artifactDir = findArtifactDirForName(type, name)
            }

            switch (type) {
                case Plugin.TYPE:
                    metadataKey = "${type}s.${name}"
                    break
                case Archetype.TYPE:
                    metadataKey = "${type}.${name}"
                    break
            }

            if (artifactDir?.exists()) {
                metadata.remove(metadataKey)
                metadata.persist()
                if (type == Plugin.TYPE) {
                    if (!settings.isPluginProject()) {
                        def uninstallScript = new File("${artifactDir}/scripts/_Uninstall.groovy")
                        runPluginScript(uninstallScript, artifactDir.name, 'uninstall script')
                    }
                }
                ant.delete(dir: artifactDir, failonerror: true)
                eventHandler "${GriffonNameUtils.capitalize(type)}Uninstalled", "Uninstalled ${type} [${name}]."
            } else {
                errorHandler("No ${type} [$name${version ? '-' + version : ''}] installed, cannot uninstall.")
            }
        } catch (e) {
            GriffonExceptionHandler.sanitize(e)
            errorHandler("An error occured uninstalling the ${type} [$name${version ? '-' + version : ''}]: ${e.message}")
        }
    }

    private String artifactBase(String type) {
        switch (type) {
            case Plugin.TYPE:
                return pluginsBase()
            case Archetype.TYPE:
                return archetypesBase()
        }
    }

    private String pluginsBase() {
        settings.getProjectPluginsDir()
    }

    private String archetypesBase() {
        "${settings.griffonWorkDir}/archetypes"
    }

    private Release inspectArtifactRelease(String type, File file) {
        ZipFile zipFile = new ZipFile(file.absolutePath)
        ZipEntry artifactEntry = zipFile.getEntry(type + '.json')
        if (artifactEntry == null) {
            throw new IllegalArgumentException("Not a valid griffon artifact of type $file: missing ${type}.json")
        }

        def json = null
        try {
            json = new JsonSlurper().parseText(zipFile.getInputStream(artifactEntry).text)
        } catch (JsonException e) {
            throw new IllegalArgumentException("Can't parse ${type}.json", e)
        }

        Release.make(zipFile, type, json)
    }

    private void verifyArtifact(ZipFile zipFile, json) {
        String fileName = "griffon-${json.name}-${json.release.version}.zip"
        ZipEntry artifactFileEntry = zipFile.getEntry(fileName)
        ZipEntry md5ChecksumEntry = zipFile.getEntry("${fileName}.md5")

        if (artifactFileEntry == null) {
            throw new IllegalArgumentException("Release does not contain expected zip entry ${fileName}")
        }
        if (md5ChecksumEntry == null) {
            throw new IllegalArgumentException("Release does not contain expected zip entry ${fileName}.md5")
        }

        byte[] bytes = zipFile.getInputStream(artifactFileEntry).bytes
        String computedHash = MD5.encode(bytes)
        String releaseHash = zipFile.getInputStream(md5ChecksumEntry).text

        if (computedHash.trim() != releaseHash.trim()) {
            throw new IllegalArgumentException("Wrong checksum for ${fileName}")
        }
    }

    private void runPluginScript(File scriptFile, fullPluginName, msg) {
        if (pluginScriptRunner != null) {
            if (pluginScriptRunner.maximumNumberOfParameters < 3) {
                throw new IllegalStateException("The [pluginScriptRunner] closure property must accept at least 3 arguments")
            } else {
                pluginScriptRunner.call(scriptFile, fullPluginName, msg)
            }
        }
    }
}
