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

import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.cli.GriffonScriptRunner;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public class GriffonScriptRunnerHolder {
    private static GriffonScriptRunner griffonScriptRunner;
    private static String lastEnvironment;
    private static GantBinding gantBinding;

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
}
