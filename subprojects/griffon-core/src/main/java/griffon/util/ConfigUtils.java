/*
 * Copyright 2011-2013 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static griffon.util.TypeUtils.*;
import static java.util.Collections.unmodifiableSortedSet;
import static java.util.Objects.requireNonNull;

/**
 * Utility class for reading applicationConfiguration properties.
 *
 * @author Andres Almiray
 */
public final class ConfigUtils {
    protected static final String ERROR_CONFIG_NULL = "Argument 'config' cannot be null";
    protected static final String ERROR_KEY_BLANK = "Argument 'key' cannot be blank";

    private ConfigUtils() {
        // prevent instantiation
    }

    /**
     * Returns true if there's a on-null value for the specified key.
     *
     * @param config the applicationConfiguration object to be searched upon
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
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull Map config, @Nonnull String key, @Nullable T defaultValue) {
        requireNonNull(config, "Argument 'config' cannot be null");
        requireNonBlank(key, ERROR_KEY_BLANK);

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
        return value != null ? (T) value : defaultValue;
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull ResourceBundle config, @Nonnull String key, @Nullable T defaultValue) {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);

        String[] keys = key.split("\\.");

        if (config.containsKey(key)) {
            return (T) config.getObject(key);
        }

        if (keys.length == 1) {
            Object node = config.getObject(keys[0]);
            return node != null ? (T) node : defaultValue;
        }

        Object node = config.getObject(keys[0]);
        if (!(node instanceof Map)) {
            return defaultValue;
        }

        Map map = (Map) node;
        for (int i = 1; i < keys.length - 1; i++) {
            if (map != null) {
                node = map.get(keys[i]);
                if (node instanceof Map) {
                    map = (Map) node;
                } else {
                    return defaultValue;
                }
            } else {
                return defaultValue;
            }
        }
        if (map == null) return defaultValue;
        Object value = map.get(keys[keys.length - 1]);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull Map config, @Nonnull String key) throws MissingResourceException {
        requireNonNull(config, "Argument 'config' cannot be null");
        requireNonBlank(key, ERROR_KEY_BLANK);
        String type = config.getClass().getName();

        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            if (config != null) {
                Object node = config.get(keys[i]);
                if (node instanceof Map) {
                    config = (Map) node;
                } else {
                    throw missingResource(type, key);
                }
            } else {
                throw missingResource(type, key);
            }
        }
        if (config == null) {
            throw missingResource(type, key);
        }
        Object value = config.get(keys[keys.length - 1]);
        if (value != null) {
            return (T) value;
        }
        throw missingResource(type, key);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull ResourceBundle config, @Nonnull String key) throws MissingResourceException {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);
        String type = config.getClass().getName();

        String[] keys = key.split("\\.");

        if (config.containsKey(key)) {
            return (T) config.getObject(key);
        }

        if (keys.length == 1) {
            Object node = config.getObject(keys[0]);
            if (node != null) {
                return (T) node;
            }
            throw missingResource(type, key);
        }

        Object node = config.getObject(keys[0]);
        if (!(node instanceof Map)) {
            throw missingResource(type, key);
        }

        Map map = (Map) node;
        for (int i = 1; i < keys.length - 1; i++) {
            if (map != null) {
                node = map.get(keys[i]);
                if (node instanceof Map) {
                    map = (Map) node;
                } else {
                    throw missingResource(type, key);
                }
            } else {
                throw missingResource(type, key);
            }
        }
        if (map == null) {
            throw missingResource(type, key);
        }

        Object value = map.get(keys[keys.length - 1]);
        if (value != null) {
            return (T) value;
        }
        throw missingResource(type, key);
    }

    private static MissingResourceException missingResource(String classname, String key) throws MissingResourceException {
        return new MissingResourceException("Can't find resource for bundle " + classname + ", key " + key, classname, key);
    }

    /**
     * Returns the value for the specified key coerced to a boolean.
     *
     * @param config the applicationConfiguration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code false} if no match.
     */
    public static boolean getConfigValueAsBoolean(Map config, String key) {
        return getConfigValueAsBoolean(config, key, false);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the applicationConfiguration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static boolean getConfigValueAsBoolean(Map config, String key, boolean defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToBoolean(value);
    }

    /**
     * Returns the value for the specified key coerced to an int.
     *
     * @param config the applicationConfiguration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code 0} if no match.
     */
    public static int getConfigValueAsInt(Map config, String key) {
        return getConfigValueAsInt(config, key, 0);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the applicationConfiguration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static int getConfigValueAsInt(Map config, String key, int defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToInt(value);
    }

    /**
     * Returns the value for the specified key coerced to a long.
     *
     * @param config the applicationConfiguration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code 0L} if no match.
     */
    public static long getConfigValueAsLong(Map config, String key) {
        return getConfigValueAsLong(config, key, 0L);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the applicationConfiguration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static long getConfigValueAsLong(Map config, String key, long defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToLong(value);
    }

    /**
     * Returns the value for the specified key coerced to a double.
     *
     * @param config the applicationConfiguration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code 0d} if no match.
     */
    public static double getConfigValueAsDouble(Map config, String key) {
        return getConfigValueAsDouble(config, key, 0d);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the applicationConfiguration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static double getConfigValueAsDouble(Map config, String key, double defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToDouble(value);
    }

    /**
     * Returns the value for the specified key coerced to a float.
     *
     * @param config the applicationConfiguration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code 0f} if no match.
     */
    public static float getConfigValueAsFloat(Map config, String key) {
        return getConfigValueAsFloat(config, key, 0f);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the applicationConfiguration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static float getConfigValueAsFloat(Map config, String key, float defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToFloat(value);
    }

    /**
     * Returns the value for the specified key coerced to a Number.
     *
     * @param config the applicationConfiguration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code null} if no match.
     */
    public static Number getConfigValueAsNumber(Map config, String key) {
        return getConfigValueAsNumber(config, key, null);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the applicationConfiguration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static Number getConfigValueAsNumber(Map config, String key, Number defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToNumber(value);
    }

    /**
     * Returns the value for the specified key converted to a String.
     *
     * @param config the applicationConfiguration object to be searched upon
     * @param key    the key to be searched
     * @return the value of the key. Returns {@code ""} if no match.
     */
    public static String getConfigValueAsString(Map config, String key) {
        return getConfigValueAsString(config, key, "");
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the applicationConfiguration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     * @return the value of the key or the default value if no match is found
     */
    public static String getConfigValueAsString(Map config, String key, String defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return value != null ? String.valueOf(value) : null;
    }

    // the following taken from SpringFramework::org.springframework.util.StringUtils

    /**
     * Extract the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -> "txt".
     *
     * @param path the file path (may be <code>null</code>)
     * @return the extracted filename extension, or <code>null</code> if none
     */
    public static String getFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int extIndex = path.lastIndexOf(".");
        if (extIndex == -1) {
            return null;
        }
        int folderIndex = path.lastIndexOf("/");
        if (folderIndex > extIndex) {
            return null;
        }
        return path.substring(extIndex + 1);
    }

    /**
     * Strip the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -> "mypath/myfile".
     *
     * @param path the file path (may be <code>null</code>)
     * @return the path with stripped filename extension,
     *         or <code>null</code> if none
     */
    public static String stripFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int extIndex = path.lastIndexOf(".");
        if (extIndex == -1) {
            return path;
        }
        int folderIndex = path.lastIndexOf("/");
        if (folderIndex > extIndex) {
            return path;
        }
        return path.substring(0, extIndex);
    }

    @Nonnull
    public static Set<String> explodeKeys(@Nonnull Collection<String> keys) {
        requireNonNull(keys, "Argument 'keys' cannot be null");

        SortedSet<String> exploded = new TreeSet<>();
        for (String key : keys) {
            String[] subkeys = key.split("\\.");
            StringBuilder b = new StringBuilder(subkeys[0]);
            String k = b.toString();
            exploded.add(k);

            for (int i = 1; i < subkeys.length; i++) {
                b.append(".").append(subkeys[i]);
                k = b.toString();

                exploded.add(k);

            }
        }

        return unmodifiableSortedSet(exploded);
    }
}
