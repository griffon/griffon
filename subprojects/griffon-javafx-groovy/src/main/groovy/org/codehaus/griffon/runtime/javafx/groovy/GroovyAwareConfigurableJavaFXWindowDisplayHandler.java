/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package org.codehaus.griffon.runtime.javafx.groovy;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.javafx.JavaFXWindowDisplayHandler;
import groovy.lang.Closure;
import javafx.stage.Window;
import org.codehaus.griffon.runtime.javafx.ConfigurableJavaFXWindowDisplayHandler;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GroovyAwareConfigurableJavaFXWindowDisplayHandler extends ConfigurableJavaFXWindowDisplayHandler {
    @Inject
    public GroovyAwareConfigurableJavaFXWindowDisplayHandler(@Nonnull GriffonApplication application, @Nonnull @Named("defaultWindowDisplayHandler") JavaFXWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(application, delegateWindowsDisplayHandler);
    }

    @Override
    protected boolean canBeRun(@Nullable Object obj) {
        return obj instanceof Closure || super.canBeRun(obj);
    }

    @Override
    protected void run(@Nonnull Object handler, @Nonnull String name, @Nonnull Window window) {
        if (handler instanceof Closure) {
            ((Closure) handler).call(name, window);
            return;
        }
        super.run(handler, name, window);
    }
}
