/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.converter.ConversionException;
import org.codehaus.griffon.converter.AbstractConverter;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class FontConverter extends AbstractConverter<Font> {
    @Override
    public Font fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof List) {
            return handleAsList((List) value);
        } else if (value instanceof Map) {
            return handleAsMap((Map) value);
        } else if (value instanceof Font) {
            return (Font) value;
        } else {
            throw illegalValue(value, Font.class);
        }
    }

    @Override
    public String toString(Font value) throws ConversionException {
        if (null == value) { return null; }
        return value.getFamily() + "-" + formatStyle(value) + "-" + value.getSize();
    }

    protected String formatStyle(Font font) {
        if (font.isBold() && font.isItalic()) {
            return "BOLDITALIC";
        } else if (font.isBold()) {
            return "BOLD";
        } else if (font.isItalic()) {
            return "ITALIC";
        }
        return "PLAIN";
    }

    protected Font handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }
        String[] parts = str.split("-");
        if (parts.length != 3) {
            throw illegalValue(str, Font.class);
        }

        String family = parts[0];
        int style = resolveStyle(str, parts[1]);
        int size = parseSize(str, parts[2]);

        return new Font(family, style, size);
    }

    protected Font handleAsList(List<?> list) {
        if (list.isEmpty()) {
            return null;
        }

        if (list.size() != 3) {
            throw illegalValue(list, Font.class);
        }

        String family = String.valueOf(list.get(0));
        int style = resolveStyle(list, String.valueOf(list.get(1)));
        int size = parseSize(list, String.valueOf(list.get(2)));

        return new Font(family, style, size);
    }

    protected Font handleAsMap(Map<?, ?> map) {
        if (map.isEmpty()) {
            return null;
        }

        String family = getMapValue(map, "family", "");
        String style = getMapValue(map, "style", "");
        String size = getMapValue(map, "size", "");
        return new Font(family, resolveStyle(map, style), parseSize(map, size));
    }

    protected String getMapValue(Map<?, ?> map, String key, String defaultValue) {
        Object val = map.get(key);
        if (null == val) { val = map.get(String.valueOf(key.charAt(0))); }
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return String.valueOf(val).trim();
        }
        throw illegalValue(map, Font.class);
    }

    protected int parseSize(Object source, String str) {
        int size;
        try {
            size = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            throw illegalValue(source, Font.class);
        }
        return size;
    }

    protected int resolveStyle(Object source, String str) {
        if ("PLAIN".equalsIgnoreCase(str)) {
            return Font.PLAIN;
        } else if ("BOLD".equalsIgnoreCase(str)) {
            return Font.BOLD;
        } else if ("ITALIC".equalsIgnoreCase(str)) {
            return Font.ITALIC;
        } else if ("BOLDITALIC".equalsIgnoreCase(str)) {
            return Font.BOLD | Font.ITALIC;
        }
        throw illegalValue(source, Font.class);
    }
}
