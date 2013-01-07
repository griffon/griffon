/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.cli.support;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

/**
 * @author Andres Almiray
 */
public class BuildListenerAdapter implements BuildListener {
    @Override
    public void buildStarted(BuildEvent buildEvent) {
    }

    @Override
    public void buildFinished(BuildEvent buildEvent) {
    }

    @Override
    public void targetStarted(BuildEvent buildEvent) {
    }

    @Override
    public void targetFinished(BuildEvent buildEvent) {
    }

    @Override
    public void taskStarted(BuildEvent buildEvent) {
    }

    @Override
    public void taskFinished(BuildEvent buildEvent) {
    }

    @Override
    public void messageLogged(BuildEvent buildEvent) {
    }
}
