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
package org.codehaus.griffon.runtime.injection;

import griffon.core.GriffonApplication;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.InjectorFactory;
import griffon.core.injection.Module;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.injection.InjectorProvider;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.runtime.injection.SpringInjector.contextFromBindings;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
@ServiceProviderFor(InjectorFactory.class)
public class SpringInjectorFactory implements InjectorFactory {
    @Nonnull
    @Override
    public SpringInjector createInjector(@Nonnull GriffonApplication application, @Nonnull Iterable<Binding<?>> bindings) {
        requireNonNull(application, "Argument 'application' must not be null");
        requireNonNull(bindings, "Argument 'bindings' must not be null");
        InjectorProvider injectorProvider = new InjectorProvider();
        SpringInjector injector = createModules(application, injectorProvider, bindings);
        injectorProvider.setInjector(injector);
        injector.getDelegateInjector().init();
        return injector;
    }

    private SpringInjector createModules(final @Nonnull GriffonApplication application, @Nonnull final InjectorProvider injectorProvider, @Nonnull Iterable<Binding<?>> bindings) {
        Module injectorModule = new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(Injector.class)
                    .toProvider(injectorProvider)
                    .asSingleton();
            }
        };

        List<Binding<?>> allBindings = new ArrayList<>();
        allBindings.addAll(injectorModule.getBindings());
        for (Binding<?> b : bindings) {
            allBindings.add(b);
        }

        return new SpringInjector(contextFromBindings(application, allBindings));

        /*
        Collection<Module> modules = new ArrayList<>();
        modules.add(injectorModule);
        modules.add(moduleFromBindings(bindings));

        ServiceLoader<Module> moduleLoader = ServiceLoader.load(Module.class, getClass().getClassLoader());
        for (Module module : moduleLoader) {
            modules.add(module);
        }

        com.google.inject.Injector injector = Guice.createInjector(modules);
        return new SpringInjector(injector);
        */
    }
}
