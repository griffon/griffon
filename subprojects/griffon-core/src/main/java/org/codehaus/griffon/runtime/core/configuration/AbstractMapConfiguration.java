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
package org.codehaus.griffon.runtime.core.configuration;

import griffon.util.AbstractMapResourceBundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public abstract class AbstractMapConfiguration extends AbstractConfiguration {
    protected static final String ERROR_KEY_BLANK = "Argument 'key' must not be blank";
    private static final String ERROR_MAP_NULL = "Argument 'map' must not be null";
    private final Map<String, Object> map;

    protected AbstractMapConfiguration(@Nonnull Map<String, Object> map) {
        this.map = requireNonNull(map, ERROR_MAP_NULL);
    }

    @Override
    public boolean containsKey(@Nonnull String key) {
        return map.containsKey(requireNonBlank(key, ERROR_KEY_BLANK));
    }

    @Nonnull
    @Override
    public Map<String, Object> asFlatMap() {
        return Collections.unmodifiableMap(map);
    }

    @Nonnull
    @Override
    public ResourceBundle asResourceBundle() {
        return new AbstractMapResourceBundle() {
            @Override
            protected void initialize(@Nonnull Map<String, Object> entries) {
                entries.putAll(map);
            }
        };
    }

    @Nullable
    @Override
    public Object get(@Nonnull String key) {
        try {
            return getConfigValue(map, key);
        } catch (MissingResourceException mre) {
            return null;
        }
    }
}
