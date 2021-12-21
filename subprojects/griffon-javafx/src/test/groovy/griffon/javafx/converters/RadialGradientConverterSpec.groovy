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
package griffon.javafx.converters

import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import griffon.converter.ConversionException

import static javafx.scene.paint.Color.BLACK
import static javafx.scene.paint.Color.WHITE

@Unroll
class RadialGradientConverterSpec extends Specification {
    @Shared
    private RadialGradient sharedPaint = new RadialGradient(1, 2, 3, 4, 5, false, CycleMethod.NO_CYCLE, [new Stop(0d, BLACK), new Stop(1d, WHITE)])

    @Shared
    private RadialGradient sharedPaintC = new RadialGradient(1, 2, 3, 4, 5, false, CycleMethod.REPEAT, [new Stop(0d, BLACK), new Stop(1d, WHITE)])

    void "RadialGradient format '#format' is supported"() {
        setup:
        RadialGradientConverter converter = new RadialGradientConverter()

        when:
        RadialGradient parsedRadialGradient = converter.fromObject(format)

        then:
        paintsAreEqual value, parsedRadialGradient

        where:
        value        | format
        null         | null
        null         | ''
        null         | ' '
        null         | []
        null         | [:]
        sharedPaint  | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)]]
        sharedPaintC | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)], 'REPEAT']
        sharedPaintC | [1, 2, 3, 4, 5, false, [new Stop(0d, BLACK), new Stop(1d, WHITE)], CycleMethod.REPEAT]
        sharedPaint  | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)]]
        sharedPaintC | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)], cycle: 'REPEAT']
        sharedPaintC | [fa: 1, fd: 2, cx: 3, cy: 4, r: 5, stops: [new Stop(0d, BLACK), new Stop(1d, WHITE)], cycle: CycleMethod.REPEAT]
        sharedPaint  | sharedPaint
    }

    private static void paintsAreEqual(RadialGradient p1, RadialGradient p2) {
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

    void "Invalid gradient format '#format'"() {
        setup:
        RadialGradientConverter converter = new RadialGradientConverter()

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
            [fa: 'a'],
            [new Object()],
            [fa: new Object()],
            new Object()
        ]
    }
}
