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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class BigIntegerFormatter extends AbstractFormatter {
    private static final String PATTERN_CURRENCY = "currency";
    private static final String PATTERN_PERCENT = "percent";

    private final NumberFormat numberFormat;

    public BigIntegerFormatter() {
        this(null);
    }

    public BigIntegerFormatter(String pattern) {
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

    @Override
    public String format(Object obj) {
        if (obj instanceof BigInteger) {
            return format((BigInteger) obj);
        }
        throw new IllegalArgumentException("Can't format given Object as a BigInteger");
    }

    public String format(BigInteger number) {
        return numberFormat.format(number);
    }

    @Override
    public Object parse(String str) throws ParseException {
        if (isBlank(str)) return null;
        try {
            BigDecimal bd = (BigDecimal) numberFormat.parse(str);
            return bd.toBigInteger();
        } catch (java.text.ParseException e) {
            throw new ParseException(e);
        }
    }
}
