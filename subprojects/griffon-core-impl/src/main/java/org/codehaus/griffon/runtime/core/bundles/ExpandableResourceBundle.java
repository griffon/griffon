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

import static griffon.core.util.ConfigUtils.getConfigValue;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ExpandableResourceBundle extends ResourceBundle {
    private final Map<String, Object> entries = new LinkedHashMap<>();

    public ExpandableResourceBundle(@Nonnull ResourceBundle delegate) {
        requireNonNull(delegate, "Argument 'delegate' must not be null");
        for (String key : delegate.keySet()) {
            Object value = getConfigValue(delegate, key);
            processKey(key, entries, value);
            entries.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    private void processKey(@Nonnull String key, @Nonnull Map<String, Object> map, @Nullable Object value) {
        String[] keys = split(key);
        if (keys[1] == null) {
            map.put(keys[0], value);
        } else {
            Map<String, Object> m = (Map<String, Object>) map.get(keys[0]);
            if (m == null) {
                m = new LinkedHashMap<>();
                map.put(keys[0], m);
            }
            processKey(keys[1], m, value);
        }
    }

    @Nonnull
    private String[] split(@Nonnull String input) {
        int split = input.indexOf('.');
        String head = split < 0 ? input : input.substring(0, split);
        String tail = split > 0 ? input.substring(split + 1) : null;
        return new String[]{head, tail};
    }

    @Nullable
    @Override
    protected final Object handleGetObject(@Nonnull String key) {
        return entries.get(requireNonBlank(key, "Argument 'key' must not be blank"));
    }

    @Nonnull
    @Override
    public final Enumeration<String> getKeys() {
        return new IteratorAsEnumeration<>(entries.keySet().iterator());
    }

    @Nonnull
    public static ResourceBundle wrapResourceBundle(@Nonnull ResourceBundle resourceBundle) {
        requireNonNull(resourceBundle, "Argument 'resourceBundle' must not be null");
        if (!(resourceBundle instanceof ExpandableResourceBundle)) {
            return new ExpandableResourceBundle(resourceBundle);
        }
        return resourceBundle;
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
