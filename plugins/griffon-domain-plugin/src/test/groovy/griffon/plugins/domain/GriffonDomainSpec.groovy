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

import griffon.core.artifact.ArtifactManager
import griffon.core.test.GriffonUnitRule
import griffon.plugins.validation.constraints.ConstrainedProperty
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Named

class GriffonDomainSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private ArtifactManager artifactManager

    @Inject
    @Named('memory')
    private GriffonDomainHandler griffonDomainHandler

    void "Verify domain class Person has constrained properties"() {
        given:
        Person person = artifactManager.newInstance(Person)

        when:
        Map<String, ConstrainedProperty> constrainedProperties = person.constrainedProperties()

        then:
        constrainedProperties.size() == 3
        constrainedProperties.keySet() == (['id', 'name', 'lastname'] as Set)
        griffonDomainHandler == person.griffonClass.domainHandler
    }
}
