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
public class InstantValue extends AbstractAtomicValue implements NumericAtomicValue {
    public InstantValue() {
    }

    public InstantValue(@Nonnull Instant arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull DateTime arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull DateMidnight arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull LocalDate arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull LocalDateTime arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull LocalTime arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull Calendar arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull Date arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public InstantValue(@Nonnull CharSequence arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    @Nullable
    public Instant instantValue() {
        return (Instant) value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value == null || value instanceof Instant) {
            super.setValue(value);
        } else if (value instanceof DateTime) {
            super.setValue(((DateTime) value).toInstant());
        } else if (value instanceof DateMidnight) {
            super.setValue(((DateMidnight) value).toInstant());
        } else if (value instanceof LocalDate) {
            super.setValue(new Instant(((LocalDate) value).toDate()));
        } else if (value instanceof LocalDateTime) {
            super.setValue(new Instant(((LocalDateTime) value).toDate()));
        } else if (value instanceof LocalTime) {
            super.setValue(((LocalTime) value).toDateTimeToday().toInstant());
        } else if (value instanceof Calendar) {
            super.setValue(new Instant(value));
        } else if (value instanceof Date) {
            super.setValue(new Instant(value));
        } else if (value instanceof CharSequence) {
            super.setValue(Instant.parse(value.toString()));
        } else {
            throw new IllegalArgumentException("Invalid value " + value);
        }
    }

    @Nonnull
    public Class<?> getValueType() {
        return Instant.class;
    }
}
