/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.cli.shell.command;

import org.apache.commons.lang.ArrayUtils;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;
import org.codehaus.griffon.cli.CommandLineConstants;
import org.fusesource.jansi.Ansi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.getProperties;
import static java.lang.System.setProperties;
import static java.util.Arrays.sort;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.*;

@Command(scope = "shell", name = "properties", description = "Prints information on system properties.")
public class PropertiesCommand extends AbstractAction {
    @Option(name = "--reset",
            description = "Reset property values as if the shell was just launched.")
    private boolean reset;

    protected Object doExecute() throws Exception {
        if (reset) {
            Properties props = getProperties();
            for (String key : getSystemProperties().keySet()) {
                props.remove(key);
            }
            for (String key : CommandLineConstants.KEYS) {
                props.remove(key);
            }
            for (Map.Entry<String, String> property : getInitialSystemProperties().entrySet()) {
                props.put(property.getKey(), property.getValue());
            }
            setSystemProperties(getInitialSystemProperties());
            setProperties(props);
        }

        printProperties("Initial Properties", getInitialSystemProperties(), false);
        printProperties("Command Properties", getCommandLineProperties(), true);
        printProperties("Custom Properties", getCustomProperties(), true);

        return null;
    }

    private void printProperties(String label, Map<String, String> properties, boolean live) {
        int maxNameLen = 0;
        System.out.println(label);
        if (!properties.isEmpty()) {
            //1st pass calculates max length
            for (String key : properties.keySet()) {
                maxNameLen = Math.max(maxNameLen, key.length());
            }
            // 2nd pass prints the values
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                if (live) {
                    printSysValue(entry.getKey(), maxNameLen);
                } else {
                    printValue(entry.getKey(), maxNameLen, entry.getValue());
                }
            }
        } else {
            System.out.println("  NONE");
        }
        System.out.println();
    }

    private Map<String, String> getCommandLineProperties() {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        for (String key : CommandLineConstants.KEYS) {
            properties.put(key, null);
        }
        return properties;
    }

    private Map<String, String> getCustomProperties() {
        String[] commandProperties = (String[]) ArrayUtils.clone(CommandLineConstants.KEYS);
        sort(commandProperties);
        Map<String, String> properties = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : getSystemProperties().entrySet()) {
            if (contains(commandProperties, entry.getKey())) continue;
            properties.put(entry.getKey(), null);
        }
        return properties;
    }

    private void printSysValue(String prop, int pad) {
        printValue(prop, pad, System.getProperty(prop));
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
