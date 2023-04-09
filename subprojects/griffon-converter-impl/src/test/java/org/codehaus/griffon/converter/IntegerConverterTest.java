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

import griffon.converter.NumberConverter;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.codehaus.griffon.formatter.AbstractNumberFormatter.PATTERN_CURRENCY;
import static org.codehaus.griffon.formatter.AbstractNumberFormatter.PATTERN_PERCENT;

/**
 * @author Andres Almiray
 */
public class IntegerConverterTest extends AbstractNumberConverterTestCase<Integer> {
    @Override
    protected NumberConverter<Integer> createConverter() {
        return new IntegerConverter();
    }

    @Override
    protected NumberConverter<Integer> createConverter(String format) {
        NumberConverter<Integer> converter = createConverter();
        converter.setFormat(format);
        return converter;
    }

    public static Stream<Arguments> where_value_format_result() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of("", null, null),
            Arguments.of("1", null, 1),
            Arguments.of("100%", PATTERN_PERCENT, 1),
            Arguments.of("$1.00", PATTERN_CURRENCY, 1),
            Arguments.of(1, null, 1),
            Arguments.of(1L, null, 1)
        );
    }

    public static Stream<Arguments> where_invalid_value() {
        return Stream.of(
            Arguments.of("garbage"),
            Arguments.of("1, 2, 3"),
            Arguments.of(Collections.emptyList()),
            Arguments.of(Collections.emptyMap()),
            Arguments.of(Arrays.asList(1, 2, 3)),
            Arguments.of(new Object())
        );
    }
}
