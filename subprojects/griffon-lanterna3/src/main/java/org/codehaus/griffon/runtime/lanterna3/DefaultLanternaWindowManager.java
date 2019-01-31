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
package org.codehaus.griffon.runtime.lanterna3;

import com.googlecode.lanterna.gui2.Window;
import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.lanterna3.LanternaWindowDisplayHandler;
import griffon.lanterna3.LanternaWindowManager;
import org.codehaus.griffon.runtime.core.view.AbstractWindowManager;

import javax.inject.Inject;
import javax.inject.Named;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class DefaultLanternaWindowManager extends AbstractWindowManager<Window> implements LanternaWindowManager {
    @Inject
    @Nonnull
    public DefaultLanternaWindowManager(@Nonnull GriffonApplication application,
                                        @Nonnull @Named("windowDisplayHandler") LanternaWindowDisplayHandler windowDisplayHandler) {
        super(application, windowDisplayHandler);
        requireNonNull(application.getEventRouter(), "Argument 'application.eventRouter' must not be null");
    }

    @Override
    protected void doAttach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
    }

    @Override
    protected void doDetach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
    }

    @Override
    protected boolean isWindowVisible(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        return true;
    }

    /*
    public void handleClose(@Nonnull Window widget) {
        if (getApplication().getPhase() == ApplicationPhase.SHUTDOWN) {
            return;
        }

        int visibleWindows = getWindows().size();
        if (visibleWindows <= 1 && isAutoShutdown() && !getApplication().shutdown()) {
            show(widget);
        }
    }
    */

    /**
     * WindowAdapter that optionally invokes hide() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    /*
    protected class WindowHelper extends WindowAdapter {
        @Override
        public void onWindowClosed(@Nonnull Window window) {
            super.onWindowClosed(window);
            event(ApplicationEvent.WINDOW_HIDDEN, asList(findWindowName(window), window));
            handleClose(window);
        }

        @Override
        public void onWindowShown(Window window) {
            super.onWindowShown(window);
            event(ApplicationEvent.WINDOW_SHOWN, asList(findWindowName(window), window));
        }
    }
    */
}
