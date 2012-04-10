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
        name = "test-app",
        description = "Runs the project's tests")
public class TestAppCommand extends AbstractGriffonCommand {
    @Option(name = "--clean",
            description = "Cleans compiled classes before test are run.",
            required = false)
    private boolean clean;

    @Option(name = "--no-reports",
            description = "Skips writing test reports if set to true.",
            required = false)
    private boolean noReports;

    @Option(name = "--rerun",
            description = "Reruns failed tests only.",
            required = false)
    private boolean reRun;

    @Option(name = "--xml",
            description = "Creates test reports in XML format.",
            required = false)
    private boolean xml;

    @Argument(index = 0,
            name = "arguments",
            description = "Specifies type:phase and/or test selection.",
            required = false,
            multiValued = true)
    private List<String> arguments;
}