/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core.configuration

import griffon.annotations.core.Nonnull
import griffon.core.Configuration
import griffon.util.AbstractMapResourceBundle
import org.codehaus.griffon.runtime.core.MapResourceBundle
import org.kordamp.jsr377.converter.DefaultConverterRegistry
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ConfigurationDecoratorSpec extends Specification {
    def 'Calling configuration.#method(#key, #defaultValue) gives #value as result'() {
        given:
        ResourceBundle bundle = new MapResourceBundle()
        Configuration configuration = new ResourceBundleConfiguration(new DefaultConverterRegistry(), bundle)
        configuration = new ConfigurationDecorator(configuration)

        expect:
        value == (defaultValue != null ? configuration."$method"(key, defaultValue) : configuration."$method"(key))

        where:
        method         | key                   | defaultValue      | value
        'get'          | 'key.string'          | 'STRING'          | 'string'
        'getAt'        | 'key.string'          | 'STRING'          | 'string'
        'getAsString'  | 'key.string2'         | null              | null
        'getAsString'  | 'key.string'          | 'STRING'          | 'string'
        'getAsBoolean' | 'key.boolean2'        | null              | false
        'getAsBoolean' | 'key.boolean.type'    | false             | true
        'getAsBoolean' | 'key.boolean.string'  | false             | true
        'getAsInt'     | 'key.int2'            | null              | 0
        'getAsInt'     | 'key.int.type'        | -1                | 42
        'getAsInt'     | 'key.int.string'      | -1                | 21
        'getAsLong'    | 'key.long2'           | null              | 0
        'getAsLong'    | 'key.long.type'       | -1L               | 64L
        'getAsLong'    | 'key.long.string'     | -1L               | 32L
        'getAsFloat'   | 'key.float2'          | null              | 0f
        'getAsFloat'   | 'key.float.type'      | -1.0f             | 3.1416f
        'getAsFloat'   | 'key.float.string'    | -1.0f             | 6.2832f
        'getAsDouble'  | 'key.double2'         | null              | 0d
        'getAsDouble'  | 'key.double.type'     | -1.0d             | 3.1416d
        'getAsDouble'  | 'key.double.string'   | -1.0d             | 6.2832d
        'getAsString'  | 'key.string.unknown'  | 'UNKNOWN'         | 'UNKNOWN'
        'getAsBoolean' | 'key.boolean.unknown' | true              | true
        'getAsInt'     | 'key.int.unknown'     | Integer.MAX_VALUE | Integer.MAX_VALUE
        'getAsLong'    | 'key.long.unknown'    | Long.MAX_VALUE    | Long.MAX_VALUE
        'getAsFloat'   | 'key.float.unknown'   | Float.MAX_VALUE   | Float.MAX_VALUE
        'getAsDouble'  | 'key.double.unknown'  | Double.MAX_VALUE  | Double.MAX_VALUE
    }

    def 'Can resolve nested keys from sample application configuration (Properties)'() {
        given:
        ResourceBundle bundle = new AppResourceBundle()
        Configuration configuration = new ResourceBundleConfiguration(new DefaultConverterRegistry(), bundle)
        configuration = new ConfigurationDecorator(configuration)
        Properties props = configuration.asProperties()

        expect:
        configuration.containsKey('application.title')
        'Griffon' == configuration['application.title']
        'sample.SampleView' == configuration['mvcGroups.sample.view']
        props['application.title'] == configuration['application.title']
    }

    def 'Can resolve nested keys from sample application configuration (ResourceBundle)'() {
        given:
        ResourceBundle bundle = new AppResourceBundle()
        Configuration configuration = new ResourceBundleConfiguration(new DefaultConverterRegistry(), bundle)
        configuration = new ConfigurationDecorator(configuration)
        ResourceBundle asBundle = configuration.asResourceBundle()

        expect:
        configuration.containsKey('application.title')
        'Griffon' == configuration['application.title']
        'sample.SampleView' == configuration['mvcGroups.sample.view']
        asBundle.getString('application.title') == configuration['application.title']
    }

    static class AppResourceBundle extends AbstractMapResourceBundle {
        @Override
        protected void initialize(@Nonnull Map<String, Object> entries) {
            entries['application'] = [
                title        : 'Griffon',
                autoShutdown : true,
                startupGroups: ['sample']
            ]
            entries['mvcGroups'] = [
                sample: [
                    model     : 'sample.SampleModel',
                    view      : 'sample.SampleView',
                    controller: 'sample.SampleController'
                ]
            ]
        }
    }
}