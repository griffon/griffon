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
package org.codehaus.griffon.runtime.core.resources


import com.google.inject.AbstractModule
import griffon.annotations.core.Nonnull
import griffon.annotations.core.Nullable
import griffon.core.ApplicationClassLoader
import griffon.core.CallableWithArgs
import griffon.core.injection.Injector
import griffon.core.resources.NoSuchResourceException
import griffon.core.resources.ResourceHandler
import griffon.core.resources.ResourceResolver
import griffon.util.AnnotationUtils
import griffon.util.CompositeResourceBundleBuilder
import griffon.util.Instantiator
import griffon.util.ResourceBundleLoader
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension
import name.falgout.jeffrey.testing.junit.guice.IncludeModule
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.util.DefaultInstantiator
import org.codehaus.griffon.runtime.util.PropertiesResourceBundleLoader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.kordamp.jsr377.converter.DefaultConverterRegistry
import org.kordamp.jsr377.converter.IntegerConverter

import javax.application.converter.ConverterRegistry
import javax.application.resources.tck.ResourceResolverTest
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.junit.jupiter.api.Assertions.assertAll
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@ExtendWith(GuiceExtension)
@IncludeModule(TestModule)
class DefaultResourceResolverTest extends ResourceResolverTest {
    private static final String KEY_PROVERB_MAP = 'key.proverb.map'
    private static final List TWO_ARGS_LIST = ['apple', 'doctor']
    private static final Map TWO_ARGS_MAP = [fruit: 'apple', occupation: 'doctor']
    private static final String PROVERB_FORMAT_MAP = 'An {:fruit} a day keeps the {:occupation} away'

    @Inject private CompositeResourceBundleBuilder bundleBuilder
    @Inject private ResourceResolver resourceResolver
    @Inject private ConverterRegistry converterRegistry
    @Inject private Provider<Injector> injector
    @Inject @Named('properties') private ResourceBundleLoader propertiesResourceBundleLoader

    @BeforeEach
    void setup() {
        converterRegistry.clear()
        converterRegistry.registerConverter(Integer, IntegerConverter)
        when(injector.get().getInstances(ResourceBundleLoader)).thenReturn([propertiesResourceBundleLoader])
        super.setup()
    }

    @Override
    protected javax.application.resources.ResourceResolver resolveResourceResolver() {
        return resourceResolver
    }

