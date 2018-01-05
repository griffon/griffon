/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.gradle.tasks

import groovy.transform.Canonical
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

/**
 * @author Andres Almiray
 */
class GenerateBomTask extends DefaultTask {
    static final TASK_NAME = 'generateBom'

    @OutputDirectory File outputDir
    @Input List<String> additionalDependencies = []
    @Input PomConfig pomConfig

    GenerateBomTask() {
        outputDir = project.file("${project.buildDir.path}/bom")
    }

    @OutputFile
    File getOutputFile() {
        return new File(getOutputDir(), "${project.name}-${project.version}.pom")
    }

    void pomConfig(Closure cls) {
        pomConfig = new PomConfig()
        ConfigureUtil.configure(cls, pomConfig)
    }

    @TaskAction
    void generate() {
        outputDir.mkdirs()

        // force evaluation of 'publishJars' property
        Set includedProjects = project.subprojects.grep {
            "${it.publishJars}" == 'true'
        }

        MarkupBuilder pom = new MarkupBuilder(new FileWriter(getOutputFile()))
        pom.mkp.xmlDeclaration(version: '1.0', encoding: 'UTF-8')
        pom.project(xmlns: 'http://maven.apache.org/POM/4.0.0',
            'xsi:schemaLocation': 'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd',
            'xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance') {
            modelVersion('4.0.0')
            groupId(project.group)
            artifactId(project.name)
            version(project.version)
            packaging('pom')
            name(project.name)
            mkp.yieldUnescaped("\n  <description>${pomConfig.description}</description>")
            url(pomConfig.url)
            scm {
                url(pomConfig.scm.url)
            }
            licenses {
                pomConfig.licenses.each { LicenseConfig lic ->
                    license {
                        name(lic.name)
                        url(lic.url)
                        distribution(lic.distribution)
                    }
                }
            }
            developers {
                pomConfig.developers.each { DeveloperConfig dev ->
                    developer {
                        id(dev.id)
                        name(dev.name)
                    }
                }
            }
            dependencyManagement {
                dependencies {
                    includedProjects.each { prj ->
                        String projectName = prj.name
                        String scopeName = 'compile'
                        boolean opt = prj.hasProperty('optional') ? prj.optional : false
                        if (projectName.endsWith('-compile')) scopeName = 'provided'
                        if (projectName.endsWith('-test')) scopeName = 'test'

                        dependency {
                            groupId('${project.groupId}')
                            artifactId(projectName)
                            version('${project.version}')
                            scope(scopeName)
                            if (opt) optional(true)
                        }
                    }
                    additionalDependencies.each { String dep ->
                        def (groupName, artifactName, versionNum) = dep.split(':')
                        String scopeName = 'compile'
                        if (artifactName.endsWith('-compile')) scopeName = 'provided'
                        if (artifactName.endsWith('-test')) scopeName = 'test'

                        dependency {
                            groupId(groupName)
                            artifactId(artifactName)
                            version(versionNum)
                            scope(scopeName)
                        }
                    }
                }
            }
        }
    }

    @Canonical
    static class PomConfig implements Serializable, Configurable<PomConfig> {
        String description
        String url

        ScmConfig scm = new ScmConfig()
        private LicensesConfig lics = new LicensesConfig()
        private DevelopersConfig devs = new DevelopersConfig()

        List<LicenseConfig> getLicenses() {
            lics.licenses
        }

        List<DeveloperConfig> getDevelopers() {
            devs.developers
        }

        void scm(Closure cls) {
            ConfigureUtil.configure(cls, scm)
        }

        void licenses(Closure cls) {
            ConfigureUtil.configure(cls, lics)
        }

        void developers(Closure cls) {
            ConfigureUtil.configure(cls, devs)
        }

        @Override
        PomConfig configure(Closure closure) {
            this.with(closure)
            this
        }
    }

    @Canonical
    static class ScmConfig implements Serializable, Configurable<ScmConfig> {
        String url

        @Override
        ScmConfig configure(Closure closure) {
            this.with(closure)
            this
        }
    }

    @Canonical
    static class LicensesConfig implements Serializable, Configurable<LicensesConfig> {
        List<LicenseConfig> licenses = []

        void license(Closure cls) {
            LicenseConfig lic = new LicenseConfig()
            licenses << lic
            ConfigureUtil.configure(cls, lic)
        }

        @Override
        LicensesConfig configure(Closure closure) {
            this.with(closure)
            this
        }
    }

    @Canonical
    static class LicenseConfig implements Serializable, Configurable<LicenseConfig> {
        String name
        String url
        String distribution

        @Override
        LicenseConfig configure(Closure closure) {
            this.with(closure)
            this
        }
    }

    @Canonical
    static class DevelopersConfig implements Serializable, Configurable<DevelopersConfig> {
        List<DeveloperConfig> developers = []

        void developer(Closure cls) {
            DeveloperConfig dev = new DeveloperConfig()
            developers << dev
            ConfigureUtil.configure(cls, dev)
        }

        @Override
        DevelopersConfig configure(Closure closure) {
            this.with(closure)
            this
        }
    }

    @Canonical
    static class DeveloperConfig implements Serializable, Configurable<DeveloperConfig> {
        String id
        String name

        @Override
        DeveloperConfig configure(Closure closure) {
            this.with(closure)
            this
        }
    }
}
