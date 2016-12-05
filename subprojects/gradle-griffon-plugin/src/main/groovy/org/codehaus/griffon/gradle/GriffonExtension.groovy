/*
 * Copyright 2008-2016 the original author or authors.
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

/**
 * @author Andres Almiray
 */
@CompileStatic
class GriffonExtension {
    static final List<String> TOOLKIT_NAMES = ['swing', 'javafx', 'pivot', 'lanterna']

    String version = '2.10.0-SNAPSHOT'

    String toolkit

    Boolean includeGroovyDependencies

    boolean disableDependencyResolution

    boolean includeDefaultRepositories = true

    boolean generateProjectStructure = true

    boolean applicationProject = true

    String applicationIconName = 'griffon.icns'

    Map applicationProperties = [:]

    GriffonExtension(Project project) {
    }
}
