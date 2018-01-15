/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonApplication;
import griffon.core.RunnableWithArgs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class MVCGroupExceptionHandler implements RunnableWithArgs {
    private static final String UNCAUGHT_MVC_GROUP_CONFIGURATION_EXCEPTION = "UncaughtMVCGroupConfigurationException";
    private static final String UNCAUGHT_MVC_GROUP_INSTANTIATION_EXCEPTION = "UncaughtMVCGroupInstantiationException";
    private static MVCGroupExceptionHandler instance;

    private final GriffonApplication application;

    private MVCGroupExceptionHandler(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");

        application.getEventRouter().addEventListener(UNCAUGHT_MVC_GROUP_CONFIGURATION_EXCEPTION, this);
        application.getEventRouter().addEventListener(UNCAUGHT_MVC_GROUP_INSTANTIATION_EXCEPTION, this);
    }

    private void unregister(@Nonnull GriffonApplication application) {
        if (this.application == application) {
            application.getEventRouter().removeEventListener(UNCAUGHT_MVC_GROUP_CONFIGURATION_EXCEPTION, this);
            application.getEventRouter().removeEventListener(UNCAUGHT_MVC_GROUP_INSTANTIATION_EXCEPTION, this);
        }
    }

    public static void registerWith(@Nonnull GriffonApplication application) {
        if (null != instance) {
            instance.unregister(application);
        }
        instance = new MVCGroupExceptionHandler(application);
    }

    public static void unregisterFrom(@Nonnull GriffonApplication application) {
        if (null != instance) {
            instance.unregister(application);
        }
    }

    public void run(@Nullable Object... args) {
        if (args != null && args.length > 0) {
            Exception exception = (Exception) args[0];
            application.getLog().error("Unrecoverable error", exception);
            // exception.printStackTrace();
            System.exit(1);
        }
    }
}
