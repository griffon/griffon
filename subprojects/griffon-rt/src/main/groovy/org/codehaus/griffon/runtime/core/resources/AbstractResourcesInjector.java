/*
 * Copyright 2010-2013 the original author or authors.
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core.resources;

import griffon.core.GriffonApplication;
import griffon.core.resources.InjectedResource;
import griffon.core.resources.ResourcesInjector;
import griffon.core.resources.editors.ExtendedPropertyEditor;
import griffon.core.resources.editors.PropertyEditorResolver;
import griffon.util.GriffonNameUtils;
import groovy.lang.MissingMethodException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.util.Arrays;

import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public abstract class AbstractResourcesInjector implements ResourcesInjector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractResourcesInjector.class);
    private final GriffonApplication app;

    public AbstractResourcesInjector(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    @Override
    public void injectResources(Object instance) {
        if (null == instance) return;
        Class klass = instance.getClass();
        do {
            doResourceInjection(klass, instance);
            klass = klass.getSuperclass();
        } while (null != klass);
    }

    protected boolean doResourceInjection(Class klass, Object instance) {
        boolean injected = false;
        for (Field field : klass.getDeclaredFields()) {
            if (field.isSynthetic()) continue;
            final InjectedResource annotation = field.getAnnotation(InjectedResource.class);
            if (null == annotation) continue;

            String fqFieldName = field.getDeclaringClass().getName().replace('$', '.') + "." + field.getName();
            String key = annotation.key();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            String format = annotation.format();
            if (isBlank(key)) key = fqFieldName;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Field " + fqFieldName +
                    " of instance " + instance +
                    " [key='" + key +
                    "', args='" + Arrays.toString(args) +
                    "', defaultValue='" + defaultValue +
                    "', format='" + format +
                    "'] is marked for resource injection.");
            }

            Object value = null;
            if (isBlank(defaultValue)) {
                value = resolveResource(key, args);
            } else {
                value = resolveResource(key, args, defaultValue);
            }

            if (null != value) {
                if (!field.getType().isAssignableFrom(value.getClass())) {
                    value = convertValue(field.getType(), value, format);
                }
                setFieldValue(instance, field, value, fqFieldName);
            }
            injected = true;
        }
        return injected;
    }

    protected abstract Object resolveResource(String key, String[] args);

    protected abstract Object resolveResource(String key, String[] args, String defaultValue);

    protected Object convertValue(Class<?> type, Object value, String format) {
        PropertyEditor propertyEditor = resolvePropertyEditor(type, format);
        if (null == propertyEditor) return value;
        if (value instanceof CharSequence) {
            propertyEditor.setAsText(String.valueOf(value));
        } else {
            propertyEditor.setValue(value);
        }
        return propertyEditor.getValue();
    }

    protected PropertyEditor resolvePropertyEditor(Class<?> type, String format) {
        PropertyEditor propertyEditor = PropertyEditorResolver.findEditor(type);
        if (propertyEditor instanceof ExtendedPropertyEditor) {
            ((ExtendedPropertyEditor) propertyEditor).setFormat(format);
        }
        return propertyEditor;
    }

    protected void setFieldValue(Object instance, Field field, Object value, String fqFieldName) {
        String setter = GriffonNameUtils.getSetterName(field.getName());
        try {
            InvokerHelper.invokeMethod(instance, setter, value);
        } catch (MissingMethodException mme) {
            try {
                field.setAccessible(true);
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot set value on field " + fqFieldName + " of instance " + instance, sanitize(e));
                }
            }
        }
    }
}
