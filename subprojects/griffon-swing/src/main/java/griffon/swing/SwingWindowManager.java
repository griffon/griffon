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
package griffon.swing;

import griffon.core.view.WindowManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JInternalFrame;
import java.awt.Window;
import java.util.Set;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface SwingWindowManager extends WindowManager<Window> {
    /**
     * Returns a set of names related to all JInternalFrames managed by this manager.
     *
     * @return a Set of managed window names
     * @since 2.3.0
     */
    @Nonnull
    Set<String> getInternalWindowNames();

    /**
     * Lookups a name related to a JInternalFrame.
     *
     * @param window the window to be looked up
     * @return the name of the window if it's managed by this manager, <tt>null</tt> otherwise.
     * @since 2.3.0
     */
    @Nullable
    String findInternalWindowName(@Nonnull JInternalFrame window);

    /**
     * Lookups the index related to a JInternalFrame.
     *
     * @param window the window to be looked up
     * @return the index of the window if it's managed by this manager, <tt>-1</tt> otherwise.
     * @since 2.3.0
     */
    int indexOfInternal(@Nonnull JInternalFrame window);
}
