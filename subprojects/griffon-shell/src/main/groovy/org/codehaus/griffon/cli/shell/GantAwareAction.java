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

import static org.codehaus.griffon.cli.GriffonScriptRunner.*;
import static org.codehaus.griffon.cli.shell.GriffonScriptRunnerHolder.*;
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
        if (binding == null) {
            binding = new GantBinding();
            setGantBinding(binding);
        }

        List<String> arguments = new ArrayList<String>();
        populateOptions(arguments, args.options, args.subject);
        populateArguments(arguments, args.orderedArguments, args.subject);

        System.setProperty(GriffonScriptRunner.KEY_SCRIPT_ARGS, join(arguments, " "));

        binding.setVariable(VAR_SCRIPT_ENV, System.getProperty(Environment.KEY));
        binding.setVariable(VAR_SCRIPT_NAME, scriptName);
        binding.setVariable(VAR_SCRIPT_FILE, scriptFile);
        Gant gant = runner.createGantInstance(binding);
        gant.loadScript(scriptFile);

        try {
            executeWithGantInstance(gant, binding);
        } catch (ScriptExitException see) {
            // OK, we just got this exception because exit is not
            // allowed when running inside the interactive shell
        } catch (RuntimeException e) {
            // bummer, we got a problem
            if (e instanceof ScriptExitException || e.getCause() instanceof ScriptExitException) {
                // false alarm
            } else {
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.RED).toString());
                e.printStackTrace(session.getConsole());
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
            }
        } finally {
            // perform some cleanup in the binding
            System.setProperty(GriffonScriptRunner.KEY_SCRIPT_ARGS, "");
            binding.setVariable("argsMap", emptyArgsMap());
            binding.setVariable("classpathSet", false);
        }

        return null;
    }

    private Map<String, Object> emptyArgsMap() {
        Map<String, Object> argsMap = new LinkedHashMap<String, Object>();
        argsMap.put("params", new ArrayList());
        return argsMap;
    }

    private void populateArguments(List<String> arguments, List<Argument> orderedArguments, Object subject) throws CommandException {
        for (Argument arg : orderedArguments) {
            String argName = arg.name();
            try {
                Field field = subject.getClass().getDeclaredField(argName);
                field.setAccessible(true);
                Object value = field.get(subject);
                if (value != null) {
                    arguments.add(value.toString());
                }
            } catch (IllegalAccessException e) {
                throw new CommandException("Could not read argument " + argName + " due to " + e.getMessage());
            } catch (NoSuchFieldException e) {
                throw new CommandException("Could not read argument " + argName + " due to " + e.getMessage());
            }
        }
    }

    private void populateOptions(List<String> arguments, Map<Option, Field> options, Object subject) throws CommandException {
        for (Map.Entry<Option, Field> option : options.entrySet()) {
            String optionName = option.getKey().name();
            if ("--env".equals(optionName) || "--non-interactive".equals(optionName)) continue;
            try {
                Field field = option.getValue();
                field.setAccessible(true);
                Object value = field.get(subject);
                if (value != null) {
                    arguments.add(optionName + "=" + quote(value));
                }
            } catch (IllegalAccessException e) {
                throw new CommandException("Could not read option " + optionName + " due to " + e.getMessage());
            }
        }
    }

    private String quote(Object value) {
        String val = value.toString();
        if (val.contains(" ")) {
            val = applyQuotes(val);
        }
        return val;
    }

    private String applyQuotes(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char b;
        char c = 0;
        int i;
        int len = string.length();
        StringBuffer sb = new StringBuffer(len * 2);
        String t;
        char[] chars = string.toCharArray();
        char[] buffer = new char[1030];
        int bufferIndex = 0;
        sb.append('"');
        for (i = 0; i < len; i += 1) {
            if (bufferIndex > 1024) {
                sb.append(buffer, 0, bufferIndex);
                bufferIndex = 0;
            }
            b = c;
            c = chars[i];
            switch (c) {
                case '\\':
                case '"':
                    buffer[bufferIndex++] = '\\';
                    buffer[bufferIndex++] = c;
                    break;
                case '/':
                    if (b == '<') {
                        buffer[bufferIndex++] = '\\';
                    }
                    buffer[bufferIndex++] = c;
                    break;
                default:
                    if (c < ' ') {
                        switch (c) {
                            case '\b':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'b';
                                break;
                            case '\t':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 't';
                                break;
                            case '\n':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'n';
                                break;
                            case '\f':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'f';
                                break;
                            case '\r':
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'r';
                                break;
                            default:
                                t = "000" + Integer.toHexString(c);
                                int tLength = t.length();
                                buffer[bufferIndex++] = '\\';
                                buffer[bufferIndex++] = 'u';
                                buffer[bufferIndex++] = t.charAt(tLength - 4);
                                buffer[bufferIndex++] = t.charAt(tLength - 3);
                                buffer[bufferIndex++] = t.charAt(tLength - 2);
                                buffer[bufferIndex++] = t.charAt(tLength - 1);
                        }
                    } else {
                        buffer[bufferIndex++] = c;
                    }
            }
        }
        sb.append(buffer, 0, bufferIndex);
        sb.append('"');
        return sb.toString();
    }
}
