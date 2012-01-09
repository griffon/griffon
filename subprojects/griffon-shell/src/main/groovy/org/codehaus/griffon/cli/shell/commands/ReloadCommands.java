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

package org.codehaus.griffon.cli.shell.commands;

import griffon.util.BuildSettings;
import griffon.util.Metadata;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.gogo.commands.basic.ActionPreparator;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.codehaus.griffon.artifacts.ArtifactUtils;
import org.codehaus.griffon.artifacts.model.Plugin;
import org.codehaus.griffon.artifacts.model.Release;
import org.codehaus.griffon.cli.GriffonScriptRunner;
import org.codehaus.griffon.cli.shell.GantAwareAction;
import org.codehaus.griffon.cli.shell.GriffonActionPreparator;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static griffon.util.BuildSettingsHolder.getSettings;
import static griffon.util.GriffonNameUtils.getHyphenatedName;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;
import static org.codehaus.griffon.cli.GriffonScriptRunner.getScriptNameFromFile;
import static org.codehaus.griffon.cli.shell.CommandProcessorImplHolder.getCommandProcessor;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "griffon", name = "reload-commands", description = "Reloads the list of available commands")
public class ReloadCommands implements Action {
    public static void reload() {
        CommandProcessorImpl commandProcessor = getCommandProcessor();

        Set<String[]> commandsToRemove = new LinkedHashSet<String[]>();
        for (String commandName : commandProcessor.getCommands()) {
            String[] tmp = commandName.split(":");
            if ("griffon".equalsIgnoreCase(tmp[0])) continue;
            commandsToRemove.add(tmp);
        }

        for (String[] command : commandsToRemove) {
            commandProcessor.removeCommand(command[0], command[1]);
        }

        BuildSettings buildSettings = getSettings();

        addCommandScripts("global", new File(buildSettings.getUserHome(), ".griffon/scripts"), commandProcessor);

        boolean isPluginProject = buildSettings.isPluginProject() != null;
        String scope = isPluginProject ? Metadata.getCurrent().getApplicationName() : "app";

        addCommandScripts(scope, new File(buildSettings.getBaseDir(), "scripts"), commandProcessor);

        for (Map.Entry<String, Release> plugin : buildSettings.pluginSettings.getPlugins().entrySet()) {
            String name = plugin.getKey();
            String version = plugin.getValue().getVersion();
            File path = ArtifactUtils.getInstallPathFor(Plugin.TYPE, name, version);
            addCommandScripts(name, new File(path, "scripts"), commandProcessor);
        }
    }

    public static void loadGriffonCommands() {
        CommandProcessorImpl commandProcessor = getCommandProcessor();
        BuildSettings buildSettings = getSettings();
        addCommandScripts("griffon", new File(buildSettings.getGriffonHome(), "scripts"), commandProcessor);
    }

    @Override
    public Object execute(CommandSession session) throws Exception {
        reload();
        return null;
    }

    private static final String[] FORBIDDEN_NAMES = {
            "exit", "help", "reload-commands"
    };

    static {
        sort(FORBIDDEN_NAMES);
    }

    private static void addCommandScripts(final String scope, File dir, CommandProcessorImpl commandProcessor) {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (GriffonScriptRunner.isCommandScript(file)) {
                    final String commandName = getHyphenatedName(getScriptNameFromFile(file)).trim();
                    if (binarySearch(FORBIDDEN_NAMES, commandName) >= 0) continue;
                    final File scriptFile = file;
                    Function function = new AbstractCommand() {
                        @Override
                        public Action createNewAction() {
                            return new GantAwareAction(scriptFile);
                        }

                        @Override
                        protected ActionPreparator getPreparator() throws Exception {
                            return new GriffonActionPreparator(scope, commandName, scriptFile);
                        }
                    };
                    commandProcessor.addCommand(scope, function, commandName);
                }
            }
        }
    }
}