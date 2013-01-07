/*
 * Copyright 2008-2013 the original author or authors.
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

import griffon.util.BuildSettings;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.CommandException;
import org.apache.karaf.shell.console.AbstractAction;

import java.awt.*;
import java.io.File;

import static griffon.util.BuildSettingsHolder.getSettings;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Command(scope = "shell", name = "open", description = "Opens a file using the platform's default editor or viewer")
public class OpenCommand extends AbstractAction {
    @Argument(name = "filename",
            description = "The file to open.")
    protected String filename;

    @Override
    protected Object doExecute() throws Exception {
        BuildSettings settings = getSettings();

        final Desktop desktop = Desktop.getDesktop();
        File file = null;
        if ("test-report".equals(filename)) {
            file = new File(settings.getTestReportsDir(), "html/index.html").getAbsoluteFile();
        } else if ("dependency-report".equals(filename)) {
            file = new File(settings.getProjectTargetDir(), "dependency-report/index.html").getAbsoluteFile();
        } else {
            file = new File(filename);
        }

        if (file.exists()) {
            desktop.open(file);
        } else {
            throw new CommandException("File " + filename + " does not exist.");
        }


        return null;
    }
}