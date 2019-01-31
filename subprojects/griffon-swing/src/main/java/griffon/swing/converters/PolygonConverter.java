/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.swing.converters;

import org.kordamp.jsr377.converter.AbstractConverter;

import javax.application.converter.ConversionException;
import java.awt.Polygon;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PolygonConverter extends AbstractConverter<Polygon> {
    @Override
    public Polygon fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Polygon) {
            return (Polygon) value;
        } else {
            throw illegalValue(value, Polygon.class);
        }
    }

    @Override
    public String toString(Polygon value) throws ConversionException {
        if (null == value) { return null; }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < value.npoints; i++) {
            if (i != 0) {
                b.append(", ");
            }
            b.append(value.xpoints[i])
                .append(", ")
                .append(value.ypoints[i]);
        }
        return b.toString();
    }

    protected Polygon handleAsString(String str) {
        if (isBlank(str)) {
            return null;
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
        return new Polygon(xpoints, ypoints, npoints);
    }

    protected Polygon handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
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
        return new Polygon(xpoints, ypoints, npoints);
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
