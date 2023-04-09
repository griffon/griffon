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

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class LocaleConverterTest extends ConversionSupport {
    private static final Locale DE_CH_BASEL = new Locale("de", "CH", "Basel");

    @ParameterizedTest
    @MethodSource("where_bidirectional")
    public void bidirectionalConversion(Object value, Locale locale, String literal) {
        // given:
        LocaleConverter formatter = new LocaleConverter();

        // when:
        Locale lcl = formatter.fromObject(value);
        String str = formatter.toString(locale);

        // then:
        assertThat(str, equalTo(literal));
        assertThat(lcl, equalTo(locale));
    }

    @ParameterizedTest
    @MethodSource("where_invalid")
    public void checkInvalidConversion(Object value) {
        // given:
        LocaleConverter converter = new LocaleConverter();

        // when:
        assertThrows(ConversionException.class, () -> converter.fromObject(value));
    }

    public static Stream<Arguments> where_bidirectional() {
        return Stream.of(
            Arguments.of(null, null, null),
            Arguments.of("", null, null),
            Arguments.of(" ", null, null),
            Arguments.of(Locale.ENGLISH, Locale.ENGLISH, "en"),
            Arguments.of("en", Locale.ENGLISH, "en")
        );
    }

    public static Stream<Arguments> where_invalid() {
        return Stream.of(
            Arguments.of(1),
            Arguments.of(new Object()),
            Arguments.of(Collections.emptyList()),
            Arguments.of(Collections.emptyMap())
        );
    }
}
