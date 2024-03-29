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
import org.codehaus.griffon.formatter.FloatFormatter;
import griffon.formatter.Formatter;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class FloatConverter extends AbstractPrimitiveNumberConverter<Float> {
    @Nonnull
    @Override
    protected Class<Float> getTypeClass() {
        return Float.class;
    }

    @Nullable
    @Override
    protected Float convertFromNumber(@Nonnull Number value) {
        return value.floatValue();
    }

    @Nullable
    @Override
    protected Float doConvertFromString(@Nonnull String value) throws ConversionException {
        try {
            return Float.valueOf(value);
        } catch (Exception e) {
            throw illegalValue(value, Float.class, e);
        }
    }

    @Nullable
    @Override
    protected Formatter<Float> resolveFormatter(@Nonnull String format) {
        return isBlank(format) ? null : new FloatFormatter(format);
    }
}
