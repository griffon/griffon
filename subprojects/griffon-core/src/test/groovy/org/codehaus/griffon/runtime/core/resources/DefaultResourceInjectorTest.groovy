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
import griffon.core.ApplicationClassLoader
import griffon.core.injection.Injector
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.kordamp.jsr377.converter.DefaultConverterRegistry
import org.kordamp.jsr377.converter.IntegerConverter
import org.kordamp.jsr377.converter.StringConverter

import javax.application.converter.ConverterRegistry
import javax.application.resources.tck.ResourceInjectorTest
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@ExtendWith(GuiceExtension)
@IncludeModule(TestModule)
class DefaultResourceInjectorTest extends ResourceInjectorTest {
    @Inject private CompositeResourceBundleBuilder bundleBuilder
    @Inject private ConverterRegistry converterRegistry
    @Inject private Provider<Injector> injector
    @Inject @Named('properties') private ResourceBundleLoader propertiesResourceBundleLoader

    @Override
    protected javax.application.resources.ResourceInjector resolveResourcesInjector() {
        ResourceResolver resourceResolver = new DefaultResourceResolver(converterRegistry, bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.injector')
        new DefaultResourceInjector(converterRegistry, resourceResolver)
    }

    @BeforeEach
    void setup() {
        converterRegistry.clear()
        converterRegistry.registerConverter(String, StringConverter)
        converterRegistry.registerConverter(Integer, IntegerConverter)
        converterRegistry.registerConverter(Integer.TYPE, IntegerConverter)
        when(injector.get().getInstances(ResourceBundleLoader)).thenReturn([propertiesResourceBundleLoader])
    }

    @AfterEach
    void cleanup() {
        converterRegistry.clear()
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
            bind(Instantiator).to(DefaultInstantiator).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('properties')).to(PropertiesResourceBundleLoader).in(Singleton)
            bind(ConverterRegistry).to(DefaultConverterRegistry).in(Singleton)
            bind(Injector).toProvider(guicify({ mock(Injector) } as Provider<Injector>)).in(Singleton)
        }
    }
}
