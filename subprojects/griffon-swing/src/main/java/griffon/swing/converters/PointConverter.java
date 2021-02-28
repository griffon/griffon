/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.swing.converters;

import org.kordamp.jsr377.converter.AbstractConverter;

import javax.application.converter.ConversionException;
import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 3.0.0
 */
public class PointConverter extends AbstractConverter<Point> {
    @Override
    public Point fromObject(Object value) throws ConversionException {
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
        } else if (value instanceof Point) {
            return (Point) value;
        } else {
            throw illegalValue(value, Point.class);
        }
    }

    @Override
    public String toString(Point value) throws ConversionException {
        if (null == value) { return null; }
        return value.getX() + ", " + value.getY();
    }

    protected Point handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }

        String[] parts = str.split(",");
        switch (parts.length) {
            case 1:
                int s = parseValue(parts[0]);
                return new Point(s, s);
            case 2:
                int x = parseValue(parts[0]);
                int y = parseValue(parts[1]);
                return new Point(x, y);
            default:
                throw illegalValue(str, Point.class);
        }
    }

    protected Point handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        switch (list.size()) {
            case 1:
                int s = parseValue(list.get(0));
                return new Point(s, s);
            case 2:
                int x = parseValue(list.get(0));
                int y = parseValue(list.get(1));
                return new Point(x, y);
            default:
                throw illegalValue(list, Point.class);
        }
    }

    protected Point handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        int x = getMapValue(map, "x", 0);
        int y = getMapValue(map, "y", 0);
        return new Point(x, y);
    }

    protected int parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Point.class);
    }

    protected int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Point.class, e);
        }
    }

    protected int parse(Number val) {
        return val.intValue();
    }

    protected int getMapValue(Map<?, ?> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Point.class);
    }

    protected Point handleAsNumber(Number value) {
        int s = parse(value);
        return new Point(s, s);
    }
}
