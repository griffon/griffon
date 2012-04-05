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

import griffon.util.BuildSettings
import griffon.util.Metadata
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static griffon.util.GriffonExceptionHandler.sanitize
import static griffon.util.GriffonNameUtils.capitalize
import static griffon.util.GriffonNameUtils.getPropertyNameForLowerCaseHyphenSeparatedName
import static org.codehaus.griffon.artifacts.ArtifactUtils.*

/**
 * Created by IntelliJ IDEA.
 * User: m29625
 * Date: 4/1/12
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
class DependencyUnrslvdArtifactInstallEngine extends ArtifactInstallEngine {

    private static final Logger LOG = LoggerFactory.getLogger(DependencyUnrslvdArtifactInstallEngine)

    DependencyUnrslvdArtifactInstallEngine(BuildSettings settings, Metadata metadata, AntBuilder ant) {
        super(settings, metadata, ant)
        this.settings = settings
        this.metadata = metadata
        this.ant = ant
    }

    boolean installPluginWithoutDependency() {
        Map<String, String> registeredPlugins = getRegisteredArtifacts(Plugin.TYPE, metadata)
        Map<String, String> installedPlugins = getInstalledArtifacts(Plugin.TYPE)

        File pluginDescriptor = settings.isPluginProject()
        if (pluginDescriptor) {
            GroovyClassLoader gcl = new GroovyClassLoader(getClass().classLoader)
            gcl.addURL(settings.baseDir.toURI().toURL())
            String artifactClassName = pluginDescriptor.name[0..-8]
            def plugin = gcl.loadClass(artifactClassName).newInstance()
            plugin.dependsOn.each { name, version ->
                registeredPlugins[name] = version.toString()
            }
        }

        if (LOG.debugEnabled) {
            String registered = registeredPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (registered) LOG.debug("Registered plugins:\n\t${registered}")
            String installed = installedPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (installed) LOG.debug("Installed plugins:\n\t${installed}")
        }

        Map<String, String> pluginsToDelete = [:]
        Map<String, String> missingPlugins = [:]
        registeredPlugins.each { name, version ->
            String v = installedPlugins[name]
            if (version.endsWith('-SNAPSHOT') || v != version) {
                missingPlugins[name] = version
            }
        }

        installedPlugins.each { name, version ->
            String v = registeredPlugins[name]
            if (v != version) {
                pluginsToDelete[name] = version
            }
        }

        if (installedPlugins) {
            installedPlugins.each { name, version ->
                metadata["plugins.${name}"] = version
            }
            metadata.persist()
        }

        if (LOG.debugEnabled) {
            String installed = installedPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (installed) LOG.debug("Installed plugins (confirmed):\n\t${installed}")
            String missing = missingPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (missing) LOG.debug("Missing plugins:\n\t${missing}")
            String todelete = pluginsToDelete.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (todelete) LOG.debug("Plugins to be deleted:\n\t${todelete}")
        }

        pluginsToDelete.each { name, version ->
            if (!version.endsWith('-SNAPSHOT')) eventHandler 'StatusUpdate', "Plugin ${name}-${version} is installed, but was not found in the application's metadata. Removing this plugin from the application's plugin base"
            ant.delete(dir: getInstallPathFor(Plugin.TYPE, name, version), failonerror: false)
            installedPlugins.remove(name)
        }

        if (!missingPlugins) {
            return true
        }

        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        List<ArtifactDependency> dependencies = []
        try {
            dependencies = resolver.resolveDependencyTree(Plugin.TYPE, missingPlugins)
        } catch (Exception e) {
            sanitize(e)
            eventHandler 'StatusError', "Some missing plugins failed to resolve => $e"
            errorHandler "Cannot continue with unresolved dependencies."
        }

        if (LOG.debugEnabled && dependencies) {
            LOG.debug("Dependency resolution outcome:\n${dependencies.collect([]) {printDependencyTree(it, true)}.join('\n')}")
        }

        try {
            return _installPlugins(dependencies, resolver)
        } catch (InstallArtifactException iae) {
            errorHandler "Could not resolve plugin dependencies."
        }
    }

    void installFromFile(String type, File file, boolean resolveDependencies = false) {
        Release release = inspectArtifactRelease(type, file)

        String artifactName = release.artifact.name
        String releaseVersion = release.version
        String releaseName = "${artifactName}-${releaseVersion}"
        String artifactInstallPath = "${artifactBase(type)}/${releaseName}"

        if (resolveDependencies && type == Plugin.TYPE) {
            if (release.dependencies) {
                Map<String, String> plugins = [:]
                release.dependencies.each { entry ->
                    plugins[entry.name] = entry.version
                }
                if (!installPlugins(plugins)) {
                    throw new InstallArtifactException("Could not install plugin ${releaseName}")
                }
            }
        }

        if (!release.snapshot && new File(artifactInstallPath).exists()) {
            if (!commandLineHelper.confirmInput("${capitalize(type)} '${releaseName}' is already installed. Overwrite?")) {
                return
            }
        }

        eventHandler 'StatusUpdate', "Software license of ${releaseName} is '${release.artifact.license}'"

        for (dir in findAllArtifactDirsForName(type, artifactName)) {
            ant.delete(dir: dir, failonerror: false)
        }
        ant.mkdir(dir: artifactInstallPath)
        ant.unzip(dest: artifactInstallPath, src: file)

        switch (type) {
            case Plugin.TYPE:
                variableStore["${getPropertyNameForLowerCaseHyphenSeparatedName(artifactName)}PluginDir"] = new File(artifactInstallPath).absoluteFile
                variableStore["${getPropertyNameForLowerCaseHyphenSeparatedName(artifactName)}PluginVersion"] = releaseVersion
                // TODO LEGACY - remove before 1.0
                if (new File(artifactInstallPath, 'plugin.xml').exists()) {
                    generateDependencyDescriptorFor(artifactInstallPath, artifactName, releaseVersion)
                }

                if (!settings.isPluginProject()) {
                    def installScript = new File("${artifactInstallPath}/scripts/_Install.groovy")
                    runPluginScript(installScript, releaseName, 'post-install script')
                }
                displayNewScripts(releaseName, artifactInstallPath)
                break
            case Archetype.TYPE:
                break
        }

        if (settings.isGriffonProject() && !settings.isArchetypeProject() && type == Plugin.TYPE) {
            // Metadata.reload()
            metadata["${type}s." + artifactName] = releaseVersion
            metadata.persist()
            Metadata.reload()
        }

        if (!installedArtifacts.contains(artifactInstallPath)) {
            installedArtifacts << artifactInstallPath
        }

        eventHandler "${capitalize(type)}Installed", [type, artifactName, releaseVersion, artifactInstallPath]
        eventHandler 'StatusFinal', "Installed ${type} '${releaseName}' in ${artifactInstallPath}"
    }
}
