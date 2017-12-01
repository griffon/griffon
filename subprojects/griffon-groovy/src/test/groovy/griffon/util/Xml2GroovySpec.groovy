/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package griffon.util

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class Xml2GroovySpec extends Specification {
    private static final String APPLICATION_XML = 'build/resources/test/griffon/util/application.xml'
    private static final String BAD_APPLICATION_XML = 'build/resources/test/griffon/util/bad_application.xml'

    @Shared
    private String expected = getClass().getResourceAsStream('/griffon/util/application.groovy').text

    @Shared
    private String expected8 = getClass().getResourceAsStream('/griffon/util/application_jdk8.groovy').text

    def "Parse application.xml from #type"() {
        given:
        String output = Xml2Groovy.instance.parse(input)

        expect:
        expected.trim().normalize() == output.trim().normalize() || expected8.trim().normalize() == output.trim().normalize()

        where:
        type          | input
        'file'        | new File(APPLICATION_XML)
        'reader'      | new FileReader(new File(APPLICATION_XML))
        'inputStream' | new FileInputStream(new File(APPLICATION_XML))
        'uri'         | new File(APPLICATION_XML).toURI().toString()
        'gpathResult' | new XmlSlurper().parse(new File(APPLICATION_XML))
    }

    def "Parse application.xml from String"() {
        given:
        String output = Xml2Groovy.instance.parseText(new File(APPLICATION_XML).text)

        expect:
        expected.trim().normalize() == output.trim().normalize() || expected8.trim().normalize() == output.trim().normalize()
    }

    def "Bad application.xml from #type results in error"() {
        when:
        Xml2Groovy.instance.parse(input)

        then:
        thrown(IllegalArgumentException)

        where:
        type          | input
        'file'        | new File(BAD_APPLICATION_XML)
        'reader'      | new FileReader(new File(BAD_APPLICATION_XML))
        'inputStream' | new FileInputStream(new File(BAD_APPLICATION_XML))
        'uri'         | new File(BAD_APPLICATION_XML).toURI().toString()
    }

    def "Bad application.xml from string results in error"() {
        when:
        Xml2Groovy.instance.parseText(new File(BAD_APPLICATION_XML).text)

        then:
        thrown(IllegalArgumentException)
    }
}
