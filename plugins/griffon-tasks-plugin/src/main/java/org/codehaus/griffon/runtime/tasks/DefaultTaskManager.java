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

import griffon.core.ExecutorServiceManager;
import griffon.core.GriffonApplication;
import griffon.plugins.tasks.*;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.Objects.requireNonNull;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 18:36
 */
public class DefaultTaskManager implements TaskManager {
    private final TaskListenerSupport taskListenerSupport;
    private final Map<String, TaskControl> tasks = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private ExecutorServiceManager executorServiceManager;
    private GriffonApplication application;

    public DefaultTaskManager(@Nonnull GriffonApplication application, @Nonnull ExecutorServiceManager executorServiceManager, final TaskBlocker blocker) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
        this.executorServiceManager = requireNonNull(executorServiceManager, "Argument 'executorServiceManager' must not be bull");
        taskListenerSupport = createTaskListenerSupport();

        blocker.setTaskManager(this);

        taskListenerSupport.addListener(new TaskListenerAdapter() {
            public void stateChanged(ChangeEvent<Task.State> event) {
                final Task.State newState = event.getNewValue();
                final String contextId = event.getSource().getContextId();
                final Task task = event.getSource().getTask();
                if (newState != null) {
                    if (newState.isFinalState()) {
                        tasks.remove(contextId);
                        if (task.getMode() == Task.Mode.BLOCKING_APPLICATION || task.getMode() == Task.Mode.BLOCKING_WINDOW) {
                            blocker.unblock(task);
                        }
                    }
                    if (newState == Task.State.STARTED) {
                        if (task.getMode() == Task.Mode.BLOCKING_APPLICATION || task.getMode() == Task.Mode.BLOCKING_WINDOW) {
                            blocker.block(task);
                        }
                    }
                }
            }
        });

        executorService = createExecutorService();
    }

    protected TaskListenerSupport createTaskListenerSupport() {
        return new DefaultTaskListenerSupport();
    }

    protected ThreadPoolExecutor createExecutorService() {
        return new ThreadPoolExecutor(0, 20,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());
    }

    public <V, C> TaskControl<V> create(Task<V, C> task) {
        application.getInjector().injectMembers(task);
        TaskContext context = createTaskContext(task);
        TaskControl<V> control = createTaskControl(context);
        tasks.put(control.getContext().getContextId(), control);
        context.fireStateChangeEvent(null, Task.State.PENDING); //must be after the task-control has been added to the map; so listeners can access it
        return control;
    }

    protected <V> TaskControl<V> createTaskControl(TaskContext context) {
        return new DefaultTaskControl<>(context);
    }

    protected <V, C> TaskContext createTaskContext(Task<V, C> task) {
        final DefaultTaskWorker<V, C> worker = new DefaultTaskWorker<>(task);
        worker.setExecutorServiceManager(executorServiceManager);
        worker.setUiThreadManager(application.getUIThreadManager());
        return new DefaultTaskContext(worker, taskListenerSupport);
    }

    public TaskListenerSupport getTaskListenerSupport() {
        return taskListenerSupport;
    }

    public Iterable<TaskControl> getTasks(TaskPredicate predicate) {
        return TaskIterable.filter(tasks.values(), predicate);
    }

    public TaskControl findTask(TaskPredicate predicate) {
        return TaskIterable.find(tasks.values(), predicate);
    }

    public Future<?> submit(final Runnable task) {
        return executorService.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    public TaskControl getTask(String contextId) {
        return tasks.get(contextId);
    }
}
