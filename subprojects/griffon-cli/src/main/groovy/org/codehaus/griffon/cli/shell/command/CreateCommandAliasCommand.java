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

import java.util.List;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "griffon",
        name = "create-command-alias",
        description = "Create Griffon command alias")
public class CreateCommandAliasCommand extends AbstractGriffonCommand {
    @Argument(index = 0,
            name = "alias",
            description = "The name of the alias to set.",
            required = true)
    private String alias;

    @Argument(index = 1,
            name = "target",
            description = "The target to be aliased.",
            required = true)
    private String target;

    @Argument(index = 2,
            name = "arguments",
            description = "Optional arguments to be set on the alias.",
            required = false,
            multiValued = true)
    private List<String> args;
}