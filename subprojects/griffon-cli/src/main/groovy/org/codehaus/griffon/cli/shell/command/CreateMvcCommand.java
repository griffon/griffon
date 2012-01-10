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
        name = "create-mvc",
        description = "Creates a new MVC Group")
public class CreateMvcCommand extends AbstractGriffonCommand {
    @Argument(index = 0,
            name = "name",
            description = "The name of the group to be created.",
            required = false)
    private String name;

    @Option(name = "--skip-package-prompt",
            description = "Skips the usage of the application's default package if the name of the class is not fully qualified.",
            required = false)
    private boolean skipPackagePrompt = false;

    @Option(name = "--file-type",
            description = "Source file type",
            required = false)
    private String fileType = "groovy";

    @Option(name = "--archetype",
            description = "Archetype to be searched for templates.",
            required = false)
    private String archetype = "default";

    @Option(name = "--with-model",
            description = "Fully qualified className Model to use. WARNING: the command will not create a file for this member.",
            required = false)
    private String withModel;

    @Option(name = "--with-view",
            description = "Fully qualified className View to use. WARNING: the command will not create a file for this member.",
            required = false)
    private String withView;

    @Option(name = "--with-controller",
            description = "Fully qualified className Controller to use. WARNING: the command will not create a file for this member.",
            required = false)
    private String withController;

    @Option(name = "--skip-model",
            description = "Skips the creation of the model MVC member.",
            required = false)
    private String skipModel;

    @Option(name = "--skip-view",
            description = "Skips the creation of the view MVC member.",
            required = false)
    private String skipView;

    @Option(name = "--skip-controller",
            description = "Skips the creation of the controller MVC member.",
            required = false)
    private String skipController;

    @Option(name = "--model",
            description = "Specifies the Model template to be used.",
            required = false)
    private String model = "Model";

    @Option(name = "--view",
            description = "Specifies the View template to be used.",
            required = false)
    private String view = "View";

    @Option(name = "--controller",
            description = "Specifies the Controller template to be used.",
            required = false)
    private String controller = "Controller";

    @Option(name = "--group",
            description = "Specifies the common template to use on all MVC members.",
            required = false)
    private String group;
}