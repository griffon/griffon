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
public class GradientPaintPropertyEditor extends AbstractPropertyEditor {
    public String getAsText() {
        if (null == getValue()) return null;
        GradientPaint p = (GradientPaint) getValue();
        return new StringBuilder()
            .append(p.getPoint1().getX())
            .append(", ")
            .append(p.getPoint1().getY())
            .append(", ")
            .append(ColorPropertyEditor.format(p.getColor1()))
            .append(", ")
            .append(p.getPoint2().getX())
            .append(", ")
            .append(p.getPoint2().getY())
            .append(", ")
            .append(ColorPropertyEditor.format(p.getColor2()))
            .append(", ")
            .append(p.isCyclic())
            .toString();
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
        } else if (value instanceof GradientPaint) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, GradientPaint.class);
        }
    }

    private void handleAsString(String str) {
        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        Color c1 = Color.WHITE;
        Color c2 = Color.BLACK;
        boolean cyclic = false;

        if (str.contains("|")) {
            String[] parts = str.split("\\|");
            switch(parts.length) {
                case 4:
                    cyclic = parseBoolean(parts[3]);
                case 3:
                    ColorPropertyEditor colorEditor = new ColorPropertyEditor();
                    // point1
                    String[] p1 = parts[0].split(",");
                    if (p1.length != 2) {
                        throw illegalValue(str, GradientPaint.class);
                    }
                    x1 = parseValue(p1[0]);
                    y1 = parseValue(p1[1]);

                    // point2
                    String[] p2 = parts[1].split(",");
                    if (p2.length != 2) {
                        throw illegalValue(str, GradientPaint.class);
                    }
                    x2 = parseValue(p2[0]);
                    y2 = parseValue(p2[1]);

                    String[] colors = parts[2].split(",");
                    try {
                        colorEditor.setAsText(colors[0]);
                        c1 = (Color) colorEditor.getValue();
                    } catch (Exception e) {
                        throw illegalValue(colors[0], GradientPaint.class);
                    }

                    try {
                        colorEditor.setAsText(colors[1]);
                        c2 = (Color) colorEditor.getValue();
                    } catch (Exception e) {
                        throw illegalValue(colors[1], GradientPaint.class);
                    }
                    super.setValueInternal(new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic));
                    break;
                default:
                    throw illegalValue(str, GradientPaint.class);
            }
        } else {
            String[] parts = str.split(",");
            switch (parts.length) {
                case 7:
                    cyclic = parseBoolean(parts[6]);
                case 6:
                    ColorPropertyEditor colorEditor = new ColorPropertyEditor();
                    x1 = parseValue(parts[0]);
                    y1 = parseValue(parts[1]);
                    x2 = parseValue(parts[3]);
                    y2 = parseValue(parts[4]);
                    try {
                        colorEditor.setAsText(parts[2]);
                        c1 = (Color) colorEditor.getValue();
                    } catch (Exception e) {
                        throw illegalValue(parts[2], GradientPaint.class);
                    }
                    try {
                        colorEditor.setAsText(parts[5]);
                        c2 = (Color) colorEditor.getValue();
                    } catch (Exception e) {
                        throw illegalValue(parts[5], GradientPaint.class);
                    }
                    super.setValueInternal(new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic));
                    break;
                default:
                    throw illegalValue(str, GradientPaint.class);
            }
        }

    }

    private void handleAsList(List list) {
        float x1 = 0;
        float y1 = 0;
        float x2 = 0;
        float y2 = 0;
        Color c1 = Color.WHITE;
        Color c2 = Color.BLACK;
        boolean cyclic = false;
        switch (list.size()) {
            case 7:
                cyclic = parseBoolean(String.valueOf(list.get(6)));
            case 6:
                ColorPropertyEditor colorEditor = new ColorPropertyEditor();
                x1 = parseValue(list.get(0));
                y1 = parseValue(list.get(1));
                x2 = parseValue(list.get(3));
                y2 = parseValue(list.get(4));
                try {
                    colorEditor.setValueInternal(list.get(2));
                    c1 = (Color) colorEditor.getValue();
                } catch (Exception e) {
                    throw illegalValue(list.get(2), GradientPaint.class);
                }
                try {
                    colorEditor.setValueInternal(list.get(5));
                    c2 = (Color) colorEditor.getValue();
                } catch (Exception e) {
                    throw illegalValue(list.get(5), GradientPaint.class);
                }
                super.setValueInternal(new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic));
                break;
            default:
                throw illegalValue(list, GradientPaint.class);
        }
    }

    private void handleAsMap(Map map) {
        float x1 = (Float) getMapValue(map, "x1", 0f);
        float y1 = (Float) getMapValue(map, "y1", 0f);
        float x2 = (Float) getMapValue(map, "x2", 0f);
        float y2 = (Float) getMapValue(map, "y2", 0f);
        Color c1 = Color.WHITE;
        Color c2 = Color.BLACK;
        boolean cyclic = false;

        ColorPropertyEditor colorEditor = new ColorPropertyEditor();
        Object colorValue = map.get("c1");
        try {
            if (null != colorValue) {
                colorEditor.setValueInternal(colorValue);
                c1 = (Color) colorEditor.getValue();
            } else {
                c1 = Color.WHITE;
            }
        } catch (Exception e) {
            throw illegalValue(colorValue, GradientPaint.class);
        }
        colorValue = map.get("c2");
        try {
            if (null != colorValue) {
                colorEditor.setValueInternal(colorValue);
                c2 = (Color) colorEditor.getValue();
            } else {
                c2 = Color.BLACK;
            }
        } catch (Exception e) {
            throw illegalValue(colorValue, GradientPaint.class);
        }
        Object cyclicValue = map.get("cyclic");
        if (null != cyclicValue) {
            cyclic = parseBoolean(String.valueOf(cyclicValue));
        }

        super.setValueInternal(new GradientPaint(x1, y1, c1, x2, y2, c2, cyclic));
    }

    private float parse(String val) {
        try {
            return Float.parseFloat(val.trim());
        } catch (NumberFormatException e) {
            throw illegalValue(val, GradientPaint.class, e);
        }
    }

    private boolean parseBoolean(String val) {
        try {
            return Boolean.parseBoolean(val.trim());
        } catch (Exception e) {
            throw illegalValue(val, GradientPaint.class, e);
        }
    }

    private float parseValue(Object value) {
        if (value instanceof CharSequence) {
            return parse(String.valueOf(value));
        } else if (value instanceof Number) {
            return parse((Number) value);
        }
        throw illegalValue(value, GradientPaint.class);
    }

    private float parse(Number val) {
        return val.floatValue();
    }

    private Object getMapValue(Map map, String key, Object defaultValue) {
        Object val = map.get(key);
        if (null == val) {
            return defaultValue;
        } else if (val instanceof CharSequence) {
            return parse(String.valueOf(val));
        } else if (val instanceof Number) {
            return parse((Number) val);
        }
        throw illegalValue(map, GradientPaint.class);
    }
}
