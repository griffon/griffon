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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DateValue extends AbstractAtomicValue implements NumericAtomicValue {
    public DateValue() {
    }

    public DateValue(@Nonnull Date arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public DateValue(@Nonnull Calendar arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    public DateValue(@Nonnull Number arg) {
        setValue(requireNonNull(arg, ERROR_ARG_NULL));
    }

    @Nullable
    public Date dateValue() {
        return (Date) value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value == null || value instanceof Date) {
            super.setValue(value);
        } else if (value instanceof Calendar) {
            super.setValue(((Calendar) value).getTime());
        } else if (value instanceof Number) {
            Date d = new Date();
            d.setTime(((Number) value).longValue());
            super.setValue(d);
        } else {
            throw new IllegalArgumentException("Invalid value " + value);
        }
    }

    @Nonnull
    public Class<?> getValueType() {
        return Date.class;
    }
}
