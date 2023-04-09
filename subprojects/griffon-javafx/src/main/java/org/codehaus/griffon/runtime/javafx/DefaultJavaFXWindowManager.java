/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package org.codehaus.griffon.runtime.javafx;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.core.env.ApplicationPhase;
import griffon.core.events.WindowHiddenEvent;
import griffon.core.events.WindowShownEvent;
import griffon.javafx.JavaFXWindowDisplayHandler;
import griffon.javafx.JavaFXWindowManager;
import javafx.event.EventHandler;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.codehaus.griffon.runtime.core.view.AbstractWindowManager;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultJavaFXWindowManager extends AbstractWindowManager<Window> implements JavaFXWindowManager {
    protected final OnWindowHidingHelper onWindowHiding = new OnWindowHidingHelper();
    protected final OnCloseRequestHelper onCloseRequest = new OnCloseRequestHelper();
    protected final OnWindowShownHelper onWindowShown = new OnWindowShownHelper();
    protected final OnWindowHiddenHelper onWindowHidden = new OnWindowHiddenHelper();

    @Inject
    @Nonnull
    public DefaultJavaFXWindowManager(@Nonnull GriffonApplication application, @Nonnull @Named("windowDisplayHandler") JavaFXWindowDisplayHandler windowDisplayHandler) {
        super(application, windowDisplayHandler);
        requireNonNull(application.getEventRouter(), "Argument 'application.eventRouter' must not be null");
    }

    @Override
    protected void doAttach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.setOnCloseRequest(onCloseRequest);
        window.setOnHiding(onWindowHiding);
        window.setOnShown(onWindowShown);
        window.setOnHidden(onWindowHidden);
    }

    @Override
    protected void doDetach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.setOnCloseRequest(null);
        window.setOnHiding(null);
        window.setOnShown(null);
        window.setOnHidden(null);
    }

    @Override
    protected boolean isWindowVisible(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        return window.isShowing();
    }

    public boolean handleClose(@Nonnull Window window) {
        if (getApplication().getPhase() == ApplicationPhase.SHUTDOWN) {
            return false;
        }

        List<Window> visibleWindows = new ArrayList<>();
        for (Window w : getWindows()) {
            if (w.isShowing()) {
                visibleWindows.add(w);
            }
        }

        if (isAutoShutdown() && visibleWindows.size() <= 1 && visibleWindows.contains(window) && !getApplication().shutdown()) {
            show(window);
            return false;
        }
        return true;
    }

    /**
     * WindowAdapter that invokes close() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    protected class OnCloseRequestHelper implements EventHandler<WindowEvent> {
        public void handle(WindowEvent event) {
            if (!handleClose((Window) event.getSource())) {
                event.consume();
            }
        }
    }

    /**
     * WindowAdapter that invokes hide() when the window is about to be hidden.
     *
     * @author Andres Almiray
     */
    protected class OnWindowHidingHelper implements EventHandler<WindowEvent> {
        public void handle(WindowEvent event) {
            hide((Window) event.getSource());
        }
    }

    /**
     * Listener that triggers application events when a window is shown.
     *
     * @author Andres Almiray
     */
    protected class OnWindowShownHelper implements EventHandler<WindowEvent> {
        /**
         * Triggers a <tt>WindowShown</tt> event with the window as sole argument
         */
        public void handle(WindowEvent windowEvent) {
            Window window = (Window) windowEvent.getSource();
            event(WindowShownEvent.of(findWindowName(window), window));
        }
    }

    /**
     * Listener that triggers application events when a window is hidden.
     *
     * @author Andres Almiray
     */
    protected class OnWindowHiddenHelper implements EventHandler<WindowEvent> {
        /**
         * Triggers a <tt>WindowHidden</tt> event with the window as sole argument
         */
        public void handle(WindowEvent windowEvent) {
            Window window = (Window) windowEvent.getSource();
            event(WindowHiddenEvent.of(findWindowName(window), window));
        }
    }
}
