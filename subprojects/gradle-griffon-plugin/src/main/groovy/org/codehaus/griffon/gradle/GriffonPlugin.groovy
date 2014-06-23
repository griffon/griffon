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
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.tooling.BuildException

class GriffonPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (!project.hasProperty('griffonVersion')) {
            throw new BuildException("You must define a project property named 'griffonVersion'",
                new IllegalStateException("Project property 'griffonVersion' is undefined"))
        }

        project.apply(plugin: 'idea')
        project.apply(plugin: 'java')
        if (!project.hasProperty('griffonPlugin') || !project.griffonPlugin) {
            project.apply(plugin: 'application')
        }

        // enable jcenter by default
        project.repositories.jcenter()

        // add compile time configurations
        project.configurations.create('compileOnly')
        project.configurations.create('testCompileOnly')

        // add default dependencies
        project.dependencies.add('compile', 'org.codehaus.griffon:griffon-core:' + project.griffonVersion)
        project.dependencies.add('compileOnly', 'org.codehaus.griffon:griffon-core-compile:' + project.griffonVersion)
        project.dependencies.add('testCompile', 'org.codehaus.griffon:griffon-core-test:' + project.griffonVersion)

        // wire up classpaths with compile time dependencies
        project.sourceSets.main.compileClasspath += [project.configurations.compileOnly]
        project.sourceSets.test.compileClasspath += [project.configurations.testCompileOnly]

        String sourceSetName = project.plugins.hasPlugin('groovy') ? 'groovy' : 'java'

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

        // adjust javadoc/groovydoc classpath
        project.javadoc.classpath += [project.configurations.compileOnly]
        if (sourceSetName == 'groovy') {
            project.groovydoc.classpath += [project.configurations.compileOnly]
        }

        // adjust Eclipse classpath, but only if EclipsePlugin is applied
        // TODO: Replace with plugins.withId('eclipse') for gradle 2
        project.plugins.withType(EclipsePlugin) {
            project.eclipse.classpath.plusConfigurations += [project.configurations.compileOnly]
            project.eclipse.classpath.plusConfigurations += [project.configurations.testCompileOnly]
        }

        // adjust IntelliJ classpath
        project.idea.module.scopes.PROVIDED.plus += [project.configurations.compileOnly]
        project.idea.module.scopes.PROVIDED.plus += [project.configurations.testCompileOnly]

        // define default exclusions
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
                    'griffon.version'    : project.griffonVersion
                ])
            }
        }

        // define default exclusions
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
                    'griffon.version'    : project.griffonVersion
                ])
            }
        }

        // create default directory structure
        project.gradle.taskGraph.whenReady {
            project.sourceSets.main[sourceSetName].srcDirs.each { it.mkdirs() }
            project.sourceSets.test[sourceSetName].srcDirs.each { it.mkdirs() }
            project.sourceSets.main.resources.srcDirs.each { it.mkdirs() }
            project.sourceSets.test.resources.srcDirs.each { it.mkdirs() }
        }
    }

}
