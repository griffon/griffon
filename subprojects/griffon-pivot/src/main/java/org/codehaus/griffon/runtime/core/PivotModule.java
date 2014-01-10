/*
 * Copyright 2012-2014 the original author or authors.
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

import griffon.core.controller.ActionManager;
import griffon.core.injection.Module;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.pivot.PivotWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.controller.PivotActionManager;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.injection.NamedImpl;
import org.codehaus.griffon.runtime.pivot.ConfigurablePivotWindowDisplayHandler;
import org.codehaus.griffon.runtime.pivot.DefaultPivotWindowDisplayHandler;
import org.codehaus.griffon.runtime.pivot.DefaultPivotWindowManager;
import org.codehaus.griffon.runtime.pivot.PivotUIThreadManager;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("pivot")
@ServiceProviderFor(Module.class)
public class PivotModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(PivotWindowDisplayHandler.class)
            .withClassifier(new NamedImpl("defaultWindowDisplayHandler"))
            .to(DefaultPivotWindowDisplayHandler.class)
            .asSingleton();

        bind(PivotWindowDisplayHandler.class)
            .withClassifier(new NamedImpl("windowDisplayHandler"))
            .to(ConfigurablePivotWindowDisplayHandler.class)
            .asSingleton();

        bind(WindowManager.class)
            .to(DefaultPivotWindowManager.class)
            .asSingleton();

        bind(UIThreadManager.class)
            .to(PivotUIThreadManager.class)
            .asSingleton();

        bind(ActionManager.class)
            .to(PivotActionManager.class)
            .asSingleton();
    }
}
