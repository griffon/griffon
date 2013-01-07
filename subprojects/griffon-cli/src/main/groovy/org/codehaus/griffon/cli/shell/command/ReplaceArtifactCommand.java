/*
 * Copyright 2012-2013 the original author or authors.
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
 * @since 1.0.0
 */
@Command(scope = "griffon",
        name = "replace-artifact",
        description = "Replaces an artifact file using another template")
public class ReplaceArtifactCommand extends AbstractGriffonCommand {
    @Argument(index = 0,
            name = "name",
            description = "The fully qualified name of the artifact to be replaced.",
            required = false)
    private String name;

    @Option(name = "--type",
            description = "Artifact type, i.e, controller, model, etc.",
            required = true)
    private String type;

    @Option(name = "--file-type",
            description = "Source file type.",
            required = true)
    private String fileType = "groovy";

    @Option(name = "--archetype",
            description = "Archetype to be searched for templates.",
            required = false)
    private String archetype = "default";
}