/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
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
     */
    void runInsideUIAsync(@Nonnull Runnable runnable);

    /**
     * Executes a code block synchronously on the UI thread.
     */
    void runInsideUISync(@Nonnull Runnable runnable);

    /**
     * Executes a code block outside of the UI thread.
     */
    void runOutsideUI(@Nonnull Runnable runnable);

    /**
     * Executes a code block on a background thread, always.
     * @since 2.11.0
     */
    void runOutsideUIAsync(@Nonnull Runnable runnable);

    /**
     * Executes a code block as a Future on an ExecutorService.
     */
    @Nonnull
    <R> Future<R> runFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable);

    /**
     * Executes a code block as a Future on a default ExecutorService.
     */
    @Nonnull
    <R> Future<R> runFuture(@Nonnull Callable<R> callable);

    /**
     * Executes a code block synchronously on the UI thread.
     * @since 2.2.0
     */
    @Nullable
    <R> R runInsideUISync(@Nonnull Callable<R> callable);
}
