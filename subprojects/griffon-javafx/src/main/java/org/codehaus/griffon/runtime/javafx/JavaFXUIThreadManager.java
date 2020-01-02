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
package org.codehaus.griffon.runtime.javafx;

import griffon.annotations.core.Nonnull;
import javafx.application.Platform;
import org.codehaus.griffon.runtime.core.threading.AbstractUIThreadManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static java.util.Objects.requireNonNull;

/**
 * @author Dean Iverson
 */
public class JavaFXUIThreadManager extends AbstractUIThreadManager {
    /**
     * True if the current thread is the UI thread.
     */
    public boolean isUIThread() {
        return Platform.isFxApplicationThread();
    }

    @Override
    public void executeInsideUIAsync(@Nonnull Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        Platform.runLater(runnable);
    }

    @Override
    public void executeInsideUISync(@Nonnull final Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        if (isUIThread()) {
            runnable.run();
        } else {
            FutureTask<Void> task = new FutureTask<>(() -> {
                try {
                    runnable.run();
                } catch (Throwable throwable) {
                    getExceptionHandler().uncaughtException(Thread.currentThread(), throwable);
                }
            }, null);

            Platform.runLater(task);
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                getExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }
}
