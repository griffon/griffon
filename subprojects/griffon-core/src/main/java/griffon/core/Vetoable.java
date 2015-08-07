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
package griffon.core;

import com.googlecode.openbeans.VetoableChangeListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Describes objects that provide bound and vetoable properties as specified in the
 * <a href="http://docs.oracle.com/javase/tutorial/javabeans/TOC.html">Java
 * Bean Specification</a>.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface Vetoable extends Observable {
    /**
     * Add a VetoableListener to the listener list.
     * The listener is registered for all properties.
     * The same listener object may be added more than once, and will be called
     * as many times as it is added.
     * If <code>listener</code> is null, no exception is thrown and no action
     * is taken.
     *
     * @param listener The VetoableChangeListener to be added
     */

    void addVetoableChangeListener(@Nullable VetoableChangeListener listener);

    /**
     * Add a VetoableChangeListener for a specific property.  The listener
     * will be invoked only when a call on fireVetoableChange names that
     * specific property.
     * The same listener object may be added more than once.  For each
     * property,  the listener will be invoked the number of times it was added
     * for that property.
     * If <code>propertyName</code> or <code>listener</code> is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The VetoableChangeListener to be added
     */

    void addVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener);

    /**
     * Remove a VetoableChangeListener from the listener list.
     * This removes a VetoableChangeListener that was registered
     * for all properties.
     * If <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If <code>listener</code> is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener The VetoableChangeListener to be removed
     */
    void removeVetoableChangeListener(@Nullable VetoableChangeListener listener);

    /**
     * Remove a VetoableChangeListener for a specific property.
     * If <code>listener</code> was added more than once to the same event
     * source for the specified property, it will be notified one less time
     * after being removed.
     * If <code>propertyName</code> is null, no exception is thrown and no
     * action is taken.
     * If <code>listener</code> is null, or was never added for the specified
     * property, no exception is thrown and no action is taken.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The VetoableChangeListener to be removed
     */

    void removeVetoableChangeListener(@Nullable String propertyName, @Nullable VetoableChangeListener listener);

    /**
     * Returns the list of VetoableChangeListeners. If named vetoable change listeners
     * were added, then VetoableChangeListenerProxy wrappers will returned
     * <p/>
     *
     * @return List of VetoableChangeListeners and VetoableChangeListenerProxys
     *         if named property change listeners were added.
     */
    @Nonnull
    VetoableChangeListener[] getVetoableChangeListeners();

    /**
     * Returns an array of all the listeners which have been associated
     * with the named property.
     *
     * @param propertyName The name of the property being listened to
     * @return all the <code>VetoableChangeListeners</code> associated with
     *         the named property.  If no such listeners have been added,
     *         or if <code>propertyName</code> is null, an empty array is
     *         returned.
     */
    @Nonnull
    VetoableChangeListener[] getVetoableChangeListeners(@Nullable String propertyName);
}
