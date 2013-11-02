/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.swing.editors;

import griffon.core.resources.editors.AbstractPropertyEditor;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class RadialGradientPaintPropertyEditor extends AbstractPropertyEditor {
    public String getAsText() {
        if (null == getValue()) return null;
        RadialGradientPaint p = (RadialGradientPaint) getValue();
        return new StringBuilder()
            .append(p.getCenterPoint().getX())
            .append(", ")
            .append(p.getCenterPoint().getY())
            .append(", ")
            .append(p.getRadius())
            .append(", ")
            .append(p.getFocusPoint().getX())
            .append(", ")
            .append(p.getFocusPoint().getY())
            .append(", ")
            .append(formatFractions(p.getFractions()))
            .append(", ")
            .append(formatColors(p.getColors()))
            .append(", ")
            .append(p.getCycleMethod().name())
            .toString();
    }

    private String formatFractions(float[] fractions) {
        StringBuilder b = new StringBuilder("[");
        boolean first = true;

        for (float f : fractions) {
            if (first) {
                first = false;
            } else {
                b.append(", ");
            }
            b.append(f);
        }
        return b.append("]").toString();
    }

    private String formatColors(Color[] colors) {
        StringBuilder b = new StringBuilder("[");
        boolean first = true;

        for (Color c : colors) {
            if (first) {
                first = false;
            } else {
                b.append(", ");
            }
            b.append(ColorPropertyEditor.format(c));
        }
        return b.append("]").toString();
    }

    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            handleAsList((List) value);
        } else if (value instanceof Map) {
            handleAsMap((Map) value);
        } else if (value instanceof RadialGradientPaint) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, RadialGradientPaint.class);
        }
    }

    private void handleAsString(String str) {
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
                super.setValueInternal(new RadialGradientPaint(cx, cy, radius, fx, fy, fractions, colors, cyclicMethod));
                break;
            default:
                throw illegalValue(str, RadialGradientPaint.class);
        }
    }

    private void handleAsList(List list) {
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
                super.setValueInternal(new RadialGradientPaint(cx, cy, radius, fx, fy, fractions, colors, cyclicMethod));
                break;
            default:
                throw illegalValue(list, RadialGradientPaint.class);
        }
    }

    private void handleAsMap(Map map) {
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
        Object cyclicValue = map.get("cyclic");
        if (null != cyclicValue) {
            cyclicMethod = parseCyclicMethod(map, String.valueOf(cyclicValue));
        }

        super.setValueInternal(new RadialGradientPaint(cx, cy, radius, fx, fy, fractions, colors, cyclicMethod));
    }

    private float[] parseFractions(Object source, Object obj) {
        if (obj instanceof CharSequence) {
            return parseFractions(source, String.valueOf(obj).trim());
        } else if (obj instanceof List) {
            return parseFractions(source, (List) obj);
        }
        throw illegalValue(source, RadialGradientPaint.class);
    }

    private float[] parseFractions(Object source, String str) {
        if (str.startsWith("[") && str.endsWith("]")) {
            throw illegalValue(source, RadialGradientPaint.class);
        }

        String[] strs = str.substring(1, str.length() - 2).split(",");
        float[] fractions = new float[strs.length];
        for (int i = 0; i < strs.length; i++) {
            fractions[i] = parseValue(strs[i]);
        }

        return fractions;
    }

    private float[] parseFractions(Object source, List list) {
        float[] fractions = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            fractions[i] = parseValue(list.get(1));
        }

        return fractions;
    }

    private Color[] parseColors(Object source, Object obj) {
        if (obj instanceof CharSequence) {
            return parseColors(source, String.valueOf(obj).trim());
        } else if (obj instanceof List) {
            return parseColors(source, (List) obj);
        }
        throw illegalValue(source, RadialGradientPaint.class);
    }

    private Color[] parseColors(Object source, String str) {
        if (str.startsWith("[") && str.endsWith("]")) {
            throw illegalValue(source, RadialGradientPaint.class);
        }

        String[] strs = str.substring(1, str.length() - 2).split(",");
        Color[] colors = new Color[strs.length];
        ColorPropertyEditor colorEditor = new ColorPropertyEditor();
        for (int i = 0; i < strs.length; i++) {
            try {
                colorEditor.setValueInternal(strs[i]);
                colors[i] = (Color) colorEditor.getValue();
            } catch (Exception e) {
                throw illegalValue(strs[i], RadialGradientPaint.class);
            }
        }

        return colors;
    }

    private Color[] parseColors(Object source, List list) {
        Color[] colors = new Color[list.size()];
        ColorPropertyEditor colorEditor = new ColorPropertyEditor();
        for (int i = 0; i < list.size(); i++) {
            try {
                colorEditor.setValueInternal(list.get(i));
                colors[i] = (Color) colorEditor.getValue();
            } catch (Exception e) {
                throw illegalValue(list.get(i), RadialGradientPaint.class);
            }
        }

        return colors;
    }

    private MultipleGradientPaint.CycleMethod parseCyclicMethod(Object source, String str) {
        try {
            Field cyclicMethodField = MultipleGradientPaint.CycleMethod.class.getDeclaredField(str.toUpperCase().trim());
            return (MultipleGradientPaint.CycleMethod) cyclicMethodField.get(null);
        } catch (NoSuchFieldException e) {
            throw illegalValue(source, RadialGradientPaint.class, e);
        } catch (IllegalAccessException e) {
            throw illegalValue(source, RadialGradientPaint.class, e);
        }
    }

    private float parse(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, RadialGradientPaint.class, e);
        }
    }

    private float parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, RadialGradientPaint.class);
    }

    private float parse(Number val) {
        return val.floatValue();
    }

    private Object getMapValue(Map map, String key, Object defaultValue) {
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
