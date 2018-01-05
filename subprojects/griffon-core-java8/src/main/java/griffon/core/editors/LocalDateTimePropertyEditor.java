/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.core.editors;

import griffon.core.formatters.Formatter;
import griffon.core.formatters.LocalDateTimeFormatter;
import griffon.metadata.PropertyEditorFor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
@PropertyEditorFor(LocalDateTime.class)
public class LocalDateTimePropertyEditor extends AbstractPropertyEditor {
    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof LocalDateTime) {
            super.setValueInternal(value);
        } else if (value instanceof LocalDate) {
            super.setValueInternal(LocalDateTime.of((LocalDate) value, LocalTime.of(0, 0, 0, 0)));
        } else if (value instanceof Date) {
            handleAsDate((Date) value);
        } else if (value instanceof Calendar) {
            handleAsCalendar((Calendar) value);
        } else if (value instanceof Number) {
            handleAsDate(new Date(((Number) value).longValue()));
        } else if (value instanceof List) {
            handleAsList((List) value);
        } else {
            throw illegalValue(value, LocalDateTime.class);
        }
    }

    protected void handleAsDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        handleAsCalendar(c);
    }

    protected void handleAsCalendar(Calendar value) {
        int h = value.get(Calendar.HOUR);
        int i = value.get(Calendar.MINUTE);
        int s = value.get(Calendar.SECOND);
        int n = value.get(Calendar.MILLISECOND) * 1000;
        super.setValueInternal(LocalDateTime.of(LocalDate.ofEpochDay(value.getTime().getTime()), LocalTime.of(h, i, s, n)));
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }

        try {
            super.setValueInternal(LocalDateTime.parse(str));
        } catch (DateTimeParseException dtpe) {
            throw illegalValue(str, LocalDateTime.class, dtpe);
        }
    }

    @Override
    protected Formatter<LocalDateTime> resolveFormatter() {
        return isBlank(getFormat()) ? null : new LocalDateTimeFormatter(getFormat());
    }

    protected void handleAsList(List<?> list) {
        if (list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        List<Object> values = new ArrayList<>();
        values.addAll(list);
        switch (list.size()) {
            case 7:
                // ok
                break;
            case 6:
                values.add(0d);
                break;
            case 5:
                values.add(0d);
                values.add(0d);
                break;
            case 4:
                values.add(0d);
                values.add(0d);
                values.add(0d);
                break;
            case 3:
                values.add(0d);
                values.add(0d);
                values.add(0d);
                values.add(0d);
                break;
            default:
                throw illegalValue(list, LocalDateTime.class);
        }

        for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
            Object val = values.get(i);
            if (val instanceof Number) {
                values.set(i, parse((Number) val));
            } else if (val instanceof CharSequence) {
                values.set(i, parse(String.valueOf(val)));
            } else {
                throw illegalValue(list, LocalDateTime.class);
            }
        }
        super.setValueInternal(
            LocalDateTime.of(
                (Integer) values.get(0),
                (Integer) values.get(1),
                (Integer) values.get(2),
                (Integer) values.get(3),
                (Integer) values.get(4),
                (Integer) values.get(5),
                (Integer) values.get(6)
            )
        );
    }

    protected int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, LocalDateTime.class, e);
        }
    }

    protected int parse(Number val) {
        return val.intValue();
    }
}
