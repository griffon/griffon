/*
 * Copyright 2010-2012 the original author or authors.
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
package griffon.core;

import groovy.lang.Closure;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Base contract for classes that can perform tasks in different threads following
 * the conventions set by the application.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public interface ThreadingHandler {
    /**
     * True if the current thread is the UI thread.
     */
    boolean isUIThread();

    /**
     * Executes a code block asynchronously on the UI thread.
     */
    void execInsideUIAsync(Runnable runnable);

    /**
     * Executes a code block synchronously on the UI thread.
     */
    void execInsideUISync(Runnable runnable);

    /**
     * Executes a code block outside of the UI thread.
     */
    void execOutsideUI(Runnable runnable);

    /**
     * Executes a code block as a Future on an ExecutorService.
     */
    Future execFuture(ExecutorService executorService, Closure closure);

    /**
     * Executes a code block as a Future on a default ExecutorService.
     */
    Future execFuture(Closure closure);

    /**
     * Executes a code block as a Future on an ExecutorService.
     */
    Future execFuture(ExecutorService executorService, Callable callable);

    /**
     * Executes a code block as a Future on a default ExecutorService.
     */
    Future execFuture(Callable callable);
}
