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

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import com.google.inject.Inject
import griffon.core.Configuration
import griffon.core.editors.IntegerPropertyEditor
import griffon.core.editors.PropertyEditorResolver
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.inject.util.Providers.guicify
import static griffon.util.AnnotationUtils.named

@Unroll
class ConfigurationSpec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private Configuration configuration

    def 'Calling configuration.get(#key, #defaultValue) returns #expectedValue'() {
        expect:
        expectedValue == configuration.get(key, defaultValue)

        where:
        key            | defaultValue || expectedValue
        'key.string'   | null         || 'string'
        'key.string'   | 'foo'        || 'string'
        'key.int.type' | null         || 42
        'key.int.type' | 'foo'        || 42
        'key.foo'      | null         || null
        'key.foo'      | 'foo'        || 'foo'
    }


    def 'Calling configuration.getAs(#key) returns blindly casted #expectedValue'() {
        when:
        Integer value = configuration.getAs(key)

        then:
        value == expectedValue

        where:
        key            || expectedValue
        'key.int.type' || 42
    }

    def 'Calling configuration.getAsConverted(#key) returns converted #expectedValue'() {
        given:
        PropertyEditorResolver.clear()
        PropertyEditorResolver.registerEditor(Integer, IntegerPropertyEditor)

        when:
        Integer value = configuration.getConverted(key, Integer)

        then:
        value == expectedValue

        cleanup:
        PropertyEditorResolver.clear()

        where:
        key              || expectedValue
        'key.int.null'   || null
        'key.int.type'   || 42
        'key.int.string' || 21
    }

    def 'Calling configuration.getAsString(#key, #defaultValue) returns #expectedValue'() {
        expect:
        expectedValue == configuration.getAsString(key, defaultValue)

        where:
        key            | defaultValue || expectedValue
        'key.string'   | null         || 'string'
        'key.string'   | 'foo'        || 'string'
        'key.int.type' | null         || '42'
        'key.int.type' | 'foo'        || '42'
        'key.foo'      | null         || null
        'key.foo'      | 'foo'        || 'foo'
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ResourceBundle)
                .annotatedWith(named('applicationResourceBundle'))
                .to(MapResourceBundle)
            bind(ConfigurationDecoratorFactory)
                .to(DefaultConfigurationDecoratorFactory)
            bind(Configuration)
                .toProvider(guicify(new ResourceBundleConfigurationProvider()))
        }
    }
}
