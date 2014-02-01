/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.pivot;

import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.env.ApplicationPhase;
import griffon.core.event.EventRouter;
import griffon.exceptions.InstanceNotFoundException;
import griffon.pivot.PivotWindowDisplayHandler;
import griffon.pivot.PivotWindowManager;
import griffon.pivot.support.adapters.WindowStateAdapter;
import org.apache.pivot.util.Vote;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;
import org.codehaus.griffon.runtime.core.view.AbstractWindowManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultPivotWindowManager extends AbstractWindowManager<Window> implements PivotWindowManager {
    private final WindowStateHelper windowStateHelper = new WindowStateHelper();

    @Inject
    @Nonnull
    public DefaultPivotWindowManager(@Nonnull GriffonApplication application, @Nonnull @Named("windowDisplayHandler") PivotWindowDisplayHandler windowDisplayHandler) {
        super(application, windowDisplayHandler);
        requireNonNull(application.getEventRouter(), "Argument 'application.eventRouter' cannot be null");
    }

    @Override
    protected void doAttach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.getWindowStateListeners().add(windowStateHelper);
    }

    @Override
    protected void doDetach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.getWindowStateListeners().remove(windowStateHelper);
    }

    @Override
    protected boolean isWindowVisible(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        return window.isVisible();
    }

    public void handleClose(@Nonnull Window widget) {
        if (getApplication().getPhase() == ApplicationPhase.SHUTDOWN) {
            return;
        }
        int visibleWindows = 0;
        for (Window window : getWindows()) {
            if (window.isShowing()) {
                visibleWindows++;
            }
        }

        if (visibleWindows <= 1 && isAutoShutdown()) {
            if (!getApplication().shutdown())
                show(widget);
        }
    }

    /**
     * WindowStateAdapter that triggers application events when a window is shown/hidden.
     *
     * @author Andres Almiray
     */
    private class WindowStateHelper extends WindowStateAdapter {
        @Override
        public Vote previewWindowOpen(Window arg0) {
            return Vote.APPROVE;
        }

        @Override
        public Vote previewWindowClose(Window arg0) {
            handleClose(arg0);
            return Vote.APPROVE;
        }

        /**
         * Triggers a <tt>WindowShown</tt> event with the window as sole argument
         */
        public void windowOpened(Window arg0) {
            event(ApplicationEvent.WINDOW_SHOWN, asList(arg0));
        }

        /**
         * Triggers a <tt>WindowHidden</tt> event with the window as sole argument
         */
        public void windowClosed(Window arg0, Display arg1, Window arg2) {
            event(ApplicationEvent.WINDOW_HIDDEN, asList(arg0));
        }
    }

    private void event(@Nonnull ApplicationEvent evt, List<?> args) {
        try {
            EventRouter eventRouter = getApplication().getEventRouter();
            eventRouter.publishEvent(evt.getName(), args);
        } catch (InstanceNotFoundException infe) {
            if (getApplication().getPhase() != ApplicationPhase.SHUTDOWN) {
                throw infe;
            }
        }
    }
}
