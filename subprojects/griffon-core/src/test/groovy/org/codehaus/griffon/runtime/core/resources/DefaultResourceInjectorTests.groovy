/*
 * Copyright 2008-2017 the original author or authors.
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

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.editors.IntegerPropertyEditor
import griffon.core.editors.PropertyEditorResolver
import griffon.core.editors.StringPropertyEditor
import griffon.core.injection.Injector
import griffon.core.resources.ResourceHandler
import griffon.core.resources.ResourceInjector
import griffon.core.resources.ResourceResolver
import griffon.util.AnnotationUtils
import griffon.util.CompositeResourceBundleBuilder
import griffon.util.Instantiator
import griffon.util.ResourceBundleLoader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.util.DefaultInstantiator
import org.codehaus.griffon.runtime.util.PropertiesResourceBundleLoader
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class DefaultResourceInjectorTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject private CompositeResourceBundleBuilder bundleBuilder
    @Inject private Provider<Injector> injector
    @Inject @Named('properties') private ResourceBundleLoader propertiesResourceBundleLoader

    @Before
    void setupMethod() {
        when(injector.get().getInstances(ResourceBundleLoader)).thenReturn([propertiesResourceBundleLoader])
    }

    @BeforeClass
    public static void setupClass() {
        PropertyEditorResolver.clear()
        PropertyEditorResolver.registerEditor(String, StringPropertyEditor)
        PropertyEditorResolver.registerEditor(Integer, IntegerPropertyEditor)
        PropertyEditorResolver.registerEditor(int.class, IntegerPropertyEditor)
    }

    @AfterClass
    public static void cleanup() {
        PropertyEditorResolver.clear()
    }

    @Test
    void resolveAllFormatsByProperties() {
        ResourceResolver resourceResolver = new DefaultResourceResolver(bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.injector')
        ResourceInjector resourcesInjector = new DefaultResourceInjector(resourceResolver)
        Bean bean = new Bean()
        resourcesInjector.injectResources(bean)

        assert bean.@privateField == 'privateField'
        assert bean.@fieldBySetter == 'fieldBySetter'
        assert bean.@privateIntField == 42
        assert bean.@intFieldBySetter == 21
        assert bean.@fieldWithKey == 'no_args'
        assert bean.@fieldWithKeyAndArgs == 'with_args 1 2'
        assert bean.@fieldWithKeyNoArgsWithDefault == 'DEFAULT_NO_ARGS'
        assert bean.@fieldWithKeyWithArgsWithDefault == 'DEFAULT_WITH_ARGS'
        assert !bean.@notFound
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
