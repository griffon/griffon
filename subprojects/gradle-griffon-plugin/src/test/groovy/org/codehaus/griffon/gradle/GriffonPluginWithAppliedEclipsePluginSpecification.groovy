/*
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
package org.codehaus.griffon.gradle

import spock.lang.Ignore
import spock.lang.Unroll

@Ignore
@Unroll
class GriffonPluginWithAppliedEclipsePluginSpecification extends AbstractPluginSpecification {
    def setupSpec() {
        project {
            apply plugin: 'org.codehaus.griffon.griffon'
            apply plugin: 'eclipse'
            project.griffon {
                version = '2.10.0-SNAPSHOT'
            }
        }
    }

    def 'eclipse classpath plusConfigurations contains #configuration'() {
        expect:
        project.eclipse.classpath.plusConfigurations.contains configuration

        where:
        configuration                          | _
        project.configurations.compileOnly     | _
        project.configurations.testCompileOnly | _
    }
}
