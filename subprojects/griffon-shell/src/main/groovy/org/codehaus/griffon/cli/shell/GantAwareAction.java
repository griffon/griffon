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
import org.apache.felix.gogo.commands.CommandException;
import org.apache.felix.service.command.CommandSession;
import org.codehaus.gant.GantBinding;
import org.codehaus.griffon.cli.GriffonScriptRunner;
import org.codehaus.griffon.cli.ScriptExitException;
import org.codehaus.griffon.cli.shell.support.CommandArguments;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.quote;
import static org.codehaus.griffon.cli.GriffonScriptRunner.*;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.*;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.flatten;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.join;

/**
 * @author Andres Almiray
 */
public class GantAwareAction extends AbstractGriffonShellCommand {
    private final String scope;
    private final String name;
    private final File scriptFile;
    private final String scriptName;

    public GantAwareAction(String scope, String name, File scriptFile) {
        this.scope = scope;
        this.name = name;
        this.scriptFile = scriptFile;
        this.scriptName = getScriptNameFromFile(scriptFile);
    }

    public String getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public String getScriptName() {
        return scriptName;
    }

    @Override
    protected Object doExecute(CommandSession session, CommandArguments args) throws Exception {
        GriffonScriptRunner runner = getGriffonScriptRunner();
        GantBinding binding = getGantBinding();
        //if (binding == null) {
        binding = new GantBinding();
        setGantBinding(binding);
        //}

        Map<String, Object> argsMap = new LinkedHashMap<String, Object>();
        populateOptions(argsMap, args.options, args.subject);
        populateArguments(argsMap, args.orderedArguments, args.subject);

        binding.setVariable(VAR_SCRIPT_UNPARSED_ARGS, join(args.params, " "));
        binding.setVariable(VAR_SCRIPT_ENV, System.getProperty(Environment.KEY));
        binding.setVariable(VAR_SCRIPT_NAME, scriptName);
        binding.setVariable(VAR_SCRIPT_FILE, scriptFile);
        binding.setVariable(VAR_SCRIPT_ARGS_MAP, argsMap);
        Gant gant = runner.createGantInstance(binding);
        gant.loadScript(scriptFile);

        try {
            runner.executeWithGantInstance(gant, binding);
        } catch (ScriptExitException see) {
            // OK, we just got this exception because exit is not
            // allowed when running inside the interactive shell
        } catch (RuntimeException e) {
            // bummer, we got a problem
            sanitize(e);
            if (!(e instanceof ScriptExitException) && !(e.getCause() instanceof ScriptExitException)) {
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.RED).toString());
                e.printStackTrace(session.getConsole());
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
            }
        } finally {
            // perform some cleanup in the binding
            // System.setProperty(GriffonScriptRunner.KEY_SCRIPT_ARGS, "");
            binding.setVariable(VAR_SCRIPT_ARGS_MAP, emptyArgsMap());
            binding.setVariable(VAR_SCRIPT_UNPARSED_ARGS, "");
        }

        return null;
    }

    private Map<String, Object> emptyArgsMap() {
        Map<String, Object> argsMap = new LinkedHashMap<String, Object>();
        argsMap.put("params", new ArrayList());
        return argsMap;
    }

    private void populateArguments(Map<String, Object> argsMap, List<Argument> orderedArguments, Object subject) throws CommandException {
        List<Object> arguments = new ArrayList<Object>();
        for (Argument arg : orderedArguments) {
            String argName = arg.name();
            try {
                Field field = subject.getClass().getDeclaredField(argName);
                field.setAccessible(true);
                Object value = field.get(subject);
                if (value != null) {
                    arguments.add(value);
                }
            } catch (IllegalAccessException e) {
                throw new CommandException("Could not read argument " + argName + " due to " + e.getMessage());
            } catch (NoSuchFieldException e) {
                throw new CommandException("Could not read argument " + argName + " due to " + e.getMessage());
            }
        }
        argsMap.put("params", flatten(arguments));
    }

    private void populateOptions(Map<String, Object> argsMap, Map<Option, Field> options, Object subject) throws CommandException {
        for (Map.Entry<Option, Field> option : options.entrySet()) {
            String optionName = option.getKey().name();
            if ("--env".equals(optionName) || "--non-interactive".equals(optionName)) continue;
            try {
                Field field = option.getValue();
                field.setAccessible(true);
                Object value = field.get(subject);
                if (value != null) {
                    String val = value.toString();
                    if ("false".equalsIgnoreCase(val)) continue;
                    argsMap.put(optionName, quote(val));
                }
            } catch (IllegalAccessException e) {
                throw new CommandException("Could not read option " + optionName + " due to " + e.getMessage());
            }
        }
    }
}
