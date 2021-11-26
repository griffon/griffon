/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class GenerateBomTaskTest {
    private File taskOutputDir = new File('build/test-output')

    private Project rootProject

    @Before
    void setupProject() {
        rootProject = project('scaffolding') {
            apply plugin: 'base'
            version = '1.0'
            group = 'com.acme'
        }

        def childConfig = {
            ext.publishJars = true
        }
        project('scaffolding-core', rootProject, childConfig)
        project('scaffolding-compile', rootProject, childConfig)
        project('scaffolding-test', rootProject, childConfig)
    }

    @After
    void cleanupProject() {
        deleteProjectDir()
    }

    @Test
    void 'GenerateBomTask creates a BOM'() {
        // given:
        Task task = rootProject.tasks.create(name: 'generateBom', type: GenerateBomTask) {
            outputDir = taskOutputDir
            additionalDependencies = ['commons-lang:commons-lang:2.6']
            pomConfig {
                description = 'description'
                url         = 'url'
                scm {
                    url = 'scm'
                }
                licenses {
                    license {
                        name         = 'The Apache Software License, Version 2.0'
                        url          = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                        distribution = 'repo'
                    }
                }
                developers {
                    developer {
                        id   = 'aalmiray'
                        name = 'Andres Almiray'
                    }
                }
            }
        }

        // when:
        task.generate()

        File expectedBom = new File('src/test/files/expected-bom.xml')
        File actualBom = new File(taskOutputDir, "${rootProject.name}-${rootProject.version}.pom")

        // then:
        assertXMLEqual(expectedBom.text, actualBom.text)
    }

    private Project project(String name, Closure<Project> configuration = null) {
        project(name, null, configuration)
    }

    private Project project(String name, Project parent, Closure<Project> configuration = null) {
        deleteProjectDir()
        ProjectBuilder builder = ProjectBuilder.builder().withName(name)
        if (parent) {
            builder = builder.withParent(parent)
        }
        Project project = builder.build()
        if (configuration) {
            project.with(configuration)
        }
        project
    }

    private void deleteProjectDir() {
        rootProject?.projectDir?.deleteDir()
    }
}
