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
package griffon.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static griffon.util.TypeUtils.castToBoolean;
import static griffon.util.TypeUtils.castToDouble;
import static griffon.util.TypeUtils.castToFloat;
import static griffon.util.TypeUtils.castToInt;
import static griffon.util.TypeUtils.castToLong;
import static griffon.util.TypeUtils.castToNumber;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

/**
 * Utility class for reading configuration properties.
 *
 * @author Andres Almiray
 */
public final class ConfigUtils {
    private static final String ERROR_CONFIG_NULL = "Argument 'config' must not be null";
    private static final String ERROR_KEY_BLANK = "Argument 'key' must not be blank";

    private ConfigUtils() {
        // prevent instantiation
    }

    /**
     * Converts a {@code ResourceBundle} instance into a {@code Properties} instance.
     *
     * @param resourceBundle the {@code ResourceBundle} to be converted. Must not be null.
     *
     * @return a newly created {@code Properties} with all key/value pairs from the given {@code ResourceBundle}.
     *
     * @since 2.10.0
     */
    @Nonnull
    public static Properties toProperties(@Nonnull ResourceBundle resourceBundle) {
        requireNonNull(resourceBundle, "Argument 'resourceBundle' must not be null");

        Properties properties = new Properties();
        for (String key : resourceBundle.keySet()) {
            properties.put(key, resourceBundle.getObject(key));
        }

        return properties;
    }

    /**
     * Returns true if there's a non-null value for the specified key.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return true if there's a value for the specified key, false otherwise
     *
     * @since 2.2.0
     */
    @SuppressWarnings("unchecked")
    public static boolean containsKey(@Nonnull Map<String, Object> config, @Nonnull String key) {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);

