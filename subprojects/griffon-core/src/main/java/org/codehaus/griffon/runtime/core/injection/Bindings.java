/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core.injection;

import griffon.core.injection.*;
import griffon.core.injection.binder.AnnotatedBindingBuilder;
import griffon.core.injection.binder.BindingBuilder;
import griffon.core.injection.binder.LinkedBindingBuilder;
import griffon.core.injection.binder.SingletonBindingBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static griffon.util.GriffonClassUtils.requireAnnotation;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class Bindings {
    public static <T> AnnotatedBindingBuilder<T> bind(@Nonnull Class<T> clazz) {
        requireNonNull(clazz, "Argument 'class' cannot be null");
        return new AnnotatedBindingBuilderImpl<>(clazz);
    }

    private static abstract class SingletonBindingBuilderImpl<T> implements SingletonBindingBuilder<T> {
        protected boolean singleton;

        @Override
        public void asSingleton() {
            singleton = true;
        }
    }

    private abstract static class LinkedBindingBuilderImpl<T> extends SingletonBindingBuilderImpl<T> implements LinkedBindingBuilder<T> {
        protected Class<? extends T> target;
        protected T instance;
        protected Provider<? extends T> provider;

        @Nonnull
        @Override
        public SingletonBindingBuilder to(@Nonnull Class<? extends T> target) {
            this.target = requireNonNull(target, "Argument 'target' cannot be null");
            return this;
        }

        @Override
        public void toInstance(@Nonnull T instance) {
            this.instance = requireNonNull(instance, "Argument 'instance' cannot be null");
        }

        @Nonnull
        @Override
        public SingletonBindingBuilder toProvider(@Nonnull Provider<? extends T> provider) {
            this.provider = requireNonNull(provider, "Argument 'provider' cannot be null");
            return this;
        }
    }

    private static class AnnotatedBindingBuilderImpl<T> extends LinkedBindingBuilderImpl<T> implements AnnotatedBindingBuilder<T> {
        private final Class<T> source;
        private Annotation classifier;
        private Class<? extends Annotation> classifierType;

        private AnnotatedBindingBuilderImpl(@Nonnull Class<T> source) {
            this.source = requireNonNull(source, "Argument 'source' cannot be null");
        }

        @Nonnull
        @Override
        public Binding<T> getBinding() {
            if (instance != null) {
                return classifier != null ? new InstanceBindingImpl<>(source, classifier, instance) : new InstanceBindingImpl<>(source, classifierType, instance);
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
            requireNonNull(annotationType, "Argument 'annotationType' cannot be null");
            requireAnnotation(annotationType, Qualifier.class);
            this.classifierType = annotationType;
            return this;
        }

        @Nonnull
        @Override
        public LinkedBindingBuilder<T> withClassifier(@Nonnull Annotation annotation) {
            requireNonNull(annotation, "Argument 'annotation' cannot be null");
            this.classifier = annotation;
            withClassifier(annotation.getClass());
            return this;
        }
    }

    private static abstract class AbstractBindingImpl<T> implements Binding<T> {
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
    }

    private static class TargetBindingImpl<T> extends AbstractBindingImpl<T> implements TargetBinding<T> {
        private final Class<? extends T> target;

        private TargetBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends T> target, @Nonnull Annotation classifier, boolean singleton) {
            super(source, classifier, singleton);
            this.target = target;
        }

        private TargetBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends T> target, @Nonnull Class<? extends Annotation> classifierType, boolean singleton) {
            super(source, classifierType, singleton);
            this.target = target;
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
        }

        protected InstanceBindingImpl(@Nonnull Class<T> source, @Nonnull Class<? extends Annotation> classifierType, @Nonnull T instance) {
            super(source, classifierType, true);
            this.instance = instance;
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
        private final Provider<? extends T> provider;

        private ProviderBindingImpl(@Nonnull Class<T> source, @Nonnull Provider<? extends T> provider, @Nonnull Annotation classifier, boolean singleton) {
            super(source, classifier, singleton);
            this.provider = provider;
        }


        private ProviderBindingImpl(@Nonnull Class<T> source, @Nonnull Provider<? extends T> provider, @Nonnull Class<? extends Annotation> classifierType, boolean singleton) {
            super(source, classifierType, singleton);
            this.provider = provider;
        }

        @Nonnull
        @Override
        public Provider<? extends T> getProvider() {
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
}
