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

@Unroll
class FindBySpec extends PersistentSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Empty result when invoking findBy() on an empty dataset"() {
        expect:
        !Employee.findBy(* arguments)

        where:
        arguments << [
            ['Name', ['Alice'] as Object[]],
            ['Name', ['Alice']],
            ['Name', ['Alice'] as Object[], [:]],
            ['Name', ['Alice'], [:]]
        ]
    }

    void "Full result when invoking findBy() on dataset (array version)"() {
        given:
        insertEmployeesIntoDataset()

        when:
        def bob = Employee.findBy('Name', ['Bob'] as Object[])
        def elmerson = Employee.findBy('Lastname', ['Elmerson'])

        then:
        bob.lastname == 'Alison'
        elmerson.name == 'Alice'
    }

    void "Full result when invoking findBy() on dataset (List version)"() {
        given:
        insertEmployeesIntoDataset()

        when:
        def bob = Employee.findBy('Name', ['Bob'])
        def elmerson = Employee.findBy('Lastname', ['Elmerson'])

        then:
        bob.lastname == 'Alison'
        elmerson.name == 'Alice'
    }

    void "Full result when invoking findBy() on dataset with composite operation"() {
        given:
        insertEmployeesIntoDataset()

        when:
        def bob = Employee.findBy('NameAndLastname', ['Bob', 'Elmerson'])

        then:
        bob.id == 4
    }
}
