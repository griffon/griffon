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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConversionException;
import org.codehaus.griffon.formatter.DateFormatter;
import griffon.formatter.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class DateConverter extends AbstractFormattingConverter<Date> {
    @Nullable
    @Override
    protected Date convertFromObject(@Nullable Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return convertFromString(String.valueOf(value).trim());
        } else if (value instanceof LocalDate) {
            return convertFromLocalDate((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            return convertFromLocalDate(((LocalDateTime) value).toLocalDate());
        } else if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        } else if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        } else {
            throw illegalValue(value, Date.class);
        }
    }

    @Nullable
    protected Date convertFromString(@Nonnull String str) {
        if (isBlank(str)) {
            return null;
        }

        try {
            return new Date(Long.parseLong(str));
        } catch (NumberFormatException nfe) {
            // ignore, let's try parsing the date in a locale specific format
        }

        try {
            return new SimpleDateFormat().parse(str);
        } catch (ParseException e) {
            throw illegalValue(str, Date.class, e);
        }
    }

    @Nullable
    protected Date convertFromLocalDate(@Nonnull LocalDate value) {
        return new Date(value.toEpochDay());
    }

    @Nullable
    @Override
    protected Formatter<Date> resolveFormatter(@Nonnull String format) {
        return isBlank(format) ? null : new DateFormatter(format);
    }
}
