/*
* Copyright 2010-2012 the original author or authors.
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

import griffon.util.GriffonExceptionHandler
import griffon.util.GriffonUtil
import griffon.util.Metadata
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.*

import static griffon.util.ArtifactSettings.isValidVersion
import static griffon.util.GriffonNameUtils.capitalize
import static org.codehaus.griffon.cli.CommandLineConstants.KEY_DEFAULT_RELEASE_ARTIFACT_REPOSITORY
import static org.codehaus.griffon.cli.CommandLineConstants.KEY_DEFAULT_ARTIFACT_REPOSITORY
import static org.codehaus.griffon.cli.CommandLineConstants.KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_artifacts_called')) return
_griffon_artifacts_called = true

artifactRepository = null

selectArtifactRepository = {
    artifactRepository = null
    repositoryName = argsMap.repository ?: getPropertyValue(KEY_DEFAULT_RELEASE_ARTIFACT_REPOSITORY, ArtifactRepository.DEFAULT_REMOTE_NAME)
    artifactRepository = ArtifactRepositoryRegistry.instance.findRepository(repositoryName)
    if (artifactRepository == null) {
        event('StatusError', ["Artifact repository ${repositoryName} is not configured."])
        exit 1
    }
    if (griffonSettings.offlineMode && !artifactRepository.local) {
        event('StatusError', ["Repository ${repositoryName} cannot be used while offline mode is enabled."])
        exit 1
    }
}

resolveArtifactRepository = {
    artifactRepository = null
    if (argsMap.repository) {
        artifactRepository = ArtifactRepositoryRegistry.instance.findRepository(argsMap.repository)
        if (griffonSettings.offlineMode && !artifactRepository.local) {
            event('StatusError', ["Repository ${repositoryName} cannot be used while offline mode is enabled."])
            exit 1
        }
    } else {
        artifactRepository = null
    }
}

doWithSelectedRepository = { callback ->
    String defaultInstallRepository = getPropertyValue(KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY, ArtifactRepository.DEFAULT_LOCAL_NAME)
    String defaultSearchRepository = getPropertyValue(KEY_DEFAULT_ARTIFACT_REPOSITORY, ArtifactRepository.DEFAULT_REMOTE_NAME)
    String customRepository = argsMap.repository

    List<String> repositories = []
    if (ArtifactRepositoryRegistry.instance.findRepository(customRepository)) {
        repositories << customRepository
    }
    if (!repositories.contains(defaultInstallRepository)) repositories << defaultInstallRepository
    if (!repositories.contains(defaultSearchRepository)) repositories << defaultSearchRepository

    for (String repositoryName : repositories) {
        artifactRepository = ArtifactRepositoryRegistry.instance.findRepository(repositoryName)
        if (artifactRepository) {
            if (griffonSettings.offlineMode && !artifactRepository.local) return
            if (callback(artifactRepository)) break
        }
    }
}

// --== INSTALL ==-

installArtifact = { String type ->
    try {
        def artifactArgs = argsMap['params']

        if (artifactArgs) {
            if (argsMap['force-upgrade']) {
                System.setProperty(ArtifactDependencyResolver.KEY_FORCE_UPGRADE, 'true')
            }

            File artifactFile = new File(artifactArgs[0])
            def urlPattern = ~"^[a-zA-Z][a-zA-Z0-9\\-\\.\\+]*://"
            if (artifactArgs[0] =~ urlPattern) {
                URL url = new URL(artifactArgs[0])
                doInstallArtifactFromURL(type, url)
            } else if (artifactFile.exists() && artifactFile.name.startsWith("griffon-") && artifactFile.name.endsWith(".zip")) {
                doInstallArtifactFromZip(type, artifactFile)
            } else {
                // The first argument is the artifact name, the second
                // (if provided) is the artifact version.
                failOnError = true
                installArtifactForName(Metadata.current, type, artifactArgs[0], artifactArgs[1])
            }
        } else {
            event('StatusError', [installArtifactErrorMessage(Archetype.TYPE)])
        }
    } catch (Exception e) {
        logError("Error installing ${type}: ${e.message}", e)
        exit(1)
    }
}

uninstallArtifact = { String type ->
    def artifactArgs = argsMap['params']
    if (artifactArgs) {
        String artifactName = artifactArgs[0]
        String artifactVersion = artifactArgs[1]
        doUninstallArtifact type, artifactName, artifactVersion, true
    } else {
        event('StatusError', [uninstallArtifactErrorMessage(Archetype.TYPE)])
    }
}

doUninstallArtifact = { String type, String name, String version = null, boolean failOnError = true ->
    try {
        resolveFrameworkFlag()
        ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine(metadata)
        artifactInstallEngine.uninstall(type, name, version, framework)
    } catch (Exception e) {
        logError("Error uninstalling ${type}: ${e.message}", e)
        if (failOnError) exit(1)
    }
}

installPlugins = { Metadata md, Map<String, String> plugins ->
    resolveFrameworkFlag()
    ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine(md)
    artifactInstallEngine.installPlugins(plugins, framework)
    md.reload()
    resetDependencyResolution()
}

installPluginExternal = { Metadata md, String name, String version = null ->
    failOnError = true
    installArtifactForName(Metadata.current, Plugin.TYPE, name, version)
}

installPluginsLatest = { Metadata md, List<String> plugins ->
    Map<String, String> transformed = plugins.inject([:]) { map, name ->
        map[name] = '<latest>'
        map
    }
    installPlugins(md, transformed)
    resetDependencyResolution()
}

installArtifactErrorMessage = { type ->
    """Usage:
    griffon install-${type} <name> [version]

    griffon install-${type} griffon-<name>-<version>.zip

    griffon install-${type} URL

Options:
    -repository <name> : specify the repository from where the artifact
                         can be downloaded. Default is 'griffon-central'
"""
}

uninstallArtifactErrorMessage = { type ->
    """Usage:
    griffon uninstall-${type} <name> [version]
"""
}

private withArtifactInstall(String type, Closure callable) {
    try {
        return callable.call()
    } catch (e) {
        logError("Error installing ${type}: ${e.message}", GriffonExceptionHandler.sanitize(e))
        exit(1)
    }
}

doInstallArtifactFromURL = { String type, URL url, Metadata md = metadata ->
    return withArtifactInstall(type) {
        File file = RemoteArtifactRepository.downloadFromURL(url)
        doInstallFromFile(type, file, md)
    }
}

doInstallArtifactFromZip = { String type, File file, Metadata md = metadata ->
    return withArtifactInstall(type) {
        doInstallFromFile(type, file, md)
    }
}

doInstallArtifact = { ArtifactRepository artifactRepository, String type, String name, String version = null, Metadata md = metadata ->
    return withArtifactInstall(type) {
        def release = null

        if (!version) {
            Artifact artifact = artifactRepository.findArtifact(type, name)
            if (!artifact) {
                if (!failOnError) return false
                event('StatusError', ["${capitalize(type)} ${name} was not found in repository ${artifactRepository.name}."])
                exit 1
            }
            for (r in artifact.releases) {
                if (isValidVersion(GriffonUtil.getGriffonVersion(), r.griffonVersion)) {
                    version = r.version
                    release = r
                    break
                }
            }
            if (!version) {
                if (!failOnError) return false
                event('StatusError', ["Repository ${artifactRepository.name} does not contain a suitable release for ${type} ${name}."])
                exit 1
            }
        } else {
            Artifact artifact = artifactRepository.findArtifact(type, name, version)
            if (artifact?.releases) release = artifact.releases[0]
        }

        if (!release) return false

        if(type == Plugin.TYPE && framework && !release.artifact.framework) {
            if (!failOnError) return false
            event('StatusError', ["Plugin ${name}-${release.version} cannot be installed as a framework plugin"])
            exit 1
        }

        File file = artifactRepository.downloadFile(type, name, version, null)
        doInstallFromFile(type, file, md)

        ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine(md)
        artifactInstallEngine.updateLocalReleaseMetadata(type, release, framework)
        artifactInstallEngine.publishReleaseToGriffonLocal(release, file, framework)

        return true
    }
}

installArtifactForName = { Metadata md, String type, String name, String version = null ->
    resolveArtifactRepository()
    failOnError = false
    boolean installed = false

    if (artifactRepository) {
        doInstallArtifact(artifactRepository, type, name, version, md)
    } else {
        ArtifactRepositoryRegistry.instance.withRepositories { aname, artifactRepository ->
            if (installed) return
            if (doInstallArtifact(artifactRepository, type, name, version, md)) {
                installed = true
                return
            }
        }
        if (!installed) {
            event('StatusError', ["Failed to install ${type} ${name}${version ? '-' + version : ''} [offline: ${griffonSettings.offlineMode}]"])
            exit 1
        }
    }
}

doInstallFromFile = { type, file, md ->
    resolveFrameworkFlag()
    ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine(md)
    try {
        artifactInstallEngine.installFromFile(type, file, true, framework)
    } catch (InstallArtifactException iae) {
        artifactInstallEngine.errorHandler "Installation of ${file} aborted."
    } catch (UninstallArtifactException uae) {
        artifactInstallEngine.errorHandler "Installation of ${file} aborted."
    }
}

uninstalledPlugins = [:]
createArtifactInstallEngine = { Metadata md = metadata ->
    ArtifactInstallEngine artifactInstallEngine = new ArtifactInstallEngine(griffonSettings, md, ant)
    artifactInstallEngine.pluginScriptRunner = runPluginScript
    artifactInstallEngine.eventHandler = { eventName, msg -> event(eventName, [msg]) }
    artifactInstallEngine.errorHandler = { msg ->
        event('StatusError', [msg])
        // install or uninstalled too
        for (dir in artifactInstallEngine.installedArtifacts) {
            ant.delete(dir: dir, failonerror: false)
        }
        for (plugin in uninstalledPlugins) {
            if (!md[plugin.key]) md[plugin.key] = plugin.value
        }
        md.persist()
        exit(1)
    }

    artifactInstallEngine.interactive = isInteractive
    artifactInstallEngine.variableStore = binding

    artifactInstallEngine
}

runPluginScript = { File scriptFile, fullPluginName, msg ->
    if (scriptFile.exists()) {
        event 'StatusUpdate', ["Executing ${fullPluginName} plugin $msg"]
        // instrumenting plugin scripts adding 'pluginBasedir' variable
        def instrumentedInstallScript = "def pluginBasedir = '${artifactSettings.artifactBase(Plugin.TYPE)}/${fullPluginName}'\n".toString().replaceAll('\\\\', '/') + scriptFile.text
        // we are using text form of script here to prevent Gant caching

        includeTargets << instrumentedInstallScript
    }
}

framework = false
resolveFrameworkFlag = {
    if (argsMap.framework != null) {
        if (argsMap.framework instanceof CharSequence) {
            framework = Boolean.parseBoolean(argsMap.framework)
        } else {
            framework = argsMap.framework as boolean
        }
    } else {
        framework = !griffonSettings.isGriffonProject()
    }
}