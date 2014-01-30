package griffon.util

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
        entries['single'] = 'single'
        entries['key.string'] = 'string'
        entries['key.number'] = 'number'
    }
}
