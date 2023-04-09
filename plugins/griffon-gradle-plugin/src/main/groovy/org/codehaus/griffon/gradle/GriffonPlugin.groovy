/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import org.gradle.BuildAdapter
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.tooling.BuildException
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.project.java.JavaProjectPlugin

import java.text.SimpleDateFormat

/**
 * @author Andres Almiray
 */
class GriffonPlugin implements Plugin<Project> {
    private static final boolean MACOSX = System.getProperty('os.name').contains('Mac OS')

    @Override
    void apply(Project project) {
        GriffonExtension extension = project.extensions.create('griffon', GriffonExtension, project)

        applyDefaultVersions(project, extension)
        applyDefaultPlugins(project, extension)
        applyDefaultDependencies(project)
        configureSourceTargetCompatibility(project)
        configureNormalization(project)
        registerBuildListener(project, extension)
    }

    static void applyDefaultVersions(Project project, GriffonExtension extension) {
        Properties versions = new Properties()
        versions.load(GriffonPlugin.class.classLoader.getResourceAsStream('META-INF/griffon/versions.properties'))

        extension.version.set(versions.getProperty('griffonVersion'))
        versions.stringPropertyNames().each { versionProperty ->
            if (!project.hasProperty(versionProperty)) {
                project.extensions.findByType(ExtraPropertiesExtension).set(versionProperty, versions.getProperty(versionProperty))
            }
        }
    }

    static void applyDefaultDependencies(final Project project) {
        project.configurations.maybeCreate('griffon').visible = false
    }

    static void applyDefaultPlugins(Project project, GriffonExtension extension) {
        project.apply(plugin: 'idea')
        project.apply(plugin: 'java-library')
        project.apply(plugin: 'application')
        JavaProjectPlugin.applyIfMissing(project)

        project.extensions.findByType(ProjectConfigurationExtension).with {
            release = (project.rootProject.findProperty('release') ?: false).toBoolean()

            info {
                name          = project.findProperty('projectDescription') ?: project.name
                description   = project.findProperty('projectDescription') ?: project.name
                inceptionYear = new SimpleDateFormat('YYYY').format(new Date())

                repositories {
                    repository {
                        name = 'localRelease'
                        url  = "${project.rootProject.buildDir}/repos/local/release"
                    }
                    repository {
                        name = 'localSnapshot'
                        url  = "${project.rootProject.buildDir}/repos/local/snapshot"
                    }
                }
            }

            coverage {
                jacoco {
                    toolVersion = project.jacocoVersion
                }
            }

            docs {
                javadoc {
                    excludes = ['**/*.html', 'META-INF/**']
                }
            }

            publishing {
                releasesRepository  = 'localRelease'
                snapshotsRepository = 'localSnapshot'
            }
        }
    }

    static void configureNormalization(Project project) {
        project.allprojects {
            normalization {
                runtimeClasspath {
                    ignore('/META-INF/MANIFEST.MF')
                }
            }

            configurations.all {
                resolutionStrategy.failOnVersionConflict()
            }
        }
    }

    static void configureSourceTargetCompatibility(Project project) {
        project.allprojects { Project p ->
            def scompat = project.findProperty('sourceCompatibility')
            def tcompat = project.findProperty('targetCompatibility')

            p.tasks.withType(JavaCompile) { JavaCompile c ->
                if (scompat) c.sourceCompatibility = scompat
                if (tcompat) c.targetCompatibility = tcompat
            }
            p.tasks.withType(GroovyCompile) { GroovyCompile c ->
                if (scompat) c.sourceCompatibility = scompat
                if (tcompat) c.targetCompatibility = tcompat
            }
        }
    }

    static void configureDefaultSourceSets(Project project, String sourceSetName) {
        // configure default source directories
        project.sourceSets.main[sourceSetName].srcDirs += [
            'griffon-app/conf',
            'griffon-app/controllers',
            'griffon-app/models',
            'griffon-app/mvcs',
            'griffon-app/views',
            'griffon-app/services',
            'griffon-app/lifecycle'
        ]

        // configure default resource directories
        project.sourceSets.main.resources.srcDirs += [
            'griffon-app/resources',
            'griffon-app/i18n'
        ]
    }

    static String resolveApplicationName(Project project) {
        if (project.hasProperty('applicationName')) {
            return project.applicationName
        }
        return project.name
    }

