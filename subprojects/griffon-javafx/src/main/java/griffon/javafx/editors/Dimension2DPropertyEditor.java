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
package griffon.javafx.editors;

import griffon.core.editors.AbstractPropertyEditor;
import griffon.metadata.PropertyEditorFor;
import javafx.geometry.Dimension2D;

import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
@PropertyEditorFor(Dimension2D.class)
public class Dimension2DPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        Dimension2D dimension = (Dimension2D) getValue();
        return dimension.getWidth() + ", " + dimension.getHeight();
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
        } else if (value instanceof Dimension2D) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Dimension2D.class);
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
                super.setValueInternal(new Dimension2D(s, s));
                break;
            case 2:
                double w = parseValue(parts[0]);
                double h = parseValue(parts[1]);
                super.setValueInternal(new Dimension2D(w, h));
                break;
            default:
                throw illegalValue(str, Dimension2D.class);
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
                super.setValueInternal(new Dimension2D(s, s));
                break;
            case 2:
                double w = parseValue(list.get(0));
                double h = parseValue(list.get(1));
                super.setValueInternal(new Dimension2D(w, h));
                break;
            default:
                throw illegalValue(list, Dimension2D.class);
        }
    }

    protected void handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        double w = getMapValue(map, "width", 0);
        double h = getMapValue(map, "height", 0);
        super.setValueInternal(new Dimension2D(w, h));
    }

    protected double parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Dimension2D.class);
    }

    protected double parse(String val) {
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Dimension2D.class, e);
        }
    }

    protected double parse(Number val) {
        return val.doubleValue();
    }

    protected double getMapValue(Map<?, ?> map, String key, double defaultValue) {
        Object val = map.get(key);
        if (null == val) val = map.get(String.valueOf(key.charAt(0)));
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Dimension2D.class);
    }

    protected void handleAsNumber(Number value) {
        double s = parse(value);
        super.setValueInternal(new Dimension2D(s, s));
    }
}
