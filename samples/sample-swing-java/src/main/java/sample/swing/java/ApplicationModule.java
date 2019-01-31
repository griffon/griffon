/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package sample.swing.java;

import griffon.annotations.inject.DependsOn;
import griffon.core.LifecycleHandler;
import griffon.core.env.Lifecycle;
import griffon.core.injection.Module;
import griffon.swing.SwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.LifecycleHandlerProvider;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.util.ResourceBundleProvider;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.ResourceBundle;

import static griffon.util.AnnotationUtils.named;

@DependsOn("swing")
@ServiceProviderFor(Module.class)
public class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(SwingWindowDisplayHandler.class)
            .withClassifier(named("defaultWindowDisplayHandler"))
            .to(CenteringWindowDisplayHandler.class)
            .asSingleton();

        bind(ResourceBundle.class)
            .withClassifier(named("applicationResourceBundle"))
            .toProvider(new ResourceBundleProvider("sample.swing.java.Config"))
            .asSingleton();

        for (Lifecycle lifecycle : Lifecycle.values()) {
            bind(LifecycleHandler.class)
                .withClassifier(named(lifecycle.getName()))
                .toProvider(new LifecycleHandlerProvider("sample.swing.java." + lifecycle.getName()))
                .asSingleton();
        }
    }
}
