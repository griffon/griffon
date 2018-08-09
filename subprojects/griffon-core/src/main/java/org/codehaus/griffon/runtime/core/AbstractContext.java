/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.Context;
import griffon.exceptions.FieldException;
import griffon.inject.Contextual;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.application.converter.Converter;
import javax.application.converter.ConverterRegistry;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static griffon.util.AnnotationUtils.annotationsOfMethodParameter;
import static griffon.util.AnnotationUtils.findAnnotation;
import static griffon.util.AnnotationUtils.nameFor;
import static griffon.util.GriffonClassUtils.getAllDeclaredFields;
import static griffon.util.GriffonClassUtils.getPropertyDescriptors;
import static griffon.util.GriffonClassUtils.setFieldValue;
import static griffon.util.TypeUtils.castToBoolean;
import static griffon.util.TypeUtils.castToDouble;
import static griffon.util.TypeUtils.castToFloat;
import static griffon.util.TypeUtils.castToInt;
import static griffon.util.TypeUtils.castToLong;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public abstract class AbstractContext implements Context {
    protected final ConverterRegistry converterRegistry;
    protected Context parentContext;

    public AbstractContext(@Nonnull ConverterRegistry converterRegistry, @Nullable Context parentContext) {
        this.converterRegistry = requireNonNull(converterRegistry, "Argument 'converterRegistry' must not be null");
        this.parentContext = parentContext;
    }

    @Nullable
    public Context getParentContext() {
        return parentContext;
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
    public Object getAt(@Nonnull String key) {
        return get(key);
    }

    @Nullable
    @Override
    public <T> T getAt(@Nonnull String key, @Nullable T defaultValue) {
        return get(key, defaultValue);
    }

    @Nullable
    @Override
    public Object get(@Nonnull String key) {
        if (hasKey(key)) {
            return doGet(key);
        } else if (parentContext != null) {
            return parentContext.get(key);
        } else {
            return null;
        }
    }

    @Override
    public void destroy() {
        parentContext = null;
    }

    @Override
    public boolean containsKey(@Nonnull String key) {
        if (hasKey(key)) {
            return true;
        } else if (parentContext != null) {
            return parentContext.containsKey(key);
        }
        return false;
    }

    @Nullable
    protected abstract Object doGet(@Nonnull String key);

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
    public String getAsString(@Nonnull String key, @Nullable String defaultValue) {
        Object value = get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAs(@Nonnull String key) {
        return (T) get(key);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAs(@Nonnull String key, @Nullable T defaultValue) {
        Object value = get(key);
        return (T) (value != null ? value : defaultValue);
    }

    @Nullable
    @Override
    public <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type) {
        requireNonNull(type, "Argument 'type' must not be null");
        return convertValue(get(key), type);
    }

    @Nullable
    @Override
    public <T> T getConverted(@Nonnull String key, @Nonnull Class<T> type, @Nullable T defaultValue) {
        T value = getConverted(key, type);
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

    @Nonnull
    @Override
    public <T> T injectMembers(@Nonnull T instance) {
        requireNonNull(instance, "Argument 'instance' must not be null");

        for (PropertyDescriptor descriptor : getPropertyDescriptors(instance.getClass())) {
            Method method = descriptor.getWriteMethod();
            if (method != null && method.getAnnotation(Contextual.class) != null) {
                String key = nameFor(method);
                Object arg = get(key);

                Nonnull nonNull = findAnnotation(annotationsOfMethodParameter(method, 0), Nonnull.class);
                if (arg == null && nonNull != null) {
                    throw new IllegalStateException("Could not find an instance of type " +
                        method.getParameterTypes()[0].getName() + " under key '" + key +
                        "' to be injected on property '" + descriptor.getName() +
                        "' (" + instance.getClass().getName() + "). Property does not accept null values.");
                }

                try {
                    method.invoke(instance, arg);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        for (Field field : getAllDeclaredFields(instance.getClass())) {
            if (field.getAnnotation(Contextual.class) != null) {
                String key = nameFor(field);
                Object arg = get(key);
                if (arg == null && field.getAnnotation(Nonnull.class) != null) {
                    throw new IllegalStateException("Could not find an instance of type " +
                        field.getType().getName() + " under key '" + key +
                        "' to be injected on field '" + field.getName() +
                        "' (" + instance.getClass().getName() + "). Field does not accept null values.");
                }

                try {
                    setFieldValue(instance, field.getName(), arg);
                } catch (FieldException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return instance;
    }
}
