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
package griffon.swing.editors

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.GradientPaint
import java.beans.PropertyEditor

import static java.awt.Color.BLACK
import static java.awt.Color.WHITE

@Unroll
class GradientPaintPropertyEditorSpec extends Specification {
    @Shared
    private GradientPaint gradientPaint = new GradientPaint(0f, 0f, BLACK, 1f, 1f, WHITE)

    void "GradientPaint format '#format' is supported"() {
        setup:

        PropertyEditor editor = new GradientPaintPropertyEditor()

        when:
        editor.value = format

        then:

        paintsAreEqual value, editor.value

        where:
        value         | format
        null          | null
        null          | ''
        null          | ' '
        null          | []
        null          | [:]
        gradientPaint | '0,0,BLACK,1,1,WHITE'
        gradientPaint | [0, 0, BLACK, 1, 1, WHITE]
        gradientPaint | [0, 0, 'BLACK', 1, 1, 'WHITE']
        gradientPaint | [x1: 0, y1: 0, c1: BLACK, x2: 1, y2: 1, c2: WHITE]
        gradientPaint | [x1: 0, y1: 0, c1: 'BLACK', x2: 1, y2: 1, c2: 'WHITE']
        gradientPaint | '0, 0 | 1, 1 | BLACK, WHITE'
        gradientPaint | gradientPaint
    }

    private static void paintsAreEqual(GradientPaint p1, GradientPaint p2) {
        if (p1 == null) {
            assert p2 == null
        } else {
            assert p1.point1 == p2.point1 &&
                p1.point2 == p2.point2 &&
                p1.color1 == p2.color1 &&
                p1.color2 == p2.color2 &&
                p1.cyclic == p2.cyclic
        }
    }

    void "Invalid gradientPaint format '#format'"() {
        setup:

        PropertyEditor editor = new GradientPaintPropertyEditor()

        when:
        editor.value = format
        println editor.value

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            '1, 2, 3, 4, 5',
            [1, 2, 3],
            [1, 2, 3, 4, 5],
            [new Object()],
            [x1: new Object()],
            [x1: 'a'],
            new Object()
        ]
    }

    void "Formatted gradient paint '#expected'"() {
        given:

        PropertyEditor editor = new GradientPaintPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value         | expected
        null          | null
        gradientPaint | '0.0, 0.0, #000000, 1.0, 1.0, #ffffff, false'
    }
}
