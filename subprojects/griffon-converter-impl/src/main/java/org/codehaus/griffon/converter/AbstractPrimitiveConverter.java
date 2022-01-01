/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
import griffon.converter.PrimitiveConverter;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public abstract class AbstractPrimitiveConverter<T> extends AbstractFormattingConverter<T> implements PrimitiveConverter<T> {
    private T defaultValue;
    private boolean nullAccepted;

    @Nullable
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    @Override
    public void setDefaultValue(@Nullable T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isNullAccepted() {
        return nullAccepted;
    }

    @Override
    public void setNullAccepted(boolean nullAccepted) {
        this.nullAccepted = nullAccepted;
    }

    @Nullable
    @Override
    protected T convertFromObject(@Nullable Object value) throws ConversionException {
        if (null == value) {
            if (isNullAccepted()) {
                return null;
            } else {
                T defaultValue = getDefaultValue();
                if (null != defaultValue) {
                    return defaultValue;
                } else {
                    throw new ConversionException(getClass().getSimpleName() + " does not accept null input");
                }
            }
        }

        return doConvertFromObject(value);
    }

    @Nullable
    protected abstract T doConvertFromObject(@Nonnull Object value) throws ConversionException;

    @Nullable
    @Override
    public String toString(@Nullable T value) throws ConversionException {
        if (null == value) {
            if (isNullAccepted()) {
                return null;
            } else {
                T defaultValue = getDefaultValue();
                if (null != defaultValue) {
                    return super.toString(defaultValue);
                } else {
                    throw new ConversionException(getClass().getSimpleName() + " does not produce null output");
                }
            }
        }
        return super.toString(value);
    }
}
