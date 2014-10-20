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

import griffon.core.controller.Action;
import griffon.core.controller.ActionExecutionStatus;
import org.codehaus.griffon.runtime.core.controller.AbstractActionHandler;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

public class InvokeActionHandler extends AbstractActionHandler {
    private boolean abort;
    private boolean configure;
    private boolean update;
    private boolean before;
    private boolean after;
    private boolean exception;

    public boolean isAbort() {
        return abort;
    }

    public boolean isConfigure() {
        return configure;
    }

    public boolean isUpdate() {
        return update;
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
    public void update(@Nonnull Action action) {
        update = true;
        super.update(action);
    }

    @Override
    public void configure(@Nonnull Action action, @Nonnull Method method) {
        configure = true;
        assert action.getFullyQualifiedName().equals(action.getController().getClass().getName() + "." + action.getActionName());
        super.configure(action, method);
    }

    @Nonnull
    @Override
    public Object[] before(@Nonnull Action action, @Nonnull Object[] args) {
        if ("abort".equals(action.getActionName())) {
            abort = true;
            throw abortActionExecution();
        }
        before = true;
        return super.before(action, args);
    }

    @Override
    public void after(@Nonnull ActionExecutionStatus status, @Nonnull Action action, @Nonnull Object[] args) {
        after = true;
        super.after(status, action, args);
    }

    @Override
    public boolean exception(@Nonnull Exception exception, @Nonnull Action action, @Nonnull Object[] args) {
        this.exception = true;
        super.exception(exception, action, args);
        return action.getActionName().equals("handleException");
    }
}
