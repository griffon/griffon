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

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.tooling.BuildException

/**
 * @author Andres Almiray
 */
class GriffonPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        GriffonExtension extension = project.extensions.create('griffon', GriffonExtension, project)

        applyDefaultPlugins(project)

        // enable jcenter by default
        project.repositories.jcenter()

        applyDefaultDependencies(project)

        String sourceSetName = project.plugins.hasPlugin('groovy') ? 'groovy' : 'java'

        configureDefaultSourceSets(project, sourceSetName)
        adjustJavadocClasspath(project, sourceSetName)
        adjustIdeClasspaths(project)
        createDefaultDirectoryStructure(project, sourceSetName)

        registerBuildListener(project, extension)
    }

    private void applyDefaultDependencies(final Project project) {
        // add compile time configurations
        project.configurations.maybeCreate('compileOnly')
        project.configurations.maybeCreate('testCompileOnly')
        project.configurations.maybeCreate('griffon').visible = false

        // wire up classpaths with compile time dependencies
        project.sourceSets.main.compileClasspath += [project.configurations.compileOnly]
        project.sourceSets.test.compileClasspath += [project.configurations.testCompileOnly]

        GriffonPluginResolutionStrategy.applyTo(project)
    }

    private void applyDefaultPlugins(Project project) {
        project.apply(plugin: 'idea')
        project.apply(plugin: 'java')
        if (!project.hasProperty('griffonPlugin') || !project.griffonPlugin) {
            project.apply(plugin: 'application')
        }
    }

    private void adjustJavadocClasspath(Project project, String sourceSetName) {
        project.javadoc.classpath += [project.configurations.compileOnly]
        if (sourceSetName == 'groovy') {
            project.groovydoc.classpath += [project.configurations.compileOnly]
        }
    }

    private void adjustIdeClasspaths(Project project) {
        // adjust Eclipse classpath, but only if EclipsePlugin is applied
        project.plugins.withId('eclipse') {
            project.eclipse.classpath.plusConfigurations += [project.configurations.compileOnly]
            project.eclipse.classpath.plusConfigurations += [project.configurations.testCompileOnly]
        }

        // adjust IntelliJ classpath
        project.idea.module.scopes.PROVIDED.plus += [project.configurations.compileOnly]
        project.idea.module.scopes.PROVIDED.plus += [project.configurations.testCompileOnly]
    }

    private void configureDefaultSourceSets(Project project, String sourceSetName) {
        // configure default source directories
        project.sourceSets.main[sourceSetName].srcDirs = [
            'griffon-app/conf',
            'griffon-app/controllers',
            'griffon-app/models',
            'griffon-app/views',
            'griffon-app/services',
            'griffon-app/lifecycle',
            'src/main/' + sourceSetName
        ]
        // configure default resource directories
        project.sourceSets.main.resources.srcDirs = [
            'griffon-app/resources',
            'griffon-app/i18n',
            'src/main/resources'
        ]
    }

    private void processMainResources(Project project, GriffonExtension extension) {
        project.processResources {
            from(project.sourceSets.main.resources.srcDirs) {
                exclude '**/*.properties'
                exclude '**/*.groovy'
                exclude '**/*.xml'
            }
            from(project.sourceSets.main.resources.srcDirs) {
                include '**/*.properties'
                include '**/*.groovy'
                include '**/*.xml'
                filter(ReplaceTokens, tokens: [
                    'application.name'   : project.name,
                    'application.version': project.version,
                    'griffon.version'    : extension.version
                ])
            }
        }
    }

    private void processTestResources(Project project, GriffonExtension extension) {
        project.processTestResources {
            from(project.sourceSets.test.resources.srcDirs) {
                exclude '**/*.properties'
                exclude '**/*.groovy'
                exclude '**/*.xml'
            }
            from(project.sourceSets.test.resources.srcDirs) {
                include '**/*.properties'
                include '**/*.groovy'
                include '**/*.xml'
                filter(ReplaceTokens, tokens: [
                    'application.name'   : project.name,
                    'application.version': project.version,
                    'griffon.version'    : extension.version
                ])
            }
        }
    }

    private void createDefaultDirectoryStructure(Project project, String sourceSetName) {
        project.gradle.taskGraph.whenReady {
            project.sourceSets.main[sourceSetName].srcDirs.each { it.mkdirs() }
            project.sourceSets.test[sourceSetName].srcDirs.each { it.mkdirs() }
            project.sourceSets.main.resources.srcDirs.each { it.mkdirs() }
            project.sourceSets.test.resources.srcDirs.each { it.mkdirs() }
        }
    }

    private void validateToolkit(Project project, GriffonExtension extension) {
        if (extension.toolkit) {
            if (!GriffonExtension.TOOLKIT_NAMES.contains(extension.toolkit)) {
                throw new BuildException("The value of griffon.toolkit can only be one of ${GriffonExtension.TOOLKIT_NAMES.join(',')}",
                    new IllegalStateException(extension.toolkit))
            }
        }
    }

    private void registerBuildListener(
        final Project project, final GriffonExtension extension) {
        project.gradle.addBuildListener(new BuildAdapter() {
            @Override
            void projectsEvaluated(Gradle gradle) {
                // add default dependencies
                appendDependency('core')
                appendDependency('core-compile')
                appendDependency('core-test')

                validateToolkit(project, extension)

                boolean groovyDependenciesEnabled = extension.includeGroovyDependencies?.toBoolean() ||
                    (project.plugins.hasPlugin('groovy') && extension.includeGroovyDependencies == null)

                if (extension.toolkit) {
                    appendDependency(extension.toolkit)
                    maybeIncludeGroovyDependency(groovyDependenciesEnabled, extension.toolkit + '-groovy')
                }
                maybeIncludeGroovyDependency(groovyDependenciesEnabled, 'groovy')
                maybeIncludeGroovyDependency(groovyDependenciesEnabled, 'groovy-compile')

                processMainResources(project, extension)
                processTestResources(project, extension)
            }

            private boolean maybeIncludeGroovyDependency(boolean groovyDependenciesEnabled, String artifactId) {
                if (artifactId =~ /groovy/) {
                    if (groovyDependenciesEnabled) {
                        appendDependency(artifactId)
                    }
                    return true
                }
                false
            }

            private void appendDependency(String artifactId) {
                if (artifactId.endsWith('-compile')) {
                    project.dependencies.add('compileOnly', ['org.codehaus.griffon', 'griffon-' + artifactId, extension.version].join(':'))
                    project.dependencies.add('testCompileOnly', ['org.codehaus.griffon', 'griffon-' + artifactId, extension.version].join(':'))
                } else if (artifactId.endsWith('-test')) {
                    project.dependencies.add('testCompile', ['org.codehaus.griffon', 'griffon-' + artifactId, extension.version].join(':'))
                } else {
                    project.dependencies.add('compile', ['org.codehaus.griffon', 'griffon-' + artifactId, extension.version].join(':'))
                }
            }
        })
    }
}
