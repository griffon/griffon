/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2021 the original author or authors.
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
import org.codehaus.griffon.formatter.CalendarFormatter;
import griffon.formatter.Formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class CalendarConverter extends AbstractFormattingConverter<Calendar> {
    @Nullable
    @Override
    protected Calendar convertFromObject(@Nullable Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return convertFromString(String.valueOf(value).trim());
        } else if (value instanceof LocalDate) {
            return convertFromLocalDate((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            return convertFromLocalDateTime(((LocalDateTime) value));
        } else if (value instanceof Calendar) {
            return (Calendar) value;
        } else if (value instanceof Date) {
            return convertFromDate((Date) value);
        } else if (value instanceof Number) {
            return convertFromNumber((Number) value);
        } else {
            throw illegalValue(value, Calendar.class);
        }
    }

    @Nullable
    protected Calendar convertFromString(@Nonnull String str) {
        if (isBlank(str)) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new Date(Long.parseLong(str)));
            return c;
        } catch (NumberFormatException nfe) {
            // ignore, let's try parsing the date in a locale specific format
        }

        try {
            c.setTime(new SimpleDateFormat().parse(str));
            return c;
        } catch (ParseException e) {
            throw illegalValue(str, Calendar.class, e);
        }
    }

    @Nullable
    protected Calendar convertFromNumber(@Nonnull Number value) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(value.longValue()));
        return c;
    }

    @Nullable
    protected Calendar convertFromDate(@Nonnull Date value) {
        Calendar c = Calendar.getInstance();
        c.setTime(value);
        return c;
    }

    @Nullable
    protected Calendar convertFromLocalDate(@Nonnull LocalDate value) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(value.toEpochDay()));
        return c;
    }

    @Nullable
    protected Calendar convertFromLocalDateTime(@Nonnull LocalDateTime value) {
        LocalDate localDate = value.toLocalDate();
        LocalTime localTime = value.toLocalTime();

        Calendar c = Calendar.getInstance();
        c.set(
            localDate.getYear(),
            localDate.getMonthValue() - 1,
            localDate.getDayOfMonth(),
            localTime.getHour(),
            localTime.getMinute(),
            localTime.getSecond()
        );

        return c;
    }

    @Nullable
    @Override
    protected Formatter<Calendar> resolveFormatter(@Nonnull String format) {
        return isBlank(format) ? null : new CalendarFormatter(format);
    }
}
