/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
import griffon.metadata.PropertyEditorFor;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
@PropertyEditorFor(Point2D.class)
public class Point2DPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        Point2D p = (Point2D) getValue();
        return p.getX() + ", " + p.getY();
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
        } else if (value instanceof Number) {
            handleAsNumber((Number) value);
        } else if (value instanceof Point2D) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Point2D.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }

        String[] parts = str.split(",");
        switch (parts.length) {
            case 1:
                double s = parseValue(parts[0]);
                super.setValueInternal(new Point2D(s, s));
                break;
            case 2:
                double x = parseValue(parts[0]);
                double y = parseValue(parts[1]);
                super.setValueInternal(new Point2D(x, y));
                break;
            default:
                throw illegalValue(str, Point2D.class);
        }
    }

    protected void handleAsList(List<?> list) {
        if (list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        switch (list.size()) {
            case 1:
                double s = parseValue(list.get(0));
                super.setValueInternal(new Point2D(s, s));
                break;
            case 2:
                double x = parseValue(list.get(0));
                double y = parseValue(list.get(1));
                super.setValueInternal(new Point2D(x, y));
                break;
            default:
                throw illegalValue(list, Point2D.class);
        }
    }

    protected void handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        double x = getMapValue(map, "x", 0);
        double y = getMapValue(map, "y", 0);
        super.setValueInternal(new Point2D(x, y));
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

    protected void handleAsNumber(Number value) {
        double s = parse(value);
        super.setValueInternal(new Point2D(s, s));
    }
}
