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

import griffon.util.TypeUtils;
import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static griffon.util.GriffonNameUtils.getPropertyNameRepresentation;
import static griffon.util.GriffonNameUtils.join;
import static griffon.util.GriffonNameUtils.quote;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class BeanUtils {
    @Nonnull
    public static List<AutowireCandidateQualifier> toAutowireCandidateQualifiers(@Nonnull Class<? extends Annotation> annotationType) {
        return toAutowireCandidateQualifiers(Arrays.<Class<? extends Annotation>>asList(annotationType));
    }

    @Nonnull
    public static List<AutowireCandidateQualifier> asAutowireCandidateQualifiers(@Nonnull Annotation annotation) {
        return asAutowireCandidateQualifiers(Arrays.<Annotation>asList(annotation));
    }

    @Nonnull
    public static List<AutowireCandidateQualifier> asAutowireCandidateQualifiers(@Nonnull Collection<Annotation> annotations) {
        List<AutowireCandidateQualifier> list = new ArrayList<>();
        for (Annotation annotation : annotations) {
            if (annotation != null) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                AutowireCandidateQualifier md = new AnnotationQualifier(annotation);
                for (Method method : annotationType.getMethods()) {
                    if (method.getParameterTypes().length == 0 && method.getDeclaringClass() != Object.class) {
                        try {
                            String attrName = method.getName();
                            Object attrValue = method.invoke(annotation);
                            BeanMetadataAttribute attribute = new BeanMetadataAttribute(attrName, attrValue);
                            md.addMetadataAttribute(attribute);
                        } catch (Exception e) {
                            throw new UnsupportedOperationException("handle me gracefully", e);
                        }
                    }
                }
                list.add(md);
            }
        }
        return list;
    }

    @Nonnull
    public static List<AutowireCandidateQualifier> toAutowireCandidateQualifiers(@Nonnull List<Class<? extends Annotation>> annotationTypes) {
        List<AutowireCandidateQualifier> list = new ArrayList<>();
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            list.add(new AnnotationQualifier<>(annotationType));
        }
        return list;
    }


    public static class Key {
        private static final String[] EXCLUSIONS = new String[]{"annotationType", "hashCode", "toString"};
        private final Class<?> type;
        private final List<AutowireCandidateQualifier> qualifiers = new ArrayList<>();

        private Key(@Nonnull Class<?> type, @Nonnull List<AutowireCandidateQualifier> qualifiers) {
            this.type = type;
            this.qualifiers.addAll(qualifiers);
        }

        @Nonnull
        @SuppressWarnings("ConstantConditions")
        public static BeanUtils.Key of(@Nonnull Class<?> type, @Nonnull List<AutowireCandidateQualifier> qualifiers) {
            return new BeanUtils.Key(type, qualifiers);
        }

        public Class<?> getType() {
            return type;
        }

        public List<AutowireCandidateQualifier> getQualifiers() {
            return qualifiers;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BeanUtils.Key key = (BeanUtils.Key) o;

            return type.equals(key.type) &&
                TypeUtils.arrayEqual(qualifiers, key.qualifiers);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + qualifiers.hashCode();
            return result;
        }

        @Nonnull
        public String asFormattedName() {
            if (qualifiers.size() == 1 && qualifiers.get(0) instanceof Named) {
                String name = ((Named) qualifiers.get(0)).value();
                if (name == null) {
                    name = getPropertyNameRepresentation(type);
                }
                if (name != null) {
                    return name;
                }
            }
            return type.getName() + format(qualifiers);
        }

        @Nonnull
        private String format(@Nonnull List<AutowireCandidateQualifier> qualifiers) {
            StringBuilder sb = new StringBuilder();
            if (qualifiers.size() > 0) {
                sb.append("{");
                for (AutowireCandidateQualifier qualifier : qualifiers) {
                    sb.append(qualifier.getTypeName());
                    List<String> attributes = new ArrayList<>();
                    for (String attributeName : qualifier.attributeNames()) {
                        if (Arrays.binarySearch(EXCLUSIONS, attributeName) < 0) {
                            BeanMetadataAttribute metaAttr = qualifier.getMetadataAttribute(attributeName);
                            attributes.add(attributeName + "=" + quote(String.valueOf(metaAttr.getValue())));
                        }
                    }
                    sb.append("[");
                    sb.append(join(attributes, ","));
                    sb.append("]");
                }
                sb.append("}");
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Key{");
            sb.append("type=").append(type);
            sb.append(", qualifiers=").append(qualifiers);
            sb.append('}');
            return sb.toString();
        }
    }
}
