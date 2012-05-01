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

package org.codehaus.griffon.cli.shell.support;

import jline.Terminal;
import org.apache.commons.lang.WordUtils;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.CommandException;
import org.apache.felix.gogo.commands.basic.DefaultActionPreparator;
import org.apache.felix.gogo.commands.converter.GenericType;
import org.apache.felix.gogo.commands.converter.GriffonDefaultConverter;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.NameScoping;
import org.codehaus.griffon.cli.shell.Argument;
import org.codehaus.griffon.cli.shell.Command;
import org.codehaus.griffon.cli.shell.GantAwareAction;
import org.codehaus.griffon.cli.shell.Option;
import org.fusesource.jansi.Ansi;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public class GriffonCommandPreparator extends DefaultActionPreparator {
    private final String name;
    private final String scope;
    private final File scriptFile;

    public static final Option HELP = new Option() {
        public String name() {
            return "--help";
        }

        public String[] aliases() {
            return new String[]{};
        }

        public String description() {
            return "Display this help message";
        }

        public boolean required() {
            return false;
        }

        public boolean multiValued() {
            return false;
        }

        public String valueToShowInHelp() {
            return Option.DEFAULT_STRING;
        }

        public Class<? extends Annotation> annotationType() {
            return Option.class;
        }
    };

    public GriffonCommandPreparator(String scope, String name, File scriptFile) {
        this.scope = scope;
        this.name = name;
        this.scriptFile = scriptFile;
    }

    public boolean prepare(Action action, CommandSession session, List<Object> params, CommandArguments args) throws Exception {
        GantAwareAction gantAction = (GantAwareAction) action;
        Object delegateAction = CommandUtils.getDelegateAction(gantAction.getScope() + ":" + gantAction.getName());
        if (delegateAction != null) {
            return prepareDelegate(delegateAction, session, params, args);
        }
        return prepareDelegate(gantAction, session, params, args);
    }

    public boolean prepareDelegate(Object subject, CommandSession session, List<Object> params, CommandArguments args) throws Exception {
        args.subject = subject;
        args.params.clear();
        args.params.addAll(params);
        // Introspect
        for (Class type = subject.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option != null) {
                    args.options.put(option, field);
                }
                Argument argument = field.getAnnotation(Argument.class);
                if (argument != null) {
                    if (Argument.DEFAULT.equals(argument.name())) {
                        final Argument delegate = argument;
                        final String name = field.getName();
                        argument = new Argument() {
                            public String name() {
                                return name;
                            }

                            public String description() {
                                return delegate.description();
                            }

                            public boolean required() {
                                return delegate.required();
                            }

                            public int index() {
                                return delegate.index();
                            }

                            public boolean multiValued() {
                                return delegate.multiValued();
                            }

                            public String valueToShowInHelp() {
                                return delegate.valueToShowInHelp();
                            }

                            public Class<? extends Annotation> annotationType() {
                                return delegate.annotationType();
                            }
                        };
                    }
                    args.arguments.put(argument, field);
                    int index = argument.index();
                    while (args.orderedArguments.size() <= index) {
                        args.orderedArguments.add(null);
                    }
                    if (args.orderedArguments.get(index) != null) {
                        throw new IllegalArgumentException("Duplicate argument index: " + index);
                    }
                    args.orderedArguments.set(index, argument);
                }
            }
        }
        // Check indexes are correct
        for (int i = 0; i < args.orderedArguments.size(); i++) {
            if (args.orderedArguments.get(i) == null) {
                throw new IllegalArgumentException("Missing argument for index: " + i);
            }
        }
        // Populate
        Map<Option, Object> optionValues = new TreeMap<Option, Object>(CommandArguments.OPTION_COMPARATOR);
        Map<Argument, Object> argumentValues = new HashMap<Argument, Object>();
        boolean processOptions = true;
        int argIndex = 0;
        for (Iterator<Object> it = params.iterator(); it.hasNext(); ) {
            Object param = it.next();
            // Check for help
            if (HELP.name().equals(param) || Arrays.asList(HELP.aliases()).contains(param)) {
                printUsageDelegate(session, subject, args.options, args.arguments, System.out);
                return false;
            }
            if (processOptions && param instanceof String && ((String) param).startsWith("-")) {
                boolean isKeyValuePair = ((String) param).indexOf('=') != -1;
                String name;
                Object value = null;
                if (isKeyValuePair) {
                    name = ((String) param).substring(0, ((String) param).indexOf('='));
                    value = ((String) param).substring(((String) param).indexOf('=') + 1);
                } else {
                    name = (String) param;
                }
                Option option = null;
                for (Option opt : args.options.keySet()) {
                    if (name.equals(opt.name()) || Arrays.asList(opt.aliases()).contains(name)) {
                        option = opt;
                        break;
                    }
                }
                if (option == null) {
                    throw new CommandException(
                            Ansi.ansi()
                                    .fg(Ansi.Color.RED)
                                    .a("Error executing command ")
                                    .a(scope)
                                    .a(":")
                                    .a(Ansi.Attribute.INTENSITY_BOLD)
                                    .a(name)
                                    .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                    .a(" undefined option ")
                                    .a(Ansi.Attribute.INTENSITY_BOLD)
                                    .a(param)
                                    .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                    .fg(Ansi.Color.DEFAULT)
                                    .toString(),
                            "Undefined option: " + param
                    );
                }
                Field field = args.options.get(option);
                if (value == null && (field.getType() == boolean.class || field.getType() == Boolean.class)) {
                    value = Boolean.TRUE;
                }
                if (value == null && it.hasNext()) {
                    value = it.next();
                }
                if (value == null) {
                    throw new CommandException(
                            Ansi.ansi()
                                    .fg(Ansi.Color.RED)
                                    .a("Error executing command ")
                                    .a(scope)
                                    .a(":")
                                    .a(Ansi.Attribute.INTENSITY_BOLD)
                                    .a(name)
                                    .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                    .a(" missing value for option ")
                                    .a(Ansi.Attribute.INTENSITY_BOLD)
                                    .a(param)
                                    .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                    .fg(Ansi.Color.DEFAULT)
                                    .toString(),
                            "Missing value for option: " + param
                    );
                }
                if (option.multiValued()) {
                    List<Object> l = (List<Object>) optionValues.get(option);
                    if (l == null) {
                        l = new ArrayList<Object>();
                        optionValues.put(option, l);
                    }
                    l.add(value);
                } else {
                    optionValues.put(option, value);
                }
            } else {
                processOptions = false;
                if (argIndex >= args.orderedArguments.size()) {
                    throw new CommandException(
                            Ansi.ansi()
                                    .fg(Ansi.Color.RED)
                                    .a("Error executing command ")
                                    .a(scope)
                                    .a(":")
                                    .a(Ansi.Attribute.INTENSITY_BOLD)
                                    .a(name)
                                    .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                    .a(": too many arguments specified")
                                    .fg(Ansi.Color.DEFAULT)
                                    .toString(),
                            "Too many arguments specified"
                    );
                }
                Argument argument = args.orderedArguments.get(argIndex);
                if (!argument.multiValued()) {
                    argIndex++;
                }
                if (argument.multiValued()) {
                    List<Object> l = (List<Object>) argumentValues.get(argument);
                    if (l == null) {
                        l = new ArrayList<Object>();
                        argumentValues.put(argument, l);
                    }
                    l.add(param);
                } else {
                    argumentValues.put(argument, param);
                }
            }
        }
        // Check required arguments / options
        for (Option option : args.options.keySet()) {
            if (option.required() && optionValues.get(option) == null) {
                throw new CommandException(
                        Ansi.ansi()
                                .fg(Ansi.Color.RED)
                                .a("Error executing command ")
                                .a(scope)
                                .a(":")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(name)
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(": option ")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(option.name())
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(" is required")
                                .fg(Ansi.Color.DEFAULT)
                                .toString(),
                        "Option " + option.name() + " is required"
                );
            }
        }
        for (Argument argument : args.arguments.keySet()) {
            if (argument.required() && argumentValues.get(argument) == null) {
                throw new CommandException(
                        Ansi.ansi()
                                .fg(Ansi.Color.RED)
                                .a("Error executing command ")
                                .a(scope)
                                .a(":")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(name)
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(": argument ")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(argument.name())
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(" is required")
                                .fg(Ansi.Color.DEFAULT)
                                .toString(),
                        "Argument " + argument.name() + " is required"
                );
            }
        }
        // Convert and inject values
        for (Map.Entry<Option, Object> entry : optionValues.entrySet()) {
            Field field = args.options.get(entry.getKey());
            Object value;
            try {
                value = convert(subject, entry.getValue(), field.getGenericType());
            } catch (Exception e) {
                throw new CommandException(
                        Ansi.ansi()
                                .fg(Ansi.Color.RED)
                                .a("Error executing command ")
                                .a(scope)
                                .a(":")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(name)
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(": unable to convert option ")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(entry.getKey().name())
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(" with value '")
                                .a(entry.getValue())
                                .a("' to type ")
                                .a(new GenericType(field.getGenericType()).toString())
                                .fg(Ansi.Color.DEFAULT)
                                .toString(),
                        "Unable to convert option " + entry.getKey().name() + " with value '"
                                + entry.getValue() + "' to type " + new GenericType(field.getGenericType()).toString(),
                        e
                );
            }
            field.setAccessible(true);
            field.set(subject, value);
        }
        for (Map.Entry<Argument, Object> entry : argumentValues.entrySet()) {
            Field field = args.arguments.get(entry.getKey());
            Object value;
            try {
                value = convert(subject, entry.getValue(), field.getGenericType());
            } catch (Exception e) {
                throw new CommandException(
                        Ansi.ansi()
                                .fg(Ansi.Color.RED)
                                .a("Error executing command ")
                                .a(scope)
                                .a(":")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(name)
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(": unable to convert argument ")
                                .a(Ansi.Attribute.INTENSITY_BOLD)
                                .a(entry.getKey().name())
                                .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                                .a(" with value '")
                                .a(entry.getValue())
                                .a("' to type ")
                                .a(new GenericType(field.getGenericType()).toString())
                                .fg(Ansi.Color.DEFAULT)
                                .toString(),
                        "Unable to convert argument " + entry.getKey().name() + " with value '"
                                + entry.getValue() + "' to type " + new GenericType(field.getGenericType()).toString(),
                        e
                );
            }
            field.setAccessible(true);
            field.set(subject, value);
        }
        return true;
    }

    protected void printUsageDelegate(CommandSession session, Object action, Map<Option, Field> optionsMap, Map<Argument, Field> argsMap, PrintStream out) {
        Command command = action.getClass().getAnnotation(Command.class);
        Terminal term = session != null ? (Terminal) session.get(".jline.terminal") : null;
        List<Argument> arguments = new ArrayList<Argument>(argsMap.keySet());
        Collections.sort(arguments, new Comparator<Argument>() {
            public int compare(Argument o1, Argument o2) {
                return Integer.valueOf(o1.index()).compareTo(Integer.valueOf(o2.index()));
            }
        });

        String commandName = command != null ? command.name() : name;
        String commandScope = command != null ? command.scope() : scope;
        String commandDescription = command != null ? command.description() : "No description available for " + name + ".";
        String commandDetailedDescription = command != null ? command.detailedDescription() : "";

        Set<Option> options = new TreeSet<Option>(CommandArguments.OPTION_COMPARATOR);
        options.addAll(optionsMap.keySet());
        options.add(HELP);
        boolean globalScope = NameScoping.isGlobalScope(session, scope);
        out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("DESCRIPTION").a(Ansi.Attribute.RESET));
        out.print("        ");
        if (commandName != null) {
            if (globalScope) {
                out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(commandName).a(Ansi.Attribute.RESET));
            } else {
                out.println(Ansi.ansi().a(commandScope).a(":").a(Ansi.Attribute.INTENSITY_BOLD).a(commandName).a(Ansi.Attribute.RESET));
            }
            out.println();
        }
        String description = commandDescription;
        if (isBlank(description)) {
            description = "No description available for " + name + ".";
        }
        out.print("\t");
        out.println(description);
        out.println();
        out.println("\tLoaded from");
        out.println("\t" + scriptFile);
        out.println();

        StringBuffer syntax = new StringBuffer();
        if (globalScope) {
            syntax.append(commandName);
        } else {
            syntax.append(String.format("%s:%s", commandScope, commandName));
        }

        if (options.size() > 0) {
            syntax.append(" [options]");
        }
        if (arguments.size() > 0) {
            syntax.append(' ');
            for (Argument argument : arguments) {
                if (!argument.required()) {
                    syntax.append(String.format("[%s] ", argument.name()));
                } else {
                    syntax.append(String.format("%s ", argument.name()));
                }
            }
        }

        out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("SYNTAX").a(Ansi.Attribute.RESET));
        out.print("        ");
        out.println(syntax.toString());
        out.println();
        if (arguments.size() > 0) {
            out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("ARGUMENTS").a(Ansi.Attribute.RESET));
            for (Argument argument : arguments) {
                out.print("        ");
                out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(argument.name()).a(Ansi.Attribute.RESET));
                printFormatted("                ", argument.description(), term != null ? term.getWidth() : 80, out);
                if (!argument.required()) {
                    if (argument.valueToShowInHelp() != null && argument.valueToShowInHelp().length() != 0) {
                        try {
                            if (Argument.DEFAULT_STRING.equals(argument.valueToShowInHelp())) {
                                argsMap.get(argument).setAccessible(true);
                                Object o = argsMap.get(argument).get(action);
                                printObjectDefaultsTo(out, o);
                            } else {
                                printDefaultsTo(out, argument.valueToShowInHelp());
                            }
                        } catch (Throwable t) {
                            // Ignore
                        }
                    }
                }
            }
            out.println();
        }
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
        if (commandDetailedDescription.length() > 0) {
            out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("DETAILS").a(Ansi.Attribute.RESET));
            String desc = loadDescription(action.getClass(), commandDetailedDescription);
            printFormattedFixed("        ", desc, term != null ? term.getWidth() : 80, out);
        }
    }

    public static void printFormattedFixed(String prefix, String str, int termWidth, PrintStream out) {
        int pfxLen = length(prefix);
        int maxwidth = termWidth - pfxLen;
        BufferedReader reader = new BufferedReader(new StringReader(str));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                out.println(prefix + WordUtils.wrap(line, maxwidth, '\n' + prefix, true));
            }
        } catch (IOException e) {
            // ignore
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

    private Object convert(Object action, Object value, Type toType) throws Exception {
        if (toType == String.class) {
            return value != null ? value.toString() : null;
        }
        return new GriffonDefaultConverter(action.getClass().getClassLoader()).convert(value, toType);
    }
}
