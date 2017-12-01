/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package griffon.core.formatters

import spock.lang.Unroll

import static griffon.core.formatters.IntegerFormatter.PATTERN_CURRENCY
import static griffon.core.formatters.IntegerFormatter.PATTERN_PERCENT

@Unroll
class IntegerFormatterSpec extends FormatterSpecSupport {
    void "Integer '#value' produces literal '#literal'"() {
        given:
        IntegerFormatter formatter = new IntegerFormatter()

        when:
        String str = formatter.format(value)
        Integer val = formatter.parse(str)

        then:
        str == literal
        val == value

        where:
        value      | literal
        100 as int | '100'
    }

    void "Integer '#value' with pattern '#pattern' produces literal '#literal'"() {
        given:
        IntegerFormatter formatter = new IntegerFormatter(pattern)

        when:
        String str = formatter.format(value)
        Integer val = formatter.parse(str)

        then:
        str == literal
        val == value

        where:
        pattern          | value      | literal
        PATTERN_CURRENCY | null       | null
        PATTERN_PERCENT  | null       | null
        PATTERN_CURRENCY | 100 as int | '$100.00'
        PATTERN_PERCENT  | 1 as int   | '100%'
        null             | 100 as int | '100'
        ''               | 100 as int | '100'
        '##.0'           | 20 as int  | '20.0'
    }

    void "Parse error for pattern '#pattern' with literal '#literal'"() {
        given:
        IntegerFormatter formatter = new IntegerFormatter(pattern)

        when:
        formatter.parse(literal)

        then:
        thrown(ParseException)

        where:
        pattern          | literal
        PATTERN_CURRENCY | 'abc'
        PATTERN_PERCENT  | 'abc'
    }

    void "Illegal pattern '#pattern'"() {
        when:
        new IntegerFormatter(pattern)

        then:
        thrown(IllegalArgumentException)

        where:
        pattern << [';garbage*@%&']
    }
}
