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
package org.codehaus.griffon.compile.beans

import org.codehaus.griffon.compile.core.BaseConstants
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ConstantsSpec extends Specification implements BaseConstants {
    void "Verify methods of org.codehaus.griffon.compile.bean.ObservableConstants"() {
        given:
        def actualMethods = ObservableConstants.OBSERVABLE_METHODS + ObservableConstants.OBSERVABLE_FIRE_METHODS

        expect:
        actualMethods.size() == OBSERVABLE_METHODS.size()
        actualMethods.each { assert OBSERVABLE_METHODS.contains(it.toString()) }
    }

    void "Verify methods of org.codehaus.griffon.compile.bean.VetoableConstants"() {
        given:
        def actualMethods = VetoableConstants.VETOABLE_METHODS + VetoableConstants.VETOABLE_FIRE_METHODS

        expect:
        actualMethods.size() == VETOABLE_METHODS.size()
        actualMethods.each { assert VETOABLE_METHODS.contains(it.toString()) }
    }

    private static final List<String> OBSERVABLE_METHODS = [
        'public void addPropertyChangeListener(@griffon.annotations.core.Nullable java.beans.PropertyChangeListener arg0)',
        'public void addPropertyChangeListener(@griffon.annotations.core.Nullable java.lang.String arg0, @griffon.annotations.core.Nullable java.beans.PropertyChangeListener arg1)',
        'public void removePropertyChangeListener(@griffon.annotations.core.Nullable java.beans.PropertyChangeListener arg0)',
        'public void removePropertyChangeListener(@griffon.annotations.core.Nullable java.lang.String arg0, @griffon.annotations.core.Nullable java.beans.PropertyChangeListener arg1)',
        '@griffon.annotations.core.Nonnull public java.beans.PropertyChangeListener[] getPropertyChangeListeners()',
        '@griffon.annotations.core.Nonnull public java.beans.PropertyChangeListener[] getPropertyChangeListeners(@griffon.annotations.core.Nullable java.lang.String arg0)',
        'protected void firePropertyChange(@griffon.annotations.core.Nonnull java.beans.PropertyChangeEvent arg0)',
        'protected void firePropertyChange(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nullable java.lang.Object arg1, @griffon.annotations.core.Nullable java.lang.Object arg2)'
    ]

    private static final List<String> VETOABLE_METHODS = [
        'public void addVetoableChangeListener(@griffon.annotations.core.Nullable java.beans.VetoableChangeListener arg0)',
        'public void addVetoableChangeListener(@griffon.annotations.core.Nullable java.lang.String arg0, @griffon.annotations.core.Nullable java.beans.VetoableChangeListener arg1)',
        'public void removeVetoableChangeListener(@griffon.annotations.core.Nullable java.beans.VetoableChangeListener arg0)',
        'public void removeVetoableChangeListener(@griffon.annotations.core.Nullable java.lang.String arg0, @griffon.annotations.core.Nullable java.beans.VetoableChangeListener arg1)',
        '@griffon.annotations.core.Nonnull public java.beans.VetoableChangeListener[] getVetoableChangeListeners()',
        '@griffon.annotations.core.Nonnull public java.beans.VetoableChangeListener[] getVetoableChangeListeners(@griffon.annotations.core.Nullable java.lang.String arg0)',
        'protected void fireVetoableChange(@griffon.annotations.core.Nonnull java.beans.PropertyChangeEvent arg0) throws java.beans.PropertyVetoException',
        'protected void fireVetoableChange(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nullable java.lang.Object arg1, @griffon.annotations.core.Nullable java.lang.Object arg2) throws java.beans.PropertyVetoException'
    ]
}
