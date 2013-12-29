/*
 * Copyright 2009-2014 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.core.injection.Binding;
import griffon.core.injection.InjectorFactory;
import org.codehaus.griffon.runtime.core.injection.InjectorProvider;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class WeldInjectorFactory implements InjectorFactory {
    @Nonnull
    @Override
    public WeldInjector createInjector(@Nonnull GriffonApplication application, @Nonnull Iterable<Binding<?>> bindings) {
        requireNonNull(application, "Argument 'application' cannot be null");
        requireNonNull(bindings, "Argument 'bindings' cannot be null");
        InjectorProvider injectorProvider = new InjectorProvider();
        WeldInjector injector = createModules(application, injectorProvider, bindings);
        injectorProvider.setInjector(injector);
        return injector;
    }

    private WeldInjector createModules(final @Nonnull GriffonApplication application, @Nonnull final InjectorProvider injectorProvider, @Nonnull Iterable<Binding<?>> bindings) {
        /*
        final InjectionListener<GriffonArtifact> injectionListener = new InjectionListener<GriffonArtifact>() {
            @Override
            public void afterInjection(GriffonArtifact injectee) {
                application.getEventRouter().publish(
                    ApplicationEvent.NEW_INSTANCE.getName(),
                    asList(injectee.getClass(), injectee)
                );
            }
        };

        final InjectionListener<Object> postConstructorInjectorListener = new InjectionListener<Object>() {
            @Override
            public void afterInjection(Object injectee) {
                List<Method> postConstructMethods = new ArrayList<>();
                Class klass = injectee.getClass();
                while (klass != null) {
                    for (Method method : klass.getDeclaredMethods()) {
                        if (method.getAnnotation(PostConstruct.class) != null &&
                            method.getParameterTypes().length == 0 &&
                            Modifier.isPublic(method.getModifiers())) {
                            postConstructMethods.add(method);
                        }
                    }

                    klass = klass.getSuperclass();
                }

                for (Method method : postConstructMethods) {
                    try {
                        method.invoke(injectee);
                    } catch (IllegalAccessException e) {
                        sanitize(e).printStackTrace();
                    } catch (InvocationTargetException e) {
                        sanitize(e.getTargetException()).printStackTrace();
                    }
                }
            }
        };

        Module injectorModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Injector.class)
                    .toProvider(guicify(injectorProvider))
                    .in(Singleton.class);

                bindListener(new AbstractMatcher<TypeLiteral<?>>() {
                                 public boolean matches(TypeLiteral<?> typeLiteral) {
                                     return GriffonArtifact.class.isAssignableFrom(typeLiteral.getRawType());
                                 }
                             }, new TypeListener() {
                                 @Override
                                 public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                                     if (GriffonArtifact.class.isAssignableFrom(type.getRawType())) {
                                         TypeEncounter<GriffonArtifact> artifactEncounter = (TypeEncounter<GriffonArtifact>) encounter;
                                         artifactEncounter.register(injectionListener);
                                     }
                                 }
                             }
                );

                bindListener(Matchers.any(), new TypeListener() {
                    @Override
                    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                        encounter.register(postConstructorInjectorListener);
                    }
                });
            }
        };

        Collection<Module> modules = new ArrayList<>();
        modules.add(injectorModule);
        modules.add(moduleFromBindings(bindings));

        com.google.inject.Injector injector = Guice.createInjector(modules);
        return new WeldInjector(injector);
        */
        return null;
    }
}
