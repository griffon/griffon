/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
@PropertyEditorFor(LinearGradient.class)
public class LinearGradientPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        return getValueInternal().toString();
    }

    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value).trim());
        } else if (value instanceof List) {
            handleAsList((List) value);
        } else if (value instanceof Map) {
            handleAsMap((Map) value);
        } else if (value instanceof LinearGradient) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, LinearGradient.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }
        try {
            super.setValueInternal(LinearGradient.valueOf(str));
        } catch (Exception e) {
            throw illegalValue(str, LinearGradient.class, e);
        }
    }

    protected void handleAsList(List<?> list) {
        if (list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        double sx = 0;
        double sy = 0;
        double ex = 0;
        double ey = 0;
        boolean proportional = false;
        List<Stop> stops = new ArrayList<>();
        CycleMethod cyclicMethod = CycleMethod.NO_CYCLE;

        switch (list.size()) {
            case 7:
                cyclicMethod = parseCyclicMethod(list, String.valueOf(list.get(6)).trim());
            case 6:
                sx = parseValue(list.get(0));
                sy = parseValue(list.get(1));
                ex = parseValue(list.get(2));
                ey = parseValue(list.get(3));
                proportional = (Boolean) list.get(4);
                stops = (List<Stop>) list.get(5);
                super.setValueInternal(new LinearGradient(sx, sy, ex, ey, proportional, cyclicMethod, stops));
                break;
            default:
                throw illegalValue(list, LinearGradient.class);
        }
    }

    protected void handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        double sx = (Double) getMapValue(map, "sx", 0d);
        double sy = (Double) getMapValue(map, "sy", 0d);
        double ex = (Double) getMapValue(map, "ex", 0d);
        double ey = (Double) getMapValue(map, "ey", 0d);
        boolean proportional = (Boolean) getMapValue(map, "p", false);
        List<Stop> stops = new ArrayList<>();
        CycleMethod cyclicMethod = CycleMethod.NO_CYCLE;

        if (map.containsKey("stops")) {
            stops = (List<Stop>) map.get("stops");
        }
        Object cyclicValue = map.get("cycle");
        if (null != cyclicValue) {
            cyclicMethod = parseCyclicMethod(map, String.valueOf(cyclicValue));
        }

        super.setValueInternal(new LinearGradient(sx, sy, ex, ey, proportional, cyclicMethod, stops));
    }

    protected CycleMethod parseCyclicMethod(Object source, String str) {
        try {
            Field cyclicMethodField = CycleMethod.class.getDeclaredField(str.toUpperCase().trim());
            return (CycleMethod) cyclicMethodField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw illegalValue(source, LinearGradient.class, e);
        }
    }

    protected double parse(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, LinearGradient.class, e);
        }
    }

    protected double parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, LinearGradient.class);
    }

    protected double parse(Number val) {
        return val.doubleValue();
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
        throw illegalValue(map, LinearGradient.class);
    }
}