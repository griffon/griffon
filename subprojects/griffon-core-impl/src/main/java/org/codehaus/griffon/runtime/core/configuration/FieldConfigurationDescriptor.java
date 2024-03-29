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
import griffon.annotations.core.Nullable;
import griffon.converter.Converter;

import java.lang.reflect.Field;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class FieldConfigurationDescriptor extends ConfigurationDescriptor {
    private final Field field;

    public FieldConfigurationDescriptor(@Nonnull Field field, @Nonnull String configuration, @Nonnull String key, @Nullable String defaultValue, @Nonnull String format, @Nonnull Class<? extends Converter> converter) {
        super(configuration, key, defaultValue, format, converter);
        this.field = requireNonNull(field, "Argument 'field' must not be null");
    }

    @Nonnull
    public Field getField() {
        return field;
    }

    @Nonnull
    public InjectionPoint asInjectionPoint() {
        return new FieldInjectionPoint(field, getConfiguration(), getKey(), getFormat(), getConverter());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FieldPreferenceDescriptor{");
        sb.append("field=").append(field);
        sb.append(", configuration='").append(getConfiguration()).append('\'');
        sb.append(", key='").append(getKey()).append('\'');
        sb.append(", defaultValue='").append(getDefaultValue()).append('\'');
        sb.append(", format='").append(getFormat()).append('\'');
        sb.append(", converter='").append(getConverter()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
