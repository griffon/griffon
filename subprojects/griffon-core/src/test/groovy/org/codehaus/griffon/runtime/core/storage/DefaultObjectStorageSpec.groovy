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
package org.codehaus.griffon.runtime.core.storage

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@Unroll
@Stepwise
class DefaultObjectStorageSpec extends Specification {
    @Shared
    private StringStorage storage = new StringStorage()

    void "Query empty storage with key '#key'"() {
        expect:
        !storage.keys
        !storage.contains(key)
        !storage.get(key)
        !storage.remove(key)

        where:
        key << [
            null,
            '',
            ' ',
            'default',
            'key'
        ]
    }

    void "Update empty storage with key '#key'"() {
        given:
        storage.set(key,'key')

        expect:
        storage.keys == ['default']
        storage.contains(key)
        storage.get(key) == 'key'
        storage.remove(key) == 'key'

        where:
        key << [
            null,
            '',
            ' ',
            'default'
        ]
    }

    void "Query non empty storage with key 'key'"() {
        given:
        String key = 'key'
        storage.set(key, key)

        expect:
        storage.keys == ['key']
        storage.contains(key)
        storage.get(key) == 'key'
        storage.values == ([key] as Set)
        storage.remove(key) == 'key'
    }
}

class StringStorage extends DefaultObjectStorage<String> {

}
