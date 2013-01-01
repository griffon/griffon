/*
 * Copyright 2008-2013 the original author or authors.
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

package org.codehaus.griffon.cli.shell.support;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.felix.service.command.CommandSession;
import org.codehaus.griffon.cli.shell.GantAwareAction;

import java.io.File;
import java.util.List;

import static org.codehaus.griffon.cli.GriffonScriptRunner.getScriptNameFromFile;
import static org.codehaus.griffon.cli.shell.support.CommandUtils.registerCommand;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public class GriffonCommandFactory extends AbstractCommand {
    private final String scope;
    private final String name;
    private final File scriptFile;

    public GriffonCommandFactory(String scope, String name, File scriptFile) {
        this.scope = scope;
        this.name = name;
        this.scriptFile = scriptFile;
        registerCommand(scope, name, getScriptNameFromFile(scriptFile));
    }

    @Override
    public Action createNewAction() {
        return new GantAwareAction(scope, name, scriptFile);
    }

    @Override
    protected ActionPreparator getPreparator() throws Exception {
        return new GriffonCommandPreparator(scope, name, scriptFile);
    }

    public Object execute(CommandSession session, List<Object> arguments) throws Exception {
        GantAwareAction action = (GantAwareAction) createNewAction();

        GriffonCommandPreparator preparator = (GriffonCommandPreparator) getPreparator();
        try {
            CommandArguments commandArguments = new CommandArguments();
            action.setCommandArguments(commandArguments);
            if (preparator.prepare(action, session, arguments, commandArguments)) {
                return action.execute(session);
            } else {
                return null;
            }
        } finally {
            releaseAction(action);
        }
    }
}
