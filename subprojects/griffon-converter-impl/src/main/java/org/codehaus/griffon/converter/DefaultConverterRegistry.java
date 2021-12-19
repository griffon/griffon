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
import griffon.converter.Converter;
import griffon.converter.ConverterRegistry;
import griffon.converter.spi.ConverterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public final class DefaultConverterRegistry implements ConverterRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultConverterRegistry.class);
    private static final String ERROR_TARGET_TYPE_NULL = "Argument 'targetType' must not be null";
    private static final String ERROR_CONVERTER_CLASS_NULL = "Argument 'converterClass' must not be null";
    private final Object lock = new Object[0];
    // @GuardedBy("lock")
    private final WeakCache<String, Class<? extends Converter<?>>> converterCache = new WeakCache<>();
    // @GuardedBy("lock")
    private final Map<String, CompositeConverter<?>> compositeConverterCache = new ConcurrentHashMap<>();

    public DefaultConverterRegistry() {
        loadConverters();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void loadConverters() {
        synchronized (lock) {
            for (ConverterProvider provider : ServiceLoader.load(ConverterProvider.class)) {
                registerConverter(provider.getTargetType(), provider.getConverterType());
            }

            registerConverter(Integer.TYPE, (Class<Converter<Integer>>) converterCache.get(Integer.class.getName()));
            registerConverter(Boolean.TYPE, (Class<Converter<Boolean>>) converterCache.get(Boolean.class.getName()));
            registerConverter(Byte.TYPE, (Class<Converter<Byte>>) converterCache.get(Byte.class.getName()));
            registerConverter(Short.TYPE, (Class<Converter<Short>>) converterCache.get(Short.class.getName()));
            registerConverter(Long.TYPE, (Class<Converter<Long>>) converterCache.get(Long.class.getName()));
            registerConverter(Float.TYPE, (Class<Converter<Float>>) converterCache.get(Float.class.getName()));
            registerConverter(Double.TYPE, (Class<Converter<Double>>) converterCache.get(Double.class.getName()));
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> void registerConverter(@Nonnull Class<T> targetType, @Nonnull Class<? extends Converter<T>> converterClass) {
        requireNonNull(targetType, ERROR_TARGET_TYPE_NULL);
        requireNonNull(converterClass, ERROR_CONVERTER_CLASS_NULL);
        synchronized (lock) {
            String targetTypeName = targetType.getName();

            // is targetType handled by a converter?
            CompositeConverter<T> converter = (CompositeConverter<T>) compositeConverterCache.get(targetTypeName);
            if (converter != null) {
                CompositeConverter<T> copy = converter.copyOfByAdding(converterClass);
                if (copy.getSize() > 1) {
                    compositeConverterCache.put(targetTypeName, copy);
                } else {
                    // standard registration
                    compositeConverterCache.remove(targetTypeName);
                    converterCache.put(targetTypeName, converterClass);
                }
            } else {
                // is targetType handled by a converter ?
                Class<Converter<T>> converterType = (Class<Converter<T>>) converterCache.get(targetTypeName);
                if (converterType != null) {
                    converterCache.remove(targetTypeName);
                    Class<? extends Converter<T>>[] converterClasses = new Class[2];
                    converterClasses[0] = converterType;
                    converterClasses[1] = converterClass;
                    converter = new CompositeConverter<T>(targetType, converterClasses);
                    if (converter.getSize() > 1) {
                        compositeConverterCache.put(targetTypeName, converter);
                    } else {
                        // standard registration
                        compositeConverterCache.remove(targetTypeName);
                        converterCache.put(targetTypeName, converterClass);
                    }
                } else {
                    // standard registration
                    compositeConverterCache.remove(targetTypeName);
                    converterCache.put(targetTypeName, converterClass);
                }
            }
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> void unregisterConverter(@Nonnull Class<T> targetType, @Nonnull Class<? extends Converter<T>> converterClass) {
        requireNonNull(targetType, ERROR_TARGET_TYPE_NULL);

        synchronized (lock) {
            String targetTypeName = targetType.getName();
            if (converterClass == null) {
                compositeConverterCache.remove(targetTypeName);
                converterCache.remove(targetTypeName);
                return;
            }

            if (converterClass.equals(converterCache.get(targetTypeName))) {
                converterCache.remove(targetTypeName);
                return;
            }

            CompositeConverter<T> converter = (CompositeConverter<T>) compositeConverterCache.get(targetTypeName);
            if (converter != null) {
                converter = converter.copyOfByRemoving(converterClass);
                if (converter == null) {
                    compositeConverterCache.remove(targetTypeName);
                } else if (converter.getSize() == 1) {
                    compositeConverterCache.remove(targetTypeName);
                    converterCache.put(targetTypeName, converter.getConverterClasses()[0]);
                } else {
                    compositeConverterCache.put(targetTypeName, converter);
                }
            }
        }
    }

    @Nullable
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> Converter<T> findConverter(@Nonnull Class<T> targetType) {
        requireNonNull(targetType, ERROR_TARGET_TYPE_NULL);
        LOG.trace("Searching PropertyEditor for {}", targetType.getName());

        Converter<?> converter = null;
        if (Enum.class.isAssignableFrom(targetType)) {
            converter = new EnumConverter();
            ((EnumConverter) converter).setEnumType(targetType);
        } else {
            converter = doFindConverter(targetType);
        }

        if (converter != null) {
            LOG.trace("Converter for {} is {}", targetType.getName(), converter.getClass().getName());
        }
        return (Converter<T>) converter;
    }

    @Override
    public void clear() {
        synchronized (lock) {
            converterCache.clear();
            compositeConverterCache.clear();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> Converter<T> doFindConverter(Class<T> targetType) {
        synchronized (lock) {
            String targetTypeName = targetType.getName();
            if (compositeConverterCache.containsKey(targetTypeName)) {
                CompositeConverter<T> converter = (CompositeConverter<T>) compositeConverterCache.get(targetTypeName);
                return converter.copyOfByAdding();
            } else {
                Class<? extends Converter<?>> converterType = converterCache.get(targetTypeName);
                if (converterType != null) {
                    try {
                        return (Converter<T>) converterType.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new IllegalStateException("Can't instantiate " + converterType, e);
                    }
                }
            }
        }
        return null;
    }

    private static final class WeakCache<K, V> {
        private final Map<K, Reference<V>> map = new WeakHashMap<>();

        private V get(K key) {
            Reference<V> reference = this.map.get(key);
            if (reference == null) {
                return null;
            } else {
                V value = reference.get();
                if (value == null) {
                    this.map.remove(key);
                }

                return value;
            }
        }

        private void put(K key, V value) {
            if (value != null) {
                this.map.put(key, new WeakReference<>(value));
            } else {
                this.map.remove(key);
            }
        }

        private void remove(K key) {
            this.map.remove(key);
        }

        private void clear() {
            this.map.clear();
        }
    }
}
