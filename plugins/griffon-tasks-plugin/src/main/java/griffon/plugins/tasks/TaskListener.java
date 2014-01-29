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

package griffon.plugins.tasks;

import java.util.EventListener;


/**
 * Listener receives events about execution of a task. All methods are invoked
 * on the UI thread.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 00:22
 */
public interface TaskListener extends EventListener {
    /**
     * Receives notifications about state change for a task. There are 3 events possible
     * in a task's life cycle
     * <ol>
     * <li><code>null</code> to {@link griffon.plugins.tasks.Task.State#PENDING pending}</li> indicates, that a brand new task has just been created.
     * <li>{@link griffon.plugins.tasks.Task.State#PENDING pending} to {@link griffon.plugins.tasks.Task.State#STARTED started} indicates, that the task has just been executed and is now running</li>
     * <li>from {@link griffon.plugins.tasks.Task.State#STARTED started} state, three different final events are possible:</li>
     * <ul>
     * <li>{@link griffon.plugins.tasks.Task.State#DONE done} indicates successful termination of the task</li>
     * <li>{@link griffon.plugins.tasks.Task.State#CANCELLED cancelled} indicates that the task has been cancelled explicitly.</li>
     * <li>{@link griffon.plugins.tasks.Task.State#FAILED failed} indicates that the task has been failed due to an exception.</li>
     * </ul>
     * </ol>
     *
     * @param event
     */
    void stateChanged(ChangeEvent<Task.State> event);

    /**
     * Receives notifications about the current progress of a task. The progress values are
     * normalized and values are <code>[0, 100]</code>.
     *
     * @param event
     */
    void progressChanged(ChangeEvent<Integer> event);

    /**
     * Receives notifications about phase changes of a task. A task can set information messages
     * about sub-steps taken, which is called a <i>phase</i>.
     *
     * @param event
     */
    void phaseChanged(ChangeEvent<String> event);
}
