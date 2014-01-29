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

import java.util.List;


/**
 * A long-running task.
 * <p/>
 * The contract is this of {@link griffon.util.UIThreadWorker} with the difference that in case
 * of an execution exception the {@link #failed(Throwable)} method is invoked and {@link #done(Object)}
 * is not.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 19.07.11 22:13
 */
public interface Task<V, C> {
    /**
     * A id that identifies this task (or a group of tasks). This doesn't have
     * to be unique among all tasks. It's used to register {@link TaskListener}
     * on this id  that will receive events solely for tasks with a certain id.
     *
     * @return
     */

    String getId();

    /**
     * This method implements the long-running work and returns a result. This
     * method is invoked on a worker thread.
     *
     * @param tracker to publish intermediate results
     * @return
     * @throws Exception
     */

    V execute(Tracker<C> tracker) throws Exception;

    /**
     * This method is invoked once {@link #execute(Tracker)} has successfully finished
     * and is called on the UI thread.
     *
     * @param value
     */
    void done(V value);

    /**
     * This method is invoked if {@link #execute(Tracker)} threw an exception. This method
     * is executed on the UI thread.
     *
     * @param cause
     */
    void failed(Throwable cause);

    /**
     * Calls going to {@link Tracker#publish(Object[])} from within
     * {@link #execute(Tracker)} are routed to this method. This method
     * is invoked on the UI thread.
     *
     * @param chunks
     */
    void process(List<C> chunks);


    Mode getMode();

    /**
     * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
     * @since 20.07.11 00:27
     */
    enum State {
        PENDING(false),
        STARTED(false),
        DONE(true),
        CANCELLED(true),
        FAILED(true);

        private final boolean finalState;

        State(boolean finalState) {
            this.finalState = finalState;
        }

        public boolean isFinalState() {
            return finalState;
        }
    }

    /**
     * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
     * @since 19.07.11 22:45
     */
    enum Mode {
        /**
         * Like {@link #BACKGROUND} but this task will not
         * throw any events during execution.
         */
        SILENT,

        /**
         * Executed in the background. The ui is still
         * responsive.
         */
        BACKGROUND,

        /**
         * Blocks the whole UI while the tasks is executed.
         */
        BLOCKING_APPLICATION,

        /**
         * Blocks a single window while the tasks is executed.
         */
        BLOCKING_WINDOW
    }
}
