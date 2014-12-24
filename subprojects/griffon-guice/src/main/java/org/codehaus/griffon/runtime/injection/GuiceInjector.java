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

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.*;
import griffon.exceptions.ClosedInjectorException;
import griffon.exceptions.InstanceNotFoundException;
import griffon.exceptions.MembersInjectionException;
import griffon.exceptions.TypeNotFoundException;
import griffon.util.AnnotationUtils;
import org.codehaus.griffon.runtime.core.injection.InjectorProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.inject.util.Providers.guicify;
import static griffon.util.AnnotationUtils.named;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.runtime.injection.MethodUtils.invokeAnnotatedMethod;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GuiceInjector implements Injector<com.google.inject.Injector> {
    private static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    private static final String ERROR_BINDINGS_NULL = "Argument 'bindings' must not be null";
    private static final String ERROR_DELEGATE_NULL = "Argument 'delegate' must not be null";
    private static final String ERROR_QUALIFIER_NULL = "Argument 'qualifier' must not be null";
    private static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    private static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";

    private final com.google.inject.Injector delegate;
    private final Object lock = new Object[0];
    @GuardedBy("lock")
    private boolean closed;


    public GuiceInjector(@Nonnull com.google.inject.Injector delegate) {
        this.delegate = requireNonNull(delegate, ERROR_DELEGATE_NULL);
    }

    @Nonnull
    @Override
    public <T> T getInstance(@Nonnull Class<T> type) throws InstanceNotFoundException {
        requireNonNull(type, ERROR_TYPE_NULL);

        if (isClosed()) {
            throw new InstanceNotFoundException(type, new ClosedInjectorException(this));
        }

        try {
            return delegate.getInstance(type);
        } catch (RuntimeException e) {
            throw new InstanceNotFoundException(type, e);
        }
    }

    @Nonnull
    @Override
    public <T> T getInstance(@Nonnull Class<T> type, @Nonnull Annotation qualifier) throws InstanceNotFoundException {
        requireNonNull(type, ERROR_TYPE_NULL);
        requireNonNull(qualifier, ERROR_QUALIFIER_NULL);

        if (isClosed()) {
            throw new InstanceNotFoundException(type, qualifier, new ClosedInjectorException(this));
        }

        try {
            return delegate.getInstance(Key.get(type, qualifier));
        } catch (RuntimeException e) {
            throw new InstanceNotFoundException(type, qualifier, e);
        }
    }

    @Nonnull
    @Override
    public <T> Collection<T> getInstances(@Nonnull Class<T> type) throws InstanceNotFoundException {
        requireNonNull(type, ERROR_TYPE_NULL);

        if (isClosed()) {
            throw new InstanceNotFoundException(type, new ClosedInjectorException(this));
        }

        List<T> instances = new ArrayList<>();

        List<com.google.inject.Binding<T>> bindings;
        try {
            bindings = delegate.findBindingsByType(TypeLiteral.get(type));
        } catch (RuntimeException e) {
            throw new InstanceNotFoundException(type, e);
        }
        if (bindings == null) {
            throw new InstanceNotFoundException(type);
        }

        for (com.google.inject.Binding<T> binding : bindings) {
            try {
                instances.add(delegate.getInstance(binding.getKey()));
            } catch (RuntimeException e) {
                throw new InstanceNotFoundException(type, e);
            }
        }

        return instances;
    }

    @Nonnull
    @Override
    public <T> Collection<Qualified<T>> getQualifiedInstances(@Nonnull Class<T> type) throws InstanceNotFoundException {
        requireNonNull(type, ERROR_TYPE_NULL);

        if (isClosed()) {
            throw new InstanceNotFoundException(type, new ClosedInjectorException(this));
        }

        List<Qualified<T>> instances = new ArrayList<>();

        List<com.google.inject.Binding<T>> bindings;
        try {
            bindings = delegate.findBindingsByType(TypeLiteral.get(type));
        } catch (RuntimeException e) {
            throw new InstanceNotFoundException(type, e);
        }
        if (bindings == null) {
            throw new InstanceNotFoundException(type);
        }

        for (com.google.inject.Binding<T> binding : bindings) {
            try {
                Key<T> key = binding.getKey();
                final T instance = delegate.getInstance(key);
                instances.add(new Qualified<>(instance, translate(key.getAnnotation())));
            } catch (RuntimeException e) {
                throw new InstanceNotFoundException(type, e);
            }
        }

        return instances;
    }

    @Nullable
    private Annotation translate(@Nullable Annotation annotation) {
        if (annotation instanceof com.google.inject.name.Named) {
            return AnnotationUtils.named(((com.google.inject.name.Named) annotation).value());
        }
        return annotation;
    }

    @Override
    public void injectMembers(@Nonnull Object instance) throws MembersInjectionException {
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        if (isClosed()) {
            throw new MembersInjectionException(instance, new ClosedInjectorException(this));
        }

        try {
            delegate.injectMembers(instance);
        } catch (RuntimeException e) {
            throw new MembersInjectionException(instance, e);
        }
    }

    @Nonnull
    @Override
    public com.google.inject.Injector getDelegateInjector() {
        return delegate;
    }

    @Nonnull
    @Override
    public Injector<com.google.inject.Injector> createNestedInjector(@Nonnull Iterable<Binding<?>> bindings) {
        requireNonNull(bindings, ERROR_BINDINGS_NULL);

        if (isClosed()) {
            throw new ClosedInjectorException(this);
        }

        return new GuiceInjector(delegate.createChildInjector(moduleFromBindings(bindings)));
    }

    @Nonnull
    @Override
    public Injector<com.google.inject.Injector> createNestedInjector(final @Nonnull String name, @Nonnull Iterable<Binding<?>> bindings) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(bindings, ERROR_BINDINGS_NULL);

        if (isClosed()) {
            throw new ClosedInjectorException(this);
        }

        final InjectorProvider injectorProvider = new InjectorProvider();
        GuiceInjector injector = new GuiceInjector(delegate.createChildInjector(moduleFromBindings(bindings), new AbstractModule() {
            @Override
            protected void configure() {
                bind(Injector.class)
                    .annotatedWith(named(name))
                    .toProvider(guicify(injectorProvider));
            }
        }));
        injectorProvider.setInjector(injector);
        return injector;
    }

    @Override
    public void close() {
        if (isClosed()) {
            throw new ClosedInjectorException(this);
        }

        for (Key<?> key : delegate.getAllBindings().keySet()) {
            try {
                com.google.inject.Binding<?> binding = delegate.getExistingBinding(key);
                if (!Scopes.isSingleton(binding)) {
                    continue;
                }
                invokeAnnotatedMethod(binding.getProvider().get(), PreDestroy.class);
            } catch (ProvisionException pe) {
                if (!(pe.getCause() instanceof TypeNotFoundException)) {
                    pe.printStackTrace();
                }
            }
        }

        synchronized (lock) {
            closed = true;
        }
    }

    private boolean isClosed() {
        synchronized (lock) {
            return closed;
        }
    }

    static Module moduleFromBindings(final @Nonnull Iterable<Binding<?>> bindings) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                for (Binding<?> binding : bindings) {
                    if (binding instanceof TargetBinding) {
                        handleTargetBinding((TargetBinding) binding);
                    } else if (binding instanceof InstanceBinding) {
                        handleInstanceBinding((InstanceBinding) binding);
                    } else if (binding instanceof ProviderTypeBinding) {
                        handleProviderTypeBinding((ProviderTypeBinding) binding);
                    } else if (binding instanceof ProviderBinding) {
                        handleProviderBinding((ProviderBinding) binding);
                    } else {
                        throw new IllegalArgumentException("Don't know how to handle " + binding);
                    }
                }
            }

            @Nonnull
            private LinkedBindingBuilder handleBinding(@Nonnull Binding<?> binding) {
                AnnotatedBindingBuilder<?> builder = bind(binding.getSource());
                if (binding.getClassifier() != null) {
                    return builder.annotatedWith(binding.getClassifier());
                } else if (binding.getClassifierType() != null) {
                    return builder.annotatedWith(binding.getClassifierType());
                }
                return builder;
            }

            @SuppressWarnings("unchecked")
            private void handleTargetBinding(@Nonnull TargetBinding<?> binding) {
                LinkedBindingBuilder lbuilder = handleBinding(binding);
                if (binding.getSource() != binding.getTarget()) {
                    ScopedBindingBuilder sbuilder = lbuilder.to(binding.getTarget());
                    if (binding.isSingleton()) {
                        sbuilder.in(Singleton.class);
                    }
                } else if (binding.isSingleton()) {
                    lbuilder.in(Singleton.class);
                }
            }

            @SuppressWarnings("unchecked")
            private void handleInstanceBinding(@Nonnull InstanceBinding<?> binding) {
                handleBinding(binding).toInstance(binding.getInstance());
            }

            @SuppressWarnings("unchecked")
            private void handleProviderTypeBinding(@Nonnull ProviderTypeBinding<?> binding) {
                ScopedBindingBuilder builder = handleBinding(binding).toProvider(binding.getProviderType());
                if (binding.isSingleton()) {
                    builder.in(Singleton.class);
                }
            }

            @SuppressWarnings("unchecked")
            private void handleProviderBinding(@Nonnull ProviderBinding<?> binding) {
                ScopedBindingBuilder builder = handleBinding(binding).toProvider(guicify(binding.getProvider()));
                if (binding.isSingleton()) {
                    builder.in(Singleton.class);
                }
            }
        };
    }
}
