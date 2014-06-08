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
 * Set intermediate results from within a long-running task.
 * <p/>
 * Calls to setters will result in an {@link ChangeEvent} and any registered
 * {@link TaskListener} is notified.
 * <p/>
 * Calls to {@link #publish(Object[])} are routed back to {@link Task#process(java.util.List)}
 * which is executed on the UI thread. See {@link griffon.util.UIThreadWorker} for details.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 19.07.11 22:16
 */
public interface Tracker<C> {
    /**
     * Set the current progress. {@code progress} must be a value
     * from 0 to 100 (inclusive).
     *
     * @param progress
     */
    void setProgress(int progress);

    /**
     * Set the current progress.
     *
     * @param min
     * @param max
     * @param current
     */
    void setProgress(int min, int max, int current);

    void setPhase(String phase);

    void publish(C... chunks);

}
