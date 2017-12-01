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

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import com.google.inject.Injector
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull
import javax.inject.Inject

@Unroll
class ResourceBundleReaderSpec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject private Injector injector

    def 'Load bundle without active conditional blocks'() {
        given:
        ResourceBundleReader reader = new ResourceBundleReader()

        when:
        ResourceBundle p = reader.read(new ResourceBundleReaderSpec.MapResourceBundle())

        then:
        p.keySet().size() == 7
    }

    def 'Load bundle with environments = #environment'() {
        given:
        ResourceBundleReader reader = new ResourceBundleReader()
        reader.environment = environment

        when:
        ResourceBundle p = reader.read(new ResourceBundleReaderSpec.MapResourceBundle())

        then:
        p.keySet().size() == size
        p.getObject('key2') == value2

        where:
        environment   | size | value2
        'development' | 2    | 'development'
        'test'        | 3    | 'test'
        'production'  | 4    | ''
    }

    def 'Load bundle with environments = #environment and variables = #variable'() {
        given:
        ResourceBundleReader reader = new ResourceBundleReader()
        reader.registerConditionalBlock('environments', environment)
        reader.registerConditionalBlock('variables', variable)

        when:
        ResourceBundle p = reader.read(new ResourceBundleReaderSpec.MapResourceBundle())

        then:
        p.keySet().size() == size
        p.getObject('key2') == value2
        reader.conditionalBlockValues == ['environments': environment, 'variables': variable]

        where:
        environment   | variable | size | value2
        'development' | 'foo'    | 2    | 'development'
        'development' | 'bar'    | 2    | 'development'
        'test'        | 'foo'    | 2    | 'foo'
        'test'        | 'bar'    | 2    | 'test'
        'production'  | 'foo'    | 2    | ''
        'production'  | 'bar'    | 2    | ''
    }

    static class MapResourceBundle extends AbstractMapResourceBundle {
        @Override
        protected void initialize(@Nonnull Map<String, Object> entries) {
            entries['key1'] = 'value1'
            entries['environments.development.key2'] = 'development'
            entries['environments.test.key2'] = 'test'
            entries['environments.production.key2'] = 'test'
            entries['environments.test.variables.foo.key2'] = 'foo'
            entries['environments.production.key2'] = ''
            entries['environments.production.variables.foo.key2'] = ''
            entries['environments.production.variables.bar.key2'] = ''
        }
    }
    
    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
        }
    }
}
