/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.core

import griffon.core.editors.IntegerPropertyEditor
import griffon.core.editors.PropertyEditorResolver
import griffon.inject.Contextual
import org.codehaus.griffon.runtime.core.DefaultContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Named

@Unroll
class ContextSpec extends Specification {
    @Shared private Context ctx1 = new DefaultContext()
    @Shared private Context ctx2 = new DefaultContext(ctx1)
    @Shared private Context ctx3 = new DefaultContext(ctx1)

    def setup() {
        ctx1['foo'] = 'foo'
        ctx1['class'] = String
        ctx1['int'] = 1
        ctx1['integer'] = '1'
        ctx2['bar'] = 'bar'
        ctx3['bar'] = 'bar'
        ctx3['foo'] = 'bar'
    }

    def "Find values on context 1 using defaults"() {
        expect:
        value == ctx1.get(key, defaultValue)
        value == ctx1.getAt(key, defaultValue)

        where:
        key   | value | defaultValue
        'foo' | 'foo' | 'bar'
        'bar' | 'bar' | 'bar'
    }

    def "Find values on context 1 using typed defaults"() {
        expect:
        value == ctx1."$methodName"(key, defaultValue)

        where:
        key   | methodName     | value | defaultValue
        'key' | 'getAsBoolean' | true  | true
        'key' | 'getAsInt'     | 1     | 1
        'key' | 'getAsLong'    | 1L    | 1L
        'key' | 'getAsFloat'   | 1f    | 1f
        'key' | 'getAsDouble'  | 1d    | 1d
        'key' | 'getAsString'  | 'foo' | 'foo'
        'key' | 'getAsString'  | null  | null
        'foo' | 'getAsString'  | 'foo' | null
    }

    def "Find values on context 1 using casted default"() {
        when:
        Integer val = ctx1."$methodName"(key, defaultValue)

        then:
        value == val

        where:
        key   | methodName | defaultValue || value
        'key' | 'getAs'    | 1            || 1
    }

    def "Find values on context 1 using converted default"() {
        when:
        String val = ctx1.getConverted(key, type, defaultValue)

        then:
        value == val

        where:
        key   | defaultValue | type   || value
        'key' | '1'          | String || '1'
    }

    def "Find values on context 1 using typed getters"() {
        expect:
        value == ctx1."$methodName"(key)

        where:
        key   | methodName     | value
        'key' | 'getAsBoolean' | false
        'key' | 'getAsInt'     | 0
        'key' | 'getAsLong'    | 0L
        'key' | 'getAsFloat'   | 0f
        'key' | 'getAsDouble'  | 0d
        'key' | 'getAsString'  | null
    }

    def "Find values on context 1 using casted value"() {
        when:
        Integer val = ctx1.getAs(key)

        then:
        value == val

        where:
        key   || value
        'int' || 1
    }

    def "Find values on context 1 using converted value"() {
        given:
        PropertyEditorResolver.clear()
        PropertyEditorResolver.registerEditor(Integer, IntegerPropertyEditor)

        when:
        Integer val = ctx1.getConverted(key, type)

        then:
        value == val

        cleanup:
        PropertyEditorResolver.clear()

        where:
        key       | type    || value
        'key'     | Integer || null
        'int'     | Integer || 1
        'integer' | Integer || 1
    }

    def "Remove values on context 1 using casted value"() {
        when:
        Integer val = ctx1.removeAs(key)

        then:
        !ctx1.hasKey(key)
        value == val

        where:
        key   || value
        'int' || 1
    }

    def "Remove values on context 1 using converted value"() {
        given:
        PropertyEditorResolver.clear()
        PropertyEditorResolver.registerEditor(Integer, IntegerPropertyEditor)

        when:
        Integer val = ctx1.removeConverted(key, type)

        then:
        !ctx1.hasKey(key)
        value == val

        cleanup:
        PropertyEditorResolver.clear()

        where:
        key       | type    || value
        'key'     | Integer || null
        'int'     | Integer || 1
        'integer' | Integer || 1
    }

    def "Find values on context 1"() {
        expect:
        value == ctx1[key]

        where:
        key   | value
        'foo' | 'foo'
        'bar' | null
    }

    def "Find values on child context 2"() {
        expect:
        value == ctx2[key]

        where:
        key   | value
        'foo' | 'foo'
        'bar' | 'bar'
        'xzy' | null
    }

    def "Find values on child context 3"() {
        expect:
        value == ctx3[key]

        where:
        key   | value
        'foo' | 'bar'
        'bar' | 'bar'
        'xzy' | null
    }

    def "Shadowed attributes in child context 2"() {
        when:
        ctx3.remove('foo')

        then:
        'foo' == ctx3['foo']
    }

    def "Destroying a context clears all attributes"() {
        when:
        ctx1.destroy()

        then:
        !ctx1['foo']
    }

    def "Get all the keys"() {
        expect:
        context.keySet() == keySet

        where:
        context | keySet
        ctx1    | ['foo', 'class', 'int', 'integer'] as Set
        ctx2    | ['foo', 'bar', 'class', 'int', 'integer'] as Set
    }

    def "ContainsKey on child context"() {
        expect:
        ctx2.containsKey('foo')
        ctx2.containsKey('bar')
        ctx1.containsKey('foo')
        !ctx1.containsKey('bar')
    }

    def "HasKey on child context"() {
        expect:
        !ctx2.hasKey('foo')
        ctx2.hasKey('bar')
        ctx1.hasKey('foo')
        !ctx1.hasKey('bar')
    }

    def "Inject contextual members"() {
        given:
        Bean bean = new Bean()

        when:
        ctx2.injectMembers(bean)

        then:
        bean.@foo == 'foo'
        bean.@bar == 'bar'
    }

    static class Bean {
        @Contextual @Named('foo')
        private String foo

        private String bar

        @Contextual
        void setBar(@Named('bar') String s) { bar = s }
    }
}
