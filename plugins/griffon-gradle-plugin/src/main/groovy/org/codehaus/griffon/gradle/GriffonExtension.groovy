/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

/**
 * @author Andres Almiray
 */
@CompileStatic
class GriffonExtension {
    static final List<String> TOOLKIT_NAMES = ['swing', 'javafx']

    final Property<String> version

    final Property<String> toolkit

    final Property<Boolean> includeGroovyDependencies

    final Property<Boolean> disableDependencyResolution

    final Property<Boolean> includeDefaultRepositories

    final Property<Boolean> generateProjectStructure

    final Property<String> applicationIconName

    final MapProperty<String, String> applicationProperties

    GriffonExtension(Project project) {
        version = project.objects.property(String).convention('3.0.0-SNAPSHOT')
        toolkit = project.objects.property(String).convention(project.providers.provider {
            project.pluginManager.hasPlugin('org.openjfx.javafxplugin') ? 'javafx' : null
        })
        includeGroovyDependencies = project.objects.property(Boolean).convention(false)
        disableDependencyResolution = project.objects.property(Boolean).convention(false)
        includeDefaultRepositories = project.objects.property(Boolean).convention(true)
        generateProjectStructure = project.objects.property(Boolean).convention(true)
        applicationIconName = project.objects.property(String).convention('griffon.icns')
        applicationProperties = project.objects.mapProperty(String, String).convention(new LinkedHashMap<String, String>())
    }
}
