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
package org.codehaus.griffon.converter;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConversionException;
import griffon.formatter.Formatter;
import griffon.formatter.ParseException;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public abstract class AbstractFormattingConverter<T> extends AbstractConverter<T> implements FormattingConverter<T> {
    private String format;

    @Nullable
    @Override
    public String getFormat() {
        return format;
    }

    @Nullable
    @Override
    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    @Nullable
    @Override
    public T fromObject(@Nullable Object value) throws ConversionException {
        if (value instanceof CharSequence) {
            String literal = String.valueOf(value);
            Formatter<T> formatter = resolveFormatter(format);

            if (formatter != null) {
                try {
                    return formatter.parse(literal);
                } catch (ParseException e) {
                    throw new ConversionException(value, e);
                }
            }
        }
        return convertFromObject(value);
    }

    @Nullable
    protected abstract T convertFromObject(@Nullable Object value) throws ConversionException;

    @Nullable
    @Override
    public String toString(@Nullable T value) throws ConversionException {
        if (null == value) {
            return null;
        }

        if (null == format) {
            return String.valueOf(value);
        }

        return getFormatted(value, format);
    }

    @Nullable
    protected String getFormatted(@Nullable T value, @Nonnull String format) throws ConversionException {
        Formatter<T> formatter = resolveFormatter(format);
        if (formatter != null) {
            return formatter.format(value);
        }
        return value != null ? value.toString() : null;
    }

    @Nullable
    protected abstract Formatter<T> resolveFormatter(@Nonnull String format);
}
