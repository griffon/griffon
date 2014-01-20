/*
 * Copyright 2010-2014 the original author or authors.
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

package griffon.core.env;

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

    /**
     * Constants that indicates whether this GriffonApplication is running in the default running mode
     */
    public static final String DEFAULT = "griffon.runmode.default";
    private static final String STANDALONE_RUNMODE_SHORT_NAME = "standalone";
    private static final String WEBSTART_RUNMODE_SHORT_NAME = "webstart";
    private static final String APPLET_RUNMODE_SHORT_NAME = "applet";

    private static final Map<String, String> MODE_NAME_MAPPINGS = new LinkedHashMap<String, String>() {{
        put(STANDALONE_RUNMODE_SHORT_NAME, RunMode.STANDALONE.getName());
        put(WEBSTART_RUNMODE_SHORT_NAME, RunMode.WEBSTART.getName());
        put(APPLET_RUNMODE_SHORT_NAME, RunMode.APPLET.getName());
    }

        private static final long serialVersionUID = -1551704296210703535L;
    };

    /**
     * Returns the current RunMode which is typically either STANDALONE, WEBSTART or APPLET.
     * For custom running modes CUSTOM type is returned.
     *
     * @return The current runMode.
     */
    public static RunMode getCurrent() {
        String modeName = System.getProperty(RunMode.KEY);

        if (isBlank(modeName)) {
            return STANDALONE;
        } else {
            RunMode mode = getRunMode(modeName);
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
    public static RunMode getRunMode(String shortName) {
        final String modeName = MODE_NAME_MAPPINGS.get(shortName);
        if (modeName != null) {
            return RunMode.valueOf(modeName.toUpperCase());
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    private String name;

    /**
     * @return The name of the running mode
     */
    public String getName() {
        if (name == null) {
            return this.toString().toLowerCase(Locale.getDefault());
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
