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
import griffon.types.IntRange as IRange
import org.codehaus.griffon.runtime.core.DefaultApplicationModule
import org.codehaus.griffon.runtime.validation.DefaultErrors
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class SizeConstraintSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'error')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "SizeConstraint supports '#type' = #support"() {
        given:
        Constraint constraint = new SizeConstraint()

        expect:
        constraint.supports(type) == support

        where:
        type                | support
        Object              | false
        String              | true
        List                | true
        new Object[0].class | true
    }

    void "Can set '#parameter' as parameter on SizeConstraint"() {
        given:
        Constraint constraint = new SizeConstraint()
        constraint.parameter = parameter

        expect:
        constraint.parameter == parameter

        where:
        parameter << [new IRange(3, 5)]
    }

    void "Invalid parameter '#parameter' throws exception"() {
        given:
        Constraint constraint = new SizeConstraint()

        when:
        constraint.parameter = parameter

        then:
        thrown(IllegalStateException)

        where:
        parameter << [1, new Object(), "1..10"]
    }

    void "Validate SizeConstraint with parameter '#parameter' and value '#propertyValue' yields #success"() {
        given:
        Bean instance = new Bean()
        Constraint constraint = new SizeConstraint()
        constraint.parameter = parameter
        constraint.constraintOwningClass = instance.class
        constraint.constraintPropertyName = propertyName
        Errors errors = new DefaultErrors(instance.class)

        when:
        constraint.validate(instance, propertyValue, errors)

        then:
        errors.hasErrors() == !success
        constraint.range == parameter

        where:
        parameter        | propertyName         | propertyValue               | success
        new IRange(3, 5) | 'sizeStringProperty' | '123'                       | true
        new IRange(3, 5) | 'sizeListProperty'   | [1, 2, 3]                   | true
        new IRange(3, 5) | 'sizeArrayProperty'  | [1, 2, 3] as int[]          | true
        new IRange(3, 5) | 'sizeStringProperty' | '1234'                      | true
        new IRange(3, 5) | 'sizeListProperty'   | [1, 2, 3, 4]                | true
        new IRange(3, 5) | 'sizeArrayProperty'  | [1, 2, 3, 4] as int[]       | true
        new IRange(3, 5) | 'sizeStringProperty' | '12345'                     | true
        new IRange(3, 5) | 'sizeListProperty'   | [1, 2, 3, 4, 5]             | true
        new IRange(3, 5) | 'sizeArrayProperty'  | [1, 2, 3, 4, 5] as int[]    | true
        new IRange(3, 5) | 'sizeStringProperty' | '12'                        | false
        new IRange(3, 5) | 'sizeListProperty'   | [1, 2]                      | false
        new IRange(3, 5) | 'sizeArrayProperty'  | [1, 2] as int[]             | false
        new IRange(3, 5) | 'sizeStringProperty' | '123456'                    | false
        new IRange(3, 5) | 'sizeListProperty'   | [1, 2, 3, 4, 5, 6]          | false
        new IRange(3, 5) | 'sizeArrayProperty'  | [1, 2, 3, 4, 5, 6] as int[] | false
    }

    @Nonnull
    private List<Module> modules() {
        [new DefaultApplicationModule()]
    }
}
