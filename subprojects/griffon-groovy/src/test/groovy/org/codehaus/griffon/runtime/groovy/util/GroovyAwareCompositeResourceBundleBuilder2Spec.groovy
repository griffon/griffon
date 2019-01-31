/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.util

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
import griffon.util.groovy.ConfigReader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.env.EnvironmentProvider
import org.codehaus.griffon.runtime.core.env.MetadataProvider
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.util.PropertiesResourceBundleLoader
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class GroovyAwareCompositeResourceBundleBuilder2Spec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject private CompositeResourceBundleBuilder bundleBuilder
    @Inject private Provider<Injector> injector
    @Inject @Named('groovy') private ResourceBundleLoader resourceBundleLoader1
    @Inject @Named('properties') private ResourceBundleLoader resourceBundleLoader2

    void setupSpec() {
        System.setProperty(Environment.KEY, 'test')
    }

    void cleanupSpec() {
        System.setProperty(Environment.KEY, 'dev')
    }

    def "Load Groovy and properties bundles"() {
        given:
        when(injector.get().getInstances(ResourceBundleLoader)).thenReturn([resourceBundleLoader1, resourceBundleLoader2])
        ResourceBundle bundle = bundleBuilder.create('org.codehaus.griffon.runtime.groovy.util.GroovyBundle')

        expect:
        bundle.getString(key) == value

        where:
        key              || value
        'properties.key' || 'properties'
        'groovy.key'     || 'test'
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(ResourceBundleReader).toProvider(guicify(new ResourceBundleReader.Provider()))
            bind(PropertiesReader).toProvider(guicify(new PropertiesReader.Provider()))
            bind(ConfigReader).toProvider(guicify(new ConfigReader.Provider()))
            bind(Metadata).toProvider(guicify(new MetadataProvider()))
            bind(Environment).toProvider(guicify(new EnvironmentProvider()))
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('groovy')).to(GroovyScriptResourceBundleLoader).in(Singleton)
            bind(ResourceBundleLoader).annotatedWith(AnnotationUtils.named('properties')).to(PropertiesResourceBundleLoader).in(Singleton)
            bind(Injector).toProvider(guicify({ mock(Injector) } as Provider<Injector>)).in(Singleton)
            bind(CompositeResourceBundleBuilder).to(DefaultCompositeResourceBundleBuilder).in(Singleton)
            bind(Instantiator).toInstance({ c -> c.newInstance() } as Instantiator)
        }
    }
}
