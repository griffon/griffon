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
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Named

class CreateMethodSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    @Named('memory')
    protected GriffonDomainHandler griffonDomainHandler

    void "Create an empty Person instance"() {
        when:
        Person person = griffonDomainHandler.create(Person)

        then:
        !person.id
        !person.name
        !person.lastname
    }

    void "Create a Person instance with properties"() {
        when:
        Person person = griffonDomainHandler.create(Person, [name: 'Foo', lastname: 'Bar'])

        then:
        !person.id
        'Foo' == person.name
        'Bar' == person.lastname
    }
}
