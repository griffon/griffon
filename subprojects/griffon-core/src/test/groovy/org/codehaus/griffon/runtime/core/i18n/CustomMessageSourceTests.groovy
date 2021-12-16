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
package org.codehaus.griffon.runtime.core.i18n

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import com.google.inject.Inject
import griffon.core.i18n.MessageSource
import griffon.core.i18n.NoSuchMessageException
import griffon.util.AbstractMapResourceBundle
import org.junit.Rule
import org.junit.Test

import javax.annotation.Nonnull
import javax.inject.Singleton

class CustomMessageSourceTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private MessageSource messageSource

    @Test(expected = MissingResourceException)
    void invalidKeysInResourceBundle() {
        ResourceBundle resourceBundle = messageSource.asResourceBundle()
        assert !resourceBundle.getString('healthy.proverb.bogus')
    }

    @Test
    void getAllDefinedMessages() {
        assert messageSource.getMessage('key.string') == 'string'
        assert messageSource.getMessage('key.boolean.string') == 'true'
        assert messageSource.getMessage('key.int.string') == '21'
        assert messageSource.getMessage('key.long.string') == '32'
        assert messageSource.getMessage('key.float.string') == '6.2832'
        assert messageSource.getMessage('key.double.string') == '6.2832'

        assert messageSource.getMessage('key.string', []) == 'string'
        assert messageSource.getMessage('key.boolean.string', []) == 'true'
        assert messageSource.getMessage('key.int.string', []) == '21'
        assert messageSource.getMessage('key.long.string', []) == '32'
        assert messageSource.getMessage('key.float.string', []) == '6.2832'
        assert messageSource.getMessage('key.double.string', []) == '6.2832'

        assert messageSource.getMessage('key.string', [] as Object[]) == 'string'
        assert messageSource.getMessage('key.boolean.string', [] as Object[]) == 'true'
        assert messageSource.getMessage('key.int.string', [] as Object[]) == '21'
        assert messageSource.getMessage('key.long.string', [] as Object[]) == '32'
        assert messageSource.getMessage('key.float.string', [] as Object[]) == '6.2832'
        assert messageSource.getMessage('key.double.string', [] as Object[]) == '6.2832'

        assert messageSource.getMessage('key.string', [], Locale.default) == 'string'
        assert messageSource.getMessage('key.boolean.string', [], Locale.default) == 'true'
        assert messageSource.getMessage('key.int.string', [], Locale.default) == '21'
        assert messageSource.getMessage('key.long.string', [], Locale.default) == '32'
        assert messageSource.getMessage('key.float.string', [], Locale.default) == '6.2832'
        assert messageSource.getMessage('key.double.string', [], Locale.default) == '6.2832'

        assert messageSource.getMessage('key.string', [] as Object[], Locale.default) == 'string'
        assert messageSource.getMessage('key.boolean.string', [] as Object[], Locale.default) == 'true'
        assert messageSource.getMessage('key.int.string', [] as Object[], Locale.default) == '21'
        assert messageSource.getMessage('key.long.string', [] as Object[], Locale.default) == '32'
        assert messageSource.getMessage('key.float.string', [] as Object[], Locale.default) == '6.2832'
        assert messageSource.getMessage('key.double.string', [] as Object[], Locale.default) == '6.2832'
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

    private String shouldFail(Class clazz, Closure code) {
        return GroovyAssert.shouldFail(clazz, code).getMessage()
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(MessageSource).to(CustomMessageSource).in(Singleton)
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
        }
    }

    static class CustomMessageSource extends AbstractMessageSource {
        private ResourceBundle bundle = new MapResourceBundle()

        @Nonnull
        @Override
        protected Object doResolveMessageValue(
            @Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException {
            try {
                return bundle.getObject(key)
            } catch (MissingResourceException mre) {
                throw new NoSuchMessageException(key, locale)
            }
        }

        @Override
        ResourceBundle asResourceBundle() {
            return bundle
        }
    }
}