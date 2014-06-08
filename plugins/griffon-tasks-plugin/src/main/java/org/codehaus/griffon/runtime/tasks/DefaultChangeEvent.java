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


import griffon.plugins.tasks.ChangeEvent;
import griffon.plugins.tasks.TaskContext;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 19:01
 */
public class DefaultChangeEvent<T> implements ChangeEvent<T> {
    private final TaskContext source;
    private final T oldValue;
    private final T newValue;

    public DefaultChangeEvent(T oldValue, T newValue, TaskContext context) {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.source = context;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public TaskContext getSource() {
        return source;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultChangeEvent that = (DefaultChangeEvent) o;

        return !(newValue != null ? !newValue.equals(that.newValue) : that.newValue != null) &&
            !(oldValue != null ? !oldValue.equals(that.oldValue) : that.oldValue != null) &&
            !(source != null ? !source.equals(that.source) : that.source != null);
    }

    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (oldValue != null ? oldValue.hashCode() : 0);
        result = 31 * result + (newValue != null ? newValue.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "DefaultChangeEvent{" +
            "source=" + source +
            ", oldValue=" + oldValue +
            ", newValue=" + newValue +
            '}';
    }
}
