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
import org.codehaus.griffon.cli.shell.Command;
import org.codehaus.griffon.cli.shell.Option;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "griffon",
        name = "release",
        description = "Publishes a Griffon project according to its type")
public class ReleaseCommand extends AbstractGriffonCommand {
    @Option(name = "--nodoc",
            description = "Skips generation of Javadoc and guide documentation.",
            required = false)
    private boolean nodoc = false;

    @Option(name = "--repository",
            description = "Name of an specific repository where the release will be published.",
            required = false)
    private String repository;

    @Option(name = "--message",
            description = "Commit message that identifies this release.",
            required = false)
    private String message;

    @Option(name = "--no-release-notes",
            description = "Do not ask for missing release notes.",
            required = false)
    private boolean releaseNotes;

    @Option(name = "--package-only",
            description = "Create a release package but do not publish it.",
            required = false)
    private boolean packageOnly;

    @Option(name = "--username",
        description = "Username credentials.",
        required = false)
    private String username;

    @Option(name = "--password",
        description = "Password credentials.",
        required = false)
    private String password;
}