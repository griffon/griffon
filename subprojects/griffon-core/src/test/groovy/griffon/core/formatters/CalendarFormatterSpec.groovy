/*
 * Copyright 2008-2015 the original author or authors.
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
class CalendarFormatterSpec extends FormatterSpecSupport {
    void "Calendar '#value' produces literal '#literal'"() {
        given:
        CalendarFormatter formatter = new CalendarFormatter()

        when:
        String str = formatter.format(value)
        Calendar val = formatter.parse(str)

        Calendar v1 = value ? clearTime(value) : value
        Calendar v2 = val ? clearTime(val) : val

        then:

        v1?.time == v2?.time

        then:
        str == literal
        v1 == v2

        where:
        value             | literal
        epochAsCalendar() | '1/1/70 12:00 AM'
    }

    void "Calendar '#value' with pattern '#pattern' produces literal '#literal'"() {
        given:
        CalendarFormatter formatter = new CalendarFormatter(pattern)

        when:
        String str = formatter.format(value)
        Calendar val = formatter.parse(str)

        Calendar v1 = value ? clearTime(value) : value
        Calendar v2 = val ? clearTime(val) : val

        then:
        pattern == formatter.pattern
        str == literal
        v1?.time?.time == v2?.time?.time

        where:
        pattern      | value             | literal
        'yyyy-MM-dd' | null              | null
        'yyyy-MM-dd' | epochAsCalendar() | '1970-01-01'
    }

    void "Parse error for pattern '#pattern' with literal '#literal'"() {
        given:
        CalendarFormatter formatter = new CalendarFormatter(pattern)

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
        new CalendarFormatter(pattern)

        then:
        thrown(IllegalArgumentException)

        where:
        pattern << [';garbage*@%&']
    }
}
