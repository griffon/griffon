/*
 * Copyright 2009-2011 the original author or authors.
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

package griffon.util;

/**
 * Indicates the type of threading management for a method or property.</p>
 * The following values apply
 * <ul>
 *  <li>{@code SKIP} - no threading management will be performed.</li>
 *  <li>{@code OUTSIDE_UITHREAD} - code should be invoked outside of the UI thread.</li>
 *  <li>{@code INSIDE_UITHREAD_SYNC} - code should be invoked inside the UI thread using a synchronous call.</li>
 *  <li>{@code INSIDE_UITHREAD_ASYNC} - code should be invoked inside the UI thread using an asynchronous call.</li>
 * </ul>
 *
 * @see griffon.util.Threading
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public enum ThreadingPolicy {
    /** Skip threading injection */
    SKIP,
    /** Inject execOutside wrapper */
    OUTSIDE_UITHREAD,
    /** Inject execSync wrapper */
    INSIDE_UITHREAD_SYNC,
    /** Inject execAsync wrapper */
    INSIDE_UITHREAD_ASYNC;
}
