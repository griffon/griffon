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

import java.util.EventListener;

// note, this is a slightly modified copy from here:
//  https://scm.ops4j.org/repos/ops4j/laboratory/users/raffael/hiveapp/trunk/hiveapp/src/main/ch/raffael/hiveapp/services/ExceptionHandler.java

/**
 * Handler for exceptions that occurred in an event listener.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ExceptionHandler<T extends EventListener> {
    ExceptionHandler RETHROW_EXCEPTION_HANDLER = new ExceptionHandler() {

        public boolean handleException(EventListener listener, Throwable exception) throws Throwable {
            throw exception;
        }
    };

    /**
     * Handle an exception that occurred in an event listener. The exception may also be
     * rethrown.
     *
     * @param listener  The listener that throw the exception.
     * @param exception The thrown exception.
     * @return <code>true</code>, if the remaining listeners should be notified,
     *         <code>false</code> to cancel.
     * @throws Throwable Rethrowing or converting exceptions.
     */
    boolean handleException(T listener, Throwable exception) throws Throwable;
}