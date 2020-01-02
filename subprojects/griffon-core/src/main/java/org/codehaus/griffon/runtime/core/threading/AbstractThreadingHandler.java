/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
import griffon.core.threading.ThreadingHandler;
import griffon.core.threading.UIThreadManager;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the ThreadingHandler interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractThreadingHandler implements ThreadingHandler {
    private final UIThreadManager uiThreadManager;

    protected AbstractThreadingHandler(@Nonnull UIThreadManager uiThreadManager) {
        this.uiThreadManager = requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be null");
    }

    @Override
    public boolean isUIThread() {
        return uiThreadManager.isUIThread();
    }

    @Override
    public void executeInsideUIAsync(@Nonnull Runnable runnable) {
        uiThreadManager.executeInsideUIAsync(runnable);
    }

    @Override
    public void executeInsideUISync(@Nonnull Runnable runnable) {
        uiThreadManager.executeInsideUISync(runnable);
    }

    @Override
    public void executeOutsideUI(@Nonnull Runnable runnable) {
        uiThreadManager.executeOutsideUI(runnable);
    }

    @Override
    public void executeOutsideUIAsync(@Nonnull Runnable runnable) {
        uiThreadManager.executeOutsideUIAsync(runnable);
    }

    @Nonnull
    @Override
    public <R> Future<R> executeFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        return uiThreadManager.executeFuture(executorService, callable);
    }

    @Nonnull
    @Override
    public <R> Future<R> executeFuture(@Nonnull Callable<R> callable) {
        return uiThreadManager.executeFuture(callable);
    }

    @Nullable
    @Override
    public <R> R executeInsideUISync(@Nonnull Callable<R> callable) {
        return uiThreadManager.executeInsideUISync(callable);
    }

    @Nonnull
    @Override
    public <R> CompletionStage<R> executeOutsideUIAsync(@Nonnull Callable<R> callable) {
        return uiThreadManager.executeOutsideUIAsync(callable);
    }

    @Nonnull
    @Override
    public <R> CompletionStage<R> executeInsideUIAsync(@Nonnull Callable<R> callable) {
        return uiThreadManager.executeInsideUIAsync(callable);
    }
}
