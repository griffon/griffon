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
import griffon.formatter.Formatter;
import griffon.formatter.ParseException;
import griffon.javafx.formatters.ColorFormatter;
import javafx.scene.paint.Color;
import org.codehaus.griffon.converter.AbstractFormattingConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class ColorConverter extends AbstractFormattingConverter<Color> {
    public static String format(Color color) {
        return ColorFormatter.LONG.format(color);
    }

    public String toString(Color value) throws ConversionException {
        if (null == value) {
            return null;
        } else {
            return null == getFormat() ? format(value) : this.getFormatted(value, this.getFormat());
        }
    }

    @Override
    protected Color convertFromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value).trim());
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Map) {
            return handleAsMap((Map) value);
        } else if (value instanceof Number) {
            return handleAsNumber((Number) value);
        } else if (value instanceof Color) {
            return (Color) value;
        } else {
            throw illegalValue(value, Color.class);
        }
    }

    @Override
    protected Formatter<Color> resolveFormatter(String s) {
        return !isBlank(getFormat()) ? ColorFormatter.getInstance(getFormat()) : null;
    }

    protected Color handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return ColorFormatter.parseColor(str);
        } catch (ParseException e) {
            throw illegalValue(str, Color.class, e);
        }
    }

    protected Color handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        List<Object> values = new ArrayList<>(list);
        switch (list.size()) {
            case 3:
                values.add(1d);
                break;
            case 4:
                // ok
                break;
            default:
                throw illegalValue(list, Color.class);
        }
        for (int i = 0, valuesSize = values.size(); i < valuesSize; i++) {
            Object val = values.get(i);
            if (val instanceof Number) {
                values.set(i, parse((Number) val));
            } else if (val instanceof CharSequence) {
                values.set(i, parse(String.valueOf(val)));
            } else {
                throw illegalValue(list, Color.class);
            }
        }

        return new Color(
            (Double) values.get(0),
            (Double) values.get(1),
            (Double) values.get(2),
            (Double) values.get(3)
        );
    }

    protected Color handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        double r = getMapValue(map, "red", 0d);
        double g = getMapValue(map, "green", 0d);
        double b = getMapValue(map, "blue", 0d);
        double a = getMapValue(map, "alpha", 1d);
        return new Color(r, g, b, a);
    }

    protected double parse(String val) {
        try {
            return (Integer.parseInt(String.valueOf(val).trim(), 16) & 0xFF) / 255d;
        } catch (NumberFormatException e) {
            throw illegalValue(val, Color.class, e);
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
        throw illegalValue(map, Color.class);
    }

    protected Color handleAsNumber(Number value) {
        double c = parse(value);
        return new Color(c, c, c, 1d);
    }
}