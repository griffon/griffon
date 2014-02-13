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
package org.codehaus.griffon.runtime.core.resources

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.resources.ResourceHandler
import griffon.core.resources.ResourceInjector
import griffon.core.resources.ResourceResolver
import griffon.util.CompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject
import javax.inject.Singleton

class DefaultResourceInjectorTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private CompositeResourceBundleBuilder bundleBuilder

    @Test
    void resolveAllFormatsByProperties() {
        ResourceResolver resourceResolver = new DefaultResourceResolver(bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.injector')
        ResourceInjector resourcesInjector = new DefaultResourceInjector(resourceResolver)
        Bean bean = new Bean()
        resourcesInjector.injectResources(bean)

        assert bean.@privateField == 'privateField'
        assert bean.@fieldBySetter == 'fieldBySetter'
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
        }
    }
}
