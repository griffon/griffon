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
package org.codehaus.griffon.runtime.validation.constraints

import griffon.core.test.GriffonUnitRule
import griffon.plugins.validation.constraints.ConstrainedPropertyAssembler
import org.junit.Rule
import spock.lang.Specification
import griffon.types.IntRange

import javax.inject.Inject

class GroovyAwareConstrainedPropertyAssemblerSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private ConstrainedPropertyAssembler constrainedPropertyAssembler

    void "Size constraint accepts Groovy range"() {
        given:
        constrainedPropertyAssembler.targetClass = ConstrainedBean

        when:
        constrainedPropertyAssembler.assemble {
            sizeStringProperty(size: 0..5)
            sizeListProperty(size: 5..10)
        }
        Map constrainedProperties = constrainedPropertyAssembler.constrainedProperties

        then:
        constrainedProperties.size() == 2
        constrainedProperties.sizeStringProperty.size == new IntRange(0, 5)
        constrainedProperties.sizeListProperty.size == new IntRange(5, 10)
    }
}
