/*
 * Copyright 2012-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.tasks;

import griffon.core.ExecutorServiceManager;
import griffon.core.GriffonApplication;
import griffon.plugins.tasks.Task;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Andres Almiray
 */
public class NonBlockingTaskManager extends DefaultTaskManager {
    @Inject
    public NonBlockingTaskManager(@Nonnull GriffonApplication application, @Nonnull ExecutorServiceManager executorServiceManager) {
        super(application, executorServiceManager, new AbstractTaskBlocker() {
            public void block(Task task) {
                // empty
            }

            public void unblock(Task task) {
                // empty
            }
        });
    }
}
