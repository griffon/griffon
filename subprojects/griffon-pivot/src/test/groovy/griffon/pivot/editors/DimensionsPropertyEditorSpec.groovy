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
package griffon.pivot.editors

import com.googlecode.openbeans.PropertyEditor
import org.apache.pivot.wtk.Dimensions
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class DimensionsPropertyEditorSpec extends Specification {
    @Shared
    private Dimensions dimensions = new Dimensions(10, 20)

    void "Dimensions format '#format' should be equal to #dimensions"() {
        setup:

        PropertyEditor editor = new DimensionsPropertyEditor()

        when:
        editor.value = format

        then:

        value == editor.value

        where:
        value                  | format
        null                   | null
        null                   | ''
        null                   | ' '
        null                   | []
        null                   | [:]
        dimensions             | '10,20'
        dimensions             | '10, 20'
        dimensions             | ' 10, 20'
        dimensions             | ' 10, 20 '
        dimensions             | [10, 20]
        dimensions             | ['10', '20']
        new Dimensions(10, 10) | 10
        new Dimensions(10, 10) | '10'
        new Dimensions(10, 10) | [10]
        new Dimensions(10, 10) | ['10']
        dimensions             | [width: 10, height: 20]
        dimensions             | [width: '10', height: '20']
        dimensions             | [w: 10, h: 20]
        dimensions             | [w: '10', h: '20']
        dimensions             | dimensions
        new Dimensions(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid dimensions format '#format'"() {
        setup:

        PropertyEditor editor = new DimensionsPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            [1, 2, 3],
            [new Object()],
            [w: new Object()],
            [width: 'a'],
            [w: 'b'],
            new Object()
        ]
    }

    void "Formatted dimensions '#expected'"() {
        given:

        PropertyEditor editor = new DimensionsPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value      | expected
        null       | null
        dimensions | '10, 20'
    }
}
