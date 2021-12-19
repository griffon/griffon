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
import griffon.converter.ConversionException;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public class EnumConverter<T extends Enum<T>> extends AbstractConverter<T> {
    private Class<T> enumType;

    @Nonnull
    public Class<T> getEnumType() {
        return enumType;
    }

    public void setEnumType(@Nonnull Class<T> enumType) {
        this.enumType = enumType;
    }

    @Nullable
    @Override
    public T fromObject(@Nullable Object value) throws ConversionException {
        if (null == enumType) {
            throw new IllegalStateException("A value for enumType must be set");
        }

        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return convertFromString(String.valueOf(value).trim());
        } else if (enumType.isAssignableFrom(value.getClass())) {
            return enumType.cast(value);
        } else {
            throw illegalValue(value, enumType);
        }
    }

    @Nullable
    @Override
    public String toString(@Nullable T value) {
        return value == null ? null : value.name();
    }

    @Nullable
    protected T convertFromString(@Nonnull String str) {
        if (isBlank(str)) {
            return null;
        }

        try {
            return Enum.valueOf(enumType, str);
        } catch (Exception e) {
            throw illegalValue(str, enumType, e);
        }
    }
}
