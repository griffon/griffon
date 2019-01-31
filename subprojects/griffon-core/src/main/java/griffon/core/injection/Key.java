/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.core.injection;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import java.lang.annotation.Annotation;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.7.0
 */
public class Key<T> {
    private static final String ERROR_SOURCE_NULL = "Argument 'source' must not be null";
    private static final String ERROR_ANNOTATION_NULL = "Argument 'annotation' must not be null";
    private static final String ERROR_ANNOTATION_TYPE_NULL = "Argument 'annotationType' must not be null";

    private final Class<T> source;
    private Class<? extends Annotation> annotationType;
    private Annotation annotation;

    private Key(@Nonnull Class<T> source) {
        this.source = requireNonNull(source, ERROR_SOURCE_NULL);
    }

    private Key(@Nonnull Class<T> source, @Nonnull Annotation annotation) {
        this.source = requireNonNull(source, ERROR_SOURCE_NULL);
        this.annotation = requireNonNull(annotation, ERROR_ANNOTATION_NULL);
        this.annotationType = annotation.getClass();
    }

    private Key(@Nonnull Class<T> source, @Nonnull Class<? extends Annotation> annotationType) {
        this.source = requireNonNull(source, ERROR_SOURCE_NULL);
        this.annotationType = requireNonNull(annotationType, ERROR_ANNOTATION_TYPE_NULL);
    }

    @Nonnull
    public Class<?> getSource() {
        return source;
    }

    @Nullable
    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    @Nullable
    public Annotation getAnnotation() {
        return annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Key key = (Key) o;

        if (annotation != null) {
            return source.equals(key.source) && annotation.equals(key.annotation);
        }

        return source.equals(key.source) &&
            !(annotationType != null ? !annotationType.equals(key.annotationType) : key.annotationType != null);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        if (annotation != null) {
            result = 31 * result + annotation.hashCode();
        } else {
            result = 31 * result + (annotationType != null ? annotationType.hashCode() : 0);
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Key{");
        sb.append("source=").append(source);
        sb.append(", annotation=").append(annotation);
        sb.append(", annotationType=").append(annotationType);
        sb.append('}');
        return sb.toString();
    }

    @Nonnull
    public static <T> Key<T> of(@Nonnull Class<T> source) {
        return new Key<>(source);
    }

    @Nonnull
    public static <T> Key<T> of(@Nonnull Class<T> source, @Nonnull Annotation annotation) {
        return new Key<>(source, annotation);
    }

    @Nonnull
    public static <T> Key<T> of(@Nonnull Class<T> source, @Nonnull Class<? extends Annotation> annotationType) {
        return new Key<>(source, annotationType);
    }

    @Nonnull
    public static <T> Key<T> of(@Nonnull Binding<T> binding) {
        return binding.getClassifier() != null ? new Key<>(binding.getSource(), binding.getClassifier()) :
            (binding.getClassifierType() != null ? new Key<>(binding.getSource(), binding.getClassifierType()) : new Key<>(binding.getSource()));
    }
}