    static void processResources(Project project, SourceSet sourceSet, GriffonExtension extension) {
        ProjectConfigurationExtension config = project.extensions.getByName('config')

        project.tasks."${sourceSet.processResourcesTaskName}" {
            filesMatching([
                '**/*.properties',
                '**/*.groovy',
                '**/*.html',
                '**/*.xml',
                '**/*.yml',
                '**/*.toml',
                '**/*.txt'
            ]) {
                expand([
                    'application_name'   : resolveApplicationName(project),
                    'application_version': project.version,
                    'griffon_version'    : extension.version.get(),
                    'build_date'         : config.buildInfo.buildDate,
                    'build_time'         : config.buildInfo.buildTime,
                    'build_revision'     : config.buildInfo.buildRevision
                ] + extension.applicationProperties.getOrElse([:]))
            }
        }
    }

    static void configureApplicationSettings(Project project, GriffonExtension extension) {
        TaskProvider<Copy> createDistributionFiles = project.tasks.register('createDistributionFiles', Copy, new Action<Copy>() {
            @Override
            void execute(Copy t) {
                t.group = 'Application'
                t.destinationDir = project.file("${project.buildDir}/assemble/distribution")
                t.from(project.file('src/media')) {
                    into 'resources'
                    include '*.icns', '*.ico'
                }
                t.from(project.file('.')) {
                    include 'README*', 'INSTALL*', 'LICENSE*'
                }
            }
        })

        project.applicationDistribution.from(createDistributionFiles)

        if (MACOSX) {
            List jvmArgs = project.applicationDefaultJvmArgs
            if (!(jvmArgs.find { it.startsWith('-Xdock:name=') })) {
                jvmArgs << "-Xdock:name=${resolveApplicationName(project)}"
            }
            if (!(jvmArgs.find { it.startsWith('-Xdock:icon=') })) {
                jvmArgs << ('-Xdock:icon=$APP_HOME/resources/' + extension.applicationIconName.orNull)
            }

            Task runTask = project.tasks.findByName('run')
            jvmArgs = (project.applicationDefaultJvmArgs + runTask.jvmArgs).unique()
            if (!(jvmArgs.find { it.startsWith('-Xdock:name=') })) {
                jvmArgs << "-Xdock:name=${resolveApplicationName(project)}"
            }

            String iconElem = jvmArgs.find { it.startsWith('-Xdock:icon=$APP_HOME/resources') }
            jvmArgs -= iconElem
            if (!(jvmArgs.find { it.startsWith('-Xdock:icon=') })) {
                File iconFile = project.file("src/media/${extension.applicationIconName.orNull}")
                if (!iconFile.exists()) iconFile = project.file('src/media/griffon.icns')
                jvmArgs << "-Xdock:icon=${iconFile.canonicalPath}"
            }
            runTask.jvmArgs = jvmArgs
        }
    }

    static void createDefaultDirectoryStructure(Project project, GriffonExtension extension, String sourceSetName) {
        if (extension.generateProjectStructure.get()) {
            def createIfNotExists = { File dir ->
                if (!dir.exists()) {
                    dir.mkdirs()
                }
            }
            project.sourceSets.main[sourceSetName].srcDirs.each(createIfNotExists)
            project.sourceSets.test[sourceSetName].srcDirs.each(createIfNotExists)
            project.sourceSets.main.resources.srcDirs.each(createIfNotExists)
            project.sourceSets.test.resources.srcDirs.each(createIfNotExists)
        }
    }

    static void validateToolkit(Project project, GriffonExtension extension) {
        if (extension.toolkit.orNull) {
            if (!GriffonExtension.TOOLKIT_NAMES.contains(extension.toolkit.orNull)) {
                throw new BuildException("The value of griffon.toolkit can only be one of ${GriffonExtension.TOOLKIT_NAMES.join(',')}",
                    new IllegalStateException(extension.toolkit.orNull))
            }
        }
    }

