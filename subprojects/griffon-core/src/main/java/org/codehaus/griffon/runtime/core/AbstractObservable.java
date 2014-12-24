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
package org.codehaus.griffon.runtime.core;

import griffon.core.Observable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractObservable implements Observable {
    protected final PropertyChangeSupport pcs;

    public AbstractObservable() {
        pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(@Nullable PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(@Nullable PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners(@Nullable String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(@Nonnull PropertyChangeEvent event) {
        pcs.firePropertyChange(requireNonNull(event, "Argument 'event' must not be null"));
    }

    protected void firePropertyChange(@Nonnull String propertyName, @Nullable Object oldValue, @Nullable Object newValue) {
        pcs.firePropertyChange(requireNonBlank(propertyName, "Argument 'propertyName' must not be blank"), oldValue, newValue);
    }
}