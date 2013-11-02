package org.codehaus.griffon.runtime.core.resources

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.resources.ResourceHandler
import griffon.core.resources.ResourceResolver
import griffon.util.CompositeResourceBundleBuilder
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder
import org.junit.Rule
import org.junit.Test

import javax.inject.Inject
import javax.inject.Singleton

class DefaultResourceResolverTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private CompositeResourceBundleBuilder bundleBuilder

    @Test
    void resolveAllFormatsByProperties() {
        ResourceResolver resourceResolver = new DefaultResourceResolver(bundleBuilder, 'org.codehaus.griffon.runtime.core.resources.props')

        String quote = resourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'] as Object[])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'] as Object[])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'] as Object[], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', ['Sparta'] as Object[], 'not found :(')
        assert quote == 'not found :('

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
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
