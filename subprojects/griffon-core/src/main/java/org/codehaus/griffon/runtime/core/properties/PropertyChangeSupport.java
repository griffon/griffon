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
package org.codehaus.griffon.runtime.core.properties;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.properties.NamedPropertyChangeListener;
import griffon.core.properties.PropertyChangeEvent;
import griffon.core.properties.PropertyChangeListener;
import griffon.core.properties.PropertySource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PropertyChangeSupport {
    private static final String DEFAULT_KEY = PropertySource.class.getName() + ".DEFAULT_KEY";

    private final Map<String, List<PropertyChangeListener>> listeners = new ConcurrentHashMap<>();
    private final PropertySource source;

    public PropertyChangeSupport(@Nonnull PropertySource source) {
        this.source = requireNonNull(source, "Argument 'source' must not be null");
    }

    public void addPropertyChangeListener(@Nullable PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        listeners.computeIfAbsent(DEFAULT_KEY, k -> new CopyOnWriteArrayList<>())
            .add(listener);
    }

    public void addPropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        if (isBlank(propertyName) || listener == null) {
            return;
        }

        listeners.computeIfAbsent(propertyName, k -> new CopyOnWriteArrayList<>())
            .add(wrap(propertyName, listener));
    }

    @Nonnull
    private PropertyChangeListener wrap(@Nonnull String propertyName, @Nonnull PropertyChangeListener listener) {
        return listener instanceof NamedPropertyChangeListener ? listener : new NamedPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(@Nullable PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        listeners.computeIfAbsent(DEFAULT_KEY, k -> new CopyOnWriteArrayList<>())
            .remove(listener);
    }

    public void removePropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        if (isBlank(propertyName) || listener == null) {
            return;
        }

        List<PropertyChangeListener> propertyChangeListeners = listeners.get(propertyName);
        if (propertyChangeListeners == null || propertyChangeListeners.isEmpty()) {
            return;
        }

        propertyChangeListeners.stream()
            .filter(l -> l == listener || NamedPropertyChangeListener.isWrappedBy(listener, (NamedPropertyChangeListener) l))
            .findFirst()
            .ifPresent(propertyChangeListeners::remove);
    }

    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners() {
        List<PropertyChangeListener> list = listeners.values().stream()
            .flatMap((Function<List<PropertyChangeListener>, Stream<PropertyChangeListener>>) Collection::stream)
            .collect(toList());
        return list.toArray(new PropertyChangeListener[list.size()]);
    }

    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners(@Nullable String propertyName) {
        if (isBlank(propertyName)) {
            return new PropertyChangeListener[0];
        }

        List<PropertyChangeListener> propertyChangeListeners = listeners.get(propertyName);
        if (propertyChangeListeners == null || propertyChangeListeners.isEmpty()) {
            return new PropertyChangeListener[0];
        }

        return propertyChangeListeners.toArray(new PropertyChangeListener[propertyChangeListeners.size()]);
    }

    public void firePropertyChange(@Nonnull PropertyChangeEvent event) {
        requireNonNull(event, "Argument 'event' must not be null");
        fire(listeners.get(DEFAULT_KEY), event);
        fire(listeners.get(event.getPropertyName()), event);
    }

    public <T> void firePropertyChange(@Nonnull String propertyName, @Nullable T oldValue, @Nullable T newValue) {
        requireNonBlank(propertyName, "Argument 'propertyName' must not be blank");
        firePropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
    }

    private void fire(@Nullable List<PropertyChangeListener> listeners, @Nonnull PropertyChangeEvent event) {
        if (listeners == null || listeners.isEmpty() || valuesAreEqual(event)) {
            return;
        }

        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

    public boolean hasListeners() {
        return getPropertyChangeListeners().length > 0;
    }

    public boolean hasListeners(@Nonnull String propertyName) {
        return getPropertyChangeListeners(propertyName).length > 0;
    }

    private boolean valuesAreEqual(@Nonnull PropertyChangeEvent event) {
        if (null == event) return true;
        if (event.getOldValue() == event.getNewValue()) return true;
        return (event.getOldValue() != null && event.getOldValue().equals(event.getNewValue())) ||
            (event.getNewValue() != null && event.getNewValue().equals(event.getOldValue()));
    }
}
