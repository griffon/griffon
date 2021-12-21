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
package griffon.swing.converters

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import griffon.converter.ConversionException
import java.awt.Color
import java.awt.RadialGradientPaint
import java.awt.geom.Point2D

import static java.awt.Color.BLACK
import static java.awt.Color.RED
import static java.awt.Color.WHITE
import static java.awt.MultipleGradientPaint.CycleMethod.NO_CYCLE
import static java.awt.MultipleGradientPaint.CycleMethod.REPEAT

@Unroll
class RadialGradientPaintConverterSpec extends Specification {
    @Shared
    private RadialGradientPaint sharedPaint = new RadialGradientPaint(new Point2D.Float(2f, 2f), 5f, new Point2D.Float(4f, 4f), [0, 1] as float[], [BLACK, WHITE] as Color[], NO_CYCLE)

    @Shared
    private RadialGradientPaint sharedPaintC = new RadialGradientPaint(new Point2D.Float(2f, 2f), 5f, new Point2D.Float(4f, 4f), [0, 1] as float[], [BLACK, WHITE] as Color[], REPEAT)

    void "RadialGradientPaint format '#format' is supported"() {
        setup:
        def converter = new RadialGradientPaintConverter()

        when:
        def converted = converter.fromObject(format)

        then:
        paintsAreEqual value, converted

        where:
        value        | format
        null         | null
        null         | ''
        null         | ' '
        null         | []
        null         | [:]
        sharedPaint  | '2,2,5,4,4,[0:1],[BLACK:WHITE]'
        sharedPaintC | '2,2,5,4,4,[0:1],[BLACK:WHITE],REPEAT'
        sharedPaint  | [2, 2, 5, 4, 4, [0, 1], [BLACK, WHITE]]
        sharedPaintC | [2, 2, 5, 4, 4, [0, 1], [BLACK, WHITE], 'REPEAT']
        sharedPaintC | [2, 2, 5, 4, 4, [0, 1], [BLACK, WHITE], REPEAT]
        sharedPaint  | [cx: 2, cy: 2, radius: '5', fx: 4, fy: 4, fractions: [0, 1], colors: [BLACK, WHITE]]
        sharedPaintC | [cx: 2, cy: 2, radius: '5', fx: 4, fy: 4, fractions: [0, 1], colors: [BLACK, WHITE], cycle: 'REPEAT']
        sharedPaintC | [cx: 2, cy: 2, radius: '5', fx: 4, fy: 4, fractions: [0, 1], colors: [BLACK, WHITE], cycle: REPEAT]
        sharedPaint  | sharedPaint
    }

    private static void paintsAreEqual(RadialGradientPaint p1, RadialGradientPaint p2) {
        if (p1 == null) {
            assert p2 == null
        } else {
            assert p1.centerPoint == p2.centerPoint &&
                p1.focusPoint == p2.focusPoint &&
                p1.radius == p2.radius &&
                p1.fractions == p2.fractions &&
                p1.colors*.toString() == p2.colors*.toString() &&
                p1.cycleMethod == p2.cycleMethod
        }
    }

    void "Invalid gradientPaint format '#format'"() {
        setup:
        def converter = new RadialGradientPaintConverter()

        when:
        converter.fromObject(format)

        then:
        thrown(ConversionException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            '1, 2, 3, 4, 5',
            [1, 2, 3],
            [1, 2, 3, 4, 5],
            [c1: 'a'],
            [new Object()],
            [c1: new Object()],
            new Object(),
            '2,2,5,4,4,[0:1],[BLACK:WHITE:RED]',
            '2,2,5,4,4,0:1,[BLACK:WHITE:RED]',
            '2,2,5,4,4,[0:1],BLACK:WHITE',
            [2, 2, 5, 4, 4, [0, 1], [BLACK, WHITE, RED]],
            [cx: 2, cy: 2, radius: '5', fx: 4, fy: 4, fractions: [0, 1], colors: [BLACK, WHITE, RED]]
        ]
    }

    void "Formatted radial gradient '#expected'"() {
        given:
        def converter = new RadialGradientPaintConverter()

        when:
        String actual = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value        | expected
        null         | null
        sharedPaint  | '2.0, 2.0, 5.0, 4.0, 4.0, [0.0:1.0], [#000000:#ffffff], NO_CYCLE'
        sharedPaintC | '2.0, 2.0, 5.0, 4.0, 4.0, [0.0:1.0], [#000000:#ffffff], REPEAT'
    }
}
