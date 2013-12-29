/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import griffon.util.CollectionUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.AbstractAction;

import static java.lang.System.setProperty;
import static org.codehaus.griffon.cli.shell.GriffonShellContext.addSystemProperties;

@Command(scope = "shell", name = "set-property", description = "Sets or changes the value of a system property.")
public class SetPropertyCommand extends AbstractAction {
    @Argument(index = 0,
            name = "name",
            description = "Name for the property to be set.",
            required = true)
    private String name;

    @Argument(index = 1,
            name = "value",
            description = "Value to be set.",
            required = true)
    private String value;

    protected Object doExecute() throws Exception {
        setProperty(name, value);
        addSystemProperties(CollectionUtils.newMap(name, value));
        return null;
    }
}
