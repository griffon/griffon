package org.codehaus.griffon.runtime.core.resources

import griffon.core.resources.ResourceResolver

class CompositeResourceResolverTests extends GroovyTestCase {
    void testResolveAllFormatsInProperties() {
        ResourceResolver resourceResolver1 = new DefaultResourceResolver('org.codehaus.griffon.runtime.core.resources.props')
        ResourceResolver resourceResolver2 = new DefaultResourceResolver('org.codehaus.griffon.runtime.core.resources.props2')
        ResourceResolver compositeResourceResolver = new CompositeResourceResolver([resourceResolver1, resourceResolver2])

        String quote = compositeResourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb2.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote2.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb2.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote2.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeResourceResolver.resolveResource('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = compositeResourceResolver.resolveResource('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeResourceResolver.resolveResource('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }

    void testResolveAllFormatsInGroovy() {
        ResourceResolver resourceResolver1 = new DefaultResourceResolver('org.codehaus.griffon.runtime.core.resources.gprops')
        ResourceResolver resourceResolver2 = new DefaultResourceResolver('org.codehaus.griffon.runtime.core.resources.gprops2')
        ResourceResolver compositeResourceResolver = new CompositeResourceResolver([resourceResolver1, resourceResolver2])

        String quote = compositeResourceResolver.resolveResource('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb.closure', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote.closure', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb2.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote2.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb2.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote2.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb2.closure', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeResourceResolver.resolveResource('famous.quote2.closure', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeResourceResolver.resolveResource('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeResourceResolver.resolveResource('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = compositeResourceResolver.resolveResource('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeResourceResolver.resolveResource('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }
}
