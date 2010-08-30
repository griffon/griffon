/*
 * Copyright 2009-2010 the original author or authors.
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
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

/**
 *
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public interface GriffonArtifact {
    /**
     * Returns the reference to the current application.
     */
    GriffonApplication getApp();
    
    /**
     * Creates a new instance of the specified class and type.
     */
    Object newInstance(Class clazz, String type);

    /**
     * Returns the <tt>GriffonClass</tt> associated with this artifact.
     */
    GriffonClass getGriffonClass();

    /**
     * True if the current thread is the UI thread.
     */
    boolean isUIThread();

    /**
     * Executes a code block asynchronously on the UI thread.
     */
    void execAsync(Runnable runnable);

    /**
     * Executes a code block synchronously on the UI thread.
     */
    void execSync(Runnable runnable);

    /**
     * Executes a code block outside of the UI thread.
     */
    void execOutside(Runnable runnable);

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