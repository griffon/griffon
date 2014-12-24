/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.javafx.editors;

import griffon.core.editors.AbstractPropertyEditor;
import griffon.core.formatters.Formatter;
import griffon.core.formatters.ParseException;
import griffon.javafx.formatters.ColorFormatter;
import griffon.metadata.PropertyEditorFor;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;


/**
 * @author Andres Almiray
 */
@PropertyEditorFor(Color.class)
public class ColorPropertyEditor extends AbstractPropertyEditor {
    public static String format(Color color) {
        return ColorFormatter.LONG.format(color);
    }

    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        return isBlank(getFormat()) ? format((Color) getValueInternal()) : getFormattedValue();
    }

    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value).trim());
        } else if (value instanceof List) {
            handleAsList((List) value);
        } else if (value instanceof Map) {
            handleAsMap((Map) value);
        } else if (value instanceof Number) {
            handleAsNumber((Number) value);
        } else if (value instanceof Color) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Color.class);
        }
    }

    @Override
    protected Formatter<Color> resolveFormatter() {
        return !isBlank(getFormat()) ? ColorFormatter.getInstance(getFormat()) : null;
    }

    private void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }
        try {
            super.setValueInternal(ColorFormatter.parseColor(str));
        } catch (ParseException e) {
            throw illegalValue(str, Color.class, e);
        }
    }

    private void handleAsList(List<?> list) {
        if (list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        List<Object> values = new ArrayList<>();
        values.addAll(list);
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
        super.setValueInternal(
            new Color(
                (Double) values.get(0),
                (Double) values.get(1),
                (Double) values.get(2),
                (Double) values.get(3)
            )
        );
    }

    private void handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        double r = getMapValue(map, "red", 0d);
        double g = getMapValue(map, "green", 0d);
        double b = getMapValue(map, "blue", 0d);
        double a = getMapValue(map, "alpha", 1d);
        super.setValueInternal(new Color(r, g, b, a));
    }

    private double parse(String val) {
        try {
            return (Integer.parseInt(String.valueOf(val).trim(), 16) & 0xFF) / 255d;
        } catch (NumberFormatException e) {
            throw illegalValue(val, Color.class, e);
        }
    }

    private double parse(Number val) {
        return val.doubleValue();
    }

    private double getMapValue(Map<?, ?> map, String key, double defaultValue) {
        Object val = map.get(key);
        if (null == val) val = map.get(String.valueOf(key.charAt(0)));
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Color.class);
    }

    private void handleAsNumber(Number value) {
        double c = parse(value);
        super.setValueInternal(new Color(c, c, c, 1d));
    }
}