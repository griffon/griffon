/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonApplication;
import griffon.core.LifecycleHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code LifecycleHandler} interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractLifecycleHandler implements LifecycleHandler {
    private final GriffonApplication application;

    @Inject
    public AbstractLifecycleHandler(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
    }

    @Nonnull
    protected GriffonApplication getApplication() {
        return application;
    }

    @Override
    public boolean isUIThread() {
        return application.getUIThreadManager().isUIThread();
    }

    @Override
    @Nonnull
    public <R> Future<R> executeFuture(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeFuture(callable);
    }

    @Override
    @Nonnull
    public <R> Future<R> executeFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeFuture(executorService, callable);
    }

    @Override
    public void executeInsideUIAsync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeInsideUIAsync(runnable);
    }

    @Override
    public void executeInsideUISync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeInsideUISync(runnable);
    }

    @Override
    public void executeOutsideUI(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeOutsideUI(runnable);
    }

    @Override
    public void executeOutsideUIAsync(@Nonnull Runnable runnable) {
        application.getUIThreadManager().executeOutsideUIAsync(runnable);
    }

    @Nullable
    @Override
    public <R> R executeInsideUISync(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeInsideUISync(callable);
    }

    @Nonnull
    @Override
    public <R> CompletionStage<R> executeOutsideUIAsync(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeOutsideUIAsync(callable);
    }

    @Nonnull
    @Override
    public <R> CompletionStage<R> executeInsideUIAsync(@Nonnull Callable<R> callable) {
        return application.getUIThreadManager().executeInsideUIAsync(callable);
    }
}
