/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.core.formatters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class BigIntegerFormatter extends AbstractFormatter<BigInteger> {
    private static final String PATTERN_CURRENCY = "currency";
    private static final String PATTERN_PERCENT = "percent";

    private final NumberFormat numberFormat;

    public BigIntegerFormatter() {
        this(null);
    }

    public BigIntegerFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            numberFormat = NumberFormat.getIntegerInstance();
        } else if (PATTERN_CURRENCY.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getCurrencyInstance();
        } else if (PATTERN_PERCENT.equalsIgnoreCase(pattern)) {
            numberFormat = NumberFormat.getPercentInstance();
        } else {
            numberFormat = new DecimalFormat(pattern);
            numberFormat.setParseIntegerOnly(true);
            ((DecimalFormat) numberFormat).setParseBigDecimal(true);
        }
    }

    @Nonnull
    public String format(@Nonnull BigInteger number) {
        return numberFormat.format(number);
    }

    @Nullable
    @Override
    public BigInteger parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) return null;
        try {
            BigDecimal bd = (BigDecimal) numberFormat.parse(str);
            return bd.toBigInteger();
        } catch (java.text.ParseException e) {
            throw new ParseException(e);
        }
    }
}
