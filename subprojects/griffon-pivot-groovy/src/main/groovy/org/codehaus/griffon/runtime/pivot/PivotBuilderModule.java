/*
 * Copyright 2008-2016 the original author or authors.
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

import griffon.builder.pivot.PivotBuilderCustomizer;
import griffon.core.injection.Module;
import griffon.inject.DependsOn;
import griffon.pivot.PivotWindowDisplayHandler;
import griffon.util.BuilderCustomizer;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(Module.class)
@DependsOn("pivot")
@Named("pivot-groovy")
public class PivotBuilderModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(BuilderCustomizer.class)
            .to(PivotBuilderCustomizer.class)
            .asSingleton();
        bind(PivotWindowDisplayHandler.class)
            .withClassifier(named("windowDisplayHandler"))
            .to(GroovyAwareConfigurablePivotWindowDisplayHandler.class)
            .asSingleton();
        // end::bindings[]
    }
}
