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

import org.springframework.beans.factory.support.AutowireCandidateQualifier;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class AnnotationQualifier<A extends Annotation> extends AutowireCandidateQualifier {
    private A annotation;
    private Class<? extends Annotation> annotationType;

    public AnnotationQualifier(@Nonnull A annotation) {
        super(requireNonNull(annotation, "annotation").annotationType());
        this.annotation = annotation;
        this.annotationType = annotation.annotationType();
    }

    public AnnotationQualifier(@Nonnull Class<? extends Annotation> annotationType) {
        super(requireNonNull(annotationType, "annotationType"));
        this.annotationType = annotationType;
    }

    public A getAnnotation() {
        return annotation;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }

    @Override
    public String toString() {
        return annotation != null ? annotation.toString() : annotationType.toString();
    }
}
