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
 * @since 1.2.0
 */
@Command(scope = "griffon",
        name = "upload-release",
        description = "Uploads a release to an artifact repository")
public class UploadReleaseCommand extends AbstractGriffonCommand {
    @Option(name = "--repository",
            description = "Name of an specific repository where the release will be published.",
            required = false)
    private String repository;

    @Option(name = "--username",
        description = "Username credentials.",
        required = false)
    private String username;

    @Option(name = "--password",
        description = "Password credentials.",
        required = false)
    private String password;

    @Argument(index = 0,
        name = "filename",
        description = "Filename of the release package",
        required = true)
    private String filename;
}