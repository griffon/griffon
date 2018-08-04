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
package org.codehaus.griffon.runtime.core.properties;

import griffon.core.properties.PropertyChangeEvent;
import griffon.core.properties.PropertyChangeListener;
import griffon.core.properties.PropertySource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class AbstractPropertySource implements PropertySource {
    protected final PropertyChangeSupport pcs;

    public AbstractPropertySource() {
        pcs = new PropertyChangeSupport(this);
    }

    @Override
    public void addPropertyChangeListener(@Nullable PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(@Nullable PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    @Nonnull
    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    @Nonnull
    @Override
    public PropertyChangeListener[] getPropertyChangeListeners(@Nullable String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(@Nonnull PropertyChangeEvent<?> event) {
        pcs.firePropertyChange(event);
    }

    protected void firePropertyChange(@Nonnull String propertyName, @Nullable Object oldValue, @Nullable Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }
}
