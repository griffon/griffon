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

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class BooleanConverterTest extends ConversionSupport {
    @ParameterizedTest
    @MethodSource("where_value_format_result")
    public void nullsafe_valueWithFormatProducesResult(Object value, String format, Boolean result) {
        // given:
        BooleanConverter converter = new BooleanConverter();
        converter.setFormat(format);
        converter.setNullAccepted(true);

        // when:
        Boolean output = converter.fromObject(value);

        // then:
        assertThat(output, equalTo(result));
    }

    @ParameterizedTest
    @MethodSource("where_invalid_value")
    public void invalidValueProducesError(Object value) {
        // given:
        BooleanConverter converter = new BooleanConverter();

        // when:
        assertThrows(ConversionException.class, () -> converter.fromObject(value));
    }

    public static Stream<Arguments> where_value_format_result() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of("", null, null),
            Arguments.of("true", null, true),
            Arguments.of("false", null, false),
            Arguments.of(true, null, true),
            Arguments.of(false, null, false),
            Arguments.of("true", "boolean", true),
            Arguments.of("false", "boolean", false),
            Arguments.of("yes", "query", true),
            Arguments.of("no", "query", false),
            Arguments.of("on", "switch", true),
            Arguments.of("off", "switch", false)
        );
    }

    public static Stream<Arguments> where_invalid_value() {
        return Stream.of(
            Arguments.of(Collections.emptyList()),
            Arguments.of(Collections.emptyMap()),
            Arguments.of(Arrays.asList(1, 2, 3)),
            Arguments.of(new Object())
        );
    }
}
