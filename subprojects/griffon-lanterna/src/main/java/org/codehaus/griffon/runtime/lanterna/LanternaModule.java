/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.lanterna;

import com.googlecode.lanterna.gui.GUIScreen;
import griffon.core.controller.ActionManager;
import griffon.core.injection.Module;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.lanterna.LanternaWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.lanterna.controller.LanternaActionManager;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("lanterna")
@ServiceProviderFor(Module.class)
public class LanternaModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(GUIScreen.class)
            .toProvider(GUIScreenProvider.class)
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
        // end::bindings[]
    }
}
