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
import griffon.util.GriffonNameUtils
import griffon.util.GriffonUtil
import griffon.util.Metadata
import org.codehaus.griffon.artifacts.ArtifactInstallEngine
import org.codehaus.griffon.artifacts.ArtifactRepository
import org.codehaus.griffon.artifacts.ArtifactRepositoryRegistry
import org.codehaus.griffon.artifacts.RemoteArtifactRepository
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import static griffon.util.GriffonNameUtils.isBlank
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

listArtifacts = { String type, ArtifactRepository repository ->
    listArtifactsHeader(repository, type)

    List<Artifact> artifacts = repository.listArtifacts(type)
    if (artifacts) {
        artifacts.each { Artifact artifact -> println formatArtifactHeader(artifact) }
    } else {
        println "No ${type}s found in repository: ${repository.name}."
    }

    // TODO: list installed artifacts of matching $type

    listArtifactsFooter(type)
}

listArtifactsHeader = { repository, type ->
    println """
${GriffonNameUtils.capitalize(type)}s available in the ${repository.name} repository are listed below:
-----------------------------------------------------------------------
${'Name'.padRight(30, ' ')}${'Releases'.padRight(8, ' ')} Title
"""
}

formatArtifactHeader = { Artifact artifact ->
    "${artifact.name.padRight(30, ' ')}${artifact.releases.size().toString().padRight(8, ' ')} ${artifact.title}"
}

listArtifactsFooter = { type ->
    println """
To find more info about ${type} type 'griffon ${type}-info [NAME]'

To install type 'griffon install-${type} [NAME] [VERSION]'

For further info visit http://griffon.codehaus.org/${GriffonNameUtils.capitalize(type)}s
"""
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
        event('StatusError', ["${GriffonNameUtils.capitalize(type)} with name '${name}' was not found in repository ${repository.name}"])
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

For further info visit http://griffon.codehaus.org/${GriffonNameUtils.capitalize(type)}s
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
                doInstallArtifact(type, artifactArgs[0], artifactArgs[1])
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
        callable.call()
    } catch (e) {
        logError("Error installing ${type}: ${e.message}", GriffonExceptionHandler.sanitize(e))
        exit(1)
    }
}

doInstallArtifactFromURL = { String type, URL url, Metadata md = metadata ->
    withArtifactInstall(type) {
        File file = RemoteArtifactRepository.downloadFromURL(url)
        doInstallFromFile(type, file, md)
    }
}

doInstallArtifactFromZip = { String type, File file, Metadata md = metadata ->
    withArtifactInstall(type) {
        doInstallFromFile(type, file, md)
    }
}

doInstallArtifact = { String type, name, version = null, Metadata md = metadata ->
    withArtifactInstall(type) {
        if (!version) {
            Artifact artifact = artifactRepository.findArtifact(type, name)
            if (!artifact) {
                event('StatusError', ["${GriffonNameUtils.capitalize(type)} ${name} was not found in repository ${artifactRepository.name}."])
                exit 1
            }
            for (release in artifact.releases) {
                if (isValidVersion(release.griffonVersion, GriffonUtil.getGriffonVersion())) {
                    version = release.version
                    break
                }
            }
            if (!version) {
                event('StatusError', ["Repository ${artifactRepository.name} does not contain a suitable release for ${type} ${name}."])
                exit 1
            }
        }
        File file = artifactRepository.downloadFile(type, name, version, null)
        doInstallFromFile(type, file, md)
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