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

class DeleteMethodSpec extends PersistentMethodSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Delete operation does not affect empty dataset"() {
        given:
        Person person = griffonDomainHandler.create(Person, [name: 'Alice', lastname: 'Elmerson'])
        person.id = 2L

        expect:
        griffonDomainHandler.delete(person)
        !griffonDomainHandler.list(Person)
    }

    void "Delete operation removes an instance from dataset"() {
        given:
        insertPeopleIntoDataset()
        Person person = griffonDomainHandler.get(Person, 2L)

        when:
        Person deleted = griffonDomainHandler.delete(person)

        then:
        6 == griffonDomainHandler.count(Person)
        person.id == deleted.id
    }
}
