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
package griffon.core.view;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.core.ShutdownHandler;

import java.util.Collection;
import java.util.Set;

/**
 * Controls a set of windows that belong to the application.<p>
 * Windows that are controlled by a WindowManager can be shown/hidden
 * using a custom strategy ({@code WindowDisplayHandler})
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface WindowManager<W> extends ShutdownHandler {
    /**
     * Finds a Window by name.
     *
     * @param name the value of the of the Window's name
     * @return a Window if a match is found, null otherwise.
     */
    @Nullable
    W findWindow(@Nonnull String name);

    /**
     * Convenience method to get a managed Window by index.<p>
     * Follows the Groovy conventions for overriding the [] operator.
     *
     * @param index the index of the Window to be retrieved
     * @return the Window found at the specified index
     * @throws ArrayIndexOutOfBoundsException if the index is invalid (below 0 or greater than the size
     *                                        of the managed windows list)
     */
    @Nullable
    W getAt(int index);

    /**
     * Finds the Window that should be displayed during the Ready phase of an application.<p>
     * The WindowManager expects a configuration flag <code>windowManager.startingWindow</code> to be
     * present in order to determine which Window will be displayed during the Ready phase. If no configuration
     * is found the WindowManager will pick the first Window found in the list of managed windows.<p>
     * The configuration flag accepts two value types:<ul>
     * <li>a String that defines the name of the Window. You must make sure the Window has a matching name property.</li>
     * <li>a Number that defines the index of the Window in the list of managed windows.</li>
     * </ul>
     *
     * @return a Window that matches the given criteria or null if no match is found.
     */
    @Nullable
    W getStartingWindow();

    /**
     * Returns the collection of windows managed by this manager.
     *
     * @return a Collection of currently managed windows
     */
    @Nonnull
    Collection<W> getWindows();

    /**
     * Returns a set of names related to all windows managed by this manager.
     *
     * @return a Set of managed window names
     * @since 2.3.0
     */
    @Nonnull
    Set<String> getWindowNames();

    /**
     * Lookups a name related to a Window.
     *
     * @param window the window to be looked up
     * @return the name of the window if it's managed by this manager, <tt>null</tt> otherwise.
     * @since 2.3.0
     */
    @Nullable
    String findWindowName(@Nonnull W window);

    /**
     * Lookups the index related to a Window.
     *
     * @param window the window to be looked up
     * @return the index of the window if it's managed by this manager, <tt>-1</tt> otherwise.
     * @since 2.3.0
     */
    int indexOf(@Nonnull W window);

    /**
     * Registers a window on this manager if an only if the window is not null
     * and it's not registered already.
     *
     * @param name   the value of the of the Window's name
     * @param window the window to be added to the list of managed windows
     */
    void attach(@Nonnull String name, @Nonnull W window);

    /**
     * Removes the window from the list of manages windows if and only if it
     * is registered with this manager.
     *
     * @param name the value of the of the Window's name
     */
    void detach(@Nonnull String name);

    /**
     * Shows the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param window the window to show
     */
    void show(@Nonnull W window);

    /**
     * Shows the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param name the name of window to show
     */
    void show(@Nonnull String name);

    /**
     * Hides the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param window the window to hide
     */
    void hide(@Nonnull W window);

    /**
     * Hides the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param name the name of window to hide
     */
    void hide(@Nonnull String name);

    boolean canShutdown(@Nonnull GriffonApplication app);

    /**
     * Hides all visible windows
     */
    void onShutdown(@Nonnull GriffonApplication app);

    /**
     * Counts how many Windows are visible regardless of their attached status to this WindowManager.
     *
     * @return the number of visible Windows
     */
    int countVisibleWindows();

    /**
     * Returns the value of the "application.autoShutdown" configuration flag.
     *
     * @return the value of the "application.autoShutdown" configuration flag.
     */
    boolean isAutoShutdown();
}
