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
package org.codehaus.griffon.runtime.core.storage;

import griffon.core.storage.ObjectStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultObjectStorage<T> implements ObjectStorage<T> {
    private static final String DEFAULT_KEY = "default";
    private final Map<String, T> instances = new ConcurrentHashMap<>();

    @Nonnull
    @Override
    public String[] getKeys() {
        Set<String> keys = instances.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    @Nonnull
    @Override
    public Collection<T> getValues() {
        return unmodifiableSet(new LinkedHashSet<>(instances.values()));
    }

    @Nullable
    @Override
    public T get(@Nonnull String key) {
        return instances.get(resolveKey(key));
    }

    @Nullable
    @Override
    public T remove(@Nonnull String key) {
        return instances.remove(resolveKey(key));
    }

    @Override
    public void set(@Nonnull String key, @Nonnull T instance) {
        instances.put(resolveKey(key), requireNonNull(instance, "Argument 'instance' must not be null"));
    }

    @Override
    public boolean contains(@Nonnull String key) {
        return instances.containsKey(resolveKey(key));
    }

    @Nonnull
    private String resolveKey(@Nonnull String key) {
        return isBlank(key) ? DEFAULT_KEY : key.trim();
    }
}
