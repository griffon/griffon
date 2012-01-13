/*
 * Copyright 2008-2012 the original author or authors.
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

package org.codehaus.griffon.cli.shell;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.cli.CommandLineConstants;
import org.codehaus.griffon.cli.GriffonScriptRunner;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.sort;
import static org.apache.commons.lang.ArrayUtils.contains;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public class GriffonShellContext {
    private static GriffonScriptRunner griffonScriptRunner;
    private static String lastEnvironment;
    private static GantBinding gantBinding;
    private static Map<String, String> initialSystemProperties = new LinkedHashMap<String, String>();
    private static Map<String, String> systemProperties = new LinkedHashMap<String, String>();

    public static GriffonScriptRunner getGriffonScriptRunner() {
        return griffonScriptRunner;
    }

    public static void setGriffonScriptRunner(GriffonScriptRunner runner) {
        griffonScriptRunner = runner;
    }

    public static String getLastEnvironment() {
        return lastEnvironment;
    }

    public static void setLastEnvironment(String env) {
        lastEnvironment = env;
    }

    public static GantBinding getGantBinding() {
        return gantBinding;
    }

    public static void setGantBinding(GantBinding binding) {
        gantBinding = binding;
    }

    public static Map<String, String> getInitialSystemProperties() {
        return initialSystemProperties;
    }

    public static void setInitialSystemProperties(Map<String, String> properties) {
        initialSystemProperties.clear();
        initialSystemProperties.putAll(properties);
    }

    public static Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public static void setSystemProperties(Map<String, String> properties) {
        systemProperties.clear();
        addSystemProperties(properties);
    }

    public static void addSystemProperties(Map<String, String> properties) {
        String[] commandProperties = (String[]) ArrayUtils.clone(CommandLineConstants.KEYS);
        sort(commandProperties);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (contains(commandProperties, entry.getKey())) continue;
            systemProperties.put(entry.getKey(), entry.getValue());
        }
    }
}
