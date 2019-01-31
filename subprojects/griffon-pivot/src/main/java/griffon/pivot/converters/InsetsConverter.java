/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.pivot.converters;

import org.apache.pivot.wtk.Insets;
import org.kordamp.jsr377.converter.AbstractConverter;

import javax.application.converter.ConversionException;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class InsetsConverter extends AbstractConverter<Insets> {
    @Override
    public Insets fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Map) {
            return handleAsMap((Map) value);
        } else if (value instanceof Number) {
            return handleAsNumber((Number) value);
        } else if (value instanceof Insets) {
            return (Insets) value;
        } else {
            throw illegalValue(value, Insets.class);
        }
    }

    @Override
    public String toString(Insets value) throws ConversionException {
        if (null == value) { return null; }
        return value.top + ", " + value.left + ", " + value.bottom + ", " + value.right;
    }

    protected Insets handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }

        int t = 0;
        int l = 0;
        int r = 0;
        int b = 0;
        String[] parts = str.split(",");
        switch (parts.length) {
            case 4:
                b = parseValue(parts[3]);
            case 3:
                r = parseValue(parts[2]);
            case 2:
                l = parseValue(parts[1]);
            case 1:
                t = parseValue(parts[0]);
                return new Insets(t, l, r, b);
            default:
                throw illegalValue(str, Insets.class);
        }
    }

    protected Insets handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        int t = 0;
        int l = 0;
        int r = 0;
        int b = 0;
        switch (list.size()) {
            case 4:
                b = parseValue(list.get(3));
            case 3:
                r = parseValue(list.get(2));
            case 2:
                l = parseValue(list.get(1));
            case 1:
                t = parseValue(list.get(0));
                return new Insets(t, l, r, b);
            default:
                throw illegalValue(list, Insets.class);
        }
    }

    protected Insets handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        int t = getMapValue(map, "top", 0);
        int l = getMapValue(map, "left", 0);
        int r = getMapValue(map, "right", 0);
        int b = getMapValue(map, "bottom", 0);
        return new Insets(t, l, r, b);
    }

    protected int parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Insets.class);
    }

    protected int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Insets.class, e);
        }
    }

    protected int parse(Number val) {
        return val.intValue();
    }

    protected int getMapValue(Map<?, ?> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (null == val) { val = map.get(String.valueOf(key.charAt(0))); }
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Insets.class);
    }

    protected Insets handleAsNumber(Number value) {
        int c = parse(value);
        return new Insets(c, c, c, c);
    }
}
