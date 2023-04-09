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
package org.codehaus.griffon.runtime.core.configuration;

import griffon.annotations.core.Nonnull;
import griffon.converter.Converter;

import static griffon.util.StringUtils.requireNonBlank;


/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public abstract class InjectionPoint {
    private final String configuration;
    private final String key;
    private final String format;
    private final Class<? extends Converter> converter;

    public InjectionPoint(@Nonnull String configuration, @Nonnull String key, @Nonnull String format, @Nonnull Class<? extends Converter> converter) {
        this.configuration = configuration;
        this.key = requireNonBlank(key, "Argument 'key' must not be blank");
        this.format = format;
        this.converter = converter;
    }

    @Nonnull
    public String getConfiguration() {
        return configuration;
    }

    @Nonnull
    public String getKey() {
        return key;
    }

    @Nonnull
    public String getFormat() {
        return format;
    }

    @Nonnull
    public Class<? extends Converter> getConverter() {
        return converter;
    }

    public abstract void setValue(@Nonnull Object instance, Object value);

    @Nonnull
    public abstract Class<?> getType();
}
