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
package org.codehaus.griffon.runtime.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConverterRegistry;
import griffon.core.Context;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.StringUtils.requireNonBlank;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public class DefaultContext extends AbstractContext {
    protected static final String ERROR_KEY_BLANK = "Argument 'key' must not be blank";
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public DefaultContext(@Nonnull ConverterRegistry converterRegistry) {
        this(converterRegistry, null);
    }

    public DefaultContext(@Nonnull ConverterRegistry converterRegistry, @Nullable Context parentContext) {
        super(converterRegistry, parentContext);
    }

    @Nullable
    @Override
    protected Object doGet(@Nonnull String key) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        return attributes.get(key);
    }

    @Override
    public boolean hasKey(@Nonnull String key) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        return attributes.containsKey(key);
    }

    @Nullable
    @Override
    public Object remove(@Nonnull String key) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        return attributes.remove(key);
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

    @Override
    public void put(@Nonnull String key, @Nullable Object value) {
        requireNonBlank(key, ERROR_KEY_BLANK);
        attributes.put(key, value);
    }

    @Override
    public void putAt(@Nonnull String key, @Nullable Object value) {
        put(key, value);
    }

    @Override
    public void destroy() {
        attributes.clear();
        super.destroy();
    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>(attributes.keySet());
        if (parentContext != null) {
            keys.addAll(parentContext.keySet());
        }
        return keys;
    }
}
