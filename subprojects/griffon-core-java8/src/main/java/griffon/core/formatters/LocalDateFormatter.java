/*
 * Copyright 2008-2017 the original author or authors.
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class LocalDateFormatter extends AbstractFormatter<LocalDate> {
    private final DateTimeFormatter formatter;
    private final String pattern;

    public LocalDateFormatter() {
        this(null);
    }

    public LocalDateFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            this.pattern = "yyyy-MM-dd";
        } else {
            formatter = DateTimeFormatter.ofPattern(pattern);
            this.pattern = pattern;
        }
    }

    @Nonnull
    public String getPattern() {
        return pattern;
    }

    @Nullable
    public String format(@Nullable LocalDate date) {
        return date == null ? null : formatter.format(date);
    }

    @Nullable
    @Override
    public LocalDate parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) return null;
        try {
            return LocalDate.parse(str, formatter);
        } catch (DateTimeParseException e) {
            throw new ParseException(e);
        }
    }
}
