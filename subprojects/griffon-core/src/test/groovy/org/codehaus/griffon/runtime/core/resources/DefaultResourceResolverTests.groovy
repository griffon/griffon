/*
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
package org.codehaus.griffon.runtime.core.resources

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.CallableWithArgs
import griffon.core.editors.IntegerPropertyEditor
import griffon.core.editors.PropertyEditorResolver
import griffon.core.resources.NoSuchResourceException
import griffon.core.resources.ResourceHandler
import griffon.core.resources.ResourceResolver
import griffon.util.CompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.junit.Rule
import org.junit.Test

import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.inject.Inject
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify

class DefaultResourceResolverTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private CompositeResourceBundleBuilder bundleBuilder

    @Inject
    private ResourceResolver resourceResolver

    @Test
    void resolveAllFormatsByProperties() {
        //assert resourceResolver.basename == 'org.codehaus.griffon.runtime.core.resources.props'

        String quote = resourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'] as Object[])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'] as Object[])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'] as Object[], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', ['Sparta'] as Object[], 'not found :(')
        assert quote == 'not found :('

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = resourceResolver.resolveResource('famous.quote.index')
        assert quote == 'This is {0}!'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'], Locale.default)
        assert quote == 'This is Sparta!'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'] as Object[], Locale.default)
        assert quote == 'This is Sparta!'
        quote = resourceResolver.resolveResource('famous.quote.index', Locale.default)
        assert quote == 'This is {0}!'
        quote = resourceResolver.resolveResource('famous.quote.map', [location: 'Sparta'], Locale.default)
        assert quote == 'This is Sparta!'
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
    void exerciseAllMethodsWithDefaultValue() {
        Object defaultValue = new Object()
        assert defaultValue == resourceResolver.resolveResource('bogus', defaultValue)
        assert defaultValue == resourceResolver.resolveResource('bogus', [], defaultValue)
        assert defaultValue == resourceResolver.resolveResource('bogus', [:], defaultValue)
        assert defaultValue == resourceResolver.resolveResource('bogus', [] as Object[], defaultValue)
        assert defaultValue == resourceResolver.resolveResource('bogus', Locale.default, defaultValue)
        assert defaultValue == resourceResolver.resolveResource('bogus', [], Locale.default, defaultValue)
        assert defaultValue == resourceResolver.resolveResource('bogus', [:], Locale.default, defaultValue)
        assert defaultValue == resourceResolver.resolveResource('bogus', [] as Object[], Locale.default, defaultValue)

        assert 'bogus' == resourceResolver.resolveResource('bogus', (Object) null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [], (Object) null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [:], (Object) null)
        assert 'bogus' == resourceResolver.resolveResource('bogus', [] as Object[], null)
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

    @Test
    void exerciseFormatResource() {
        assert 'key = value' == resourceResolver.formatResource('key = {0}', ['value'])
        assert 'key = value' == resourceResolver.formatResource('key = {0}', ['value'] as Object[])
        assert 'key = value' == resourceResolver.formatResource('key = {:value}', [value: 'value'])
        assert 'key = {:value}' == resourceResolver.formatResource('key = {:value}', [value: null])
    }

    @Test
    void resolveResourceValueWithReferencedKey() {
        assert "what's up doc?" == resourceResolver.resolveResourceValue('reference.key', Locale.default)
        assert '@[NOT' == resourceResolver.resolveResourceValue('not.a.reference.key1', Locale.default)
        assert 'NOT]' == resourceResolver.resolveResourceValue('not.a.reference.key2', Locale.default)
        assert '@[]' == resourceResolver.resolveResourceValue('not.a.reference.key3', Locale.default)
    }

    @Test
    void resolveResourceFromCustomResourceResolver() {
        ResourceResolver resourceResolver = new CustomResourceResolver()

        assert 'rainbows' == resourceResolver.resolveResource('magic')
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('frowny.face', new Object[0], Locale.default)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bomb', new Object[0], Locale.default)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('frowny.face', [:], Locale.default)
        }
        shouldFail(NoSuchResourceException) {
            resourceResolver.resolveResource('bomb', [:], Locale.default)
        }
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
            bind(ResourceResolverDecoratorFactory)
                .to(DefaultResourceResolverDecoratorFactory)
            bind(ResourceResolver)
                .toProvider(guicify(new ResourceResolverProvider('org.codehaus.griffon.runtime.core.resources.props')))
                .in(Singleton)
        }
    }

    static final class CustomResourceResolver extends AbstractResourceResolver {
        @Nonnull
        @Override
        protected Object doResolveResourceValue(
            @Nonnull String key,
            @Nonnull Locale locale) throws NoSuchResourceException {
            if (key == 'bomb') return null
            return new CallableWithArgs<String>() {
                @Override
                String call(@Nullable Object... args) {
                    key == 'magic' ? 'rainbows' : null
                }
            }
        }
    }

    private String shouldFail(Class clazz, Closure code) {
        return GroovyAssert.shouldFail(clazz, code).getMessage()
    }
}
