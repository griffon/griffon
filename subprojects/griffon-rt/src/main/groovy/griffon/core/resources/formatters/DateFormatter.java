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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class DateFormatter extends AbstractFormatter {
    private final DateFormat dateFormat;

    public DateFormatter() {
        this(null);
    }

    public DateFormatter(String pattern) {
        if (isBlank(pattern)) {
            dateFormat = new SimpleDateFormat();
        } else {
            dateFormat = new SimpleDateFormat(pattern);
        }
    }

    @Override
    public String format(Object obj) {
        if (obj instanceof Date) {
            return format((Date) obj);
        }
        throw new IllegalArgumentException("Can't format given Object as a Date");
    }

    public String format(Date date) {
        return dateFormat.format(date);
    }

    @Override
    public Object parse(String str) throws ParseException {
        try {
            return dateFormat.parse(str);
        } catch (java.text.ParseException e) {
            throw new ParseException(e);
        }
    }
}
