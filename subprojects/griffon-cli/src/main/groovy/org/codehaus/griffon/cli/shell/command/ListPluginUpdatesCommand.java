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
import org.codehaus.griffon.cli.shell.Command;
import org.codehaus.griffon.cli.shell.Option;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "griffon",
        name = "list-plugin-updates",
        description = "Lists all available plugin updates that match the project's configuration")
public class ListPluginUpdatesCommand extends AbstractGriffonCommand {
    @Option(name = "--repository",
            description = "Name of an specific repository where the search will be performed.",
            required = false)
    private String repository;

    @Option(name = "--install",
            description = "Install all matching upgrades.",
            required = false)
    private boolean install;

    @Option(name = "--framework",
            description = "Forces checking for available updates of framework plugins.",
            required = false)
    private boolean framework;
}