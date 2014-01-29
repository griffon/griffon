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
 * @since 21.09.11 21:13
 */
public class TaskIterator implements Iterator<TaskControl> {
    private final Iterator<TaskControl> delegate;
    private final TaskPredicate filter;

    private TaskControl current;

    public TaskIterator(Iterator<TaskControl> delegate, TaskPredicate filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    public boolean hasNext() {
        if (delegate.hasNext()) {
            this.current = delegate.next();
            return filter.apply(current) || hasNext();
        }
        return false;
    }

    public TaskControl next() {
        return current;
    }

    public void remove() {
        delegate.remove();
    }
}
