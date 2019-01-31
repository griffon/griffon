/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.pivot.converters

import griffon.pivot.support.Colors
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.ConversionException
import java.awt.Color

@Unroll
class ColorConverterSpec extends Specification {
    void "Color format '#format' should be equal to #color"() {
        setup:
        ColorConverter converter = new ColorConverter()

        when:
        Color parsedColor = converter.fromObject(format)

        then:
        color == parsedColor

        where:
        color                      | format
        null                       | null
        null                       | ''
        null                       | ' '
        null                       | []
        null                       | [:]
        Color.RED                  | 'red'
        Color.RED                  | 'RED'
        Color.RED                  | '#F00'
        Color.RED                  | '#F00F'
        Color.RED                  | '#FF0000'
        Color.RED                  | '#FF0000FF'
        Color.RED                  | ['FF', '00', '00']
        Color.RED                  | ['FF', '00', '00', 'FF']
        Color.RED                  | [255, 0, 0]
        Color.RED                  | [255, 0, 0, 255]
        Color.RED                  | ['FF', 0, 0, 'FF']
        Color.WHITE                | 255
        Color.RED                  | [red: 255]
        Color.RED                  | [red: 'FF']
        Color.RED                  | [red: 'FF', green: 0, blue: 0]
        Color.RED                  | [r: 255]
        Color.RED                  | [r: 'FF']
        Color.RED                  | [r: 'FF', g: '00', b: '00']
        new Color(16, 32, 48, 255) | '#102030'
        new Color(16, 32, 48, 64)  | '#10203040'
        new Color(16, 32, 48, 64)  | [16, 32, 48, 64]
        new Color(16, 32, 48, 64)  | [r: 16, g: 32, b: 48, a: 64]
        new Color(16, 32, 48, 64)  | ['10', '20', '30', '40']
        new Color(16, 32, 48, 64)  | [r: '10', g: '20', b: '30', a: '40']
        Colors.FUCHSIA.color       | 'FUCHSIA'
        Colors.FUCHSIA.color       | 'fuchsia'
        Colors.FUCHSIA.color       | Colors.FUCHSIA.color
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
            [new Object()],
            [new Object(), new Object(), new Object()],
            [red: new Object()],
            'F00',
            '#F0',
            '#FF0000FF00',
            ['HH', 'FF', '00'],
            new Object()
        ]
    }

    void "Formatted color '#expected' with format #format"() {
        given:
        ColorConverter converter = new ColorConverter()

        when:
        converter.format = format
        String actual = converter.toString(converter.fromObject(value))

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