    @Test
    void verify_resolveResource_withArguments_withLocale_additionalArgumentTypes() {
        // given:
        def resourceResolver = resolveResourceResolver();

        // expect:
        assertAll(
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB_MAP),
                    equalTo(PROVERB_FORMAT_MAP))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB, TWO_ARGS_LIST),
                    equalTo(PROVERB_TEXT))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB, TWO_ARGS_LIST, Locale.default),
                    equalTo(PROVERB_TEXT))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB_MAP, TWO_ARGS_MAP),
                    equalTo(PROVERB_TEXT))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB_MAP, TWO_ARGS_MAP, Locale.default),
                    equalTo(PROVERB_TEXT))
            } as Executable
        )
    }

    @Test
    void verify_resolveResource_withArguments_withLocale_withDefaultValue_additionalArguments() {
        // given:
        def resourceResolver = resolveResourceResolver()

        // expect:
        assertAll(
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, TWO_ARGS_LIST, DEFAULT_VALUE),
                    equalTo(DEFAULT_VALUE))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, TWO_ARGS_LIST, Locale.default, DEFAULT_VALUE),
                    equalTo(DEFAULT_VALUE))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, TWO_ARGS_MAP, DEFAULT_VALUE),
                    equalTo(DEFAULT_VALUE))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResource(KEY_PROVERB_BOGUS, TWO_ARGS_MAP, Locale.default, DEFAULT_VALUE),
                    equalTo(DEFAULT_VALUE))
            } as Executable)
    }

    @Test
    void verify_resolveResourceConverted_withArguments_withLocale_additionalArguments() {
        // given:
        def resourceResolver = resolveResourceResolver()

        // expect:
        int value = 42
        assertAll(
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, TWO_ARGS_LIST, Integer),
                    equalTo(value))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, TWO_ARGS_LIST, Locale.default, Integer),
                    equalTo(value))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, TWO_ARGS_MAP, Integer),
                    equalTo(value))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_INTEGER, TWO_ARGS_MAP, Locale.default, Integer),
                    equalTo(value))
            } as Executable
        )
    }

    @Test
    void verify_resolveResourceConverted_withArguments_withLocale_withDefaultValue_additionalArguments() {
        // given:
        def resourceResolver = resolveResourceResolver()

        // expect:
        int defaultValue = 21
        assertAll(
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_LIST, defaultValue, Integer),
                    equalTo(defaultValue))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_LIST, Locale.default, defaultValue, Integer),
                    equalTo(defaultValue))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_MAP, defaultValue, Integer),
                    equalTo(defaultValue))
            } as Executable,
            { ->
                assertThat(resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_MAP, Locale.default, defaultValue, Integer),
                    equalTo(defaultValue))
            } as Executable
        )
    }

    @Test
    void verify_resolveResource_withUnknownKey_withArguments_withLocale_additionalArguments() {
        // given:
        def resourceResolver = resolveResourceResolver()

        // expect:
        assertAll(
            { ->
                assertThrows(NoSuchResourceException,
                    { -> resourceResolver.resolveResource(KEY_BOGUS, TWO_ARGS_LIST) } as Executable)
            } as Executable,
            { ->
                assertThrows(NoSuchResourceException,
                    { -> resourceResolver.resolveResource(KEY_BOGUS, TWO_ARGS_LIST, Locale.default) } as Executable)
            } as Executable,
            { ->
                assertThrows(NoSuchResourceException,
                    { -> resourceResolver.resolveResource(KEY_BOGUS, TWO_ARGS_MAP) } as Executable)
            } as Executable,
            { ->
                assertThrows(NoSuchResourceException,
                    { -> resourceResolver.resolveResource(KEY_BOGUS, TWO_ARGS_MAP, Locale.default) } as Executable)
            } as Executable
        )
    }

    @Test
    void verify_resolveResourceConverted_withUnknownKey_withArguments_withLocale_additionalArguments() {
        // given:
        def resourceResolver = resolveResourceResolver()

        // expect:
        assertAll(
            { ->
                assertThrows(NoSuchResourceException, { ->
                    resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_LIST, Integer)
                } as Executable)
            } as Executable,
            { ->
                assertThrows(NoSuchResourceException, { ->
                    resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_LIST, Locale.default, Integer)
                } as Executable)
            } as Executable,
            { ->
                assertThrows(NoSuchResourceException, { ->
                    resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_MAP, Integer)
                } as Executable)
            } as Executable,
            { ->
                assertThrows(NoSuchResourceException, { ->
                    resourceResolver.resolveResourceConverted(KEY_BOGUS, TWO_ARGS_MAP, Locale.default, Integer)
                } as Executable)
            } as Executable
        )
    }

    /*
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

        assert null == resourceResolver.resolveResource('bogus', (Object) null)
        assert null == resourceResolver.resolveResource('bogus', [], (Object) null)
        assert null == resourceResolver.resolveResource('bogus', [:], (Object) null)
        assert null == resourceResolver.resolveResource('bogus', [] as Object[], null)
        assert null == resourceResolver.resolveResource('bogus', Locale.default, null)
        assert null == resourceResolver.resolveResource('bogus', [], Locale.default, null)
        assert null == resourceResolver.resolveResource('bogus', [:], Locale.default, null)
        assert null == resourceResolver.resolveResource('bogus', [] as Object[], Locale.default, null)
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
        def resourceResolver = new CustomResourceResolver(converterRegistry)

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
    */

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
            bind(ResourceResolverDecoratorFactory)
                .to(DefaultResourceResolverDecoratorFactory)
            bind(ResourceResolver)
                .toProvider(guicify(new ResourceResolverProvider('org.codehaus.griffon.runtime.core.resources.props')))
                .in(Singleton)
            bind(Instantiator).to(DefaultInstantiator).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('properties')).to(PropertiesResourceBundleLoader).in(Singleton)
            bind(Injector).toProvider(guicify({ mock(Injector) } as Provider<Injector>)).in(Singleton)
            bind(ConverterRegistry).to(DefaultConverterRegistry).in(Singleton)
        }
    }

    static final class CustomResourceResolver extends AbstractResourceResolver {
        @Inject
        protected CustomResourceResolver(@Nonnull ConverterRegistry converterRegistry) {
            super(converterRegistry)
        }

        @Nonnull
        @Override
        protected Object doResolveResourceValue(
            @Nonnull String key, @Nonnull Locale locale) throws NoSuchResourceException {
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
