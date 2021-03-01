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
package org.codehaus.griffon.runtime.swing.groovy;

import griffon.annotations.inject.DependsOn;
import griffon.builder.swing.SwingBuilderCustomizer;
import griffon.core.injection.Module;
import griffon.swing.SwingWindowDisplayHandler;
import griffon.util.groovy.BuilderCustomizer;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(Module.class)
@DependsOn("swing")
@Named("swing-groovy")
public class SwingBuilderModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(BuilderCustomizer.class)
            .to(SwingBuilderCustomizer.class)
            .asSingleton();
        bind(SwingWindowDisplayHandler.class)
            .withClassifier(named("windowDisplayHandler"))
            .to(GroovyAwareConfigurableSwingWindowDisplayHandler.class)
            .asSingleton();
        // end::bindings[]
    }
}
