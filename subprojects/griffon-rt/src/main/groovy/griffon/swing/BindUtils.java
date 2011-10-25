/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language govnerning permissions and
 * limitations under the License.
 */
package griffon.swing;

import griffon.util.CallableWithArgs;
import griffon.util.CallableWithArgsClosure;
import groovy.util.FactoryBuilderSupport;

import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * Helper class for constructing bindings between two objects.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public class BindUtils {
    /**
     * Create a new Binding using a builder.
     */
    public static BindingBuilder binding() {
        return new BindingBuilder();
    }

    public static class BindingBuilder {
        private Object source;
        private Object target;
        private String sourceProperty;
        private String targetProperty;
        private CallableWithArgs converter;
        private CallableWithArgs validator;
        private boolean mutual;

        public BindingBuilder withSource(Object source) {
            this.source = source;
            return this;
        }

        public BindingBuilder withTarget(Object target) {
            this.target = target;
            return this;
        }

        public BindingBuilder withSourceProperty(String sourceProperty) {
            this.sourceProperty = sourceProperty;
            return this;
        }

        public BindingBuilder withTargetProperty(String targetProperty) {
            this.targetProperty = targetProperty;
            return this;
        }

        public BindingBuilder withConverter(CallableWithArgs converter) {
            this.converter = converter;
            return this;
        }

        public BindingBuilder withValidator(CallableWithArgs validator) {
            this.validator = validator;
            return this;
        }

        public BindingBuilder withMutual(boolean mutual) {
            this.mutual = mutual;
            return this;
        }

        public void make(FactoryBuilderSupport builder) {
            if (builder == null) {
                throw new IllegalArgumentException("Cannot make binding with a null builder!");
            }

            Map<String, Object> attributes = new LinkedHashMap<String, Object>();

            if (source == null) {
                throw new IllegalArgumentException("Unspecified value for: source");
            }
            if (target == null) {
                throw new IllegalArgumentException("Unspecified value for: target");
            }
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

            if (converter != null) attributes.put("converter", new CallableWithArgsClosure(builder, converter));
            if (validator != null) attributes.put("validator", new CallableWithArgsClosure(builder, validator));

            builder.invokeMethod("bind", attributes);
        }
    }
}
