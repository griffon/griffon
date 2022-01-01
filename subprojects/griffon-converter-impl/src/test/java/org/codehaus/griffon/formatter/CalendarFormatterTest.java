/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package org.codehaus.griffon.formatter;

import griffon.formatter.ParseException;
import org.codehaus.griffon.ConversionSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Calendar;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class CalendarFormatterTest extends ConversionSupport {
    @ParameterizedTest
    @MethodSource("where_value_literal")
    public void valueProducesLiteral(Calendar value, String literal) {
        // given:
        CalendarFormatter formatter = new CalendarFormatter();

        // when:
        String str = formatter.format(value);
        Calendar result = formatter.parse(str);

        Calendar v1 = null != value ? clearTime(value) : value;
        Calendar v2 = null != result ? clearTime(result) : result;

        // then:
        assertThat(str, equalTo(literal));
        assertThat(v1, equalTo(v2));
    }

    public static Stream<Arguments> where_value_literal() {
        return Stream.of(
            Arguments.of(epochAsCalendar(), "1/1/70 12:00 AM")
        );
    }

    @ParameterizedTest
    @MethodSource("where_pattern_value_literal")
    public void valueWithPatternProducesLiteral(String pattern, Calendar value, String literal) {
        // given:
        CalendarFormatter formatter = new CalendarFormatter(pattern);

        // when:
        String str = formatter.format(value);
        Calendar result = formatter.parse(str);

        Calendar v1 = null != value ? clearTime(value) : value;
        Calendar v2 = null != result ? clearTime(result) : result;

        // then:
        assertThat(pattern, equalTo(formatter.getPattern()));
        assertThat(str, equalTo(literal));
        assertThat(v1, equalTo(v2));
    }

    public static Stream<Arguments> where_pattern_value_literal() {
        return Stream.of(
            Arguments.of("yyyy-MM-dd", null, null),
            Arguments.of("yyyy-MM-dd", epochAsCalendar(), "1970-01-01")
        );
    }

    @ParameterizedTest
    @MethodSource("where_parse_error")
    public void parseErrorWithPatternAndLiteral(String pattern, String literal) {
        // given:
        CalendarFormatter formatter = new CalendarFormatter(pattern);

        // when:
        assertThrows(ParseException.class, () -> formatter.parse(literal));
    }

    public static Stream<Arguments> where_parse_error() {
        return Stream.of(
            Arguments.of("yyyy-MM-dd", "abc")
        );
    }

    @ParameterizedTest
    @MethodSource("where_invalid_pattern")
    public void createFormatterWithInvalidPattern(String pattern) {
        // expect:
        assertThrows(IllegalArgumentException.class, () -> new CalendarFormatter(pattern));
    }

    public static Stream<Arguments> where_invalid_pattern() {
        return Stream.of(
            Arguments.of(";garbage*@%&")
        );
    }
}
