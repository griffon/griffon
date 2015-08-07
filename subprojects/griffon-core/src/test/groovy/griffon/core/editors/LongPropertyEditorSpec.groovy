/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.core.editors

import com.googlecode.openbeans.PropertyEditor
import spock.lang.Unroll

@Unroll
class LongPropertyEditorSpec extends PropertyEditorSpecSupport {
    void "Long literal '#literal' with format '#format' should be equal to #value"() {
        setup:

        PropertyEditor editor = new LongPropertyEditor()

        when:
        editor.format = format
        editor.value = literal

        then:

        value == editor.value

        where:
        literal | format     | value
        null    | null       | null
        ''      | null       | null
        '1'     | null       | 1L
        '100%'  | 'percent'  | 1L
        '$1.00' | 'currency' | 1L
        1L      | null       | 1L
    }

    void "Invalid long literal '#literal'"() {
        setup:

        PropertyEditor editor = new LongPropertyEditor()

        when:
        editor.value = literal

        then:

        thrown(IllegalArgumentException)

        where:
        literal << [
            'garbage',
            '1, 2, 3',
            [],
            [1, 2, 3],
            [:],
            [key: 'value'],
            new Object()
        ]
    }
}
