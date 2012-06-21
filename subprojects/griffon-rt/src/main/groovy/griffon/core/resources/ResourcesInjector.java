/*
 * Copyright 2010-2012 the original author or authors.
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

package griffon.core.resources;

import griffon.core.ApplicationHandler;
import griffon.core.GriffonApplication;
import griffon.util.RunnableWithArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.util.Arrays;

import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class ResourcesInjector implements ApplicationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesInjector.class);
    private final GriffonApplication app;

    public static RunnableWithArgs getEventHandler(GriffonApplication app) {
        final ResourcesInjector injector = new ResourcesInjector(app);
        return new RunnableWithArgs() {
            public void run(Object[] args) {
                Class klass = (Class) args[0];
                Object instance = args[2];
                injector.injectResources(klass, instance);
            }
        };
    }

    public ResourcesInjector(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    private void injectResources(Class klass, Object instance) {
        do {
            doResourceInjection(klass, instance);
            klass = klass.getSuperclass();
        } while (null != klass);
    }

    private void doResourceInjection(Class klass, Object instance) {
        for (Field field : klass.getDeclaredFields()) {
            if (field.isSynthetic()) continue;
            final InjectedResource annotation = field.getAnnotation(InjectedResource.class);
            if (null == annotation) continue;

            String fieldNameKey = field.getDeclaringClass().getName().replace('$', '.') + "." + field.getName();
            String key = annotation.key();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            if (isBlank(key)) key = fieldNameKey;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Field " + fieldNameKey +
                        " of instance " + instance +
                        " [key='" + key +
                        "', args='" + Arrays.toString(args) +
                        "', defaultValue='" + defaultValue +
                        "'] is marked for resource injection.");
            }

            Object value = null;
            if (isBlank(defaultValue)) {
                value = app.getMessage(key, args, app.getLocale());
            } else {
                value = app.getMessage(key, args, defaultValue, app.getLocale());
            }

            if (null != value) {
                if (!field.getType().isAssignableFrom(value.getClass())) {
                    value = convertValue(field.getType(), value);
                }
                setFieldValue(instance, field, value, fieldNameKey);
            }
        }
    }

    private Object convertValue(Class<?> type, Object value) {
        final PropertyEditor propertyEditor = PropertyEditorManager.findEditor(type);
        if (null == propertyEditor) return value;
        if (value instanceof CharSequence) {
            propertyEditor.setAsText(String.valueOf(value));
        } else {
            propertyEditor.setValue(value);
        }
        return propertyEditor.getValue();
    }

    private void setFieldValue(Object instance, Field field, Object value, String fieldName) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Cannot set value on field " + fieldName + " of instance " + instance, sanitize(e));
            }
        }
    }
}
