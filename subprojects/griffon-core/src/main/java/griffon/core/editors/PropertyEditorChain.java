/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.core.editors;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static griffon.util.GriffonClassUtils.requireNonEmpty;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class PropertyEditorChain extends PropertyEditorSupport {
    private final Class<?> targetClass;
    private final Object lock = new Object[0];
    private final WeakReference<Class<? extends PropertyEditor>>[] propertyEditorClasses;
    @GuardedBy("lock")
    private WeakReference<PropertyEditor>[] propertyEditors;

    @SuppressWarnings("unchecked")
    public PropertyEditorChain(@Nonnull Class<?> targetClass, @Nonnull Class<? extends PropertyEditor>[] propertyEditorClasses) {
        this.targetClass = requireNonNull(targetClass, "Argument 'targetClass' must not be null");
        requireNonEmpty(propertyEditorClasses, "Argument 'propertyEditorClasses' must not be null nor empty");
        this.propertyEditorClasses = new WeakReference[propertyEditorClasses.length];
        for (int i = 0; i < propertyEditorClasses.length; i++) {
            this.propertyEditorClasses[i] = new WeakReference<Class<? extends PropertyEditor>>(propertyEditorClasses[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public PropertyEditorChain copyOf() {
        List<Class<? extends PropertyEditor>> classes = new ArrayList<>();
        for (WeakReference<Class<? extends PropertyEditor>> reference : propertyEditorClasses) {
            if (reference.get() != null) {
                classes.add(reference.get());
            }
        }
        return new PropertyEditorChain(targetClass, classes.toArray(new Class[classes.size()]));
    }

    @SuppressWarnings("unchecked")
    public PropertyEditorChain copyOf(Class<? extends PropertyEditor> propertyEditorClass) {
        requireNonNull(propertyEditorClass, "Argument 'propertyEditorClass' must not be null");
        List<Class<? extends PropertyEditor>> classes = new ArrayList<>();
        for (WeakReference<Class<? extends PropertyEditor>> reference : propertyEditorClasses) {
            if (reference.get() != null) {
                classes.add(reference.get());
            }
        }
        classes.add(propertyEditorClass);
        return new PropertyEditorChain(targetClass, classes.toArray(new Class[classes.size()]));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[").append(targetClass.getName()).append(']');
        return sb.toString();
    }

    @Override
    public Object getValue() {
        initPropertyEditors();

        Object value = super.getValue();
        for (WeakReference<PropertyEditor> reference : propertyEditors) {
            try {
                PropertyEditor propertyEditor = reference.get();
                if (propertyEditor != null) {
                    propertyEditor.setValue(value);
                    return propertyEditor.getValue();
                }
            } catch (Exception e) {
                // ignore. next editor
            }
        }

        throw illegalValue(value, targetClass);
    }

    @Override
    public String getAsText() {
        initPropertyEditors();

        Object value = super.getValue();

        for (WeakReference<PropertyEditor> reference : propertyEditors) {
            try {
                PropertyEditor propertyEditor = reference.get();
                if (propertyEditor != null) {
                    propertyEditor.setValue(value);
                    return propertyEditor.getAsText();
                }
            } catch (Exception e) {
                // ignore. next editor
            }
        }

        throw illegalValue(value, targetClass);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        initPropertyEditors();

        for (WeakReference<PropertyEditor> reference : propertyEditors) {
            try {
                PropertyEditor propertyEditor = reference.get();
                if (propertyEditor != null) {
                    propertyEditor.setAsText(text);
                    super.setValue(propertyEditor.getValue());
                    return;
                }
            } catch (Exception e) {
                // ignore. next editor
            }
        }

        throw illegalValue(text, targetClass);
    }

    protected ValueConversionException illegalValue(Object value, Class<?> klass) {
        throw new ValueConversionException(value, klass);
    }

    protected ValueConversionException illegalValue(Object value, Class<?> klass, Exception e) {
        throw new ValueConversionException(value, klass, e);
    }

    @SuppressWarnings("unchecked")
    private void initPropertyEditors() {
        synchronized (lock) {
            if (propertyEditors == null) {
                List<WeakReference<PropertyEditor>> editors = new ArrayList<>();
                for (WeakReference<Class<? extends PropertyEditor>> propertyEditorClass : propertyEditorClasses) {
                    try {
                        Class<? extends PropertyEditor> klass = propertyEditorClass.get();
                        if (klass != null) {
                            editors.add(new WeakReference<>(klass.newInstance()));
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new IllegalArgumentException("Can't create instance", e);
                    }
                }

                if (editors.size() > 0) {
                    propertyEditors = editors.toArray(new WeakReference[editors.size()]);
                } else {
                    throw new IllegalStateException("No available PropertyEditors for " + this);
                }
            }
        }
    }
}
