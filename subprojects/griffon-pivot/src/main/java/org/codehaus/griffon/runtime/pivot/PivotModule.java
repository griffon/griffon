/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.pivot;

import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionManager;
import griffon.core.injection.Module;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.pivot.PivotWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.pivot.controller.PivotActionFactory;
import org.codehaus.griffon.runtime.pivot.controller.PivotActionManager;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("pivot")
@ServiceProviderFor(Module.class)
public class PivotModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(PivotWindowDisplayHandler.class)
            .withClassifier(named("defaultWindowDisplayHandler"))
            .to(DefaultPivotWindowDisplayHandler.class)
            .asSingleton();

        bind(PivotWindowDisplayHandler.class)
            .withClassifier(named("windowDisplayHandler"))
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

        bind(ActionFactory.class)
            .to(PivotActionFactory.class)
            .asSingleton();
        // end::bindings[]
    }
}
