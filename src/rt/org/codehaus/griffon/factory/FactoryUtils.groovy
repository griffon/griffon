/*
 * Copyright 2010 the original author or authors.
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
package org.codehaus.griffon.factory

/**
 *
 * @author Andres Almiray
 */
final class FactoryUtils {
    private FactoryUtils() {}

    static double toDouble(value, double defaultValue = 0.0d) {
        if(value instanceof Number) {
            return value.doubleValue()
        } else {
            try {
                return Double.parseDouble(String.valueOf(value))
            } catch(NumberFormatException nfe) {
                return defaultValue
            }
        }
    }

    static float toFloat(value, float defaultValue = 0.0f) {
        if(value instanceof Number) {
            return value.floatValue()
        } else {
            try {
                return Float.parseFloat(String.valueOf(value))
            } catch(NumberFormatException nfe) {
                return defaultValue
            }
        }
    }

    static int toInt(value, int defaultValue = 0i) {
        if(value instanceof Number) {
            return value.intValue()
        } else {
            try {
                return Integer.parseInt(String.valueOf(value))
            } catch(NumberFormatException nfe) {
                return defaultValue
            }
        }
    }

    static long toLong(value, long defaultValue = 0l) {
        if(value instanceof Number) {
            return value.longValue()
        } else {
            try {
                return Long.parseLong(String.valueOf(value))
            } catch(NumberFormatException nfe) {
                return defaultValue
            }
        }
    }

    static String parseSize(String size, List<String> sizes) {
        if(size in sizes) {
                return size +'x'+ size
        }
        return sizes[0] +'x'+ sizes[0]
    }
}
