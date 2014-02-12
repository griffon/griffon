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

package org.codehaus.griffon.runtime.util;

import griffon.util.GriffonExceptionHandler;
import griffon.util.UIThreadHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base implementation of {@code UIThreadHandler}
 *
 * @author Andres Almiray
 */
public abstract class AbstractUIThreadHandler implements UIThreadHandler {
    protected static final ExecutorService DEFAULT_EXECUTOR_SERVICE = ExecutorServiceHolder.add(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    private static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = new GriffonExceptionHandler();

    public void executeOutside(final Runnable runnable) {
        if (!isUIThread()) {
            runnable.run();
        } else {
            DEFAULT_EXECUTOR_SERVICE.submit(new Runnable() {
                public void run() {
                    try {
                        runnable.run();
                    } catch (Throwable throwable) {
                        UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), throwable);
                    }
                }
            });
        }
    }
}
