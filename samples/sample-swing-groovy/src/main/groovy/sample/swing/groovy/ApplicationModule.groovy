/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package sample.swing.groovy

import griffon.core.LifecycleHandler
import griffon.core.env.Lifecycle
import griffon.core.injection.Module
import griffon.annotations.inject.DependsOn
import griffon.swing.SwingWindowDisplayHandler
import org.codehaus.griffon.runtime.core.LifecycleHandlerProvider
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.codehaus.griffon.runtime.util.bundles.ResourceBundleProvider
import org.kordamp.jipsy.annotations.ServiceProviderFor

import static griffon.util.AnnotationUtils.named

@DependsOn('swing')
@ServiceProviderFor(Module.class)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(SwingWindowDisplayHandler.class)
            .withClassifier(named('defaultWindowDisplayHandler'))
            .to(CenteringWindowDisplayHandler.class)
            .asSingleton()

        bind(ResourceBundle.class)
            .withClassifier(named('applicationResourceBundle'))
            .toProvider(new ResourceBundleProvider('sample.swing.groovy.Config'))
            .asSingleton()

        for (Lifecycle lifecycle : Lifecycle.values()) {
            bind(LifecycleHandler.class)
                .withClassifier(named(lifecycle.getName()))
                .toProvider(new LifecycleHandlerProvider('sample.swing.groovy.' + lifecycle.getName()))
                .asSingleton()
        }
    }
}
