/*
* Copyright 2010-2011 the original author or authors.
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
import org.codehaus.griffon.artifacts.model.Release
import static griffon.util.GriffonNameUtils.capitalize
import static griffon.util.GriffonNameUtils.isBlank
import org.codehaus.griffon.artifacts.*
import static org.codehaus.griffon.artifacts.ArtifactUtils.getInstalledReleases
import static org.codehaus.griffon.artifacts.ArtifactUtils.isValidVersion

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_artifacts_called')) return
_griffon_artifacts_called = true

artifactRepository = null

selectArtifactRepository = {
    repositoryName = argsMap.repository ?: ArtifactRepository.DEFAULT_REMOTE_NAME
    artifactRepository = ArtifactRepositoryRegistry.instance.findRepository(repositoryName)
    if (artifactRepository == null) {
        event('StatusError', ["Artifact repository ${repositoryName} is not configured."])
        exit 1
    }
}

resolveArtifactRepository = {
    if (argsMap.repository) {
        artifactRepository = ArtifactRepositoryRegistry.instance.findRepository(argsMap.repository)
    } else {
        artifactRepository = null
    }
}

listArtifacts = { String type ->
    resolveArtifactRepository()

    def artifactLister = { repository ->
        List<Artifact> artifacts = repository.listArtifacts(type)
        if (artifacts) {
            listArtifactsHeader(repository, type)
            artifacts.each { Artifact artifact -> println formatArtifactForPrint(artifact) }
        } else {
            println "No ${type}s found in repository ${repository.name}."
        }
    }

    if (artifactRepository) {
        artifactLister(artifactRepository)
    } else {
        ArtifactRepositoryRegistry.instance.withRepositories {name, repository ->
            artifactLister(repository)
        }
    }

    listInstalledArtifacts(type)
    listArtifactsFooter(type)
}

listArtifactsHeader = { repository, type ->
    println """
${capitalize(type)}s available in the ${repository.name} repository are listed below:
${'-' * 80}
${'Name'.padRight(30, ' ')}${'Releases'.padRight(20, ' ')} Title
"""
}

formatArtifactForPrint = { Artifact artifact ->
    "${artifact.name.padRight(30, ' ')}${artifact.releases.size().toString().padRight(20, ' ')} ${artifact.title}"
}

listArtifactsFooter = { type ->
    println """
To find more info about ${type} type 'griffon ${type}-info [NAME]'

To install type 'griffon install-${type} [NAME] [VERSION]'

For further info visit http://griffon.codehaus.org/${capitalize(type)}s
"""
}

listInstalledArtifacts = { String type ->
    Map installedArtifacts = getInstalledReleases(type)
    if (type == Archetype.TYPE) {
        installedArtifacts['default'] = [
                version: GriffonUtil.getGriffonVersion(),
                title: 'Used when no archetype is specified'
        ]
    }

    if (installedArtifacts) {
        println """
${capitalize(type)}s you currently have installed are listed below:
${'-' * 80}
${'Name'.padRight(30, ' ')}${'Version'.padRight(20, ' ')} Title
"""

        List list = installedArtifacts.collect([]) { entry ->
            "${entry.key.padRight(30, ' ')}${entry.value.version.toString().padRight(20, ' ')} ${entry.value.title}"
        }
        list.sort()
        list.each { println it }
    } else {
        println "You do not have any ${type}s installed."
    }
}

displayArtifact = { String type, String name, String version, ArtifactRepository repository ->
    displayArtifactHeader(type, repository)
    displayArtifactInfo(type, name, version, repository)
    displayArtifactFooter(type)
}

displayArtifactHeader = { String type, ArtifactRepository repository ->
    println """
${'-' * 80}
Information about ${type} listed at ${repository.name}
${'-' * 80}\
"""
}

displayArtifactInfo = { String type, String name, String version, ArtifactRepository repository ->
    Artifact artifact = repository.findArtifact(type, name)
    if (artifact == null) {
        event('StatusError', ["${capitalize(type)} with name '${name}' was not found in repository ${repository.name}"])
        exit 1
    }

    Release release = artifact.releases.find { it.version == version }

    int padding = type == Archetype.TYPE ? 8i : 9i

    [
            'Name': artifact.name,
            'Title': artifact.title,
            'License': artifact.license,
            'Source': artifact.source ?: 'No source link provided'
    ].each { label, value ->
        println "${label.padRight(padding, ' ')}: ${value}"
    }
    println('-' * 80)

    if (type == Plugin.TYPE) {
        [
                'Toolkits': artifact.toolkits*.getLowercaseName().join(', ') ?: 'works with all toolkits',
                'Platforms': artifact.platforms*.getLowercaseName().join(', ') ?: 'works in all platforms',
        ].each { label, value ->
            println "${label.padRight(padding, ' ')}: ${value}"
        }
        println('-' * 80)
    }

    println 'Authors:'
    artifact.authors.each { author ->
        println "\t${author.name} (${author.email})"
    }
    println('-' * 80)

    if (version) {
        if (release) {
            [
                    'Version': release.version,
                    'GriffonVersion': release.griffonVersion,
                    'Date': release.date,
            ].each { label, value ->
                println "${label.padRight(15, ' ')}: ${value}"
            }
            if (release.dependencies) {
                println 'Dependencies:'
                release.dependencies.each { depname, depversion ->
                    println "\t${depname}-${depversion}"
                }
            }
        } else {
            println "<release ${version} not found for this ${type}>"
            println('-' * 80)
        }
    } else if (artifact.releases) {
        println 'Releases:'
        println "${'Version'.padRight(20, ' ')}${'Griffon Version'.padRight(25, ' ')}Date"
        artifact.releases.each { r ->
            println "${r.version.padRight(20, ' ')}${r.griffonVersion.padRight(25, ' ')}${r.date}"
        }
    } else {
        println "No releases found for this ${type}"
        println('-' * 80)
    }
}

displayArtifactFooter = { type ->
    println """
To get info about specific release of ${type} 'griffon ${type}-info [NAME] [VERSION]'

To get list of all ${type}s type 'griffon list-${type}s'

To install latest version of ${type} type 'griffon install-${type} [NAME]'

To install specific version of ${type} type 'griffon install-${type} [NAME] [VERSION]'

For further info visit http://griffon.codehaus.org/${capitalize(type)}s
"""
}

// --== INSTALL ==-

installArtifact = { String type ->
    try {
        def artifactArgs = argsMap['params']

        if (artifactArgs) {
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
    }
    catch (Exception e) {
        logError("Error installing ${type}: ${e.message}", e)
        exit(1)
    }
}

uninstallArtifact = { String type ->
    try {
        def artifactArgs = argsMap['params']
        if (artifactArgs) {
            String artifactName = artifactArgs[0]
            String artifactVersion = artifactArgs[1]

            ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine(metadata)
            artifactInstallEngine.uninstall(type, artifactName, artifactVersion)
        } else {
            event('StatusError', [uninstallArtifactErrorMessage(Archetype.TYPE)])
        }
    }
    catch (Exception e) {
        logError("Error installing ${type}: ${e.message}", e)
        exit(1)
    }
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
        if (!version) {
            Artifact artifact = artifactRepository.findArtifact(type, name)
            if (!artifact) {
                if (!failOnError) return false
                event('StatusError', ["${capitalize(type)} ${name} was not found in repository ${artifactRepository.name}."])
                exit 1
            }
            for (release in artifact.releases) {
                if (isValidVersion(GriffonUtil.getGriffonVersion(), release.griffonVersion)) {
                    version = release.version
                    break
                }
            }
            if (!version) {
                if (!failOnError) return false
                event('StatusError', ["Repository ${artifactRepository.name} does not contain a suitable release for ${type} ${name}."])
                exit 1
            }
        }
        File file = artifactRepository.downloadFile(type, name, version, null)
        doInstallFromFile(type, file, md)
        return true
    }
}

installArtifactForName = { Metadata md, String type, String name, String version = null ->
    resolveArtifactRepository()
    failOnError = false
    installed = false

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
            event('StatusError', ["Failed to install ${type} ${name}${version ? '-' + version : ''}"])
            exit 1
        }
    }
}

doInstallFromFile = { type, file, md ->
    ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine(md)
    try {
        artifactInstallEngine.installFromFile(type, file, true)
        md.reload()
    } catch (InstallArtifactException iae) {
        artifactInstallEngine.errorHandler "Installation of ${file} aborted."
    } catch (UninstallArtifactException uae) {
        artifactInstallEngine.errorHandler "Installation of ${file} aborted."
    }
}

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
        exit(1)
    }

    artifactInstallEngine.interactive = isInteractive
    artifactInstallEngine.variableStore = binding

    artifactInstallEngine
}

// --== RELEASE ==--

setupCredentials = {
    username = ''
    password = ''
    if (artifactRepository.type != ArtifactRepository.REMOTE) return

    username = resolveCredential('username')
    password = resolveCredential('password')
}

resolveCredential = { String key ->
    String value = artifactRepository[key]

    if (isBlank(value)) {
        value = argsMap[key]
    }

    if (isBlank(value)) {
        String prop = "credential.${key}".toString()
        ant.input(message: "Please enter your ${key}:", addproperty: prop) {
            if (key == 'password') handler(type: 'secure')
        }
        value = ant.antProject.getProperty(prop)
    }

    if (isBlank(value)) {
        event('StatusError', ["You must provide a value for your ${key} when releasing artifacts to ${repository.name}."])
        exit(1)
    }

    value
}

resolveCommitMessage = {
    commitMessage = argsMap.message

    if (isBlank(commitMessage)) {
        ant.input(message: 'Enter a commit message: ', addproperty: 'commit.message')
        commitMessage = ant.antProject.properties.'commit.message'
    }

    if (isBlank(commitMessage)) {
        event('StatusError', ["You must provide a commit message when releasing artifacts."])
        exit(1)
    }

    commitMessage
}

// --== LIST ARTIFACTS ==--

doListArtifactUpdates = { String type ->
    Map availableArtifacts = getAvailableArtifacts(type)
    Map installedArtifacts = getInstalledReleases(type)
    Map outdatedArtifacts = [:]

    if (!availableArtifacts) {
        println "\nNo ${type} updates available in configured repositories."
    }

    boolean headerDisplayed = false
    if (installedArtifacts) {
        installedArtifacts.each {name, release ->
            String version = release.version
            String availableVersion = availableArtifacts[name].version
            if (availableVersion != version && availableVersion != null) {
                if (!headerDisplayed) {
                    println """
${capitalize(type)}s with available updates are listed below:
${'-' * 80}
<${capitalize(type)}>                   <Current>         <Available>"""
                    headerDisplayed = true
                }
                println "${name.padRight(27 + (type == Archetype.TYPE ? 3 : 0), " ")}${version.padRight(20, " ")}  ${availableVersion}"
                outdatedArtifacts[name] = [
                        version: availableVersion,
                        repository: availableArtifacts[name].repository
                ]
            }
        }
        if (!headerDisplayed) {
            println "\nAll ${type}s are up to date."
        }
        if (argsMap.install && outdatedArtifacts) {
            println ''
            if (confirmInput("Proceed with ${type} upgrades?", "artifact.upgrade")) {
                wasInteractive = isInteractive
                isInteractive = false
                try {
                    outdatedArtifacts.each { name, data ->
                        // skip if name-version has been installed already because
                        // it is a dependency of another artifact that was upgraded in  a previous
                        // iteration
                        if (Metadata.current["${type}${type == Plugin.TYPE ? 's' : ''}" + name] == data.version) return
                        doInstallArtifact(data.repository, type, name, data.version, Metadata.current)
                    }
                } finally {
                    isInteractive = wasInteractive
                }
            }
        }
    } else {
        println "\nYou do not have any ${type}s installed."
    }
}

getAvailableArtifacts = { String type ->
    Map artifacts = [:]

    def finder = { repository ->
        repository.listArtifacts(type).each { Artifact artifact ->
            for (release in artifact.releases) {
                if (isValidVersion(GriffonUtil.getGriffonVersion(), release.griffonVersion)) {
                    artifacts[artifact.name] = [
                            version: release.version,
                            title: artifact.title,
                            repository: repository
                    ]
                    break
                }
            }
        }
    }

    resolveArtifactRepository()

    if (artifactRepository) {
        finder(artifactRepository)
    } else {
        ArtifactRepositoryRegistry.instance.withRepositories {String name, ArtifactRepository artifactRepository ->
            finder(artifactRepository)
        }
    }

    artifacts
}

runPluginScript = { File scriptFile, fullPluginName, msg ->
    if (scriptFile.exists()) {
        event 'StatusUpdate', ["Executing ${fullPluginName} plugin $msg"]
        // instrumenting plugin scripts adding 'pluginBasedir' variable
        def instrumentedInstallScript = "def pluginBasedir = '${pluginsHome}/${fullPluginName}'\n".toString().replaceAll('\\\\', '/') + scriptFile.text
        // we are using text form of script here to prevent Gant caching

        // temporary crutch --- REMOVE BEFORE 1.0!!
        builderConfig = new ConfigObject()
        if (builderConfigFile.exists()) builderConfig = configSlurper.parse(builderConfigFile.text)
        // temporary crutch --- REMOVE BEFORE 1.0!!

        includeTargets << instrumentedInstallScript
    }
}
