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

import groovy.json.JsonBuilder
import org.apache.commons.io.FileUtils
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.codehaus.griffon.cli.CommandLineHelper
import org.codehaus.griffon.cli.ScriptExitException
import org.codehaus.griffon.resolve.IvyDependencyManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import griffon.util.*
import static griffon.util.GriffonNameUtils.*
import static griffon.util.GriffonUtil.getScriptName
import static griffon.util.GriffonUtil.sanitize
import static org.codehaus.griffon.artifacts.ArtifactRepository.DEFAULT_LOCAL_NAME
import static org.codehaus.griffon.artifacts.ArtifactUtils.*
import static org.codehaus.griffon.cli.CommandLineConstants.KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class ArtifactInstallEngine {
    private static final Logger LOG = LoggerFactory.getLogger(ArtifactInstallEngine)
    private static final String INSTALL_FAILURE_KEY = 'griffon.install.failure'
    private static final String INSTALL_FAILURE_ABORT = 'abort'
    private static final String INSTALL_FAILURE_CONTINUE = 'continue'
    private static final String INSTALL_FAILURE_RETRY = 'retry'

    private final BuildSettings settings
    private final Metadata metadata
    private final AntBuilder ant
    private CommandLineHelper commandLineHelper = new CommandLineHelper(System.out)

    final List installedArtifacts = []
    final List uninstalledArtifacts = []
    def variableStore = [:]

    Closure errorHandler = { msg -> throw new ScriptExitException(1, msg) }
    Closure eventHandler = { name, msg -> if (msg instanceof CharSequence) println msg}
    Closure pluginScriptRunner
    boolean interactive

    ArtifactInstallEngine(BuildSettings settings, Metadata metadata, AntBuilder ant) {
        this.settings = settings
        this.metadata = metadata
        this.ant = ant
    }

    String getInstallFailureStrategy() {
        String value = System.getProperty(INSTALL_FAILURE_KEY)
        if (isBlank(value)) {
            value = settings.config[INSTALL_FAILURE_KEY]
        }

        value = value ? value.toString().toLowerCase() : INSTALL_FAILURE_CONTINUE
        value in [INSTALL_FAILURE_ABORT, INSTALL_FAILURE_CONTINUE] ? value : INSTALL_FAILURE_CONTINUE
    }

    boolean resolvePluginDependencies() {
        Map<String, String> registeredPlugins = getRegisteredArtifacts(Plugin.TYPE, metadata)
        Map<String, String> installedPlugins = getInstalledArtifacts(Plugin.TYPE)

        File pluginDescriptor = settings.isPluginProject()
        if (pluginDescriptor) {
            GroovyClassLoader gcl = new GroovyClassLoader(getClass().classLoader)
            String artifactClassName = pluginDescriptor.name[0..-8]
            def plugin = gcl.loadClass(artifactClassName).newInstance()
            plugin.dependsOn.each { name, version ->
                registeredPlugins[name] = version
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
            GriffonExceptionHandler.sanitize(e)
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

    boolean installArtifact(String type, String name, String version = null) {
        switch (type) {
            case Plugin.TYPE:
                return installPlugin(name, version)
            case Archetype.TYPE:
                return installArchetype(name, version)
        }
    }

    boolean installArchetype(String name, String version = null) {
        String type = Archetype.TYPE
        Artifact artifact = null
        ArtifactRepository artifactRepository = null

        ArtifactRepositoryRegistry.instance.withRepositories {String repoName, ArtifactRepository repository ->
            if (artifact) return
            artifact = repository.findArtifact(type, name)
            if (artifact) {
                if (LOG.debugEnabled) {
                    LOG.debug("Resolved ${type}:${name}:${version ? version : '<noversion>'} with repository ${repository.name}")
                }
            }
            artifactRepository = repository
        }

        Release release = null
        if (artifact != null) {
            if (version != null) {
                release = artifact.releases.find {it.version == version}
            } else {
                for (r in artifact.releases) {
                    if (isValidVersion(GriffonUtil.griffonVersion, r.griffonVersion)) {
                        release = r
                        break
                    }
                }
            }
        }

        if (release == null) {
            if (LOG.debugEnabled) {
                LOG.debug("Could not resolve ${type}:${name}:${version ? version : '<noversion>'}")
            }
            errorHandler "Installation of ${type} ${name}${version ? '-' + version : ''} aborted."
        }

        File file = artifactRepository.downloadFile(type, name, release.version, null)
        installFromFile(type, file)
        true
    }

    boolean installPlugin(String name, String version = null) {
        String type = Plugin.TYPE

        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        List<ArtifactDependency> dependencies = resolveDependenciesFor(resolver, type, name, version)

        try {
            return _installPlugins(dependencies, resolver)
        } catch (InstallArtifactException iae) {
            errorHandler "Installation of ${type} ${name}${version ? '-' + version : ''} aborted."
        }
    }

    boolean installPlugins(Map<String, String> plugins) {
        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        List<ArtifactDependency> dependencies = []
        try {
            dependencies = resolver.resolveDependencyTree(Plugin.TYPE, plugins)
        } catch (Exception e) {
            GriffonExceptionHandler.sanitize(e)
            eventHandler 'StatusError', "Some plugins failed to resolve => $e"
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

    private boolean _installPlugins(List<ArtifactDependency> dependencies, ArtifactDependencyResolver resolver) {
        installedArtifacts.clear()
        uninstalledArtifacts.clear()

        List<ArtifactDependency> installPlan = resolveDependenciesFor(dependencies, resolver)
        installPluginsInternal(installPlan)
    }

    private boolean installPluginsInternal(List<ArtifactDependency> installPlan) {
        List<ArtifactDependency> failedDependencies = []
        List<ArtifactDependency> retryDependencies = []

        _installDependencies(installPlan, failedDependencies, retryDependencies, true)

        if (retryDependencies) {
            _installDependencies(retryDependencies, failedDependencies, [], false)
        }

        if (failedDependencies) {
            String failed = ''
            failedDependencies.each {failed += it.toString() }
            eventHandler 'StatusFinal', "The following plugins failed to be installed due to missing dependencies or a postinstall error.\n${failed}"
            return false
        }

        true
    }

    private void _installDependencies(List<ArtifactDependency> dependencies, List<ArtifactDependency> failedDependencies, List<ArtifactDependency> retryDependencies, boolean retryAllowed) {
        String type = Plugin.TYPE
        for (ArtifactDependency dependency: dependencies) {
            try {
                if (dependency.evicted) {
                    doUninstall(type, dependency.name, dependency.version)
                } else {
                    if (dependency.snapshot) {
                        Release installedRelease = getInstalledRelease(type, dependency.name, dependency.version)
                        if (installedRelease) {
                            if (LOG.debugEnabled) {
                                LOG.debug("${dependency.name}-${dependency.version} installed=[checksum: ${installedRelease.checksum}, date: ${installedRelease.date}] download=[checksum: ${dependency.release.checksum}, date: ${dependency.release.date}] ")
                            }
                            if (installedRelease.checksum == dependency.release.checksum ||
                                    installedRelease.date >= dependency.release.date) continue
                        }
                    }

                    File file = dependency.repository.downloadFile(type, dependency.name, dependency.version, null)
                    installFromFile(type, file)

                    updateLocalReleaseMetadata(type, dependency.release)
                    publishReleaseToGriffonLocal(dependency.release, file)
                }
            } catch (Exception e) {
                failedDependencies << dependency
                GriffonExceptionHandler.sanitize(e)
                eventHandler 'StatusError', "${dependency.toString().trim()} [FAILED]"
                eventHandler 'StatusError', e.message
                switch (getInstallFailureStrategy()) {
                    case INSTALL_FAILURE_CONTINUE:
                        ant.delete(dir: getInstallPathFor(type, dependency.name, dependency.version), failonerror: false)
                        // try next dependency
                        break
                    case INSTALL_FAILURE_RETRY:
                        if (retryAllowed) {
                            failedDependencies.remove(dependency)
                            retryDependencies << dependency
                        }
                        break
                    case INSTALL_FAILURE_ABORT:
                        eventHandler 'StatusError', "Plugin ${dependency.name}-${dependency.version} could not be installed => $e"
                        throw new InstallArtifactException("Installation of ${dependency.name}-${dependency.version} aborted.")
                }
            }
        }
    }

    void updateLocalReleaseMetadata(String type, Release release) {
        String artifactInstallPath = getInstallPathFor(type, release.artifact.name, release.version)
        File releaseFile = new File(artifactInstallPath, "${type}.json")
        releaseFile.text = release.toJSON().toString()
    }

    void publishReleaseToGriffonLocal(Release release, File file) {
        if (!release.snapshot && !settings.config.griffon.disable.local.repository.sync) {
            _publishReleaseToGriffonLocal(release.artifact.type, release.artifact.name, release.version, file)
        }
    }

    private void _publishReleaseToGriffonLocal(String type, String name, String version, File file) {
        String repositoryName = settings.getConfigValue(KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY, DEFAULT_LOCAL_NAME)
        ArtifactRepository griffonLocal = ArtifactRepositoryRegistry.instance.findRepository(repositoryName)

        if(griffonLocal.type != ArtifactRepository.LOCAL) {
            if (LOG.warnEnabled) {
                LOG.warn("Repository ${repositoryName} is not a local repository; will use ${DEFAULT_LOCAL_NAME} instead.")
            }
            griffonLocal = ArtifactRepositoryRegistry.instance.findRepository(DEFAULT_LOCAL_NAME)
        }

        // don't install if already available at griffon-local
        if (griffonLocal.findArtifact(type, name, version)) return

        File tmpdir = new File(System.getProperty('java.io.tmpdir'), "griffon-${name}-${version}")
        tmpdir.deleteOnExit()
        try {
            tmpdir.mkdirs()

            Release release = getInstalledRelease(type, name, version)

            Map map = release.artifact.asMap(false)
            map.release = release.asMap()
            JsonBuilder builder = new JsonBuilder()
            builder.call(map)

            File releaseDescriptor = new File("${tmpdir}/${type}.json")
            releaseDescriptor.text = builder.toString()

            File checksum = new File("${tmpdir}/griffon-${name}-${version}.zip.md5")
            checksum.text = release.checksum

            FileUtils.copyFile(file, new File("${tmpdir}/griffon-${name}-${version}.zip"))

            File releaseZipFile = new File("${System.getProperty('java.io.tmpdir')}/griffon-${name}-${version}.zip")
            ant.zip(destfile: releaseZipFile, filesonly: true) {
                fileset(dir: tmpdir)
            }

            release.file = releaseZipFile
            griffonLocal.uploadRelease(release, null, null)
            if (LOG.infoEnabled) {
                LOG.info("Successfully published plugin ${name}-${version} to ${repositoryName}.")
            }
        } catch (Exception e) {
            if (LOG.warnEnabled) {
                LOG.warn("Could not push release ${name}-${version} to griffon-local.", sanitize(e))
            }
        }
    }

    private List resolveDependenciesFor(ArtifactDependencyResolver resolver, String type, String name, String version) {
        List<ArtifactDependency> dependencies = []
        try {
            dependencies << resolver.resolveDependencyTree(type, name, version)
        } catch (Exception e) {
            GriffonExceptionHandler.sanitize(e)
            eventHandler 'StatusError', "${capitalize(type)} ${name}${version ? '-' + version : ''} could not be installed => $e"
            errorHandler "Installation of ${type} ${name}${version ? '-' + version : ''} aborted."
        }

        if (LOG.debugEnabled && dependencies) {
            LOG.debug("Dependency resolution outcome:\n${dependencies.collect([]) {printDependencyTree(it, true)}.join('\n')}")
        }
        dependencies
    }

    private List<ArtifactDependency> resolveDependenciesFor(List<ArtifactDependency> dependencies, ArtifactDependencyResolver resolver) {
        Map<String, Release> installedReleases = getInstalledReleases(Plugin.TYPE)

        Map<String, ArtifactDependency> installedDependencies = [:]
        installedReleases.each { String key, release ->
            ArtifactDependency dep = installedDependencies[key]
            if (!dep && !release.version.endsWith('-SNAPSHOT')) {
                dep = new ArtifactDependency(key)
                dep.version = release.version
                dep.installed = true
                dep.resolved = true
                installedDependencies[key] = dep
            }
        }

        installedReleases.each { key, installed ->
            ArtifactDependency dependency = installedDependencies[key]
            installed.dependencies.each { entry ->
                ArtifactDependency dep = installedDependencies[entry.name]
                if (dep) dependency.dependencies << dep
            }
        }

        if (LOG.debugEnabled && installedDependencies) {
            LOG.debug("Installed dependencies:\n${installedDependencies.values().collect([]) {printDependencyTree(it, true)}.join('\n')}")
        }

        if (dependencies.grep {!it.resolved}) {
            String installed = installedDependencies.values().collect([]) {printDependencyTree(it, true)}.join('\n')
            String target = dependencies.collect([]) {printDependencyTree(it, true)}.join('\n')
            eventHandler 'StatusError', "Some dependencies could not be resolved.\n-= INSTALLED =-\n${installed}\n-= MISSING =-\n${target}"
            throw new InstallArtifactException('There are unresolved plugin dependencies.')
        }

        List<ArtifactDependency> installPlan = resolver.resolveEvictions(installedDependencies.values(), dependencies)
        if (LOG.debugEnabled) {
            String installed = installedDependencies.values().collect([]) {printDependencyTree(it, true)}.join('\n')
            String target = dependencies.collect([]) {printDependencyTree(it, true)}.join('\n')
            if (installed || target) LOG.debug("Dependency evictions & conflicts outcome:\n-= INSTALLED =-\n${installed}\n-= MISSING =-\n${target}")
        }

        if (dependencies.grep {it.conflicted}) {
            String installed = installedDependencies.values().collect([]) {printDependencyTree(it, true)}.join('\n')
            String target = dependencies.collect([]) {printDependencyTree(it, true)}.join('\n')
            eventHandler 'StatusError', "Some dependencies have conflicts.\n-= INSTALLED =-\n${installed}\n-= MISSING =-\n${target}"
            throw new InstallArtifactException('There are plugin dependencies with unresolved conflicts.')
        }

        if (LOG.debugEnabled && installPlan) {
            LOG.debug("Dependency install plan:\n${installPlan.collect([]) {it.toString().trim()}.join('\n')}")
        }

        installPlan
    }

    private String printDependencyTree(ArtifactDependency artifactDependency, boolean trim = false) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream out = new PrintStream(baos)
        artifactDependency.printout(0i, out, true)
        trim ? baos.toString().trim() : baos.toString()
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

        for (dir in findAllArtifactDirsForName(type, releaseName)) {
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
                try {
                    resolvePluginJarDependencies(artifactInstallPath, artifactName, releaseVersion)
                } catch (InstallArtifactException iae) {
                    ant.mkdir(dir: artifactInstallPath)
                    throw iae
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
            Metadata.reload()
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

    // TODO LEGACY - remove before 1.0
    private void generateDependencyDescriptorFor(String pluginDirPath, String name, String version) {
        File addonJar = new File(pluginDirPath, "addon/griffon-${name}-addon-${version}.jar")
        File cliJar = new File(pluginDirPath, "addon/griffon-${name}-cli-${version}.jar")
        File testJar = new File(pluginDirPath, "dist/griffon-${name}-${version}-test.jar")

        String compile = ''
        if (addonJar.exists()) {
            compile = "compile(group: 'org.codehaus.griffon.plugins', name: 'griffon-${name}-addon', version: '${version}')"
        }
        if (cliJar.exists()) {
            compile += "\n\t\tcompile(group: 'org.codehaus.griffon.plugins', name: 'griffon-${name}-cli', version: '${version}')"
        }
        String test = ''
        if (testJar.exists()) {
            test = "test(group: 'org.codehaus.griffon.plugins', name: 'griffon-${name}', version: '${version}', classifier: 'test')"
        }

        if (compile || test) {
            File dependencyDescriptor = new File("${pluginDirPath}/plugin-dependencies.groovy")
            dependencyDescriptor.text = """
            |griffon.project.dependency.resolution = {
            |    repositories {
            |        flatDir(name: 'plugin ${name}-${version}', dirs: [
            |            "\${pluginDirPath}/dist",
            |            "\${pluginDirPath}/addon",
            |            "\${pluginDirPath}/lib"
            |        ])
            |    }
            |
            |    dependencies {
            |        ${compile.trim()}
            |        $test
            |    }
            |}""".stripMargin().trim()
        }
    }

    private void resolvePluginJarDependencies(String pluginInstallPath, String pluginName, String pluginVersion) {
        List<File> dependencyDescriptors = [
                new File("$pluginInstallPath/dependencies.groovy"),
                new File("$pluginInstallPath/plugin-dependencies.groovy")
        ]

        if (dependencyDescriptors.any {it.exists()}) {
            eventHandler 'StatusUpdate', "Resolving plugin ${pluginName}-${pluginVersion} JAR dependencies"
            def callable = settings.pluginDependencyHandler()
            int result = callable.call(new File(pluginInstallPath), pluginName, pluginVersion)
            if (result == BuildSettings.RESOLUTION_OK) {
                IvyDependencyManager dependencyManager = settings.dependencyManager
                for (conf in ['compile', 'build', 'test', 'runtime']) {
                    def resolveReport = dependencyManager.resolveDependencies(IvyDependencyManager."${conf.toUpperCase()}_CONFIGURATION")
                    if (resolveReport.hasError()) {
                        throw new InstallArtifactException("Plugin ${pluginName}-${pluginVersion} has missing JAR dependencies.")
                    } else {
                        settings.updateDependenciesFor conf, resolveReport.allArtifactsReports.localFile
                    }
                }
            }
        }
    }

    void uninstall(String type, String name, String version = null) {
        installedArtifacts.clear()
        uninstalledArtifacts.clear()

        try {
            if (!doUninstall(type, name, version)) {
                errorHandler "No ${type} [$name${version ? '-' + version : ''}] installed, cannot uninstall."
            }
        } catch (e) {
            GriffonExceptionHandler.sanitize(e)
            errorHandler "An error occured uninstalling the ${type} [$name${version ? '-' + version : ''}]: ${e.message}"
        }
    }

    private boolean doUninstall(String type, String name, String version = null) {
        String metadataKey = ''
        File artifactDir = null

        if (name && version) {
            artifactDir = getInstallPathFor(type, name, version)
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
            version = metadata.remove(metadataKey)
            metadata.persist()
            if (type == Plugin.TYPE) {
                if (!settings.isPluginProject()) {
                    def uninstallScript = new File("${artifactDir}/scripts/_Uninstall.groovy")
                    runPluginScript(uninstallScript, artifactDir.name, 'uninstall script')
                }
            }
            ant.delete(dir: artifactDir, failonerror: true)
            Map uninstallData = [type: type, name: name, version: version, dir: artifactDir]
            if (!uninstalledArtifacts.contains(uninstallData)) {
                uninstalledArtifacts << uninstallData
            }
            eventHandler "${capitalize(type)}Uninstalled", "Uninstalled ${type} [${name}]."
            return true
        }
        false
    }

    private Release inspectArtifactRelease(String type, File file) {
        Release release = createReleaseFromMetadata(type, file)
        String artifactNameAndVersion = "${release.artifact.name}-${release.version}"

        // check against release.griffonVersion
        if (!isValidVersion(GriffonUtil.griffonVersion, release.griffonVersion)) {
            eventHandler 'StatusError', "${release.artifact.capitalizedType} ${artifactNameAndVersion} could not be installed because it does not meet version requirements. Current version: ${GriffonUtil.griffonVersion}; Expected version ${release.griffonVersion}"
            throw new InstallArtifactException("Installation of ${type} ${artifactNameAndVersion} aborted.")
        }

        if (type == Plugin.TYPE) {
            // check platforms
            List<String> requiredPlatforms = release.artifact.platforms*.lowercaseName
            if (LOG.debugEnabled) {
                LOG.debug("Plugin ${artifactNameAndVersion} requires platforms: ${requiredPlatforms}")
            }
            if (requiredPlatforms) {
                if (!(PlatformUtils.isCompatible(requiredPlatforms*.lowercaseName))) {
                    eventHandler 'StatusError', "Required platforms are [${requiredPlatforms}], current one is ${PlatformUtils.platform}"
                    throw new InstallArtifactException("Installation of ${type} ${artifactNameAndVersion} aborted.")
                }
            }

            // check toolkits
            List<String> requiredToolkits = release.artifact.toolkits*.lowercaseName
            if (LOG.debugEnabled) {
                LOG.debug("Plugin ${artifactNameAndVersion} requires toolkits: ${requiredToolkits}")
            }
            if (requiredToolkits) {
                List<String> supportedToolkits = metadata.getApplicationToolkit() ? [metadata.getApplicationToolkit()] : []
                if (LOG.debugEnabled) {
                    LOG.debug("Supported toolkits: ${supportedToolkits}")
                }
                List<String> unsupportedToolkits = supportedToolkits - requiredToolkits
                // 2nd condition is a special case for plugins that provide toolkit support
                if (!unsupportedToolkits.isEmpty() && unsupportedToolkits != [release.artifact.name]) {
                    eventHandler 'StatusError', "Current application has ${supportedToolkits} as supported UI toolkits.\nThe plugin [${artifactNameAndVersion}] requires any of the following toolkits: ${requiredToolkits}"
                    throw new InstallArtifactException("Installation of ${type} ${artifactNameAndVersion} aborted.")
                }
            }
        }

        release
    }

    private Release createReleaseFromMetadata(String type, File file) {
        try {
            ArtifactUtils.createReleaseFromMetadata(type, file)
        } catch (IllegalArgumentException iae) {
            throw new InstallArtifactException(iae.message)
        }
    }

    private void displayNewScripts(String pluginName, installPath) {
        def providedScripts = new File("${installPath}/scripts").listFiles().findAll { !it.name.startsWith('_') && it.name.endsWith('.groovy')}
        if (providedScripts) {
            String legend = "Plugin ${pluginName} provides the following new scripts:"
            println legend
            println('-' * legend.length())
            providedScripts.each { File file ->
                println "griffon ${getScriptName(file.name)}"
            }
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