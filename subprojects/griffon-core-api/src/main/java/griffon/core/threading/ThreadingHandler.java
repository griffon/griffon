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
package griffon.core.threading;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base contract for classes that can perform tasks in different threads following
 * the conventions set by the application.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface ThreadingHandler {
    /**
     * True if the current thread is the UI thread.
     */
    boolean isUIThread();

    /**
     * Executes a code block asynchronously on the UI thread.
     *
     * @param runnable block of code that must be executed. Must not be {@code null}.
     */
    void executeInsideUIAsync(@Nonnull Runnable runnable);

    /**
     * Executes a code block synchronously on the UI thread.
     *
     * @param runnable block of code that must be executed. Must not be {@code null}.
     */
    void executeInsideUISync(@Nonnull Runnable runnable);

    /**
     * Executes a code block synchronously on the UI thread.
     *
     * @param callable block of code that must be executed. Must not be {@code null}.
     *
     * @return return value from the executed block. May be {@code null}.
     */
    @Nullable
    <R> R executeInsideUISync(@Nonnull Callable<R> callable);

    /**
     * Executes a code block outside of the UI thread.
     * The {@code runnable} will be invoked on the same thread as the caller if the caller
     * is already outside the UI thread.
     *
     * @param runnable block of code that must be executed. Must not be {@code null}.
     */
    void executeOutsideUI(@Nonnull Runnable runnable);

    /**
     * Executes a code block on a background thread, always.
     * The {@code runnable} will be invoked on a different thread regardless of the thread
     * where the caller issued the call.
     *
     * @param runnable block of code that must be executed. Must not be {@code null}.
     */
    void executeOutsideUIAsync(@Nonnull Runnable runnable);

    /**
     * Executes a code block on a background thread, always.
     * The {@code callable} will be invoked on a different thread regardless of the thread
     * where the caller issued the call.
     *
     * @param callable block of code that must be executed. Must not be {@code null}.
     *
     * @return a {@code CompletionStage} that can be used to signal the resolution or rejection of the code block. Never returns {@code null}.
     */
    @Nonnull
    <R> CompletionStage<R> executeOutsideUIAsync(@Nonnull Callable<R> callable);

    /**
     * Executes a code block asynchronously on the UI thread.
     *
     * @param callable block of code that must be executed. Must not be {@code null}.
     *
     * @return a {@code CompletionStage} that can be used to signal the resolution or rejection of the code block. Never returns {@code null}.
     */
    @Nonnull
    <R> CompletionStage<R> executeInsideUIAsync(@Nonnull Callable<R> callable);

    /**
     * Executes a code block as a Future on a default ExecutorService.
     */
    @Nonnull
    <R> Future<R> executeFuture(@Nonnull Callable<R> callable);

    /**
     * Executes a code block as a Future on an ExecutorService.
     */
    @Nonnull
    <R> Future<R> executeFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable);
}
