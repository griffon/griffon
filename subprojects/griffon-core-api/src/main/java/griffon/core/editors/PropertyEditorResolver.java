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
package griffon.core.editors;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * The PropertyEditorResolver can be used to locate a property editor for
 * any given type name. This property editor must support the
 * java.beans.PropertyEditor interface for editing a given object.
 * <p>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class PropertyEditorResolver {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyEditorResolver.class);
    private static final Object LOCK = new Object[0];
    // GuardedBy("LOCK")
    private static final WeakCache<String, Class<? extends PropertyEditor>> propertyEditorRegistry = new WeakCache<>();
    // @GuardedBy("LOCK")
    private static final Map<String, PropertyEditorChain> propertyEditorChainRegistry = new ConcurrentHashMap<>();
    private static final String ERROR_TARGET_TYPE_NULL = "Argument 'targetType' must not be null";
    public static final PropertyEditor NOOP_PROPERTY_EDITOR = new NoopPropertyEditor();

    private PropertyEditorResolver() {

    }

    /**
     * Removes all currently registered value editors.
     *
     * @since 2.4.0
     */
    public static void clear() {
        synchronized (LOCK) {
            propertyEditorRegistry.clear();
            propertyEditorChainRegistry.clear();
        }
    }

    /**
     * Locate a value editor for a given target type.
     * <p>
     * If the input {@code type} is an Enum then an instance of {@code EnumPropertyEditor}
     * is returned with the {@code type} set as {@code enumType}.
     *
     * @param type The Class object for the type to be edited
     *
     * @return An editor object for the given target class.
     * The result is null if no suitable editor can be found.
     *
     * @see griffon.core.editors.EnumPropertyEditor
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static PropertyEditor findEditor(@Nonnull Class<?> type) {
        requireNonNull(type, ERROR_TARGET_TYPE_NULL);
        LOG.trace("Searching PropertyEditor for {}", type.getName());

        PropertyEditor editor;
        if (Enum.class.isAssignableFrom(type)) {
            editor = new EnumPropertyEditor();
            ((EnumPropertyEditor) editor).setEnumType((Class<? extends Enum<?>>) type);
        } else {
            editor = doFindEditor(type);
        }

        if (editor == null) {
            // fallback
            editor = PropertyEditorManager.findEditor(type);
        }

        if (editor == null) {
            editor = new NoopPropertyEditor();
        }

        LOG.trace("PropertyEditor for {} is {}", type.getName(), editor.getClass().getName());
        return editor;
    }

    /**
     * Unregisters an editor class to edit values of the given target class.
     *
     * @param targetType the class object of the type to be edited
     *
     * @since 2.4.0
     */
    public static void unregisterEditor(@Nonnull Class<?> targetType) {
        requireNonNull(targetType, ERROR_TARGET_TYPE_NULL);
        synchronized (LOCK) {
            String targetTypeName = targetType.getName();
            propertyEditorChainRegistry.remove(targetTypeName);
            propertyEditorRegistry.remove(targetTypeName);
        }
    }

    /**
     * Registers an editor class to edit values of the given target class.
     * If the editor class is {@code null},
     * then any existing definition will be removed.
     * Thus this method can be used to cancel the registration.
     * The registration is canceled automatically
     * if either the target or editor class is unloaded.
     * <p>
     *
     * @param targetType  the class object of the type to be edited
     * @param editorClass the class object of the editor class
     *
     * @since 2.4.0
     */
    @SuppressWarnings("unchecked")
    public static void registerEditor(@Nonnull Class<?> targetType, @Nullable Class<? extends PropertyEditor> editorClass) {
        requireNonNull(targetType, ERROR_TARGET_TYPE_NULL);
        synchronized (LOCK) {
            String targetTypeName = targetType.getName();
            if (editorClass == null) {
                propertyEditorChainRegistry.remove(targetTypeName);
                propertyEditorRegistry.remove(targetTypeName);
                return;
            }

            // is targetType handled by a chain?
            PropertyEditorChain chain = propertyEditorChainRegistry.get(targetTypeName);
            if (chain != null) {
                PropertyEditorChain propertyEditorChain = chain.copyOf(editorClass);
                if (propertyEditorChain.getSize() > 1) {
                    propertyEditorChainRegistry.put(targetTypeName, propertyEditorChain);
                } else {
                    // standard registration
                    propertyEditorChainRegistry.remove(targetTypeName);
                    propertyEditorRegistry.put(targetTypeName, editorClass);
                }
            } else {
                // is targetType handled by an editor ?
                Class<? extends PropertyEditor> propertyEditorType = propertyEditorRegistry.get(targetTypeName);
                if (propertyEditorType != null) {
                    propertyEditorRegistry.remove(targetTypeName);
                    Class<? extends PropertyEditor>[] propertyEditorClasses = new Class[2];
                    propertyEditorClasses[0] = propertyEditorType;
                    propertyEditorClasses[1] = editorClass;
                    PropertyEditorChain propertyEditorChain = new PropertyEditorChain(targetType, propertyEditorClasses);
                    if (propertyEditorChain.getSize() > 1) {
                        propertyEditorChainRegistry.put(targetTypeName, propertyEditorChain);
                    } else {
                        // standard registration
                        propertyEditorChainRegistry.remove(targetTypeName);
                        propertyEditorRegistry.put(targetTypeName, editorClass);
                    }
                } else {
                    // standard registration
                    propertyEditorChainRegistry.remove(targetTypeName);
                    propertyEditorRegistry.put(targetTypeName, editorClass);
                }
            }
        }
    }

    @Nullable
    private static PropertyEditor doFindEditor(@Nonnull Class<?> targetType) {
        synchronized (LOCK) {
            String targetTypeName = targetType.getName();
            if (propertyEditorChainRegistry.containsKey(targetTypeName)) {
                PropertyEditorChain chain = propertyEditorChainRegistry.get(targetTypeName);
                return chain.copyOf();
            } else {
                Class<?> propertyEditorType = propertyEditorRegistry.get(targetTypeName);
                if (propertyEditorType != null) {
                    try {
                        return (PropertyEditor) propertyEditorType.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new IllegalStateException("Can't instantiate " + propertyEditorType, e);
                    }
                }
            }
        }
        return null;
    }

    public static final class NoopPropertyEditor extends PropertyEditorSupport {

    }

    private static final class WeakCache<K, V> {
        private final Map<K, Reference<V>> map = new WeakHashMap<>();

        private V get(K key) {
            Reference<V> reference = this.map.get(key);
            if (reference == null) {
                return null;
            } else {
                V value = reference.get();
                if (value == null) {
                    this.map.remove(key);
                }

                return value;
            }
        }

        private void put(K key, V value) {
            if (value != null) {
                this.map.put(key, new WeakReference<>(value));
            } else {
                this.map.remove(key);
            }
        }

        private void remove(K key) {
            this.map.remove(key);
        }

        private void clear() {
            this.map.clear();
        }
    }
}
