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
class FindSpec extends PersistentSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Empty result when invoking find() on an empty dataset"() {
        expect:
        !Employee.find(* arguments)

        where:
        arguments << [
            [[lastname: 'Alison']],
            [eq('lastname', 'Alison')]
        ]
    }

    void "Full result when invoking find() on dataset"() {
        given:
        insertEmployeesIntoDataset()

        when:
        Employee alice = Employee.find(* arguments)

        then:
        alice.name == 'Alice'

        where:
        arguments << [
            [[lastname: 'Alison']],
            [eq('lastname', 'Alison')]
        ]
    }

    void "Result when invoking find() with example"() {
        given:
        insertEmployeesIntoDataset()

        when:
        Employee example = Employee.create(lastname: 'Alison')
        def alice = Employee.find(example)

        then:
        alice.name == 'Alice'
    }
}
