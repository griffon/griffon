/*
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
import javafx.scene.paint.RadialGradient;
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
@PropertyEditorFor(RadialGradient.class)
public class RadialGradientPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        return getValueInternal().toString();
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
        } else if (value instanceof RadialGradient) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, RadialGradient.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }
        try {
            super.setValueInternal(RadialGradient.valueOf(str));
        } catch (Exception e) {
            throw illegalValue(str, RadialGradient.class, e);
        }
    }

    protected void handleAsList(List<?> list) {
        if (list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        double fa = 0;
        double fd = 0;
        double cx = 0;
        double cy = 0;
        double r = 0;
        boolean proportional = false;
        List<Stop> stops = new ArrayList<>();
        CycleMethod cyclicMethod = CycleMethod.NO_CYCLE;

        switch (list.size()) {
            case 8:
                cyclicMethod = parseCyclicMethod(list, String.valueOf(list.get(7)).trim());
            case 7:
                fa = parseValue(list.get(0));
                fd = parseValue(list.get(1));
                cx = parseValue(list.get(2));
                cy = parseValue(list.get(3));
                r = parseValue(list.get(4));
                proportional = (Boolean) list.get(5);
                stops = (List<Stop>) list.get(6);
                super.setValueInternal(new RadialGradient(fa, fd, cx, cy, r, proportional, cyclicMethod, stops));
                break;
            default:
                throw illegalValue(list, RadialGradient.class);
        }
    }

    protected void handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        double fa = (Double) getMapValue(map, "fa", 0d);
        double fd = (Double) getMapValue(map, "fd", 0d);
        double cx = (Double) getMapValue(map, "cx", 0d);
        double cy = (Double) getMapValue(map, "cy", 0d);
        double r = (Double) getMapValue(map, "r", 0d);
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

        super.setValueInternal(new RadialGradient(fa, fd, cx, cy, r, proportional, cyclicMethod, stops));
    }

    protected CycleMethod parseCyclicMethod(Object source, String str) {
        try {
            Field cyclicMethodField = CycleMethod.class.getDeclaredField(str.toUpperCase().trim());
            return (CycleMethod) cyclicMethodField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw illegalValue(source, RadialGradient.class, e);
        }
    }

    protected double parse(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, RadialGradient.class, e);
        }
    }

    protected double parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, RadialGradient.class);
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
        throw illegalValue(map, RadialGradient.class);
    }
}