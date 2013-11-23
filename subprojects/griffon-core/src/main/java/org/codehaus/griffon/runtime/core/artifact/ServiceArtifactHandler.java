/*
 * Copyright 2009-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonService;
import griffon.core.artifact.GriffonServiceClass;
import griffon.core.injection.Binding;
import griffon.core.injection.binder.AnnotatedBindingBuilder;
import griffon.core.injection.binder.LinkedBindingBuilder;
import org.codehaus.griffon.runtime.core.injection.Bindings;
import org.codehaus.griffon.runtime.core.injection.TypedImpl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Handler for 'Service' artifacts.
 *
 * @author Andres Almiray
 */
public class ServiceArtifactHandler extends AbstractArtifactHandler<GriffonService> {
    @Inject
    public ServiceArtifactHandler(@Nonnull GriffonApplication application) {
        super(application, GriffonService.class, GriffonServiceClass.TYPE, GriffonServiceClass.TRAILING);
    }

    @Nonnull
    public GriffonClass newGriffonClassInstance(@Nonnull Class<GriffonService> clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        return new DefaultGriffonServiceClass(clazz);
    }

    @Override
    protected void createBindings(@Nonnull List<Binding<?>> bindings, @Nonnull Class<GriffonService> clazz, @Nonnull GriffonClass griffonClass) {
        LinkedBindingBuilder<GriffonClass> builder1 = Bindings.bind(GriffonClass.class)
            .withClassifier(new TypedImpl(clazz));
        builder1.toInstance(griffonClass);
        bindings.add(builder1.getBinding());
        AnnotatedBindingBuilder<GriffonService> builder2 = Bindings.bind(clazz);
        builder2.asSingleton();
        bindings.add(builder2.getBinding());
    }
}