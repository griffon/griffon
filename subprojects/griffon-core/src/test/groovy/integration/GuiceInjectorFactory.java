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
package integration;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.Contextual;
import griffon.core.Context;
import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.env.ApplicationPhase;
import griffon.core.events.NewInstanceEvent;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.InjectorFactory;
import griffon.exceptions.FieldException;
import griffon.exceptions.NewInstanceException;
import org.codehaus.griffon.runtime.core.injection.InjectorProvider;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static com.google.inject.util.Providers.guicify;
import static griffon.util.AnnotationUtils.annotationsOfMethodParameter;
import static griffon.util.AnnotationUtils.findAnnotation;
import static griffon.util.AnnotationUtils.namesFor;
import static griffon.util.GriffonClassUtils.getAllDeclaredFields;
import static griffon.util.GriffonClassUtils.getPropertyDescriptors;
import static griffon.util.GriffonClassUtils.invokeAnnotatedMethod;
import static griffon.util.GriffonClassUtils.setFieldValue;
import static integration.GuiceInjector.moduleFromBindings;
import static java.util.Objects.requireNonNull;

@ServiceProviderFor(InjectorFactory.class)
public class GuiceInjectorFactory implements InjectorFactory {
    @Nonnull
    @Override
    public GuiceInjector createInjector(@Nonnull GriffonApplication application, @Nonnull Iterable<Binding<?>> bindings) {
        requireNonNull(application, "Argument 'application' must not be null");
        requireNonNull(bindings, "Argument 'bindings' must not be null");
        InjectorProvider injectorProvider = new InjectorProvider();
        GuiceInjector injector = createModules(application, injectorProvider, bindings);
        injectorProvider.setInjector(injector);
        return injector;
    }

