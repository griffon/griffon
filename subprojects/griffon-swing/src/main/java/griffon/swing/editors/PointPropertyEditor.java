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
public class PointPropertyEditor extends AbstractPropertyEditor {
    public String getAsText() {
        if (null == getValue()) return null;
        Point p = (Point) getValue();
        return p.getX() + ", " + p.getY();
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
        } else if (value instanceof Point) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Point.class);
        }
    }

    private void handleAsString(String str) {
        String[] parts = str.split(",");
        switch (parts.length) {
            case 1:
                int s = parseValue(parts[0]);
                super.setValueInternal(new Point(s, s));
                break;
            case 2:
                int x = parseValue(parts[0]);
                int y = parseValue(parts[1]);
                super.setValueInternal(new Point(x, y));
                break;
            default:
                throw illegalValue(str, Point.class);
        }
    }

    private void handleAsList(List list) {
        switch (list.size()) {
            case 1:
                int s = parseValue(list.get(0));
                super.setValueInternal(new Point(s, s));
                break;
            case 2:
                int x = parseValue(list.get(0));
                int y = parseValue(list.get(1));
                super.setValueInternal(new Point(x, y));
                break;
            default:
                throw illegalValue(list, Point.class);
        }
    }

    private void handleAsMap(Map map) {
        int x = getMapValue(map, "x", 0);
        int y = getMapValue(map, "y", 0);
        super.setValueInternal(new Point(x, y));
    }

    private int parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Point.class);
    }

    private int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Point.class, e);
        }
    }

    private int parse(Number val) {
        return val.intValue();
    }

    private int getMapValue(Map map, String key, int defaultValue) {
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

    private void handleAsNumber(Number value) {
        int s = parse(value);
        super.setValueInternal(new Point(s, s));
    }
}
