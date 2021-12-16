/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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


/**
 * An enum that represents the current running mode.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public enum RunMode {
    STANDALONE, WEBSTART, APPLET, CUSTOM;
    /**
     * Constant used to resolve the runMode via System.getProperty(RunMode.KEY)
     */
    public static final String KEY = "griffon.runmode";

    private static final String STANDALONE_RUNMODE_SHORT_NAME = "standalone";
    private static final String WEBSTART_RUNMODE_SHORT_NAME = "webstart";
    private static final String APPLET_RUNMODE_SHORT_NAME = "applet";

    private static final Map<String, String> MODE_NAME_MAPPINGS = new LinkedHashMap<>();

    static {
        MODE_NAME_MAPPINGS.put(STANDALONE_RUNMODE_SHORT_NAME, RunMode.STANDALONE.getName());
        MODE_NAME_MAPPINGS.put(WEBSTART_RUNMODE_SHORT_NAME, RunMode.WEBSTART.getName());
        MODE_NAME_MAPPINGS.put(APPLET_RUNMODE_SHORT_NAME, RunMode.APPLET.getName());
    }

    private String name;

    /**
     * @return Return true if the running mode has been set as a System property
     */
    public static boolean isSystemSet() {
        return System.getProperty(KEY) != null;
    }

    /**
     * Returns the running mode for the given short name
     *
     * @param shortName The short name
     * @return The RunMode or null if not known
     */
    @Nullable
    public static RunMode resolveRunMode(@Nullable String shortName) {
        final String modeName = MODE_NAME_MAPPINGS.get(shortName);
        if (modeName != null) {
            return RunMode.valueOf(modeName.toUpperCase());
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * @return The name of the running mode
     */
    @Nonnull
    public String getName() {
        if (this != CUSTOM || isBlank(name)) {
            return this.toString().toLowerCase(Locale.getDefault());
        }
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }
}
