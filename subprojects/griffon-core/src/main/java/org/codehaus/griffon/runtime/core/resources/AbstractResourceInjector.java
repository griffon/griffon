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
package org.codehaus.griffon.runtime.core.resources;

import griffon.core.editors.ExtendedPropertyEditor;
import griffon.core.resources.InjectedResource;
import griffon.core.resources.ResourceInjector;
import griffon.exceptions.InstanceMethodInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.core.editors.PropertyEditorResolver.findEditor;
import static griffon.util.GriffonClassUtils.getPropertyDescriptors;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonNameUtils.getSetterName;
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

        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(klass);
        for (PropertyDescriptor pd : propertyDescriptors) {
            Method method = pd.getWriteMethod();
            if (null == method || isStatic(method.getModifiers())) {
                continue;
            }

            final InjectedResource annotation = method.getAnnotation(InjectedResource.class);
            if (null == annotation) continue;

            String propertyName = pd.getName();
            String fqName = method.getDeclaringClass().getName().replace('$', '.') + "." + propertyName;
            String key = annotation.key();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            String format = annotation.format();
            if (isBlank(key)) key = fqName;

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

        for (Field field : klass.getDeclaredFields()) {
            if (field.isSynthetic() || names.contains(field.getName())) {
                continue;
            }
            final InjectedResource annotation = field.getAnnotation(InjectedResource.class);
            if (null == annotation) continue;

            String fqName = field.getDeclaringClass().getName().replace('$', '.') + "." + field.getName();
            String key = annotation.key();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            String format = annotation.format();
            if (isBlank(key)) key = fqName;

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

    @Nullable
    protected abstract Object resolveResource(@Nonnull String key, @Nonnull String[] args);

    @Nullable
    protected abstract Object resolveResource(@Nonnull String key, @Nonnull String[] args, @Nonnull String defaultValue);

    @Nonnull
    protected Object convertValue(@Nonnull Class<?> type, @Nonnull Object value, @Nullable String format) {
        requireNonNull(type, ERROR_TYPE_NULL);
        requireNonNull(value, ERROR_VALUE_NULL);
        PropertyEditor propertyEditor = resolvePropertyEditor(type, format);
        if (null == propertyEditor) return value;
        if (value instanceof CharSequence) {
            propertyEditor.setAsText(String.valueOf(value));
        } else {
            propertyEditor.setValue(value);
        }
        return propertyEditor.getValue();
    }

    @Nullable
    protected PropertyEditor resolvePropertyEditor(@Nonnull Class<?> type, @Nullable String format) {
        requireNonNull(type, ERROR_TYPE_NULL);
        PropertyEditor propertyEditor = findEditor(type);
        if (propertyEditor instanceof ExtendedPropertyEditor) {
            ((ExtendedPropertyEditor) propertyEditor).setFormat(format);
        }
        return propertyEditor;
    }

    protected void setPropertyValue(@Nonnull Object instance, @Nonnull Method method, @Nullable Object value, @Nonnull String fqName) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);
        requireNonNull(method, ERROR_METHOD_NULL);
        requireNonBlank(fqName, ERROR_FULLY_QUALIFIED_NAME_BLANK);
        try {
            method.invoke(instance, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Cannot set value on property " + fqName + " of instance " + instance, sanitize(e));
            }
        }
    }

    protected void setFieldValue(@Nonnull Object instance, @Nonnull Field field, @Nullable Object value, @Nonnull String fqFieldName) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);
        requireNonNull(field, ERROR_FIELD_NULL);
        requireNonBlank(fqFieldName, ERROR_FULLY_QUALIFIED_FIELD_NAME_BLANK);
        String setter = getSetterName(field.getName());
        try {
            invokeExactInstanceMethod(instance, setter, value);
        } catch (InstanceMethodInvocationException imie) {
            try {
                field.setAccessible(true);
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                LOG.warn("Cannot set value on field {} of instance {}", fqFieldName, instance, sanitize(e));
            }
        }
    }
}
