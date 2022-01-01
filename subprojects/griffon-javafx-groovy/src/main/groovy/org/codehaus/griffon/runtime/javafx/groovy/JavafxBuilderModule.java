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
package org.codehaus.griffon.runtime.javafx.groovy;

import griffon.annotations.inject.DependsOn;
import griffon.builder.javafx.JavafxBuilderCustomizer;
import griffon.core.injection.Module;
import griffon.javafx.JavaFXWindowDisplayHandler;
import griffon.core.util.groovy.BuilderCustomizer;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(Module.class)
@DependsOn("javafx")
@Named("javafx-groovy")
public class JavafxBuilderModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(BuilderCustomizer.class)
            .to(JavafxBuilderCustomizer.class)
            .asSingleton();
        bind(JavaFXWindowDisplayHandler.class)
            .withClassifier(named("windowDisplayHandler"))
            .to(GroovyAwareConfigurableJavaFXWindowDisplayHandler.class)
            .asSingleton();
        // end::bindings[]
    }
}
