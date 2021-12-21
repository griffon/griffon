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

import javafx.geometry.Rectangle2D
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import griffon.converter.ConversionException

@Unroll
class Rectangle2DConverterSpec extends Specification {
    @Shared
    private Rectangle2D rectangle = new Rectangle2D(10, 20, 30, 40)

    void "Rectangle2D format '#format' should be equal to #rectangle"() {
        setup:
        Rectangle2DConverter converter = new Rectangle2DConverter()

        when:
        Rectangle2D parsedRectangle = converter.fromObject(format)

        then:
        value == parsedRectangle

        where:
        value                       | format
        null                        | null
        null                        | ''
        null                        | ' '
        null                        | []
        null                        | [:]
        rectangle                   | '10,20,30,40'
        rectangle                   | '10, 20, 30, 40'
        rectangle                   | ' 10, 20, 30, 40'
        rectangle                   | ' 10, 20, 30, 40 '
        rectangle                   | [10, 20, 30, 40]
        rectangle                   | ['10', '20', '30', '40']
        rectangle                   | [x: 10, y: 20, width: 30, height: 40]
        rectangle                   | [x: '10', y: '20', width: '30', height: '40']
        rectangle                   | [x: 10, y: 20, w: 30, h: 40]
        rectangle                   | [x: '10', y: '20', w: '30', h: '40']
        rectangle                   | rectangle
        new Rectangle2D(0, 0, 0, 0) | [foo: 10, bar: 20]
    }

    void "Invalid rectangle format '#format'"() {
        setup:
        Rectangle2DConverter converter = new Rectangle2DConverter()

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
            [x: 'a'],
            [y: 'b'],
            [new Object()],
            [new Object(), new Object(), new Object(), new Object()],
            [x: new Object()],
            new Object()
        ]
    }


    void "Formatted rectangle '#expected'"() {
        given:
        Rectangle2DConverter converter = new Rectangle2DConverter()

        when:
        String actual = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value     | expected
        null      | null
        rectangle | '10.0, 20.0, 30.0, 40.0'
    }
}
