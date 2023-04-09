/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package org.codehaus.griffon.runtime.core.env;

import griffon.core.env.Environment;
import griffon.core.env.Metadata;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public class EnvironmentProvider implements Provider<Environment> {
    @Inject
    private Metadata metadata;

    private static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    @Override
    public Environment get() {
        String envName = System.getProperty(Environment.KEY);
        if (metadata != null && isBlank(envName)) {
            envName = metadata.getEnvironment();
        }

        if (isBlank(envName)) {
            return Environment.DEVELOPMENT;
        }

        Environment env = Environment.resolveEnvironment(envName);
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
}
