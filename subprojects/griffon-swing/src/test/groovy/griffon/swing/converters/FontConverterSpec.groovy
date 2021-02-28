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

import griffon.swing.TestUtils
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.ConversionException
import java.awt.Font

@Unroll
@IgnoreIf({ TestUtils.headless })
class FontConverterSpec extends Specification {
    void "Font format '#format' should be equal to #font"() {
        setup:
        def converter = new FontConverter()

        when:
        def converted = converter.fromObject(format)

        then:
        font == converted

        where:
        font                                               | format
        null                                               | null
        null                                               | ''
        null                                               | ' '
        null                                               | []
        null                                               | [:]
        new Font('SansSerif', Font.PLAIN, 12)              | 'SansSerif-PLAIN-12'
        new Font('SansSerif', Font.BOLD, 12)               | 'SansSerif-BOLD-12'
        new Font('SansSerif', Font.ITALIC, 12)             | 'SansSerif-ITALIC-12'
        new Font('SansSerif', Font.BOLD | Font.ITALIC, 12) | 'SansSerif-BOLDITALIC-12'
        new Font('SansSerif', Font.PLAIN, 12)              | ['SansSerif', 'PLAIN', '12']
        new Font('SansSerif', Font.BOLD, 12)               | ['SansSerif', 'BOLD', '12']
        new Font('SansSerif', Font.ITALIC, 12)             | ['SansSerif', 'ITALIC', '12']
        new Font('SansSerif', Font.BOLD | Font.ITALIC, 12) | ['SansSerif', 'BOLDITALIC', '12']
        new Font('SansSerif', Font.PLAIN, 12)              | [family: 'SansSerif', style: 'PLAIN', size: '12']
        new Font('SansSerif', Font.BOLD, 12)               | [family: 'SansSerif', style: 'BOLD', size: '12']
        new Font('SansSerif', Font.ITALIC, 12)             | [family: 'SansSerif', style: 'ITALIC', size: '12']
        new Font('SansSerif', Font.BOLD | Font.ITALIC, 12) | [family: 'SansSerif', style: 'BOLDITALIC', size: '12']
        new Font('SansSerif', Font.BOLD | Font.ITALIC, 12) | new Font('SansSerif', Font.BOLD | Font.ITALIC, 12)
    }

    void "Invalid font format '#format'"() {
        setup:
        def converter = new FontConverter()

        when:
        converter.fromObject(format)

        then:
        thrown(ConversionException)

        where:
        format << [
            'garbage',
            'foo-bar-baz',
            'SansSerif-FOO-12',
            'SansSerif-BOLD-baz',
            ['SansSerif'],
            ['SansSerif', 'BOLD'],
            [family: 'SansSerif'],
            [new Object()],
            [family: new Object()],
            new Object()
        ]
    }

    void "Formatted font '#expected'"() {
        given:
        def converter = new FontConverter()

        when:
        String actual = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value                                              | expected
        null                                               | null
        new Font('SansSerif', Font.PLAIN, 12)              | 'SansSerif-PLAIN-12'
        new Font('SansSerif', Font.BOLD, 12)               | 'SansSerif-BOLD-12'
        new Font('SansSerif', Font.ITALIC, 12)             | 'SansSerif-ITALIC-12'
        new Font('SansSerif', Font.BOLD | Font.ITALIC, 12) | 'SansSerif-BOLDITALIC-12'
    }
}
