package org.codehaus.griffon.runtime.core.resources

import griffon.core.resources.ResourceResolver

class DefaultResourceResolverTests extends GroovyTestCase {
    void testResolveAllFormatsInProperties() {
        ResourceResolver resourceResolver = new DefaultResourceResolver('org.codehaus.griffon.runtime.core.resources.props')

        String quote = resourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }

    void testResolveAllFormatsInGroovy() {
        ResourceResolver resourceResolver = new DefaultResourceResolver('org.codehaus.griffon.runtime.core.resources.gprops')

        String quote = resourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.closure', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = resourceResolver.resolveResource('famous.quote.closure', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = resourceResolver.resolveResource('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = resourceResolver.resolveResource('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }
}
