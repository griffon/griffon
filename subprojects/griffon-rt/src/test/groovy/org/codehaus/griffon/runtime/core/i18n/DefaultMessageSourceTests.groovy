package org.codehaus.griffon.runtime.core.i18n

import griffon.core.i18n.MessageSource

class DefaultMessageSourceTests extends GroovyTestCase {
    void testResolveAllFormatsInProperties() {
        MessageSource messageSource = new DefaultMessageSource('org.codehaus.griffon.runtime.core.i18n.props')

        String quote = messageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = messageSource.getMessage('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = messageSource.getMessage('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = messageSource.getMessage('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }

    void testResolveAllFormatsInGroovy() {
        MessageSource messageSource = new DefaultMessageSource('org.codehaus.griffon.runtime.core.i18n.gprops')

        String quote = messageSource.getMessage('healthy.proverb.index', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.index', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.map', [fruit: 'apple', occupation: 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.map', [location: 'Sparta'])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.closure', ['apple', 'doctor'])
        assert quote == 'An apple a day keeps the doctor away'
        quote = messageSource.getMessage('famous.quote.closure', ['Sparta'])
        assert quote == 'This is Sparta!'

        quote = messageSource.getMessage('healthy.proverb.bogus', ['apple', 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = messageSource.getMessage('famous.quote.bogus', ['Sparta'], 'not found :(')
        assert quote == 'not found :('

        quote = messageSource.getMessage('healthy.proverb.bogus', [fruit: 'apple', occupation: 'doctor'], 'not found :(')
        assert quote == 'not found :('
        quote = messageSource.getMessage('famous.quote.bogus', [location: 'Sparta'], 'not found :(')
        assert quote == 'not found :('
    }
}
