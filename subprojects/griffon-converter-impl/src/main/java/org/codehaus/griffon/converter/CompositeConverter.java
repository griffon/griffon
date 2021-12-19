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
import griffon.converter.Converter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class CompositeConverter<T> extends AbstractConverter<T> implements FormattingConverter<T> {
    private final Class<T> targetClass;
    private final Object lock = new Object[0];
    private final WeakReference<Class<? extends Converter<T>>>[] converterClasses;
    private WeakReference<Converter<T>>[] converters;
    private String format;

    @Nullable
    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public CompositeConverter(@Nonnull Class<T> targetClass, @Nonnull Class<? extends Converter<T>>[] converterClasses) {
        this.targetClass = requireNonNull(targetClass, "Argument 'targetClass' must not be null");
        if (converterClasses == null) {
            throw new NullPointerException("Argument 'converterClasses' must not be null");
        }
        if (converterClasses.length == 0) {
            throw new IllegalArgumentException("Argument 'converterClasses' must not be empty");
        }

        // let's make sure converterClasses contains unique elements
        Set<Class<? extends Converter<T>>> classes = new LinkedHashSet<>();
        Collections.addAll(classes, converterClasses);

        int i = 0;
        this.converterClasses = new WeakReference[classes.size()];
        for (Class<? extends Converter<T>> klass : classes) {
            this.converterClasses[i++] = new WeakReference<>(klass);
        }
    }

    public int getSize() {
        return converterClasses.length;
    }

    @Nonnull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public CompositeConverter<T> copyOfByAdding() {
        List<Class<? extends Converter<T>>> classes = new ArrayList<>();

        for (WeakReference<Class<? extends Converter<T>>> reference : converterClasses) {
            if (reference.get() != null) {
                classes.add(reference.get());
            }
        }

        return new CompositeConverter<>(targetClass, classes.toArray(new Class[classes.size()]));
    }

    @Nonnull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public CompositeConverter<T> copyOfByAdding(@Nonnull Class<? extends Converter<T>> converterClass) {
        requireNonNull(converterClass, "Argument 'converterClass' must not be null");

        List<Class<? extends Converter<T>>> classes = new ArrayList<>();
        for (WeakReference<Class<? extends Converter<T>>> reference : converterClasses) {
            if (reference.get() != null) {
                classes.add(reference.get());
            }
        }
        if (!classes.contains(converterClass)) {
            classes.add(converterClass);
        }

        return new CompositeConverter<>(targetClass, classes.toArray(new Class[classes.size()]));
    }

    @Nonnull
    @SuppressWarnings({"unchecked", "rawtypes"})
    CompositeConverter<T> copyOfByRemoving(@Nonnull Class<? extends Converter<T>> converterClass) {
        requireNonNull(converterClass, "Argument 'converterClass' must not be null");

        List<Class<? extends Converter<T>>> classes = new ArrayList<>();
        for (WeakReference<Class<? extends Converter<T>>> reference : converterClasses) {
            if (reference.get() != null) {
                classes.add(reference.get());
            }
        }
        if (classes.contains(converterClass)) {
            classes.remove(converterClass);
        }

        return classes.isEmpty() ? null : new CompositeConverter<>(targetClass, classes.toArray(new Class[classes.size()]));
    }

    @Nonnull
    @SuppressWarnings({"unchecked", "rawtypes"})
    Class<? extends Converter<T>>[] getConverterClasses() {
        List<Class<? extends Converter<T>>> classes = new ArrayList<>();

        for (WeakReference<Class<? extends Converter<T>>> reference : converterClasses) {
            if (reference.get() != null) {
                classes.add(reference.get());
            }
        }

        return classes.toArray(new Class[classes.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[").append(targetClass.getName()).append(']');
        return sb.toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initConverters() {
        synchronized (lock) {
            if (converters == null) {
                List<WeakReference<Converter<T>>> converters = new ArrayList<>();
                for (WeakReference<Class<? extends Converter<T>>> converterClass : converterClasses) {
                    try {
                        Class<? extends Converter<T>> klass = converterClass.get();
                        if (klass != null) {
                            converters.add(new WeakReference<>(klass.newInstance()));
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new IllegalArgumentException("Can't create instance", e);
                    }
                }

                if (!converters.isEmpty()) {
                    this.converters = converters.toArray(new WeakReference[converters.size()]);
                } else {
                    throw new IllegalStateException("No available converters for " + this);
                }
            }
        }
    }

    @Nullable
    @Override
    public T fromObject(@Nullable Object value) throws ConversionException {
        initConverters();

        for (WeakReference<Converter<T>> reference : converters) {
            try {
                Converter<T> converter = reference.get();
                if (converter != null) {
                    if (converter instanceof FormattingConverter) {
                        ((FormattingConverter) converter).setFormat(format);
                    }
                    return converter.fromObject(value);
                }
            } catch (ConversionException e) {
                // ignore. next converter
            }
        }

        throw illegalValue(value, targetClass);
    }

    @Nullable
    @Override
    public String toString(@Nullable T value) {
        initConverters();

        for (WeakReference<Converter<T>> reference : converters) {
            try {
                Converter<T> converter = reference.get();
                if (converter != null) {
                    if (converter instanceof FormattingConverter) {
                        ((FormattingConverter) converter).setFormat(format);
                    }
                    return converter.toString(value);
                }
            } catch (ConversionException e) {
                // ignore. next converter
            }
        }

        throw illegalValue(value, targetClass);
    }
}
