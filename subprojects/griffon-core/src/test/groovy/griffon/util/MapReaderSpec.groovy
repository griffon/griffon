/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

@Unroll
class MapReaderSpec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject private Injector injector

    @Shared private Map map = [
        key1                                  : 'value1',
        'environments.development.key2'       : 'development',
        'environments.test.key2'              : 'test',
        'environments.test.variables.foo.key2': 'foo'
    ]

    def 'Load map without active conditional blocks'() {
        given:
        MapReader reader = new MapReader()

        when:
        Map p = reader.read(map)

        then:
        p.size() == 4
    }

    def 'Load map with environments = #environment'() {
        given:
        MapReader reader = new MapReader()
        reader.environment = environment

        when:
        Map p = reader.read(map)

        then:
        p.size() == size
        p.key2 == value2

        where:
        environment   | size | value2
        'development' | 2    | 'development'
        'test'        | 3    | 'test'
        'production'  | 1    | null
    }

    def 'Load map with environments = #environment and variables = #variable'() {
        given:
        MapReader reader = new MapReader()
        reader.registerConditionalBlock('environments', environment)
        reader.registerConditionalBlock('variables', variable)

        when:
        Map p = reader.read(map)

        then:
        p.size() == size
        p.key2 == value2
        reader.conditionalBlockValues == ['environments': environment, 'variables': variable]

        where:
        environment   | variable | size | value2
        'development' | 'foo'    | 2    | 'development'
        'development' | 'bar'    | 2    | 'development'
        'test'        | 'foo'    | 2    | 'foo'
        'test'        | 'bar'    | 2    | 'test'
        'production'  | 'foo'    | 1    | null
        'production'  | 'bar'    | 1    | null
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
        }
    }
}
