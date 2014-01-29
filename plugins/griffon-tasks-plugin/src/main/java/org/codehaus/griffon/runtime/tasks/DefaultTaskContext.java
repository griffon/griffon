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

import griffon.plugins.tasks.*;
import griffon.util.UIThreadWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Wraps the execution of a {@link griffon.plugins.tasks.Task} which is governed by a {@link DefaultTaskWorker}. This context holds
 * adds listener support. It registers itself as {@link java.beans.PropertyChangeListener} to the {@link DefaultTaskWorker}
 * object and translates the {@link griffon.util.UIThreadWorker} events to {@link griffon.plugins.tasks.TaskEvent}s.
 * <p/>
 * Each execution can be identified by a growing id. For retrieving task execution properties it delegates
 * to the wrapped {@link DefaultTaskWorker}.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 21:40
 */
public class DefaultTaskContext implements TaskContext, PropertyChangeListener {
    private final static Logger log = LoggerFactory.getLogger(DefaultTaskContext.class);
    private final static AtomicLong counter = new AtomicLong(0);

    private final String contextId = String.valueOf(counter.getAndIncrement());

    private final TaskWorker worker;
    private final TaskListenerSupport taskListenerSupport;
    private final EventEmitter<TaskListener> localListeners = EventEmitter
        .newEmitter(TaskListener.class, new LoggingExceptionHandler<TaskListener>(log));

    public DefaultTaskContext(TaskWorker worker, TaskListenerSupport taskListenerSupport) {
        this.worker = worker;
        this.worker.setContext(this);
        this.taskListenerSupport = taskListenerSupport;
        this.worker.addPropertyChangeListener(this);
    }

    public String getContextId() {
        return contextId;
    }

    public Long getStartedTimestamp() {
        return worker.getStartedTimestamp();
    }

    public Long getFinishTimestamp() {
        return worker.getFinishTimestamp();
    }

    public Task.State getState() {
        if (worker.isCancelled()) {
            return Task.State.CANCELLED;
        }
        return toState(worker.getState());
    }

    public int getProgress() {
        return worker.getProgress();
    }

    public String getPhase() {
        return worker.getPhase();
    }

    public Long getDuration() {
        Long start = getStartedTimestamp();
        if (start != null) {
            Long finish = getFinishTimestamp();
            long end = finish != null ? finish : System.currentTimeMillis();
            return end - start;
        }
        return null;
    }

    public void addListener(TaskListener listener) {
        if (listener != null) {
            localListeners.addListener(listener);
        }
    }

    public void removeListener(TaskListener listener) {
        if (listener != null) {
            localListeners.addListener(listener);
        }
    }

    public <V, C> TaskWorker<V, C> getWorker() {
        return worker;
    }

    public Task getTask() {
        return worker.getTask();
    }

    public void fireStateChangeEvent(Task.State oldValue, Task.State newValue) {
        ChangeEvent<Task.State> e = new DefaultChangeEvent<Task.State>(oldValue, newValue, this);
        taskListenerSupport.fireStateChanged(e);
        localListeners.emitter().stateChanged(e);
    }

    public void fireProgressChangeEvent(Integer oldValue, Integer newValue) {
        ChangeEvent<Integer> e = new DefaultChangeEvent<Integer>(oldValue, newValue, this);
        taskListenerSupport.fireProgressChanged(e);
        localListeners.emitter().progressChanged(e);
    }

    public void firePhaseChangeEvent(String oldValue, String newValue) {
        ChangeEvent<String> e = new DefaultChangeEvent<String>(oldValue, newValue, this);
        taskListenerSupport.firePhaseChanged(e);
        localListeners.emitter().phaseChanged(e);
    }
    // ~~ PropertyChangeListener

    public void propertyChange(PropertyChangeEvent evt) {
        if (getTask().getMode() == Task.Mode.SILENT) {
            return;
        }
        if (evt.getPropertyName().equals("state")) {
            UIThreadWorker.StateValue nv = (UIThreadWorker.StateValue) evt.getNewValue();
            if (nv == UIThreadWorker.StateValue.DONE) {
                if (worker.isError()) {
                    fireStateChangeEvent(toState(evt.getOldValue()), Task.State.FAILED);
                } else if (worker.isCancelled()) {
                    fireStateChangeEvent(toState(evt.getOldValue()), Task.State.CANCELLED);
                } else {
                    fireStateChangeEvent(toState(evt.getOldValue()), toState(evt.getNewValue()));
                }
            } else {
                fireStateChangeEvent(toState(evt.getOldValue()), toState(evt.getNewValue()));
            }
        }
        if (evt.getPropertyName().equals("progress")) {
            fireProgressChangeEvent((Integer) evt.getOldValue(), (Integer) evt.getNewValue());
        }
        if (evt.getPropertyName().equals("phase")) {
            firePhaseChangeEvent((String) evt.getOldValue(), (String) evt.getNewValue());
        }
    }

    private Task.State toState(Object value) {
        if (value == Task.State.CANCELLED) {
            return (Task.State) value;
        }
        switch ((UIThreadWorker.StateValue) value) {
            case DONE:
                return Task.State.DONE;
            case PENDING:
                return Task.State.PENDING;
            case STARTED:
                return Task.State.STARTED;
        }
        throw new Error("unknown state: " + value);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultTaskContext that = (DefaultTaskContext) o;

        return !(contextId != null ? !contextId.equals(that.contextId) : that.contextId != null);
    }

    public int hashCode() {
        return contextId != null ? contextId.hashCode() : 0;
    }

    public String toString() {
        return "DefaultTaskContext{" +
            "contextId='" + contextId + '\'' +
            '}';
    }
}
