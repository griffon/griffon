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
package griffon.javafx.editors

import javafx.geometry.Insets
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.beans.PropertyEditor

@Unroll
class InsetsPropertyEditorSpec extends Specification {
    @Shared
    private Insets insets = new Insets(1, 2, 3, 4)

    void "Insets format '#format' should be equal to #insets"() {
        setup:

        PropertyEditor editor = new InsetsPropertyEditor()

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
        new Insets(1, 0, 0, 0) | '1'
        new Insets(1, 2, 0, 0) | '1,2'
        new Insets(1, 2, 3, 0) | '1,2,3'
        insets                 | '1,2,3,4'
        insets                 | '1, 2, 3, 4'
        insets                 | ' 1, 2, 3, 4'
        insets                 | ' 1, 2,3 , 4 '
        insets                 | [1, 2, 3, 4]
        insets                 | ['1', '2', '3', '4']
        new Insets(1, 1, 1, 1) | 1
        new Insets(1, 0, 0, 0) | [1]
        new Insets(1, 0, 0, 0) | ['1']
        insets                 | [top: 1, right: 2, bottom: 3, left: 4]
        insets                 | [top: '1', right: '2', bottom: '3', left: '4']
        insets                 | [t: 1, r: 2, b: 3, l: 4]
        insets                 | [t: '1', r: '2', b: '3', l: '4']
        insets                 | insets
        new Insets(0, 0, 0, 0) | [foo: 1, bar: 2]
    }

    void "Invalid insets format '#format'"() {
        setup:

        PropertyEditor editor = new InsetsPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            '1, 2, 3,4 ,5',
            '1, a',
            [1, 2, 3, 4, 5],
            [top: 'a'],
            [t: 'b'],
            [new Object()],
            [t: new Object()],
            new Object()
        ]
    }

    void "Formatted insets '#expected'"() {
        given:

        PropertyEditor editor = new InsetsPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value  | expected
        null   | null
        insets | '1.0, 2.0, 3.0, 4.0'
    }
}
