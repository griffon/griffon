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
 */
public class FontPropertyEditor extends AbstractPropertyEditor {
    public String getAsText() {
        if (null == getValue()) return null;
        Font font = (Font) getValue();
        return font.getFamily() + "-" + formatStyle(font) + "-" + font.getSize();
    }

    private String formatStyle(Font font) {
        if (font.isBold() && font.isItalic()) {
            return "BOLDITALIC";
        } else if (font.isBold()) {
            return "BOLD";
        } else if (font.isItalic()) {
            return "ITALIC";
        }
        return "PLAIN";
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
        } else if (value instanceof Font) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Font.class);
        }
    }

    private void handleAsString(String str) {
        String[] parts = str.split("-");
        if (parts.length != 3) {
            throw illegalValue(str, Font.class);
        }

        String family = parts[0];
        int style = resolveStyle(str, parts[1]);
        int size = parseSize(str, parts[2]);

        super.setValueInternal(new Font(family, style, size));
    }

    private void handleAsList(List list) {
        if (list.size() != 3) {
            throw illegalValue(list, Font.class);
        }

        String family = String.valueOf(list.get(0));
        int style = resolveStyle(list, String.valueOf(list.get(1)));
        int size = parseSize(list, String.valueOf(list.get(2)));

        super.setValueInternal(new Font(family, style, size));
    }

    private void handleAsMap(Map map) {
        String family = getMapValue(map, "family", "");
        String style = getMapValue(map, "style", "");
        String size = getMapValue(map, "size", "");
        super.setValueInternal(new Font(family, resolveStyle(map, style), parseSize(map, size)));
    }

    private String getMapValue(Map map, String key, String defaultValue) {
        Object val = map.get(key);
        if (null == val) val = map.get(String.valueOf(key.charAt(0)));
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return String.valueOf(val).trim();
        }
        throw illegalValue(map, Font.class);
    }

    private int parseSize(Object source, String str) {
        int size;
        try {
            size = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            throw illegalValue(source, Font.class);
        }
        return size;
    }

    private int resolveStyle(Object source, String str) {
        if ("PLAIN".equals(str.toUpperCase())) {
            return Font.PLAIN;
        } else if ("BOLD".equals(str.toUpperCase())) {
            return Font.BOLD;
        } else if ("ITALIC".equals(str.toUpperCase())) {
            return Font.ITALIC;
        } else if ("BOLDITALIC".equals(str.toUpperCase())) {
            return Font.BOLD | Font.ITALIC;
        }
        throw illegalValue(source, Font.class);
    }
}
