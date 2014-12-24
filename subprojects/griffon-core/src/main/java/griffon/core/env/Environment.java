/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.core.env;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * An enum that represents the current environment
 *
 * @author Graeme Rocher (Grails 1.1)
 */
public enum Environment {
    /**
     * The development environment
     */
    DEVELOPMENT,

    /**
     * The production environment
     */
    PRODUCTION,

    /**
     * The test environment
     */
    TEST,

    /**
     * A custom environment
     */
    CUSTOM;

    /**
     * Constant used to resolve the environment via System.getProperty(Environment.KEY)
     */
    public static final String KEY = "griffon.env";

    /**
     * Constants that indicates whether this GriffonApplication is running in the default environment
     */
    public static final String DEFAULT = "griffon.env.default";
    private static final String PRODUCTION_ENV_SHORT_NAME = "prod";
    private static final String DEVELOPMENT_ENVIRONMENT_SHORT_NAME = "dev";
    private static final String TEST_ENVIRONMENT_SHORT_NAME = "test";

    private static final Map<String, String> ENV_NAME_MAPPINGS = new LinkedHashMap<String, String>() {{
        put(DEVELOPMENT_ENVIRONMENT_SHORT_NAME, Environment.DEVELOPMENT.getName());
        put(PRODUCTION_ENV_SHORT_NAME, Environment.PRODUCTION.getName());
        put(TEST_ENVIRONMENT_SHORT_NAME, Environment.TEST.getName());
    }

        private static final long serialVersionUID = -8447299990856630300L;
    };

    /**
     * Returns the current environment which is typically either DEVELOPMENT, PRODUCTION or TEST.
     * For custom environments CUSTOM type is returned.
     *
     * @return The current environment.
     */
    @Nonnull
    public static Environment getCurrent() {
        String envName = System.getProperty(Environment.KEY);
        Metadata metadata = Metadata.getCurrent();
        if (metadata != null && isBlank(envName)) {
            envName = metadata.getEnvironment();
        }

        if (isBlank(envName)) {
            return DEVELOPMENT;
        }

        Environment env = getEnvironment(envName);
        if (env == null) {
            try {
                env = Environment.valueOf(envName.toUpperCase());
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        if (env == null) {
            env = Environment.CUSTOM;
            env.setName(envName);
        }
        return env;
    }

    /**
     * @return Return true if the environment has been set as a System property
     */
    public static boolean isSystemSet() {
        return System.getProperty(KEY) != null;
    }

    /**
     * Returns the environment for the given short name
     *
     * @param shortName The short name
     * @return The Environment or null if not known
     */
    @Nullable
    public static Environment getEnvironment(@Nullable String shortName) {
        final String envName = ENV_NAME_MAPPINGS.get(shortName);
        if (envName != null) {
            return Environment.valueOf(envName.toUpperCase());
        }
        return null;
    }

    private String name;

    /**
     * @return The name of the environment
     */
    @Nonnull
    public String getName() {
        if (name == null) {
            return this.toString().toLowerCase(Locale.getDefault());
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    public static String getEnvironmentShortName() {
        switch(Environment.getCurrent()) {
            case DEVELOPMENT: return "dev";
            case TEST:        return "test";
            case PRODUCTION:  return "prod";
            default: return Environment.getCurrent().getName();
        }
    }
}
