/*
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

import java.time.LocalDate

@Unroll
class LocalDateFormatterSpec extends FormatterSpecSupport {
    void "LocalDate '#value' produces literal '#literal'"() {
        given:
        LocalDateFormatter formatter = new LocalDateFormatter()

        when:
        String str = formatter.format(value)
        LocalDate val = formatter.parse(str)

        then:
        str == literal
        value == val

        where:
        value                    | literal
        LocalDate.of(1970, 1, 1) | '1970-01-01'
    }

    void "LocalDate '#value' with pattern '#pattern' produces literal '#literal'"() {
        given:
        LocalDateFormatter formatter = new LocalDateFormatter(pattern)

        when:
        String str = formatter.format(value)
        LocalDate val = formatter.parse(str)

        then:
        pattern == formatter.pattern
        str == literal
        value == val

        where:
        pattern      | value                    | literal
        'yyyy-MM-dd' | null                     | null
        'yyyy-MM-dd' | LocalDate.of(1970, 1, 1) | '1970-01-01'
    }

    void "Parse error for pattern '#pattern' with literal '#literal'"() {
        given:
        LocalDateFormatter formatter = new LocalDateFormatter(pattern)

        when:
        formatter.parse(literal)

        then:
        thrown(ParseException)

        where:
        pattern      | literal
        'yyyy-MM-dd' | 'abc'
    }

    void "Illegal pattern '#pattern'"() {
        when:
        new LocalDateFormatter(pattern)

        then:
        thrown(IllegalArgumentException)

        where:
        pattern << [';garbage*@%&']
    }
}
