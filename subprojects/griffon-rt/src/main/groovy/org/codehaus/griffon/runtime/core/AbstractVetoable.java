/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core;

import griffon.core.Vetoable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class AbstractVetoable extends AbstractObservable implements Vetoable {
    protected VetoableChangeSupport vcs;

    public AbstractVetoable() {
        vcs = new VetoableChangeSupport(this);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vcs.addVetoableChangeListener(listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vcs.removeVetoableChangeListener(listener);
    }

    public VetoableChangeListener[] getVetoableChangeListeners() {
        return vcs.getVetoableChangeListeners();
    }

    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vcs.addVetoableChangeListener(propertyName, listener);
    }

    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vcs.removeVetoableChangeListener(propertyName, listener);
    }

    public VetoableChangeListener[] getVetoableChangeListeners(String propertyName) {
        return vcs.getVetoableChangeListeners(propertyName);
    }

    protected void fireVetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
        vcs.fireVetoableChange(event);
    }

    protected void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
        vcs.fireVetoableChange(propertyName, oldValue, newValue);
    }
}
