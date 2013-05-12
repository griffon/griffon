/*
 * Copyright 2010-2013 the original author or authors.
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

import griffon.core.ApplicationHandler;
import griffon.core.GriffonController;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public interface GriffonControllerActionInterceptor extends ApplicationHandler {
    /**
     * Inspect the action during the configuration phase.
     * <p/>
     * This is the perfect time to search for annotations or any other information
     * required by the action. Interceptors have the option to cache such inspections
     * and recall them during {@code before()}, {@code after()} and {@code exception()}.
     *
     * @param controller the controller that owns the action
     * @param actionName the action's name
     * @param method     the method that represents the action itself
     */
    void configure(GriffonController controller, String actionName, Method method);

    /**
     * Inspect the action during the configuration phase.
     * <p/>
     * This is the perfect time to search for annotations or any other information
     * required by the action. Interceptors have the option to cache such inspections
     * and recall them during {@code before()}, {@code after()} and {@code exception()}.
     *
     * @param controller the controller that owns the action
     * @param actionName the action's name
     * @param closure    the closure that represents the action itself
     */
    void configure(GriffonController controller, String actionName, Field closure);

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
    Object[] before(GriffonController controller, String actionName, Object[] args);

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
    void after(ActionExecutionStatus status, GriffonController controller, String actionName, Object[] args);

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
    boolean exception(Exception exception, GriffonController controller, String actionName, Object[] args);
}