        if (config.containsKey(key)) {
            return true;
        }

        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            Object node = config.get(keys[i]);
            if (node instanceof Map) {
                config = (Map<String, Object>) node;
            } else {
                return false;
            }
        }
        return config.containsKey(keys[keys.length - 1]);
    }

    /**
     * Returns true if there's a non-null value for the specified key.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return true if there's a value for the specified key, false otherwise
     *
     * @since 2.2.0
     */
    @SuppressWarnings("unchecked")
    public static boolean containsKey(@Nonnull ResourceBundle config, @Nonnull String key) {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);

        String[] keys = key.split("\\.");

        try {
            if (config.containsKey(key)) {
                return true;
            }
        } catch (MissingResourceException mre) {
            // OK
        }

        if (keys.length == 1) {
            return config.containsKey(keys[0]);
        }

        Object node = config.getObject(keys[0]);
        if (!(node instanceof Map)) {
            return false;
        }

        Map<String, Object> map = (Map) node;
        for (int i = 1; i < keys.length - 1; i++) {
            node = map.get(keys[i]);
            if (node instanceof Map) {
                map = (Map) node;
            } else {
                return false;
            }
        }
        return map.containsKey(keys[keys.length - 1]);
    }

    /**
     * Returns true if there's a non-null value for the specified key.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return true if there's a value for the specified key, false otherwise
     */
    @SuppressWarnings("unchecked")
    public static boolean isValueDefined(@Nonnull Map<String, Object> config, @Nonnull String key) {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);

        if (config.containsKey(key)) {
            return true;
        }

        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            Object node = config.get(keys[i]);
            if (node instanceof Map) {
                config = (Map<String, Object>) node;
            } else {
                return false;
            }
        }
        return config.get(keys[keys.length - 1]) != null;
    }

    /**
     * Returns true if there's a non-null value for the specified key.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return true if there's a value for the specified key, false otherwise
     */
    @SuppressWarnings("unchecked")
    public static boolean isValueDefined(@Nonnull ResourceBundle config, @Nonnull String key) {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);

        String[] keys = key.split("\\.");

        try {
            Object value = config.getObject(key);
            if (value != null) {
                return true;
            }
        } catch (MissingResourceException mre) {
            // OK
        }

        if (keys.length == 1) {
            try {
                Object node = config.getObject(keys[0]);
                return node != null;
            } catch (MissingResourceException mre) {
                return false;
            }
        }

        Object node = config.getObject(keys[0]);
        if (!(node instanceof Map)) {
            return false;
        }

        Map<String, Object> map = (Map) node;
        for (int i = 1; i < keys.length - 1; i++) {
            node = map.get(keys[i]);
            if (node instanceof Map) {
                map = (Map) node;
            } else {
                return false;
            }
        }
        return map.get(keys[keys.length - 1]) != null;
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull Map<String, Object> config, @Nonnull String key, @Nullable T defaultValue) {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);

        if (config.containsKey(key)) {
            return (T) config.get(key);
        }

        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            Object node = config.get(keys[i]);
            if (node instanceof Map) {
                config = (Map) node;
            } else {
                return defaultValue;
            }
        }
        Object value = config.get(keys[keys.length - 1]);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull ResourceBundle config, @Nonnull String key, @Nullable T defaultValue) {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);

        String[] keys = key.split("\\.");

        try {
            Object value = config.getObject(key);
            if (value != null) {
                return (T) value;
            }
        } catch (MissingResourceException mre) {
            // OK
        }

        if (keys.length == 1) {
            Object node = config.getObject(keys[0]);
            return node != null ? (T) node : defaultValue;
        }

        Object node = config.getObject(keys[0]);
        if (!(node instanceof Map)) {
            return defaultValue;
        }

        Map<String, Object> map = (Map) node;
        for (int i = 1; i < keys.length - 1; i++) {
            node = map.get(keys[i]);
            if (node instanceof Map) {
                map = (Map) node;
            } else {
                return defaultValue;
            }
        }
        Object value = map.get(keys[keys.length - 1]);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull Map<String, Object> config, @Nonnull String key) throws MissingResourceException {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);
        String type = config.getClass().getName();

        if (config.containsKey(key)) {
            return (T) config.get(key);
        }

        String[] keys = key.split("\\.");
        for (int i = 0; i < keys.length - 1; i++) {
            Object node = config.get(keys[i]);
            if (node instanceof Map) {
                config = (Map) node;
            } else {
                throw missingResource(type, key);
            }
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
     *
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static <T> T getConfigValue(@Nonnull ResourceBundle config, @Nonnull String key) throws MissingResourceException {
        requireNonNull(config, ERROR_CONFIG_NULL);
        requireNonBlank(key, ERROR_KEY_BLANK);
        String type = config.getClass().getName();

        String[] keys = key.split("\\.");

        try {
            Object value = config.getObject(key);
            if (value != null) {
                return (T) value;
            }
        } catch (MissingResourceException mre) {
            // OK
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

        Map<String, Object> map = (Map<String, Object>) node;
        for (int i = 1; i < keys.length - 1; i++) {
            node = map.get(keys[i]);
            if (node instanceof Map) {
                map = (Map<String, Object>) node;
            } else {
                throw missingResource(type, key);
            }
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
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key. Returns {@code false} if no match.
     */
    public static boolean getConfigValueAsBoolean(@Nonnull Map<String, Object> config, @Nonnull String key) {
        return getConfigValueAsBoolean(config, key, false);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    public static boolean getConfigValueAsBoolean(@Nonnull Map<String, Object> config, @Nonnull String key, boolean defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToBoolean(value);
    }

    /**
     * Returns the value for the specified key coerced to an int.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key. Returns {@code 0} if no match.
     */
    public static int getConfigValueAsInt(@Nonnull Map<String, Object> config, @Nonnull String key) {
        return getConfigValueAsInt(config, key, 0);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    public static int getConfigValueAsInt(@Nonnull Map<String, Object> config, @Nonnull String key, int defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToInt(value);
    }

    /**
     * Returns the value for the specified key coerced to a long.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key. Returns {@code 0L} if no match.
     */
    public static long getConfigValueAsLong(@Nonnull Map<String, Object> config, @Nonnull String key) {
        return getConfigValueAsLong(config, key, 0L);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    public static long getConfigValueAsLong(@Nonnull Map<String, Object> config, @Nonnull String key, long defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToLong(value);
    }

    /**
     * Returns the value for the specified key coerced to a double.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key. Returns {@code 0d} if no match.
     */
    public static double getConfigValueAsDouble(@Nonnull Map<String, Object> config, @Nonnull String key) {
        return getConfigValueAsDouble(config, key, 0d);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    public static double getConfigValueAsDouble(@Nonnull Map<String, Object> config, @Nonnull String key, double defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToDouble(value);
    }

    /**
     * Returns the value for the specified key coerced to a float.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key. Returns {@code 0f} if no match.
     */
    public static float getConfigValueAsFloat(@Nonnull Map<String, Object> config, @Nonnull String key) {
        return getConfigValueAsFloat(config, key, 0f);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    public static float getConfigValueAsFloat(@Nonnull Map<String, Object> config, @Nonnull String key, float defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToFloat(value);
    }

    /**
     * Returns the value for the specified key coerced to a Number.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key. Returns {@code null} if no match.
     */
    @Nullable
    public static Number getConfigValueAsNumber(@Nonnull Map<String, Object> config, @Nonnull String key) {
        return getConfigValueAsNumber(config, key, null);
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    public static Number getConfigValueAsNumber(@Nonnull Map<String, Object> config, @Nonnull String key, @Nullable Number defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return castToNumber(value);
    }

    /**
     * Returns the value for the specified key converted to a String.
     *
     * @param config the configuration object to be searched upon
     * @param key    the key to be searched
     *
     * @return the value of the key. Returns {@code ""} if no match.
     */
    @Nullable
    public static String getConfigValueAsString(@Nonnull Map<String, Object> config, @Nonnull String key) {
        return getConfigValueAsString(config, key, "");
    }

    /**
     * Returns the value for the specified key with an optional default value if no match is found.
     *
     * @param config       the configuration object to be searched upon
     * @param key          the key to be searched
     * @param defaultValue the value to send back if no match is found
     *
     * @return the value of the key or the default value if no match is found
     */
    @Nullable
    public static String getConfigValueAsString(@Nonnull Map<String, Object> config, @Nonnull String key, @Nullable String defaultValue) {
        Object value = getConfigValue(config, key, defaultValue);
        return value != null ? String.valueOf(value) : null;
    }

    // the following taken from SpringFramework::org.springframework.util.StringUtils

    /**
     * Extract the filename extension from the given path,
     * e.g. "mypath/myfile.txt" -> "txt".
     *
     * @param path the file path (may be <code>null</code>)
     *
     * @return the extracted filename extension, or <code>null</code> if none
     */
    public static String getFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int extIndex = path.lastIndexOf('.');
        if (extIndex == -1) {
            return null;
        }
        int folderIndex = path.lastIndexOf('/');
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
     *
     * @return the path with stripped filename extension,
     * or <code>null</code> if none
     */
    public static String stripFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int extIndex = path.lastIndexOf('.');
        if (extIndex == -1) {
            return path;
        }
        int folderIndex = path.lastIndexOf('/');
        if (folderIndex > extIndex) {
            return path;
        }
        return path.substring(0, extIndex);
    }

    @Nonnull
    public static Set<String> collectKeys(@Nonnull Map<String, Object> map) {
        requireNonNull(map, "Argument 'map' must not be null");

        Set<String> keys = new LinkedHashSet<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            doCollectKeys(key, value, keys);
        }

        return unmodifiableSet(keys);
    }

    @SuppressWarnings("unchecked")
    private static void doCollectKeys(String key, Object value, Set<String> keys) {
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                doCollectKeys(key + "." + entry.getKey(), entry.getValue(), keys);
            }
        } else {
            keys.add(key);
        }
    }
}
