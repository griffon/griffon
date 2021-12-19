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

import org.codehaus.griffon.ConversionSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Andres Almiray
 */
public class LocaleFormatterTest extends ConversionSupport {
    private static final Locale DE_CH_BASEL = new Locale("de", "CH", "Basel");

    @ParameterizedTest
    @MethodSource("where_bidirectional")
    public void bidirectionalConversion(Locale locale, String literal) {
        // given:
        LocaleFormatter formatter = new LocaleFormatter();

        // when:
        String str = formatter.format(locale);
        Locale lcl = formatter.parse(literal);

        // then:
        assertThat(str, equalTo(literal));
        assertThat(lcl, equalTo(locale));
    }

    @ParameterizedTest
    @MethodSource("where_parse")
    public void checkParse(Locale locale, String literal) {
        // given:
        LocaleFormatter formatter = new LocaleFormatter();

        // when:
        Locale lcl = formatter.parse(literal);

        // then:
        assertThat(lcl, equalTo(locale));
    }

    public static Stream<Arguments> where_bidirectional() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of(Locale.ENGLISH, "en"),
            Arguments.of(Locale.US, "en_US"),
            Arguments.of(Locale.UK, "en_GB"),
            Arguments.of(DE_CH_BASEL, "de_CH_Basel")
        );
    }

    public static Stream<Arguments> where_parse() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of(null, ""),
            Arguments.of(null, " "),
            Arguments.of(Locale.ENGLISH, "en"),
            Arguments.of(Locale.US, "en_US"),
            Arguments.of(DE_CH_BASEL, "de_CH_Basel"),
            Arguments.of(null, "de_CH_Basel_X")
        );
    }
}
