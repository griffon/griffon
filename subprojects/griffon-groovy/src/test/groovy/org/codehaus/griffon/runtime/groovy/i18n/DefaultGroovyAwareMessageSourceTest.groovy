/*
 * Copyright 2008-2016 the original author or authors.
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
import griffon.core.i18n.MessageSource
import griffon.core.resources.ResourceHandler
import griffon.util.CompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.i18n.MessageSourceDecoratorFactory
import org.codehaus.griffon.runtime.core.i18n.MessageSourceProvider
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify

class DefaultGroovyAwareMessageSourceTest {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private MessageSource messageSource

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
        }
    }
}
