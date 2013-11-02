package org.codehaus.griffon.runtime.core

import griffon.core.ApplicationConfiguration
import griffon.util.AbstractMapResourceBundle
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Nonnull

@Unroll
class DefaultApplicationConfigurationSpec extends Specification {
    def 'Calling configuration.#method(#key, #defaultValue) gives #value as result'() {
        given:
        ResourceBundle bundle = new MapResourceBundle()
        ApplicationConfiguration configuration = new DefaultApplicationConfiguration(bundle)

        expect:
        value == configuration."$method"(key, defaultValue)

        where:
        method         | key                   | defaultValue      || value
        'get'          | 'key.string'          | 'STRING'          || 'string'
        'getAsString'  | 'key.string'          | 'STRING'          || 'string'
        'getAsBoolean' | 'key.boolean'         | false             || true
        'getAsBoolean' | 'key.boolean.string'  | false             || true
        'getAsInt'     | 'key.int'             | -1                || 42
        'getAsInt'     | 'key.int.string'      | -1                || 21
        'getAsLong'    | 'key.long'            | -1L               || 64L
        'getAsLong'    | 'key.long.string'     | -1L               || 32L
        'getAsFloat'   | 'key.float'           | -1.0f             || 3.1416f
        'getAsFloat'   | 'key.float.string'    | -1.0f             || 6.2832f
        'getAsDouble'  | 'key.double'          | -1.0d             || 3.1416d
        'getAsDouble'  | 'key.double.string'   | -1.0d             || 6.2832d
        'getAsString'  | 'key.string.unknown'  | 'UNKNOWN'         || 'UNKNOWN'
        'getAsBoolean' | 'key.boolean.unknown' | true              || true
        'getAsInt'     | 'key.int.unknown'     | Integer.MAX_VALUE || Integer.MAX_VALUE
        'getAsLong'    | 'key.long.unknown'    | Long.MAX_VALUE    || Long.MAX_VALUE
        'getAsFloat'   | 'key.float.unknown'   | Float.MAX_VALUE   || Float.MAX_VALUE
        'getAsDouble'  | 'key.double.unknown'  | Double.MAX_VALUE  || Double.MAX_VALUE
    }

    class MapResourceBundle extends AbstractMapResourceBundle {
        @Override
        protected void initialize(@Nonnull Map<String, Object> entries) {
            entries['key.string'] = 'string'
            entries['key.boolean'] = true
            entries['key.boolean.string'] = 'true'
            entries['key.int'] = 42
            entries['key.int.string'] = '21'
            entries['key.long'] = 64L
            entries['key.long.string'] = '32'
            entries['key.float'] = 3.1416f
            entries['key.float.string'] = '6.2832'
            entries['key.double'] = 3.1416d
            entries['key.double.string'] = '6.2832'
        }
    }
}