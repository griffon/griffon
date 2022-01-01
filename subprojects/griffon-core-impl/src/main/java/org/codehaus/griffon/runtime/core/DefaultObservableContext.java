/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConverterRegistry;
import griffon.core.Context;
import griffon.core.ObservableContext;
import griffon.core.util.TypeUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.5.0
 */
public class DefaultObservableContext extends DefaultContext implements ObservableContext {
    private static final String ERROR_LISTENER_NULL = "Argument 'listener' must not be null";
    private final List<ContextEventListener> listeners = new CopyOnWriteArrayList<>();

    private final ContextEventListener parentListener = event -> {
        String key = event.getKey();
        if (!hasKey(key)) {
            fireContextEvent(event.getType(), key, event.getOldValue(), event.getNewValue());
        }
    };

    public DefaultObservableContext(@Nonnull ConverterRegistry converterRegistry) {
        super(converterRegistry);
    }

    public DefaultObservableContext(@Nonnull ConverterRegistry converterRegistry, @Nullable Context parentContext) {
        super(converterRegistry, parentContext);
        if (parentContext instanceof ObservableContext) {
            ObservableContext observableParent = (ObservableContext) parentContext;
            observableParent.addContextEventListener(parentListener);
        }
    }

    @Override
    public void addContextEventListener(@Nonnull ContextEventListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeContextEventListener(@Nonnull ContextEventListener listener) {
        requireNonNull(listener, ERROR_LISTENER_NULL);
        listeners.remove(listener);
    }

    @Nonnull
    @Override
    public ContextEventListener[] getContextEventListeners() {
        return listeners.toArray(new ContextEventListener[listeners.size()]);
    }

    @Override
    public void put(@Nonnull String key, @Nullable Object value) {
        boolean localKey = hasKey(key);
        boolean parentKey = !localKey && containsKey(key);
        Object oldValue = get(key);
        super.put(key, value);
        boolean valuesAreEqual = TypeUtils.equals(oldValue, value);

        if (parentKey) {
            if (!valuesAreEqual) {
                fireContextEvent(ContextEvent.Type.UPDATE, key, oldValue, value);
            }
        } else {
            if (localKey) {
                fireContextEvent(ContextEvent.Type.UPDATE, key, oldValue, value);
            } else {
                fireContextEvent(ContextEvent.Type.ADD, key, null, value);
            }
        }
    }

    @Nullable
    @Override
    public Object remove(@Nonnull String key) {
        boolean localKey = hasKey(key);
        Object oldValue = super.remove(key);
        boolean localKeyRemoved = localKey && !hasKey(key);
        boolean containsKey = containsKey(key);

        try {
            return oldValue;
        } finally {
            if (localKeyRemoved) {
                if (containsKey) {
                    Object value = get(key);
                    boolean valuesAreEqual = TypeUtils.equals(oldValue, value);
                    if (!valuesAreEqual) {
                        fireContextEvent(ContextEvent.Type.UPDATE, key, oldValue, value);
                    }
                } else {
                    fireContextEvent(ContextEvent.Type.REMOVE, key, oldValue, null);
                }
            }
        }
    }

    @Nullable
    @Override
    public <T> T removeAs(@Nonnull String key) {
        boolean localKey = hasKey(key);
        T oldValue = super.removeAs(key);
        boolean localKeyRemoved = localKey && !hasKey(key);
        boolean containsKey = containsKey(key);

        try {
            return oldValue;
        } finally {
            if (localKeyRemoved) {
                if (containsKey) {
                    T value = getAs(key);
                    boolean valuesAreEqual = TypeUtils.equals(oldValue, value);
                    if (!valuesAreEqual) {
                        fireContextEvent(ContextEvent.Type.UPDATE, key, oldValue, value);
                    }
                } else {
                    fireContextEvent(ContextEvent.Type.REMOVE, key, oldValue, null);
                }
            }
        }
    }

    @Nullable
    @Override
    public <T> T removeConverted(@Nonnull String key, @Nonnull Class<T> type) {
        boolean localKey = hasKey(key);
        T oldValue = super.removeConverted(key, type);
        boolean localKeyRemoved = localKey && !hasKey(key);
        boolean containsKey = containsKey(key);

        try {
            return oldValue;
        } finally {
            if (localKeyRemoved) {
                if (containsKey) {
                    T value = getConverted(key, type);
                    boolean valuesAreEqual = TypeUtils.equals(oldValue, value);
                    if (!valuesAreEqual) {
                        fireContextEvent(ContextEvent.Type.UPDATE, key, oldValue, value);
                    }
                } else {
                    fireContextEvent(ContextEvent.Type.REMOVE, key, oldValue, null);
                }
            }
        }
    }

    @Override
    public void destroy() {
        if (getParentContext() instanceof ObservableContext) {
            ObservableContext observableParent = (ObservableContext) getParentContext();
            observableParent.removeContextEventListener(parentListener);
        }
        listeners.clear();
        super.destroy();
    }

    protected void fireContextEvent(@Nonnull ContextEvent.Type type, @Nonnull String key, @Nullable Object oldValue, @Nullable Object newValue) {
        fireContextEvent(new ContextEvent(type, key, oldValue, newValue));
    }

    protected void fireContextEvent(@Nonnull ContextEvent event) {
        for (ContextEventListener listener : listeners) {
            listener.contextChanged(event);
        }
    }
}
