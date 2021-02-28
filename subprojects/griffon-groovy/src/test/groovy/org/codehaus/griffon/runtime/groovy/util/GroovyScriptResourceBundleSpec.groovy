/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.util

import griffon.util.groovy.ConfigReader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class GroovyScriptResourceBundleSpec extends Specification {
    void "Load bundle from #source"() {
        given:
        ConfigReader configReader = new ConfigReader(new DefaultApplicationClassLoader())

        when:
        ResourceBundle resourceBundle = new GroovyScriptResourceBundle(configReader, source)

        then:
        resourceBundle.getObject('groovy.key') == 'groovy'

        where:
        source << [
            GroovyBundle,
            new GroovyBundle(),
            "groovy.key = 'groovy'"
        ]
    }

    void "Querying for missing '#key' should result in an exception"() {
        given:
        ConfigReader configReader = new ConfigReader(new DefaultApplicationClassLoader())

        when:
        ResourceBundle resourceBundle = new GroovyScriptResourceBundle(configReader, source)
        resourceBundle.getObject(key)

        then:
        thrown(MissingResourceException)
        resourceBundle.source == GroovyBundle.name

        where:
        source       | key
        GroovyBundle | 'bogus'
        GroovyBundle | 'doesnt.exist'
    }

    void "Verify key order"() {
        given:
        ConfigReader configReader = new ConfigReader(new DefaultApplicationClassLoader())

        when:
        ResourceBundle resourceBundle = new GroovyScriptResourceBundle(configReader, new GroovyConfigBundle())

        then:
        resourceBundle.keys.collect { it } == resourceBundle.keySet().collect { it }
    }
}
