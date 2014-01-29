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
 * Represents the context of a running task.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 08:34
 */
public interface TaskContext {
    /**
     * The context id is generated on each task execution and
     * is (at least) unique among all executions in this application
     * run.
     *
     * @return
     */
    String getContextId();


    Task getTask();


    Long getStartedTimestamp();


    Long getFinishTimestamp();


    Task.State getState();

    int getProgress();


    String getPhase();


    Long getDuration();

    /**
     * Adds listener that receives events for this execution only. Use
     * {@link TaskListenerSupport} to add more global listeners.
     *
     * @param listener
     */
    void addListener(TaskListener listener);

    void removeListener(TaskListener listener);

    <V, C> TaskWorker<V, C> getWorker();

    void fireStateChangeEvent(Task.State oldValue, Task.State newValue);

    void fireProgressChangeEvent(Integer oldValue, Integer newValue);

    void firePhaseChangeEvent(String oldValue, String newValue);
}
