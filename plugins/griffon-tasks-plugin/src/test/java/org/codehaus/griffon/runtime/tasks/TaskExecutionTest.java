/*
 * Copyright 2011 Eike Kettner
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

package org.codehaus.griffon.runtime.tasks;

import griffon.core.injection.Module;
import griffon.core.test.GriffonUnitRule;
import griffon.core.threading.UIThreadManager;
import griffon.plugins.tasks.*;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 19:16
 */
public class TaskExecutionTest {
    private final static Logger LOG = LoggerFactory.getLogger(TaskExecutionTest.class);

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    @Rule
    public final GriffonUnitRule griffon = new GriffonUnitRule();

    @Inject
    private TaskManager manager;

    @Test
    public void testExceptionTask() throws Exception {

        Task<Long, Long> task = new LongTask();
        final TaskControl<Long> control = manager.create(task);
        control.getContext().addListener(new TaskListener() {
            public void stateChanged(ChangeEvent<Task.State> event) {
                LOG.info(">>> State: " + event.getOldValue() + " => " + event.getNewValue());
                if (event.getNewValue() == Task.State.STARTED) {
                    LOG.info("Started: " + event.getSource().getStartedTimestamp());
                }
            }

            public void progressChanged(ChangeEvent<Integer> event) {
                LOG.info(">>> Progress: " + event.getOldValue() + " => " + event.getNewValue());
            }

            public void phaseChanged(ChangeEvent<String> event) {
                LOG.info(">>> Phase: " + event.getOldValue() + " => " + event.getNewValue());
            }
        });
        Long value = control.waitFor();
        assertNotNull(value);
        assertEquals(value.intValue(), 40L);
        LOG.info("Waited for task: " + value);
    }

    @Nonnull
    private List<Module> moduleOverrides() {
        return CollectionUtils.<Module>newList(new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(UIThreadManager.class)
                    .to(SwingUIThreadManager.class)
                    .asSingleton();
            }
        });
    }
}
