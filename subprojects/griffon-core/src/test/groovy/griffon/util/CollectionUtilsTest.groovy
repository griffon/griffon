/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.util

class CollectionUtilsTest extends GroovyTestCase {
    void testMapBuilderAdDelegate() {
        // given:
        Map delegate = [:]
        Map map = CollectionUtils.map(delegate)

        // when:
        map['key'] = 'value'

        // then:
        assert map.size() == 1
        assert map.keySet() == delegate.keySet()
        assert map.values() == delegate.values()
        assert map.containsKey('key') == delegate.containsKey('key')
        assert map.containsValue('value') == delegate.containsValue('value')

        // when:
        map.remove('key')

        // then:
        assert !delegate.size()

        // when:
        map['key'] = 'value'
        assert delegate.size()
        map.clear()

        // then:
        assert !delegate.size()

        assert map.hashCode() == delegate.hashCode()
    }

    void testListBuilderAndDelegate() {
        // given:
        List delegate = []
        List list = CollectionUtils.list(delegate)

        // when:
        list.add(2)
        list.add(0, 0)
        list.set(1, 1)
        list.add('foo')

        // then:
        assert delegate.size() == 3
        assert delegate == [0, 1, 'foo']

        // when:
        list.remove('foo')
        // then:
        assert delegate == [0, 1]

        // when:
        list.clear()
        // then:
        assert !delegate

        assert list.hashCode() == delegate.hashCode()

        // when:
        list.e(1)
        // then:
        assert list.size() == delegate.size()
        assert list.contains(1) == delegate.contains(1)
        assert list.containsAll([1])
    }

    void testSetBuilderAndDelegate() {
        // given:
        Set delegate = [] as Set
        Set set = CollectionUtils.set(delegate)

        // when:
        set.add(0)
        set.add(1)
        set.add('foo')

        // then:
        assert delegate.size() == 3
        assert delegate == ([0, 1, 'foo'] as Set)

        // when:
        set.remove('foo')
        // then:
        assert delegate == ([0, 1] as Set)

        // when:
        set.clear()
        // then:
        assert !delegate

        assert set.hashCode() == delegate.hashCode()

        // when:
        set.e(1)
        // then:
        assert set.size() == delegate.size()
        assert set.contains(1) == delegate.contains(1)
        assert set.containsAll([1])
    }

    void testToPropertiesDeep() {
        // given:
        Map<String, Object> map = [
            singleKey    : 'singleValue',
            'key.string' : 'string',
            'key.boolean': true,
            'key.list'   : [1, 2, 3],
            'key.map'    : [
                'one': 1,
                'two': 2
            ]
        ]

        // when:
        Properties props = CollectionUtils.toPropertiesDeep(map)

        // then:
        assert props.keySet().size() == 6
        assert props.singleKey == 'singleValue'
        assert props.get('key.string') == 'string'
        assert props.get('key.boolean') == true
        assert props.get('key.list') == [1, 2, 3]
        assert props.get('key.map.one') == 1
        assert props.get('key.map.two') == 2
    }
}
