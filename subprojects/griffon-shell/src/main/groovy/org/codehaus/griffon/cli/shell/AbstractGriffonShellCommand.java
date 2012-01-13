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

import griffon.util.BuildSettingsHolder;
import griffon.util.Environment;
import griffon.util.GriffonUtil;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.service.command.CommandSession;
import org.codehaus.griffon.cli.GriffonScriptRunner;
import org.codehaus.griffon.cli.shell.support.CommandArguments;

import static griffon.util.GriffonNameUtils.getShortName;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.*;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public abstract class AbstractGriffonShellCommand implements GriffonCommand {
    @Option(name = "--env", description = "Sets the environment to use.")
    protected String env = Environment.DEVELOPMENT.getName();

    @Option(name = "--non-interactive", description = "Controls if the shell can ask for input or not.")
    protected boolean nonInteractive = false;

    private CommandArguments commandArguments;

    public CommandArguments getCommandArguments() {
        return commandArguments;
    }

    public void setCommandArguments(CommandArguments commandArguments) {
        this.commandArguments = commandArguments;
    }

    @Override
    public Object execute(CommandSession session) throws Exception {
        GriffonScriptRunner runner = getGriffonScriptRunner();
        setEnvironment(runner);
        return doExecute(session, commandArguments);
    }


    private void setEnvironment(GriffonScriptRunner runner) {
        System.clearProperty(Environment.KEY);
        runner.setRunningEnvironment(getScriptName(), env);
        runner.setInteractive(!nonInteractive);
        String currentEnvironment = BuildSettingsHolder.getSettings().getGriffonEnv();
        if (!currentEnvironment.equalsIgnoreCase(getLastEnvironment())) {
            System.clearProperty("griffon.env.set");
        }
        setLastEnvironment(currentEnvironment);
    }

    protected String getScriptName() {
        return GriffonUtil.getScriptName(getShortName(getClass().getName()));
    }

    protected abstract Object doExecute(CommandSession session, CommandArguments commandArguments) throws Exception;
}
