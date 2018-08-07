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
package org.codehaus.griffon.runtime.lanterna3;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.TextGUIThreadFactory;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.WindowPostRenderer;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.TerminalFactory;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionManager;
import griffon.core.injection.Module;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.lanterna3.LanternaWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.lanterna3.controller.LanternaActionFactory;
import org.codehaus.griffon.runtime.lanterna3.controller.LanternaActionManager;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
@Named("lanterna")
@ServiceProviderFor(Module.class)
public class LanternaModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(TerminalFactory.class)
            .toProvider(TerminalFactoryProvider.class)
            .asSingleton();

        bind(Screen.class)
            .toProvider(ScreenProvider.class)
            .asSingleton();

        bind(TextGUIThreadFactory.class)
            .toProvider(TextGUIThreadFactoryProvider.class)
            .asSingleton();

        bind(com.googlecode.lanterna.gui2.WindowManager.class)
            .toProvider(WindowManagerProvider.class)
            .asSingleton();

        bind(WindowPostRenderer.class)
            .toProvider(WindowPostRendererProvider.class)
            .asSingleton();

        bind(Component.class)
            .withClassifier(named("background"))
            .toProvider(BackgroundProvider.class)
            .asSingleton();

        bind(WindowBasedTextGUI.class)
            .toProvider(WindowBasedTextGUIProvider.class)
            .asSingleton();

        bind(LanternaWindowDisplayHandler.class)
            .withClassifier(named("defaultWindowDisplayHandler"))
            .to(DefaultLanternaWindowDisplayHandler.class)
            .asSingleton();

        bind(LanternaWindowDisplayHandler.class)
            .withClassifier(named("windowDisplayHandler"))
            .to(ConfigurableLanternaWindowDisplayHandler.class)
            .asSingleton();

        bind(WindowManager.class)
            .to(DefaultLanternaWindowManager.class)
            .asSingleton();

        bind(UIThreadManager.class)
            .to(LanternaUIThreadManager.class)
            .asSingleton();

        bind(ActionManager.class)
            .to(LanternaActionManager.class)
            .asSingleton();

        bind(ActionFactory.class)
            .to(LanternaActionFactory.class)
            .asSingleton();
        // end::bindings[]
    }
}
