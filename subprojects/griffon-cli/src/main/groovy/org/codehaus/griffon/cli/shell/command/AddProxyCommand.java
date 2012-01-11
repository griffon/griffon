/*
 * Copyright 2012 the original author or authors.
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

import org.codehaus.griffon.cli.shell.AbstractGriffonCommand;
import org.codehaus.griffon.cli.shell.Argument;
import org.codehaus.griffon.cli.shell.Command;
import org.codehaus.griffon.cli.shell.Option;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "griffon",
        name = "add-proxy",
        description = "Adds a proxy configuration")
public class AddProxyCommand extends AbstractGriffonCommand {
    @Option(name = "--host",
            description = "The proxy's hostname.",
            required = true)
    private String host;

    @Option(name = "--port",
            description = "The proxy's port.",
            required = true)
    private String port;

    @Option(name = "--username",
            description = "Username credentials.",
            required = false)
    private String username;

    @Option(name = "--password",
            description = "Password credentials.",
            required = false)
    private String password;

    @Argument(index = 0,
            name = "name",
            description = "Name for the proxy configuration.",
            required = true)
    private String name;
}