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
import griffon.core.injection.InstanceBinding;
import griffon.core.injection.ProviderBinding;
import griffon.core.injection.ProviderTypeBinding;
import griffon.core.injection.Qualified;
import griffon.core.injection.TargetBinding;
import griffon.exceptions.ClosedInjectorException;
import griffon.exceptions.InstanceNotFoundException;
import griffon.exceptions.MembersInjectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static griffon.core.GriffonExceptionHandler.getRootCause;
import static java.util.Objects.requireNonNull;
import static org.codehaus.griffon.runtime.injection.BeanUtils.asAutowireCandidateQualifiers;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class SpringInjector implements Injector<GriffonApplicationContext> {
    private static final Logger LOG = LoggerFactory.getLogger(SpringInjector.class);
    private static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    private static final String ERROR_BINDINGS_NULL = "Argument 'bindings' must not be null";
    private static final String ERROR_DELEGATE_NULL = "Argument 'delegate' must not be null";
    private static final String ERROR_QUALIFIER_NULL = "Argument 'qualifier' must not be null";
    private static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    private static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";

    private final GriffonApplicationContext delegate;
    private final Object lock = new Object[0];
    @GuardedBy("lock")
    private boolean closed;

    public SpringInjector(@Nonnull SpringContext delegate) {
        requireNonNull(delegate, ERROR_DELEGATE_NULL);
        this.delegate = delegate.create();
    }

    static SpringContext contextFromBindings(final @Nonnull GriffonApplication application, final @Nonnull Iterable<Binding<?>> bindings) {
        SpringContext context = new SpringContext(application);

        for (Binding<?> binding : bindings) {
            if (binding instanceof TargetBinding) {
                handleTargetBinding(context, (TargetBinding) binding);
            } else if (binding instanceof InstanceBinding) {
                handleInstanceBinding(context, (InstanceBinding) binding);
            } else if (binding instanceof ProviderTypeBinding) {
                handleProviderTypeBinding(context, (ProviderTypeBinding) binding);
            } else if (binding instanceof ProviderBinding) {
                handleProviderBinding(context, (ProviderBinding) binding);
            } else {
                throw new IllegalArgumentException("Don't know how to handle " + binding);
            }
        }

        return context;
    }

    private static void handleTargetBinding(SpringContext context, TargetBinding binding) {
        context.declareBean(
            binding.getSource(),
            binding.getClassifierType(),
            binding.getClassifier(),
            binding.getTarget(),
            binding.isSingleton()
        );
    }

    private static void handleInstanceBinding(SpringContext context, InstanceBinding binding) {
        context.bindBean(
            binding.getSource(),
            binding.getClassifierType(),
            binding.getClassifier(),
            binding.getInstance()
        );
    }

    private static void handleProviderTypeBinding(SpringContext context, ProviderTypeBinding binding) {
        context.declareProvider(
            binding.getSource(),
            binding.getClassifierType(),
            binding.getClassifier(),
            binding.getProviderType(),
            binding.isSingleton()
        );
    }

    private static void handleProviderBinding(SpringContext context, ProviderBinding binding) {
        context.bindProvider(
            binding.getSource(),
            binding.getClassifierType(),
            binding.getClassifier(),
            binding.getProvider(),
            binding.isSingleton()
        );
    }

    @Nonnull
    @Override
    public GriffonApplicationContext getDelegateInjector() {
        return delegate;
    }

    @Nonnull
    @Override
    public <T> T getInstance(@Nonnull Class<T> type) throws InstanceNotFoundException {
        requireNonNull(type, ERROR_TYPE_NULL);

        if (isClosed()) {
            throw new InstanceNotFoundException(type, new ClosedInjectorException(this));
        }

        try {
            delegate.refresh();
            return delegate.getBean(type);
        } catch (RuntimeException e) {
            Throwable t = getRootCause(e);
            if (t instanceof NoUniqueBeanDefinitionException) {
                String beanDefinitionName = BeanUtils.Key.of(type, Collections.<AutowireCandidateQualifier>emptyList()).asFormattedName();
                try {
                    return (T) delegate.getBean(beanDefinitionName);
                } catch (RuntimeException e2) {
                    LOG.trace("ERROR", e2);
                    throw new InstanceNotFoundException(type, e);
                }
            }
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
            List<AutowireCandidateQualifier> qualifiers = asAutowireCandidateQualifiers(qualifier);
            String beanDefinitionName = BeanUtils.Key.of(type, qualifiers).asFormattedName();

            delegate.refresh();
            return (T) delegate.getBean(beanDefinitionName);
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

        try {
            delegate.refresh();
            return delegate.getBeansOfType(type).values();
        } catch (RuntimeException e) {
            throw new InstanceNotFoundException(type, e);
        }
    }

    @Nonnull
    @Override
    public <T> Collection<Qualified<T>> getQualifiedInstances(@Nonnull Class<T> type) throws InstanceNotFoundException {
        requireNonNull(type, ERROR_TYPE_NULL);

        if (isClosed()) {
            throw new InstanceNotFoundException(type, new ClosedInjectorException(this));
        }

        List<Qualified<T>> instances = new ArrayList<>();

        /*
        try {
            System.out.println("-------------------------");
            delegate.getBeanNamesForType(type);
            AutowireCapableBeanFactory autowireCapableBeanFactory = delegate.getAutowireCapableBeanFactory();
            if (autowireCapableBeanFactory instanceof BeanDefinitionRegistry) {
                BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) autowireCapableBeanFactory;
                for (String beanDefinitionName : delegate.getBeanNamesForType(type)) {
                    // T instance= (T) delegate.getBean(beanDefinitionName);
                    BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
                    AnnotatedGenericBeanDefinition agbd = (AnnotatedGenericBeanDefinition) beanDefinition;
                    AutowireCandidateQualifier qualifier = null;
                    Set<AutowireCandidateQualifier> qualifiers = agbd.getQualifiers();
                    if (qualifiers != null && qualifiers.size() > 0) {
                        qualifier = qualifiers.iterator().next();
                    }
                    AnnotationMetadata metadata = agbd.getMetadata();
                }
            }

            for (T instance : delegate.getBeansOfType(type).values()) {
                try {
                    List<Annotation> qualifiers = AnnotationUtils.harvestQualifiers(instance.getClass());
                    Annotation annotation = !qualifiers.isEmpty() ? qualifiers.get(0) : null;
                    instances.add(new Qualified<>(instance, annotation));
                } catch (RuntimeException e) {
                    throw new InstanceNotFoundException(type, e);
                }
            }

        } catch (RuntimeException e) {
            throw new InstanceNotFoundException(type, e);
        }
        */

        return instances;
    }

    @Override
    public void injectMembers(@Nonnull Object instance) throws MembersInjectionException {
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        if (isClosed()) {
            throw new MembersInjectionException(instance, new ClosedInjectorException(this));
        }

        try {
            delegate.refresh();
            delegate.getAutowireCapableBeanFactory().autowireBean(instance);
        } catch (RuntimeException e) {
            throw new MembersInjectionException(instance, e);
        }
    }

    @Override
    public void close() {
        if (isClosed()) {
            throw new ClosedInjectorException(this);
        }

        delegate.refresh();
        delegate.close();

        synchronized (lock) {
            closed = true;
        }
    }

    private boolean isClosed() {
        synchronized (lock) {
            return closed;
        }
    }
}
