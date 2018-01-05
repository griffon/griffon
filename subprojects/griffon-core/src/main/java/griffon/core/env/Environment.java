/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import static java.util.Objects.requireNonNull;

/**
 * An enum that represents the current environment
 *
 * @author Andres Almiray
 * @since 2.0.0
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

    private static final String PRODUCTION_ENV_SHORT_NAME = "prod";
    private static final String DEVELOPMENT_ENVIRONMENT_SHORT_NAME = "dev";
    private static final String TEST_ENVIRONMENT_SHORT_NAME = "test";

    private static final Map<String, String> ENV_NAME_MAPPINGS = new LinkedHashMap<>();

    static {
        ENV_NAME_MAPPINGS.put(DEVELOPMENT_ENVIRONMENT_SHORT_NAME, Environment.DEVELOPMENT.getName());
        ENV_NAME_MAPPINGS.put(PRODUCTION_ENV_SHORT_NAME, Environment.PRODUCTION.getName());
        ENV_NAME_MAPPINGS.put(TEST_ENVIRONMENT_SHORT_NAME, Environment.TEST.getName());
    }

    private String name;

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
    public static Environment resolveEnvironment(@Nullable String shortName) {
        final String envName = ENV_NAME_MAPPINGS.get(shortName);
        if (envName != null) {
            return Environment.valueOf(envName.toUpperCase());
        }
        return null;
    }

    @Nonnull
    public static String getEnvironmentShortName(@Nonnull Environment env) {
        requireNonNull(env, "Argument 'env' must not be null");
        switch (env) {
            case DEVELOPMENT:
                return "dev";
            case TEST:
                return "test";
            case PRODUCTION:
                return "prod";
            default:
                return env.getName();
        }
    }

    /**
     * @return The name of the environment
     */
    @Nonnull
    public String getName() {
        if (this != CUSTOM || name == null) {
            return this.toString().toLowerCase(Locale.getDefault());
        }
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }
}
