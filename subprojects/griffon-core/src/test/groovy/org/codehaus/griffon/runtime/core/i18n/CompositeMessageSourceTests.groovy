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
import griffon.core.ApplicationClassLoader
import griffon.core.i18n.MessageSource
import griffon.core.injection.Injector
import griffon.core.resources.ResourceHandler
import griffon.util.AnnotationUtils
import griffon.util.CompositeResourceBundleBuilder
import griffon.util.Instantiator
import griffon.util.ResourceBundleLoader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.util.DefaultInstantiator
import org.codehaus.griffon.runtime.util.PropertiesResourceBundleLoader
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class CompositeMessageSourceTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject private CompositeResourceBundleBuilder bundleBuilder
    @Inject private Provider<Injector> injector
    @Inject @Named('properties') private ResourceBundleLoader propertiesResourceBundleLoader

    @Before
    void setup() {
        when(injector.get().getInstances(ResourceBundleLoader)).thenReturn([propertiesResourceBundleLoader])
    }

    @Test
    void getAllMessagesByProperties() {
        MessageSource messageSource1 = new DefaultMessageSource(bundleBuilder, 'org.codehaus.griffon.runtime.core.i18n.props')
        MessageSource messageSource2 = new DefaultMessageSource(bundleBuilder, 'org.codehaus.griffon.runtime.core.i18n.props2')
        MessageSource compositeMessageSource = new CompositeMessageSource([messageSource1, messageSource2])

        String quote = compositeMessageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'] as Object[])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.index', ['Sparta'] as Object[])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.index', ['apple', 'doctor'] as Object[])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.index', ['Sparta'] as Object[])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeMessageSource.getMessage('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = compositeMessageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'] as Object[], 'not found :(')
        assert quote == 'not found :('
        quote = compositeMessageSource.getMessage('famous.quote.bogus', ['Sparta'] as Object[], 'not found :(')
        assert quote == 'not found :('

        quote = compositeMessageSource.getMessage('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeMessageSource.getMessage('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }

    @Test
    void getCompositeResourceBundle() {
        MessageSource messageSource1 = new DefaultMessageSource(bundleBuilder, 'org.codehaus.griffon.runtime.core.i18n.props')
        MessageSource messageSource2 = new DefaultMessageSource(bundleBuilder, 'org.codehaus.griffon.runtime.core.i18n.props2')
        MessageSource compositeMessageSource = new CompositeMessageSource([messageSource1, messageSource2])

        ResourceBundle resourceBundle = compositeMessageSource.asResourceBundle()

        assert resourceBundle.containsKey('healthy.proverb.index')
        assert resourceBundle.containsKey('healthy.proverb2.index')
        assert resourceBundle.containsKey('healthy.proverb.map')
        assert resourceBundle.containsKey('healthy.proverb2.map')
    }

    @Test(expected = IllegalStateException)
    void cantCreateBundleWithEmptyBundleList() {
        new CompositeMessageSource([])
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
            bind(Instantiator).to(DefaultInstantiator).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('properties')).to(PropertiesResourceBundleLoader).in(Singleton)
            bind(Injector).toProvider(guicify({ mock(Injector) } as Provider<Injector>)).in(Singleton)
        }
    }
}
