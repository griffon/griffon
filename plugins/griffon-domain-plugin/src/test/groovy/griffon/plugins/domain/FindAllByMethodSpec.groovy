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
class FindAllByMethodSpec extends PersistentMethodSpecSupport {
    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    void "Empty result when invoking findAllBy() on an empty dataset"() {
        expect:
        List args = [Person] + arguments
        !griffonDomainHandler.findAllBy(* args)

        where:
        arguments << [
            ['Name', ['Alice'] as Object[]],
            ['Name', ['Alice']],
            ['Name', ['Alice'] as Object[], [:]],
            ['Name', ['Alice'], [:]]
        ]
    }

    void "Full result when invoking findAllBy() on dataset"() {
        given:
        insertPeopleIntoDataset()

        when:
        def bobs = griffonDomainHandler.findAllBy(Person, 'Name', ['Bob'])
        def elmersons = griffonDomainHandler.findAllBy(Person, 'Lastname', ['Elmerson'])

        then:
        bobs.size() == 2
        bobs.lastname == ['Alison', 'Elmerson']
        elmersons.size() == 3
        elmersons.name == ['Alice', 'Bob', 'Elmer']
    }

    void "Full result when invoking findAllBy() with options on dataset"() {
        given:
        insertPeopleIntoDataset()

        when:
        def elmersons = griffonDomainHandler.findAllBy(Person, 'Lastname', ['Elmerson'], [order: 'desc'])

        then:
        elmersons.size() == 3
        elmersons.name == ['Elmer', 'Bob', 'Alice']
    }

    void "Full result when invoking findAllBy() on dataset with composite operation"() {
        given:
        insertPeopleIntoDataset()

        when:
        def bobs = griffonDomainHandler.findAllBy(Person, 'NameAndLastname', ['Bob', 'Elmerson'])

        then:
        bobs.size() == 1
        bobs.id == [4]
    }
}
