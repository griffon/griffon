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
package org.codehaus.griffon.formatter;

import griffon.formatter.Formatter;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.codehaus.griffon.formatter.AbstractNumberFormatter.PATTERN_CURRENCY;
import static org.codehaus.griffon.formatter.AbstractNumberFormatter.PATTERN_PERCENT;

/**
 * @author Andres Almiray
 */
public class DoubleFormatterTest extends AbstractNumberFormatterTestCase<Double> {
    @Override
    protected Formatter<Double> createFormatter() {
        return new DoubleFormatter();
    }

    @Override
    protected Formatter<Double> createFormatter(String pattern) {
        return new DoubleFormatter(pattern);
    }

    public static Stream<Arguments> where_simple() {
        return Stream.of(
            Arguments.of(100d, "100.0")
        );
    }

    public static Stream<Arguments> where_pattern() {
        return Stream.of(
            Arguments.of(PATTERN_PERCENT, null, null),
            Arguments.of(PATTERN_CURRENCY, 100d, "$100.00"),
            Arguments.of(PATTERN_PERCENT, 1d, "100%"),
            Arguments.of(null, 100d, "100.0"),
            Arguments.of("", 100d, "100.0"),
            Arguments.of("##.0", 20d, "20.0")
        );
    }

    public static Stream<Arguments> where_parse_error() {
        return Stream.of(
            Arguments.of(PATTERN_CURRENCY, "abc"),
            Arguments.of(PATTERN_PERCENT, "abc")
        );
    }

    public static Stream<Arguments> where_invalid_pattern() {
        return Stream.of(
            Arguments.of(";garbage*@%&")
        );
    }
}
