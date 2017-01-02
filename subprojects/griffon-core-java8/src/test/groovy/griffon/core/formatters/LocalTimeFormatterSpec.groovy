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

import java.time.LocalTime

@Unroll
class LocalTimeFormatterSpec extends FormatterSpecSupport {
    void "LocalTime '#value' produces literal '#literal'"() {
        given:
        LocalTimeFormatter formatter = new LocalTimeFormatter()

        when:
        String str = formatter.format(value)
        LocalTime val = formatter.parse(str)

        then:
        str == literal
        value == val

        where:
        value                            | literal
        LocalTime.of(1, 2, 3, 101000000) | '01:02:03.101'
    }

    void "LocalTime '#value' with pattern '#pattern' produces literal '#literal'"() {
        given:
        LocalTimeFormatter formatter = new LocalTimeFormatter(pattern)

        when:
        String str = formatter.format(value)
        LocalTime val = formatter.parse(str)

        then:
        pattern == formatter.pattern
        str == literal
        value == val

        where:
        pattern        | value                            | literal
        'HH mm ss.SSS' | null                             | null
        'HH mm ss.SSS' | LocalTime.of(1, 2, 3, 400000000) | '01 02 03.400'
    }

    void "Parse error for pattern '#pattern' with literal '#literal'"() {
        given:
        LocalTimeFormatter formatter = new LocalTimeFormatter(pattern)

        when:
        formatter.parse(literal)

        then:
        thrown(ParseException)

        where:
        pattern     | literal
        'HH::mm:SS' | 'abc'
    }

    void "Illegal pattern '#pattern'"() {
        when:
        new LocalTimeFormatter(pattern)

        then:
        thrown(IllegalArgumentException)

        where:
        pattern << [';garbage*@%&']
    }
}
