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
package griffon.util;

import griffon.core.CallableWithArgs;
import groovy.lang.Closure;
import groovy.util.FactoryBuilderSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * Helper class for constructing bindings between two objects.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class BindUtils {
    private BindUtils() {
        // prevent instantiation
    }

    /**
     * Create a new Binding using a builder.
     */
    @Nonnull
    public static BindingBuilder binding() {
        return new BindingBuilder();
    }

    public static class BindingBuilder {
        private Object source;
        private Object target;
        private String sourceProperty;
        private String targetProperty;
        private CallableWithArgs<?> converter;
        private CallableWithArgs<?> validator;
        private boolean mutual;

        @Nonnull
        public BindingBuilder withSource(@Nullable Object source) {
            this.source = source;
            return this;
        }

        @Nonnull
        public BindingBuilder withTarget(@Nullable Object target) {
            this.target = target;
            return this;
        }

        @Nonnull
        public BindingBuilder withSourceProperty(@Nullable String sourceProperty) {
            this.sourceProperty = sourceProperty;
            return this;
        }

        @Nonnull
        public BindingBuilder withTargetProperty(@Nullable String targetProperty) {
            this.targetProperty = targetProperty;
            return this;
        }

        @Nonnull
        public BindingBuilder withConverter(@Nullable CallableWithArgs<?> converter) {
            this.converter = converter;
            return this;
        }

        @Nonnull
        public BindingBuilder withValidator(@Nullable CallableWithArgs<?> validator) {
            this.validator = validator;
            return this;
        }

        @Nonnull
        public BindingBuilder withMutual(boolean mutual) {
            this.mutual = mutual;
            return this;
        }

        public void make(@Nonnull FactoryBuilderSupport builder) {
            requireNonNull(builder, "Cannot make binding with a null builder!");
            requireNonNull(source, "Unspecified value for: source");
            requireNonNull(target, "Unspecified value for: target");

            Map<String, Object> attributes = new LinkedHashMap<>();

            if (isBlank(sourceProperty)) sourceProperty = targetProperty;
            if (isBlank(sourceProperty)) {
                throw new IllegalArgumentException("Unspecified values for: sourceProperty, targetProperty");
            }
            if (isBlank(targetProperty)) targetProperty = sourceProperty;

            attributes.put("source", source);
            attributes.put("target", target);
            attributes.put("sourceProperty", sourceProperty);
            attributes.put("targetProperty", targetProperty);
            attributes.put("mutual", mutual);

            if (converter != null) {
                attributes.put("converter", makeClosure(builder, converter));
            }
            if (validator != null) {
                attributes.put("validator", makeClosure(builder, validator));
            }

            builder.invokeMethod("bind", attributes);
        }

        private Closure<?> makeClosure(@Nonnull FactoryBuilderSupport builder, @Nonnull final CallableWithArgs<?> callback) {
            if (callback instanceof Closure) {
                return (Closure<?>) callback;
            }
            return new Closure<Object>(builder) {
                private static final long serialVersionUID = -4108869890482462552L;

                @Override
                public Object call(Object... args) {
                    return callback.call(args);
                }

                @Override
                public Object call(Object args) {
                    return callback.call(args);
                }
            };
        }
    }
}
