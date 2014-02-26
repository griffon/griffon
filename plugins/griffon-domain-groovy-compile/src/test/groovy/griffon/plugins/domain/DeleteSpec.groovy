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

class DeleteSpec extends PersistentSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Delete operation does not affect empty dataset"() {
        given:
        Employee employee = Employee.create(name: 'Alice', lastname: 'Elmerson')
        employee.id = 2L

        expect:
        employee.delete()
        !Employee.list()
    }

    void "Delete operation removes an instance from dataset"() {
        given:
        insertEmployeesIntoDataset()
        Employee employee = Employee.get(2L)

        when:
        Employee deleted = employee.delete()

        then:
        6 == Employee.count()
        employee.id == deleted.id
    }
}
