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
import java.awt.Color;
import java.awt.GradientPaint;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 3.0.0
 */
public class GradientPaintConverter extends AbstractConverter<GradientPaint> {
    @Override
    public GradientPaint fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Map) {
            return handleAsMap((Map) value);
        } else if (value instanceof GradientPaint) {
            return (GradientPaint) value;
        } else {
            throw illegalValue(value, GradientPaint.class);
        }
    }

    @Override
    public String toString(GradientPaint value) throws ConversionException {
        if (null == value) { return null; }
        return new StringBuilder()
            .append(value.getPoint1().getX())
            .append(", ")
            .append(value.getPoint1().getY())
            .append(", ")
            .append(ColorConverter.format(value.getColor1()))
            .append(", ")
            .append(value.getPoint2().getX())
            .append(", ")
            .append(value.getPoint2().getY())
            .append(", ")
            .append(ColorConverter.format(value.getColor2()))
            .append(", ")
            .append(value.isCyclic())
            .toString();
    }

    protected GradientPaint handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }

        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        Color c1 = Color.WHITE;
        Color c2 = Color.BLACK;
        boolean cyclic = false;

        if (str.contains("|")) {
            String[] parts = str.split("\\|");
            switch (parts.length) {
                case 4:
                    cyclic = parseBoolean(parts[3]);
                case 3:
                    ColorConverter colorConverter = new ColorConverter();
                    // point1
                    String[] p1 = parts[0].split(",");
                    if (p1.length != 2) {
                        throw illegalValue(str, GradientPaint.class);
                    }
                    x1 = parseValue(p1[0]);
                    y1 = parseValue(p1[1]);

                    // point2
                    String[] p2 = parts[1].split(",");
                    if (p2.length != 2) {
                        throw illegalValue(str, GradientPaint.class);
                    }
                    x2 = parseValue(p2[0]);
                    y2 = parseValue(p2[1]);

                    String[] colors = parts[2].split(",");
                    try {
                        c1 = colorConverter.fromObject(colors[0]);
                    } catch (Exception e) {
                        throw illegalValue(colors[0], GradientPaint.class);
                    }

                    try {
                        c2 = colorConverter.fromObject(colors[1]);
                    } catch (Exception e) {
                        throw illegalValue(colors[1], GradientPaint.class);
                    }
                    return new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic);
                default:
                    throw illegalValue(str, GradientPaint.class);
            }
        } else {
            String[] parts = str.split(",");
            switch (parts.length) {
                case 7:
                    cyclic = parseBoolean(parts[6]);
                case 6:
                    ColorConverter colorConverter = new ColorConverter();
                    x1 = parseValue(parts[0]);
                    y1 = parseValue(parts[1]);
                    x2 = parseValue(parts[3]);
                    y2 = parseValue(parts[4]);
                    try {
                        c1 = colorConverter.fromObject(parts[2]);
                    } catch (Exception e) {
                        throw illegalValue(parts[2], GradientPaint.class);
                    }
                    try {
                        c2 = colorConverter.fromObject(parts[5]);
                    } catch (Exception e) {
                        throw illegalValue(parts[5], GradientPaint.class);
                    }
                    return new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic);
                default:
                    throw illegalValue(str, GradientPaint.class);
            }
        }

    }

    protected GradientPaint handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        Color c1 = Color.WHITE;
        Color c2 = Color.BLACK;
        boolean cyclic = false;
        switch (list.size()) {
            case 7:
                cyclic = parseBoolean(String.valueOf(list.get(6)));
            case 6:
                ColorConverter colorConverter = new ColorConverter();
                x1 = parseValue(list.get(0));
                y1 = parseValue(list.get(1));
                x2 = parseValue(list.get(3));
                y2 = parseValue(list.get(4));
                try {
                    c1 = colorConverter.fromObject(list.get(2));
                } catch (Exception e) {
                    throw illegalValue(list.get(2), GradientPaint.class);
                }
                try {
                    c2 = colorConverter.fromObject(list.get(5));
                } catch (Exception e) {
                    throw illegalValue(list.get(5), GradientPaint.class);
                }
                return new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic);
            default:
                throw illegalValue(list, GradientPaint.class);
        }
    }

    protected GradientPaint handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        float x1 = (Float) getMapValue(map, "x1", 0f);
        float y1 = (Float) getMapValue(map, "y1", 0f);
        float x2 = (Float) getMapValue(map, "x2", 0f);
        float y2 = (Float) getMapValue(map, "y2", 0f);
        Color c1 = Color.WHITE;
        Color c2 = Color.BLACK;
        boolean cyclic = false;

        ColorConverter colorConverter = new ColorConverter();
        Object colorValue = map.get("c1");
        try {
            if (null != colorValue) {
                c1 = colorConverter.fromObject(colorValue);
            } else {
                c1 = Color.WHITE;
            }
        } catch (Exception e) {
            throw illegalValue(colorValue, GradientPaint.class);
        }
        colorValue = map.get("c2");
        try {
            if (null != colorValue) {
                c2 = colorConverter.fromObject(colorValue);
            } else {
                c2 = Color.BLACK;
            }
        } catch (Exception e) {
            throw illegalValue(colorValue, GradientPaint.class);
        }
        Object cyclicValue = map.get("cyclic");
        if (null != cyclicValue) {
            cyclic = parseBoolean(String.valueOf(cyclicValue));
        }

        return new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic);
    }

    protected float parse(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, GradientPaint.class, e);
        }
    }

    protected boolean parseBoolean(String val) {
        try {
            return Boolean.parseBoolean(val.trim());
        } catch (Exception e) {
            throw illegalValue(val, GradientPaint.class, e);
        }
    }

    protected float parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, GradientPaint.class);
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
        throw illegalValue(map, GradientPaint.class);
    }
}
