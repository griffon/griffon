/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.converter.ConversionException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.*

@Unroll
class RectangleConverterSpec extends Specification {
    @Shared
    private Rectangle rectangle = new Rectangle(10, 20, 30, 40)

    void "Rectangle format '#format' should be equal to #rectangle"() {
        setup:
        def converter = new RectangleConverter()

        when:
        def converted = converter.fromObject(format)

        then:
        value == converted

        where:
        value                     | format
        null                      | null
        null                      | ''
        null                      | ' '
        null                      | []
        null                      | [:]
        rectangle                 | '10,20,30,40'
        rectangle                 | '10, 20, 30, 40'
        rectangle                 | ' 10, 20, 30, 40'
        rectangle                 | ' 10, 20, 30, 40 '
        rectangle                 | [10, 20, 30, 40]
        rectangle                 | ['10', '20', '30', '40']
        rectangle                 | [x: 10, y: 20, width: 30, height: 40]
        rectangle                 | [x: '10', y: '20', width: '30', height: '40']
        rectangle                 | [x: 10, y: 20, w: 30, h: 40]
        rectangle                 | [x: '10', y: '20', w: '30', h: '40']
        rectangle                 | rectangle
        new Rectangle(0, 0, 0, 0) | [foo: 10, bar: 20]
    }

    void "Invalid rectangle format '#format'"() {
        setup:
        def converter = new RectangleConverter()

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
        def converter = new RectangleConverter()

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
