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

import org.apache.pivot.wtk.Bounds;
import org.kordamp.jsr377.converter.AbstractConverter;

import javax.application.converter.ConversionException;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class BoundsConverter extends AbstractConverter<Bounds> {
    @Override
    public Bounds fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Map) {
            return handleAsMap((Map) value);
        } else if (value instanceof Bounds) {
            return (Bounds) value;
        } else {
            throw illegalValue(value, Bounds.class);
        }
    }

    @Override
    public String toString(Bounds value) throws ConversionException {
        if (null == value) { return null; }
        return value.x + ", " + value.y + ", " + value.width + ", " + value.height;
    }

    protected Bounds handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }

        String[] parts = str.split(",");
        switch (parts.length) {
            case 4:
                int x = parseValue(parts[0]);
                int y = parseValue(parts[1]);
                int w = parseValue(parts[2]);
                int h = parseValue(parts[3]);
                return new Bounds(x, y, w, h);
            default:
                throw illegalValue(str, Bounds.class);
        }
    }

    protected Bounds handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        switch (list.size()) {
            case 4:
                int x = parseValue(list.get(0));
                int y = parseValue(list.get(1));
                int w = parseValue(list.get(2));
                int h = parseValue(list.get(3));
                return new Bounds(x, y, w, h);
            default:
                throw illegalValue(list, Bounds.class);
        }
    }

    protected Bounds handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        int x = getMapValue(map, "x", 0);
        int y = getMapValue(map, "y", 0);
        int w = getMapValue(map, "width", 0);
        int h = getMapValue(map, "height", 0);
        return new Bounds(x, y, w, h);
    }

    protected int parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Bounds.class);
    }

    protected int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Bounds.class, e);
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
        throw illegalValue(map, Bounds.class);
    }
}
