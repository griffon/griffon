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
package org.codehaus.griffon.runtime.core.i18n

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.i18n.MessageSource
import griffon.core.resources.ResourceHandler
import griffon.util.CompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject
import javax.inject.Singleton

class DefaultMessageSourceTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private CompositeResourceBundleBuilder bundleBuilder

    @Test
    void testGetAllMessagesByProperties() {
        MessageSource messageSource = new DefaultMessageSource(bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.props')

        assert messageSource.basename == 'org.codehaus.griffon.runtime.core.resources.props'

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
        MessageSource messageSource = new DefaultMessageSource(bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.props')
        ResourceBundle resourceBundle = messageSource.asResourceBundle()

        assert resourceBundle.getString('healthy.proverb.index')
        assert resourceBundle.getString('famous.quote.index')
        assert resourceBundle.getString('healthy.proverb.map')
        assert resourceBundle.getString('famous.quote.map')
    }

    @Test(expected = MissingResourceException)
    void invalidKeysInResourceBundle() {
        MessageSource messageSource = new DefaultMessageSource(bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.props')
        ResourceBundle resourceBundle = messageSource.asResourceBundle()
        assert !resourceBundle.getString('healthy.proverb.bogus')
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
        }
    }
}
