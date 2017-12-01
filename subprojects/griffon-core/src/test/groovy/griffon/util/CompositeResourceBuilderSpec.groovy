/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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

class CompositeResourceBuilderSpec extends Specification {
    def 'Invalid constructor args'() {
        when:
        new CompositeResourceBundle(bundles)

        then:
        thrown(exception)

        where:
        bundles               | exception
        null                  | NullPointerException
        []                    | IllegalStateException
        new ResourceBundle[0] | IllegalStateException
    }

    def 'Resolve messages from multiple bundles'() {
        setup:
        ResourceBundle bundle = new CompositeResourceBundle(
            [new MapResourceBundle(), new MapResourceBundle2()]
        )

        expect:
        value == bundle.getString(key)

        where:
        key          | value
        'single'     | 'single'
        'key.string' | 'string'
        'key.number' | 'number'
        'key.float'  | 'float'
    }
}
