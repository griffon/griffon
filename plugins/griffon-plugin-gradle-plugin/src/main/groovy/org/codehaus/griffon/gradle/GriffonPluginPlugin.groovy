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

import enforcer.rules.BanDuplicateClasses
import enforcer.rules.DependencyConvergence
import enforcer.rules.EnforceBytecodeVersion
import enforcer.rules.RequireJavaVersion
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.kordamp.gradle.plugin.enforcer.BuildEnforcerPlugin
import org.kordamp.gradle.plugin.enforcer.api.BuildEnforcerExtension
import org.kordamp.gradle.plugin.inline.InlinePlugin
import org.kordamp.gradle.plugin.insight.InsightPlugin
import org.kordamp.gradle.plugin.settings.ProjectsExtension
import org.kordamp.gradle.plugin.settings.SettingsPlugin

/**
 * @author Andres Almiray
 */
class GriffonPluginPlugin implements Plugin<Settings> {
    void apply(Settings settings) {
        settings.plugins.apply(SettingsPlugin)
        settings.plugins.apply(InlinePlugin)
        settings.plugins.apply(InsightPlugin)
        settings.plugins.apply(BuildEnforcerPlugin)

        ExtraPropertiesExtension ext = settings.extensions.findByType(ExtraPropertiesExtension)
        String scompat = ext.has('sourceCompatibility') ? ext.get('sourceCompatibility') : ''
        String tcompat = ext.has('targetCompatibility') ? ext.get('targetCompatibility') : ''
        String javaVersion = scompat ?: tcompat

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

        settings.extensions.findByType(BuildEnforcerExtension).with {
            if (javaVersion) {
                rule(EnforceBytecodeVersion) { r ->
                    r.maxJdkVersion.set(javaVersion)
                }
                rule(RequireJavaVersion) { r ->
                    r.version.set(javaVersion)
                }
            }
            rule(DependencyConvergence)
            rule(BanDuplicateClasses) { r ->
                r.ignoreWhenIdentical = true
            }
        }

        settings.gradle.addBuildListener(new BuildAdapter() {
            @Override
            void projectsLoaded(Gradle gradle) {
                File versionFile = new File(gradle.rootProject.rootDir, 'VERSION')
                if (versionFile.exists()) {
                    String version = versionFile.text.trim()
                    gradle.rootProject.version = version
                    gradle.rootProject.childProjects.values().each { p -> p.version = version }
                }

                gradle.rootProject.pluginManager.apply(GriffonParentPomPlugin)

                gradle.rootProject.subprojects
                    .grep { it.projectDir.absolutePath.contains('examples') }
                    .each { p ->  p.pluginManager.apply(GriffonPlugin) }
            }
        })
    }
}
