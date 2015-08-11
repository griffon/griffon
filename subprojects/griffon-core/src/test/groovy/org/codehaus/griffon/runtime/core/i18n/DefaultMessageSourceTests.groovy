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
package org.codehaus.griffon.runtime.core.i18n

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.CallableWithArgs
import griffon.core.i18n.MessageSource
import griffon.core.i18n.NoSuchMessageException
import griffon.core.resources.ResourceHandler
import griffon.util.CompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.junit.Rule
import org.junit.Test

import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.inject.Inject
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify

class DefaultMessageSourceTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private CompositeResourceBundleBuilder bundleBuilder

    @Inject
    private MessageSource messageSource

    @Test
    void testGetAllMessagesByProperties() {
        // assert messageSource.basename == 'org.codehaus.griffon.runtime.core.i18n.props'

        String quote = messageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'] as Object[])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.index', ['Sparta'] as Object[])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = messageSource.getMessage('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = messageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'] as Object[], 'not found :(')
        assert quote == 'not found :('
        quote = messageSource.getMessage('famous.quote.bogus', ['Sparta'] as Object[], 'not found :(')
        assert quote == 'not found :('

        quote = messageSource.getMessage('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = messageSource.getMessage('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }

    @Test
    void getAllMessagesFromResourceBundle() {
        ResourceBundle resourceBundle = messageSource.asResourceBundle()

        assert resourceBundle.getString('healthy.proverb.index')
        assert resourceBundle.getString('famous.quote.index')
        assert resourceBundle.getString('healthy.proverb.map')
        assert resourceBundle.getString('famous.quote.map')
    }

    @Test(expected = MissingResourceException)
    void invalidKeysInResourceBundle() {
        ResourceBundle resourceBundle = messageSource.asResourceBundle()
        assert !resourceBundle.getString('healthy.proverb.bogus')
    }

    @Test
    void exerciseAllExceptionThrowingMethods() {
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus')
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus', [])
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus', [:])
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus', [] as Object[])
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus', Locale.default)
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus', [], Locale.default)
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus', [:], Locale.default)
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bogus', [] as Object[], Locale.default)
        }
    }

    @Test
    void exerciseAllMethodsWithDefaultMessage() {
        String defaultMessage = 'default'
        assert defaultMessage == messageSource.getMessage('bogus', defaultMessage)
        assert defaultMessage == messageSource.getMessage('bogus', [], defaultMessage)
        assert defaultMessage == messageSource.getMessage('bogus', [:], defaultMessage)
        assert defaultMessage == messageSource.getMessage('bogus', [] as Object[], defaultMessage)
        assert defaultMessage == messageSource.getMessage('bogus', Locale.default, defaultMessage)
        assert defaultMessage == messageSource.getMessage('bogus', [], Locale.default, defaultMessage)
        assert defaultMessage == messageSource.getMessage('bogus', [:], Locale.default, defaultMessage)
        assert defaultMessage == messageSource.getMessage('bogus', [] as Object[], Locale.default, defaultMessage)

        assert 'bogus' == messageSource.getMessage('bogus', (String) null)
        assert 'bogus' == messageSource.getMessage('bogus', [], (String) null)
        assert 'bogus' == messageSource.getMessage('bogus', [:], (String) null)
        assert 'bogus' == messageSource.getMessage('bogus', [] as Object[], (String) null)
        assert 'bogus' == messageSource.getMessage('bogus', Locale.default, null)
        assert 'bogus' == messageSource.getMessage('bogus', [], Locale.default, null)
        assert 'bogus' == messageSource.getMessage('bogus', [:], Locale.default, null)
        assert 'bogus' == messageSource.getMessage('bogus', [] as Object[], Locale.default, null)
    }

    @Test
    void exerciseFormatMessage() {
        assert 'key = value' == messageSource.formatMessage('key = {0}', ['value'])
        assert 'key = value' == messageSource.formatMessage('key = {0}', ['value'] as Object[])
        assert 'key = value' == messageSource.formatMessage('key = {:value}', [value: 'value'])
        assert 'key = {:value}' == messageSource.formatMessage('key = {:value}', [value: null])
    }

    @Test
    void resolveMessageValueWithReferencedKey() {
        assert "what's up doc?" == messageSource.resolveMessageValue('reference.key', Locale.default)
        assert '@[NOT' == messageSource.resolveMessageValue('not.a.reference.key1', Locale.default)
        assert 'NOT]' == messageSource.resolveMessageValue('not.a.reference.key2', Locale.default)
        assert '@[]' == messageSource.resolveMessageValue('not.a.reference.key3', Locale.default)
    }

    @Test
    void getMessageFromCustomMessageSource() {
        CustomMessageSource messageSource = new CustomMessageSource()

        assert 'rainbows' == messageSource.getMessage('magic')
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('frowny.face', new Object[0], Locale.default)
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bomb', new Object[0], Locale.default)
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('frowny.face', [:], Locale.default)
        }
        shouldFail(NoSuchMessageException) {
            messageSource.getMessage('bomb', [:], Locale.default)
        }
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
            bind(MessageSourceDecoratorFactory)
                .to(DefaultMessageSourceDecoratorFactory)
            bind(MessageSource)
                .toProvider(guicify(new MessageSourceProvider('org.codehaus.griffon.runtime.core.i18n.props')))
                .in(Singleton)
        }
    }

    static final class CustomMessageSource extends AbstractMessageSource {
        @Nonnull
        @Override
        protected Object doResolveMessageValue(
            @Nonnull String key,
            @Nonnull Locale locale) throws NoSuchMessageException {
            if (key == 'bomb') return null
            return new CallableWithArgs<String>() {
                @Override
                String call(@Nullable Object... args) {
                    key == 'magic' ? 'rainbows' : null
                }
            }
        }

        @Override
        ResourceBundle asResourceBundle() {
            return null
        }
    }

    private String shouldFail(Class clazz, Closure code) {
        return GroovyAssert.shouldFail(clazz, code).getMessage()
    }
}
