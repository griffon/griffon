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

import griffon.util.GriffonUtil
import griffon.util.Metadata
import org.codehaus.griffon.artifacts.ArtifactDependencyResolver
import org.codehaus.griffon.artifacts.ArtifactRepository
import org.codehaus.griffon.artifacts.ArtifactRepositoryRegistry
import org.codehaus.griffon.artifacts.VersionComparator
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release

import static griffon.util.ArtifactSettings.getRegisteredArtifacts
import static griffon.util.ArtifactSettings.isValidVersion
import static griffon.util.GriffonNameUtils.capitalize

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_list_artifacts_called')) return
_griffon_list_artifacts_called = true

includeTargets << griffonScript('_GriffonArtifacts')

listArtifacts = { String type ->
    resolveArtifactRepository()

    def artifactLister = { repository ->
        List<Artifact> artifacts = repository.listArtifacts(type)
        if (artifacts) {
            listArtifactsHeader(repository, type)
            artifacts.each { Artifact artifact -> println formatArtifactForPrint(artifact) }
        } else {
            println "\nNo ${type}s found in repository ${repository.name}."
        }
    }

    if (!argsMap.installed) {
        if (artifactRepository) {
            artifactLister(artifactRepository)
        } else {
            ArtifactRepositoryRegistry.instance.withRepositories {name, repository ->
                artifactLister(repository)
            }
        }
    }

    if (type == Plugin.TYPE) {
        if (griffonSettings.isGriffonProject() && !griffonSettings.isArchetypeProject()) {
            println '\n<Project>'
            listInstalledArtifacts(type, artifactSettings.getInstalledReleases(type))
        }
        Map<String, Release> frameworkPlugins = pluginSettings.getFrameworkPluginReleases()
        if (frameworkPlugins) {
            println '\n<Framework>'
            listInstalledArtifacts(type, frameworkPlugins)
        }
    } else {
        listInstalledArtifacts(type, artifactSettings.getInstalledReleases(type))
    }
    listArtifactsFooter(type)
}

listArtifactsHeader = { repository, type ->
    println """${capitalize(type)}s available in the ${repository.name} repository are listed below:
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

listInstalledArtifacts = { String type, Map<String, Release> installedArtifacts ->
    if (type == Archetype.TYPE) {
        installedArtifacts['default'] = [
                version: GriffonUtil.getGriffonVersion(),
                artifact: [title: 'Used when no archetype is specified']
        ]
    }

    if (installedArtifacts) {
        println """
${capitalize(type)}s you currently have installed are listed below:
${'-' * 80}
${'Name'.padRight(30, ' ')}${'Version'.padRight(20, ' ')} Title
"""

        List list = installedArtifacts.collect([]) { entry ->
            "${entry.key.padRight(30, ' ')}${entry.value.version.toString().padRight(20, ' ')} ${entry.value.artifact.title}"
        }
        list.sort()
        list.each { println it }
    } else {
        println "\nYou do not have any ${type}s installed."
    }
}

displayArtifact = { String type, String name, String version ->
    Artifact artifact = null
    doWithSelectedRepository { repository ->
        artifact = repository.findArtifact(type, name)
        artifact != null
    }

    if (artifact == null) {
        event('StatusError', ["${capitalize(type)} with name '${name}' was not found"])
        exit 1
    }

    displayArtifactHeader(type, artifactRepository)
    displayArtifactInfo(type, name, version, artifactRepository)
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

    int padding = 13i

    [
            'Name': artifact.name,
            'Title': artifact.title,
            'License': artifact.license,
            'Source': artifact.source ?: 'No source link provided',
            'Documentation': artifact.documentation ?: 'No documentation link provided'
    ].each { label, value ->
        println "${label.padRight(padding, ' ')}: ${value}"
    }
    println('-' * 80)

    if (type == Plugin.TYPE) {
        [
                'Toolkits': artifact.toolkits*.getLowercaseName().join(', ') ?: 'works with all toolkits',
                'Platforms': artifact.platforms*.getLowercaseName().join(', ') ?: 'works in all platforms',
                'Framework': artifact.framework ? 'yes' : 'no'
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
        println "${'Version'.padRight(20, ' ')}${'Griffon Version'.padRight(25, ' ')}Date\n"
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

doListArtifactUpdates = { String type ->
    resolveFrameworkFlag()
    Map availableArtifacts = getAvailableArtifacts(type)
    Map installedArtifacts = type == Archetype.TYPE || framework ? artifactSettings.getInstalledReleases(type, framework) : getRegisteredArtifacts(type)
    Map outdatedArtifacts = [:]

    if (!availableArtifacts) {
        println "\nNo ${type} updates available in configured repositories."
        exit 0
    }

    boolean headerDisplayed = false
    if (installedArtifacts) {
        installedArtifacts = installedArtifacts.sort()
        VersionComparator versionComparator = new VersionComparator()
        installedArtifacts.each {name, version ->
            if (version instanceof Release) version = version.version
            String availableVersion = availableArtifacts.get(name)?.version
            if (availableVersion != version && availableVersion != null && versionComparator.compare(availableVersion, version) > 0) {
                String repositoryName = availableArtifacts.get(name).repository.name
                if (!headerDisplayed) {
                    println """
${capitalize(type)}s with available updates are listed below:
${'-' * 80}
${('<' + capitalize(type) + '>').padRight(20, ' ')}${'<Current>'.padRight(20, ' ')}${'<Available>'.padRight(20, ' ')}<From>"""
                    headerDisplayed = true
                }
                println "${name.padRight(20, ' ')}${version.padRight(20, ' ')}${availableVersion.padRight(20, ' ')}${repositoryName}"
                outdatedArtifacts[name] = [
                        newVersion: availableVersion,
                        oldVersion: version,
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
                System.setProperty(ArtifactDependencyResolver.KEY_FORCE_UPGRADE, 'true')
                if (type == Plugin.TYPE) {
                    uninstalledPlugins.clear()
                    outdatedArtifacts.each { name, data ->
                        uninstalledPlugins[name] = data.newVersion
                        doUninstallArtifact Plugin.TYPE, name, data.oldVersion, false
                    }
                    installPlugins(Metadata.current, uninstalledPlugins)
                } else {
                    outdatedArtifacts.each { name, data ->
                        doInstallArtifact(data.repository, type, name, data.version, Metadata.current)
                    }
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
        VersionComparator versionComparator = new VersionComparator()
        repository.listArtifacts(type).each { Artifact artifact ->
            for (release in artifact.releases) {
                if (isValidVersion(GriffonUtil.getGriffonVersion(), release.griffonVersion)) {
                    Map data = artifacts[artifact.name]
                    if (!data || versionComparator.compare(release.version, data.version) > 0) {
                        artifacts[artifact.name] = [
                                version: release.version,
                                title: artifact.title,
                                repository: repository
                        ]
                    }
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
