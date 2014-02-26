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

class ListMethodSpec extends PersistentMethodSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Empty result when invoking list() on an empty dataset"() {
        when:
        Collection<Person> people = griffonDomainHandler.list(Person)

        then:
        people.empty
    }

    void "Empty result when invoking list(Map) on an empty dataset"() {
        when:
        Collection<Person> people = griffonDomainHandler.list(Person, [:])

        then:
        people.empty
    }

    void "Full result when invoking list() on dataset"() {
        given:
        insertPeopleIntoDataset()

        when:
        def people = griffonDomainHandler.list(Person)

        then:
        people.size() == 7
        people.id == [1, 2, 3, 4, 5, 6, 7]
        people.name == ['Alice', 'Alice', 'Bob', 'Bob', 'Charles', 'David', 'Elmer']
    }

    void "Partial result when invoking list(Map) on dataset"() {
        given:
        insertPeopleIntoDataset()

        when:
        def people = griffonDomainHandler.list(Person, [max: 3])

        then:
        people.size() == 3
        people.id == [1, 2, 3]
        people.name == ['Alice', 'Alice', 'Bob']
    }
}
