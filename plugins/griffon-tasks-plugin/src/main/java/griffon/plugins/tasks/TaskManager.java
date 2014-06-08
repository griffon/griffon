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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;


/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 19.07.11 22:16
 */
public interface TaskManager {

    /**
     * Creates a new {@link TaskControl} for a given task. The state is
     * set to {@link griffon.plugins.tasks.Task.State#PENDING} and the task is not started. That
     * allows to add listeners for this task execution before starting
     * it.
     * <p/>
     * Tasks created by this method will fire {@link TaskEvent}s as
     * appropriate. See {@link TaskListener} for details.
     *
     * @param task
     * @param <V>
     * @param <C>
     * @return
     */

    <V, C> TaskControl<V> create(Task<V, C> task);

    /**
     * Allows to add listeners.
     *
     * @return
     */

    TaskListenerSupport getTaskListenerSupport();

    /**
     * Returns a collection of all currently created task conforming to
     * the specified predicate. The contract is to have all started
     * and pending tasks available. It's not necessary returning finished
     * tasks.
     *
     * @param predicate
     * @return
     */

    Iterable<TaskControl> getTasks(TaskPredicate predicate);


    TaskControl findTask(TaskPredicate predicate);

    /**
     * Looks up a task by the specified context id.
     *
     * @param contextId
     * @return
     */

    TaskControl getTask(String contextId);

    /**
     * Submits a {@link Runnable} object for execution. Contrary to executing
     * {@link Task}s this is more lightweight as it does not fire any events
     * and is not managed by this manager.
     *
     * @param task
     * @return
     */

    Future<?> submit(Runnable task);

    /**
     * Submits a {@link java.util.concurrent.Callable} object for execution. Contrary to executing
     * {@link Task}s this is more lightweight as it does not fire any events
     * and is not managed by this manager.
     *
     * @param task
     * @return
     */

    <T> Future<T> submit(Callable<T> task);

}
