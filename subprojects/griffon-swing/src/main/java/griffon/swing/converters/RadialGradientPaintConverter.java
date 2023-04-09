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

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class RadialGradientPaintConverter extends AbstractConverter<RadialGradientPaint> {
    @Override
    public RadialGradientPaint fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Map) {
            return handleAsMap((Map) value);
        } else if (value instanceof RadialGradientPaint) {
            return (RadialGradientPaint) value;
        } else {
            throw illegalValue(value, RadialGradientPaint.class);
        }
    }

    @Override
    public String toString(RadialGradientPaint value) throws ConversionException {
        if (null == value) { return null; }
        return new StringBuilder()
            .append(value.getCenterPoint().getX())
            .append(", ")
            .append(value.getCenterPoint().getY())
            .append(", ")
            .append(value.getRadius())
            .append(", ")
            .append(value.getFocusPoint().getX())
            .append(", ")
            .append(value.getFocusPoint().getY())
            .append(", ")
            .append(formatFractions(value.getFractions()))
            .append(", ")
            .append(formatColors(value.getColors()))
            .append(", ")
            .append(value.getCycleMethod().name())
            .toString();
    }

    protected String formatFractions(float[] fractions) {
        StringBuilder b = new StringBuilder("[");
        boolean first = true;

        for (float f : fractions) {
            if (first) {
                first = false;
            } else {
                b.append(":");
            }
            b.append(f);
        }
        return b.append("]").toString();
    }

    protected String formatColors(Color[] colors) {
        StringBuilder b = new StringBuilder("[");
        boolean first = true;

        for (Color c : colors) {
            if (first) {
                first = false;
            } else {
                b.append(":");
            }
            b.append(ColorConverter.format(c));
        }
        return b.append("]").toString();
    }

    protected RadialGradientPaint handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }

        float cx = 0;
        float cy = 0;
        float radius = 0;
        float fx = 0;
        float fy = 0;
        float[] fractions = null;
        Color[] colors = null;
        MultipleGradientPaint.CycleMethod cyclicMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        String[] parts = str.split(",");
        switch (parts.length) {
            case 8:
                cyclicMethod = parseCyclicMethod(str, parts[7]);
            case 7:
                cx = parseValue(parts[0]);
                cy = parseValue(parts[1]);
                radius = parseValue(parts[2]);
                fx = parseValue(parts[3]);
                fy = parseValue(parts[4]);
                fractions = parseFractions(str, parts[5].trim());
                colors = parseColors(str, parts[6].trim());
                if (fractions.length != colors.length) {
                    throw illegalValue(str, RadialGradientPaint.class);
                }
                return new RadialGradientPaint(cx, cy, radius, fx, fy, fractions, colors, cyclicMethod);
            default:
                throw illegalValue(str, RadialGradientPaint.class);
        }
    }

    protected RadialGradientPaint handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        float cx = 0;
        float cy = 0;
        float radius = 0;
        float fx = 0;
        float fy = 0;
        float[] fractions = null;
        Color[] colors = null;
        MultipleGradientPaint.CycleMethod cyclicMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        switch (list.size()) {
            case 8:
                cyclicMethod = parseCyclicMethod(list, String.valueOf(list.get(7)).trim());
            case 7:
                cx = parseValue(list.get(0));
                cy = parseValue(list.get(1));
                radius = parseValue(list.get(2));
                fx = parseValue(list.get(3));
                fy = parseValue(list.get(4));
                fractions = parseFractions(list, list.get(5));
                colors = parseColors(list, list.get(6));
                if (fractions.length != colors.length) {
                    throw illegalValue(list, RadialGradientPaint.class);
                }
                return new RadialGradientPaint(cx, cy, radius, fx, fy, fractions, colors, cyclicMethod);
            default:
                throw illegalValue(list, RadialGradientPaint.class);
        }
    }

    protected RadialGradientPaint handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        float cx = (Float) getMapValue(map, "cx", 0f);
        float cy = (Float) getMapValue(map, "cy", 0f);
        float radius = (Float) getMapValue(map, "radius", 0f);
        float fx = (Float) getMapValue(map, "fx", 0f);
        float fy = (Float) getMapValue(map, "fy", 0f);
        MultipleGradientPaint.CycleMethod cyclicMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;

        float[] fractions = parseFractions(map, map.get("fractions"));
        Color[] colors = parseColors(map, map.get("colors"));
        if (fractions.length != colors.length) {
            throw illegalValue(map, RadialGradientPaint.class);
        }
        Object cyclicValue = map.get("cycle");
        if (null != cyclicValue) {
            cyclicMethod = parseCyclicMethod(map, String.valueOf(cyclicValue));
        }

        return new RadialGradientPaint(cx, cy, radius, fx, fy, fractions, colors, cyclicMethod);
    }

    protected float[] parseFractions(Object source, Object obj) {
        if (obj instanceof CharSequence) {
            return parseFractions(source, String.valueOf(obj).trim());
        } else if (obj instanceof List) {
            return parseFractions(source, (List) obj);
        }
        throw illegalValue(source, RadialGradientPaint.class);
    }

    protected float[] parseFractions(Object source, String str) {
        if (!str.startsWith("[") || !str.endsWith("]")) {
            throw illegalValue(source, RadialGradientPaint.class);
        }

        String[] strs = str.substring(1, str.length() - 1).split(":");
        float[] fractions = new float[strs.length];
        for (int i = 0; i < strs.length; i++) {
            fractions[i] = parseValue(strs[i]);
        }

        return fractions;
    }

    protected float[] parseFractions(Object source, List<?> list) {
        float[] fractions = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            fractions[i] = parseValue(list.get(i));
        }

        return fractions;
    }

    protected Color[] parseColors(Object source, Object obj) {
        if (obj instanceof CharSequence) {
            return parseColors(source, String.valueOf(obj).trim());
        } else if (obj instanceof List) {
            return parseColors(source, (List) obj);
        }
        throw illegalValue(source, RadialGradientPaint.class);
    }

    protected Color[] parseColors(Object source, String str) {
        if (!str.startsWith("[") || !str.endsWith("]")) {
            throw illegalValue(source, RadialGradientPaint.class);
        }

        String[] strs = str.substring(1, str.length() - 1).split(":");
        Color[] colors = new Color[strs.length];
        ColorConverter colorConverter = new ColorConverter();
        for (int i = 0; i < strs.length; i++) {
            try {
                colors[i] = colorConverter.fromObject(strs[i]);
            } catch (Exception e) {
                throw illegalValue(strs[i], RadialGradientPaint.class);
            }
        }

        return colors;
    }

    protected Color[] parseColors(Object source, List<?> list) {
        Color[] colors = new Color[list.size()];
        ColorConverter colorConverter = new ColorConverter();
        for (int i = 0; i < list.size(); i++) {
            try {
                colors[i] = colorConverter.fromObject(list.get(i));
            } catch (Exception e) {
                throw illegalValue(list.get(i), RadialGradientPaint.class, e);
            }
        }

        return colors;
    }

    protected MultipleGradientPaint.CycleMethod parseCyclicMethod(Object source, String str) {
        try {
            Field cyclicMethodField = MultipleGradientPaint.CycleMethod.class.getDeclaredField(str.toUpperCase().trim());
            return (MultipleGradientPaint.CycleMethod) cyclicMethodField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw illegalValue(source, RadialGradientPaint.class, e);
        }
    }

    protected float parse(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, RadialGradientPaint.class, e);
        }
    }

    protected float parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, RadialGradientPaint.class);
    }

    protected float parse(Number val) {
        return val.floatValue();
    }

    protected Object getMapValue(Map<?, ?> map, String key, Object defaultValue) {
        Object val = map.get(key);
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, RadialGradientPaint.class);
    }
}
