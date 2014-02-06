/*
 * Copyright 2008-2014 the original author or authors.
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
package integration;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.AbortActionExecution;
import griffon.core.controller.ActionExecutionStatus;
import org.codehaus.griffon.runtime.core.controller.AbstractActionInterceptor;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

public class InvokeActionInterceptor extends AbstractActionInterceptor {
    private boolean configure;
    private boolean before;
    private boolean after;
    private boolean exception;

    public boolean isConfigure() {
        return configure;
    }

    public boolean isBefore() {
        return before;
    }

    public boolean isAfter() {
        return after;
    }

    public boolean isException() {
        return exception;
    }

    @Override
    public void configure(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Method method) {
        configure = true;
        super.configure(controller, actionName, method);
    }

    @Nonnull
    @Override
    public Object[] before(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        before = true;
        return super.before(controller, actionName, args);
    }

    @Override
    public void after(@Nonnull ActionExecutionStatus status, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        after = true;
        super.after(status, controller, actionName, args);
    }

    @Override
    public boolean exception(@Nonnull Exception exception, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        this.exception = true;
        super.exception(exception, controller, actionName, args);
        return true;
    }
}
