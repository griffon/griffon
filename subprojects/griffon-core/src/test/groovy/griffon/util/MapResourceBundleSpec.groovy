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

class MapResourceBundle2 extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        entries['single'] = 'single'
        entries['key.string'] = 'string'
        entries['key.number'] = 'number'
        entries['key.float'] = 'float'
    }
}
