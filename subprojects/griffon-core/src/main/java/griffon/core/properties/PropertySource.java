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
package griffon.core.properties;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface PropertySource {
    /**
     * Adds the given PropertyChangeListener to the listener list.<p>
     * The listener is registered for all bound properties of this class.
     *
     * @param listener the PropertyChangeListener to be added
     * @see #removePropertyChangeListener(PropertyChangeListener)
     */
    void addPropertyChangeListener(@Nullable PropertyChangeListener listener);

    /**
     * Removes the given PropertyChangeListener from the listener list.<p>
     * The listener is registered an specific property of this class.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     the PropertyChangeListener to be added
     * @see #removePropertyChangeListener(String, PropertyChangeListener)
     */
    void addPropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener);

    /**
     * Removes the given PropertyChangeListener from the listener list.<p>
     * This method should be used to remove PropertyChangeListeners that were
     * registered for all bound properties of this class.
     *
     * @param listener the PropertyChangeListener to be removed
     * @see #addPropertyChangeListener(PropertyChangeListener)
     */
    void removePropertyChangeListener(@Nullable PropertyChangeListener listener);

    /**
     * Removes the given PropertyChangeListener from the listener list.<p>
     * This method should be used to remove PropertyChangeListeners that were
     * registered for an specific property of this class.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     the PropertyChangeListener to be removed
     * @see #addPropertyChangeListener(String, PropertyChangeListener)
     */
    void removePropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener);

    /**
     * Returns an array of all the listeners that were added with addPropertyChangeListener().<p>
     *
     * @return all of the {@code PropertyChangeListeners} added or an empty array if no
     * listeners have been added.
     */
    @Nonnull
    PropertyChangeListener[] getPropertyChangeListeners();

    /**
     * Returns an array of all the listeners which have been associated
     * with the named property.
     *
     * @param propertyName The name of the property being listened to
     * @return all of the <code>PropertyChangeListeners</code> associated with
     * the named property.  If no such listeners have been added,
     * or if <code>propertyName</code> is null, an empty array is
     * returned.
     */
    @Nonnull
    PropertyChangeListener[] getPropertyChangeListeners(@Nullable String propertyName);
}
