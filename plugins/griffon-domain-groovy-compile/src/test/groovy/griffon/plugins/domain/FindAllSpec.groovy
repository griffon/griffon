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
package griffon.plugins.domain

import griffon.core.test.GriffonUnitRule
import org.junit.Rule
import spock.lang.Unroll

import static griffon.plugins.domain.orm.Restrictions.eq

@Unroll
class FindAllSpec extends PersistentSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Empty result when invoking findAll() on an empty dataset"() {
        expect:
        !Employee.findAll(* arguments)

        where:
        arguments << [
            [[lastname: 'Alison']],
            [eq('lastname', 'Alison')],
            [[lastname: 'Alison'], [max: 1]],
            [eq('lastname', 'Alison'), [max: 1]]
        ]
    }

    void "Full result when invoking findAll() on dataset"() {
        given:
        insertEmployeesIntoDataset()

        when:
        def employees = Employee.findAll(* arguments)

        then:
        employees.size() == size
        employees[0].name == 'Alice'

        where:
        size | arguments
        2    | [[lastname: 'Alison']]
        2    | [eq('lastname', 'Alison')]
        1    | [[lastname: 'Alison'], [max: 1]]
        1    | [eq('lastname', 'Alison'), [max: 1]]
    }

    void "Result when invoking findAll() with example"() {
        given:
        insertEmployeesIntoDataset()

        when:
        Employee example = Employee.create(lastname: 'Alison')
        def employees = Employee.findAll(example)

        then:
        employees.size() == 2
        employees[0].name == 'Alice'
    }

    void "Result when invoking findAll() with example and options"() {
        given:
        insertEmployeesIntoDataset()

        when:
        Employee example = Employee.create(lastname: 'Alison')
        def employees = Employee.findAll(example, [max: 1])

        then:
        employees.size() == 1
        employees[0].name == 'Alice'
    }
}
