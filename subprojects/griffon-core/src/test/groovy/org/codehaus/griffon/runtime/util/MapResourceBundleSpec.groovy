package org.codehaus.griffon.runtime.util

import griffon.util.AbstractMapResourceBundle
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class MapResourceBundleSpec extends Specification {
    def 'Calling bundle.getObject(#key, #defaultValue) gives #value as result'() {
        given:
        ResourceBundle bundle = new MapResourceBundle()

        expect:
        value == bundle.getObject(key)

        where:
        key          || value
        'key.string' || 'string'
    }
}

class MapResourceBundle extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        entries['key.string'] = 'string'
    }
}
