/*
 * Copyright 2008-2016 the original author or authors.
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

import griffon.core.threading.ThreadingHandler;
import griffon.core.threading.UIThreadManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.concurrent.Callable;
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
    private UIThreadManager uiThreadManager;

    @Inject
    public void setUIThreadManager(@Nonnull UIThreadManager uiThreadManager) {
        this.uiThreadManager = requireNonNull(uiThreadManager, "Argument 'uiThreadManager' must not be null");
    }

    public boolean isUIThread() {
        return uiThreadManager.isUIThread();
    }

    public void runInsideUIAsync(@Nonnull Runnable runnable) {
        uiThreadManager.runInsideUIAsync(runnable);
    }

    public void runInsideUISync(@Nonnull Runnable runnable) {
        uiThreadManager.runInsideUISync(runnable);
    }

    public void runOutsideUI(@Nonnull Runnable runnable) {
        uiThreadManager.runOutsideUI(runnable);
    }

    @Nonnull
    public <R> Future<R> runFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        return uiThreadManager.runFuture(executorService, callable);
    }

    @Nonnull
    public <R> Future<R> runFuture(@Nonnull Callable<R> callable) {
        return uiThreadManager.runFuture(callable);
    }

    @Nullable
    @Override
    public <R> R runInsideUISync(@Nonnull Callable<R> callable) {
        return uiThreadManager.runInsideUISync(callable);
    }
}
