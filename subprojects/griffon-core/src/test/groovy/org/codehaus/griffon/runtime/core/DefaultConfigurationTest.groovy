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
package org.codehaus.griffon.runtime.core

import griffon.core.Configuration
import griffon.util.AbstractMapResourceBundle
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class DefaultConfigurationTest extends Specification {
    def 'Calling configuration.#method(#key, #defaultValue) gives #value as result'() {
        given:
        ResourceBundle bundle = new MapResourceBundle()
        Configuration configuration = new DefaultConfiguration(bundle)

        expect:
        value == (defaultValue != null ? configuration."$method"(key, defaultValue) : configuration."$method"(key))

        where:
        method         | key                   | defaultValue      | value
        'get'          | 'key.string'          | 'STRING'          | 'string'
        'getAt'        | 'key.string'          | 'STRING'          | 'string'
        'getAsString'  | 'key.string2'         | null              | null
        'getAsString'  | 'key.string'          | 'STRING'          | 'string'
        'getAsBoolean' | 'key.boolean2'        | null              | false
        'getAsBoolean' | 'key.boolean'         | false             | true
        'getAsBoolean' | 'key.boolean.string'  | false             | true
        'getAsInt'     | 'key.int2'            | null              | 0
        'getAsInt'     | 'key.int'             | -1                | 42
        'getAsInt'     | 'key.int.string'      | -1                | 21
        'getAsLong'    | 'key.long2'           | null              | 0
        'getAsLong'    | 'key.long'            | -1L               | 64L
        'getAsLong'    | 'key.long.string'     | -1L               | 32L
        'getAsFloat'   | 'key.float2'          | null              | 0f
        'getAsFloat'   | 'key.float'           | -1.0f             | 3.1416f
        'getAsFloat'   | 'key.float.string'    | -1.0f             | 6.2832f
        'getAsDouble'  | 'key.double2'         | null              | 0d
        'getAsDouble'  | 'key.double'          | -1.0d             | 3.1416d
        'getAsDouble'  | 'key.double.string'   | -1.0d             | 6.2832d
        'getAsString'  | 'key.string.unknown'  | 'UNKNOWN'         | 'UNKNOWN'
        'getAsBoolean' | 'key.boolean.unknown' | true              | true
        'getAsInt'     | 'key.int.unknown'     | Integer.MAX_VALUE | Integer.MAX_VALUE
        'getAsLong'    | 'key.long.unknown'    | Long.MAX_VALUE    | Long.MAX_VALUE
        'getAsFloat'   | 'key.float.unknown'   | Float.MAX_VALUE   | Float.MAX_VALUE
        'getAsDouble'  | 'key.double.unknown'  | Double.MAX_VALUE  | Double.MAX_VALUE
    }

    class MapResourceBundle extends AbstractMapResourceBundle {
        @Override
        protected void initialize(@Nonnull Map<String, Object> entries) {
            entries['key.string'] = 'string'
            entries['key.boolean'] = true
            entries['key.boolean.string'] = 'true'
            entries['key.int'] = 42
            entries['key.int.string'] = '21'
            entries['key.long'] = 64L
            entries['key.long.string'] = '32'
            entries['key.float'] = 3.1416f
            entries['key.float.string'] = '6.2832'
            entries['key.double'] = 3.1416d
            entries['key.double.string'] = '6.2832'
        }
    }

    def 'Can resolve nested keys from sample application configuration'() {
        given:
        ResourceBundle bundle = new AppResourceBundle()
        Configuration configuration = new DefaultConfiguration(bundle)

        expect:

        'Griffon' == configuration['application.title']
        'sample.SampleView' == configuration['mvcGroups.sample.view']
    }

    class AppResourceBundle extends AbstractMapResourceBundle {
        @Override
        protected void initialize(@Nonnull Map<String, Object> entries) {
            entries['application'] = [
                title: 'Griffon',
                autoShutdown: true,
                startupGroups: ['sample']
            ]
            entries['mvcGroups'] = [
                sample: [
                    model: 'sample.SampleModel',
                    view: 'sample.SampleView',
                    controller: 'sample.SampleController'
                ]
            ]
        }
    }
}