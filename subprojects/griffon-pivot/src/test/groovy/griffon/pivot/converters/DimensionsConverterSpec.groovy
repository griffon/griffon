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

import org.apache.pivot.wtk.Dimensions
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.application.converter.ConversionException

@Unroll
class DimensionsConverterSpec extends Specification {
    @Shared
    private Dimensions dimensions = new Dimensions(10, 20)

    void "Dimensions format '#format' should be equal to #dimensions"() {
        setup:
        DimensionsConverter converter = new DimensionsConverter()

        when:
        Dimensions parsedDimensions = converter.fromObject(format)

        then:
        value == parsedDimensions

        where:
        value                  | format
        null                   | null
        null                   | ''
        null                   | ' '
        null                   | []
        null                   | [:]
        dimensions             | '10,20'
        dimensions             | '10, 20'
        dimensions             | ' 10, 20'
        dimensions             | ' 10, 20 '
        dimensions             | [10, 20]
        dimensions             | ['10', '20']
        new Dimensions(10, 10) | 10
        new Dimensions(10, 10) | '10'
        new Dimensions(10, 10) | [10]
        new Dimensions(10, 10) | ['10']
        dimensions             | [width: 10, height: 20]
        dimensions             | [width: '10', height: '20']
        dimensions             | [w: 10, h: 20]
        dimensions             | [w: '10', h: '20']
        dimensions             | dimensions
        new Dimensions(0, 0)   | [foo: 10, bar: 20]
    }

    void "Invalid dimensions format '#format'"() {
        setup:
        DimensionsConverter converter = new DimensionsConverter()

        when:
        converter.fromObject(format)

        then:
        thrown(ConversionException)

        where:
        format << [
            'garbage',
            '1, 2, 3',
            [1, 2, 3],
            [new Object()],
            [w: new Object()],
            [width: 'a'],
            [w: 'b'],
            new Object()
        ]
    }

    void "Formatted dimensions '#expected'"() {
        given:
        DimensionsConverter converter = new DimensionsConverter()

        when:
        String actual = converter.toString(converter.fromObject(value))

        then:
        expected == actual

        where:
        value      | expected
        null       | null
        dimensions | '10, 20'
    }
}
