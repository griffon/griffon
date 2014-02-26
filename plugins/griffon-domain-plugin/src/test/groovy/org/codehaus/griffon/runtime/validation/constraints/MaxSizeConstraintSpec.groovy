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
class MaxSizeConstraintSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'error')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "MaxSizeConstraint supports '#type' = #support"() {
        given:
        Constraint constraint = new MaxSizeConstraint()

        expect:
        constraint.supports(type) == support

        where:
        type                | support
        Object              | false
        String              | true
        List                | true
        new Object[0].class | true
    }

    void "Can set '#parameter' as parameter on MaxSizeConstraint"() {
        given:
        Constraint constraint = new MaxSizeConstraint()
        constraint.parameter = parameter

        expect:
        constraint.parameter == parameter

        where:
        parameter << [3]
    }

    void "Invalid parameter '#parameter' throws exception"() {
        given:
        Constraint constraint = new MaxSizeConstraint()

        when:
        constraint.parameter = parameter

        then:
        thrown(IllegalStateException)

        where:
        parameter << [new Object(), "1..10"]
    }

    void "Validate MaxSizeConstraint with parameter '#parameter' and value '#propertyValue' yields #success"() {
        given:
        Bean instance = new Bean()
        Constraint constraint = new MaxSizeConstraint()
        constraint.parameter = parameter
        constraint.constraintOwningClass = instance.class
        constraint.constraintPropertyName = propertyName
        Errors errors = new DefaultErrors(instance.class)

        when:
        constraint.validate(instance, propertyValue, errors)

        then:
        errors.hasErrors() == !success
        constraint.maxSize == parameter

        where:
        parameter | propertyName         | propertyValue         | success
        3         | 'sizeStringProperty' | ''                    | true
        3         | 'sizeListProperty'   | []                    | true
        3         | 'sizeArrayProperty'  | [] as int[]           | true
        3         | 'sizeStringProperty' | '1'                   | true
        3         | 'sizeListProperty'   | [1]                   | true
        3         | 'sizeArrayProperty'  | [1] as int[]          | true
        3         | 'sizeStringProperty' | '12'                  | true
        3         | 'sizeListProperty'   | [1, 2]                | true
        3         | 'sizeArrayProperty'  | [1, 2] as int[]       | true
        3         | 'sizeStringProperty' | '123'                 | true
        3         | 'sizeListProperty'   | [1, 2, 3]             | true
        3         | 'sizeArrayProperty'  | [1, 2, 3] as int[]    | true
        3         | 'sizeStringProperty' | '1234'                | false
        3         | 'sizeListProperty'   | [1, 2, 3, 4]          | false
        3         | 'sizeArrayProperty'  | [1, 2, 3, 4] as int[] | false
    }

    @Nonnull
    private List<Module> modules() {
        [new DefaultApplicationModule()]
    }
}
