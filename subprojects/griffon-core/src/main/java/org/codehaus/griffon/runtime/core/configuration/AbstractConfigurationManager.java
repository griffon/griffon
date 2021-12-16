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
package org.codehaus.griffon.runtime.core.configuration;

import griffon.core.ApplicationEvent;
import griffon.core.Configuration;
import griffon.core.GriffonApplication;
import griffon.core.configuration.ConfigurationManager;
import griffon.core.configuration.Configured;
import griffon.core.editors.ExtendedPropertyEditor;
import griffon.core.editors.PropertyEditorResolver;
import griffon.exceptions.GriffonException;
import griffon.util.GriffonClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.core.editors.PropertyEditorResolver.findEditor;
import static griffon.util.GriffonNameUtils.isNotBlank;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public abstract class AbstractConfigurationManager implements ConfigurationManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigurationManager.class);

    private static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    private static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    private static final String ERROR_VALUE_NULL = "Argument 'value' must not be null";

    @Inject
    protected GriffonApplication application;

    @PostConstruct
    private void initialize() {
        requireNonNull(application, "Argument 'application' cannot ne null");

        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), args -> {
            Object instance = args[1];
            injectConfiguration(instance);
        });
    }

    @Override
    public void injectConfiguration(@Nonnull Object instance) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        Map<String, ConfigurationDescriptor> descriptors = new LinkedHashMap<>();
        Class<?> klass = instance.getClass();
        do {
            harvestDescriptors(instance.getClass(), klass, instance, descriptors);
            klass = klass.getSuperclass();
        } while (null != klass);

        doConfigurationInjection(instance, descriptors);
    }

    protected void harvestDescriptors(@Nonnull Class<?> instanceClass, @Nonnull Class<?> currentClass, @Nonnull Object instance, @Nonnull Map<String, ConfigurationDescriptor> descriptors) {
        PropertyDescriptor[] propertyDescriptors = GriffonClassUtils.getPropertyDescriptors(currentClass);
        for (PropertyDescriptor pd : propertyDescriptors) {
            Method writeMethod = pd.getWriteMethod();
            if (null == writeMethod || isStatic(writeMethod.getModifiers())) { continue; }

            Configured annotation = writeMethod.getAnnotation(Configured.class);
            if (null == annotation) { continue; }

            String propertyName = pd.getName();
            String configuration = annotation.configuration().trim();
            String key = annotation.value();
            String defaultValue = annotation.defaultValue();
            defaultValue = Configured.NO_VALUE.equals(defaultValue) ? null : defaultValue;
            String format = annotation.format();
            Class<? extends PropertyEditor> editor = annotation.editor();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Property " + propertyName +
                    " of instance " + instance +
                    " [configuration='" + configuration +
                    "', key='" + key +
                    "', defaultValue='" + defaultValue +
                    "', format='" + format +
                    "'] is marked for configuration injection.");
            }
            descriptors.put(propertyName, new MethodConfigurationDescriptor(writeMethod, configuration, key, defaultValue, format, editor));
        }

        for (Field field : currentClass.getDeclaredFields()) {
            if (field.isSynthetic() || isStatic(field.getModifiers()) || descriptors.containsKey(field.getName())) {
                continue;
            }
            final Configured annotation = field.getAnnotation(Configured.class);
            if (null == annotation) { continue; }

            Class<?> resolvedClass = field.getDeclaringClass();
            String fqFieldName = resolvedClass.getName().replace('$', '.') + "." + field.getName();
            String configuration = annotation.configuration().trim();
            String key = annotation.value();
            String defaultValue = annotation.defaultValue();
            defaultValue = Configured.NO_VALUE.equals(defaultValue) ? null : defaultValue;
            String format = annotation.format();
            Class<? extends PropertyEditor> editor = annotation.editor();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Field " + fqFieldName +
                    " of instance " + instance +
                    " [configuration='" + configuration +
                    "', key='" + key +
                    "', defaultValue='" + defaultValue +
                    "', format='" + format +
                    "'] is marked for configuration injection.");
            }

            descriptors.put(field.getName(), new FieldConfigurationDescriptor(field, configuration, key, defaultValue, format, editor));
        }
    }

    protected void doConfigurationInjection(@Nonnull Object instance, @Nonnull Map<String, ConfigurationDescriptor> descriptors) {
        for (ConfigurationDescriptor descriptor : descriptors.values()) {
            Object value = resolveConfiguration(descriptor.getConfiguration(), descriptor.getKey(), descriptor.getDefaultValue());

            if (value != null) {
                InjectionPoint injectionPoint = descriptor.asInjectionPoint();
                if (!isNoopPropertyEditor(descriptor.getEditor()) || !injectionPoint.getType().isAssignableFrom(value.getClass())) {
                    value = convertValue(injectionPoint.getType(), value, descriptor.getFormat(), descriptor.getEditor());
                }
                injectionPoint.setValue(instance, value);
            }
        }
    }

    @Nullable
    protected Object resolveConfiguration(@Nonnull String name, @Nonnull String key, @Nullable String defaultValue) {
        Configuration configuration = getConfiguration();
        if (isNotBlank(name)) {
            configuration = getConfiguration(name);
        }

        if (configuration.containsKey(key)) {
            return configuration.get(key);
        } else {
            return defaultValue;
        }
    }

    @Nonnull
    protected Object convertValue(@Nonnull Class<?> type, @Nonnull Object value, @Nullable String format, @Nonnull Class<? extends PropertyEditor> editor) {
        requireNonNull(type, ERROR_TYPE_NULL);
        requireNonNull(value, ERROR_VALUE_NULL);

        PropertyEditor propertyEditor = resolvePropertyEditor(type, format, editor);
        if (isNoopPropertyEditor(propertyEditor.getClass())) { return value; }
        if (value instanceof CharSequence) {
            propertyEditor.setAsText(String.valueOf(value));
        } else {
            propertyEditor.setValue(value);
        }
        return propertyEditor.getValue();
    }

    @Nonnull
    protected PropertyEditor resolvePropertyEditor(@Nonnull Class<?> type, @Nullable String format, @Nonnull Class<? extends PropertyEditor> editor) {
        requireNonNull(type, ERROR_TYPE_NULL);

        PropertyEditor propertyEditor = null;
        if (isNoopPropertyEditor(editor)) {
            propertyEditor = findEditor(type);
        } else {
            try {
                propertyEditor = editor.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GriffonException("Could not instantiate editor with " + editor, e);
            }
        }

        if (propertyEditor instanceof ExtendedPropertyEditor) {
            ((ExtendedPropertyEditor) propertyEditor).setFormat(format);
        }
        return propertyEditor;
    }

    protected boolean isNoopPropertyEditor(@Nonnull Class<? extends PropertyEditor> editor) {
        return PropertyEditorResolver.NoopPropertyEditor.class.isAssignableFrom(editor);
    }
}
