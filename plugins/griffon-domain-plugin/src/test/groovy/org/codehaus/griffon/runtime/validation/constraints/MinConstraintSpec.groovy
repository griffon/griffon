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

import griffon.core.injection.Module
import griffon.core.test.GriffonUnitRule
import griffon.plugins.validation.Errors
import griffon.plugins.validation.constraints.Constraint
import org.codehaus.griffon.runtime.core.DefaultApplicationModule
import org.codehaus.griffon.runtime.validation.DefaultErrors
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class MinConstraintSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'error')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "MinConstraint supports '#type' = #support"() {
        given:
        Constraint constraint = new MinConstraint()

        expect:
        constraint.supports(type) == support

        where:
        type    | support
        Object  | false
        String  | true
        Integer | true
    }

    void "Can set '#parameter' as parameter on MinConstraint"() {
        given:
        Constraint constraint = new MinConstraint()
        constraint.constraintOwningClass = Bean
        constraint.constraintPropertyName = 'minNumberProperty'
        constraint.parameter = parameter

        expect:
        constraint.parameter == parameter

        where:
        parameter << [3]
    }

    void "Invalid parameter '#parameter' throws exception"() {
        given:
        Constraint constraint = new MinConstraint()
        constraint.constraintOwningClass = Bean
        constraint.constraintPropertyName = propertyName

        when:
        constraint.parameter = parameter

        then:
        thrown(IllegalStateException)

        where:
        parameter      | propertyName
        new Object()   | 'minNumberProperty'
        BigInteger.ONE | 'minNumberProperty'
        '3'            | 'minNumberProperty'
    }

    void "Validate MinConstraint with parameter '#parameter' and value '#propertyValue' yields #success"() {
        given:
        Bean instance = new Bean()
        Constraint constraint = new MinConstraint()
        constraint.constraintOwningClass = instance.class
        constraint.constraintPropertyName = propertyName
        constraint.parameter = parameter
        Errors errors = new DefaultErrors(instance.class)

        when:
        constraint.validate(instance, propertyValue, errors)

        then:
        errors.hasErrors() == !success
        constraint.minValue == parameter

        where:
        parameter | propertyName        | propertyValue | success
        3         | 'minNumberProperty' | null          | true
        3         | 'minNumberProperty' | 1             | false
        3         | 'minNumberProperty' | 2             | false
        3         | 'minNumberProperty' | 3             | true
        3         | 'minNumberProperty' | 4             | true
        '123'     | 'minStringProperty' | null          | true
        '123'     | 'minStringProperty' | ''            | true
        '123'     | 'minStringProperty' | '1'           | false
        '123'     | 'minStringProperty' | '12'          | false
        '123'     | 'minStringProperty' | '123'         | true
        '123'     | 'minStringProperty' | '1234'        | true
    }

    @Nonnull
    private List<Module> modules() {
        [new DefaultApplicationModule()]
    }
}
