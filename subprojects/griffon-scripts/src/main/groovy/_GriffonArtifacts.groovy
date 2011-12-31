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
import groovy.json.JsonSlurper
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import static griffon.util.GriffonNameUtils.capitalize
import static griffon.util.GriffonNameUtils.isBlank
import org.codehaus.griffon.artifacts.*
import static org.codehaus.griffon.artifacts.ArtifactUtils.artifactBase
import static org.codehaus.griffon.artifacts.ArtifactUtils.isValidVersion

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_artifacts_called')) return
_griffon_artifacts_called = true

includeTargets << griffonScript('Init')

target(configureArtifactRepositories: 'Configures available artifact repositories') {
    ArtifactRepositoryRegistry.instance.configureRepositories()
}

selectArtifactRepository = {
    repositoryName = argsMap.repository ?: ArtifactRepository.DEFAULT
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

listArtifacts = { String type, ArtifactRepository repository ->
    listArtifactsHeader(repository, type)

    List<Artifact> artifacts = repository.listArtifacts(type)
    if (artifacts) {
        artifacts.each { Artifact artifact -> println formatArtifactForPrint(artifact) }
    } else {
        println "No ${type}s found in repository: ${repository.name}."
    }

    listInstalledArtifacts(type)
    listArtifactsFooter(type)
}

listArtifactsHeader = { repository, type ->
    println """
${capitalize(type)}s available in the ${repository.name} repository are listed below:
-----------------------------------------------------------------------
${'Name'.padRight(25, ' ')}${'Releases'.padRight(16, ' ')} Title
"""
}

formatArtifactForPrint = { Artifact artifact ->
    "${artifact.name.padRight(25, ' ')}${artifact.releases.size().toString().padRight(16, ' ')} ${artifact.title}"
}

listArtifactsFooter = { type ->
    println """
To find more info about ${type} type 'griffon ${type}-info [NAME]'

To install type 'griffon install-${type} [NAME] [VERSION]'

For further info visit http://griffon.codehaus.org/${capitalize(type)}s
"""
}

listInstalledArtifacts = { String type ->
    Map installedArtifacts = getInstalledArtifacts(type)
    if (type == Archetype.TYPE) {
        installedArtifacts['default'] = [
                version: GriffonUtil.getGriffonVersion(),
                title: 'Used when no archetype is specified'
        ]
    }

    if (installedArtifacts) {
        println """
${capitalize(type)}s you currently have installed are listed below:
-----------------------------------------------------------------------
${'Name'.padRight(25, ' ')}${'Version'.padRight(16, ' ')} Title
"""

        List list = installedArtifacts.collect([]) { entry ->
            "${entry.key.padRight(25, ' ')}${entry.value.version.toString().padRight(16, ' ')} ${entry.value.title}"
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
--------------------------------------------------------------------------
Information about ${type} listed at ${repository.name}
--------------------------------------------------------------------------\
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
    println '--------------------------------------------------------------------------'

    if (type == Plugin.TYPE) {
        [
                'Toolkits': artifact.toolkits*.getLowercaseName().join(', ') ?: 'This plugin works with all toolkits.',
                'Platforms': artifact.platforms*.getLowercaseName().join(', ') ?: 'This plugin works in all platforms.',
        ].each { label, value ->
            println "${label.padRight(padding, ' ')}: ${value}"
        }
        if (artifact.dependencies) {
            println 'Dependencies:'
            artifact.dependencies.each { depname, depversion ->
                println "\t${depname}-${depversion}"
            }
        }
        println '--------------------------------------------------------------------------'
    }

    println 'Authors:'
    artifact.authors.each { author ->
        println "\t${author.name} (${author.email})"
    }
    println '--------------------------------------------------------------------------'

    if (version) {
        if (release) {
            [
                    'Version': release.version,
                    'GriffonVersion': release.griffonVersion,
                    'Date': release.date,
            ].each { label, value ->
                println "${label.padRight(15, ' ')}: ${value}"
            }
        } else {
            println "<release ${version} not found for this ${type}>"
            println '--------------------------------------------------------------------------'
        }
    } else if (artifact.releases) {
        println 'Releases:'
        println "${'Version'.padRight(20, ' ')}${'Griffon Version'.padRight(25, ' ')}Date"
        artifact.releases.each { r ->
            println "${r.version.padRight(20, ' ')}${r.griffonVersion.padRight(25, ' ')}${r.date}"
        }
    } else {
        println "No releases found for this ${type}"
        println '--------------------------------------------------------------------------'
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
    selectArtifactRepository()
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
                doInstallArtifact(artifactRepository, type, artifactArgs[0], artifactArgs[1])
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

            ArtifactInstallEngine pluginInstallEngine = createArtifactInstallEngine(metadata)
            pluginInstallEngine.uninstall(type, artifactName, artifactVersion)
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

doInstallArtifact = { ArtifactRepository artifactRepository, String type, name, version = null, Metadata md = metadata ->
    return withArtifactInstall(type) {
        if (!version) {
            Artifact artifact = artifactRepository.findArtifact(type, name)
            if (!artifact) {
                if (!failOnError) return false
                event('StatusError', ["${capitalize(type)} ${name} was not found in repository ${artifactRepository.name}."])
                exit 1
            }
            for (release in artifact.releases) {
                if (isValidVersion(release.griffonVersion, GriffonUtil.getGriffonVersion())) {
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

installArtifactForName = { String type, String name, String version, Metadata md ->
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
    }
}

doInstallFromFile = { type, file, md ->
    ArtifactInstallEngine pluginInstallEngine = createArtifactInstallEngine(metadata)
    pluginInstallEngine.installFromFile(type, file)
}

private ArtifactInstallEngine createArtifactInstallEngine(Metadata md) {
    def artifactInstallEngine = new ArtifactInstallEngine(griffonSettings, md, ant)
    artifactInstallEngine.pluginScriptRunner = runPluginScript
    artifactInstallEngine.eventHandler = { eventName, msg -> event(eventName, [msg]) }
    artifactInstallEngine.errorHandler = { msg ->
        event('StatusError', [msg])
        for (dir in artifactInstallEngine.installedArtifacts) {
            ant.delete(dir: dir, failonerror: false)
        }
        exit(1)
    }
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
    Map installedArtifacts = getInstalledArtifacts(type)
    Map<String, String> outdatedArtifacts = [:]

    if (!availableArtifacts) {
        println "\nNo ${type}s available${artifactRepository ? ' in ' + artifactRepository.name : ''}."
    }

    boolean headerDisplayed = false
    if (installedArtifacts) {
        installedArtifacts.each {name, data ->
            String version = data.version
            String availableVersion = availableArtifacts[name].version
            if (availableVersion != version && availableVersion != null) {
                if (!headerDisplayed) {
                    println """
${capitalize(type)}s with available updates are listed below:
-----------------------------------------------------------------------
<${capitalize(type)}>                   <Current>         <Available>"""
                    headerDisplayed = true
                }
                println "${name.padRight(27 + (type == Archetype.TYPE ? 3 : 0), " ")}${version.padRight(16, " ")}  ${availableVersion}"
                outdatedArtifacts[name] = availableVersion
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
                    System.setProperty('griffon.artifact.force.updates', 'true')
                    outdatedArtifacts.each { name, version ->
                        // skip if name-version has been installed already because
                        // it is a dependency of another artifact that was upgraded in  a previous
                        // iteration
                        if (Metadata.current["${type}${type == Plugin.TYPE ? 's' : ''}" + name] == version) return
                        installArtifactForName(type, name, version, Metadata.current)
                    }
                } finally {
                    isInteractive = wasInteractive
                    System.setProperty('griffon.artifact.force.updates', 'false')
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
                if (isValidVersion(release.griffonVersion, GriffonUtil.getGriffonVersion())) {
                    artifacts[artifact.name] = [
                            version: release.version,
                            title: artifact.title
                    ]
                    break
                }
            }
        }
    }

    resolveArtifactRepository()

    if (artifactRepository) {
        finder(artifactRepository)
    }
    else {
        ArtifactRepositoryRegistry.instance.withRepositories {String name, ArtifactRepository artifactRepository ->
            finder(artifactRepository)
        }
    }

    artifacts
}

getInstalledArtifacts = { String type ->
    Map artifacts = [:]

    for (resource in ArtifactUtils.resolveResources("file://${artifactBase(type)}/*/${type}.json")) {
        Release release = Release.make(type, new JsonSlurper().parseText(resource.file.text))
        artifacts[release.artifact.name] = [
                version: release.version,
                title: release.artifact.title
        ]
    }

    artifacts
}