/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package org.codehaus.griffon.runtime.swing;

import griffon.core.addon.GriffonAddon;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionManager;
import griffon.core.injection.Module;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.swing.SwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.swing.controller.SwingActionFactory;
import org.codehaus.griffon.runtime.swing.controller.SwingActionManager;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("swing")
@ServiceProviderFor(Module.class)
public class SwingModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(SwingWindowDisplayHandler.class)
            .withClassifier(named("defaultWindowDisplayHandler"))
            .to(DefaultSwingWindowDisplayHandler.class)
            .asSingleton();

        bind(SwingWindowDisplayHandler.class)
            .withClassifier(named("windowDisplayHandler"))
            .to(ConfigurableSwingWindowDisplayHandler.class)
            .asSingleton();

        bind(WindowManager.class)
            .to(DefaultSwingWindowManager.class)
            .asSingleton();

        bind(UIThreadManager.class)
            .to(SwingUIThreadManager.class)
            .asSingleton();

        bind(ActionManager.class)
            .to(SwingActionManager.class)
            .asSingleton();

        bind(ActionFactory.class)
            .to(SwingActionFactory.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(SwingAddon.class)
            .asSingleton();
        // end::bindings[]
    }
}
