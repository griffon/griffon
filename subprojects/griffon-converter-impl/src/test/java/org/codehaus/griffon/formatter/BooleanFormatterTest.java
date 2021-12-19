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
package org.codehaus.griffon.formatter;


import griffon.formatter.ParseException;
import org.codehaus.griffon.ConversionSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.codehaus.griffon.formatter.BooleanFormatter.PATTERN_BOOL;
import static org.codehaus.griffon.formatter.BooleanFormatter.PATTERN_QUERY;
import static org.codehaus.griffon.formatter.BooleanFormatter.PATTERN_SWITCH;

/**
 * @author Andres Almiray
 */
public class BooleanFormatterTest extends ConversionSupport {
    @ParameterizedTest
    @MethodSource("where_value_literal")
    public void valueProducesLiteral(Boolean value, String literal) {
        // given:
        BooleanFormatter formatter = new BooleanFormatter();

        // when:
        String str = formatter.format(value);
        Boolean result = formatter.parse(str);

        // then:
        assertThat(str, equalTo(literal));
        assertThat(result, equalTo(value));
    }

    public static Stream<Arguments> where_value_literal() {
        return Stream.of(
            Arguments.of(false, "false"),
            Arguments.of(true, "true")
        );
    }

    @ParameterizedTest
    @MethodSource("where_parse_value_literal")
    public void parseValueProducesLiteral(Boolean value, String literal) {
        // expect:
        assertThat(BooleanFormatter.parseBoolean(literal), equalTo(value));
    }

    public static Stream<Arguments> where_parse_value_literal() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of(null, ""),
            Arguments.of(false, "false"),
            Arguments.of(true, "true"),
            Arguments.of(false, "no"),
            Arguments.of(true, "yes"),
            Arguments.of(false, "off"),
            Arguments.of(true, "on")
        );
    }

    @ParameterizedTest
    @MethodSource("where_pattern_value_literal")
    public void valueWithPatternProducesLiteral(String pattern, Boolean value, String literal) {
        // given:
        BooleanFormatter formatter = BooleanFormatter.getInstance(pattern);

        // when:
        String str = formatter.format(value);
        Boolean result = formatter.parse(str);

        // then:
        assertThat(str, equalTo(literal));
        assertThat(result, equalTo(value));
        if (null != pattern) {
            assertThat(formatter.getPattern(), equalTo(pattern));
        }
    }

    public static Stream<Arguments> where_pattern_value_literal() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of(null, false, "false"),
            Arguments.of(null, true, "true"),
            Arguments.of(PATTERN_BOOL, null, null),
            Arguments.of(PATTERN_BOOL, false, "false"),
            Arguments.of(PATTERN_BOOL, true, "true"),
            Arguments.of(PATTERN_QUERY, null, null),
            Arguments.of(PATTERN_QUERY, false, "no"),
            Arguments.of(PATTERN_QUERY, true, "yes"),
            Arguments.of(PATTERN_SWITCH, null, null),
            Arguments.of(PATTERN_SWITCH, false, "off"),
            Arguments.of(PATTERN_SWITCH, true, "on")
        );
    }

    @ParameterizedTest
    @MethodSource("where_parse_error")
    public void parseErrorWithPatternAndLiteral(String pattern, String literal) {
        // given:
        BooleanFormatter formatter = BooleanFormatter.getInstance(pattern);

        // when:
        assertThrows(ParseException.class, () -> formatter.parse(literal));
    }

    public static Stream<Arguments> where_parse_error() {
        return Stream.of(
            Arguments.of(PATTERN_BOOL, "abc"),
            Arguments.of(PATTERN_QUERY, "abc"),
            Arguments.of(PATTERN_SWITCH, "abc")
        );
    }

    @ParameterizedTest
    @MethodSource("where_invalid_pattern")
    public void createFormatterWithInvalidPattern(String pattern) {
        // expect:
        assertThrows(IllegalArgumentException.class, () -> BooleanFormatter.getInstance(pattern));
    }

    public static Stream<Arguments> where_invalid_pattern() {
        return Stream.of(
            Arguments.of(";garbage*@%&")
        );
    }

    @ParameterizedTest
    @MethodSource("where_invalid_literal")
    public void parseInvalidLiteral(String literal) {
        // expect:
        assertThrows(ParseException.class, () -> BooleanFormatter.parseBoolean(literal));
    }

    public static Stream<Arguments> where_invalid_literal() {
        return Stream.of(
            Arguments.of(";garbage*@%&")
        );
    }
}
