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

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class LocalDateTimeFormatterTest extends ConversionSupport {
    @ParameterizedTest
    @MethodSource("where_value_literal")
    public void valueProducesLiteral(LocalDateTime value, String literal) {
        // given:
        LocalDateTimeFormatter formatter = new LocalDateTimeFormatter();

        // when:
        String str = formatter.format(value);
        LocalDateTime result = formatter.parse(str);

        // then:
        assertThat(str, equalTo(literal));
        assertThat(result, equalTo(value));
    }

    public static Stream<Arguments> where_value_literal() {
        return Stream.of(
            Arguments.of(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0), "1970-01-01T00:00:00")
        );
    }

    @ParameterizedTest
    @MethodSource("where_pattern_value_literal")
    public void valueWithPatternProducesLiteral(String pattern, LocalDateTime value, String literal) {
        // given:
        LocalDateTimeFormatter formatter = new LocalDateTimeFormatter(pattern);

        // when:
        String str = formatter.format(value);
        LocalDateTime result = formatter.parse(str);

        // then:
        assertThat(str, equalTo(literal));
        assertThat(result, equalTo(value));
    }

    public static Stream<Arguments> where_pattern_value_literal() {
        return Stream.of(
            Arguments.of("yyyy-MM-dd HH:mm:ss", null, null),
            Arguments.of("yyyy-MM-dd HH:mm:ss", LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0), "1970-01-01 00:00:00")
        );
    }

    @ParameterizedTest
    @MethodSource("where_parse_error")
    public void parseErrorWithPatternAndLiteral(String pattern, String literal) {
        // given:
        LocalDateTimeFormatter formatter = new LocalDateTimeFormatter(pattern);

        // when:
        assertThrows(ParseException.class, () -> formatter.parse(literal));
    }

    public static Stream<Arguments> where_parse_error() {
        return Stream.of(
            Arguments.of("yyyy-MM-dd HH:mm:ss", "abc")
        );
    }

    @ParameterizedTest
    @MethodSource("where_invalid_pattern")
    public void createFormatterWithInvalidPattern(String pattern) {
        // expect:
        assertThrows(IllegalArgumentException.class, () -> new LocalDateTimeFormatter(pattern));
    }

    public static Stream<Arguments> where_invalid_pattern() {
        return Stream.of(
            Arguments.of(";garbage*@%&")
        );
    }
}
