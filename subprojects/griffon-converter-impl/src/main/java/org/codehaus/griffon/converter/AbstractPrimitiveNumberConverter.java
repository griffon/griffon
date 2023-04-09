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
package org.codehaus.griffon.converter;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConversionException;
import griffon.converter.NumberConverter;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public abstract class AbstractPrimitiveNumberConverter<T extends Number> extends AbstractPrimitiveConverter<T> implements NumberConverter<T> {
    @Nonnull
    protected abstract Class<T> getTypeClass();

    @Nullable
    @Override
    protected T doConvertFromObject(@Nonnull Object value) throws ConversionException {
        Class<T> typeClass = getTypeClass();
        if (typeClass.isAssignableFrom(value.getClass())) {
            return typeClass.cast(value);
        } else if (value instanceof Number) {
            return convertFromNumber((Number) value);
        } else if (value instanceof CharSequence) {
            return convertFromString(String.valueOf(value).trim());
        }
        throw illegalValue(value, typeClass);
    }

    @Nullable
    protected T convertFromString(@Nonnull String str) {
        if (isBlank(str)) {
            if (isNullAccepted()) {
                return null;
            } else {
                T defaultValue = getDefaultValue();
                if (null != defaultValue) {
                    return defaultValue;
                } else {
                    throw new ConversionException(getClass().getSimpleName() + " does not accept empty input");
                }
            }
        }
        return doConvertFromString(str);
    }

    @Nullable
    protected abstract T convertFromNumber(@Nonnull Number value);

    @Nullable
    protected abstract T doConvertFromString(@Nonnull String value) throws ConversionException;
}
