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

class ListOrderBySpec extends PersistentSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Empty result when invoking listOrderBy() on an empty dataset"() {
        when:
        Collection<Employee> employees = Employee.listOrderBy('id')

        then:
        employees.empty
    }

    void "Empty result when invoking listOrderBy(Map) on an empty dataset"() {
        when:
        Collection<Employee> employees = Employee.listOrderBy('id', [order: 'desc'])

        then:
        employees.empty
    }

    void "Full result when invoking listOrderBy() on dataset"() {
        given:
        insertEmployeesIntoDataset()

        when:
        def employees = Employee.listOrderBy('id', [order: 'desc'])

        then:
        employees.size() == 7
        employees.id == [7, 6, 5, 4, 3, 2, 1]
        employees.name == ['Elmer', 'David', 'Charles', 'Bob', 'Bob', 'Alice', 'Alice']
    }
}
