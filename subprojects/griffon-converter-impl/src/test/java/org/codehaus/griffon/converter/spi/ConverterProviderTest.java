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
package org.codehaus.griffon.converter.spi;

import griffon.converter.Converter;
import griffon.converter.spi.ConverterProvider;
import org.codehaus.griffon.converter.BigDecimalConverter;
import org.codehaus.griffon.converter.BigIntegerConverter;
import org.codehaus.griffon.converter.BooleanConverter;
import org.codehaus.griffon.converter.ByteConverter;
import org.codehaus.griffon.converter.CalendarConverter;
import org.codehaus.griffon.converter.DateConverter;
import org.codehaus.griffon.converter.DoubleConverter;
import org.codehaus.griffon.converter.FileConverter;
import org.codehaus.griffon.converter.FloatConverter;
import org.codehaus.griffon.converter.IntegerConverter;
import org.codehaus.griffon.converter.LocalDateConverter;
import org.codehaus.griffon.converter.LocalDateTimeConverter;
import org.codehaus.griffon.converter.LocalTimeConverter;
import org.codehaus.griffon.converter.LocaleConverter;
import org.codehaus.griffon.converter.LongConverter;
import org.codehaus.griffon.converter.PathConverter;
import org.codehaus.griffon.converter.ShortConverter;
import org.codehaus.griffon.converter.StringConverter;
import org.codehaus.griffon.converter.URIConverter;
import org.codehaus.griffon.converter.URLConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Andres Almiray
 */
public class ConverterProviderTest {
    @ParameterizedTest
    @MethodSource("where_types")
    @SuppressWarnings("rawtypes")
    public <T> void loadAndCheckConverterProvider(Class<T> targetType, Class<? extends Converter<T>> converterType) {
        // given:
        ServiceLoader<ConverterProvider> providers = ServiceLoader.load(ConverterProvider.class);

        // when:
        ConverterProvider converterProvider = StreamSupport.stream(providers.spliterator(), false)
            .filter(cp -> targetType.equals(cp.getTargetType()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Did not find a ConverterProvider for target type " + targetType.getName()));

        // then:
        assertThat(converterProvider.getConverterType(), equalTo(converterType));
    }

    public static Stream<Arguments> where_types() {
        return Stream.of(
            Arguments.of(BigDecimal.class, BigDecimalConverter.class),
            Arguments.of(BigInteger.class, BigIntegerConverter.class),
            Arguments.of(Boolean.class, BooleanConverter.class),
            Arguments.of(Byte.class, ByteConverter.class),
            Arguments.of(Calendar.class, CalendarConverter.class),
            Arguments.of(Date.class, DateConverter.class),
            Arguments.of(Double.class, DoubleConverter.class),
            Arguments.of(File.class, FileConverter.class),
            Arguments.of(Float.class, FloatConverter.class),
            Arguments.of(Integer.class, IntegerConverter.class),
            Arguments.of(LocalDate.class, LocalDateConverter.class),
            Arguments.of(LocalDateTime.class, LocalDateTimeConverter.class),
            Arguments.of(LocalTime.class, LocalTimeConverter.class),
            Arguments.of(Locale.class, LocaleConverter.class),
            Arguments.of(Long.class, LongConverter.class),
            Arguments.of(Path.class, PathConverter.class),
            Arguments.of(Short.class, ShortConverter.class),
            Arguments.of(String.class, StringConverter.class),
            Arguments.of(URI.class, URIConverter.class),
            Arguments.of(URL.class, URLConverter.class)
        );
    }
}
