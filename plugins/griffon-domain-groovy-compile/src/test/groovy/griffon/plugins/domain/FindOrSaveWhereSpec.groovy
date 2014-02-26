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
class FindOrSaveWhereSpec extends PersistentSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "New entity when invoking findOrSaveWhere() on an empty dataset"() {
        when:
        def bob = Employee.findOrSaveWhere(name: 'Bob', lastname: 'Elmerson')

        then:
        bob.id == 1
        bob.name == 'Bob'
        bob.lastname == 'Elmerson'
    }

    void "Found result when invoking findOrSaveWhere() on dataset"() {
        given:
        insertEmployeesIntoDataset()

        when:
        def bob1 = Employee.findOrSaveWhere(name: 'Bob', lastname: 'Elmerson')
        def bob2 = Employee.findOrSaveWhere(name: 'Bob', lastname: 'Bobson')

        then:
        bob1.id == 4
        bob2.id == 8
    }
}
