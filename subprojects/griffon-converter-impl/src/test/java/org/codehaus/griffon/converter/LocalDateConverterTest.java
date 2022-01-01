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
package org.codehaus.griffon.converter;

import griffon.converter.ConversionException;
import org.codehaus.griffon.ConversionSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class LocalDateConverterTest extends ConversionSupport {
    @ParameterizedTest
    @MethodSource("where_value_format_result")
    public void valueWithFormatProducesResult(Object value, String format, LocalDate result) {
        // given:
        LocalDateConverter converter = new LocalDateConverter();
        converter.setFormat(format);

        // when:
        LocalDate output = converter.fromObject(value);

        // then:
        assertThat(output, equalTo(result));
    }

    @ParameterizedTest
    @MethodSource("where_invalid_value")
    public void invalidValueProducesError(Object value) {
        // given:
        LocalDateConverter converter = new LocalDateConverter();

        // when:
        assertThrows(ConversionException.class, () -> converter.fromObject(value));
    }

    public static Stream<Arguments> where_value_format_result() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of("", null, null),
            Arguments.of("1970-01-01", null, LocalDate.of(1970, 1, 1)),
            Arguments.of(0, null, LocalDate.of(1970, 1, 1)),
            Arguments.of(epochAsDate(), null, LocalDate.of(1970, 1, 1)),
            Arguments.of(epochAsCalendar(), null, LocalDate.of(1970, 1, 1)),
            Arguments.of(Arrays.asList(1970, 1, 1), null, LocalDate.of(1970, 1, 1)),
            Arguments.of(Arrays.asList("1970", "1", "1"), null, LocalDate.of(1970, 1, 1)),
            Arguments.of(dateMap(1970, 1, 1), null, LocalDate.of(1970, 1, 1)),
            Arguments.of(dateMap("1970", "1", "1"), null, LocalDate.of(1970, 1, 1)),
            Arguments.of(LocalDate.of(1970, 1, 1), null, LocalDate.of(1970, 1, 1)),
            Arguments.of(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0), null, LocalDate.of(1970, 1, 1)),
            Arguments.of("", "yyyy-MM-dd", null),
            Arguments.of("1970-01-01", "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(0, "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(epochAsDate(), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(epochAsCalendar(), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(Arrays.asList(1970, 1, 1), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(Arrays.asList("1970", "1", "1"), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(dateMap(1970, 1, 1), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(dateMap("1970", "1", "1"), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(LocalDate.of(1970, 1, 1), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(LocalDateTime.of(1970, 1, 1, 0, 0, 0, 0), "yyyy-MM-dd", LocalDate.of(1970, 1, 1)),
            Arguments.of(Collections.emptyList(), null, null),
            Arguments.of(Collections.emptyMap(), null, null)
        );
    }

    public static Stream<Arguments> where_invalid_value() {
        return Stream.of(
            Arguments.of("garbage"),
            Arguments.of(Arrays.asList(1, 2)),
            Arguments.of(Arrays.asList(1, 2, 3, 4)),
            Arguments.of(new Object())
        );
    }
}
