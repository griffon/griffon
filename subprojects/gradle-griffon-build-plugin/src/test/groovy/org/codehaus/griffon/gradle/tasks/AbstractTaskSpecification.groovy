/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractTaskSpecification extends Specification {
    @Shared
    Project rootProject

    Project project(String name, Closure<Project> configuration = null) {
        project(name, null, configuration)
    }

    Project project(String name, Project parent, Closure<Project> configuration = null) {
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

    def cleanupSpec() {
        deleteProjectDir()
    }

    def deleteProjectDir() {
        rootProject?.projectDir?.deleteDir()
    }
}
