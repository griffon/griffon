/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
import griffon.core.properties.NamedVetoableChangeListener;
import griffon.core.properties.PropertyChangeEvent;
import griffon.core.properties.PropertySource;
import griffon.core.properties.VetoableChangeListener;
import griffon.core.properties.VetoablePropertySource;
import griffon.exceptions.PropertyVetoException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import static griffon.util.StringUtils.isBlank;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class VetoableChangeSupport {
    private static final String DEFAULT_KEY = PropertySource.class.getName() + ".DEFAULT_KEY";

    private final Map<String, List<VetoableChangeListener>> listeners = new ConcurrentHashMap<>();
    private final VetoablePropertySource source;

    public VetoableChangeSupport(@Nonnull VetoablePropertySource source) {
        this.source = requireNonNull(source, "Argument 'source' must not be null");
    }

    public void addVetoableChangeListener(@Nullable VetoableChangeListener listener) {
        if (listener == null) {
            return;
        }

        listeners.computeIfAbsent(DEFAULT_KEY, k -> new CopyOnWriteArrayList<>())
            .add(listener);
    }

    public void addVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener) {
        if (isBlank(propertyName) || listener == null) {
            return;
        }

        listeners.computeIfAbsent(propertyName, k -> new CopyOnWriteArrayList<>())
            .add(wrap(propertyName, listener));
    }

    @Nonnull
    private VetoableChangeListener wrap(@Nonnull String propertyName, @Nonnull VetoableChangeListener listener) {
        return listener instanceof NamedVetoableChangeListener ? listener : new NamedVetoableChangeListener(propertyName, listener);
    }

    public void removeVetoableChangeListener(@Nullable VetoableChangeListener listener) {
        if (listener == null) {
            return;
        }

        listeners.computeIfAbsent(DEFAULT_KEY, k -> new CopyOnWriteArrayList<>())
            .remove(listener);
    }

    public void removeVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener) {
        if (isBlank(propertyName) || listener == null) {
            return;
        }

        List<VetoableChangeListener> propertyChangeListeners = listeners.get(propertyName);
        if (propertyChangeListeners == null || propertyChangeListeners.isEmpty()) {
            return;
        }

        propertyChangeListeners.stream()
            .filter(l -> l == listener || NamedVetoableChangeListener.isWrappedBy(listener, (NamedVetoableChangeListener) l))
            .findFirst()
            .ifPresent(propertyChangeListeners::remove);
    }

    @Nonnull
    public VetoableChangeListener[] getVetoableChangeListeners() {
        List<VetoableChangeListener> list = listeners.values().stream()
            .flatMap((Function<List<VetoableChangeListener>, Stream<VetoableChangeListener>>) Collection::stream)
            .collect(toList());
        return list.toArray(new VetoableChangeListener[list.size()]);
    }

    @Nonnull
    public VetoableChangeListener[] getVetoableChangeListeners(@Nullable String propertyName) {
        if (isBlank(propertyName)) {
            return new VetoableChangeListener[0];
        }

        List<VetoableChangeListener> propertyChangeListeners = listeners.get(propertyName);
        if (propertyChangeListeners == null || propertyChangeListeners.isEmpty()) {
            return new VetoableChangeListener[0];
        }

        return propertyChangeListeners.toArray(new VetoableChangeListener[propertyChangeListeners.size()]);
    }

    public void fireVetoableChange(@Nonnull PropertyChangeEvent event) throws PropertyVetoException {
        requireNonNull(event, "Argument 'event' must not be null");

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
            String name = event.getPropertyName();

            List<VetoableChangeListener> common = listeners.get(DEFAULT_KEY);
            List<VetoableChangeListener> named = (name != null) ? listeners.get(name) : null;

            List<VetoableChangeListener> listeners = new ArrayList<>();
            if (common != null) listeners.addAll(common);
            if (named != null) listeners.addAll(named);

            if (!listeners.isEmpty()) {
                int current = 0;
                try {
                    while (current < listeners.size()) {
                        listeners.get(current).vetoableChange(event);
                        current++;
                    }
                } catch (PropertyVetoException veto) {
                    event = new PropertyChangeEvent(this.source, name, newValue, oldValue);
                    for (int i = 0; i < current; i++) {
                        try {
                            listeners.get(i).vetoableChange(event);
                        } catch (PropertyVetoException ignored) {
                            // noop
                        }
                    }
                    throw veto;
                }
            }
        }
    }

    public <T> void fireVetoableChange(@Nonnull String propertyName, @Nullable T oldValue, @Nullable T newValue) throws PropertyVetoException {
        requireNonBlank(propertyName, "Argument 'propertyName' must not be blank");
        fireVetoableChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
    }

    public boolean hasVetoableListeners() {
        return getVetoableChangeListeners().length > 0;
    }

    public boolean hasVetoableListeners(@Nonnull String propertyName) {
        return getVetoableChangeListeners(propertyName).length > 0;
    }
}
