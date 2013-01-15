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

import groovy.util.ConfigObject;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static griffon.util.ApplicationHolder.getApplication;
import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * Utility class for reading configuration properties.
 *
 * @author Andres Almiray
 */
public final class ConfigUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigUtils.class);
    private static final String PROPERTIES_SUFFIX = "properties";
    private static final String GROOVY_SUFFIX = "groovy";

    private ConfigUtils() {
        // prevent instantiation
    }

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

    /**
     * Creates a new {@code ConfigReader} instance configured with default conditional blocks.<br/>
     * The following list enumerates the conditional blocks that get registered automatically:
     * <ul>
     * <li><strong>environments</strong> = <tt>Environment.getCurrent().getName()</tt></strong></li>
     * <li><strong>projects</strong> = <tt>Metadata.getCurrent().getApplicationName()</tt></strong></li>
     * <li><strong>platforms</strong> = <tt>GriffonApplicationUtils.getFullPlatform()</tt></strong></li>
     * </ul>
     *
     * @return a newly instantiated {@code ConfigReader}.
     * @since 1.1.0
     */
    public static ConfigReader createConfigReader() {
        ConfigReader configReader = new ConfigReader();
        configReader.registerConditionalBlock("environments", Environment.getCurrent().getName());
        configReader.registerConditionalBlock("projects", Metadata.getCurrent().getApplicationName());
        configReader.registerConditionalBlock("platforms", GriffonApplicationUtils.getPlatform());
        return configReader;
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file as fallback.<br/>
     * The name of the script matches the name of the file.
     *
     * @param configFileName the configuration file
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfig(String configFileName) {
        return loadConfig(createConfigReader(), safeLoadClass(configFileName), configFileName);
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file as fallback.<br/>
     * The alternate properties file matches the simple name of the script.
     *
     * @param configClass the script's class, may be null
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfig(Class configClass) {
        return loadConfig(createConfigReader(), configClass, configClass.getSimpleName());
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file as fallback.
     *
     * @param configClass    the script's class, may be null
     * @param configFileName the alternate configuration file
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfig(Class configClass, String configFileName) {
        return loadConfig(createConfigReader(), configClass, configFileName);
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file as fallback.
     *
     * @param configReader   a ConfigReader instance already configured
     * @param configClass    the script's class, may be null
     * @param configFileName the alternate configuration file
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfig(ConfigReader configReader, Class configClass, String configFileName) {
        ConfigObject config = new ConfigObject();
        try {
            if (configClass != null) {
                config.merge(configReader.parse(configClass));
            }
            config.merge(loadConfigFile(configReader, configFileName));
        } catch (FileNotFoundException fnfe) {
            // ignore
        } catch (Exception x) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Cannot read configuration [class: " + configClass + ", file: " + configFileName + "]", sanitize(x));
            }
        }
        return config;
    }

    private static ConfigObject loadConfigFile(ConfigReader configReader, String configFileName) throws IOException {
        ConfigObject config = new ConfigObject();

        if (isBlank(configFileName)) return config;

        String fileNameExtension = getFilenameExtension(configFileName);
        if (isBlank(fileNameExtension)) {
            configFileName += "." + PROPERTIES_SUFFIX;
            fileNameExtension = PROPERTIES_SUFFIX;
        }

        if (PROPERTIES_SUFFIX.equals(fileNameExtension)) {
            InputStream is = null;
            if (configFileName.startsWith("/")) {
                is = new FileInputStream(configFileName);
            } else {
                is = ApplicationClassLoader.get().getResourceAsStream(configFileName);
            }
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                config = configReader.parse(p);
                is.close();
            }
        } else if (GROOVY_SUFFIX.equals(fileNameExtension)) {
            InputStream is = null;
            if (configFileName.startsWith("/")) {
                is = new FileInputStream(configFileName);
            } else {
                is = ApplicationClassLoader.get().getResourceAsStream(configFileName);
            }
            if (is != null) {
                String scriptText = IOGroovyMethods.getText(is);
                if (!isBlank(scriptText)) {
                    config = configReader.parse(scriptText);
                }
            }
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("Invalid configuration [file: " + configFileName + "]. Skipping");
            }
        }

        return config;
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file. The script and file names
     * are Locale aware.<p>
     * The name of the script matches the name of the file.<br/>
     * The following suffixes will be used besides the base names for script and file
     * <ul>
     * <li>locale.getLanguage()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant()</li>
     * </ul>
     *
     * @param baseConfigFileName the configuration file
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfigWithI18n(String baseConfigFileName) {
        return loadConfigWithI18n(getApplication().getLocale(), createConfigReader(), safeLoadClass(baseConfigFileName), baseConfigFileName);
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file. The script and file names
     * are Locale aware.<p>
     * The alternate properties file matches the simple name of the script.<br/>
     * The following suffixes will be used besides the base names for script and file
     * <ul>
     * <li>locale.getLanguage()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant()</li>
     * </ul>
     *
     * @param baseConfigClass the script's class
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfigWithI18n(Class baseConfigClass) {
        return loadConfigWithI18n(getApplication().getLocale(), createConfigReader(), baseConfigClass, baseConfigClass.getSimpleName());
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file. The script and file names
     * are Locale aware.<p>
     * The following suffixes will be used besides the base names for script and file
     * <ul>
     * <li>locale.getLanguage()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant()</li>
     * </ul>
     *
     * @param baseConfigClass    the script's class, may be null
     * @param baseConfigFileName the alternate configuration file
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfigWithI18n(Class baseConfigClass, String baseConfigFileName) {
        return loadConfigWithI18n(getApplication().getLocale(), createConfigReader(), baseConfigClass, baseConfigFileName);
    }

    /**
     * Loads configuration settings defined in a Groovy script and a properties file. The script and file names
     * are Locale aware.<p>
     * The following suffixes will be used besides the base names for script and file
     * <ul>
     * <li>locale.getLanguage()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry()</li>
     * <li>locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant()</li>
     * </ul>
     *
     * @param locale             the locale to use
     * @param configReader       a ConfigReader instance already configured
     * @param baseConfigClass    the script's class, may be null
     * @param baseConfigFileName the alternate configuration file
     * @return a merged configuration between the script and the alternate file. The file has precedence over the script.
     * @since 1.1.0
     */
    public static ConfigObject loadConfigWithI18n(Locale locale, ConfigReader configReader, Class baseConfigClass, String baseConfigFileName) {
        ConfigObject config = loadConfig(configReader, baseConfigClass, baseConfigFileName);
        String[] combinations = {
            locale.getLanguage(),
            locale.getLanguage() + "_" + locale.getCountry(),
            locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant()
        };

        String baseClassName = baseConfigClass != null ? baseConfigClass.getName() : null;
        String fileExtension = !isBlank(baseConfigFileName) ? getFilenameExtension(baseConfigFileName) : null;
        for (String suffix : combinations) {
            if (isBlank(suffix) || suffix.endsWith("_")) continue;
            if (baseClassName != null) {
                Class configClass = safeLoadClass(baseClassName + "_" + suffix);
                if (configClass != null) config.merge(configReader.parse(configClass));
            }

            if (fileExtension == null) continue;
            String configFileName = stripFilenameExtension(baseConfigFileName) + "_" + suffix + "." + fileExtension;
            try {
                config.merge(loadConfigFile(configReader, configFileName));
            } catch (FileNotFoundException fne) {
                // ignore
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot read configuration [file: " + configFileName + "]", sanitize(e));
                }
            }
        }

        return config;
    }

    public static Class loadClass(String className) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null;

        ClassLoader cl = ApplicationClassLoader.get();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        cl = Thread.currentThread().getContextClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        if (cnfe != null) throw cnfe;
        return null;
    }

    public static Class safeLoadClass(String className) {
        try {
            return loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
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
}
