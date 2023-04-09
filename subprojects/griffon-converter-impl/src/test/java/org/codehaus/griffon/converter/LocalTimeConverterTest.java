/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class LocalTimeConverterTest extends ConversionSupport {
    @ParameterizedTest
    @MethodSource("where_value_format_result")
    public void valueWithFormatProducesResult(Object value, String format, LocalTime result) {
        // given:
        LocalTimeConverter converter = new LocalTimeConverter();
        converter.setFormat(format);

        // when:
        LocalTime output = converter.fromObject(value);

        // then:
        assertThat(output, equalTo(result));
    }

    @ParameterizedTest
    @MethodSource("where_invalid_value")
    public void invalidValueProducesError(Object value) {
        // given:
        LocalTimeConverter converter = new LocalTimeConverter();

        // when:
        assertThrows(ConversionException.class, () -> converter.fromObject(value));
    }

    public static Stream<Arguments> where_value_format_result() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of("", null, null),
            Arguments.of(emptyList(), null, null),
            Arguments.of("01:02:03.400", null, LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(0, null, LocalTime.of(0, 0, 0, 0)),
            Arguments.of(epochAsDate(), null, LocalTime.of(0, 0, 0, 0)),
            Arguments.of(epochAsCalendar(), null, LocalTime.of(0, 0, 0, 0)),
            Arguments.of(asList(1, 2, 3), null, LocalTime.of(1, 2, 3)),
            Arguments.of(asList(1, 2, 3, 400000000), null, LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(asList("1", "2", "3", "400000000"), null, LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(LocalTime.of(1, 2, 3, 400000000), null, LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(LocalDateTime.of(1970, 1, 1, 1, 2, 3, 400000000), null, LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of("", "HH:mm:ss.SSS", null),
            Arguments.of("01:02:03.400", "HH:mm:ss.SSS", LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(0, "HH:mm:ss.SSS", LocalTime.of(0, 0, 0, 0)),
            Arguments.of(epochAsDate(), "HH:mm:ss.SSS", LocalTime.of(0, 0, 0, 0)),
            Arguments.of(epochAsCalendar(), "HH:mm:ss.SSS", LocalTime.of(0, 0, 0, 0)),
            Arguments.of(asList(1, 2, 3), "HH:mm:ss.SSS", LocalTime.of(1, 2, 3, 0)),
            Arguments.of(asList(1, 2, 3, 400000000), "HH:mm:ss.SSS", LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(asList("1", "2", "3", "400000000"), "HH:mm:ss.SSS", LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(LocalTime.of(1, 2, 3, 400000000), "HH:mm:ss.SSS", LocalTime.of(1, 2, 3, 400000000)),
            Arguments.of(LocalDateTime.of(1970, 1, 1, 1, 2, 3, 400000000), "HH:mm:ss.SSS", LocalTime.of(1, 2, 3, 400000000))

        );
    }

    public static Stream<Arguments> where_invalid_value() {
        return Stream.of(
            Arguments.of("garbage"),
            Arguments.of(Collections.emptyMap()),
            Arguments.of(asList(1, 2)),
            Arguments.of(asList(1, 2, 3, 4, 5)),
            Arguments.of(new Object())
        );
    }
}
