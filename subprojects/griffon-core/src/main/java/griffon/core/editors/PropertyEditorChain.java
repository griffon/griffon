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
package griffon.core.editors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static griffon.util.GriffonClassUtils.requireNonEmpty;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class PropertyEditorChain extends PropertyEditorSupport implements ExtendedPropertyEditor {
    private final Class<?> targetClass;
    private final Object lock = new Object[0];
    private final WeakReference<Class<? extends PropertyEditor>>[] propertyEditorClasses;
    @GuardedBy("lock")
    private WeakReference<PropertyEditor>[] propertyEditors;
    private String format;

    @SuppressWarnings("unchecked")
    public PropertyEditorChain(@Nonnull Class<?> targetClass, @Nonnull Class<? extends PropertyEditor>[] propertyEditorClasses) {
        this.targetClass = requireNonNull(targetClass, "Argument 'targetClass' must not be null");
        requireNonEmpty(propertyEditorClasses, "Argument 'propertyEditorClasses' must not be null nor empty");
        // let's make sure propertyEditorClasses contains unique elements
        Set<Class<? extends PropertyEditor>> classes = new LinkedHashSet<>();
        Collections.addAll(classes, propertyEditorClasses);

        int i = 0;
        this.propertyEditorClasses = new WeakReference[classes.size()];
        for (Class<? extends PropertyEditor> klass : classes) {
            this.propertyEditorClasses[i++] = new WeakReference<Class<? extends PropertyEditor>>(klass);
        }
    }

    @Override
    @Nullable
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(@Nullable String format) {
        this.format = format;
    }

    public int getSize() {
        return propertyEditorClasses.length;
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
        if (!classes.contains(propertyEditorClass)) {
            classes.add(propertyEditorClass);
        }
        return new PropertyEditorChain(targetClass, classes.toArray(new Class[classes.size()]));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[").append(targetClass.getName()).append(']');
        return sb.toString();
    }

    @Override
    public String getAsText() {
        return isBlank(getFormat()) ? getAsTextInternal() : getFormattedValue();
    }

    @Override
    public void setAsText(String str) throws IllegalArgumentException {
        if (isBlank(getFormat())) {
            setAsTextInternal(str);
        } else {
            setFormattedValue(str);
        }
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof CharSequence) {
            setFormattedValue(String.valueOf(value));
        } else {
            setValueInternal(value);
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String getFormattedValue() {
        initPropertyEditors();

        Object value = super.getValue();
        for (WeakReference<PropertyEditor> reference : propertyEditors) {
            try {
                PropertyEditor propertyEditor = reference.get();
                if (propertyEditor != null && propertyEditor instanceof ExtendedPropertyEditor) {
                    ExtendedPropertyEditor extendedPropertyEditor = (ExtendedPropertyEditor) propertyEditor;
                    extendedPropertyEditor.setFormat(format);
                    extendedPropertyEditor.setValue(value);
                    return extendedPropertyEditor.getFormattedValue();
                }
            } catch (Exception e) {
                // ignore. next editor
            }
        }

        throw illegalValue(value, targetClass);
    }

    @Override
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void setFormattedValue(String value) {
        initPropertyEditors();

        for (WeakReference<PropertyEditor> reference : propertyEditors) {
            try {
                PropertyEditor propertyEditor = reference.get();
                if (propertyEditor != null && propertyEditor instanceof ExtendedPropertyEditor) {
                    ExtendedPropertyEditor extendedPropertyEditor = (ExtendedPropertyEditor) propertyEditor;
                    extendedPropertyEditor.setFormat(format);
                    extendedPropertyEditor.setValue(value);
                    super.setValue(extendedPropertyEditor.getValue());
                    return;
                }
            } catch (Exception e) {
                // ignore. next editor
            }
        }

        throw illegalValue(value, targetClass);
    }

    protected void setValueInternal(Object value) throws IllegalArgumentException {
        initPropertyEditors();

        for (WeakReference<PropertyEditor> reference : propertyEditors) {
            try {
                PropertyEditor propertyEditor = reference.get();
                if (propertyEditor != null) {
                    propertyEditor.setValue(value);
                    super.setValue(propertyEditor.getValue());
                    return;
                }
            } catch (Exception e) {
                // ignore. next editor
            }
        }

        throw illegalValue(value, targetClass);
    }

    protected Object getValueInternal() {
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

    protected String getAsTextInternal() {
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

    protected void setAsTextInternal(String text) throws IllegalArgumentException {
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

                if (!editors.isEmpty()) {
                    propertyEditors = editors.toArray(new WeakReference[editors.size()]);
                } else {
                    throw new IllegalStateException("No available PropertyEditors for " + this);
                }
            }
        }
    }
}
