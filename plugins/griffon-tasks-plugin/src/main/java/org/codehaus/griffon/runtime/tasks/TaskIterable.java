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

import griffon.plugins.tasks.TaskControl;
import griffon.plugins.tasks.TaskPredicate;

import java.util.Iterator;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 21.09.11 21:16
 */
public class TaskIterable implements Iterable<TaskControl> {
    private final Iterable<TaskControl> delegate;
    private final TaskPredicate filter;

    protected TaskIterable(Iterable<TaskControl> delegate, TaskPredicate filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    public Iterator<TaskControl> iterator() {
        return new TaskIterator(delegate.iterator(), filter);
    }

    public static TaskIterable filter(Iterable<TaskControl> iterable, TaskPredicate filter) {
        return new TaskIterable(iterable, filter);
    }

    public static TaskControl find(Iterable<TaskControl> iterable, TaskPredicate filter) {
        Iterator<TaskControl> iter = filter(iterable, filter).iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }
}
