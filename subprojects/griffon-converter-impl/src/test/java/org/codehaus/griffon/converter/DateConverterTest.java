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
import java.util.Date;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class DateConverterTest extends ConversionSupport {
    @ParameterizedTest
    @MethodSource("where_value_format_result")
    public void valueWithFormatProducesResult(Object value, String format, Date result) {
        // given:
        DateConverter converter = new DateConverter();
        converter.setFormat(format);

        // when:
        Date output = converter.fromObject(value);

        Date v1 = null != output ? clearTime(output) : output;
        Date v2 = null != result ? clearTime(result) : result;

        // then:
        assertThat(v1, equalTo(v2));
    }

    @ParameterizedTest
    @MethodSource("where_invalid_value")
    public void invalidValueProducesError(Object value) {
        // given:
        DateConverter converter = new DateConverter();

        // when:
        assertThrows(ConversionException.class, () -> converter.fromObject(value));
    }

    public static Stream<Arguments> where_value_format_result() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of("", null, null),
            Arguments.of("1/1/70 12:00 AM", null, epochAsDate()),
            Arguments.of("0", null, epochAsDate()),
            Arguments.of(0, null, epochAsDate()),
            Arguments.of(epochAsDate(), null, epochAsDate()),
            Arguments.of(epochAsCalendar(), null, epochAsDate()),
            Arguments.of("", "yyyy-MM-dd HH:mm:ss", null),
            Arguments.of("1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss", epochAsDate()),
            Arguments.of(0, "yyyy-MM-dd HH:mm:ss", epochAsDate()),
            Arguments.of(epochAsDate(), "yyyy-MM-dd HH:mm:ss", epochAsDate()),
            Arguments.of(epochAsCalendar(), "yyyy-MM-dd HH:mm:ss", epochAsDate()),
            Arguments.of(LocalDate.of(1970, 1, 1), null, epochAsDate()),
            Arguments.of(LocalDateTime.of(1970, 1, 1, 12, 13, 14), null, epochAsDate())
        );
    }

    public static Stream<Arguments> where_invalid_value() {
        return Stream.of(
            Arguments.of("garbage"),
            Arguments.of(Collections.emptyList()),
            Arguments.of(Collections.emptyMap()),
            Arguments.of(Arrays.asList(1, 2, 3)),
            Arguments.of(new Object())
        );
    }
}
