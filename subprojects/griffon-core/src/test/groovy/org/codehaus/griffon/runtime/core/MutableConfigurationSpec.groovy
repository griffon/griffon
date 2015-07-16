/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.core

import griffon.core.Configuration
import griffon.core.MutableConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MutableConfigurationSpec extends Specification {
    def 'Calling configuration.set(#key, #value) stores #value'() {
        given:
        Configuration configuration = new MyMutableConfiguration()

        expect:
        configuration.containsKey(key)

        when:
        configuration.set(key, value)

        then:
        configuration.get(key) == value

        where:
        key                  || value
        'key.string'         || 'foo'
        'key.boolean.type'   || 'foo'
        'key.boolean.string' || 'foo'
        'key.int.type'       || 'foo'
        'key.int.string'     || 'foo'
        'key.long.type'      || 'foo'
        'key.long.string'    || 'foo'
        'key.float.type'     || 'foo'
        'key.float.string'   || 'foo'
        'key.double.type'    || 'foo'
        'key.double.string'  || 'foo'
    }

    def 'Calling configuration.set(#key, #value) stores #value (recall with getAsString)'() {
        given:
        Configuration configuration = new MyMutableConfiguration()

        expect:
        configuration.containsKey(key)

        when:
        configuration.set(key, value)

        then:
        configuration.getAsString(key) == value

        where:
        key                  || value
        'key.string'         || 'foo'
        'key.boolean.type'   || 'foo'
        'key.boolean.string' || 'foo'
        'key.int.type'       || 'foo'
        'key.int.string'     || 'foo'
        'key.long.type'      || 'foo'
        'key.long.string'    || 'foo'
        'key.float.type'     || 'foo'
        'key.float.string'   || 'foo'
        'key.double.type'    || 'foo'
        'key.double.string'  || 'foo'
    }

    def 'Calling configuration.remove(#key) removes the key'() {
        given:
        MutableConfiguration configuration = new MyMutableConfiguration()
        configuration.set('key.foo', 'foo')

        expect:
        configuration.containsKey(key)
        null != configuration.get(key)

        when:
        configuration.remove(key)

        then:
        !configuration.containsKey(key)

        when:
        def value = configuration.get(key)

        then:
        null == value

        when:
        value = configuration.remove(key)

        then:
        null == value

        where:
        key                  || _
        'key.string'         || _
        'key.boolean.type'   || _
        'key.boolean.string' || _
        'key.int.type'       || _
        'key.int.string'     || _
        'key.long.type'      || _
        'key.long.string'    || _
        'key.float.type'     || _
        'key.float.string'   || _
        'key.double.type'    || _
        'key.double.string'  || _
        'key.foo'            || _
    }

    def 'Transforming configuration into flat Map and retrieving key #key'() {
        given:
        MutableConfiguration configuration = new MyMutableConfiguration()
        configuration.set('key.foo', 'foo')

        when:
        Map map = configuration.asFlatMap()

        then:
        null != map[key]

        when:
        configuration.remove(key)
        map = configuration.asFlatMap()

        then:
        !map[key]

        where:
        key                  || _
        'key.string'         || _
        'key.boolean.type'   || _
        'key.boolean.string' || _
        'key.int.type'       || _
        'key.int.string'     || _
        'key.long.type'      || _
        'key.long.string'    || _
        'key.float.type'     || _
        'key.float.string'   || _
        'key.double.type'    || _
        'key.double.string'  || _
        'key.foo'            || _
    }

    def 'Transforming configuration into Properties and retrieving key #key'() {
        given:
        MutableConfiguration configuration = new MyMutableConfiguration()
        configuration.set('key.foo', 'foo')

        when:
        Properties props = configuration.asProperties()

        then:
        null != props.getProperty(key)

        where:
        key                  || _
        'key.string'         || _
        'key.boolean.type'   || _
        'key.boolean.string' || _
        'key.int.type'       || _
        'key.int.string'     || _
        'key.long.type'      || _
        'key.long.string'    || _
        'key.float.type'     || _
        'key.float.string'   || _
        'key.double.type'    || _
        'key.double.string'  || _
        'key.foo'            || _
    }

    def 'Transforming configuration into ResourceBundle and retrieving key #key'() {
        given:
        MutableConfiguration configuration = new MyMutableConfiguration()
        configuration.set('key.foo', 'foo')

        when:
        ResourceBundle bundle = configuration.asResourceBundle()

        then:
        null != bundle.getObject(key)

        where:
        key                  || _
        'key.string'         || _
        'key.boolean.type'   || _
        'key.boolean.string' || _
        'key.int.type'       || _
        'key.int.string'     || _
        'key.long.type'      || _
        'key.long.string'    || _
        'key.float.type'     || _
        'key.float.string'   || _
        'key.double.type'    || _
        'key.double.string'  || _
        'key.foo'            || _
    }

    static class MyMutableConfiguration extends DelegatingMutableConfiguration {
        MyMutableConfiguration() {
            super(new DefaultConfiguration(new MapResourceBundle()))
        }
    }
}
