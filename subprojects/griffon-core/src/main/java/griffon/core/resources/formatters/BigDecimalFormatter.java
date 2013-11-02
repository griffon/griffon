/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.core.resources.formatters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class BigDecimalFormatter extends AbstractFormatter<BigDecimal> {
    private static final String PATTERN_CURRENCY = "currency";
    private static final String PATTERN_PERCENT = "percent";

    private final NumberFormat numberFormat;

    public BigDecimalFormatter() {
        this(null);
    }

    public BigDecimalFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            numberFormat = new DecimalFormat();
            ((DecimalFormat) numberFormat).setParseBigDecimal(true);
        } else if (PATTERN_CURRENCY.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getCurrencyInstance();
        } else if (PATTERN_PERCENT.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getPercentInstance();
        } else {
            numberFormat = new DecimalFormat(pattern);
            ((DecimalFormat) numberFormat).setParseBigDecimal(true);
        }
    }

    @Nonnull
    public String format(@Nonnull BigDecimal number) {
        return numberFormat.format(number);
    }

    @Nullable
    @Override
    public BigDecimal parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) return null;
        try {
            return (BigDecimal) numberFormat.parse(str);
        } catch (java.text.ParseException e) {
            throw new ParseException(e);
        }
    }
}
