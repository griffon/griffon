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
        name = "integrate-with",
        description = "Generates tool specific files for a Griffon project")
public class IntegrateWithCommand extends AbstractGriffonCommand {
    @Option(name = "--gradle",
            description = "Generates a build.gradle file.",
            required = false)
    private String gradle;

    @Option(name = "--ant",
            description = "Generates a build.xml file.",
            required = false)
    private String ant;

    @Option(name = "--eclipse",
            description = "Generates Eclipse .project and .classpath files.",
            required = false)
    private String eclipse;

    @Option(name = "--idea",
            aliases = {"--intellij"},
            description = "Generates IntelliJ IDEA project files.",
            required = false)
    private String idea;

    @Option(name = "--sublimetext2",
            description = "Generates Sublime Text 2 project files.",
            required = false)
    private String sublimetext2;

    @Option(name = "--textmate",
            description = "Generates TextMate project files.",
            required = false)
    private String textmate;
}