    private static void registerBuildListener(
        final Project project, final GriffonExtension extension) {
        project.gradle.addBuildListener(new BuildAdapter() {
            @Override
            void projectsEvaluated(Gradle gradle) {
                if (extension.includeDefaultRepositories.get()) {
                    project.repositories.mavenLocal()
                    // enable mavenCentral
                    project.repositories.mavenCentral()
                }

                configureDefaultSourceSets(project, 'java')
                createDefaultDirectoryStructure(project, extension, 'java')
                project.pluginManager.withPlugin('groovy') {
                    configureDefaultSourceSets(project, 'groovy')
                    createDefaultDirectoryStructure(project, extension, 'groovy')
                }

                // add default core dependencies
                appendDependency('core-impl')
                appendDependency('core-compile')
                appendDependency('core-test')

                validateToolkit(project, extension)
                project.pluginManager.withPlugin('application') { plugin ->
                    configureApplicationSettings(project, extension)
                }

                boolean groovyDependenciesEnabled = extension.includeGroovyDependencies.get() ||
                    (project.pluginManager.hasPlugin('groovy') && extension.includeGroovyDependencies.get())

                if (extension.toolkit.orNull) {
                    appendDependency(extension.toolkit.orNull)
                    appendDependency(extension.toolkit.orNull + '-test')
                    appendDependency((extension.toolkit.orNull == 'javafx' ? 'javafx' : ' beans') + '-compile')
                    maybeIncludeGroovyDependency(groovyDependenciesEnabled, extension.toolkit.orNull + '-groovy')
                }
                maybeIncludeGroovyDependency(groovyDependenciesEnabled, 'groovy')
                maybeIncludeGroovyDependency(groovyDependenciesEnabled, 'groovy-compile')

                GriffonPluginResolutionStrategy.applyTo(project)

                processResources(project, project.sourceSets.main, extension)
                processResources(project, project.sourceSets.test, extension)

                project.pluginManager.withPlugin('org.kordamp.gradle.source-stats', new Action<AppliedPlugin>() {
                    @Override
                    void execute(AppliedPlugin p) {
                        project.tasks.named('sourceStats', new Action<Task>() {
                            @Override
                            void execute(Task t) {
                                t.paths += [
                                    mvc       : [name: 'MVCGroups', path: 'griffon-app/mvcs'],
                                    model     : [name: 'Models', path: 'griffon-app/models'],
                                    view      : [name: 'Views', path: 'griffon-app/views'],
                                    controller: [name: 'Controllers', path: 'griffon-app/controllers'],
                                    service   : [name: 'Services', path: 'griffon-app/services'],
                                    config    : [name: 'Configuration', path: 'griffon-app/conf'],
                                    lifecycle : [name: 'Lifecycle', path: 'griffon-app/lifecycle']
                                ]
                            }
                        })
                    }
                })

                project.pluginManager.withPlugin('com.github.johnrengelman.shadow', new Action<AppliedPlugin>() {
                    @Override
                    void execute(AppliedPlugin p) {
                        project.tasks.shadowJar {
                            transform(com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer)
                            transform(com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer) {
                                path = 'META-INF/griffon'
                            }
                            transform(com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer) {
                                path = 'META-INF/types'
                            }
                        }
                    }
                })

                // update Griffon environment settings
                project.gradle.taskGraph.whenReady {
                    if (project.gradle.taskGraph.hasTask(':startScripts')) {
                        if (project.hasProperty('griffonEnv')) {
                            project.applicationDefaultJvmArgs << "-Dgriffon.env=${project.griffonEnv}"
                        } else {
                            project.applicationDefaultJvmArgs << '-Dgriffon.env=prod'
                        }
                    } else {
                        Task runTask = project.tasks.findByName('run')
                        if (runTask != null) {
                            if (project.hasProperty('griffonEnv')) {
                                project.applicationDefaultJvmArgs << "-Dgriffon.env=${project.griffonEnv}"
                                def jvmArgs = []
                                jvmArgs.addAll(runTask.jvmArgs)
                                jvmArgs << "-Dgriffon.env=${project.griffonEnv}"
                                runTask.jvmArgs = jvmArgs
                            } else {
                                runTask.jvmArgs << '-Dgriffon.env=dev'
                            }
                        }
                    }
                }
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
                String dependencyCoordinates = ['org.codehaus.griffon', 'griffon-' + artifactId, extension.version.orNull].join(':')

                if (artifactId.endsWith('-compile')) {
                    ['compileOnly', 'testCompileOnly'].each { conf ->
                        project.logger.info("Adding {} to '{}' configuration", dependencyCoordinates, conf)
                        project.dependencies.add(conf, dependencyCoordinates)
                    }
                    ['annotationProcessor', 'testAnnotationProcessor'].each { conf ->
                        project.logger.info("Adding {} to '{}' configuration", dependencyCoordinates, conf)
                        project.dependencies.add(conf, dependencyCoordinates)
                    }
                } else if (artifactId.endsWith('-test')) {
                    project.logger.info("Adding {} to 'testImplementation' configuration", dependencyCoordinates)
                    project.dependencies.add('testImplementation', dependencyCoordinates)
                } else {
                    project.logger.info("Adding {} to 'api' configuration", dependencyCoordinates)
                    project.dependencies.add('api', dependencyCoordinates)
                }
            }
        })
    }
}
