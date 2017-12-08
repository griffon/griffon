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
package griffon.core.threading;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface ThreadingHandler extends javax.application.threading.ThreadingHandler {
    @Override
    void executeInsideUIAsync(@Nonnull Runnable runnable);

    @Override
    void executeInsideUISync(@Nonnull Runnable runnable);

    @Override
    void executeOutsideUI(@Nonnull Runnable runnable);

    @Override
    void executeOutsideUIAsync(@Nonnull Runnable runnable);

    @Nullable
    @Override
    <R> R executeInsideUISync(@Nonnull Callable<R> callable);

    @Nonnull
    @Override
    <R> CompletionStage<R> executeOutsideUIAsync(@Nonnull Callable<R> callable);

    @Nonnull
    @Override
    <R> CompletionStage<R> executeInsideUIAsync(@Nonnull Callable<R> callable);

    @Nonnull
    <R> Future<R> executeFuture(@Nonnull Callable<R> callable);

    @Nonnull
    <R> Future<R> executeFuture(@Nonnull ExecutorService executorService, @Nonnull Callable<R> callable);
}
