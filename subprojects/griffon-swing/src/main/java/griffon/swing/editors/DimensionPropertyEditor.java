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

import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 2.0.0
 */
@PropertyEditorFor(Dimension.class)
public class DimensionPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        Dimension dimension = (Dimension) getValue();
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
        } else if (value instanceof Dimension) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Dimension.class);
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
                int s = parseValue(parts[0]);
                super.setValueInternal(new Dimension(s, s));
                break;
            case 2:
                int w = parseValue(parts[0]);
                int h = parseValue(parts[1]);
                super.setValueInternal(new Dimension(w, h));
                break;
            default:
                throw illegalValue(str, Dimension.class);
        }
    }

    protected void handleAsList(List<?> list) {
        if(list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        switch (list.size()) {
            case 1:
                int s = parseValue(list.get(0));
                super.setValueInternal(new Dimension(s, s));
                break;
            case 2:
                int w = parseValue(list.get(0));
                int h = parseValue(list.get(1));
                super.setValueInternal(new Dimension(w, h));
                break;
            default:
                throw illegalValue(list, Dimension.class);
        }
    }

    protected void handleAsMap(Map<?, ?> map) {
        if(map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        int w = getMapValue(map, "width", 0);
        int h = getMapValue(map, "height", 0);
        super.setValueInternal(new Dimension(w, h));
    }

    protected int parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Dimension.class);
    }

    protected int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Dimension.class, e);
        }
    }

    protected int parse(Number val) {
        return val.intValue();
    }

    protected int getMapValue(Map<?, ?> map, String key, int defaultValue) {
        Object val = map.get(key);
        if (null == val) val = map.get(String.valueOf(key.charAt(0)));
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, Dimension.class);
    }

    protected void handleAsNumber(Number value) {
        int s = parse(value);
        super.setValueInternal(new Dimension(s, s));
    }
}
