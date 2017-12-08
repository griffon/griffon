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
package org.codehaus.griffon.runtime.core.resources

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import com.google.inject.Inject
import griffon.core.editors.IntegerPropertyEditor
import griffon.core.editors.PropertyEditorResolver
import griffon.core.resources.NoSuchResourceException
import griffon.core.resources.ResourceResolver
import griffon.util.AbstractMapResourceBundle
import org.junit.Rule
import org.junit.Test

import javax.annotation.Nonnull
import javax.application.converter.ConverterRegistry
import javax.inject.Singleton

class CustomResourceResolverTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private ResourceResolver resourceResolver

    @Test
    void getAllDefinedMessages() {
        assert resourceResolver.resolveResource('key.string') == 'string'
        assert resourceResolver.resolveResource('key.boolean.string') == 'true'
        assert resourceResolver.resolveResource('key.int.string') == '21'
        assert resourceResolver.resolveResource('key.long.string') == '32'
        assert resourceResolver.resolveResource('key.float.string') == '6.2832'
        assert resourceResolver.resolveResource('key.double.string') == '6.2832'

        assert resourceResolver.resolveResource('key.string', []) == 'string'
        assert resourceResolver.resolveResource('key.boolean.string', []) == 'true'
        assert resourceResolver.resolveResource('key.int.string', []) == '21'
        assert resourceResolver.resolveResource('key.long.string', []) == '32'
        assert resourceResolver.resolveResource('key.float.string', []) == '6.2832'
        assert resourceResolver.resolveResource('key.double.string', []) == '6.2832'

        assert resourceResolver.resolveResource('key.string', [] as Object[]) == 'string'
        assert resourceResolver.resolveResource('key.boolean.string', [] as Object[]) == 'true'
        assert resourceResolver.resolveResource('key.int.string', [] as Object[]) == '21'
        assert resourceResolver.resolveResource('key.long.string', [] as Object[]) == '32'
        assert resourceResolver.resolveResource('key.float.string', [] as Object[]) == '6.2832'
        assert resourceResolver.resolveResource('key.double.string', [] as Object[]) == '6.2832'

        assert resourceResolver.resolveResource('key.string', [], Locale.default) == 'string'
        assert resourceResolver.resolveResource('key.boolean.string', [], Locale.default) == 'true'
        assert resourceResolver.resolveResource('key.int.string', [], Locale.default) == '21'
        assert resourceResolver.resolveResource('key.long.string', [], Locale.default) == '32'
        assert resourceResolver.resolveResource('key.float.string', [], Locale.default) == '6.2832'
        assert resourceResolver.resolveResource('key.double.string', [], Locale.default) == '6.2832'

        assert resourceResolver.resolveResource('key.string', [] as Object[], Locale.default) == 'string'
        assert resourceResolver.resolveResource('key.boolean.string', [] as Object[], Locale.default) == 'true'
        assert resourceResolver.resolveResource('key.int.string', [] as Object[], Locale.default) == '21'
        assert resourceResolver.resolveResource('key.long.string', [] as Object[], Locale.default) == '32'
        assert resourceResolver.resolveResource('key.float.string', [] as Object[], Locale.default) == '6.2832'
        assert resourceResolver.resolveResource('key.double.string', [] as Object[], Locale.default) == '6.2832'
    }

    @Test
    void exerciseAllExceptionThrowingMethods() {
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus')
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus', [])
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus', [:])
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus', [] as Object[])
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus', Locale.default)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus', [], Locale.default)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus', [:], Locale.default)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bogus', [] as Object[], Locale.default)
        }

        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', Integer)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', [], Integer)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', [:], Integer)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', [] as Object[], Integer)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', Locale.default, Integer)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', [], Locale.default, Integer)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', [:], Locale.default, Integer)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResourceConverted('bogus', [] as Object[], Locale.default, Integer)
        }
    }

    @Test
    void exerciseAllMethodsWithDefaultMessage() {
        String defaultMessage = 'default'
        assert defaultMessage == resourceResolver.resolveResource('bogus', defaultMessage)
        assert defaultMessage == resourceResolver.resolveResource('bogus', [], defaultMessage)
        assert defaultMessage == resourceResolver.resolveResource('bogus', [:], defaultMessage)
        assert defaultMessage == resourceResolver.resolveResource('bogus', [] as Object[], defaultMessage)
        assert defaultMessage == resourceResolver.resolveResource('bogus', Locale.default, defaultMessage)
        assert defaultMessage == resourceResolver.resolveResource('bogus', [], Locale.default, defaultMessage)
        assert defaultMessage == resourceResolver.resolveResource('bogus', [:], Locale.default, defaultMessage)
        assert defaultMessage == resourceResolver.resolveResource('bogus', [] as Object[], Locale.default, defaultMessage)

        assert 'bogus' == resourceResolver.resolveResource('bogus', (String) null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [], (String) null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [:], (String) null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [] as Object[], (String) null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', Locale.default, null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [], Locale.default, null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [:], Locale.default, null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [] as Object[], Locale.default, null)
    }

    @Test
    void exerciseAllMethodsWithConverter() {
        PropertyEditorResolver.clear()
        PropertyEditorResolver.registerEditor(Integer, IntegerPropertyEditor)

        try {
            Integer defaultValue = 21
            assert 21 == resourceResolver.resolveResourceConverted('bogus', defaultValue, Integer)
            assert 21 == resourceResolver.resolveResourceConverted('bogus', [], defaultValue, Integer)
            assert 21 == resourceResolver.resolveResourceConverted('bogus', [:], defaultValue, Integer)
            assert 21 == resourceResolver.resolveResourceConverted('bogus', [] as Object[], defaultValue, Integer)
            assert 21 == resourceResolver.resolveResourceConverted('bogus', Locale.default, defaultValue, Integer)
            assert 21 == resourceResolver.resolveResourceConverted('bogus', [], Locale.default, defaultValue, Integer)
            assert 21 == resourceResolver.resolveResourceConverted('bogus', [:], Locale.default, defaultValue, Integer)
            assert 21 == resourceResolver.resolveResourceConverted('bogus', [] as Object[], Locale.default, defaultValue, Integer)

            assert 42 == resourceResolver.resolveResourceConverted('integer', Integer)
            assert 42 == resourceResolver.resolveResourceConverted('integer', [], Integer)
            assert 42 == resourceResolver.resolveResourceConverted('integer', [:], Integer)
            assert 42 == resourceResolver.resolveResourceConverted('integer', [] as Object[], Integer)
            assert 42 == resourceResolver.resolveResourceConverted('integer', Locale.default, Integer)
            assert 42 == resourceResolver.resolveResourceConverted('integer', [], Locale.default, Integer)
            assert 42 == resourceResolver.resolveResourceConverted('integer', [:], Locale.default, Integer)
            assert 42 == resourceResolver.resolveResourceConverted('integer', [] as Object[], Locale.default, Integer)
        } finally {
            PropertyEditorResolver.clear()
        }
    }

    private String shouldFail(Class clazz, Closure code) {
        return GroovyAssert.shouldFail(clazz, code).getMessage()
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ResourceResolver).to(CustomResourceResolver).in(Singleton)
        }
    }

    static class MapResourceBundle extends AbstractMapResourceBundle {
        @Override
        protected void initialize(@Nonnull Map<String, Object> entries) {
            entries['key.string'] = 'string'
            entries['key.boolean.string'] = 'true'
            entries['key.int.string'] = '21'
            entries['key.long.string'] = '32'
            entries['key.float.string'] = '6.2832'
            entries['key.double.string'] = '6.2832'
            entries['reference.key'] = '@[referenced.key]'
            entries['referenced.key'] = "what's up doc?"
            entries['not.a.reference.key1'] = '@[NOT'
            entries['not.a.reference.key2'] = 'NOT]'
            entries['not.a.reference.key3'] = '@[]'
            entries['integer'] = '42'
        }
    }

    static class CustomResourceResolver extends AbstractResourceResolver {
        private ResourceBundle bundle = new MapResourceBundle()

        protected CustomResourceResolver(@Nonnull ConverterRegistry converterRegistry) {
            super(converterRegistry)
        }

        @Nonnull
        @Override
        protected Object doResolveResourceValue(
            @Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
            try {
                return bundle.getObject(key)
            } catch (MissingResourceException mre) {
                throw new NoSuchResourceException(key, locale)
            }
        }
    }
}