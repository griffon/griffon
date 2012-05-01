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

import griffon.util.BuildSettings;
import jline.Terminal;
import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.jline.Console;
import org.codehaus.griffon.cli.GriffonScriptRunner;
import org.codehaus.griffon.cli.GriffonSetup;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static griffon.util.BuildSettingsHolder.getSettings;
import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.unquote;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.*;
import static org.codehaus.griffon.cli.shell.command.ReloadCommandsCommand.reload;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "griffon", name = "griffon", description = "Executes the Griffon interactive shell")
public class GriffonShell extends KarafMain implements Action {
    private static final Pattern SYSTEM_PROPERTY_PATTERN = Pattern.compile("-D(.+?)=(['\"].+?['\"]|.+?)");

    public static void main(String[] args) {
        Ansi.ansi();
        try {
            String[] transformedArgs = processSystemArguments(args);
            new GriffonShell().run(transformedArgs);
        } catch (Exception e) {
            sanitize(e).printStackTrace();
        }
    }


    public GriffonShell() {
        setUser("me");
        setApplication("griffon");

        // Get hold of the Griffon_HOME environment variable if it is
        // available.
        String griffonHome = System.getProperty("griffon.home");

        // Now we can pick up the Griffon version from the Ant project properties.

        BuildSettings buildSettings = null;
        try {
            buildSettings = new BuildSettings(new File(griffonHome));
        } catch (Exception e) {
            System.err.println("An error occurred loading the griffon-app/conf/BuildConfig.groovy file: " + e.getMessage());
            System.exit(1);
        }

        // Check that Griffon' home actually exists.
        final File griffonHomeInSettings = buildSettings.getGriffonHome();
        if (griffonHomeInSettings == null || !griffonHomeInSettings.exists()) {
            exitWithError("Griffon' installation directory not found: " + buildSettings.getGriffonHome());
        }

        System.setProperty("griffon.disable.exit", "true");
        GriffonScriptRunner runner = new GriffonScriptRunner(buildSettings);
        setGriffonScriptRunner(runner);
        buildSettings.getSystemProperties().putAll(SYSTEM_PROPERTIES);
        runner.setup();
    }

    private static final Map<String, String> SYSTEM_PROPERTIES = new LinkedHashMap<String, String>();

    private static String[] processSystemArguments(String[] args) {
        List<String> transformedArgs = new ArrayList<String>();

        for (String arg : args) {
            Matcher m = SYSTEM_PROPERTY_PATTERN.matcher(arg);
            if (m.matches()) {
                String key = m.group(1).trim();
                String value = unquote(m.group(2).trim());
                SYSTEM_PROPERTIES.put(key, value);
                System.setProperty(key, value);
            } else {
                transformedArgs.add(arg);
            }
        }
        setInitialSystemProperties(SYSTEM_PROPERTIES);
        setSystemProperties(SYSTEM_PROPERTIES);

        return transformedArgs.toArray(new String[transformedArgs.size()]);
    }

    private static void exitWithError(String error) {
        System.out.println(error);
        System.exit(1);
    }

    @Override
    public boolean isMultiScopeMode() {
        return true;
    }

    @Override
    protected Console createConsole(CommandProcessorImpl commandProcessor, InputStream in, PrintStream out, PrintStream err, Terminal terminal) throws Exception {
        final String bold = "\u001B[1m";
        final String plain = "\u001B[0m";

        return new Console(commandProcessor, in, out, err, terminal, null) {
            @Override
            protected String getPrompt() {
                return bold + "griffon> " + plain;
            }

            @Override
            protected void welcome() {
                BuildSettings buildSettings = getSettings();
                File griffonHome = buildSettings.getGriffonHome();
                session.getConsole().println(
                        "Welcome to Griffon " + buildSettings.getGriffonVersion() + " - http://griffon.codehaus.org/" + '\n' +
                                "Licensed under Apache Standard License 2.0" + '\n' +
                                "Griffon home is " + (griffonHome == null ? "not set" : "set to: " + griffonHome) + '\n' + '\n' +
                                "Type 'exit' or ^D to terminate this interactive shell" + '\n' + '\n');
                GriffonSetup.run();
            }

            @Override
            protected void setSessionProperties() {

            }
        };
    }

    @Override
    public String getDiscoveryResource() {
        return "META-INF/services/org.codehaus.griffon/commands.index";
    }

    @Argument(name = "args", description = "griffon command arguments", multiValued = true)
    private String[] args = new String[0];

    @Override
    public Object execute(CommandSession session) throws Exception {
        run(session, args);
        return null;
    }

    @Override
    protected void discoverCommands(CommandProcessorImpl commandProcessor, ClassLoader cl) throws IOException, ClassNotFoundException {
        super.discoverCommands(commandProcessor, cl);
        reload();
    }
}
