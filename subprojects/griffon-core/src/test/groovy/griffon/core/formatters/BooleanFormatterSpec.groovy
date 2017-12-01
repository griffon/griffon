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

import spock.lang.Specification
import spock.lang.Unroll

import static griffon.core.formatters.BooleanFormatter.PATTERN_BOOL
import static griffon.core.formatters.BooleanFormatter.PATTERN_QUERY
import static griffon.core.formatters.BooleanFormatter.PATTERN_SWITCH
import static griffon.core.formatters.BooleanFormatter.getInstance
import static griffon.core.formatters.BooleanFormatter.parseBoolean

@Unroll
class BooleanFormatterSpec extends Specification {
    void "Boolean '#value' produces literal '#literal'"() {
        given:
        BooleanFormatter formatter = new BooleanFormatter()

        when:
        String str = formatter.format(value)
        Boolean val = formatter.parse(str)

        then:
        str == literal
        val == value

        where:
        value | literal
        false | 'false'
        true  | 'true'
    }

    void "Boolean '#value' produces '#literal'"() {
        expect:
        value == parseBoolean(literal)

        where:
        value | literal
        null  | null
        null  | ''
        false | 'false'
        true  | 'true'
        false | 'no'
        true  | 'yes'
        false | 'off'
        true  | 'on'
    }

    void "Boolean '#value' with pattern '#pattern' produces literal '#literal'"() {
        given:
        BooleanFormatter formatter = getInstance(pattern)

        when:
        String str = formatter.format(value)
        Boolean val = formatter.parse(str)

        then:
        str == literal
        val == value
        pattern ? pattern == formatter.pattern : true

        where:
        pattern        | value | literal
        null           | null  | null
        null           | false | 'false'
        null           | true  | 'true'
        PATTERN_BOOL   | null  | null
        PATTERN_BOOL   | false | 'false'
        PATTERN_BOOL   | true  | 'true'
        PATTERN_QUERY  | null  | null
        PATTERN_QUERY  | false | 'no'
        PATTERN_QUERY  | true  | 'yes'
        PATTERN_SWITCH | null  | null
        PATTERN_SWITCH | false | 'off'
        PATTERN_SWITCH | true  | 'on'
    }

    void "Parse error for pattern '#pattern' with literal '#literal'"() {
        given:
        BooleanFormatter formatter = new BooleanFormatter(pattern)

        when:
        formatter.parse(literal)

        then:
        thrown(ParseException)

        where:
        pattern        | literal
        PATTERN_BOOL   | 'abc'
        PATTERN_QUERY  | 'abc'
        PATTERN_SWITCH | 'abc'
    }

    void "Illegal pattern '#pattern'"() {
        when:
        new BooleanFormatter(pattern)

        then:
        thrown(IllegalArgumentException)

        where:
        pattern << ['garbage']
    }

    void "Illegal literal '#literal'"() {
        when:
        parseBoolean(literal)

        then:
        thrown(ParseException)

        where:
        literal << ['garbage']
    }
}
