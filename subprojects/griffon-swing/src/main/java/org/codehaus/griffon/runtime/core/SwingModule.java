/*
 * Copyright 2013 the original author or authors.
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

import griffon.core.addon.GriffonAddon;
import griffon.core.controller.ActionManager;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.swing.SwingAddon;
import griffon.swing.SwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.controller.SwingActionManager;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.injection.NamedImpl;
import org.codehaus.griffon.runtime.swing.ConfigurableSwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.swing.DefaultSwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.swing.DefaultSwingWindowManager;
import org.codehaus.griffon.runtime.swing.SwingUIThreadManager;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SwingModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(SwingWindowDisplayHandler.class)
            .withClassifier(new NamedImpl("defaultWindowDisplayHandler"))
            .to(DefaultSwingWindowDisplayHandler.class)
            .asSingleton();

        bind(SwingWindowDisplayHandler.class)
            .withClassifier(new NamedImpl("windowDisplayHandler"))
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

        bind(GriffonAddon.class)
            .to(SwingAddon.class)
            .asSingleton();
    }
}
