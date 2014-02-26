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
class LastSpec extends PersistentSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Empty result when invoking last() on an empty dataset"() {
        expect:
        !(arguments ? Employee.last(arguments) : Employee.last())

        where:
        arguments << [
            null,
            'lastname',
            [sort: 'lastname']
        ]
    }

    void "Full result when invoking last() on dataset"() {
        given:
        insertEmployeesIntoDataset()

        when:
        def employee = arguments ? Employee.last(arguments) : Employee.last()

        then:
        employee.name == name

        where:
        name    | id | arguments
        'Elmer' | 7  | null
        'Alice' | 2  | 'lastname'
        'Alice' | 2  | [sort: 'lastname']
    }
}
