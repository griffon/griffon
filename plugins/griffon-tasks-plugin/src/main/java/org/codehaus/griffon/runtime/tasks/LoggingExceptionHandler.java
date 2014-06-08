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

import griffon.plugins.tasks.ExceptionHandler;
import org.slf4j.Logger;

import java.util.EventListener;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 01.04.11 22:17
 */
public class LoggingExceptionHandler<T extends EventListener> implements ExceptionHandler<T> {
    private final Logger log;
    private boolean notifyOtherListeners = true;

    public LoggingExceptionHandler(Logger log) {
        this.log = log;
        this.notifyOtherListeners = true;
    }

    public LoggingExceptionHandler(Logger log, boolean notifyOtherListeners) {
        this.log = log;
        this.notifyOtherListeners = notifyOtherListeners;
    }

    public boolean isNotifyOtherListeners() {
        return notifyOtherListeners;
    }

    public void setNotifyOtherListeners(boolean notifyOtherListeners) {
        this.notifyOtherListeners = notifyOtherListeners;
    }

    public static <T extends EventListener> LoggingExceptionHandler<T> newInstance(Logger log) {
        return new LoggingExceptionHandler<T>(log);
    }

    public boolean handleException(T listener, Throwable t) throws Throwable {
        log.error("Listener '" + listener + "' threw an exception: " + t.getMessage(), t);
        return notifyOtherListeners;
    }
}
