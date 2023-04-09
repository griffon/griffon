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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import griffon.converter.Converter;
import griffon.converter.ConverterRegistry;
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
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Andres Almiray
 */
public class DefaultConverterRegistryTest {
    @ParameterizedTest
    @MethodSource("where_types")
    @SuppressWarnings("rawtypes")
    public <T> void checkDefaultSetup(Class<T> targetType, Class<? extends Converter<T>> converterType) {
        // given:
        ConverterRegistry converterRegistry = new DefaultConverterRegistry();

        // when:
        Converter<T> converter = converterRegistry.findConverter(targetType);

        // then:
        assertThat(converter, instanceOf(converterType));
    }

    @Test
    public void converterAtIndex0IsCalled() {
        // given:
        Converter1.called = false;
        Converter2.called = false;
        ConverterRegistry converterRegistry = new DefaultConverterRegistry();
        converterRegistry.clear();
        converterRegistry.registerConverter(Integer.class, Converter1.class);
        converterRegistry.registerConverter(Integer.class, Converter2.class);

        // when:
        Converter<Integer> converter = converterRegistry.findConverter(Integer.class);
        Integer result = converter.fromObject("1");

        // then:
        assertThat(result, equalTo(1));
        assertThat(Converter1.called, equalTo(true));
        assertThat(Converter2.called, equalTo(false));
    }


    @Test
    public void converterAtIndex1IsCalled() {
        // given:
        Converter1.called = false;
        Converter2.called = false;
        ConverterRegistry converterRegistry = new DefaultConverterRegistry();
        converterRegistry.clear();
        converterRegistry.registerConverter(Integer.class, Converter1.class);
        converterRegistry.registerConverter(Integer.class, Converter2.class);

        // when:
        Converter<Integer> converter = converterRegistry.findConverter(Integer.class);
        Integer result = converter.fromObject(1);

        // then:
        assertThat(result, equalTo(1));
        assertThat(Converter1.called, equalTo(false));
        assertThat(Converter2.called, equalTo(true));
    }

    @Test
    public void registerAndUnregisterConverter() {
        // given:
        ConverterRegistry converterRegistry = new DefaultConverterRegistry();
        converterRegistry.clear();
        converterRegistry.registerConverter(Integer.class, Converter1.class);

        // when:
        Converter<Integer> converter = converterRegistry.findConverter(Integer.class);

        // then:
        assertThat(converter, instanceOf(Converter1.class));

        // when:
        converterRegistry.unregisterConverter(Integer.class, Converter1.class);
        converter = converterRegistry.findConverter(Integer.class);

        // when:
        assertThat(converter, nullValue());
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
            Arguments.of(URL.class, URLConverter.class),
            Arguments.of(Boolean.TYPE, BooleanConverter.class),
            Arguments.of(Byte.TYPE, ByteConverter.class),
            Arguments.of(Double.TYPE, DoubleConverter.class),
            Arguments.of(Float.TYPE, FloatConverter.class),
            Arguments.of(Integer.TYPE, IntegerConverter.class),
            Arguments.of(Long.TYPE, LongConverter.class),
            Arguments.of(Short.TYPE, ShortConverter.class),
            Arguments.of(MyEnum.class, EnumConverter.class)
        );
    }

    public enum MyEnum {
        ONE,
        TWO
    }

    public static class Converter1 extends IntegerConverter {
        private static boolean called;

        @Override
        public Integer fromObject(Object value) throws ConversionException {
            if (value instanceof CharSequence) {
                called = true;
                return super.fromObject(value);
            } else {
                throw illegalValue(value, Integer.class);
            }
        }
    }

    public static class Converter2 extends IntegerConverter {
        private static boolean called;

        @Override
        public Integer fromObject(Object value) throws ConversionException {
            if (value instanceof Number) {
                called = true;
                return super.fromObject(value);
            } else {
                throw illegalValue(value, Integer.class);
            }
        }
    }
}
