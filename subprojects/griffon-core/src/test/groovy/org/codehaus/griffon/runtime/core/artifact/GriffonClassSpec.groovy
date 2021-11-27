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
package org.codehaus.griffon.runtime.core.artifact

import griffon.core.artifact.GriffonClass
import integration.SimpleModel
import integration.TestGriffonApplication
import spock.lang.Shared
import spock.lang.Specification

class GriffonClassSpec extends Specification {
    @Shared
    private static GriffonClass griffonClass = new DefaultGriffonModelClass(new TestGriffonApplication(), SimpleModel)

    void 'Verify properties'() {
        expect:
        griffonClass.artifactType == 'model'
        griffonClass.clazz == SimpleModel
        griffonClass.fullName == 'integration.SimpleModel'
        griffonClass.logicalPropertyName == 'simple'
        griffonClass.name == 'Simple'
        griffonClass.naturalName == 'Simple Model'
        griffonClass.packageName == 'integration'
        griffonClass.propertyName == 'simpleModel'
        griffonClass.shortName == 'SimpleModel'
    }
}
