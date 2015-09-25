/*
 * Copyright 2008-2015 the original author or authors.
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

import griffon.core.Configuration;
import griffon.core.MutableConfiguration;
import griffon.util.AbstractMapResourceBundle;
import griffon.util.CompositeResourceBundle;
import griffon.util.ConfigUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public class DelegatingMutableConfiguration extends ConfigurationDecorator implements MutableConfiguration {
    private static final String ERROR_KEY_BLANK = "Argument 'key' must not be blank";
    private static final String ERROR_VALUE_NULL = "Argument 'value' must not be null";

    private final Map<String, Object> mutableKeyValues = new LinkedHashMap<>();
    private final Set<String> removedKeys = new LinkedHashSet<>();

    public DelegatingMutableConfiguration(@Nonnull Configuration delegate) {
        super(delegate);
    }

    @Override
    public void set(@Nonnull String key, @Nonnull Object value) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(value, ERROR_VALUE_NULL);
        mutableKeyValues.put(key, value);
    }

    @Nullable
    @Override
    public Object remove(@Nonnull String key) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        if (mutableKeyValues.containsKey(key)) {
            removedKeys.add(key);
            return mutableKeyValues.remove(key);
        } else if (!removedKeys.contains(key) && delegate.containsKey(key)) {
            removedKeys.add(key);
            return delegate.get(key);
        }
        return null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T removeAs(@Nonnull String key) {
        return (T) remove(key);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T removeConverted(@Nonnull String key, @Nonnull Class<T> type) {
        return convertValue(remove(key), type);
    }

    @Nonnull
    @Override
    public Map<String, Object> asFlatMap() {
        Map<String, Object> flatMap = new LinkedHashMap<>(delegate.asFlatMap());
        flatMap.putAll(mutableKeyValues);
        for (String removedKey : removedKeys) {
            flatMap.remove(removedKey);
        }
        return unmodifiableMap(flatMap);
    }

    @Nonnull
    @Override
    public ResourceBundle asResourceBundle() {
        return new CompositeResourceBundle(asList(new PrivateMapResourceBundle(asFlatMap()), delegate.asResourceBundle()));
    }

    @Nullable
    @Override
    public Object get(@Nonnull String key) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        try {
            return getConfigValue(mutableKeyValues, key);
        } catch (MissingResourceException mre) {
            if (removedKeys.contains(key)) {
                return null;
            }
            return super.get(key);
        }
    }

    @Nullable
    @Override
    public <T> T get(@Nonnull String key, @Nullable T defaultValue) {
        T value = (T) get(key);
        return value != null ? value : defaultValue;
    }

    @Nullable
    @Override
    public String getAsString(@Nonnull String key, @Nullable String defaultValue) {
        Object value = get(key);
        return value != null ? String.valueOf(value) : defaultValue;
    }

    @Override
    public boolean containsKey(@Nonnull String key) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        return ConfigUtils.containsKey(mutableKeyValues, key) || (!removedKeys.contains(key) && delegate.containsKey(key));
    }

    private static class PrivateMapResourceBundle extends AbstractMapResourceBundle {
        private final Map<String, Object> map = new LinkedHashMap<>();

        private PrivateMapResourceBundle(Map<String, Object> map) {
            this.map.putAll(map);
            initialize(entries);
            initializeKeys();
        }

        @Override
        protected void initialize(@Nonnull Map<String, Object> entries) {
            if (map != null && entries != null) {
                entries.putAll(map);
            }
        }
    }
}
