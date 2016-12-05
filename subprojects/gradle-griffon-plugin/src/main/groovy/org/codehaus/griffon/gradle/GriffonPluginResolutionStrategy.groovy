/*
 * Copyright 2008-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvableDependencies

/**
 * @author Andres Almiray
 */
class GriffonPluginResolutionStrategy {
    private static final String PLUGIN_PREFIX = 'griffon-'
    private static final String PLUGIN_SUFFIX = '-plugin'
    private static final String GRIFFON_CONFIGURATION = 'griffon'
    private static final List<String> CONFIGURATION_NAMES = [
        'compile',
        'compileOnly',
        'testCompileOnly',
        'testCompile',
        'runtime'
    ]

    private static final Map<String, List<String>> DEPENDENCY_MAP = [:]

    static void applyTo(Project project) {
        GriffonPluginDependencyResolver resolver = new GriffonPluginDependencyResolver(project)

        Configuration griffonConfiguration = project.configurations.getByName(GRIFFON_CONFIGURATION)
        griffonConfiguration.incoming.beforeResolve(resolver)
        griffonConfiguration.resolve()

        CONFIGURATION_NAMES.each { String configurationName ->
            if (project.extensions.getByName(GRIFFON_CONFIGURATION).disableDependencyResolution) {
                return
            }
            DEPENDENCY_MAP[configurationName].each { String dependency ->
                project.logger.info("Adding {} to '{}' configuration", configurationName, dependency)
                resolver.project.dependencies.add(configurationName, dependency)
            }
        }
    }

    private static class GriffonPluginDependencyResolver implements Action<ResolvableDependencies> {
        final Project project

        GriffonPluginDependencyResolver(Project project) {
            this.project = project
        }

        @Override
        void execute(ResolvableDependencies resolvableDependencies) {
            if (griffonExtension.disableDependencyResolution) {
                return
            }

            String toolkit = griffonExtension.toolkit
            project.logger.info("UI toolkit for project {} is {}", project.name, toolkit)
            String toolkitRegex = (GriffonExtension.TOOLKIT_NAMES - toolkit).join('|')

            boolean groovyDependenciesEnabled = griffonExtension.includeGroovyDependencies?.toBoolean() ||
                (project.plugins.hasPlugin('groovy') && griffonExtension.includeGroovyDependencies == null)
            project.logger.info("Groovy dependencies are {}enabled in project {}", (groovyDependenciesEnabled ? '' : 'NOT '), project.name)

            resolvableDependencies.dependencies.each { Dependency dependency ->
                String pluginName = dependency.name
                if (pluginName.startsWith(PLUGIN_PREFIX) && pluginName.endsWith(PLUGIN_SUFFIX)) {
                    String bomDependency = "${dependency.group}:${dependency.name}:${dependency.version}@pom"

                    project.logger.info("Resolving {}", bomDependency)
                    File bomFile = project.configurations.detachedConfiguration(
                        project.dependencies.create(bomDependency)
                    ).singleFile
                    def bom = new XmlSlurper().parse(bomFile)

                    bom.dependencyManagement.dependencies.dependency.each { importedDependency ->
                        if (Boolean.parseBoolean(importedDependency.optional?.text() ?: 'false')) {
                            return
                        }

                        String groupId = importedDependency.groupId.text()
                        String artifactId = importedDependency.artifactId.text()
                        String version = importedDependency.version.text()
                        String scope = importedDependency.scope?.text() ?: 'compile'

                        groupId = groupId == '${project.groupId}' ? dependency.group : groupId
                        version = version == '${project.version}' ? dependency.version : version

                        String dependencyCoordinates = [groupId, artifactId, version].join(':')
                        project.logger.info("Processing {} in scope {}", dependencyCoordinates, scope)

                        if (toolkit) {
                            if (artifactId =~ /$toolkitRegex/) {
                                return
                            } else if (!maybeIncludeGroovyDependency(groovyDependenciesEnabled, artifactId, scope, dependencyCoordinates)) {
                                appendDependency(artifactId, scope, dependencyCoordinates)
                            }
                        } else if (!maybeIncludeGroovyDependency(groovyDependenciesEnabled, artifactId, scope, dependencyCoordinates)) {
                            appendDependency(artifactId, scope, dependencyCoordinates)
                        }
                    }
                } else {
                    project.logger.warn("Dependency {}:{}:{} does not appear to be a valid Griffon plugin!",
                        dependency.group, dependency.name, dependency.version)
                }
            }
        }

        private GriffonExtension getGriffonExtension() {
            project.extensions.getByName(GRIFFON_CONFIGURATION)
        }

        private boolean maybeIncludeGroovyDependency(boolean groovyDependenciesEnabled, String artifactId, String scope, String dependencyCoordinates) {
            if (artifactId =~ /groovy/) {
                if (groovyDependenciesEnabled) {
                    appendDependency(artifactId, scope, dependencyCoordinates)
                }
                return true
            }
            false
        }

        private void appendDependency(String artifactId, String scope, String dependencyCoordinates) {
            if (artifactId.endsWith('-compile')) {
                DEPENDENCY_MAP.get('compileOnly', []) << dependencyCoordinates
                DEPENDENCY_MAP.get('testCompileOnly', []) << dependencyCoordinates
            } else if (scope == 'test' && artifactId.endsWith('-test')) {
                DEPENDENCY_MAP.get('testCompile', []) << dependencyCoordinates
            } else {
                DEPENDENCY_MAP.get(scope, []) << dependencyCoordinates
            }
        }
    }
}