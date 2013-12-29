/*
 * Copyright 2008-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.CallableWithArgs;
import griffon.core.GriffonApplication;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 0.9.4
 */
public final class MVCGroupExceptionHandler implements CallableWithArgs<Void> {
    private final GriffonApplication application;

    public static void registerWith(@Nonnull GriffonApplication application) {
        new MVCGroupExceptionHandler(application);
    }

    private MVCGroupExceptionHandler(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");

        application.getEventRouter().addEventListener("UncaughtMVCGroupConfigurationException", this);
        application.getEventRouter().addEventListener("UncaughtMVCGroupInstantiationException", this);
    }

    public Void call(@Nonnull Object... args) {
        Exception exception = (Exception) args[0];
        application.getLog().error("Unrecoverable error", exception);
        // exception.printStackTrace();
        System.exit(1);
        return null;
    }
}
