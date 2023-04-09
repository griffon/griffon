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
package griffon.swing.converters;

import griffon.converter.ConversionException;
import org.codehaus.griffon.converter.AbstractConverter;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class Rectangle2DConverter extends AbstractConverter<Rectangle2D> {
    @Override
    public Rectangle2D fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Map) {
            return handleAsMap((Map) value);
        } else if (value instanceof Rectangle2D) {
            return (Rectangle2D) value;
        } else {
            throw illegalValue(value, Rectangle2D.class);
        }
    }

    @Override
    public String toString(Rectangle2D value) throws ConversionException {
        if (null == value) { return null; }
        return value.getX() + ", " + value.getY() + ", " + value.getWidth() + ", " + value.getHeight();
    }


    protected Rectangle2D handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }

        String[] parts = str.split(",");
        switch (parts.length) {
            case 4:
                double x = parseValue(parts[0]);
                double y = parseValue(parts[1]);
                double w = parseValue(parts[2]);
                double h = parseValue(parts[3]);
                return new Rectangle2D.Double(x, y, w, h);
            default:
                throw illegalValue(str, Rectangle2D.class);
        }
    }

    protected Rectangle2D handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        switch (list.size()) {
            case 4:
                double x = parseValue(list.get(0));
                double y = parseValue(list.get(1));
                double w = parseValue(list.get(2));
                double h = parseValue(list.get(3));
                return new Rectangle2D.Double(x, y, w, h);
            default:
                throw illegalValue(list, Rectangle2D.class);
        }
    }

    protected Rectangle2D handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        double x = getMapValue(map, "x", 0);
        double y = getMapValue(map, "y", 0);
        double w = getMapValue(map, "width", 0);
        double h = getMapValue(map, "height", 0);
        return new Rectangle2D.Double(x, y, w, h);
    }

    protected double parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Rectangle2D.class);
    }

    protected double parse(String val) {
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Rectangle2D.class, e);
        }
    }

    protected double parse(Number val) {
        return val.doubleValue();
    }

    protected double getMapValue(Map<?, ?> map, String key, double defaultValue) {
        Object val = map.get(key);
        if (null == val) { val = map.get(String.valueOf(key.charAt(0))); }
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Rectangle2D.class);
    }
}
