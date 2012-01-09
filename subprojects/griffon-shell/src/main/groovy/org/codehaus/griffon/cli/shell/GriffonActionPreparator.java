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

import jline.Terminal;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.basic.DefaultActionPreparator;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.NameScoping;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Andres Almiray
 */
public class GriffonActionPreparator extends DefaultActionPreparator {
    private final String scope;
    private final String name;
    private final File scriptFile;

    public GriffonActionPreparator(String scope, String name, File scriptFile) {
        this.scope = scope;
        this.name = name;
        this.scriptFile = scriptFile;
    }

    protected void printUsage(CommandSession session, Action action, Map<Option, Field> optionsMap, Map<Argument, Field> argsMap, PrintStream out) {
        Terminal term = session != null ? (Terminal) session.get(".jline.terminal") : null;
        Set<Option> options = new HashSet<Option>(optionsMap.keySet());
        options.add(HELP);

        boolean globalScope = NameScoping.isGlobalScope(session, scope);
        out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("DESCRIPTION").a(Ansi.Attribute.RESET));
        out.print("        ");
        if (name != null) {
            if (globalScope) {
                out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(name).a(Ansi.Attribute.RESET));
            } else {
                out.println(Ansi.ansi().a(scope).a(":").a(Ansi.Attribute.INTENSITY_BOLD).a(name).a(Ansi.Attribute.RESET));
            }
            out.println();
        }
        out.print("\t");
        out.println("No description available for " + name);
        out.println();
        out.println("\tLoaded from");
        out.println("\t" + scriptFile);
        out.println();
        StringBuffer syntax = new StringBuffer();
        if (globalScope) {
            syntax.append(name);
        } else {
            syntax.append(String.format("%s:%s", scope, name));
        }

        if (options.size() > 0) {
            syntax.append(" [options]");
        }

        out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("SYNTAX").a(Ansi.Attribute.RESET));
        out.print("        ");
        out.println(syntax.toString());
        out.println();

        if (options.size() > 0) {
            out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("OPTIONS").a(Ansi.Attribute.RESET));
            for (Option option : options) {
                String opt = option.name();
                for (String alias : option.aliases()) {
                    opt += ", " + alias;
                }
                out.print("        ");
                out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(opt).a(Ansi.Attribute.RESET));
                printFormatted("                ", option.description(), term != null ? term.getWidth() : 80, out);
                if (option.valueToShowInHelp() != null && option.valueToShowInHelp().length() != 0) {
                    try {
                        if (Option.DEFAULT_STRING.equals(option.valueToShowInHelp())) {
                            optionsMap.get(option).setAccessible(true);
                            Object o = optionsMap.get(option).get(action);
                            printObjectDefaultsTo(out, o);
                        } else {
                            printDefaultsTo(out, option.valueToShowInHelp());
                        }
                    } catch (Throwable t) {
                        // Ignore
                    }
                }
            }
            out.println();
        }
    }

    private void printObjectDefaultsTo(PrintStream out, Object o) {
        if (o != null
                && (!(o instanceof Boolean) || ((Boolean) o))
                && (!(o instanceof Number) || ((Number) o).doubleValue() != 0.0)) {
            printDefaultsTo(out, o.toString());
        }
    }

    private void printDefaultsTo(PrintStream out, String value) {
        out.print("                (defaults to ");
        out.print(value);
        out.println(")");
    }
}
