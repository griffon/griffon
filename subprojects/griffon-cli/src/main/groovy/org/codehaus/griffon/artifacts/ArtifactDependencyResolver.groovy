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

import griffon.util.GriffonUtil
import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static griffon.util.ArtifactSettings.isValidVersion

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class ArtifactDependencyResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ArtifactDependencyResolver)

    static final String KEY_FORCE_UPGRADE = 'griffon.artifact.force.upgrade'

    private final Map<ArtifactDependency.Key, ArtifactDependency> processedDependencies = [:]

    List<ArtifactDependency> resolveDependencyTree(String type, Map<String, String> dependencies) {
        processedDependencies.clear()
        List<ArtifactDependency> artifactDependencies = []
        dependencies.each { name, version ->
            if (version == '<noversion>' || version == '<latest>') version = null
            if (version) {
                ArtifactDependency.Key key = new ArtifactDependency.Key(name, version)
                if (processedDependencies.get(key)) return
            }
            ArtifactDependency dependency = resolveDependencyTree(type, name, version)
            artifactDependencies << dependency
        }
        artifactDependencies
    }

    ArtifactDependency resolveDependencyTree(String type, String name, String version = null) {
        ArtifactDependency artifactDependency = new ArtifactDependency(name)
        artifactDependency.version = version

        if (version) {
            ArtifactDependency.Key key = new ArtifactDependency.Key(name, version)
            ArtifactDependency processed = processedDependencies.get(key)
            if (processed) return processed
        }

        if (LOG.debugEnabled) {
            LOG.debug("Resolving for ${type}:${name}:${version ? version : '<noversion>'}")
        }

        Release release = null
        List<Map<String, Object>> snapshots = []
        ArtifactRepositoryRegistry.instance.withRepositories { String repoName, ArtifactRepository repository ->
            if (release) return
            Artifact artifact = repository.findArtifact(type, name)
            if (artifact) {
                artifactDependency.repository = repository

                String v = version
                if (v) {
                    release = artifact.releases.find {it.version == version}
                } else {
                    for (r in artifact.releases) {
                        if (isValidVersion(GriffonUtil.griffonVersion, r.griffonVersion)) {
                            release = r
                            v = r.version
                            break
                        }
                    }
                }

                if (v.endsWith('-SNAPSHOT') && release) {
                    snapshots << [
                            release: release,
                            repository: repository
                    ]
                    release = null
                }
            }
        }

        if(snapshots) {
            snapshots.sort { a, b ->
                b.release.date <=> a.release.date
            }
            release = snapshots[0].release
            artifactDependency.repository = snapshots[0].repository
        }

        if(release) {
            if (LOG.debugEnabled) {
                LOG.debug("Resolved ${type}:${name}:${version} with repository ${artifactDependency.repository.name}")
            }
            artifactDependency.release = release
            artifactDependency.version = release.version

            ArtifactDependency.Key key = new ArtifactDependency.Key(name, release.version)
            ArtifactDependency processed = processedDependencies.get(key)
            if (processed) return processed

            processedDependencies[key] = artifactDependency
            return resolveDependenciesOf(artifactDependency)
        }

        if (LOG.debugEnabled) {
            LOG.debug("Could not resolve ${type}:${name}:${version ? version : '<noversion>'}")
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

    List<ArtifactDependency> resolveEvictions(Collection<ArtifactDependency> installed, Collection<ArtifactDependency> targets) {
        List<Map> evictions = []
        for (dependency in targets) {
            fillEvictions(dependency, evictions)
        }
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
                    if (winner.dependency.installed || System.getProperty(KEY_FORCE_UPGRADE) == 'true') {
                        matches.each {
                            if (it.version != winner.version) it.dependency.evicted = true
                            it.processed = true
                        }
                    } else {
                        matches.each {it.dependency.conflicted = it.processed = true}
                    }
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
                    winner = matches[0]
                    winner.dependency.evicted = false
                    matches.each {it.dependency.evicted = it != winner}
                }
            }
        }

        targets*.updateConflicts()

        List<ArtifactDependency> installPlan = []
        for (dependency in installed) {
            // first mark installed & evicted dependencies to uninstall
            if (dependency.evicted) installPlan << dependency
        }
        for (dependency in targets) {
            processEvictionsAndConflicts(dependency, installPlan)
        }
        installPlan
    }

    private void processEvictionsAndConflicts(ArtifactDependency dependency, List<ArtifactDependency> list) {
        dependency.dependencies.each {processEvictionsAndConflicts(it, list)}
        if (!dependency.evicted && !dependency.conflicted && !dependency.installed &&
                !list.find {it.name == dependency.name && it.version == dependency.version}) {
            list << dependency
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
