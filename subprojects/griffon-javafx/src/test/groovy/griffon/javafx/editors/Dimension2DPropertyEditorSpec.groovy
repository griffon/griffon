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
package griffon.javafx.editors

import com.googlecode.openbeans.PropertyEditor
import javafx.geometry.Dimension2D
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class Dimension2DPropertyEditorSpec extends Specification {
    @Shared
    private Dimension2D dimension = new Dimension2D(10, 20)

    void "Dimension2D format '#format' should be equal to #dimension"() {
        setup:

        PropertyEditor editor = new Dimension2DPropertyEditor()

        when:

        editor.value = format

        then:

        value == editor.value

        where:
        value                   | format
        null                    | null
        null                    | ''
        null                    | ' '
        null                    | []
        null                    | [:]
        dimension               | '10,20'
        dimension               | '10, 20'
        dimension               | ' 10, 20'
        dimension               | ' 10, 20 '
        dimension               | [10, 20]
        dimension               | ['10', '20']
        new Dimension2D(10, 10) | 10
        new Dimension2D(10, 10) | '10'
        new Dimension2D(10, 10) | [10]
        new Dimension2D(10, 10) | ['10']
        dimension               | [width: 10, height: 20]
        dimension               | [width: '10', height: '20']
        dimension               | [w: 10, h: 20]
        dimension               | [w: '10', h: '20']
        dimension               | dimension
        new Dimension2D(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid dimension format '#format'"() {
        setup:

        PropertyEditor editor = new Dimension2DPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            [1, 2, 3],
            [width: 'a'],
            [w: 'b'],
            [new Object()],
            [w: new Object()],
            new Object()
        ]
    }


    void "Formatted dimension '#expected'"() {
        given:

        PropertyEditor editor = new Dimension2DPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value     | expected
        null      | null
        dimension | '10.0, 20.0'
    }
}
