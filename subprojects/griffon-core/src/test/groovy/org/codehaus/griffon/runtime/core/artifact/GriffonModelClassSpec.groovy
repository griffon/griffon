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
package org.codehaus.griffon.runtime.core.artifact

import griffon.core.artifact.GriffonModelClass
import integration.SimpleModel
import integration.TestGriffonApplication
import spock.lang.Shared
import spock.lang.Specification

class GriffonModelClassSpec extends Specification {
    @Shared
    private GriffonModelClass griffonClass = new DefaultGriffonModelClass(new TestGriffonApplication(), SimpleModel)

    void 'Get and Set properties on model instance'() {
        given:
        SimpleModel model = new SimpleModel()
        model.application = new TestGriffonApplication()

        // expect:
        assert griffonClass.getModelPropertyValue(model, 'value1') == null
        assert griffonClass.getModelPropertyValue(model, 'value2') == null

        when:
        griffonClass.setModelPropertyValue(model, 'value1', 'value1')
        griffonClass.setModelPropertyValue(model, 'value2', 'value2')

        then:
        griffonClass.getModelPropertyValue(model, 'value1') == 'value1'
        griffonClass.getModelPropertyValue(model, 'value2') == 'value2'
    }
}
