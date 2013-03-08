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

package org.codehaus.griffon.runtime.core.controller;

import griffon.core.GriffonApplication;
import griffon.core.GriffonController;
import griffon.core.controller.AbortActionExecution;
import griffon.core.controller.GriffonControllerActionInterceptor;
import griffon.util.ApplicationHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public abstract class AbstractGriffonControllerActionInterceptor implements GriffonControllerActionInterceptor {
    private GriffonApplication app;

    public AbstractGriffonControllerActionInterceptor() {
        this(ApplicationHolder.getApplication());
    }

    public AbstractGriffonControllerActionInterceptor(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    public void setApp(GriffonApplication app) {
        this.app = app;
    }

    @Override
    public void configure(GriffonController controller, String actionName, Method method) {
        // empty
    }

    @Override
    public void configure(GriffonController controller, String actionName, Field closure) {
        // empty
    }

    @Override
    public Object[] before(GriffonController controller, String actionName, Object[] args) {
        return args;
    }

    @Override
    public void after(GriffonController controller, String actionName, Object[] args) {
        // empty
    }

    @Override
    public boolean exception(Exception exception, GriffonController controller, String actionName, Object[] args) {
        // empty
        return false;
    }

    protected AbortActionExecution abortActionExecution() throws AbortActionExecution {
        throw new AbortActionExecution();
    }

    protected String qualifyActionName(GriffonController controller, String actionName) {
        return controller.getClass().getName() + "." + actionName;
    }
}
