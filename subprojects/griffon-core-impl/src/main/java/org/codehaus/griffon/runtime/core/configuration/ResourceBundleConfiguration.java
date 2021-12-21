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
package org.codehaus.griffon.runtime.core.configuration;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConverterRegistry;

import javax.inject.Inject;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static griffon.core.util.ConfigUtils.getConfigValue;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ResourceBundleConfiguration extends AbstractConfiguration {
    protected static final String ERROR_KEY_BLANK = "Argument 'key' must not be blank";
    private final ResourceBundle resourceBundle;
    private final Map<String, Object> flatMap = new LinkedHashMap<>();

    @Inject
    public ResourceBundleConfiguration(@Nonnull ConverterRegistry converterRegistry, @Nonnull ResourceBundle resourceBundle) {
        super(converterRegistry);
        this.resourceBundle = requireNonNull(resourceBundle, "Argument 'resourceBundle' must not be null");
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            flatMap.put(key, getConfigValue(resourceBundle, key));
        }
    }

    public boolean containsKey(@Nonnull String key) {
        return resourceBundle.containsKey(requireNonBlank(key, ERROR_KEY_BLANK));
    }

    @Nonnull
    @Override
    public Map<String, Object> asFlatMap() {
        return unmodifiableMap(flatMap);
    }

    @Nonnull
    @Override
    public ResourceBundle asResourceBundle() {
        return resourceBundle;
    }

    @Nullable
    @Override
    public <T> T get(@Nonnull String key) {
        try {
            return getConfigValue(resourceBundle, key);
        } catch (MissingResourceException mre) {
            return null;
        }
    }
}
