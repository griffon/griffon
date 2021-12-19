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
import griffon.formatter.Formatter;
import org.codehaus.griffon.formatter.IntegerFormatter;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class IntegerConverter extends AbstractPrimitiveNumberConverter<Integer> {
    @Nonnull
    @Override
    protected Class<Integer> getTypeClass() {
        return Integer.class;
    }

    @Nullable
    @Override
    protected Integer convertFromNumber(@Nonnull Number value) {
        return value.intValue();
    }

    @Nullable
    @Override
    protected Integer doConvertFromString(@Nonnull String value) throws ConversionException {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            throw illegalValue(value, Integer.class, e);
        }
    }

    @Nullable
    @Override
    protected Formatter<Integer> resolveFormatter(@Nonnull String format) {
        return isBlank(format) ? null : new IntegerFormatter(format);
    }
}
