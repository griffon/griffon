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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.formatter.ParseException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class LocalTimeFormatter extends AbstractFormatter<LocalTime> {
    private final DateTimeFormatter formatter;
    private final String pattern;

    public LocalTimeFormatter() {
        this(null);
    }

    public LocalTimeFormatter(@Nullable String pattern) {
        if (isBlank(pattern)) {
            formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            this.pattern = "HH:mm:ss.SSS";
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
    public String format(@Nullable LocalTime time) {
        return time == null ? null : formatter.format(time);
    }

    @Nullable
    @Override
    public LocalTime parse(@Nullable String str) throws ParseException {
        if (isBlank(str)) { return null; }
        try {
            return LocalTime.parse(str, formatter);
        } catch (DateTimeParseException e) {
            throw new ParseException(e);
        }
    }
}
