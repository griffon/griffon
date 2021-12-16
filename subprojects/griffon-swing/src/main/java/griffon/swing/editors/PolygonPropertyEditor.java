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

import java.awt.Polygon;
import java.util.List;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@PropertyEditorFor(Polygon.class)
public class PolygonPropertyEditor extends AbstractPropertyEditor {
    @Override
    public String getAsText() {
        if (null == getValue()) return null;
        Polygon p = (Polygon) getValue();
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < p.npoints; i++) {
            if (i != 0) {
                b.append(", ");
            }
            b.append(p.xpoints[i])
                .append(", ")
                .append(p.ypoints[i]);
        }
        return b.toString();
    }

    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            handleAsList((List) value);
        } else if (value instanceof Polygon) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Polygon.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }

        String[] parts = str.split(",");
        if (parts.length % 2 == 1) {
            throw illegalValue(str, Polygon.class);
        }

        int npoints = parts.length / 2;
        int[] xpoints = new int[npoints];
        int[] ypoints = new int[npoints];

        for (int i = 0; i < npoints; i++) {
            xpoints[i] = parse(parts[2 * i]);
            ypoints[i] = parse(parts[(2 * i) + 1]);
        }
        super.setValueInternal(new Polygon(xpoints, ypoints, npoints));
    }

    protected void handleAsList(List<?> list) {
        if (list.isEmpty()) {
            super.setValueInternal(null);
            return;
        }

        if (list.size() % 2 == 1) {
            throw illegalValue(list, Polygon.class);
        }

        int npoints = list.size() / 2;
        int[] xpoints = new int[npoints];
        int[] ypoints = new int[npoints];

        for (int i = 0; i < npoints; i++) {
            xpoints[i] = parseValue(list.get(2 * i));
            ypoints[i] = parseValue(list.get((2 * i) + 1));
        }
        super.setValueInternal(new Polygon(xpoints, ypoints, npoints));
    }

    protected int parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, Polygon.class);
    }

    protected int parse(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, Polygon.class, e);
        }
    }

    protected int parse(Number val) {
        return val.intValue();
    }
}
