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
        name = "stats",
        description = "Generates basic stats for a Griffon project")
public class StatsCommand extends AbstractGriffonCommand {
    @Option(name = "--xml",
            description = "Writes project stats in XML format to $appHome/target.",
            required = false)
    private boolean xml = false;

    @Option(name = "--html",
            description = "Writes project stats in HTML format to $appHome/target.",
            required = false)
    private boolean html = false;

    @Option(name = "--txt",
            description = "Writes project stats in plain text to $appHome/target.",
            required = false)
    private boolean txt = false;
}