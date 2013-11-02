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

package org.codehaus.griffon.runtime.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.InjectorFactory;
import org.codehaus.griffon.runtime.core.injection.InjectorProvider;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.inject.util.Providers.guicify;
import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.runtime.injection.GuiceInjector.moduleFromBindings;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GuiceInjectorFactory implements InjectorFactory {
    @Nonnull
    @Override
    public GuiceInjector createInjector(@Nonnull Iterable<Binding<?>> bindings) {
        requireNonNull(bindings, "Argument 'bindings' cannot be null");
        InjectorProvider injectorProvider = new InjectorProvider();
        GuiceInjector injector = createModules(injectorProvider, bindings);
        injectorProvider.setInjector(injector);
        return injector;
    }

    private GuiceInjector createModules(@Nonnull final InjectorProvider injectorProvider, @Nonnull Iterable<Binding<?>> bindings) {
        Module injectorModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Injector.class)
                    .toProvider(guicify(injectorProvider))
                    .in(Singleton.class);
            }
        };

        Collection<Module> modules = new ArrayList<>();
        modules.add(injectorModule);
        modules.add(moduleFromBindings(bindings));

        com.google.inject.Injector injector = Guice.createInjector(modules);
        return new GuiceInjector(injector);
    }
}
