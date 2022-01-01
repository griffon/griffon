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
package org.codehaus.griffon.runtime.javafx;

import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionManager;
import griffon.core.injection.Module;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.javafx.JavaFXWindowDisplayHandler;
import griffon.javafx.support.ActionMatcher;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.javafx.controller.JavaFXActionFactory;
import org.codehaus.griffon.runtime.javafx.controller.JavaFXActionManager;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("javafx")
@ServiceProviderFor(Module.class)
public class JavaFXModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(JavaFXWindowDisplayHandler.class)
            .withClassifier(named("defaultWindowDisplayHandler"))
            .to(DefaultJavaFXWindowDisplayHandler.class)
            .asSingleton();

        bind(JavaFXWindowDisplayHandler.class)
            .withClassifier(named("windowDisplayHandler"))
            .to(ConfigurableJavaFXWindowDisplayHandler.class)
            .asSingleton();

        bind(WindowManager.class)
            .to(DefaultJavaFXWindowManager.class)
            .asSingleton();

        bind(UIThreadManager.class)
            .to(JavaFXUIThreadManager.class)
            .asSingleton();

        bind(ActionManager.class)
            .to(JavaFXActionManager.class)
            .asSingleton();

        bind(ActionFactory.class)
            .to(JavaFXActionFactory.class)
            .asSingleton();

        bind(ActionMatcher.class)
            .toInstance(ActionMatcher.DEFAULT);
        // end::bindings[]
    }
}
