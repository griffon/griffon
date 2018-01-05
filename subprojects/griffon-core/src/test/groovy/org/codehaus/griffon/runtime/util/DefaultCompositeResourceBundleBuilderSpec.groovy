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
package org.codehaus.griffon.runtime.util

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.env.Environment
import griffon.core.env.Metadata
import griffon.core.injection.Injector
import griffon.core.resources.ResourceHandler
import griffon.util.AnnotationUtils
import griffon.util.CompositeResourceBundleBuilder
import griffon.util.Instantiator
import griffon.util.PropertiesReader
import griffon.util.ResourceBundleLoader
import griffon.util.ResourceBundleReader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.env.EnvironmentProvider
import org.codehaus.griffon.runtime.core.env.MetadataProvider
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify
import static org.mockito.Mockito.mock

@Unroll
class DefaultCompositeResourceBundleBuilderSpec extends Specification {
    @Rule
    final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject private CompositeResourceBundleBuilder bundleBuilder
    @Inject private Provider<Injector> injector
    @Inject @Named('class') private ResourceBundleLoader resourceBundleLoader1
    @Inject @Named('properties') private ResourceBundleLoader resourceBundleLoader2

    def 'Create throws #exception'() {
        given:
        injector.get().getInstances(ResourceBundleLoader) >> [resourceBundleLoader1, resourceBundleLoader2]

        when:
        bundleBuilder.create(filename, locale)

        then:
        thrown(exception)

        where:
        filename | locale         | exception
        null     | Locale.default | IllegalArgumentException
        'empty'  | null           | NullPointerException
        'empty'  | Locale.default | IllegalArgumentException
    }

    def 'Excercise bundle creation with #filename'() {
        given:
        injector.get().getInstances(ResourceBundleLoader) >> [resourceBundleLoader1, resourceBundleLoader2]

        when:
        bundleBuilder.create(filename)

        then:
        thrown(IllegalArgumentException)

        where:
        filename << [
            'org.codehaus.griffon.runtime.util.NotFoundBundle',
            'org.codehaus.griffon.runtime.util.NotABundle',
            'org.codehaus.griffon.runtime.util.BrokenBundle']
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(PropertiesReader).toProvider(guicify(new PropertiesReader.Provider())).in(Singleton)
            bind(ResourceBundleReader).toProvider(guicify(new ResourceBundleReader.Provider())).in(Singleton)
            bind(Metadata).toProvider(guicify(new MetadataProvider())).in(Singleton)
            bind(Environment).toProvider(guicify(new EnvironmentProvider())).in(Singleton)
            bind(Instantiator).to(DefaultInstantiator).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('class')).to(ClassResourceBundleLoader).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('properties')).to(PropertiesResourceBundleLoader).in(Singleton)
            bind(Injector).toProvider(guicify({ mock(Injector) } as Provider<Injector>)).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
        }
    }
}
