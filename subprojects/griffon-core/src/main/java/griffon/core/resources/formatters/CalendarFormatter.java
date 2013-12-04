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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class CalendarFormatter extends AbstractFormatter<Calendar> {
    private final DateFormat dateFormat;

    public CalendarFormatter() {
        this(null);
    }

    public CalendarFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            dateFormat = new SimpleDateFormat();
        } else {
            dateFormat = new SimpleDateFormat(pattern);
        }
    }

    @Nonnull
    public String format(@Nonnull Calendar date) {
        return dateFormat.format(date.getTime());
    }

    @Nullable
    @Override
    public Calendar parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) return null;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(dateFormat.parse(str));
            return c;
        } catch (java.text.ParseException e) {
            throw new ParseException(e);
        }
    }
}