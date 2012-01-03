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

    /**
     * Finds all artifacts of the given type that are installed.
     *
     * @param type one of <tt>Archetype.TYPE</tt> or <tt>Plugin.TYPE</tt>.
     * @return
     */
    Map getInstalledArtifacts(String type) {
        Map artifacts = [:]

        for (resource in resolveResources("file://${artifactBase(type)}/*/${type}.json")) {
            Release release = Release.makeFromJSON(type, new JsonSlurper().parseText(resource.file.text))
            artifacts[release.artifact.name] = [
                    type: type,
                    version: release.version,
                    title: release.artifact.title,
                    dependencies: release.dependencies.collect([]) {it.name}
            ]
        }

        // TODO - remove this code before 1.0
        // legacy plugins
        if (type == Plugin.TYPE) {
            for (resource in resolveResources("file://${artifactBase(type)}/*/plugin.xml")) {
                def xml = new XmlSlurper().parse(resource.file)
                String name = xml.@name.text()
                if (artifacts[name]) continue
                artifacts[name] = [
                        type: type,
                        version: xml.@version.text(),
                        title: xml.title.text(),
                        dependencies: xml.dependencies?.plugin?.collect([]) {it.@name.text()}
                ]
            }
        }

        artifacts
    }

    /**
     * Finds all artifacts of the given type that are registered with the project's metadata.
     *
     * @param type one of <tt>Archetype.TYPE</tt> or <tt>Plugin.TYPE</tt>.
     * @return
     */
    Map<String, String> getRegisteredArtifacts(String type) {
        Map artifacts = [:]

        switch (type) {
            case Archetype.TYPE:
                String property = metadata.propertyNames().find {it.startsWith('archetype.')}
                if (property) {
                    String name = property - 'archetype.'
                    String version = metadata[property]
                    artifacts[name] = version
                }
                break
            case Plugin.TYPE:
                metadata.propertyNames().grep {it.startsWith('plugins.')}.each { property ->
                    String name = property - 'plugins.'
                    String version = metadata[property]
                    artifacts[name] = version
                }
                break
        }

        artifacts
    }

    void installPlugin(String name, String version = null) {
        String type = Plugin.TYPE
        installedArtifacts.clear()
        uninstalledArtifacts.clear()

        Map installedArtifacts = getInstalledArtifacts(type)

        Map<String, ArtifactDependency> installedDependencies = [:]
        installedArtifacts.each { String key, installed ->
            ArtifactDependency dep = installedDependencies[key]
            if (!dep) {
                dep = new ArtifactDependency(key)
                dep.version = installed.version
                dep.installed = true
                dep.resolved = true
                installedDependencies[key] = dep
            }
        }

        installedArtifacts.each { key, installed ->
            ArtifactDependency dependency = installedDependencies[key]
            installed.dependencies.each { k ->
                ArtifactDependency dep = installedDependencies[k]
                if (dep) dependency.dependencies << dep
            }
        }

        if (LOG.debugEnabled) {
            LOG.debug('Installed dependencies:')
            installedDependencies.values().each {LOG.debug it.toString().trim()}
        }

        ArtifactDependency artifactDependency = null
        try {
            artifactDependency = resolveDependencyTree(type, name, version)
        } catch (Exception e) {
            GriffonExceptionHandler.sanitize(e)
            eventHandler 'StatusError', "${capitalize(type)} ${name}${version ? '-' + version : ''} could not be installed => $e"
            errorHandler "Installation of ${type} ${name}${version ? '-' + version : ''} aborted."
        }

        if (LOG.debugEnabled) {
            LOG.debug('Dependency resolution outcome:')
            LOG.debug printDependencyTree(artifactDependency)
        }

        if (!artifactDependency.resolved) {
            String installed = ''
            installedDependencies.values().each {installed += it.toString() }
            String target = printDependencyTree(artifactDependency)
            eventHandler 'StatusError', "${capitalize(type)} ${name}${version ? '-' + version : ''} could not be installed because some of its dependencies could not be resolved.\n${installed}${target}"
            errorHandler "Installation of ${type} ${name}${version ? '-' + version : ''} aborted."
        }

        List<ArtifactDependency> installPlan = resolveEvictions(installedDependencies.values(), artifactDependency)
        if (LOG.debugEnabled) {
            LOG.debug('Dependency evictions & conflicts outcome:')
            installedDependencies.values().each {LOG.debug it.toString().trim()}
            LOG.debug printDependencyTree(artifactDependency)
        }

        if (artifactDependency.conflicted) {
            String installed = ''
            installedDependencies.values().each {installed += it.toString() }
            String target = printDependencyTree(artifactDependency)
            eventHandler 'StatusError', "${capitalize(type)} ${name}${version ? '-' + version : ''} could not be installed because some of its dependencies have conflicts.\n${installed}${target}"
            errorHandler "Installation of ${type} ${name}${version ? '-' + version : ''} aborted."
        }

        if (LOG.debugEnabled) {
            LOG.debug('Dependency install plan:')
            installPlan.each {LOG.debug it.toString().trim()}
        }

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
                        eventHandler 'StatusError', "${capitalize(type)} ${name}${version ? '-' + version : ''} could not be installed => $e"
                        errorHandler "Installation of ${type} ${name}${version ? '-' + version : ''} aborted."
                }
            }
        }

        if (failedDependencies) {
            String failed = ''
            failedDependencies.each {failed += it.toString() }
            eventHandler 'StatusFinal', "The following ${type}s failed to be installed due to missing dependencies or a postinstall error.\n${failed}"
        }
    }

    private String printDependencyTree(ArtifactDependency artifactDependency) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream out = new PrintStream(baos)
        artifactDependency.printout(0i, out, true)
        baos.toString()
    }

    void installFromFile(String type, File file) {
        Release release = inspectArtifactRelease(type, file)

        String artifactName = release.artifact.name
        String releaseName = "${artifactName}-${release.version}"
        String artifactInstallPath = "${artifactBase(type)}/${releaseName}"

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
                resolvePluginJarDependencies(releaseName, artifactInstallPath)
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

        eventHandler 'StatusFinal', "Installed ${type} '${releaseName}' in ${artifactInstallPath}"
    }

    private void resolvePluginJarDependencies(String pluginName, String pluginInstallPath) {
        File pluginDependencyDescriptor = new File("$pluginInstallPath/dependencies.groovy")
        if (pluginDependencyDescriptor.exists()) {
            eventHandler 'StatusUpdate', 'Resolving plugin JAR dependencies'
            def callable = settings.pluginDependencyHandler()
            callable.call(new File(pluginInstallPath))
            IvyDependencyManager dependencyManager = settings.dependencyManager
            dependencyManager.resetGriffonPluginsResolver()
            def resolveReport = dependencyManager.resolveDependencies(IvyDependencyManager.RUNTIME_CONFIGURATION)
            if (resolveReport.hasError()) {
                throw new InstallArtifactException("Plugin ${pluginName} has missing JAR dependencies.")
            } else {
                addJarsToRootLoader resolveReport.allArtifactsReports.localFile
            }
        }
        List pluginJars = new File("${pluginInstallPath}/lib").listFiles().findAll {it.name.endsWith('.jar')}
        addJarsToRootLoader(pluginJars)
    }

    protected def addJarsToRootLoader(Collection pluginJars) {
        ClassLoader loader = getClass().classLoader.rootLoader
        for (File jar: pluginJars) {
            loader.addURL(jar.toURI().toURL())
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
            if (requiredPlatforms) {
                if (!(PlatformUtils.isCompatible(requiredPlatforms*.lowercaseName))) {
                    eventHandler 'StatusError', "Required platforms are [${requiredPlatforms}], current one is ${PlatformUtils.platform}"
                    throw new InstallArtifactException("Installation of ${type} ${artifactNameAndVersion} aborted.")
                }
            }

            // check toolkits
            List<String> requiredToolkits = release.artifact.toolkits*.lowercaseName
            if (requiredToolkits) {
                List<String> supportedToolkits = metadata.getApplicationToolkits().toList()
                List<String> unsupportedToolkits = supportedToolkits - requiredToolkits
                if (unsupportedToolkits) {
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
            println('_' * legend.length())
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

    private ArtifactDependency resolveDependencyTree(String type, String name, String version = null) {
        ArtifactDependency artifactDependency = new ArtifactDependency(name)
        artifactDependency.version = version

        Artifact artifact = null
        ArtifactRepositoryRegistry.instance.withRepositories {String repoName, ArtifactRepository repository ->
            if (artifact) return
            artifact = repository.findArtifact(type, name)
            if (artifact) {
                artifactDependency.repository = repository
            }
        }

        if (version) {
            Release release = artifact.releases.find {it.version == version}
            if (release) {
                artifactDependency.release = release
                return resolveDependenciesOf(artifactDependency)
            }
        } else {
            for (release in artifact.releases) {
                if (isValidVersion(GriffonUtil.griffonVersion, release.griffonVersion)) {
                    artifactDependency.release = release
                    artifactDependency.version = release.version
                    return resolveDependenciesOf(artifactDependency)
                }
            }
        }
        artifactDependency
    }

    private ArtifactDependency resolveDependenciesOf(ArtifactDependency artifactDependency) {
        boolean resolutionTrouble = false
        for (dependency in artifactDependency.release.dependencies) {
            // Watch out, only plugins can be resolved as dependencies
            ArtifactDependency dep = resolveDependencyTree(Plugin.TYPE, dependency.name, dependency.version)
            artifactDependency.dependencies << dep
            if (!dep.resolved) {
                artifactDependency.resolved = false
                resolutionTrouble = true
            }
        }

        artifactDependency.resolved = !resolutionTrouble
        artifactDependency
    }

    private List<ArtifactDependency> resolveEvictions(Collection<ArtifactDependency> installed, ArtifactDependency target) {

        List evictions = []
        fillEvictions(target, evictions)
        installed.each { dep ->
            evictions.grep {it.dependency.name == dep.name && it.dependency.version == dep.version}.each {
                it.dependency.installed = true
            }
        }
        installed.collect(evictions) { dep ->
            [
                    name: dep.name,
                    version: dep.version,
                    dependency: dep,
                    processed: false
            ]
        }

        for (element in evictions) {
            if (element.processed) continue
            element.processed = true
            List<Map> matches = evictions.findAll {it.name == element.name}
            if (matches.size() > 1) {
                matches.sort {a, b -> b.dependency.major <=> a.dependency.major}
                def winner = matches[0]
                if (matches.find {it.dependency.major != element.dependency.major}) {
                    matches.each {it.dependency.conflicted = it.processed = true}
                    continue
                }
                matches.sort {a, b -> b.dependency.minor <=> a.dependency.minor}
                winner = matches[0]
                matches.grep {it.dependency.minor < winner.dependency.minor}.each {it.dependency.evicted = true}
                matches.sort {a, b -> b.dependency.revision <=> a.dependency.revision}
                winner = matches[0]
                matches.grep {it.dependency.revision != winner.dependency.revision}.each {it.dependency.evicted = true}
                matches.each {it.processed = true}

                matches = evictions.findAll {it.name == element.name && it.version == element.version}
                if (matches.size() > 1) {
                    List alreadyInstalled = matches.findAll {it.dependency.installed}
                    if (matches.size() == alreadyInstalled.size()) continue
                    matches[0].dependency.evicted = false
                    matches[1..-1].each {it.dependency.evicted = true}
                }
            }
        }

        target.updateConflicts()

        List<ArtifactDependency> installPlan = []
        for (dependency in installed) {
            // first mark installed & evicted dependencies to uninstall
            if (dependency.evicted) installPlan << dependency
        }
        processEvictionsAndConflicts(target, installPlan)
        installPlan
    }

    private void processEvictionsAndConflicts(ArtifactDependency artifactDependency, List<ArtifactDependency> list) {
        for (dependency in artifactDependency.dependencies) {
            processEvictionsAndConflicts(dependency, list)
        }
        if (!artifactDependency.evicted && !artifactDependency.conflicted && !artifactDependency.installed) {
            list << artifactDependency
        }
    }

    private void fillEvictions(ArtifactDependency artifactDependency, List<Map> evictions) {
        evictions << [
                name: artifactDependency.name,
                version: artifactDependency.version,
                dependency: artifactDependency,
                processed: false
        ]
        for (dependency in artifactDependency.dependencies) {
            fillEvictions(dependency, evictions)
        }
    }
}