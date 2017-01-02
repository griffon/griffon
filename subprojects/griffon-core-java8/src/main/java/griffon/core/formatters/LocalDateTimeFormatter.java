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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class LocalDateTimeFormatter extends AbstractFormatter<LocalDateTime> {
    private final DateTimeFormatter formatter;
    private final String pattern;

    public LocalDateTimeFormatter() {
        this(null);
    }

    public LocalDateTimeFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            this.pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
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
    public String format(@Nullable LocalDateTime time) {
        return time == null ? null : formatter.format(time);
    }

    @Nullable
    @Override
    public LocalDateTime parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) return null;
        try {
            return LocalDateTime.parse(str, formatter);
        } catch (DateTimeParseException e) {
            throw new ParseException(e);
        }
    }
}
