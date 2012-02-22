/*
 * Copyright 2008-2012 the original author or authors.
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
import org.codehaus.groovy.runtime.DateGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.codehaus.griffon.cli.CommandLineConstants.KEY_CLI_VERBOSE;
import static org.codehaus.griffon.cli.CommandLineConstants.KEY_NON_INTERACTIVE_DEFAULT_ANSWER;

/**
 * Methods optimized to Java for the BuildSettings class.
 *
 * @since 0.9.1
 */
public abstract class AbstractBuildSettings {
    /**
     * Used to cache results of certain expensive operations
     */
    protected Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
    /**
     * The settings stored in the project's BuildConfig.groovy file if there is one.
     */
    protected ConfigObject config = new ConfigObject();
    /**
     * The location where project-specific plugins are installed to.
     */
    protected File projectPluginsDir;

    protected boolean projectPluginsDirSet;

    /**
     * Flattened version of the ConfigObject for easy access from Java
     */
    @SuppressWarnings("rawtypes")
    protected Map flatConfig = Collections.emptyMap();

    abstract File getBaseDir();

    /**
     * Clears any locally cached values
     */
    void clearCache() {
        cache.clear();
    }

    public ConfigObject getConfig() {
        return config;
    }

    public void setConfig(ConfigObject config) {
        this.config = config;
    }

    public File getProjectPluginsDir() {
        return projectPluginsDir;
    }

    public void setProjectPluginsDir(File projectPluginsDir) {
        this.projectPluginsDir = projectPluginsDir;
        projectPluginsDirSet = true;
    }

    public boolean isDebugEnabled() {
        if (System.getProperty(KEY_CLI_VERBOSE) != null) return Boolean.getBoolean(KEY_CLI_VERBOSE);
        return DefaultTypeTransformation.castToBoolean(getConfig().flatten().get(KEY_CLI_VERBOSE));
    }

    public String getDefaultAnswerNonInteractive() {
        if (System.getProperty(KEY_NON_INTERACTIVE_DEFAULT_ANSWER) != null)
            return System.getProperty(KEY_NON_INTERACTIVE_DEFAULT_ANSWER);
        return ConfigUtils.getConfigValueAsString(getConfig(), KEY_NON_INTERACTIVE_DEFAULT_ANSWER, "");
    }

    public void debug(String msg) {
        if (isDebugEnabled()) {
            Date now = new Date();
            System.out.println("[" +
                    DateGroovyMethods.getDateString(now)
                    + " " +
                    DateGroovyMethods.getTimeString(now)
                    + "] " + msg);
        }
    }
}
