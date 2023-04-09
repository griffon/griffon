/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.core.action;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.lang.reflect.Method;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface ActionInterceptor {
    /**
     * Update the action's properties.
     *
     * @param action the action to be updated. Must not be {@code null}.
     */
    void update(Action action);

    /**
     * Inspect the action during the configuration phase.
     * <p/>
     * This is the perfect time to search for annotations or any other information
     * required by the action. interceptors have the option to cache such inspections
     * and recall them during {@code before()}, {@code after()} and {@code exception()}.
     *
     * @param action the action to be configured. Must not be {@code null}.
     * @param method the method that represents the action itself. Must not be {@code null}.
     */
    void configure(@Nonnull Action action, @Nonnull Method method);

    /**
     * Called before an action is executed.
     * <p>
     * Implementors have the choice of throwing an {@code AbortActionExecution} in
     * order to signal that the action should not be invoked. In any case this method
     * returns the arguments to be sent to the action, thus allowing the action interceptor
     * to modify the arguments as it deem necessary. Failure to return an appropriate
     * value will most likely cause an error during the action's execution.
     *
     * @param action the action to execute. Must not be {@code null}.
     * @param args   the action's arguments. Must not be {@code null}.
     * @return arguments to be sent to the action
     * @throws AbortActionExecution if action execution should be aborted.
     */
    @Nonnull
    Object[] before(@Nonnull Action action, @Nonnull Object[] args);

    /**
     * Called after the action has been aborted or executed, even if an exception
     * occurred during execution.
     * <p/>
     *
     * @param status a flag that indicates the execution status of the action. Must not be {@code null}.
     * @param action the action to execute. Must not be {@code null}.
     * @param args   the arguments sent to the action. Must not be {@code null}.
     * @param result the result of executing the action. May be {@code null}.
     */
    @Nullable
    Object after(@Nonnull ActionExecutionStatus status, @Nonnull Action action, @Nonnull Object[] args, @Nullable Object result);

    /**
     * Called after the action has been executed, when an exception occurred
     * during execution.
     * <p>
     * The exception must be rethrown if was not handled by any action interceptor.
     *
     * @param exception the exception thrown during the action's execution. Must not be {@code null}.
     * @param action    the action to execute. Must not be {@code null}.
     * @param args      the arguments sent to the action during execution. Must not be {@code null}.
     * @return {@code true} if the exception was handled successfully,
     * {@code false} otherwise.
     */
    boolean exception(@Nonnull Exception exception, @Nonnull Action action, @Nonnull Object[] args);
}
