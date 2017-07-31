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
package griffon.core.editors;

import griffon.core.formatters.Formatter;
import griffon.core.formatters.LocalDateFormatter;
import griffon.metadata.PropertyEditorFor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.time.LocalDate.ofEpochDay;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
@PropertyEditorFor(LocalDate.class)
public class LocalDatePropertyEditor extends AbstractPropertyEditor {
    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof LocalDate) {
            super.setValueInternal(value);
        } else if (value instanceof LocalDateTime) {
            super.setValueInternal(((LocalDateTime) value).toLocalDate());
        } else if (value instanceof Date) {
            super.setValueInternal(ofEpochDay(((Date) value).getTime()));
        } else if (value instanceof Calendar) {
            super.setValueInternal(ofEpochDay(((Calendar) value).getTime().getTime()));
        } else if (value instanceof Number) {
            super.setValueInternal(ofEpochDay(((Number) value).longValue()));
        } else if (value instanceof List) {
            handleAsList((List) value);
        } else if (value instanceof Map) {
            handleAsMap((Map) value);
        } else {
            throw illegalValue(value, LocalDate.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }

        try {
            super.setValueInternal(LocalDate.parse(str));
        } catch (DateTimeParseException dtpe) {
            throw illegalValue(str, LocalDate.class, dtpe);
        }
    }

    @Override
    protected Formatter<LocalDate> resolveFormatter() {
        return isBlank(getFormat()) ? null : new LocalDateFormatter(getFormat());
    }

    protected void handleAsList(List<?> list) {
        if (list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        List<Object> values = new ArrayList<>();
        values.addAll(list);
        if (values.size() != 3) {
            throw illegalValue(list, LocalDate.class);
        }

        for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
            Object val = values.get(i);
            if (val instanceof Number) {
                values.set(i, parse((Number) val));
            } else if (val instanceof CharSequence) {
                values.set(i, parse(String.valueOf(val)));
            } else {
                throw illegalValue(list, LocalDate.class);
            }
        }
        super.setValueInternal(
            LocalDate.of(
                (Integer) values.get(0),
                (Integer) values.get(1),
                (Integer) values.get(2)
            )
        );
    }

    protected void handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        int y = getMapValue(map, "year", 1970);
        int m = getMapValue(map, "month", 1);
        int d = getMapValue(map, "day", 1);
        super.setValueInternal(LocalDate.of(y, m, d));
    }

    protected int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, LocalDate.class, e);
        }
    }

    protected int parse(Number val) {
        return val.intValue();
    }

    protected int getMapValue(Map<?, ?> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (null == val) val = map.get(String.valueOf(key.charAt(0)));
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, LocalDate.class);
    }
}
