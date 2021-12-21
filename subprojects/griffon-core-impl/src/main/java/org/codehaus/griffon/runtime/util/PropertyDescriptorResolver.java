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
package org.codehaus.griffon.runtime.util;

import griffon.annotations.core.Nonnull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.ObjectUtils.isGetterMethod;
import static griffon.util.ObjectUtils.isSetterMethod;
import static griffon.util.StringUtils.getPropertyName;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PropertyDescriptorResolver {
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> DESCRIPTORS = new ConcurrentHashMap<>();

    @Nonnull
    public static Map<String, PropertyDescriptor> findDescriptors(@Nonnull Class<?> klass) {
        return DESCRIPTORS.computeIfAbsent(klass, PropertyDescriptorResolver::fetchPropertyMetadata);
    }

    @Nonnull
    public static PropertyDescriptor findDescriptorFor(@Nonnull Class<?> klass, @Nonnull String propertyName) {
        requireNonNull(klass, "Argument 'class' must no be null");
        requireNonBlank(propertyName, "Argument 'propertyName' must no be null");
        Map<String, PropertyDescriptor> metadata = DESCRIPTORS.computeIfAbsent(klass, PropertyDescriptorResolver::fetchPropertyMetadata);
        return metadata.get(propertyName);
    }

    @Nonnull
    private static Map<String, PropertyDescriptor> fetchPropertyMetadata(@Nonnull Class<?> klass) {
        Map<String, PropertyDescriptor> metadata = new HashMap<>();

        Class<?> c = klass;
        while (c != null) {
            metadata.putAll(doFetchPropertyMetadata(c));
            c = c.getSuperclass();
        }

        return metadata;
    }

    @Nonnull
    private static Map<String, PropertyDescriptor> doFetchPropertyMetadata(@Nonnull Class<?> klass) {
        Map<String, PropertyDescriptor> metadata = new HashMap<>();

        Map<String, PropertyDescriptorBuilder> builders = new HashMap<>();
        for (Method method : klass.getMethods()) {
            if (isGetterMethod(method)) {
                String name = getPropertyName(method);
                Class<?> type = method.getReturnType();
                PropertyDescriptorBuilder builder = builders.computeIfAbsent(name, PropertyDescriptorBuilder::new);
                if (builder.getType() != null && !type.equals(builder.getType())) {
                    builders.remove(name);
                }
                builder.withType(type);
                builder.withReadMethod(method);
            } else if (isSetterMethod(method)) {
                String name = getPropertyName(method);
                Class<?> type = method.getParameters()[0].getType();
                PropertyDescriptorBuilder builder = builders.computeIfAbsent(name, PropertyDescriptorBuilder::new);
                if (builder.getType() != null && !type.equals(builder.getType())) {
                    builders.remove(name);
                }
                builder.withType(type);
                builder.withWriteMethod(method);
            }
        }

        for (PropertyDescriptorBuilder builder : builders.values()) {
            metadata.put(builder.getName(), builder.build());
        }

        return metadata;
    }

    private static class PropertyDescriptorBuilder {
        private final String name;
        private Class<?> type;
        private Method readMethod;
        private Method writeMethod;

        public PropertyDescriptorBuilder(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public PropertyDescriptorBuilder withType(Class<?> type) {
            this.type = type;
            return this;
        }

        public PropertyDescriptorBuilder withReadMethod(Method readMethod) {
            this.readMethod = readMethod;
            return this;
        }

        public PropertyDescriptorBuilder withWriteMethod(Method writeMethod) {
            this.writeMethod = writeMethod;
            return this;
        }

        public PropertyDescriptor build() {
            return new PropertyDescriptor(name, type, readMethod, writeMethod);
        }
    }
}
