/*
 * Copyright 2013-2014 the original author or authors.
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

package griffon.core;

import griffon.core.injection.Binding;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface ApplicationBootstrapper {
    void bootstrap() throws Exception;

    void run();

    public static class Key {
        private final Class<?> source;
        private final Class<? extends Annotation> annotationType;
        private Annotation annotation;

        private Key(@Nonnull Class<?> source, @Nonnull Annotation annotation) {
            this.source = source;
            this.annotation = annotation;
            this.annotationType = annotation.getClass();
        }

        private Key(@Nonnull Class<?> source, @Nonnull Class<? extends Annotation> annotationType) {
            this.source = source;
            this.annotationType = annotationType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApplicationBootstrapper.Key key = (ApplicationBootstrapper.Key) o;

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
        @SuppressWarnings("ConstantConditions")
        public static ApplicationBootstrapper.Key of(Binding<?> binding) {
            return binding.getClassifier() != null ? new ApplicationBootstrapper.Key(binding.getSource(), binding.getClassifier()) :
                new ApplicationBootstrapper.Key(binding.getSource(), binding.getClassifierType());
        }
    }
}
