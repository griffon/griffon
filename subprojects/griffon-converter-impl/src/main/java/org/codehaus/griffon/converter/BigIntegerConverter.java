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
import griffon.converter.NumberConverter;
import org.codehaus.griffon.formatter.BigIntegerFormatter;
import griffon.formatter.Formatter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class BigIntegerConverter extends AbstractFormattingConverter<BigInteger> implements NumberConverter<BigInteger> {
    @Nullable
    @Override
    protected BigInteger convertFromObject(@Nullable Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof Number) {
            return convertFromNumber((Number) value);
        } else if (value instanceof CharSequence) {
            return convertFromString(String.valueOf(value).trim());
        } else {
            throw illegalValue(value, BigInteger.class);
        }
    }

    @Nullable
    private BigInteger convertFromString(@Nonnull String str) {
        try {
            return isBlank(str) ? null : new BigInteger(str);
        } catch (NumberFormatException e) {
            throw illegalValue(str, BigInteger.class, e);
        }
    }

    @Nonnull
    protected BigInteger convertFromNumber(@Nonnull Number number) {
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        } else if (number instanceof BigInteger) {
            return (BigInteger) number;
        } else {
            return BigInteger.valueOf(number.longValue());
        }
    }

    @Nullable
    @Override
    protected Formatter<BigInteger> resolveFormatter(@Nonnull String format) {
        return isBlank(format) ? null : new BigIntegerFormatter(format);
    }
}
