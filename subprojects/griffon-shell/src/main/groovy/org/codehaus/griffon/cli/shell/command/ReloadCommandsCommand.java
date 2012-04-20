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

package org.codehaus.griffon.cli.shell.command;

import griffon.util.BuildSettings;
import griffon.util.Metadata;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.codehaus.griffon.cli.GriffonScriptRunner;
import org.codehaus.griffon.cli.shell.support.GriffonCommandFactory;
import org.codehaus.griffon.plugins.PluginInfo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static griffon.util.BuildSettingsHolder.getSettings;
import static griffon.util.GriffonNameUtils.getHyphenatedName;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;
import static org.codehaus.griffon.cli.GriffonScriptRunner.getScriptNameFromFile;
import static org.codehaus.griffon.cli.shell.CommandProcessorImplHolder.getCommandProcessor;
import static org.codehaus.griffon.cli.shell.support.CommandUtils.clearCaches;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "shell", name = "reload-commands", description = "Reloads the list of available commands")
public class ReloadCommandsCommand implements Action {
    public static void reload() {
        CommandProcessorImpl commandProcessor = getCommandProcessor();

        Set<String[]> commandsToRemove = new LinkedHashSet<String[]>();
        for (String commandName : commandProcessor.getCommands()) {
            String[] tmp = commandName.split(":");
            if ("shell".equalsIgnoreCase(tmp[0])) continue;
            commandsToRemove.add(tmp);
        }

        for (String[] command : commandsToRemove) {
            commandProcessor.removeCommand(command[0], command[1]);
        }

        BuildSettings buildSettings = getSettings();

        addCommandScripts("griffon", new File(buildSettings.getGriffonHome(), "scripts"), commandProcessor);
        addCommandScripts("global", new File(buildSettings.getUserHome(), ".griffon/scripts"), commandProcessor);

        boolean isPluginProject = buildSettings.isPluginProject() != null;
        String scope = isPluginProject ? Metadata.getCurrent().getApplicationName() : "app";

        addCommandScripts(scope, new File(buildSettings.getBaseDir(), "scripts"), commandProcessor);

        for (PluginInfo pluginInfo : buildSettings.pluginSettings.getPlugins().values()) {
            String name = pluginInfo.getName();
            try {
                File path = pluginInfo.getDirectory().getFile();
                addCommandScripts(name, new File(path, "scripts"), commandProcessor);
            } catch (IOException e) {
                // ignore ??
            }
        }
    }

    @Override
    public Object execute(CommandSession session) throws Exception {
        clearCaches();
        reload();
        return null;
    }

    private static final String[] FORBIDDEN_NAMES = {
            "exit", "help", "info", "reload-commands",
            "create-app", "create-plugin", "create-archetype",
            "create-addon", "init", "interactive"
    };

    static {
        sort(FORBIDDEN_NAMES);
    }

    private static void addCommandScripts(String scope, File dir, CommandProcessorImpl commandProcessor) {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (GriffonScriptRunner.isCommandScript(file)) {
                    String commandName = getHyphenatedName(getScriptNameFromFile(file));
                    if (binarySearch(FORBIDDEN_NAMES, commandName) >= 0) continue;
                    Function function = new GriffonCommandFactory(scope, commandName, file);
                    commandProcessor.addCommand(scope, function, commandName);
                }
            }
        }
    }
}