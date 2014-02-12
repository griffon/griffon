/*
 * Copyright 2012-2014 the original author or authors.
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
        name = "create-service",
        description = "Creates a new service class")
public class CreateServiceCommand extends AbstractGriffonCommand {
    @Argument(index = 0,
            name = "name",
            description = "The name of the service to be created.",
            required = false)
    private String name;

    @Option(name = "--skip-package-prompt",
            description = "Skips the usage of the application's default package if the name of the class is not fully qualified.",
            required = false)
    private boolean skipPackagePrompt = false;

    @Option(name = "--file-type",
            description = "Source file type.",
            required = false)
    private String fileType = "groovy";

    @Option(name = "--archetype",
            description = "Archetype to be searched for templates.",
            required = false)
    private String archetype = "default";
}