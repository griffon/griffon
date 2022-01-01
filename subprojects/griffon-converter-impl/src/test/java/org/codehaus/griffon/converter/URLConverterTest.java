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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andres Almiray
 */
public class URLConverterTest extends ConversionSupport {
    private static final URL DEFAULT_URL;

    static {
        try {
            DEFAULT_URL = new URL("http://localhost");
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @ParameterizedTest
    @MethodSource("where_value_result")
    public void valueProducesResult(Object value, URL result) {
        // given:
        URLConverter converter = new URLConverter();

        // when:
        URL output = converter.fromObject(value);

        // then:
        assertThat(output, equalTo(result));
    }

    @ParameterizedTest
    @MethodSource("where_invalid_value")
    public void invalidValueProducesError(Object value) {
        // given:
        URLConverter converter = new URLConverter();

        // when:
        assertThrows(ConversionException.class, () -> converter.fromObject(value));
    }

    public static Stream<Arguments> where_value_result() throws Exception {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of("", null),
            Arguments.of(" ", null),
            Arguments.of("http://localhost", DEFAULT_URL),
            Arguments.of(new File("/").getAbsoluteFile(), new File("/").getAbsoluteFile().toURI().toURL()),
            Arguments.of(DEFAULT_URL, DEFAULT_URL)
        );
    }

    public static Stream<Arguments> where_invalid_value() {
        return Stream.of(
            Arguments.of(Collections.emptyList()),
            Arguments.of(Collections.emptyMap()),
            Arguments.of(new Object()),
            Arguments.of("garbage")
        );
    }
}