    private GuiceInjector createModules(@Nonnull final GriffonApplication application, @Nonnull final InjectorProvider injectorProvider, @Nonnull Iterable<Binding<?>> bindings) {
        final InjectionListener<GriffonArtifact> injectionListener = injectee -> application.getEventRouter()
            .publishEvent(NewInstanceEvent.of((Class<GriffonArtifact>) injectee.getClass(), injectee));

        final InjectionListener<Object> postConstructorInjectorListener = injectee -> {
            resolveContextualInjections(injectee, application);
            resolveConfigurationInjections(injectee, application);
            invokeAnnotatedMethod(injectee, PostConstruct.class);
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
                                 @SuppressWarnings("unchecked")
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

        ServiceLoader<Module> moduleLoader = ServiceLoader.load(Module.class, getClass().getClassLoader());
        for (Module module : moduleLoader) {
            modules.add(module);
        }

        com.google.inject.Injector injector = Guice.createInjector(modules);
        return new GuiceInjector(injector);
    }

    protected void resolveContextualInjections(@Nonnull Object injectee, @Nonnull GriffonApplication application) {
        if (application.getPhase() == ApplicationPhase.INITIALIZE || injectee instanceof GriffonArtifact) {
            // skip
            return;
        }

        Map<String, Field> fields = new LinkedHashMap<>();
        for (Field field : getAllDeclaredFields(injectee.getClass())) {
            fields.put(field.getName(), field);
        }

        Map<String, InjectionPoint> injectionPoints = new LinkedHashMap<>();
        for (PropertyDescriptor descriptor : getPropertyDescriptors(injectee.getClass())) {
            Method method = descriptor.getWriteMethod();
            if (method == null || isInjectable(method)) { continue; }
            boolean nullable = method.getAnnotation(Nonnull.class) == null && findAnnotation(annotationsOfMethodParameter(method, 0), Nonnull.class) == null;
            InjectionPoint.Type type = resolveType(method);
            Field field = fields.get(descriptor.getName());
            if (field != null && type == InjectionPoint.Type.OTHER) {
                type = resolveType(field);
                nullable = field.getAnnotation(Nonnull.class) == null;
            }
            injectionPoints.put(descriptor.getName(), new MethodInjectionPoint(descriptor.getName(), nullable, method, type));
        }

        for (Field field : getAllDeclaredFields(injectee.getClass())) {
            if (Modifier.isStatic(field.getModifiers()) || isInjectable(field)) { continue; }
            if (!injectionPoints.containsKey(field.getName())) {
                boolean nullable = field.getAnnotation(Nonnull.class) == null;
                InjectionPoint.Type type = resolveType(field);
                injectionPoints.put(field.getName(), new FieldInjectionPoint(field.getName(), nullable, field, type));
            }
        }

        for (InjectionPoint ip : injectionPoints.values()) {
            ip.apply(application.getContext(), injectee);
        }
    }

    @Nonnull
    protected InjectionPoint.Type resolveType(@Nonnull AnnotatedElement element) {
        if (isContextual(element)) {
            return InjectionPoint.Type.CONTEXTUAL;
        }
        return InjectionPoint.Type.OTHER;
    }

    protected boolean isContextual(AnnotatedElement element) {
        return element != null && element.getAnnotation(Contextual.class) != null;
    }

    protected boolean isInjectable(AnnotatedElement element) {
        return element != null && element.getAnnotation(Inject.class) != null;
    }

    protected void resolveConfigurationInjections(@Nonnull Object injectee, @Nonnull GriffonApplication application) {
        if (application.getPhase() == ApplicationPhase.INITIALIZE || injectee instanceof GriffonArtifact) {
            // skip
            return;
        }
        application.getConfigurationManager().injectConfiguration(injectee);
    }

    protected abstract static class InjectionPoint {
        protected final String name;
        protected final boolean nullable;
        protected final Type type;

        protected InjectionPoint(String name, boolean nullable, Type type) {
            this.name = name;
            this.nullable = nullable;
            this.type = type;
        }

        protected enum Type {
            CONTEXTUAL,
            OTHER
        }

        protected abstract void apply(@Nonnull Context context, @Nonnull Object instance);
    }

    protected static class FieldInjectionPoint extends InjectionPoint {
        protected final Field field;

        protected FieldInjectionPoint(String name, boolean nullable, Field field, Type type) {
            super(name, nullable, type);
            this.field = field;
        }

        @Override
        protected void apply(@Nonnull Context context, @Nonnull Object instance) {
            if (type == Type.CONTEXTUAL) {
                String[] keys = namesFor(field);
                Object argValue = null;

                for (String key : keys) {
                    if (context.containsKey(key)) {
                        argValue = context.get(key);
                        break;
                    }
                }

                try {
                    if (argValue == null) {
                        if (!nullable) {
                            throw new IllegalStateException("Could not find an instance of type " +
                                field.getType().getName() + " under keys '" + Arrays.toString(keys) +
                                "' in the application context to be injected on field '" + field.getName() +
                                "' in " + instance.getClass().getName() + ". Field does not accept null values.");
                        }
                        return;
                    }

                    setFieldValue(instance, name, argValue);
                } catch (IllegalStateException | FieldException e) {
                    throw new NewInstanceException(instance.getClass(), e);
                }
            }
        }
    }

    protected static class MethodInjectionPoint extends InjectionPoint {
        protected final Method method;

        protected MethodInjectionPoint(String name, boolean nullable, Method method, Type type) {
            super(name, nullable, type);
            this.method = method;
        }

        @Override
        protected void apply(@Nonnull Context context, @Nonnull Object instance) {
            if (type == Type.CONTEXTUAL) {
                String[] keys = namesFor(method);
                Object argValue = null;

                for (String key : keys) {
                    if (context.containsKey(key)) {
                        argValue = context.get(key);
                        break;
                    }
                }

                try {
                    if (argValue == null) {
                        if (!nullable) {
                            throw new IllegalStateException("Could not find an instance of type " +
                                method.getParameterTypes()[0].getName() + " under keys '" + Arrays.toString(keys) +
                                "' in the application context to be injected on property '" + name +
                                "' in " + instance.getClass().getName() + "). Property does not accept null values.");
                        } return;
                    }

                    method.invoke(instance, argValue);
                } catch (IllegalStateException | IllegalAccessException | InvocationTargetException e) {
                    throw new NewInstanceException(instance.getClass(), e);
                }
            }
        }
    }
}
