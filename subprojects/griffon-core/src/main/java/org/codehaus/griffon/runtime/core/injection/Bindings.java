/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.core.injection;

import griffon.core.injection.Binding;
import griffon.core.injection.InstanceBinding;
import griffon.core.injection.ProviderBinding;
import griffon.core.injection.ProviderTypeBinding;
import griffon.core.injection.TargetBinding;
import griffon.util.AnnotationUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.List;

import static griffon.util.AnnotationUtils.harvestQualifiers;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class Bindings {
    private Bindings() {
        // prevent instantiation
    }

    public static <T> AnnotatedBindingBuilder<T> bind(@Nonnull Class<T> clazz) {
        requireNonNull(clazz, "Argument 'class' must not be null");
        return new AnnotatedBindingBuilderImpl<>(clazz);
    }

    private abstract static class SingletonBindingBuilderImpl<T> implements SingletonBindingBuilder<T> {
        protected boolean singleton;

        @Override
        public void asSingleton() {
            singleton = true;
        }
    }

    private abstract static class LinkedBindingBuilderImpl<T> extends SingletonBindingBuilderImpl<T> implements LinkedBindingBuilder<T> {
        protected Class<? extends T> target;
        protected T instance;
        protected Provider<T> provider;
        protected Class<? extends Provider<T>> providerType;

        @Nonnull
        @Override
        public SingletonBindingBuilder<T> to(@Nonnull Class<? extends T> target) {
            this.target = requireNonNull(target, "Argument 'target' must not be null");
            return this;
        }

        @Override
        public void toInstance(@Nonnull T instance) {
            this.instance = requireNonNull(instance, "Argument 'instance' must not be null");
        }

        @Nonnull
        @Override
        public SingletonBindingBuilder<T> toProvider(@Nonnull Provider<T> provider) {
            this.provider = requireNonNull(provider, "Argument 'provider' must not be null");
            return this;
        }

        @Nonnull
        @Override
        public SingletonBindingBuilder<T> toProvider(@Nonnull Class<? extends Provider<T>> providerType) {
            this.providerType = requireNonNull(providerType, "Argument 'providerType' must not be null");
            return this;
        }
    }

    private static class AnnotatedBindingBuilderImpl<T> extends LinkedBindingBuilderImpl<T> implements AnnotatedBindingBuilder<T> {
        private final Class<T> source;
        private Annotation classifier;
        private Class<? extends Annotation> classifierType;

        private AnnotatedBindingBuilderImpl(@Nonnull Class<T> source) {
            this.source = requireNonNull(source, "Argument 'source' must not be null");
        }

        @Nonnull
        @Override
        public Binding<T> getBinding() {
            if (instance != null) {
                return classifier != null ? new InstanceBindingImpl<>(source, classifier, instance) : new InstanceBindingImpl<>(source, classifierType, instance);
            } else if (providerType != null) {
                return classifier != null ? new ProviderTypeBindingImpl<>(source, providerType, classifier, singleton) : new ProviderTypeBindingImpl<>(source, providerType, classifierType, singleton);
            } else if (provider != null) {
                return classifier != null ? new ProviderBindingImpl<>(source, provider, classifier, singleton) : new ProviderBindingImpl<>(source, provider, classifierType, singleton);
            } else if (target != null) {
                return classifier != null ? new TargetBindingImpl<>(source, target, classifier, singleton) : new TargetBindingImpl<>(source, target, classifierType, singleton);
            }

            return classifier != null ? new TargetBindingImpl<>(source, source, classifier, singleton) : new TargetBindingImpl<>(source, source, classifierType, singleton);
        }

        @Nonnull
        @Override
        public LinkedBindingBuilder<T> withClassifier(@Nonnull Class<? extends Annotation> annotationType) {
            requireNonNull(annotationType, "Argument 'annotationType' must not be null");
            AnnotationUtils.requireAnnotation(annotationType, Qualifier.class);
            this.classifierType = annotationType;
            return this;
        }

        @Nonnull
        @Override
        public LinkedBindingBuilder<T> withClassifier(@Nonnull Annotation annotation) {
            requireNonNull(annotation, "Argument 'annotation' must not be null");
            this.classifier = annotation;
            withClassifier(annotation.getClass());
            return this;
        }
    }

    private abstract static class AbstractBindingImpl<T> implements Binding<T> {
        protected final Class<T> source;
        protected final boolean singleton;
        protected Annotation classifier;
        protected Class<? extends Annotation> classifierType;

        protected AbstractBindingImpl(@Nonnull Class<T> source, @Nonnull Annotation classifier, boolean singleton) {
            this.source = source;
            this.singleton = singleton;
            this.classifier = classifier;
            this.classifierType = classifier.getClass();
        }

        protected AbstractBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends Annotation> classifierType, boolean singleton) {
            this.source = source;
            this.singleton = singleton;
            this.classifierType = classifierType;
        }

        @Nonnull
        @Override
        public Class<T> getSource() {
            return source;
        }

        @Nullable
        @Override
        public Class<? extends Annotation> getClassifierType() {
            return classifierType;
        }

        @Nullable
        @Override
        public Annotation getClassifier() {
            return classifier;
        }

        @Override
        public boolean isSingleton() {
            return singleton;
        }

        protected void updateClassifier(Class<?> klass) {
            if (this.classifier == null) {
                List<Annotation> qualifiers = harvestQualifiers(klass);
                if (!qualifiers.isEmpty()) {
                    this.classifier = qualifiers.get(0);
                }
            }
        }

        protected void updateClassifierType(Class<?> klass) {
            if (this.classifierType == null) {
                List<Annotation> qualifiers = harvestQualifiers(klass);
                if (!qualifiers.isEmpty()) {
                    this.classifier = qualifiers.get(0);
                }
            }
        }
    }

    private static class TargetBindingImpl<T> extends AbstractBindingImpl<T> implements TargetBinding<T> {
        private final Class<? extends T> target;

        private TargetBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends T> target, @Nonnull Annotation classifier, boolean singleton) {
            super(source, classifier, singleton);
            this.target = target;
            updateClassifier(target);
        }

        private TargetBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends T> target, @Nonnull Class<? extends Annotation> classifierType, boolean singleton) {
            super(source, classifierType, singleton);
            this.target = target;
            updateClassifierType(target);
        }

        @Nonnull
        @Override
        public Class<? extends T> getTarget() {
            return target;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TargetBinding[");
            sb.append("source=").append(source.getName());
            if (classifier != null) {
                sb.append(", classifier=").append(classifier);
            } else if (classifierType != null) {
                sb.append(", classifierType=").append(classifierType.getName());
            }
            sb.append(", target=").append(target.getName());
            sb.append(", singleton=").append(singleton);
            sb.append(']');
            return sb.toString();
        }
    }

    private static class InstanceBindingImpl<T> extends AbstractBindingImpl<T> implements InstanceBinding<T> {
        private final T instance;

        protected InstanceBindingImpl(@Nonnull Class<T> source, @Nonnull Annotation classifier, @Nonnull T instance) {
            super(source, classifier, true);
            this.instance = instance;
            updateClassifier(instance.getClass());
        }

        protected InstanceBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends Annotation> classifierType, @Nonnull T instance) {
            super(source, classifierType, true);
            this.instance = instance;
            updateClassifierType(instance.getClass());
        }

        @Nonnull
        @Override
        public T getInstance() {
            return instance;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("InstanceBinding[");
            sb.append("source=").append(source.getName());
            if (classifier != null) {
                sb.append(", classifier=").append(classifier);
            } else if (classifierType != null) {
                sb.append(", classifierType=").append(classifierType.getName());
            }
            sb.append(", instance=").append(instance);
            sb.append(", singleton=").append(singleton);
            sb.append(']');
            return sb.toString();
        }
    }

    private static class ProviderBindingImpl<T> extends AbstractBindingImpl<T> implements ProviderBinding<T> {
        private final Provider<T> provider;

        private ProviderBindingImpl(@Nonnull Class<T> source, @Nonnull Provider<T> provider, @Nonnull Annotation classifier, boolean singleton) {
            super(source, classifier, singleton);
            this.provider = provider;
            updateClassifier(provider.getClass());
        }


        private ProviderBindingImpl(@Nonnull Class<T> source, @Nonnull Provider<T> provider, @Nonnull Class<? extends Annotation> classifierType, boolean singleton) {
            super(source, classifierType, singleton);
            this.provider = provider;
            updateClassifierType(provider.getClass());
        }

        @Nonnull
        @Override
        public Provider<T> getProvider() {
            return provider;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ProviderBinding[");
            sb.append("source=").append(source.getName());
            if (classifier != null) {
                sb.append(", classifier=").append(classifier);
            } else if (classifierType != null) {
                sb.append(", classifierType=").append(classifierType.getName());
            }
            sb.append(", provider=").append(provider);
            sb.append(", singleton=").append(singleton);
            sb.append(']');
            return sb.toString();
        }
    }

    private static class ProviderTypeBindingImpl<T> extends AbstractBindingImpl<T> implements ProviderTypeBinding<T> {
        private final Class<? extends Provider<T>> providerType;

        private ProviderTypeBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends Provider<T>> providerType, @Nonnull Annotation classifier, boolean singleton) {
            super(source, classifier, singleton);
            this.providerType = providerType;
            updateClassifier(providerType);
        }


        private ProviderTypeBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends Provider<T>> providerType, @Nonnull Class<? extends Annotation> classifierType, boolean singleton) {
            super(source, classifierType, singleton);
            this.providerType = providerType;
            updateClassifierType(providerType);
        }

        @Nonnull
        @Override
        public Class<? extends Provider<T>> getProviderType() {
            return providerType;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ProviderTypeBinding[");
            sb.append("source=").append(source.getName());
            if (classifier != null) {
                sb.append(", classifier=").append(classifier);
            } else if (classifierType != null) {
                sb.append(", classifierType=").append(classifierType.getName());
            }
            sb.append(", providerType=").append(providerType.getName());
            sb.append(", singleton=").append(singleton);
            sb.append(']');
            return sb.toString();
        }
    }
}
