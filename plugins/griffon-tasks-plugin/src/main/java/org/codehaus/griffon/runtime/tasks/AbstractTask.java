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

import griffon.plugins.tasks.Task;

import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 20.07.11 00:40
 */
public abstract class AbstractTask<V, C> implements Task<V, C> {
    private final String id;
    private Mode mode = Mode.BACKGROUND;

    protected AbstractTask(String id) {
        this.id = id;
    }

    protected AbstractTask(String id, Mode mode) {
        this.id = id;
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public void done(V value) {
    }

    public void failed(Throwable cause) {
    }

    public void process(List<C> chunks) {
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
