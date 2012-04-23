/*
 * Copyright 2008-2011 the original author or authors.
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

package org.codehaus.griffon.cli.shell.command;

import gant.Gant;
import griffon.util.BuildSettings;
import griffon.util.BuildSettingsHolder;
import griffon.util.Environment;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.AbstractAction;
import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.cli.GriffonScriptRunner;
import org.codehaus.griffon.cli.ScriptExitException;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static griffon.util.GriffonExceptionHandler.sanitize;
import static org.codehaus.griffon.cli.GriffonScriptRunner.*;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.*;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "shell", name = "resolve-dependencies", description = "Resolves plugin dependencies")
public class ResolveDependenciesCommand extends AbstractAction {
    public Object doExecute() throws Exception {
        resolveDependencies(session);
        return null;
    }

    public static void resolveDependencies(CommandSession session) {
        GriffonScriptRunner runner = getGriffonScriptRunner();
        GantBinding binding = getGantBinding();
        if (binding == null) {
            binding = new GantBinding();
            setGantBinding(binding);
        }

        String scriptName = "_GriffonResolveDependencies";
        BuildSettings settings = BuildSettingsHolder.getSettings();
        File scriptFile = new File(settings.getGriffonHome(), "scripts/" + scriptName + ".groovy");
        System.setProperty(Environment.KEY, Environment.DEVELOPMENT.getName());
        settings.setGriffonEnv(Environment.DEVELOPMENT.getName());
        binding.setVariable(VAR_SCRIPT_ENV, Environment.DEVELOPMENT.getName());
        binding.setVariable(VAR_SCRIPT_NAME, scriptName);
        binding.setVariable(VAR_SCRIPT_FILE, scriptFile);
        binding.setVariable("runFrameworkDependencyResolution", Boolean.TRUE);
        binding.setVariable("runDependencyResolution", Boolean.TRUE);
        Gant gant = runner.createGantInstance(binding);
        List<String> targets = new ArrayList<String>();
        targets.add("resolveFrameworkDependencies");
        targets.add("resolveDependencies");

        try {
            runner.executeWithGantInstance(gant, binding, targets);
        } catch (ScriptExitException see) {
            // OK, we just got this exception because exit is not
            // allowed when running inside the interactive shell
        } catch (RuntimeException e) {
            sanitize(e);
            // bummer, we got a problem
            if (!(e instanceof ScriptExitException) && !(e.getCause() instanceof ScriptExitException)) {
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.RED).toString());
                e.printStackTrace(session.getConsole());
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
            }
        }
    }
}