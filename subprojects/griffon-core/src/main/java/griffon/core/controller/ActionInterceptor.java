/*
 * Copyright 2010-2014 the original author or authors.
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

package griffon.core.controller;

import griffon.core.artifact.GriffonController;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public interface ActionInterceptor {
    @Nonnull
    List<String> dependsOn();

    /**
     * Inspect the action during the applicationConfiguration phase.
     * <p/>
     * This is the perfect time to search for annotations or any other information
     * required by the action. Interceptors have the option to cache such inspections
     * and recall them during {@code before()}, {@code after()} and {@code exception()}.
     *
     * @param controller the controller that owns the action
     * @param actionName the action's name
     * @param method     the method that represents the action itself
     */
    void configure(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Method method);

    /**
     * Called before an action is executed.
     * <p/>
     * Implementors have the choice of throwing an {@code AbortActionExecution} in
     * order to signal that the action should not be invoked. In any case this method
     * returns the arguments to be sent to the action, thus allowing the interceptor
     * to modify the arguments as it deem necessary. Failure to return an appropriate
     * value will most likely cause an error during the action's execution.
     *
     * @param controller the controller that owns the action
     * @param actionName the action's name
     * @param args       the action's arguments
     * @return arguments to be sent to the action
     * @throws AbortActionExecution if action execution should be aborted.
     */
    @Nonnull
    Object[] before(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args);

    /**
     * Called after the action has been aborted or executed, even if an exception
     * occurred during execution.
     * <p/>
     *
     * @param status     a flag that indicates the execution status of the action
     * @param controller the controller that owns the action
     * @param actionName the action's name
     * @param args       the arguments sent to the action
     */
    void after(@Nonnull ActionExecutionStatus status, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args);

    /**
     * Called after the action has been executed when an exception occurred
     * during execution.
     * <p/>
     * The exception will be rethrown by the ActionManager if is not handled by
     * any interceptor.
     *
     * @param exception  the exception thrown during the action's execution
     * @param controller the controller that owns the action
     * @param actionName the action's name
     * @param args       the arguments sent to the action during execution
     * @return <code>true</code> if the exception was handled successfully,
     *         <code>false</code> otherwise.
     */
    boolean exception(@Nonnull Exception exception, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args);
}
