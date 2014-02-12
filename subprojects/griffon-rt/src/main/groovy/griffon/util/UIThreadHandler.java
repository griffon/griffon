/*
 * Copyright 2009-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util;

/**
 * Manages execution of code in an specific thread.<p>
 * On Swing/AWT applications this would be the EDT. Other toolkits
 * may use a different thread.
 *
 * @author Andres Almiray
 */
public interface UIThreadHandler {
    /**
     * True if the current thread is the UI thread.
     */
    boolean isUIThread();

    /**
     * Executes a code block asynchronously on the UI thread.
     */
    void executeAsync(Runnable runnable);

    /**
     * Executes a code block synchronously on the UI thread.
     */
    void executeSync(Runnable runnable);

    /**
     * Executes a code block outside of the UI thread.
     */
    void executeOutside(Runnable runnable);
}
