package org.codehaus.griffon.runtime.core.i18n

import griffon.core.i18n.MessageSource

class CompositeMessageSourceTests extends GroovyTestCase {
    void testResolveAllFormatsInProperties() {
        MessageSource messageSource1 = new DefaultMessageSource('org.codehaus.griffon.runtime.core.i18n.props')
        MessageSource messageSource2 = new DefaultMessageSource('org.codehaus.griffon.runtime.core.i18n.props2')
        MessageSource compositeMessageSource = new CompositeMessageSource([messageSource1, messageSource2])

        String quote = compositeMessageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeMessageSource.getMessage('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = compositeMessageSource.getMessage('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeMessageSource.getMessage('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }

    void testResolveAllFormatsInGroovy() {
        MessageSource messageSource1 = new DefaultMessageSource('org.codehaus.griffon.runtime.core.i18n.gprops')
        MessageSource messageSource2 = new DefaultMessageSource('org.codehaus.griffon.runtime.core.i18n.gprops2')
        MessageSource compositeMessageSource = new CompositeMessageSource([messageSource1, messageSource2])

        String quote = compositeMessageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.closure', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote.closure', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb2.closure', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = compositeMessageSource.getMessage('famous.quote2.closure', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = compositeMessageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeMessageSource.getMessage('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = compositeMessageSource.getMessage('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = compositeMessageSource.getMessage('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }
}
