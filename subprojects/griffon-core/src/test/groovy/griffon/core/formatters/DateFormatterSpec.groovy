/*
 * Copyright 2008-2014 the original author or authors.
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

@Unroll
class DateFormatterSpec extends FormatterSpecSupport {
    void "Date '#value' produces literal '#literal'"() {
        given:
        DateFormatter formatter = new DateFormatter()

        when:
        String str = formatter.format(value)
        Date val = formatter.parse(str)

        Date v1 = value ? clearTime(value) : value
        Date v2 = val ? clearTime(val) : val

        then:
        str == literal
        v1 == v2

        where:
        value         | literal
        epochAsDate() | '1/1/70 12:00 AM'
    }

    void "Date '#value' with pattern '#pattern' produces literal '#literal'"() {
        given:
        DateFormatter formatter = new DateFormatter(pattern)

        when:
        String str = formatter.format(value)
        Date val = formatter.parse(str)

        Date v1 = value ? clearTime(value) : value
        Date v2 = val ? clearTime(val) : val

        then:
        str == literal
        v1?.time == v2?.time

        where:
        pattern      | value         | literal
        'yyyy-MM-dd' | null          | null
        'yyyy-MM-dd' | epochAsDate() | '1970-01-01'
    }

    void "Parse error for pattern '#pattern' with literal '#literal'"() {
        given:
        DateFormatter formatter = new DateFormatter(pattern)

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
        new DateFormatter(pattern)

        then:
        thrown(IllegalArgumentException)

        where:
        pattern << [';garbage*@%&']
    }
}
