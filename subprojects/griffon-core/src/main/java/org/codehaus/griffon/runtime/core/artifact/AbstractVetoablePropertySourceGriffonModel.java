/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.properties.PropertyChangeEvent;
import griffon.core.properties.VetoableChangeListener;
import griffon.core.properties.VetoablePropertySource;
import griffon.exceptions.PropertyVetoException;
import org.codehaus.griffon.runtime.core.properties.VetoableChangeSupport;

/**
 * GriffonModel implementation that can trigger vetoable property change events.
 *
 * @author Andres Almiray
 * @since 3.0.0
 */
public abstract class AbstractVetoablePropertySourceGriffonModel extends AbstractPropertySourceGriffonModel implements VetoablePropertySource {
    protected final VetoableChangeSupport vcs;

    public AbstractVetoablePropertySourceGriffonModel() {
        super();
        vcs = new VetoableChangeSupport(this);
    }

    @Override
    public void addVetoableChangeListener(@Nullable VetoableChangeListener listener) {
        vcs.addVetoableChangeListener(listener);
    }

    @Override
    public void addVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener) {
        vcs.addVetoableChangeListener(propertyName, listener);
    }

    @Override
    public void removeVetoableChangeListener(@Nullable VetoableChangeListener listener) {
        vcs.removeVetoableChangeListener(listener);
    }

    @Override
    public void removeVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener) {
        vcs.removeVetoableChangeListener(propertyName, listener);
    }

    @Nonnull
    @Override
    public VetoableChangeListener[] getVetoableChangeListeners() {
        return vcs.getVetoableChangeListeners();
    }

    @Nonnull
    @Override
    public VetoableChangeListener[] getVetoableChangeListeners(@Nullable String propertyName) {
        return vcs.getVetoableChangeListeners(propertyName);
    }

    protected void fireVetoableChange(@Nonnull PropertyChangeEvent event) throws PropertyVetoException {
        vcs.fireVetoableChange(event);
    }

    protected void fireVetoableChange(@Nonnull String propertyName, @Nullable Object oldValue, @Nullable Object newValue) throws PropertyVetoException {
        vcs.fireVetoableChange(propertyName, oldValue, newValue);
    }
}
