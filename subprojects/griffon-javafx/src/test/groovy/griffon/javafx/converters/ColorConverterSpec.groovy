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

import javafx.scene.paint.Color
import spock.lang.Specification
import spock.lang.Unroll

import griffon.converter.ConversionException

@Unroll
class ColorConverterSpec extends Specification {
    void "Color format '#format' should be equal to #color"() {
        setup:
        ColorConverter converter = new ColorConverter()

        when:
        Color parsedColor = converter.fromObject(format)

        then:
        // use string comparison to avoid inequality on double values
        color?.toString() == parsedColor?.toString()

        where:
        color                         | format
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
        Color.WHITE                   | 1
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
    }

    void "Invalid color format '#format'"() {
        setup:
        ColorConverter converter = new ColorConverter()

        when:
        converter.fromObject(format)

        then:
        thrown(ConversionException)

        where:
        format << [
            'garbage',
            [1],
            [1, 2],
            [1, 2, 3, 4, 5],
            'F00',
            '#F0',
            '#FF0000FF00',
            ['HH', 'FF', '00'],
            [new Object()],
            [new Object(), new Object(), new Object()],
            [r: new Object()],
            new Object()
        ]
    }

    void "Formatted color '#expected' with format #format"() {
        given:
        ColorConverter converter = new ColorConverter()

        when:
        converter.format = format
        String actual  = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value     | format      | expected
        null      | null        | null
        Color.RED | null        | '#ff0000'
        Color.RED | '#RGB'      | '#f00'
        Color.RED | '#RGBA'     | '#f00f'
        Color.RED | '#RRGGBB'   | '#ff0000'
        Color.RED | '#RRGGBBAA' | '#ff0000ff'
    }
}
