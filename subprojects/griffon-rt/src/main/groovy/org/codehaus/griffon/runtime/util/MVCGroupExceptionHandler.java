/*
 * Copyright 2008-2012 the original author or authors.
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

package org.codehaus.griffon.runtime.util;

import griffon.core.GriffonApplication;
import griffon.util.RunnableWithArgs;

/**
 * @author Andres Almiray
 * @since 0.9.4
 */
public final class MVCGroupExceptionHandler extends RunnableWithArgs {
    private final GriffonApplication application;

    public static void registerWith(GriffonApplication application) {
        new MVCGroupExceptionHandler(application);
    }

    private MVCGroupExceptionHandler(GriffonApplication application) {
        this.application = application;

        application.addApplicationEventListener("UncaughtMVCGroupConfigurationException", this);
        application.addApplicationEventListener("UncaughtMVCGroupInstantiationException", this);
    }

    public void run(Object[] args) {
        Exception exception = (Exception) args[0];
        application.getLog().error("Unrecoverable error", exception);
        // exception.printStackTrace();
        System.exit(1);
    }
}
