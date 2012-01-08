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
import org.codehaus.griffon.resolve.IvyDependencyManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import griffon.util.*
import static griffon.util.GriffonNameUtils.*
import static griffon.util.GriffonUtil.getScriptName
import static org.codehaus.griffon.artifacts.ArtifactUtils.*

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class ArtifactInstallEngine {
    private static final Logger LOG = LoggerFactory.getLogger(ArtifactInstallEngine)
    private static final String INSTALL_FAILURE_KEY = 'griffon.install.failure'
    private static final String INSTALL_FAILURE_ABORT = 'abort'
    private static final String INSTALL_FAILURE_CONTINUE = 'continue'

    private final BuildSettings settings
    private final Metadata metadata
    private final AntBuilder ant
    private CommandLineHelper commandLineHelper = new CommandLineHelper(System.out)

    final List installedArtifacts = []
    final List uninstalledArtifacts = []
    def variableStore = [:]

    Closure errorHandler = { String msg -> throw new ScriptExitException(msg) }
    Closure eventHandler = { String name, String msg -> println msg }
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

        if (LOG.debugEnabled) {
            String registered = registeredPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (registered) LOG.debug("Registered plugins:\n\t${registered}")
            String installed = installedPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (installed) LOG.debug("Installed plugins:\n\t${installed}")
        }

        Map<String, String> pluginsToDelete = [:]
        Map<String, String> missingPlugins = [:]
        registeredPlugins.each {name, version ->
            String v = installedPlugins[name]
            if (v != version) {
                missingPlugins[name] = version
            }
        }

        installedPlugins.each {name, version ->
            String v = registeredPlugins[name]
            if (v != version) {
                pluginsToDelete[name] = version
            }
        }

        if (LOG.debugEnabled) {
            String installed = installedPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (installed) LOG.debug("Installed plugins (confirmed):\n\t${installed}")
            String missing = missingPlugins.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (missing) LOG.debug("Missing plugins:\n\t${missing}")
            String todelete = pluginsToDelete.collect([]) {k, v -> "${k}-${v}"}.join('\n\t')
            if (todelete) LOG.debug("Plugins to be deleted:\n\t${todelete}")
        }

        pluginsToDelete.each {name, version ->
            eventHandler 'StatusUpdate', "Plugin ${name}-${version} is installed, but was not found in the application's metadata. Removing this plugin from the application's plugin base."
            ant.delete(dir: getInstallPathFor(Plugin.TYPE, name, version), failonerror: false)
            installedPlugins.remove(name)
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
            return installPlugins(dependencies, resolver)
        } catch (InstallArtifactException iae) {
            errorHandler "Could not resolve plugin dependencies."
        }
    }

    boolean installPlugin(String name, String version = null) {
        String type = Plugin.TYPE

        ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
        List<ArtifactDependency> dependencies = resolveDependenciesFor(resolver, type, name, version)

        try {
            return installPlugins(dependencies, resolver)
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
            return installPlugins(dependencies, resolver)
        } catch (InstallArtifactException iae) {
            errorHandler "Could not resolve plugin dependencies."
        }
    }

    private boolean installPlugins(List<ArtifactDependency> dependencies, ArtifactDependencyResolver resolver) {
        installedArtifacts.clear()
        uninstalledArtifacts.clear()

        List<ArtifactDependency> installPlan = resolveDependenciesFor(dependencies, resolver)
        installPluginsInternal(installPlan)
    }

    private boolean installPluginsInternal(List<ArtifactDependency> installPlan) {
        String type = Plugin.TYPE
        List<ArtifactDependency> failedDependencies = []
        for (ArtifactDependency dependency: installPlan) {
            try {
                if (dependency.evicted) {
                    doUninstall(type, dependency.name, dependency.version)
                } else {
                    File file = dependency.repository.downloadFile(type, dependency.name, dependency.version, null)
                    installFromFile(type, file)
                }
            } catch (Exception e) {
                failedDependencies << dependency
                GriffonExceptionHandler.sanitize(e)
                eventHandler 'StatusError', "${dependency.toString().trim()} [FAILED]"
                eventHandler 'StatusError', e.message
                switch (getInstallFailureStrategy()) {
                    case INSTALL_FAILURE_CONTINUE:
                        // try next dependency
                        break
                    case INSTALL_FAILURE_ABORT:
                        eventHandler 'StatusError', "Plugin ${dependency.name}-${dependency.version} could not be installed => $e"
                        throw new InstallArtifactException("Installation of ${dependency.name}-${dependency.version} aborted.")
                }
            }
        }

        if (failedDependencies) {
            String failed = ''
            failedDependencies.each {failed += it.toString() }
            eventHandler 'StatusFinal', "The following plugins failed to be installed due to missing dependencies or a postinstall error.\n${failed}"
            return false
        }

        true
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
            if (!dep) {
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
        String releaseName = "${artifactName}-${release.version}"
        String artifactInstallPath = "${artifactBase(type)}/${releaseName}"

        if (resolveDependencies && type == Plugin.TYPE) {
            ArtifactDependencyResolver resolver = new ArtifactDependencyResolver()
            List<ArtifactDependency> dependencies = resolveDependenciesFor(resolver, type, artifactName, releaseVersion)
            // this is the file we already got
            dependencies.findAll {it.name == artifactName && it.version == releaseVersion}.each {dependency ->
                dependency.installed = true
                dependency.resolved = true
            }
            List<ArtifactDependency> installPlan = resolveDependenciesFor(dependencies, resolver)
            // if we've reached this stage then we can proceed with installs
            if (!installPluginsInternal(installPlan)) {
                throw new InstallArtifactException("Could not install plugin ${releaseName}")
            }
        }

        if (new File(artifactInstallPath).exists()) {
            if (!commandLineHelper.confirmInput("${capitalize(type)} '${releaseName}' is already installed. Overwrite?")) {
                return
            }
        }

        eventHandler 'StatusUpdate', "${release.artifact.capitalizedType} license for ${releaseName} is '${release.artifact.license}'"

        for (dir in findAllArtifactDirsForName(type, release.artifact.name)) {
            ant.delete(dir: dir, failonerror: false)
        }
        ant.mkdir(dir: artifactInstallPath)
        ant.unzip(dest: artifactInstallPath, src: file)

        switch (type) {
            case Plugin.TYPE:
                variableStore["${getPropertyNameForLowerCaseHyphenSeparatedName(artifactName)}PluginDir"] = new File(artifactInstallPath).absoluteFile
                variableStore["${getPropertyNameForLowerCaseHyphenSeparatedName(artifactName)}PluginVersion"] = release.version
                // TODO LEGACY - remove before 1.0
                if (new File(artifactInstallPath, 'plugin.xml').exists()) {
                    generateDependencyDescriptorFor(artifactInstallPath, artifactName, releaseVersion)
                }
                resolvePluginJarDependencies(artifactInstallPath, artifactName, releaseVersion)
                if (!settings.isPluginProject()) {
                    def installScript = new File("${artifactInstallPath}/scripts/_Install.groovy")
                    runPluginScript(installScript, releaseName, 'post-install script')
                }
                displayNewScripts(releaseName, artifactInstallPath)
                break
            case Archetype.TYPE:
                break
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
            compile = "compile(group: '${name}', name: 'griffon-${name}-addon', version: '${version}')"
        }
        if (cliJar.exists()) {
            compile += "\n\t\tcompile(group: '${name}', name: 'griffon-${name}-cli', version: '${version}')"
        }
        String test = ''
        if (testJar.exists()) {
            test = "test(group: '${name}', name: 'griffon-${name}', version: ${version}', classifier: 'test')"
        }

        File dependencyDescriptor = new File("${pluginDirPath}/plugin-dependencies.groovy")
        dependencyDescriptor.text = """
        |griffon.project.dependency.resolution = {
        |    repositories {
        |        flatDir(name: 'plugin ${name}-${version}', dirs: [
        |            '${pluginDirPath}/dist',
        |            '${pluginDirPath}/addon'
        |        ])
        |    }
        |
        |    dependencies {
        |        ${compile.trim()}
        |        $test
        |    }
        |}""".stripMargin().trim()
    }

    private void resolvePluginJarDependencies(String pluginInstallPath, String pluginName, String pluginVersion) {
        List<File> dependencyDescriptors = [
                new File("$pluginInstallPath/dependencies.groovy"),
                new File("$pluginInstallPath/plugin-dependencies.groovy")
        ]

        if (dependencyDescriptors.any {it.exists()}) {
            eventHandler 'StatusUpdate', 'Resolving plugin JAR dependencies'
            def callable = settings.pluginDependencyHandler()
            callable.call(new File(pluginInstallPath), pluginName, pluginVersion)
            IvyDependencyManager dependencyManager = settings.dependencyManager
            for (conf in ['compile', 'build', 'test', 'runtime']) {
                def resolveReport = dependencyManager.resolveDependencies(IvyDependencyManager."${conf.toUpperCase()}_CONFIGURATION")
                if (resolveReport.hasError()) {
                    throw new InstallArtifactException("Plugin ${pluginName}-${pluginVersion} has missing JAR dependencies.")
                } else {
                    settings.addJarsToRootLoader resolveReport.allArtifactsReports.localFile
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
            true
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

            metadata.propertyNames().each { property ->
                println "${property} ${metadata[property]}"
            }

            // check toolkits
            List<String> requiredToolkits = release.artifact.toolkits*.lowercaseName
            if (LOG.debugEnabled) {
                LOG.debug("Plugin ${artifactNameAndVersion} requires toolkits: ${requiredToolkits}")
            }
            if (requiredToolkits) {
                List<String> supportedToolkits = metadata.getApplicationToolkits().toList()
                if (LOG.debugEnabled) {
                    LOG.debug("Supported toolkits: ${supportedToolkits}")
                }
                List<String> unsupportedToolkits = supportedToolkits - requiredToolkits
                // 2nd condition is a special case for plugins that provide toolkit support
                if (unsupportedToolkits && unsupportedToolkits != [release.artifact.name]) {
                    eventHandler 'StatusError', "Current application has ${supportedToolkits} as supported UI toolkits.\nThe plugin [${artifactNameAndVersion}] requires any of the following toolkits: ${requiredToolkits}"
                    throw new InstallArtifactException("Installation of ${type} ${artifactNameAndVersion} aborted.")
                }
            }
        }

        release
    }

    private Release createReleaseFromMetadata(String type, File file) {
        ZipFile zipFile = new ZipFile(file.absolutePath)
        ZipEntry artifactEntry = zipFile.getEntry(type + '.json')
        if (artifactEntry == null) {
            throw new InstallArtifactException("Not a valid griffon artifact of type $type: missing ${type}.json")
        }

        try {
            def json = new JsonSlurper().parseText(zipFile.getInputStream(artifactEntry).text)
            Release.makeFromJSON(type, json)
        } catch (JsonException e) {
            throw new InstallArtifactException("Can't parse ${type}.json", e)
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

    /*
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
    */

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