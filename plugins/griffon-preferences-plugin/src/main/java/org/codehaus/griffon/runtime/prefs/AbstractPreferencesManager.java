/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.prefs;

import griffon.core.ApplicationEvent;
import griffon.core.CallableWithArgs;
import griffon.core.GriffonApplication;
import griffon.core.editors.ExtendedPropertyEditor;
import griffon.exceptions.InstanceMethodInvocationException;
import griffon.plugins.preferences.*;
import griffon.util.GriffonClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.core.editors.PropertyEditorResolver.findEditor;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonNameUtils.getGetterName;
import static griffon.util.GriffonNameUtils.getSetterName;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractPreferencesManager implements PreferencesManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractPreferencesManager.class);

    protected static final String ERROR_INSTANCE_NULL = "Argument 'instance' must not be null";
    protected static final String ERROR_FIELD_NULL = "Argument 'field' must not be null";
    protected static final String ERROR_TYPE_NULL = "Argument 'type' must not be null";
    protected static final String ERROR_VALUE_NULL = "Argument 'value' must not be null";

    private final GriffonApplication application;
    private final InstanceStore instanceStore = new InstanceStore();

    @Inject
    public AbstractPreferencesManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot ne null");

        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), new CallableWithArgs<Void>() {
            @Override
            @Nullable
            public Void call(@Nullable Object... args) {
                Object instance = args[1];
                injectPreferences(instance);
                return null;
            }
        });

        application.getEventRouter().addEventListener(ApplicationEvent.DESTROY_INSTANCE.getName(), new CallableWithArgs<Void>() {
            @Override
            @Nullable
            public Void call(@Nullable Object... args) {
                Object instance = args[1];
                if (instanceStore.contains(instance)) {
                    instanceStore.remove(instance);
                }
                return null;
            }
        });
    }

    protected void init() {
        getPreferences().addNodeChangeListener(new NodeChangeListener() {
            public void nodeChanged(@Nonnull NodeChangeEvent event) {
                if (event.getType() == NodeChangeEvent.Type.ADDED) {
                    for (InstanceContainer instanceContainer : instanceStore) {
                        if (instanceContainer.containsPartialPath(event.getPath())) {
                            injectPreferences(instanceContainer.instance());
                        }
                    }
                }
            }
        });

        getPreferences().addPreferencesChangeListener(new PreferenceChangeListener() {
            public void preferenceChanged(@Nonnull PreferenceChangeEvent event) {
                for (InstanceContainer instanceContainer : instanceStore) {
                    String path = event.getPath();
                    if (PreferencesNode.PATH_SEPARATOR.equals(path)) {
                        path = event.getKey();
                    } else {
                        path += "." + event.getKey();
                    }
                    if (instanceContainer.containsPath(path)) {
                        InjectionPoint injectionPoint = instanceContainer.injectionPoints.get(path);
                        Object value = event.getNewValue();

                        if (null != value) {
                            if (!injectionPoint.getType().isAssignableFrom(value.getClass())) {
                                value = convertValue(injectionPoint.getType(), value, injectionPoint.format);
                            }
                        }
                        injectionPoint.setValue(instanceContainer.instance(), value);
                    }
                }
            }
        });
    }

    @Nonnull
    protected GriffonApplication getApplication() {
        return application;
    }

    public void save(@Nonnull Object instance) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        Map<String, PreferenceDescriptor> descriptors = new LinkedHashMap<>();
        Class klass = instance.getClass();
        do {
            harvestDescriptors(klass, instance, descriptors);
            klass = klass.getSuperclass();
        } while (null != klass);

        doSavePreferences(instance, descriptors);
    }

    protected void injectPreferences(@Nonnull Object instance) {
        requireNonNull(instance, ERROR_INSTANCE_NULL);

        Map<String, PreferenceDescriptor> descriptors = new LinkedHashMap<>();
        Class klass = instance.getClass();
        do {
            harvestDescriptors(klass, instance, descriptors);
            klass = klass.getSuperclass();
        } while (null != klass);

        doPreferencesInjection(instance, descriptors);
        if (instance.getClass().getAnnotation(PreferencesAware.class) != null && !instanceStore.contains(instance)) {
            List<InjectionPoint> injectionPoints = new LinkedList<>();
            for (PreferenceDescriptor pd : descriptors.values()) {
                injectionPoints.add(pd.asInjectionPoint());
            }
            instanceStore.add(instance, injectionPoints);
        }
    }

    protected void harvestDescriptors(@Nonnull Class klass, @Nonnull Object instance, @Nonnull Map<String, PreferenceDescriptor> descriptors) {
        PropertyDescriptor[] propertyDescriptors = GriffonClassUtils.getPropertyDescriptors(klass);
        for (PropertyDescriptor pd : propertyDescriptors) {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (null == readMethod || null == writeMethod) continue;
            if (isStatic(readMethod.getModifiers()) || isStatic(writeMethod.getModifiers())) {
                continue;
            }

            Preference annotation = writeMethod.getAnnotation(Preference.class);
            if (null == annotation) {
                annotation = readMethod.getAnnotation(Preference.class);
            }
            if (null == annotation) continue;

            String propertyName = pd.getName();
            String fqName = writeMethod.getDeclaringClass().getName().replace('$', '.') + "." + writeMethod.getName();
            String path = "/" + writeMethod.getDeclaringClass().getName().replace('$', '/').replace('.', '/') + "." + propertyName;
            String key = annotation.key();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            String resolvedPath = !isBlank(key) ? key : path;
            String format = annotation.format();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Property " + propertyName +
                    " of instance " + instance +
                    " [path='" + resolvedPath +
                    "', args='" + Arrays.toString(args) +
                    "', defaultValue='" + defaultValue +
                    "', format='" + format +
                    "'] is marked for preference injection.");
            }
            descriptors.put(propertyName, new MethodPreferenceDescriptor(readMethod, writeMethod, fqName, resolvedPath, args, defaultValue, format));
        }

        for (Field field : klass.getDeclaredFields()) {
            if (field.isSynthetic() || isStatic(field.getModifiers()) || descriptors.containsKey(field.getName())) {
                continue;
            }
            final Preference annotation = field.getAnnotation(Preference.class);
            if (null == annotation) continue;

            String fqFieldName = field.getDeclaringClass().getName().replace('$', '.') + "." + field.getName();
            String path = "/" + field.getDeclaringClass().getName().replace('$', '/').replace('.', '/') + "." + field.getName();
            String key = annotation.key();
            String[] args = annotation.args();
            String defaultValue = annotation.defaultValue();
            String resolvedPath = !isBlank(key) ? key : path;
            String format = annotation.format();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Field " + fqFieldName +
                    " of instance " + instance +
                    " [path='" + resolvedPath +
                    "', args='" + Arrays.toString(args) +
                    "', defaultValue='" + defaultValue +
                    "', format='" + format +
                    "'] is marked for preference injection.");
            }

            descriptors.put(field.getName(), new FieldPreferenceDescriptor(field, fqFieldName, resolvedPath, args, defaultValue, format));
        }
    }

    protected void doPreferencesInjection(@Nonnull Object instance, @Nonnull Map<String, PreferenceDescriptor> descriptors) {
        for (PreferenceDescriptor descriptor : descriptors.values()) {
            Object value = resolvePreference(descriptor.path, descriptor.args, descriptor.defaultValue);

            if (null != value) {
                InjectionPoint injectionPoint = descriptor.asInjectionPoint();
                if (!injectionPoint.getType().isAssignableFrom(value.getClass())) {
                    value = convertValue(injectionPoint.getType(), value, descriptor.format);
                }
                injectionPoint.setValue(instance, value);
            }
        }
    }

    protected void doSavePreferences(@Nonnull Object instance, @Nonnull Map<String, PreferenceDescriptor> descriptors) {
        for (PreferenceDescriptor descriptor : descriptors.values()) {
            InjectionPoint injectionPoint = descriptor.asInjectionPoint();
            Object value = injectionPoint.getValue(instance);
            String[] parsedPath = parsePath(descriptor.path);
            final PreferencesNode node = getPreferences().node(parsedPath[0]);
            final String key = parsedPath[1];
            if (value != null) {
                // Convert value only if descriptor.format is not null
                if (!isBlank(descriptor.format)) {
                    PropertyEditor propertyEditor = resolvePropertyEditor(value.getClass(), descriptor.format);
                    if (propertyEditor != null) {
                        propertyEditor.setValue(value);
                        value = propertyEditor.getAsText();
                    }
                }
                node.putAt(key, value);
            } else {
                node.remove(key);
            }
        }
    }

    protected Object resolvePreference(@Nonnull String path, @Nonnull String[] args, @Nonnull String defaultValue) {
        String[] parsedPath = parsePath(path);
        final PreferencesNode node = getPreferences().node(parsedPath[0]);
        final String key = parsedPath[1];
        if (node.containsKey(key)) {
            return evalPreferenceWithArguments(node.getAt(key), args);
        } else {
            node.putAt(key, defaultValue);
            return defaultValue;
        }
    }

    protected Object evalPreferenceWithArguments(@Nullable Object value, @Nullable Object[] args) {
        if (value instanceof CallableWithArgs) {
            CallableWithArgs callable = (CallableWithArgs) value;
            return callable.call(args);
        } else if (value instanceof CharSequence) {
            return formatPreferenceValue(String.valueOf(value), args);
        }
        return value;
    }

    protected String formatPreferenceValue(@Nonnull String message, @Nullable Object[] args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Formatting message={} args={}", message, Arrays.toString(args));
        }
        if (args == null || args.length == 0) return message;
        return MessageFormat.format(message, args);
    }

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

    @Nonnull
    protected String[] parsePath(@Nonnull String path) {
        int split = path.lastIndexOf(".");
        String head = split < 0 ? path : path.substring(0, split);
        String tail = split > 0 ? path.substring(split + 1) : null;
        head = head.replace('.', '/');
        return new String[]{head, tail};
    }

    private static class InstanceStore implements Iterable<InstanceContainer> {
        private final List<InstanceContainer> instances = new CopyOnWriteArrayList<InstanceContainer>();

        private void add(Object instance, List<InjectionPoint> injectionPoints) {
            if (null == instance) return;
            instances.add(new InstanceContainer(instance, injectionPoints));
        }

        private void remove(Object instance) {
            if (null == instance) return;
            InstanceContainer subject = null;
            for (InstanceContainer instance1 : instances) {
                subject = instance1;
                Object candidate = subject.instance();
                if (instance.equals(candidate)) {
                    break;
                }
            }
            if (subject != null) instances.remove(subject);
        }

        private boolean contains(Object instance) {
            if (null == instance) return false;
            for (InstanceContainer instanceContainer : instances) {
                Object candidate = instanceContainer.instance();
                if (instance.equals(candidate)) {
                    return true;
                }
            }
            return false;
        }

        public Iterator<InstanceContainer> iterator() {
            final Iterator<InstanceContainer> it = instances.iterator();
            return new Iterator<InstanceContainer>() {
                public boolean hasNext() {
                    return it.hasNext();
                }

                public InstanceContainer next() {
                    return it.next();
                }

                public void remove() {
                    it.remove();
                }
            };
        }
    }

    private static class InstanceContainer {
        private final WeakReference<Object> instance;
        private final Map<String, InjectionPoint> injectionPoints = new LinkedHashMap<String, InjectionPoint>();

        private InstanceContainer(Object instance, List<InjectionPoint> injectionPoints) {
            this.instance = new WeakReference<>(instance);
            for (InjectionPoint ip : injectionPoints) {
                this.injectionPoints.put(ip.path, ip);
            }
        }

        private Object instance() {
            return instance.get();
        }

        private boolean containsPath(String path) {
            for (String p : injectionPoints.keySet()) {
                if (p.equals(path)) return true;
            }
            return false;
        }

        public boolean containsPartialPath(String path) {
            for (String p : injectionPoints.keySet()) {
                if (p.startsWith(path + ".")) return true;
            }
            return false;
        }
    }

    private static abstract class InjectionPoint {
        public final String fqName;
        public final String path;
        public final String format;

        private InjectionPoint(String fqName, String path, String format) {
            this.fqName = fqName;
            this.path = path;
            this.format = format;
        }

        public abstract void setValue(Object instance, Object value);

        public abstract Object getValue(Object instance);

        public abstract Class<?> getType();
    }

    private static class FieldInjectionPoint extends InjectionPoint {
        private static final Object[] NO_ARGS = new Object[0];
        public final Field field;

        private FieldInjectionPoint(Field field, String fqName, String path, String format) {
            super(fqName, path, format);
            this.field = field;
        }

        public void setValue(Object instance, Object value) {
            requireNonNull(instance, ERROR_INSTANCE_NULL);
            requireNonNull(field, ERROR_FIELD_NULL);
            String setter = getSetterName(field.getName());
            try {
                invokeExactInstanceMethod(instance, setter, value);
            } catch (InstanceMethodInvocationException imie) {
                try {
                    field.setAccessible(true);
                    field.set(instance, value);
                } catch (IllegalAccessException e) {
                    LOG.warn("Cannot set value on field {} of instance {}", fqName, instance, sanitize(e));
                }
            }
        }

        public Object getValue(Object instance) {
            String getter = getGetterName(field.getName());
            try {
                return invokeExactInstanceMethod(instance, getter);
            } catch (InstanceMethodInvocationException imie) {
                try {
                    field.setAccessible(true);
                    return field.get(instance);
                } catch (IllegalAccessException e) {
                    LOG.warn("Cannot set value on field {} of instance {}", fqName, instance, sanitize(e));
                }
            }
            return null;
        }

        public Class<?> getType() {
            return field.getType();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FieldInjectionPoint{");
            sb.append("field=").append(field);
            sb.append(", fqName='").append(fqName).append('\'');
            sb.append(", path='").append(path).append('\'');
            sb.append(", format='").append(format).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    private static class MethodInjectionPoint extends InjectionPoint {
        public final Method readMethod;
        public final Method writeMethod;
        private final Class type;

        private MethodInjectionPoint(Method readMethod, Method writeMethod, String fqName, String path, String format) {
            super(fqName, path, format);
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
            this.type = readMethod.getReturnType();
        }

        public void setValue(Object instance, Object value) {
            try {
                writeMethod.invoke(instance, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot set value on method " + fqName + "() of instance " + instance, sanitize(e));
                }
            }
        }

        public Object getValue(Object instance) {
            try {
                return readMethod.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Cannot get value on method " + fqName + "() of instance " + instance, sanitize(e));
                }
            }
            return null;
        }

        public Class<?> getType() {
            return type;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MethodInjectionPoint{");
            sb.append("readMethod=").append(readMethod);
            sb.append(", writeMethod=").append(writeMethod);
            sb.append(", type=").append(type);
            sb.append(", fqName='").append(fqName).append('\'');
            sb.append(", path='").append(path).append('\'');
            sb.append(", format='").append(format).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    private static abstract class PreferenceDescriptor {
        public final String fqName;
        public final String path;
        public final String[] args;
        public final String defaultValue;
        public final String format;

        private PreferenceDescriptor(String fqName, String path, String[] args, String defaultValue, String format) {
            this.fqName = fqName;
            this.path = path;
            this.args = args;
            this.defaultValue = defaultValue;
            this.format = format;
        }

        public abstract InjectionPoint asInjectionPoint();
    }

    private static class FieldPreferenceDescriptor extends PreferenceDescriptor {
        public final Field field;

        private FieldPreferenceDescriptor(Field field, String fqName, String path, String[] args, String defaultValue, String format) {
            super(fqName, path, args, defaultValue, format);
            this.field = field;
        }

        public InjectionPoint asInjectionPoint() {
            return new FieldInjectionPoint(field, fqName, path, format);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FieldPreferenceDescriptor{");
            sb.append("field=").append(field);
            sb.append(", fqName='").append(fqName).append('\'');
            sb.append(", path='").append(path).append('\'');
            sb.append(", args=").append(Arrays.toString(args));
            sb.append(", defaultValue='").append(defaultValue).append('\'');
            sb.append(", format='").append(format).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    private static class MethodPreferenceDescriptor extends PreferenceDescriptor {
        public final Method readMethod;
        public final Method writeMethod;

        private MethodPreferenceDescriptor(Method readMethod, Method writeMethod, String fqName, String path, String[] args, String defaultValue, String format) {
            super(fqName, path, args, defaultValue, format);
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
        }

        public InjectionPoint asInjectionPoint() {
            return new MethodInjectionPoint(readMethod, writeMethod, fqName, path, format);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MethodPreferenceDescriptor{");
            sb.append("readMethod=").append(readMethod);
            sb.append(", writeMethod=").append(writeMethod);
            sb.append(", fqName='").append(fqName).append('\'');
            sb.append(", path='").append(path).append('\'');
            sb.append(", args=").append(Arrays.toString(args));
            sb.append(", defaultValue='").append(defaultValue).append('\'');
            sb.append(", format='").append(format).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
