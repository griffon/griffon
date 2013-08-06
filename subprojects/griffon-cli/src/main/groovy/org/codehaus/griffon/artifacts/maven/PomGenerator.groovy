/*
 * Copyright 2011-2013 the original author or authors.
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

package org.codehaus.griffon.artifacts.maven

import griffon.util.BuildSettings
import groovy.xml.MarkupBuilder
import org.codehaus.griffon.plugins.PluginInfo

import static org.codehaus.griffon.artifacts.maven.PluginDependenciesParser.traversePluginDependencies

/**
 * @author Andres Almiray
 * @since 1.4.0
 */
class PomGenerator {
    private final Map<String, String> MAVEN_SCOPES = [
        runtime: 'compile',
        compile: 'provided',
        test: 'test'
    ]

    private BuildSettings settings
    private Map artifactInfo
    private String targetDirPath
    private final Map<String, List<PluginDependenciesParser.Dependency>> pluginDependencies = [
        runtime: [],
        compile: [],
        build: [],
        test: []
    ]

    PomGenerator(BuildSettings settings, Map artifactInfo, String targetDirPath) {
        this.settings = settings
        this.artifactInfo = artifactInfo
        this.targetDirPath = targetDirPath

        settings.pluginSettings.getPlugins().values().each { PluginInfo pluginInfo ->
            traversePluginDependencies(new File("${pluginInfo.directory.file}/plugin-dependencies.groovy")).each { k, deps ->
                pluginDependencies[k] += deps
            }
        }
    }

    private static String pomBuilder(Closure cls) {
        StringWriter sw = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(sw)
        cls.delegate = builder
        cls.resolveStrategy = Closure.DELEGATE_FIRST
        builder.project(xmlns: 'http://maven.apache.org/POM/4.0.0',
            'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance',
            'xsi:schemaLocation': 'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd') {
            modelVersion('4.0.0')
            cls()
        }
        return sw.toString()
    }

    void generatePluginPom(String scp, List<PluginDependenciesParser.Dependency> deps) {
        def pom = pomBuilder {
            parent {
                groupId(artifactInfo.group)
                artifactId("griffon-${artifactInfo.name}-parent")
                version(artifactInfo.version)
            }
            artifactId("griffon-${artifactInfo.name}-${scp}")
            version(artifactInfo.version)
            packaging('jar')
            name("${artifactInfo.title} [${scp.toUpperCase()}]")
            description("${artifactInfo.title} [${scp.toUpperCase()}]")

            dependencies {
                if (scp == 'runtime') {
                    dependency {
                        groupId('org.codehaus.griffon')
                        artifactId('griffon-rt')
                        version(settings.griffonVersion)
                    }
                } else if (scp == 'compile') {
                    dependency {
                        groupId(artifactInfo.group)
                        artifactId("griffon-${artifactInfo.name}-runtime")
                        version(artifactInfo.version)
                        scope(scp)
                    }
                } else {
                    dependency {
                        groupId(artifactInfo.group)
                        artifactId("griffon-${artifactInfo.name}-compile")
                        version(artifactInfo.version)
                        scope(scp)
                    }
                }
                if (scp == 'runtime') {
                    pluginDependencies[scp].each { PluginDependenciesParser.Dependency dep ->
                        dependency {
                            groupId(dep.groupId)
                            artifactId(dep.artifactId)
                            version(dep.version)
                            if (dep.classifier) classifier(dep.classifier)
                            scope(MAVEN_SCOPES[scp])
                        }
                    }
                    pluginDependencies['compile'].each { PluginDependenciesParser.Dependency dep ->
                        dependency {
                            groupId(dep.groupId)
                            artifactId(dep.artifactId)
                            version(dep.version)
                            if (dep.classifier) classifier(dep.classifier)
                            scope(MAVEN_SCOPES[scp])
                        }
                    }
                } else if (scp == 'compile') {
                    pluginDependencies['build'].each { PluginDependenciesParser.Dependency dep ->
                        dependency {
                            groupId(dep.groupId)
                            artifactId(dep.artifactId)
                            version(dep.version)
                            if (dep.classifier) classifier(dep.classifier)
                            scope(MAVEN_SCOPES[scp])
                        }
                    }
                } else {
                    pluginDependencies[scp].each { PluginDependenciesParser.Dependency dep ->
                        dependency {
                            groupId(dep.groupId)
                            artifactId(dep.artifactId)
                            version(dep.version)
                            if (dep.classifier) classifier(dep.classifier)
                            scope(MAVEN_SCOPES[scp])
                        }
                    }
                }
                deps.each { PluginDependenciesParser.Dependency dep ->
                    dependency {
                        groupId(dep.groupId)
                        artifactId(dep.artifactId)
                        version(dep.version)
                        if (dep.classifier) classifier(dep.classifier)
                        scope(MAVEN_SCOPES[scp])
                    }
                }
            }
        }

        new File("${targetDirPath}/pom-${scp}.xml").text = pom
    }

    void generatePluginParentPom(List mods) {
        def pom = pomBuilder {
            artifactId("griffon-${artifactInfo.name}-parent")
            version(artifactInfo.version)
            packaging('pom')
            name("griffon-${artifactInfo.name} aggregator")
            description("griffon-${artifactInfo.name} aggregator")

            if (artifactInfo.source) {
                scm {
                    url(artifactInfo.source)
                }
            }

            licenses {
                license {
                    name(artifactInfo.license)
                }
            }

            developers {
                for (author in artifactInfo.authors) {
                    developer {
                        id(author.id)
                        name(author.name)
                        email(author.email)
                    }
                }
            }

            modules {
                mods.each { mod ->
                    module("griffon-${artifactInfo.name}-${mod}")
                }
            }
        }

        new File("${targetDirPath}/pom-parent.xml").text = pom
    }

    void generatePluginBom(List mods) {
        def pom = pomBuilder {
            artifactId("griffon-${artifactInfo.name}-bom")
            version(artifactInfo.version)
            packaging('pom')
            name("griffon-${artifactInfo.name} BOM")
            description("griffon-${artifactInfo.name} BOM")

            if (artifactInfo.source) {
                scm {
                    url(artifactInfo.source)
                }
            }

            licenses {
                license {
                    name(artifactInfo.license)
                }
            }

            developers {
                for (author in artifactInfo.authors) {
                    developer {
                        id(author.id)
                        name(author.name)
                        email(author.email)
                    }
                }
            }

            dependencyManagement {
                dependencies {
                    mods.each { mod ->
                        dependency {
                            groupId(artifactInfo.group)
                            artifactId("griffon-${artifactInfo.name}-${mod}")
                            version('${project.version}')
                        }
                    }
                }
            }
        }

        new File("${targetDirPath}/pom-bom.xml").text = pom
    }
}
