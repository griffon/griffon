/*
 * Copyright 2008-2014 the original author or authors.
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
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvableDependencies

class GriffonPluginResolutionStrategy {
    private static final String PLUGIN_PREFIX = 'griffon-'
    private static final String PLUGIN_SUFFIX = '-plugin'
    private static final String GRIFFON_PLUGIN_CONFIGURATION = 'griffonPlugin'
    private static final List<String> CONFIGURATION_NAMES = ['compile', 'compileOnly', 'testCompileOnly', 'testCompile', 'runtime']

    static void applyTo(Project project) {
        GriffonPluginDependencyResolver resolver = new GriffonPluginDependencyResolver(project)
        project.configurations.getByName(GRIFFON_PLUGIN_CONFIGURATION).incoming.beforeResolve(resolver)
        CONFIGURATION_NAMES.each { String configurationName ->
            project.configurations.getByName(configurationName).incoming.beforeResolve(new GriffonDependencyResolver(configurationName, resolver))
        }
    }

    private static class GriffonDependencyResolver implements Action<ResolvableDependencies> {
        private final GriffonPluginDependencyResolver resolver
        private final String configurationName

        GriffonDependencyResolver(String configurationName, GriffonPluginDependencyResolver resolver) {
            this.resolver = resolver
            this.configurationName = configurationName
        }

        @Override
        void execute(ResolvableDependencies resolvableDependencies) {
            if (!resolver.dependencyMap) {
                resolver.project.configurations.getByName(GRIFFON_PLUGIN_CONFIGURATION).resolve()
            }
            resolver.dependencyMap[configurationName].each { String dependency ->
                resolver.project.dependencies.add(configurationName, dependency)
            }
        }
    }

    private static class GriffonPluginDependencyResolver implements Action<ResolvableDependencies> {
        final Project project
        final Map<String, List<String>> dependencyMap = [:]

        GriffonPluginDependencyResolver(Project project) {
            this.project = project
        }

        @Override
        void execute(ResolvableDependencies resolvableDependencies) {
            resolvableDependencies.dependencies.each { Dependency dependency ->
                String pluginName = dependency.name
                if (pluginName.startsWith(PLUGIN_PREFIX) && pluginName.endsWith(PLUGIN_SUFFIX)) {
                    String bomDependency = "${dependency.group}:${dependency.name}:${dependency.version}@pom"

                    project.logger.debug("Resolving {}", bomDependency)
                    File bomFile = project.configurations.detachedConfiguration(
                        project.dependencies.create(bomDependency)
                    ).singleFile
                    def bom = new XmlSlurper().parse(bomFile)

                    boolean groovyDependenciesEnabled = project.griffonIncludeGroovyRuntime?.toBoolean() ||
                        (project.plugins.hasPlugin('groovy') && project.griffonIncludeGroovyRuntime == null)
                    project.logger.debug("Groovy dependencies are {}enabled in project {}", (groovyDependenciesEnabled ? '': 'NOT '),project.name)

                    bom.dependencyManagement.dependencies.dependency.each { importedDependency ->
                        String groupId = importedDependency.groupId.text()
                        String artifactId = importedDependency.artifactId.text()
                        String version = importedDependency.version.text()
                        String scope = importedDependency.scope?.text() ?: 'compile'

                        groupId = groupId == '${project.groupId}' ? dependency.group : groupId
                        version = version == '${project.version}' ? dependency.version : version

                        String dependencyCoordinates = [groupId, artifactId, version].join(':')
                        project.logger.debug("Processing {} in scope {}", dependencyCoordinates, scope)

                        if (artifactId =~ /groovy/) {
                            if (groovyDependenciesEnabled) {
                                appendDependency(artifactId, scope, dependencyCoordinates)
                            }
                        } else {
                            appendDependency(artifactId, scope, dependencyCoordinates)
                        }
                    }
                } else {
                    project.logger.warn("Dependency {}:{}:{} does not appear to be a valid Griffon plugin!",
                        dependency.group, dependency.name, dependency.version)
                }
            }

            project.configurations.getByName(GRIFFON_PLUGIN_CONFIGURATION).incoming.dependencies.clear()
        }

        private void appendDependency(String artifactId, String scope, String dependencyCoordinates) {
            if (artifactId.endsWith('-compile')) {
                project.logger.debug("Adding {} to 'compileOnly' configuration", dependencyCoordinates)
                dependencyMap.get('compileOnly', []) << dependencyCoordinates
                project.logger.debug("Adding {} to 'testCompileOnly' configuration", dependencyCoordinates)
                dependencyMap.get('testCompileOnly', []) << dependencyCoordinates
            } else if (scope == 'test') {
                project.logger.debug("Adding {} to 'testCompile' configuration", dependencyCoordinates)
                dependencyMap.get('testCompile', []) << dependencyCoordinates
            } else {
                project.logger.debug("Adding {} to '{}' configuration", dependencyCoordinates, scope)
                dependencyMap.get(scope, []) << dependencyCoordinates
            }
        }
    }
}