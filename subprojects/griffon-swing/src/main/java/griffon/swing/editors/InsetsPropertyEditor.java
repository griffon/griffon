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
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 1.1.0
 */
public class InsetsPropertyEditor extends AbstractPropertyEditor {
    public String getAsText() {
        if (null == getValue()) return null;
        Insets i = (Insets) getValue();
        return i.top + ", " + i.left + ", " + i.bottom + ", " + i.right;
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
        } else if (value instanceof Number) {
            handleAsNumber((Number) value);
        } else if (value instanceof Insets) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Insets.class);
        }
    }

    private void handleAsString(String str) {
        int t = 0;
        int l = 0;
        int r = 0;
        int b = 0;
        String[] parts = str.split(",");
        switch (parts.length) {
            case 4:
                b = parseValue(parts[3]);
            case 3:
                r = parseValue(parts[2]);
            case 2:
                l = parseValue(parts[1]);
            case 1:
                t = parseValue(parts[0]);
                super.setValueInternal(new Insets(t, l, r, b));
                break;
            default:
                throw illegalValue(str, Insets.class);
        }
    }

    private void handleAsList(List list) {
        int t = 0;
        int l = 0;
        int r = 0;
        int b = 0;
        switch (list.size()) {
            case 4:
                b = parseValue(list.get(3));
            case 3:
                r = parseValue(list.get(2));
            case 2:
                l = parseValue(list.get(1));
            case 1:
                t = parseValue(list.get(0));
                super.setValueInternal(new Insets(t, l, r, b));
                break;
            default:
                throw illegalValue(list, Insets.class);
        }
    }

    private void handleAsMap(Map map) {
        int t = getMapValue(map, "top", 0);
        int l = getMapValue(map, "left", 0);
        int r = getMapValue(map, "right", 0);
        int b = getMapValue(map, "bottom", 0);
        super.setValueInternal(new Insets(t, l, r, b));
    }

    private int parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Insets.class);
    }

    private int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Insets.class, e);
        }
    }

    private int parse(Number val) {
        return val.intValue();
    }

    private int getMapValue(Map map, String key, int defaultValue) {
        Object val = map.get(key);
        if (null == val) val = map.get(String.valueOf(key.charAt(0)));
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Insets.class);
    }

    private void handleAsNumber(Number value) {
        int c = parse(value);
        super.setValueInternal(new Insets(c, c, c, c));
    }
}
