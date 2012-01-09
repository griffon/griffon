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

import gant.Gant;
import griffon.util.Environment;
import org.apache.felix.service.command.CommandSession;
import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.cli.GriffonScriptRunner;

import java.io.File;

import static org.codehaus.griffon.cli.GriffonScriptRunner.*;
import static org.codehaus.griffon.cli.shell.GriffonScriptRunnerHolder.*;

/**
 * @author Andres Almiray
 */
public class GantAwareAction extends AbstractGriffonShellCommand {
    private final File scriptFile;
    private final String scriptName;

    public GantAwareAction(File scriptFile) {
        this.scriptFile = scriptFile;
        this.scriptName = getScriptNameFromFile(scriptFile);
    }

    public String getScriptName() {
        return scriptName;
    }

    @Override
    protected Object doExecute(CommandSession session) throws Exception {
        GriffonScriptRunner runner = getGriffonScriptRunner();
        GantBinding binding = getGantBinding();
        if (binding == null) {
            binding = new GantBinding();
            setGantBinding(binding);
        }

        binding.setVariable("classpathSet", false);

        binding.setVariable(VAR_SCRIPT_ENV, System.getProperty(Environment.KEY));
        binding.setVariable(VAR_SCRIPT_NAME, scriptName);
        binding.setVariable(VAR_SCRIPT_FILE, scriptFile);
        Gant gant = runner.createGantInstance(binding);
        gant.loadScript(scriptFile);
        executeWithGantInstance(gant, binding);

        return null;
    }
}
