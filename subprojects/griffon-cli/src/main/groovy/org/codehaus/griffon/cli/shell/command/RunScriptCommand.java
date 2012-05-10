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

import java.util.List;

/**
 * @author Andres Almiray
 * @since 0.9.6
 */
@Command(scope = "griffon",
        name = "run-script",
        description = "Runs a Groovy script with full classpath")
public class RunScriptCommand extends AbstractGriffonCommand {
    @Option(name = "--bootstrap",
            description = "Whether to bootstrap an application instance or not.",
            required = false)
    private boolean debug = false;

    @Argument(index = 0,
            name = "script",
            description = "Name of the script to be executed.",
            required = true,
            multiValued = false)
    private String script;

    @Argument(index = 1,
            name = "arguments",
            description = "Optional arguments to be passed to the running script.",
            required = false,
            multiValued = true)
    private List<String> arguments;
}