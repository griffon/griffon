/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.beans;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.beans.Vetoable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class AbstractVetoable extends AbstractObservable implements Vetoable {
    protected final VetoableChangeSupport vcs;

    public AbstractVetoable() {
        vcs = new VetoableChangeSupport(this);
    }

    @Override
    public void addVetoableChangeListener(@Nullable VetoableChangeListener listener) {
        vcs.addVetoableChangeListener(listener);
    }

    @Override
    public void removeVetoableChangeListener(@Nullable VetoableChangeListener listener) {
        vcs.removeVetoableChangeListener(listener);
    }

    @Nonnull
    @Override
    public VetoableChangeListener[] getVetoableChangeListeners() {
        return vcs.getVetoableChangeListeners();
    }

    @Override
    public void addVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener) {
        vcs.addVetoableChangeListener(propertyName, listener);
    }

    @Override
    public void removeVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener) {
        vcs.removeVetoableChangeListener(propertyName, listener);
    }

    @Nonnull
    @Override
    public VetoableChangeListener[] getVetoableChangeListeners(@Nullable String propertyName) {
        return vcs.getVetoableChangeListeners(propertyName);
    }

    protected void fireVetoableChange(@Nonnull PropertyChangeEvent event) throws PropertyVetoException {
        vcs.fireVetoableChange(requireNonNull(event, "Argument 'event' must not be null"));
    }

    protected void fireVetoableChange(@Nonnull String propertyName, @Nullable Object oldValue, @Nullable Object newValue) throws PropertyVetoException {
        vcs.fireVetoableChange(requireNonBlank(propertyName, "Argument 'propertyName' must not be blank"), oldValue, newValue);
    }
}
