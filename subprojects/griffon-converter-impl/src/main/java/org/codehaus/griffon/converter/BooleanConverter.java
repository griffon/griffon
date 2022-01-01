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
import org.codehaus.griffon.formatter.BooleanFormatter;
import griffon.formatter.Formatter;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class BooleanConverter extends AbstractPrimitiveConverter<Boolean> {
    @Nullable
    @Override
    protected Boolean doConvertFromObject(@Nonnull Object value) throws ConversionException {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof CharSequence) {
            return convertFromString(String.valueOf(value).trim());
        }
        throw illegalValue(value, Boolean.class);
    }

    @Nullable
    protected Boolean convertFromString(@Nonnull String str) {
        if (isBlank(str)) {
            if (isNullAccepted()) {
                return null;
            } else {
                Boolean defaultValue = getDefaultValue();
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
    protected Boolean doConvertFromString(@Nonnull String value) throws ConversionException {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            throw illegalValue(value, Boolean.class, e);
        }
    }

    @Nullable
    @Override
    protected Formatter<Boolean> resolveFormatter(@Nonnull String format) {
        return isBlank(format) ? null : BooleanFormatter.getInstance(format);
    }
}
