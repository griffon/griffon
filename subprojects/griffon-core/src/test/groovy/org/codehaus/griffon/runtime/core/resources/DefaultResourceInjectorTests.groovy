package org.codehaus.griffon.runtime.core.resources

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.resources.ResourceHandler
import griffon.core.resources.ResourcesInjector
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
        ResourcesInjector resourcesInjector = new DefaultResourcesInjector()
        resourcesInjector.resourceResolver = new DefaultResourceResolver(bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.injector')
        Bean bean = new Bean()
        resourcesInjector.injectResources(bean)

        assert bean.@privateField == 'privateField'
        assert bean.@fieldBySetter == 'fieldBySetter'
        assert bean.@fieldWithKey == 'no_args'
        assert bean.@fieldWithKeyAndArgs == 'with_args 1 2'
        assert bean.@fieldWithKeyNoArgsWithDefault == 'DEFAULT_NO_ARGS'
        assert bean.@fieldWithKeyWithArgsWithDefault == 'DEFAULT_WITH_ARGS'
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
