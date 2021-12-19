/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.ExceptionHandler;
import griffon.core.ExecutorServiceManager;
import griffon.core.threading.UIThreadManager;
import griffon.exceptions.GriffonException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractUIThreadManager implements UIThreadManager {
    protected static final String ERROR_RUNNABLE_NULL = "Argument 'runnable' must not be null";
    protected static final String ERROR_CALLABLE_NULL = "Argument 'callable' must not be null";

    private ExecutorServiceManager executorServiceManager;

    @Inject @Named("defaultExecutorService")
    private ExecutorService executorService;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    public void setExecutorServiceManager(@Nonnull ExecutorServiceManager executorServiceManager) {
        requireNonNull(executorServiceManager, "Argument 'executorServiceManager' must not be null");
        if (this.executorServiceManager != null) {
            this.executorServiceManager.remove(executorService);
        }
        this.executorServiceManager = executorServiceManager;
        this.executorServiceManager.add(executorService);
    }

    /**
     * Executes a code block as a Future on an ExecutorService.
     *
     * @param callable a code block to be executed
     *
     * @return a Future that contains the result of the execution
     */
    @Nonnull
    @Override
    public <R> Future<R> runFuture(@Nonnull Callable<R> callable) {
        requireNonNull(callable, ERROR_CALLABLE_NULL);
        return runFuture(executorService, callable);
    }

    /**
     * Executes a code block as a Future on an ExecutorService.
     *
     * @param executorService the ExecutorService to use. Will use the default ExecutorService if null.
     * @param callable        a code block to be executed
     *
     * @return a Future that contains the result of the execution
     */
    @Nonnull
    @Override
    public <R> Future<R> runFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        requireNonNull(executorService, "Argument 'executorService' must not be null");
        requireNonNull(callable, ERROR_CALLABLE_NULL);
        return executorService.submit(callable);
    }

    @Override
    public void runOutsideUI(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        if (!isUIThread()) {
            runnable.run();
        } else {
            executorService.submit(() -> {
                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    exceptionHandler.uncaughtException(Thread.currentThread(), throwable);
                }
            });
        }
    }

    @Override
    public void runOutsideUIAsync(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);

        executorService.submit(() -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                exceptionHandler.uncaughtException(Thread.currentThread(), throwable);
            }
        });
    }

    @Nullable
    @Override
    public <R> R runInsideUISync(@Nonnull Callable<R> callable) {
        requireNonNull(callable, ERROR_CALLABLE_NULL);
        FutureTask<R> ft = new FutureTask<>(callable);
        runInsideUISync(ft);
        try {
            return ft.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new GriffonException("An error occurred while executing a task inside the UI thread", e);
        }
    }

    @Nonnull
    protected ExecutorServiceManager getExecutorServiceManager() {
        return executorServiceManager;
    }

    @Nonnull
    protected ExecutorService getExecutorService() {
        return executorService;
    }

    @Nonnull
    protected ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
}
