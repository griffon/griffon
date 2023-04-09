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
package griffon.javafx.converters;

import griffon.converter.ConversionException;
import javafx.geometry.Point2D;
import org.codehaus.griffon.converter.AbstractConverter;

import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class Point2DConverter extends AbstractConverter<Point2D> {
    @Override
    public Point2D fromObject(Object value) throws ConversionException {
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
        } else if (value instanceof Point2D) {
            return (Point2D) value;
        } else {
            throw illegalValue(value, Point2D.class);
        }
    }

    @Override
    public String toString(Point2D value) throws ConversionException {
        if (null == value) { return null; }
        return value.getX() + ", " + value.getY();
    }

    protected Point2D handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }

        String[] parts = str.split(",");
        switch (parts.length) {
            case 1:
                double s = parseValue(parts[0]);
                return new Point2D(s, s);
            case 2:
                double x = parseValue(parts[0]);
                double y = parseValue(parts[1]);
                return new Point2D(x, y);
            default:
                throw illegalValue(str, Point2D.class);
        }
    }

    protected Point2D handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        switch (list.size()) {
            case 1:
                double s = parseValue(list.get(0));
                return new Point2D(s, s);
            case 2:
                double x = parseValue(list.get(0));
                double y = parseValue(list.get(1));
                return new Point2D(x, y);
            default:
                throw illegalValue(list, Point2D.class);
        }
    }

    protected Point2D handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        double x = getMapValue(map, "x", 0);
        double y = getMapValue(map, "y", 0);
        return new Point2D(x, y);
    }

    protected double parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Point2D.class);
    }

    protected double parse(String val) {
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Point2D.class, e);
        }
    }

    protected double parse(Number val) {
        return val.doubleValue();
    }

    protected double getMapValue(Map<?, ?> map, String key, double defaultValue) {
        Object val = map.get(key);
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Point2D.class);
    }

    protected Point2D handleAsNumber(Number value) {
        double s = parse(value);
        return new Point2D(s, s);
    }
}
