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

package org.codehaus.griffon.cli.shell;

import griffon.util.Environment;
import org.codehaus.griffon.cli.shell.Option;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public abstract class AbstractGriffonCommand {
    @Option(name = "--env", description = "Sets the environment to use.")
    protected String env = Environment.DEVELOPMENT.getName();

    @Option(name = "--non-interactive", description = "Controls if the shell can ask for input or not.")
    protected boolean nonInteractive = false;
}