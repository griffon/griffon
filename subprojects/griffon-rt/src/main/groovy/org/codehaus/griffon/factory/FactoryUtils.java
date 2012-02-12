/*
 * Copyright 2010-2012 the original author or authors.
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
package org.codehaus.griffon.factory;

import java.util.List;

/**
 * @author Andres Almiray
 */
public final class FactoryUtils {
    private FactoryUtils() {
    }

    public static double toDouble(Object value) {
        return toDouble(value, 0.0d);
    }

    public static double toDouble(Object value, double defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
    }

    public static float toFloat(Object value) {
        return toFloat(value, 0.0f);
    }

    public static float toFloat(Object value, float defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else {
            try {
                return Float.parseFloat(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
    }

    public static int toInt(Object value) {
        return toInt(value, 0);
    }

    public static int toInt(Object value, int defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
    }

    public static Long toLong(Object value) {
        return toLong(value, 0L);
    }

    public static Long toLong(Object value, Long defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                return defaultValue;
            }
        }
    }

    public static String parseSize(String size, List<String> sizes) {
        if (sizes.contains(size)) {
            return size + "x" + size;
        }
        return sizes.get(0) + "x" + sizes.get(0);
    }
}
