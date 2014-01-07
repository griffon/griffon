/*
 * Copyright 2007-2014 the original author or authors.
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
 */

package org.codehaus.griffon.runtime.core.controller;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.AbortActionExecution;
import griffon.core.controller.ActionExecutionStatus;
import griffon.core.controller.ActionInterceptor;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public class AbstractActionInterceptor implements ActionInterceptor {
    private static final String ERROR_CONTROLLER_NULL = "Argument 'controller' cannot be null";
    private static final String ERROR_ACTION_NAME_BLANK = "Argument 'actionName' cannot be blank";

    @Nonnull
    @Override
    public List<String> dependsOn() {
        return Collections.emptyList();
    }

    @Override
    public void configure(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Method method) {
    }

    @Nonnull
    @Override
    public Object[] before(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        return new Object[0];
    }

    @Override
    public void after(@Nonnull ActionExecutionStatus status, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
    }

    @Override
    public boolean exception(@Nonnull Exception exception, @Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Object[] args) {
        return false;
    }

    @Nonnull
    protected AbortActionExecution abortActionExecution() throws AbortActionExecution {
        throw new AbortActionExecution();
    }

    @Nonnull
    protected String qualifyActionName(@Nonnull GriffonController controller, @Nonnull String actionName) {
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        requireNonBlank(actionName, ERROR_ACTION_NAME_BLANK);
        return controller.getClass().getName() + "." + actionName;
    }
}
