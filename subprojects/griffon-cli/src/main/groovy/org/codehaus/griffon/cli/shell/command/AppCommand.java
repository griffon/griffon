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
 * @since 0.9.5
 */
@Command(scope = "griffon",
        name = "app",
        description = "Runs the application in standalone mode")
public class AppCommand extends AbstractGriffonCommand {
    @Option(name = "--debug",
            description = "Whether to run the application in debug more or not.",
            required = false)
    private boolean debug = false;

    @Option(name = "--debug-suspend",
            description = "Whether to start the debug process in suspend mode or not.",
            required = false)
    private String debugSuspend = "n";

    @Option(name = "--debug-port",
            description = "Debugging port connection.",
            required = false)
    private String debugPort = "18290";

    @Option(name = "--debug-addr",
            description = "Debugging address.",
            required = false)
    private String debugAddr = "127.0.0.1";

    @Option(name = "--java-opts",
            description = "Additional JVM options.",
            required = false)
    private String javaOpts;

    @Option(name = "--jvm-opts",
            description = "Additional JVM options such as --javaagent.",
            required = false)
    private String jvmOpts;

    @Argument(index = 0,
            name = "arguments",
            description = "Optional arguments to be passed to the application.",
            required = false,
            multiValued = true)
    private List<String> arguments;
}