/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.core.threading;

import griffon.core.ExecutorServiceManager;
import griffon.core.GriffonExceptionHandler;
import griffon.core.threading.UIThreadManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractUIThreadManager implements UIThreadManager {
    protected static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' cannot be bull";
    protected static final String ERROR_CALLABLE_NULL = "Argument 'callable' cannot be null";

    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = new GriffonExceptionHandler();

    private ExecutorServiceManager executorServiceManager;

    @Inject
    public void setExecutorServiceManager(@Nonnull ExecutorServiceManager executorServiceManager) {
        requireNonNull(executorServiceManager, "Argument 'executorServiceManager' cannot be bull");
        if (this.executorServiceManager != null) {
            this.executorServiceManager.remove(DEFAULT_EXECUTOR_SERVICE);
        }
        this.executorServiceManager = executorServiceManager;
        this.executorServiceManager.add(DEFAULT_EXECUTOR_SERVICE);
    }

    /**
     * Executes a code block as a Future on an ExecutorService.
     *
     * @param callable a code block to be executed
     * @return a Future that contains the result of the execution
     */
    @Nonnull
    public <R> Future<R> runFuture(@Nonnull Callable<R> callable) {
        requireNonNull(callable, ERROR_CALLABLE_NULL);
        return runFuture(DEFAULT_EXECUTOR_SERVICE, callable);
    }

    /**
     * Executes a code block as a Future on an ExecutorService.
     *
     * @param executorService the ExecutorService to use. Will use the default ExecutorService if null.
     * @param callable        a code block to be executed
     * @return a Future that contains the result of the execution
     */
    @Nonnull
    public <R> Future<R> runFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        requireNonNull(executorService, "Argument 'executorService' cannot be null");
        requireNonNull(callable, ERROR_CALLABLE_NULL);
        return executorService.submit(callable);
    }

    public void runOutsideUI(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
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
