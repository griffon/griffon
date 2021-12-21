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
package org.codehaus.griffon.runtime.groovy.i18n

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.Instantiator
import griffon.core.bundles.CompositeResourceBundleBuilder
import griffon.core.bundles.ResourceBundleLoader
import griffon.core.i18n.MessageSource
import griffon.core.injection.Injector
import griffon.core.resources.ResourceHandler
import griffon.util.AnnotationUtils
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.bundles.DefaultCompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.core.bundles.PropertiesResourceBundleLoader
import org.codehaus.griffon.runtime.core.i18n.MessageSourceDecoratorFactory
import org.codehaus.griffon.runtime.core.i18n.MessageSourceProvider
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.util.DefaultInstantiator
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

class DefaultGroovyAwareMessageSourceTest {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject private CompositeResourceBundleBuilder bundleBuilder
    @Inject private MessageSource messageSource
    @Inject private Provider<Injector> injector
    @Inject @Named('properties') private ResourceBundleLoader propertiesResourceBundleLoader

    @Before
    void setup() {
        when(injector.get().getInstances(ResourceBundleLoader)).thenReturn([propertiesResourceBundleLoader])
    }

    @Test
    void testGetAllMessagesByProperties() {
        String quote = messageSource['healthy.proverb.index', 'apple', 'doctor']
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource['famous.quote.index', 'Sparta']
        assert quote == 'This is Sparta!'
        quote = messageSource['referenced.key']
        assert quote == "what's up doc?"
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
            bind(MessageSourceDecoratorFactory)
                .to(GroovyAwareMessageSourceDecoratorFactory)
                .in(Singleton)
            bind(MessageSource)
                .toProvider(guicify(new MessageSourceProvider('org.codehaus.griffon.runtime.groovy.i18n.props')))
                .in(Singleton)
            bind(Instantiator).to(DefaultInstantiator).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('properties')).to(PropertiesResourceBundleLoader).in(Singleton)
            bind(Injector).toProvider(guicify({ mock(Injector) } as Provider<Injector>)).in(Singleton)
        }
    }
}
