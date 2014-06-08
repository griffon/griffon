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
 * @since 21.07.11 17:34
 */
public class TaskPredicates {
    public static TaskPredicate blockingTasks = new TaskPredicate() {

        public boolean apply(TaskControl input) {
            return input.getContext().getTask().getMode() == Task.Mode.BLOCKING_APPLICATION ||
                input.getContext().getTask().getMode() == Task.Mode.BLOCKING_WINDOW;
        }
    };

    public static TaskPredicate backgroundTasks = new TaskPredicate() {

        public boolean apply(TaskControl input) {
            return input.getContext().getTask().getMode() == Task.Mode.BACKGROUND;
        }
    };

    public static TaskPredicate pendingTasks = new TaskPredicate() {

        public boolean apply(TaskControl input) {
            return input.getContext().getState() == Task.State.PENDING;
        }
    };

    public static TaskPredicate startedTasks = new TaskPredicate() {

        public boolean apply(TaskControl input) {
            return input.getContext().getState() == Task.State.STARTED;
        }
    };

    public static TaskPredicate allTasks = new TaskPredicate() {

        public boolean apply(TaskControl control) {
            return true;
        }
    };
}
