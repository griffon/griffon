/*
 * Copyright 2011-2012 the original author or authors.
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

package griffon.util;

import groovy.util.ConfigObject;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.util.Map;

/**
 * Utility class for reading configuration properties.
 *
 * @author Andres Almiray
 */
public abstract class ConfigUtils {
    /**
     * Returns true if there's a on-null value for the specified key.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     * @return true if there's a value for the specified key, false otherwise
     */
    public static boolean isValueDefined(Map config, String key) {
        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            if (config != null) {
                config = (Map) config.get(keys[i]);
            } else {
                return false;
            }
        }
        if (config == null) return false;
        Object value = config.get(keys[keys.length - 1]);
        return value != null;
    }

    /**
     * Returns the value for the specified key.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. May return null
     */
    public static Object getConfigValue(Map config, String key) {
        return getConfigValue(config, key, null);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static Object getConfigValue(Map config, String key, Object defaultValue) {
        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            if (config != null) {
                Object node = config.get(keys[i]);
                if (node instanceof Map) {
                    config = (Map) node;
                } else {
                    return defaultValue;
                }
            } else {
                return defaultValue;
            }
        }
        if (config == null) return defaultValue;
        Object value = config.get(keys[keys.length - 1]);
        return value != null ? value : defaultValue;
    }

    /**
     * Returns the value for the specified key coerced to a boolean.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code false} if no match.
     */
    public static boolean getConfigValueAsBoolean(Map config, String key) {
        return getConfigValueAsBoolean(config, key, false);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static boolean getConfigValueAsBoolean(Map config, String key, boolean defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return DefaultTypeTransformation.castToBoolean(value);
    }

    /**
     * Returns the value for the specified key coerced to an int.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code 0} if no match.
     */
    public static int getConfigValueAsInt(Map config, String key) {
        return getConfigValueAsInt(config, key, 0);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static int getConfigValueAsInt(Map config, String key, int defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return DefaultTypeTransformation.castToNumber(value).intValue();
    }

    /**
     * Returns the value for the specified key converted to a String.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code ""} if no match.
     */
    public static String getConfigValueAsString(Map config, String key) {
        return getConfigValueAsString(config, key, "");
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static String getConfigValueAsString(Map config, String key, String defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return String.valueOf(value);
    }

    /**
     * Merges two maps using <tt>ConfigObject.merge()</tt>.
     *
     * @param defaults  configuration values available by default
     * @param overrides configuration values that override defaults
     * @return the result of merging both maps
     */
    public static Map merge(Map defaults, Map overrides) {
        ConfigObject configDefaults = new ConfigObject();
        ConfigObject configOverrides = new ConfigObject();
        configDefaults.putAll(defaults);
        configOverrides.putAll(overrides);
        return configDefaults.merge(configOverrides);
    }
}
