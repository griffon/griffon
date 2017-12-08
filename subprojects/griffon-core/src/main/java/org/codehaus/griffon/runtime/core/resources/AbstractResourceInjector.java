/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.core.resources;

import griffon.core.resources.ResourceInjector;
import griffon.util.PropertyDescriptor;
import griffon.util.PropertyDescriptorResolver;
import org.kordamp.jsr377.converter.FormattingConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.application.converter.Converter;
import javax.application.converter.ConverterRegistry;
import javax.application.resources.InjectedResource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractResourceInjector implements ResourceInjector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceInjector.class);

    protected static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    protected static final String ERROR_METHOD_NULL = "Argument 'method' must not be null";
    protected static final String ERROR_FIELD_NULL = "Argument 'field' must not be null";
    protected static final String ERROR_CLASS_NULL = "Argument 'klass' must not be null";
    protected static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    protected static final String ERROR_VALUE_NULL = "Argument 'value' must not be null";
    protected static final String ERROR_FULLY_QUALIFIED_NAME_BLANK = "Argument 'fqName' must not be blank";
    protected static final String ERROR_FULLY_QUALIFIED_FIELD_NAME_BLANK = "Argument 'fqFieldName' must not be blank";

    private final ConverterRegistry converterRegistry;

    protected AbstractResourceInjector(ConverterRegistry converterRegistry) {
        this.converterRegistry = requireNonNull(converterRegistry, "Argument 'converterRegistry' must not be null");
    }

    protected final ConverterRegistry getConverterRegistry() {
        return converterRegistry;
    }

    @Override
    public void injectResources(@Nonnull Object instance) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);
        Class<?> klass = instance.getClass();
        do {
            doResourceInjection(klass, instance);
            klass = klass.getSuperclass();
        } while (null != klass);
    }

    protected boolean doResourceInjection(@Nonnull Class<?> klass, @Nonnull Object instance) {
        requireNonNull(klass, ERROR_CLASS_NULL);
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        boolean injected = false;
        List<String> names = new ArrayList<>();

        Map<String, PropertyDescriptor> descriptors = PropertyDescriptorResolver.findDescriptors(klass);
        for (PropertyDescriptor pd : descriptors.values()) {
            Method method = pd.getWriteMethod();
            if (null == method || isStatic(method.getModifiers())) {
                continue;
            }

            final InjectedResource annotation = method.getAnnotation(InjectedResource.class);
            if (null == annotation) { continue; }

            String propertyName = pd.getName();
            String fqName = method.getDeclaringClass().getName().replace('$', '.') + "." + propertyName;
            String key = annotation.value();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            String format = annotation.format();
            if (isBlank(key)) { key = fqName; }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Property " + propertyName +
                    " of instance " + instance +
                    " [key='" + key +
                    "', args='" + Arrays.toString(args) +
                    "', defaultValue='" + defaultValue +
                    "', format='" + format +
                    "'] is marked for resource injection.");
            }

            Object value;
            if (isBlank(defaultValue)) {
                value = resolveResource(key, args);
            } else {
                value = resolveResource(key, args, defaultValue);
            }

            if (null != value) {
                Class<?> propertyType = method.getParameterTypes()[0];
                if (!propertyType.isAssignableFrom(value.getClass())) {
                    value = convertValue(propertyType, value, format);
                }
                setPropertyValue(instance, method, value, fqName);
            }
            names.add(propertyName);
            injected = true;
        }

        for (Field field : klass.getFields()) {
            if (field.isSynthetic() || names.contains(field.getName())) {
                continue;
            }
            final javax.application.resources.InjectedResource annotation = field.getAnnotation(javax.application.resources.InjectedResource.class);
            if (null == annotation) { continue; }

            String fqName = field.getDeclaringClass().getName().replace('$', '.') + "." + field.getName();
            String key = annotation.value();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            String format = annotation.format();
            if (isBlank(key)) { key = fqName; }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Field " + fqName +
                    " of instance " + instance +
                    " [key='" + key +
                    "', args='" + Arrays.toString(args) +
                    "', defaultValue='" + defaultValue +
                    "', format='" + format +
                    "'] is marked for resource injection.");
            }

            Object value;
            if (isBlank(defaultValue)) {
                value = resolveResource(key, args);
            } else {
                value = resolveResource(key, args, defaultValue);
            }

            if (null != value) {
                if (!field.getType().isAssignableFrom(value.getClass())) {
                    value = convertValue(field.getType(), value, format);
                }
                setFieldValue(instance, field, value, fqName);
            }
            injected = true;
        }
        return injected;
    }

    @Nonnull
    protected abstract Object resolveResource(@Nonnull String key, @Nonnull String[] args);

    @Nonnull
    protected abstract Object resolveResource(@Nonnull String key, @Nonnull String[] args, @Nullable String defaultValue);

    @Nonnull
    protected Object convertValue(@Nonnull Class<?> type, @Nonnull Object value, @Nullable String format) {
        requireNonNull(type, ERROR_TYPE_NULL);
        requireNonNull(value, ERROR_VALUE_NULL);
        Converter<?> converter = resolveConverter(type, format);
        if (null == converter) { return value; }
        return converter.fromObject(value);
    }

    @Nullable
    protected Converter<?> resolveConverter(@Nonnull Class<?> type, @Nullable String format) {
        requireNonNull(type, ERROR_TYPE_NULL);
        Converter<?> converter = converterRegistry.findConverter(type);
        if (converter instanceof FormattingConverter) {
            ((FormattingConverter) converter).setFormat(format);
        }
        return converter;
    }

    protected void setPropertyValue(@Nonnull Object instance, @Nonnull Method method, @Nonnull Object value, @Nonnull String fqName) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);
        requireNonNull(method, ERROR_METHOD_NULL);
        requireNonBlank(fqName, ERROR_FULLY_QUALIFIED_NAME_BLANK);
        try {
            method.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Cannot set value on property " + fqName + " of instance " + instance, e);
            }
        }
    }

    protected void setFieldValue(@Nonnull Object instance, @Nonnull Field field, @Nonnull Object value, @Nonnull String fqFieldName) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);
        requireNonNull(field, ERROR_FIELD_NULL);
        requireNonBlank(fqFieldName, ERROR_FULLY_QUALIFIED_FIELD_NAME_BLANK);
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            LOG.warn("Cannot set value on field {} of instance {}", fqFieldName, instance, e);
        }
    }
}
