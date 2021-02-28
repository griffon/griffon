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
package org.codehaus.griffon.gradle

import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.kordamp.gradle.plugin.settings.ProjectsExtension
import org.kordamp.gradle.plugin.settings.SettingsPlugin

/**
 * @author Andres Almiray
 */
class GriffonPluginPlugin implements Plugin<Settings> {
    void apply(Settings settings) {
        settings.plugins.apply(SettingsPlugin)

        settings.extensions.findByType(ProjectsExtension).with {
            layout = 'two-level'
            directories = ['docs', 'subprojects', 'examples']

            plugins {
                all {
                    id 'idea'
                }
                dir('subprojects') {
                    id 'java-library'
                    id 'groovy'
                    id 'org.jonnyzzz.java9c'
                }
                dir('docs') {
                    id 'org.kordamp.gradle.guide'
                    id 'org.ajoberstar.git-publish'
                }
                dir('examples') {
                    id 'groovy'
                    id 'java-library'
                }
            }
        }

        settings.gradle.addBuildListener(new BuildAdapter() {
            @Override
            void projectsLoaded(Gradle gradle) {
                gradle.rootProject.pluginManager.apply(GriffonParentPomPlugin)
                gradle.rootProject.subprojects
                    .grep { it.projectDir.absolutePath.contains('examples') }
                    .each { p ->  p.pluginManager.apply(GriffonPlugin) }
            }
        })
    }
}
