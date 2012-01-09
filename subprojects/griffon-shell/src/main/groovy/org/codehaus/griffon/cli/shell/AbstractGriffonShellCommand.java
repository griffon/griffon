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
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.service.command.CommandSession;
import org.codehaus.griffon.cli.GriffonScriptRunner;

import static griffon.util.GriffonNameUtils.getShortName;
import static org.codehaus.griffon.cli.shell.GriffonScriptRunnerHolder.*;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public abstract class AbstractGriffonShellCommand implements Action {
    @Option(name = "--env", description = "Sets the environment to use.")
    protected String env = Environment.DEVELOPMENT.getName();

    @Override
    public Object execute(CommandSession session) throws Exception {
        GriffonScriptRunner runner = getGriffonScriptRunner();
        setEnvironment(runner);
        return doExecute(session);
    }

    private void setEnvironment(GriffonScriptRunner runner) {
        System.clearProperty(Environment.KEY);
        runner.setRunningEnvironment(getScriptName(), env);
        String currentEnvironment = BuildSettingsHolder.getSettings().getGriffonEnv();
        if (!currentEnvironment.equalsIgnoreCase(getLastEnvironment())) {
            System.clearProperty("griffon.env.set");
        }
        setLastEnvironment(currentEnvironment);
    }

    protected String getScriptName() {
        return GriffonUtil.getScriptName(getShortName(getClass().getName()));
    }

    protected abstract Object doExecute(CommandSession session) throws Exception;
}
