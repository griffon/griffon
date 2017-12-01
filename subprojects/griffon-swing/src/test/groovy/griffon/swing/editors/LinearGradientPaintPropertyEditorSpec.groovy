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
package griffon.swing.editors

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.Color
import java.awt.LinearGradientPaint
import java.beans.PropertyEditor

import static java.awt.Color.BLACK
import static java.awt.Color.RED
import static java.awt.Color.WHITE
import static java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE
import static java.awt.MultipleGradientPaint.CycleMethod.REPEAT

@Unroll
class LinearGradientPaintPropertyEditorSpec extends Specification {
    @Shared
    private LinearGradientPaint sharedPaint = new LinearGradientPaint(1, 2, 3, 4, [0, 1] as float[], [BLACK, WHITE] as Color[], NO_CYCLE)

    @Shared
    private LinearGradientPaint sharedPaintC = new LinearGradientPaint(1, 2, 3, 4, [0, 1] as float[], [BLACK, WHITE] as Color[], REPEAT)

    void "LinearGradientPaint format '#format' is supported"() {
        setup:

        PropertyEditor editor = new LinearGradientPaintPropertyEditor()

        when:
        editor.value = format

        then:

        paintsAreEqual value, editor.value

        where:
        value        | format
        null         | null
        null         | ''
        null         | ' '
        null         | []
        null         | [:]
        sharedPaint  | '1,2,3,4,[0:1],[BLACK:WHITE]'
        sharedPaintC | '1,2,3,4,[0:1],[BLACK:WHITE],REPEAT'
        sharedPaint  | [1, 2, 3, 4, [0, 1], [BLACK, WHITE]]
        sharedPaintC | [1, 2, 3, 4, [0, 1], [BLACK, WHITE], 'REPEAT']
        sharedPaintC | [1, 2, 3, 4, [0, 1], [BLACK, WHITE], REPEAT]
        sharedPaint  | [x1: 1, y1: 2, x2: 3, y2: 4, fractions: [0, 1], colors: [BLACK, WHITE]]
        sharedPaintC | [x1: 1, y1: 2, x2: 3, y2: 4, fractions: [0, 1], colors: [BLACK, WHITE], cycle: 'REPEAT']
        sharedPaintC | [x1: 1, y1: 2, x2: 3, y2: 4, fractions: [0, 1], colors: [BLACK, WHITE], cycle: REPEAT]
        sharedPaint  | sharedPaint
    }

    private static void paintsAreEqual(LinearGradientPaint p1, LinearGradientPaint p2) {
        if (p1 == null) {
            assert p2 == null
        } else {
            assert p1.startPoint == p2.startPoint &&
                p1.endPoint == p2.endPoint &&
                p1.fractions == p2.fractions &&
                p1.colors*.toString() == p2.colors*.toString() &&
                p1.cycleMethod == p2.cycleMethod
        }
    }

    void "Invalid gradientPaint format '#format'"() {
        setup:

        PropertyEditor editor = new LinearGradientPaintPropertyEditor()

        when:
        editor.value = format

        then:

        thrown(IllegalArgumentException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            '1, 2, 3, 4, 5',
            [1, 2, 3],
            [1, 2, 3, 4, 5],
            [x1: 'a'],
            [new Object()],
            [x1: new Object()],
            new Object(),
            '1,2,3,4,[0:1],[BLACK:WHITE:RED]',
            '1,2,3,4,0:1,[BLACK:WHITE:RED]',
            '1,2,3,4,[0:1],BLACK:WHITE',
            [1, 2, 3, 4, [0, 1], [BLACK, WHITE, RED]],
            [x1: 1, y1: 2, x2: 3, y2: 4, fractions: [0, 1], colors: [BLACK, WHITE, RED]]
        ]
    }

    void "Formatted linear gradient '#expected'"() {
        given:

        PropertyEditor editor = new LinearGradientPaintPropertyEditor()

        when:

        editor.value = value
        String actual = editor.asText

        then:

        expected == actual

        where:
        value        | expected
        null         | null
        sharedPaint  | '1.0, 2.0, 3.0, 4.0, [0.0:1.0], [#000000:#ffffff], NO_CYCLE'
        sharedPaintC | '1.0, 2.0, 3.0, 4.0, [0.0:1.0], [#000000:#ffffff], REPEAT'
    }
}
