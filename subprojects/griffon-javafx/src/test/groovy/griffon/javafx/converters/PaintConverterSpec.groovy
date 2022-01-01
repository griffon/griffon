/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.javafx.converters

import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Paint
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static javafx.scene.paint.Color.BLACK
import static javafx.scene.paint.Color.WHITE

@Unroll
class PaintConverterSpec extends Specification {
    @Shared
    private RadialGradient sharedLinerPaint = new RadialGradient(1, 2, 3, 4, 5, false, CycleMethod.NO_CYCLE, [new Stop(0d, BLACK), new Stop(1d, WHITE)])

    @Shared
    private RadialGradient sharedLinearPaintC = new RadialGradient(1, 2, 3, 4, 5, false, CycleMethod.REPEAT, [new Stop(0d, BLACK), new Stop(1d, WHITE)])

    @Shared
    private RadialGradient sharedRadialPaint = new RadialGradient(1, 2, 3, 4, 5, false, CycleMethod.NO_CYCLE, [new Stop(0d, BLACK), new Stop(1d, WHITE)])

    @Shared
    private RadialGradient sharedRadialPaintC = new RadialGradient(1, 2, 3, 4, 5, false, CycleMethod.REPEAT, [new Stop(0d, BLACK), new Stop(1d, WHITE)])

    void "Paint format '#format' is supported"() {
        setup:
        PaintConverter converter = new PaintConverter()

        when:
        Paint parsedPaint = converter.fromObject(format)

        then:
        paintsAreEqual value, parsedPaint

        where:
        value                         | format
        null                          | null
        null                          | ''
        null                          | ' '
        null                          | []
        null                          | [:]
        Color.RED                     | 'red'
        Color.RED                     | 'RED'
        Color.RED                     | '#F00'
        Color.RED                     | '#F00F'
        Color.RED                     | '#FF0000'
        Color.RED                     | '#FF0000FF'
        Color.RED                     | ['FF', '00', '00']
        Color.RED                     | ['FF', '00', '00', 'FF']
        Color.RED                     | [1, 0, 0]
        Color.RED                     | [1, 0, 0, 1]
        Color.RED                     | ['FF', 0, 0, 'FF']
        Color.RED                     | [red: 1]
        Color.RED                     | [red: 'FF']
        Color.RED                     | [red: 'FF', green: 0, blue: 0]
        Color.RED                     | [r: 1]
        Color.RED                     | [r: 'FF']
        Color.RED                     | [r: 'FF', g: '00', b: '00']
        new Color(0.25, 0.5, 0.75, 1) | [0.25, 0.5, 0.75, 1]
        new Color(0.25, 0.5, 0.75, 1) | [r: 0.25, g: 0.5, b: 0.75, a: 1]
        new Color(0.25, 0.5, 0.75, 1) | '#4080BF'
        new Color(0.25, 0.5, 0.75, 1) | '#4080BFFF'
        new Color(0.25, 0.5, 0.75, 1) | ['40', '80', 'BF', 'FF']
        new Color(0.25, 0.5, 0.75, 1) | [r: '40', g: '80', b: 'BF', a: 'FF']
        new Color(0.25, 0.5, 0.75, 1) | new Color(0.25, 0.5, 0.75, 1)
        sharedLinerPaint              | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)]]
        sharedLinearPaintC            | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)], 'REPEAT']
        sharedLinearPaintC            | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)], CycleMethod.REPEAT]
        sharedLinerPaint              | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)]]
        sharedLinearPaintC            | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)], cycle: 'REPEAT']
        sharedLinearPaintC            | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)], cycle: CycleMethod.REPEAT]
        sharedLinerPaint              | sharedLinerPaint
        sharedRadialPaint             | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)]]
        sharedRadialPaintC            | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)], 'REPEAT']
        sharedRadialPaintC            | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)], CycleMethod.REPEAT]
        sharedRadialPaint             | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)]]
        sharedRadialPaintC            | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)], cycle: 'REPEAT']
        sharedRadialPaintC            | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)], cycle: CycleMethod.REPEAT]
        sharedRadialPaint             | sharedRadialPaint
    }

    private static void paintsAreEqual(Object p1, Object p2) {
        if (p1 == null) {
            assert p2 == null
        } else {
            _paintsAreEqual(p1, p2)
        }
    }

    private static void _paintsAreEqual(Color p1, Color p2) {
        assert p1?.toString() == p2?.toString()
    }

    private static void _paintsAreEqual(LinearGradient p1, LinearGradient p2) {
        if (p1 == null) {
            assert p2 == null
        } else {
            assert p1.startX == p2.startX &&
                p1.startY == p2.startY &&
                p1.endX == p2.endX &&
                p1.endY == p2.endY &&
                p1.proportional == p2.proportional &&
                p1.stops*.toString() == p2.stops*.toString() &&
                p1.cycleMethod == p2.cycleMethod
        }
    }

    private static void _paintsAreEqual(RadialGradient p1, RadialGradient p2) {
        if (p1 == null) {
            assert p2 == null
        } else {
            assert p1.focusAngle == p2.focusAngle &&
                p1.focusDistance == p2.focusDistance &&
                p1.centerX == p2.centerX &&
                p1.centerY == p2.centerY &&
                p1.radius == p2.radius &&
                p1.proportional == p2.proportional &&
                p1.stops*.toString() == p2.stops*.toString() &&
                p1.cycleMethod == p2.cycleMethod
        }
    }
}
