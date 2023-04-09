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

import groovy.transform.CompileDynamic
import org.gradle.BuildAdapter
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.kordamp.gradle.plugin.base.ProjectConfigurationExtension
import org.kordamp.gradle.plugin.bom.BomPlugin
import org.kordamp.gradle.plugin.profiles.ProfilesExtension
import org.kordamp.gradle.plugin.project.java.JavaProjectPlugin

import java.text.SimpleDateFormat

/**
 * @author Andres Almiray
 */
class GriffonParentPomPlugin implements Plugin<Project> {
    void apply(Project project) {
        JavaProjectPlugin.applyIfMissing(project)
        BomPlugin.applyIfMissing(project)

        if (!project.hasProperty('sonatypeReleaseRepositoryUrl')) project.ext.sonatypeReleaseRepositoryUrl = 'https://s01.oss.sonatype.org/service/local/'
        if (!project.hasProperty('sonatypeSnapshotRepositoryUrl')) project.ext.sonatypeSnapshotRepositoryUrl = 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
        if (!project.hasProperty('sonatypeUsername')) project.ext.sonatypeUsername = '**undefined**'
        if (!project.hasProperty('sonatypePassword')) project.ext.sonatypePassword = '**undefined**'

        String guideProjectName = project.rootProject.name - '-plugin' + '-guide'
        Set<Project> exampleProjects = project.rootProject.subprojects.grep { it.projectDir.absolutePath.contains('examples') }

        GriffonExtension griffonExtension = project.extensions.create('griffon', GriffonExtension, project)
        GriffonPlugin.applyDefaultVersions(project, griffonExtension)

        project.extensions.findByType(ProjectConfigurationExtension).with {
            release = (project.rootProject.project.findProperty('release') ?: false).toBoolean()

            info {
                name          = project.findProperty('projectDescription') ?: project.name
                description   = project.findProperty('projectDescription') ?: project.name
                inceptionYear = new SimpleDateFormat('YYYY').format(new Date())
                vendor        = 'Griffon'

                links {
                    website = "https://github.com/griffon-plugins/${project.rootProject.name}"
                    issueTracker = "https://github.com/griffon-plugins/${project.rootProject.name}/issues"
                    scm = "https://github.com/griffon-plugins/${project.rootProject.name}.git"
                }

                scm {
                    url = "https://github.com/griffon-plugins/${project.rootProject.name}"
                    connection = "scm:git:https://github.com/griffon-plugins/${project.rootProject.name}.git"
                    developerConnection = "scm:git:git@github.com:griffon-plugins/${project.rootProject.name}.git"
                }

                people {
                    person {
                        id = 'aalmiray'
                        name = 'Andres Almiray'
                        url = 'http://andresalmiray.com/'
                        roles = ['developer']
                        properties = [
                            twitter: 'aalmiray',
                            github : 'aalmiray'
                        ]
                    }
                }

                credentials {
                    sonatype {
                        username = project.sonatypeUsername
                        password = project.sonatypePassword
                    }
                }

                repositories {
                    repository {
                        name = 'localRelease'
                        url = "${project.rootProject.buildDir}/repos/local/release"
                    }
                    repository {
                        name = 'localSnapshot'
                        url = "${project.rootProject.buildDir}/repos/local/snapshot"
                    }
                }
            }

            coverage {
                jacoco {
                    toolVersion = project.jacocoVersion
                }
            }

            licensing {
                licenses {
                    license {
                        id = 'Apache-2.0'
                    }
                }
                mappings = [
                    gdsl: 'SLASHSTAR_STYLE',
                    dsld: 'SLASHSTAR_STYLE'
                ]
            }

            docs {
                javadoc {
                    excludes = ['**/*.html', 'META-INF/**']
                }
                sourceXref {
                    inputEncoding = 'UTF-8'
                }
            }

            publishing {
                releasesRepository  = 'localRelease'
                snapshotsRepository = 'localSnapshot'
            }

            bom {
                exclude(guideProjectName)
                exampleProjects.each { p -> exclude(p.name) }
            }

            dependencyManagement {
                dependency("org.kordamp.gipsy:gipsy:${project.jipsyVersion}")
                dependency('jipsy') {
                    groupId    = 'org.kordamp.jipsy'
                    artifactId = 'jipsy-processor'
                    version    = project.jipsyVersion
                    modules    = ['jipsy-util']
                }
                dependency("junit:junit:${project.junitVersion}")
                dependency('junit5') {
                    groupId    = 'org.junit.jupiter'
                    artifactId = 'junit-jupiter-api'
                    version    = project.junit5Version
                    modules    = [
                        'junit-jupiter-params',
                        'junit-jupiter-engine'
                    ]
                }
                dependency('junit5v') {
                    groupId    = 'org.junit.vintage'
                    artifactId = 'junit-vintage-engine'
                    version    = project.junit5Version
                }
            }
        }

        project.extensions.findByType(ProfilesExtension).with {
            profile('release') {
                activation {
                    property {
                        key = 'full-release'
                    }
                }
                action {
                    config {
                        release = true
                    }
                }
            }

            profile('sign') {
                activation {
                    property {
                        key = 'full-release'
                    }
                }
                action {
                    println 'Artifact signing is turned ON'

                    config {
                        publishing {
                            signing {
                                enabled = true
                                keyId = System.getenv()['GPG_KEY_ID']
                                secretKey = System.getenv()['GPG_SECRET_KEY']
                                password = System.getenv()['GPG_PASSPHRASE']
                            }
                        }
                    }
                }
            }

            profile('stage') {
                activation {
                    property {
                        key = 'full-release'
                    }
                }
                action {
                    println 'Staging to Sonatype is turned ON'

                    apply plugin: 'io.github.gradle-nexus.publish-plugin'

                    nexusPublishing {
                        repositories {
                            sonatype {
                                username = project.ext.sonatypeUsername
                                password = project.ext.sonatypePassword
                                nexusUrl = uri(project.ext.sonatypeReleaseRepositoryUrl)
                                snapshotRepositoryUrl = uri(project.ext.sonatypeSnapshotRepositoryUrl)
                            }
                        }
                    }
                }
            }
        }

        project.allprojects {
            repositories {
                mavenCentral()
                mavenCentral()
                mavenLocal()
            }

            normalization {
                runtimeClasspath {
                    ignore('/META-INF/MANIFEST.MF')
                }
            }

            dependencyUpdates.resolutionStrategy {
                componentSelection { rules ->
                    rules.all { selection ->
                        boolean rejected = ['alpha', 'beta', 'rc', 'cr'].any { qualifier ->
                            selection.candidate.version ==~ /(?i).*[.-]${qualifier}[.\d-]*.*/
                        }
                        if (rejected) {
                            selection.reject('Release candidate')
                        }
                    }
                }
            }
        }

        project.allprojects { Project p ->
            def scompat = project.project.findProperty('sourceCompatibility')
            def tcompat = project.project.findProperty('targetCompatibility')

            p.tasks.withType(JavaCompile) { JavaCompile c ->
                if (scompat) c.sourceCompatibility = scompat
                if (tcompat) c.targetCompatibility = tcompat
            }
            p.tasks.withType(GroovyCompile) { GroovyCompile c ->
                if (scompat) c.sourceCompatibility = scompat
                if (tcompat) c.targetCompatibility = tcompat
            }
        }

        project.subprojects { sub ->
            sub.pluginManager.withPlugin('java-base', new Action<AppliedPlugin>() {
                @Override
                void execute(AppliedPlugin ap) {
                    sub.dependencies {
                        compileOnly sub.config.dependencyManagement.gav('gipsy')
                        compileOnly sub.config.dependencyManagement.gav('jipsy')
                        annotationProcessor sub.config.dependencyManagement.gav('jipsy')
                        testAnnotationProcessor sub.config.dependencyManagement.gav('jipsy')

                        annotationProcessor "org.codehaus.griffon:griffon-core-compile:${sub.griffonVersion}"
                        testAnnotationProcessor "org.codehaus.griffon:griffon-core-compile:${sub.griffonVersion}"

                        testImplementation sub.config.dependencyManagement.gav('junit5', 'junit-jupiter-api')
                        testImplementation sub.config.dependencyManagement.gav('junit5', 'junit-jupiter-params')
                        testRuntimeOnly sub.config.dependencyManagement.gav('junit5', 'junit-jupiter-engine')
                        testRuntimeOnly(sub.config.dependencyManagement.gav('junit5v', 'junit-vintage-engine')) {
                            exclude group: 'junit', module: 'junit'
                        }
                        testImplementation(sub.config.dependencyManagement.gav('junit')) {
                            exclude group: 'org.hamcrest', module: 'hamcrest-core'
                        }
                        testImplementation("org.apache.groovy:groovy-all:${sub.groovyVersion}") {
                            exclude group: 'junit', module: 'junit'
                        }
                        testImplementation("org.spockframework:spock-core:${sub.spockVersion}") {
                            exclude group: 'junit', module: 'junit'
                            exclude group: 'org.codehaus.groovy', module: 'groovy-all'
                        }

                        compileOnly "org.codehaus.griffon:griffon-core-compile:${sub.griffonVersion}"
                        compileOnly "org.codehaus.griffon:griffon-beans-compile:${sub.griffonVersion}"

                        api "org.codehaus.griffon:griffon-core-impl:${sub.griffonVersion}"

                        testCompileOnly "org.codehaus.griffon:griffon-core-compile:${sub.griffonVersion}"
                        testCompileOnly "org.codehaus.griffon:griffon-beans-compile:${sub.griffonVersion}"
                        testCompileOnly "org.codehaus.griffon:griffon-groovy-compile:${sub.griffonVersion}"
                        testCompileOnly sub.config.dependencyManagement.gav('jipsy')
                        testCompileOnly sub.config.dependencyManagement.gav('gipsy')

                        testImplementation "org.codehaus.griffon:griffon-core-test:${sub.griffonVersion}"
                        testImplementation("org.codehaus.griffon:griffon-groovy:${sub.griffonVersion}") {
                            exclude group: 'org.codehaus.groovy', module: 'groovy-all'
                        }

                        testRuntimeOnly "org.codehaus.griffon:griffon-guice:${sub.griffonVersion}"
                        testRuntimeOnly "org.slf4j:slf4j-simple:${sub.slf4jVersion}"
                    }

                    sub.test {
                        useJUnitPlatform()
                    }

                    sub.jar {
                        manifest {
                            attributes('Automatic-Module-Name': (sub.group - 'org.codehaus.') + '.' + (sub.name - 'griffon-').replace('-', '.'))
                        }
                    }
                }
            })
        }

        project.projects {
            subprojects {
                dir('subprojects') {
                    config {
                        info {
                            name        = project.findProperty('projectDescription') ?: project.name
                            description = project.findProperty('projectDescription') ?: project.name
                        }
                    }

                    compileGroovy.enabled = false
                }

                dir('examples') {
                    config {
                        docs {
                            javadoc {
                                enabled = false
                            }
                        }

                        publishing {
                            enabled = false
                        }
                    }

                    dependencies {
                        compileOnly("org.apache.groovy:groovy-all:${project.groovyVersion}") {
                            exclude group: 'junit', module: 'junit'
                        }
                    }
                }

                path(':' + guideProjectName) {
                    ext.projectDependencies = []

                    asciidoctor {
                        baseDirFollowsSourceDir()
                        attributes = [
                            toc                    : 'left',
                            doctype                : 'book',
                            icons                  : 'font',
                            encoding               : 'utf-8',
                            sectlink               : true,
                            sectanchors            : true,
                            numbered               : true,
                            linkattrs              : true,
                            imagesdir              : 'images',
                            linkcss                : true,
                            stylesheet             : 'css/style.css',
                            'source-highlighter'   : 'coderay',
                            'coderay-linenums-mode': 'table',
                            'griffon-version'      : rootProject.griffonVersion
                        ]

                        sources {
                            include 'index.adoc'
                        }

                        resources {
                            from file('src/resources')
                        }
                    }

                    guide {
                        sourceHtmlDir = 'api-src'
                    }
                }
            }
        }

        project.afterEvaluate {
            project.tasks.named('aggregateJavadoc', Javadoc,
                new Action<Javadoc>() {
                    @Override
                    @CompileDynamic
                    void execute(Javadoc t) {try{
                        t.classpath = t.project.findProject(':' + guideProjectName).ext.projectDependencies.collect { projectName ->
                            [t.project.findProject(projectName).sourceSets.main.output,
                             t.project.findProject(projectName).configurations.compile,
                             t.project.findProject(projectName).configurations.compileOnly]
                        }.flatten().sum() as FileCollection

                        t.options.overview    = t.project.findProject(':' + guideProjectName).file('src/javadoc/overview.html')
                        t.options.links       = ['https://www.slf4j.org/apidocs/',
                                               'https://junit.org/junit4/javadoc/latest/',
                                               'https://javax-inject.github.io/javax-inject/api/',
                                               "https://griffon-framework.org/guide/${t.project.griffonVersion}/api/".toString()]

                        t.doLast { task ->
                            t.project.copy {
                                into task.destinationDir
                                from t.project.findProject(':' + guideProjectName).file('src/javadoc/resources/img/griffon.ico'),
                                    t.project.findProject(':' + guideProjectName).file('src/javadoc/resources/css/stylesheet.css')
                            }
                            t.project.copy {
                                into t.project.file("${task.destinationDir}/resources")
                                from t.project.findProject(':' + guideProjectName).file('src/javadoc/resources/img/')
                            }
                        }}catch(Exception x){x.printStackTrace()}
                    }
                })
        }

        project.gradle.addBuildListener(new BuildAdapter() {
            @Override
            void projectsEvaluated(Gradle gradle) {
                for (Project p : project.subprojects) {
                    if (p.name.endsWith('-guide')) continue
                    GriffonPlugin.processResources(p, p.sourceSets.main, griffonExtension)
                    GriffonPlugin.processResources(p, p.sourceSets.test, griffonExtension)
                }
            }
        })
    }
}
