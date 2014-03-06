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
package griffon.plugins.scaffolding.atoms;

import org.joda.time.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class LocalDateTimeValue extends AbstractAtomicValue implements NumericAtomicValue {
    public LocalDateTimeValue() {
    }

    public LocalDateTimeValue(@Nonnull DateTime arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public LocalDateTimeValue(@Nonnull DateMidnight arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public LocalDateTimeValue(@Nonnull Instant arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public LocalDateTimeValue(@Nonnull LocalDate arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public LocalDateTimeValue(@Nonnull LocalDateTime arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public LocalDateTimeValue(@Nonnull Calendar arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public LocalDateTimeValue(@Nonnull Date arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public LocalDateTimeValue(@Nonnull Number arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    @Nullable
    public LocalDateTime localDateTimeValue() {
        return (LocalDateTime) value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value == null || value instanceof LocalDateTime) {
            super.setValue(value);
        } else if (value instanceof DateTime) {
            super.setValue(((DateTime) value).toLocalDateTime());
        } else if (value instanceof DateMidnight) {
            super.setValue(((DateMidnight) value).toDateTime().toLocalDateTime());
        } else if (value instanceof Instant) {
            super.setValue(((Instant) value).toDateTime().toLocalDateTime());
        } else if (value instanceof LocalDate) {
            super.setValue(((LocalDate) value).toDateTimeAtStartOfDay().toLocalDateTime());
        } else if (value instanceof Calendar || value instanceof Date) {
            super.setValue(new LocalDateTime(value));
        } else if (value instanceof Number) {
            super.setValue(new LocalDateTime(((Number) value).longValue()));
        } else {
            throw new IllegalArgumentException("Invalid value " + value);
        }
    }

    @Nonnull
    public Class<?> getValueType() {
        return LocalDateTime.class;
    }
}
