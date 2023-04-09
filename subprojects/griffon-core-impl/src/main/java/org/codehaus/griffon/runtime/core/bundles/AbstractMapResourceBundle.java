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
package org.codehaus.griffon.runtime.core.bundles;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import static griffon.core.util.ConfigUtils.collectKeys;
import static griffon.core.util.ConfigUtils.getConfigValue;
import static griffon.util.StringUtils.requireNonBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractMapResourceBundle extends ResourceBundle {
    protected final Map<String, Object> entries = new LinkedHashMap<>();
    protected volatile Set<String> keys;

    public AbstractMapResourceBundle() {
        initialize(entries);
        initializeKeys();
    }

    protected abstract void initialize(@Nonnull Map<String, Object> entries);

    protected void initializeKeys() {
        keys = collectKeys(entries);
    }

    @Nullable
    @Override
    protected final Object handleGetObject(@Nonnull String key) {
        return getConfigValue(entries, requireNonBlank(key, "Argument 'key' must not be blank"));
    }

    @Nonnull
    @Override
    public final Enumeration<String> getKeys() {
        return new IteratorAsEnumeration<>(keys.iterator());
    }

    @Override
    public Set<String> keySet() {
        return keys;
    }

    @Override
    protected Set<String> handleKeySet() {
        return keys;
    }

    private static class IteratorAsEnumeration<E> implements Enumeration<E> {
        private final Iterator<E> iterator;

        public IteratorAsEnumeration(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public E nextElement() {
            return iterator.next();
        }
    }
}
