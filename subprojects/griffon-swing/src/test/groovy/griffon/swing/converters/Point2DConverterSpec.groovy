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
package griffon.swing.converters

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.ConversionException
import java.awt.geom.Point2D

@Unroll
class Point2DConverterSpec extends Specification {
    @Shared
    private Point2D point = new Point2D.Double(10, 20)

    void "Point2D format '#format' should be equal to #point"() {
        setup:
        def converter = new Point2DConverter()

        when:
        def converted = converter.fromObject(format)

        then:
        value == converted

        where:
        value                      | format
        null                       | null
        null                       | ''
        null                       | ' '
        null                       | []
        null                       | [:]
        point                      | '10,20'
        point                      | '10, 20'
        point                      | ' 10, 20'
        point                      | ' 10, 20 '
        point                      | [10, 20]
        point                      | ['10', '20']
        new Point2D.Double(10, 10) | 10
        new Point2D.Double(10, 10) | '10'
        new Point2D.Double(10, 10) | [10]
        new Point2D.Double(10, 10) | ['10']
        point                      | [x: 10, y: 20]
        point                      | [x: '10', y: '20']
        point                      | point
        new Point2D.Double(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid point format '#format'"() {
        setup:
        def converter = new Point2DConverter()

        when:
        converter.fromObject(format)

        then:
        thrown(ConversionException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            [1, 2, 3],
            [x: 'a'],
            [y: 'b'],
            [new Object()],
            [x: new Object()],
            new Object()
        ]
    }


    void "Formatted point '#expected'"() {
        given:
        def converter = new Point2DConverter()

        when:
        String actual = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value | expected
        null  | null
        point | '10.0, 20.0'
    }
}
