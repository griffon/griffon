/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2021 the original author or authors.
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
import org.codehaus.griffon.formatter.DoubleFormatter;
import griffon.formatter.Formatter;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class DoubleConverter extends AbstractPrimitiveNumberConverter<Double> {
    @Nonnull
    @Override
    protected Class<Double> getTypeClass() {
        return Double.class;
    }

    @Nullable
    @Override
    protected Double convertFromNumber(@Nonnull Number value) {
        return value.doubleValue();
    }

    @Nullable
    @Override
    protected Double doConvertFromString(@Nonnull String value) throws ConversionException {
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            throw illegalValue(value, Double.class, e);
        }
    }

    @Nullable
    @Override
    protected Formatter<Double> resolveFormatter(@Nonnull String format) {
        return isBlank(format) ? null : new DoubleFormatter(format);
    }
}
