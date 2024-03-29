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
class DimensionConverterSpec extends Specification {
    @Shared
    private Dimension dimension = new Dimension(10, 20)

    void "Dimension format '#format' should be equal to #dimension"() {
        setup:
        def converter = new DimensionConverter()

        when:
        def converted = converter.fromObject(format)

        then:
        value == converted

        where:
        value                 | format
        null                  | null
        null                  | ''
        null                  | ' '
        null                  | []
        null                  | [:]
        dimension             | '10,20'
        dimension             | '10, 20'
        dimension             | ' 10, 20'
        dimension             | ' 10, 20 '
        dimension             | [10, 20]
        dimension             | ['10', '20']
        new Dimension(10, 10) | 10
        new Dimension(10, 10) | '10'
        new Dimension(10, 10) | [10]
        new Dimension(10, 10) | ['10']
        dimension             | [width: 10, height: 20]
        dimension             | [width: '10', height: '20']
        dimension             | [w: 10, h: 20]
        dimension             | [w: '10', h: '20']
        dimension             | dimension
        new Dimension(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid dimension format '#format'"() {
        setup:
        def converter = new DimensionConverter()

        when:
        converter.fromObject(format)

        then:
        thrown(ConversionException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            [1, 2, 3],
            [width: 'a'],
            [w: 'b'],
            [new Object()],
            [w: new Object()],
            new Object()
        ]
    }

    void "Formatted dimension '#expected'"() {
        given:
        def converter = new DimensionConverter()

        when:
        String actual = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value     | expected
        null      | null
        dimension | '10.0, 20.0'
    }
}
