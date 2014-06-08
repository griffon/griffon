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


/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 11:31
 */
public interface TaskListenerSupport {
    /**
     * Add a {@link TaskListener} that receives events for all tasks executed.
     *
     * @param listener
     */
    void addListener(TaskListener listener);

    void removeListener(TaskListener listener);

    /**
     * Add a {@link TaskListener} that will only receive events for tasks with
     * the specified id.
     *
     * @param taskId
     * @param listener
     */
    void addListener(String taskId, TaskListener listener);

    void removeListener(String taskId, TaskListener listener);

    void fireStateChanged(ChangeEvent<Task.State> e);

    void fireProgressChanged(ChangeEvent<Integer> e);

    void firePhaseChanged(ChangeEvent<String> e);
}
