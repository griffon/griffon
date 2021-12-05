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
package griffon.swing.editors;

import griffon.core.editors.AbstractPropertyEditor;
import griffon.metadata.PropertyEditorFor;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@PropertyEditorFor(LinearGradientPaint.class)
public class LinearGradientPaintPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        LinearGradientPaint p = (LinearGradientPaint) getValue();
        return new StringBuilder()
            .append(p.getStartPoint().getX())
            .append(", ")
            .append(p.getStartPoint().getY())
            .append(", ")
            .append(p.getEndPoint().getX())
            .append(", ")
            .append(p.getEndPoint().getY())
            .append(", ")
            .append(formatFractions(p.getFractions()))
            .append(", ")
            .append(formatColors(p.getColors()))
            .append(", ")
            .append(p.getCycleMethod().name())
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
            b.append(ColorPropertyEditor.format(c));
        }
        return b.append("]").toString();
    }

    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            handleAsList((List) value);
        } else if (value instanceof Map) {
            handleAsMap((Map) value);
        } else if (value instanceof LinearGradientPaint) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, LinearGradientPaint.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }

        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        float[] fractions = null;
        Color[] colors = null;
        MultipleGradientPaint.CycleMethod cyclicMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        String[] parts = str.split(",");
        switch (parts.length) {
            case 7:
                cyclicMethod = parseCyclicMethod(str, parts[6]);
            case 6:
                x1 = parseValue(parts[0]);
                y1 = parseValue(parts[1]);
                x2 = parseValue(parts[2]);
                y2 = parseValue(parts[3]);
                fractions = parseFractions(str, parts[4].trim());
                colors = parseColors(str, parts[5].trim());
                if (fractions.length != colors.length) {
                    throw illegalValue(str, LinearGradientPaint.class);
                }
                super.setValueInternal(new LinearGradientPaint(x1, y1, x2, y2, fractions, colors, cyclicMethod));
                break;
            default:
                throw illegalValue(str, LinearGradientPaint.class);
        }
    }

    protected void handleAsList(List<?> list) {
        if(list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        float[] fractions = null;
        Color[] colors = null;
        MultipleGradientPaint.CycleMethod cyclicMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;
        switch (list.size()) {
            case 7:
                cyclicMethod = parseCyclicMethod(list, String.valueOf(list.get(6)).trim());
            case 6:
                x1 = parseValue(list.get(0));
                y1 = parseValue(list.get(1));
                x2 = parseValue(list.get(2));
                y2 = parseValue(list.get(3));
                fractions = parseFractions(list, list.get(4));
                colors = parseColors(list, list.get(5));
                if (fractions.length != colors.length) {
                    throw illegalValue(list, LinearGradientPaint.class);
                }
                super.setValueInternal(new LinearGradientPaint(x1, y1, x2, y2, fractions, colors, cyclicMethod));
                break;
            default:
                throw illegalValue(list, LinearGradientPaint.class);
        }
    }

    protected void handleAsMap(Map<?, ?> map) {
        if(map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        float x1 = (Float) getMapValue(map, "x1", 0f);
        float y1 = (Float) getMapValue(map, "y1", 0f);
        float x2 = (Float) getMapValue(map, "x2", 0f);
        float y2 = (Float) getMapValue(map, "y2", 0f);
        MultipleGradientPaint.CycleMethod cyclicMethod = MultipleGradientPaint.CycleMethod.NO_CYCLE;

        float[] fractions = parseFractions(map, map.get("fractions"));
        Color[] colors = parseColors(map, map.get("colors"));
        if (fractions.length != colors.length) {
            throw illegalValue(map, LinearGradientPaint.class);
        }
        Object cyclicValue = map.get("cycle");
        if (null != cyclicValue) {
            cyclicMethod = parseCyclicMethod(map, String.valueOf(cyclicValue));
        }

        super.setValueInternal(new LinearGradientPaint(x1, y1, x2, y2, fractions, colors, cyclicMethod));
    }

    protected float[] parseFractions(Object source, Object obj) {
        if (obj instanceof CharSequence) {
            return parseFractions(source, String.valueOf(obj).trim());
        } else if (obj instanceof List) {
            return parseFractions(source, (List) obj);
        }
        throw illegalValue(source, LinearGradientPaint.class);
    }

    protected float[] parseFractions(Object source, String str) {
        if (!str.startsWith("[") || !str.endsWith("]")) {
            throw illegalValue(source, LinearGradientPaint.class);
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
        throw illegalValue(source, LinearGradientPaint.class);
    }

    protected Color[] parseColors(Object source, String str) {
        if (!str.startsWith("[") || !str.endsWith("]")) {
            throw illegalValue(source, LinearGradientPaint.class);
        }

        String[] strs = str.substring(1, str.length() - 1).split(":");
        Color[] colors = new Color[strs.length];
        ColorPropertyEditor colorEditor = new ColorPropertyEditor();
        for (int i = 0; i < strs.length; i++) {
            try {
                colorEditor.setValueInternal(strs[i]);
                colors[i] = (Color) colorEditor.getValue();
            } catch (Exception e) {
                throw illegalValue(strs[i], LinearGradientPaint.class);
            }
        }

        return colors;
    }

    protected Color[] parseColors(Object source, List<?> list) {
        Color[] colors = new Color[list.size()];
        ColorPropertyEditor colorEditor = new ColorPropertyEditor();
        for (int i = 0; i < list.size(); i++) {
            try {
                colorEditor.setValueInternal(list.get(i));
                colors[i] = (Color) colorEditor.getValue();
            } catch (Exception e) {
                throw illegalValue(list.get(i), LinearGradientPaint.class, e);
            }
        }

        return colors;
    }

    protected MultipleGradientPaint.CycleMethod parseCyclicMethod(Object source, String str) {
        try {
            Field cyclicMethodField = MultipleGradientPaint.CycleMethod.class.getDeclaredField(str.toUpperCase().trim());
            return (MultipleGradientPaint.CycleMethod) cyclicMethodField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw illegalValue(source, LinearGradientPaint.class, e);
        }
    }

    protected float parse(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, LinearGradientPaint.class, e);
        }
    }

    protected float parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, LinearGradientPaint.class);
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
        throw illegalValue(map, LinearGradientPaint.class);
    }
}
