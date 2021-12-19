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
package org.codehaus.griffon.formatter;

import griffon.annotations.core.Nullable;
import griffon.formatter.NumberFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public abstract class AbstractNumberFormatter<T extends Number> extends AbstractFormatter<T> implements NumberFormatter<T> {
    protected final NumberFormat numberFormat;

    public AbstractNumberFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            numberFormat = NumberFormat.getIntegerInstance();
        } else if (PATTERN_CURRENCY.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getCurrencyInstance();
        } else if (PATTERN_PERCENT.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getPercentInstance();
        } else {
            numberFormat = new DecimalFormat(pattern);
            numberFormat.setParseIntegerOnly(true);
        }
    }

    @Nullable
    @Override
    public String format(@Nullable T obj) {
        return obj == null ? null : numberFormat.format(obj);
    }
}
