/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package org.codehaus.griffon.runtime.core.env;

import griffon.core.env.Metadata;
import griffon.core.env.RunMode;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Andres Almiray
 * @since 2.2.0
 */
public class RunModeProvider implements Provider<RunMode> {
    @Inject
    private Metadata metadata;

    private static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    @Override
    public RunMode get() {
        String modeName = System.getProperty(RunMode.KEY);
        if (metadata != null && isBlank(modeName)) {
            modeName = metadata.getRunMode();
        }

        if (isBlank(modeName)) {
            return RunMode.STANDALONE;
        }

        RunMode mode = RunMode.resolveRunMode(modeName);
        if (mode == null) {
            try {
                mode = RunMode.valueOf(modeName.toUpperCase());
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        if (mode == null) {
            mode = RunMode.CUSTOM;
            mode.setName(modeName);
        }
        return mode;
    }
}
