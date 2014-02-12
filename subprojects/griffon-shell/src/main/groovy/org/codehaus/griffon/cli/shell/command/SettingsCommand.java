/*
 * Copyright 2008-2014 the original author or authors.
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
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static griffon.util.BuildSettingsHolder.getSettings;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.getGriffonScriptRunner;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.setGantBinding;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "shell", name = "settings", description = "Displays current Griffon settings")
public class SettingsCommand extends AbstractAction {
    @Option(name = "--reload",
            description = "Reads all settings from config files again.")
    protected boolean reload;

    @Override
    protected Object doExecute() throws Exception {
        BuildSettings settings = getSettings();
        if (reload) {
            settings.resetConfig();
            getGriffonScriptRunner().setLoggingOptions();
            setGantBinding(null);
        }

        int maxNameLen = 25;
        System.out.println("Griffon");
        printValue("Griffon version", maxNameLen, settings.getGriffonVersion());
        printValue("Griffon home", maxNameLen, settings.getGriffonHome().getAbsolutePath());
        printValue("Groovy version", maxNameLen, settings.getGroovyVersion());
        printValue("Basedir", maxNameLen, settings.getBaseDir().getAbsolutePath());
        URL configFile = settings.getConfig().getConfigFile();
        String configHeader = "Config files";
        if (configFile != null) {
            printValue(configHeader, maxNameLen, configFile.getFile());
            configHeader = "";
        }
        printValue(configHeader, maxNameLen, new File(settings.getUserHome() + "/.griffon/settings.groovy").getAbsolutePath());
        printValue("", maxNameLen, new File(settings.getUserHome() + "/.griffon/ProxySettings.groovy").getAbsolutePath());
        System.out.println();

        Map map = settings.getConfig().flatten(new LinkedHashMap());
        Set<String> keys = new TreeSet<String>();
        keys.addAll(map.keySet());

        System.out.println("Settings");
        for (String key : keys) {
            Object value = map.get(key);
            if (isPrintableValue(value)) {
                printValue(key, value.toString());
            }
        }
        System.out.println();

        return null;
    }

    private boolean isPrintableValue(Object value) {
        return value instanceof CharSequence ||
                value instanceof Boolean ||
                value instanceof Number ||
                value instanceof File ||
                value instanceof URL;
    }

    protected void printValue(String name, String value) {
        System.out.println(Ansi.ansi().a("  ")
                .a(Ansi.Attribute.INTENSITY_BOLD).a(name).a(Ansi.Attribute.RESET)
                .a(" = ").a(value).toString());
    }

    private void printValue(String name, int pad, String value) {
        if (value == null) {
            value = "<not set>";
        } else if (value.equals("")) {
            value = "<empty>";
        }
        System.out.println(Ansi.ansi().a("  ")
                .a(Ansi.Attribute.INTENSITY_BOLD).a(name).a(spaces(pad - name.length())).a(Ansi.Attribute.RESET)
                .a("   ").a(value).toString());
    }

    private String spaces(int nb) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nb; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
}