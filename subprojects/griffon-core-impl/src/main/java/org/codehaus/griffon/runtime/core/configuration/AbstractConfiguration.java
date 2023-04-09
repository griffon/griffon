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
package org.codehaus.griffon.runtime.core.configuration;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.Converter;
import griffon.converter.ConverterRegistry;
import griffon.converter.FormattingConverter;
import griffon.core.Configuration;

import java.util.Properties;

import static griffon.core.util.TypeUtils.castToBoolean;
import static griffon.core.util.TypeUtils.castToDouble;
import static griffon.core.util.TypeUtils.castToFloat;
import static griffon.core.util.TypeUtils.castToInt;
import static griffon.core.util.TypeUtils.castToLong;
import static griffon.util.CollectionUtils.toProperties;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractConfiguration implements Configuration {
    private static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    private static final String ERROR_FORMAT_BLANK = "Argument 'format' must not be blank";

    protected ConverterRegistry converterRegistry;

    public AbstractConfiguration(@Nonnull ConverterRegistry converterRegistry) {
        this.converterRegistry = requireNonNull(converterRegistry, "Argument 'converterRegistry' must not be null");
    }

    @Nonnull
    @Override
    public ConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@Nonnull String key, @Nullable T defaultValue) {
        T value = (T) get(key);
        return value != null ? value : defaultValue;
    }

    @Nullable
    @Override
    public <T> T getAt(@Nonnull String key) {
        return get(key);
    }

    @Nullable
    @Override
    public <T> T getAt(@Nonnull String key, @Nullable T defaultValue) {
        return get(key, defaultValue);
    }

    @Override
    public boolean getAsBoolean(@Nonnull String key) {
        return getAsBoolean(key, false);
    }

    @Override
    public boolean getAsBoolean(@Nonnull String key, boolean defaultValue) {
        return castToBoolean(get(key), defaultValue);
    }

    @Override
    public int getAsInt(@Nonnull String key) {
        return getAsInt(key, 0);
    }

    @Override
    public int getAsInt(@Nonnull String key, int defaultValue) {
        return castToInt(get(key), defaultValue);
    }

    @Override
    public long getAsLong(@Nonnull String key) {
        return getAsLong(key, 0L);
    }

    @Override
    public long getAsLong(@Nonnull String key, long defaultValue) {
        return castToLong(get(key), defaultValue);
    }

    @Override
    public float getAsFloat(@Nonnull String key) {
        return getAsFloat(key, 0f);
    }

    @Override
    public float getAsFloat(@Nonnull String key, float defaultValue) {
        return castToFloat(get(key), defaultValue);
    }

    @Override
    public double getAsDouble(@Nonnull String key) {
        return getAsDouble(key, 0d);
    }

    @Override
    public double getAsDouble(@Nonnull String key, double defaultValue) {
        return castToDouble(get(key), defaultValue);
    }

    @Nullable
    @Override
    public String getAsString(@Nonnull String key) {
        return getAsString(key, null);
    }

    @Nullable
    @Override
    public <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type) {
        requireNonNull(type, ERROR_TYPE_NULL);
        return convertValue(get(key), type);
    }

    @Nullable
    @Override
    public <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nullable T defaultValue) {
        T value = getConverted(key, type);
        return type.cast(value != null ? value : defaultValue);
    }

    @Nullable
    @Override
    public <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nonnull String format) {
        requireNonNull(type, ERROR_TYPE_NULL);
        return convertValue(get(key), type, format);
    }

    @Nullable
    @Override
    public <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nonnull String format, @Nullable T defaultValue) {
        T value = getConverted(key, type, format);
        return type.cast(value != null ? value : defaultValue);
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertValue(@Nullable Object value, @Nonnull Class<T> type) {
        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                return (T) value;
            } else {
                Converter<T> converter = converterRegistry.findConverter(type);
                if (null != converter) {
                    return converter.fromObject(value);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T> T convertValue(@Nullable Object value, @Nonnull Class<T> type, @Nonnull String format) {
        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                return (T) value;
            } else {
                Converter<T> converter = converterRegistry.findConverter(type);
                if (null != converter) {
                    if (converter instanceof FormattingConverter) {
                        ((FormattingConverter) converter).setFormat(format);
                    }
                    return converter.fromObject(value);
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getAsString(@Nonnull String key, @Nullable String defaultValue) {
        Object value = get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    @Nonnull
    @Override
    public Properties asProperties() {
        return toProperties(asFlatMap());
    }
}
