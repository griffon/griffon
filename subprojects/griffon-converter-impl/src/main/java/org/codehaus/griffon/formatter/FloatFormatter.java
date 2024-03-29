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
package org.codehaus.griffon.formatter;

import griffon.annotations.core.Nullable;
import griffon.formatter.NumberFormatter;
import griffon.formatter.ParseException;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class FloatFormatter extends AbstractFormatter<Float> implements NumberFormatter<Float> {
    private final NumberFormat numberFormat;

    public FloatFormatter() {
        this(null);
    }

    public FloatFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            numberFormat = new DecimalFormat("#.0");
        } else if (PATTERN_CURRENCY.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getCurrencyInstance();
        } else if (PATTERN_PERCENT.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getPercentInstance();
        } else {
            numberFormat = new DecimalFormat(pattern);
        }
    }

    @Nullable
    @Override
    public Float parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) { return null; }
        try {
            return numberFormat.parse(str).floatValue();
        } catch (java.text.ParseException e) {
            throw new ParseException(e);
        }
    }

    @Nullable
    @Override
    public String format(@Nullable Float obj) {
        return obj == null ? null : numberFormat.format(obj);
    }
}
