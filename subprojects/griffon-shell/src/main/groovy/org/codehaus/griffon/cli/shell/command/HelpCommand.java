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
import griffon.util.GriffonExceptionHandler;
import jline.Terminal;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.gogo.commands.basic.DefaultActionPreparator;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.NameScoping;
import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.cli.GriffonScriptRunner;
import org.codehaus.griffon.cli.ScriptExitException;
import org.codehaus.griffon.cli.shell.GriffonCommand;
import org.codehaus.griffon.cli.shell.support.CommandUtils;
import org.fusesource.jansi.Ansi;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.codehaus.griffon.cli.GriffonScriptRunner.*;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.*;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "shell", name = "help", description = "Displays this help or help about a command")
public class HelpCommand extends AbstractAction {
    @Argument(name = "command", required = false, description = "The command to get help for")
    private String command;

    @Option(name = "--resolve-dependencies", required = false, description = "Resolve plugin dependencies before collecting help information")
    private boolean resolveDependencies;

    public Object doExecute() throws Exception {
        if (resolveDependencies) {
            resolvePluginDependencies();
        }

        if (command == null) {
            Set<String> names = (Set<String>) session.get(".commands");
            if (!names.isEmpty()) {
                Terminal term = (Terminal) session.get(".jline.terminal");
                PrintStream out = System.out;
                SortedMap<String, String> commands = new TreeMap<String, String>();
                for (String name : names) {
                    String description = null;
                    Function function = (Function) session.get(name);
                    function = unProxy(function);
                    if (function instanceof AbstractCommand) {
                        try {
                            Method mth = AbstractCommand.class.getDeclaredMethod("createNewAction");
                            mth.setAccessible(true);
                            Object action = mth.invoke(function);
                            Class<?> clazz = action.getClass();
                            if (GriffonCommand.class.isAssignableFrom(clazz)) {
                                action = CommandUtils.getDelegateAction(name);
                                if (action != null) {
                                    org.codehaus.griffon.cli.shell.Command ann = action.getClass().getAnnotation(org.codehaus.griffon.cli.shell.Command.class);
                                    description = ann.description();
                                }
                            } else {
                                Command ann = clazz.getAnnotation(Command.class);
                                description = ann.description();
                            }
                        } catch (Throwable e) {
                            GriffonExceptionHandler.sanitize(e).printStackTrace();
                        }
                        if (name.startsWith("*:")) {
                            name = name.substring(2);
                        }
                        commands.put(name, description);
                    }
                }
                // Post process the commands list

                out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("COMMANDS").a(Ansi.Attribute.RESET));
                for (Map.Entry<String, String> entry : commands.entrySet()) {
                    out.print("        ");
                    String key = NameScoping.getCommandNameWithoutGlobalPrefix(session, entry.getKey());
                    out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(key).a(Ansi.Attribute.RESET));
                    if (entry.getValue() != null) {
                        DefaultActionPreparator.printFormatted("                ", entry.getValue(), term != null ? term.getWidth() : 80, out);
                    }
                }
                out.println();
            }
            return null;
        } else {
            return session.execute(command + " --help");
        }
    }

    private void resolvePluginDependencies() {
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
        binding.setVariable("runDependencyResolution", Boolean.TRUE);
        Gant gant = runner.createGantInstance(binding);

        try {
            gant.setAllPerTargetPreHooks(DO_NOTHING_CLOSURE);
            gant.setAllPerTargetPostHooks(DO_NOTHING_CLOSURE);
            gant.processTargets("resolveDependencies");
        } catch (ScriptExitException see) {
            // OK, we just got this exception because exit is not
            // allowed when running inside the interactive shell
        } catch (RuntimeException e) {
            GriffonExceptionHandler.sanitize(e);
            // bummer, we got a problem
            if (!(e instanceof ScriptExitException) && !(e.getCause() instanceof ScriptExitException)) {
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.RED).toString());
                e.printStackTrace(session.getConsole());
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
            }
        }
    }

    protected Function unProxy(Function function) {
        try {
            if (function.getClass().getName().contains("CommandProxy")) {
                Field contextField = function.getClass().getDeclaredField("context");
                Field referenceField = function.getClass().getDeclaredField("reference");
                contextField.setAccessible(true);
                referenceField.setAccessible(true);
                BundleContext context = (BundleContext) contextField.get(function);
                ServiceReference reference = (ServiceReference) referenceField.get(function);
                Object target = context.getService(reference);
                try {
                    if (target instanceof Function) {
                        function = (Function) target;
                    }
                } finally {
                    context.ungetService(reference);
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return function;
    }
}