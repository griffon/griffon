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
package org.codehaus.griffon.runtime.swing;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.core.env.ApplicationPhase;
import griffon.core.events.WindowAttachedEvent;
import griffon.core.events.WindowDetachedEvent;
import griffon.core.events.WindowHiddenEvent;
import griffon.core.events.WindowShownEvent;
import griffon.swing.SwingWindowDisplayHandler;
import griffon.swing.SwingWindowManager;
import griffon.util.StringUtils;
import org.codehaus.griffon.runtime.core.view.AbstractWindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultSwingWindowManager extends AbstractWindowManager<Window> implements SwingWindowManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSwingWindowManager.class);
    protected final WindowHelper windowHelper = new WindowHelper();
    protected final ComponentHelper componentHelper = new ComponentHelper();
    protected final InternalFrameHelper internalFrameHelper = new InternalFrameHelper();
    protected final Map<String, JInternalFrame> internalFrames = Collections.synchronizedMap(new LinkedHashMap<String, JInternalFrame>());
    protected boolean hideBeforeHandler = false;

    @Inject
    @Nonnull
    public DefaultSwingWindowManager(@Nonnull GriffonApplication application, @Nonnull @Named("windowDisplayHandler") SwingWindowDisplayHandler windowDisplayHandler) {
        super(application, windowDisplayHandler);
        requireNonNull(application.getEventRouter(), "Argument 'application.eventRouter' must not be null");
    }

    /**
     * Finds a JInternalFrame by name.
     *
     * @param name the value of the name: property
     *
     * @return a JInternalFrame if a match is found, null otherwise.
     *
     * @since 2.0.0
     */
    public JInternalFrame findInternalFrame(String name) {
        if (!StringUtils.isBlank(name)) {
            for (JInternalFrame internalFrame : internalFrames.values()) {
                if (name.equals(internalFrame.getName())) { return internalFrame; }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Set<String> getInternalWindowNames() {
        return Collections.unmodifiableSet(internalFrames.keySet());
    }

    @Nullable
    @Override
    public String findInternalWindowName(@Nonnull JInternalFrame window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        synchronized (internalFrames) {
            for (Map.Entry<String, JInternalFrame> e : internalFrames.entrySet()) {
                if (e.getValue().equals(window)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public int indexOfInternal(@Nonnull JInternalFrame window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        synchronized (internalFrames) {
            int index = 0;
            for (JInternalFrame w : internalFrames.values()) {
                if (window.equals(w)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    /**
     * Returns the list of internal frames managed by this manager.
     *
     * @return a List of currently managed internal frames
     *
     * @since 2.0.0
     */
    public Collection<JInternalFrame> getInternalFrames() {
        return unmodifiableCollection(internalFrames.values());
    }

    /**
     * Registers an internal frame on this manager if an only if the internal frame is not null
     * and it's not registered already.
     *
     * @param name          the value of the of the Window's name
     * @param internalFrame the internal frame to be added to the list of managed internal frames
     *
     * @since 2.0.0
     */
    public void attach(@Nonnull String name, @Nonnull JInternalFrame internalFrame) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(internalFrame, ERROR_WINDOW_NULL);
        if (internalFrames.containsKey(name)) {
            JInternalFrame window2 = internalFrames.get(name);
            if (window2 != internalFrame) {
                detach(name);
            }
        }

        doAttach(internalFrame);

        LOG.debug("Attaching internal frame with name: '{}' at index {} {}", name, internalFrames.size(), internalFrame);
        internalFrames.put(name, internalFrame);
        event(WindowAttachedEvent.of(name, internalFrame));
    }

    protected void doAttach(@Nonnull JInternalFrame internalFrame) {
        internalFrame.addInternalFrameListener(internalFrameHelper);
        internalFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Removes the internal frame from the list of manages internal frames if and only if it
     * is registered with this manager.
     *
     * @param name the value of the of the Window's name
     *
     * @since 2.0.0
     */
    @Override
    public void detach(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        if (internalFrames.containsKey(name)) {
            JInternalFrame window = internalFrames.get(name);

            doDetach(window);

            LOG.debug("Detaching internalFrame with name: '{}' {}", name, window);
            internalFrames.remove(name);
            event(WindowDetachedEvent.of(name, window));
        }
    }

    protected void doDetach(@Nonnull JInternalFrame internalFrame) {
        internalFrame.removeInternalFrameListener(internalFrameHelper);
    }

    /**
     * Shows the internal frame.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param internalFrame the internal frame to show
     *
     * @since 2.0.0
     */
    public void show(@Nonnull final JInternalFrame internalFrame) {
        requireNonNull(internalFrame, ERROR_WINDOW_NULL);
        if (!internalFrames.containsValue(internalFrame)) {
            return;
        }

        String windowName = null;
        int windowIndex = -1;
        synchronized (internalFrames) {
            int i = 0;
            for (Map.Entry<String, JInternalFrame> entry : internalFrames.entrySet()) {
                if (entry.getValue() == internalFrame) {
                    windowName = entry.getKey();
                    windowIndex = i;
                    break;
                }
                i++;
            }
        }

        final String name = windowName;
        final int index = windowIndex;

        getApplication().getUIThreadManager().executeInsideUIAsync(() -> {
            LOG.debug("Showing window with name: '{}' at index {} {}", name, index, internalFrame);
            //noinspection ConstantConditions
            resolveSwingWindowDisplayHandler().show(name, internalFrame);
        });
    }

    /**
     * Hides the internal frame.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param internalFrame the internal frame to hide
     *
     * @since 2.0.0
     */
    public void hide(@Nonnull final JInternalFrame internalFrame) {
        requireNonNull(internalFrame, ERROR_WINDOW_NULL);
        if (!internalFrames.containsValue(internalFrame)) {
            return;
        }

        String windowName = null;
        int windowIndex = -1;
        synchronized (internalFrames) {
            int i = 0;
            for (Map.Entry<String, JInternalFrame> entry : internalFrames.entrySet()) {
                if (entry.getValue() == internalFrame) {
                    windowName = entry.getKey();
                    windowIndex = i;
                    break;
                }
                i++;
            }
        }

        final String name = windowName;
        final int index = windowIndex;

        getApplication().getUIThreadManager().executeInsideUIAsync(() -> {
            LOG.debug("Hiding window with name: '{}' at index {} {}", name, index, internalFrame);
            //noinspection ConstantConditions
            resolveSwingWindowDisplayHandler().hide(name, internalFrame);
        });
    }

    /**
     * Should the window be hidden before all ShutdownHandlers be called ?
     *
     * @return current value
     */
    public boolean isHideBeforeHandler() {
        return hideBeforeHandler;
    }

    /**
     * Set if the window should be hidden before all ShutdownHandler be called.
     *
     * @param hideBeforeHandler new value
     */
    public void setHideBeforeHandler(boolean hideBeforeHandler) {
        this.hideBeforeHandler = hideBeforeHandler;
    }

    @Nonnull
    protected SwingWindowDisplayHandler resolveSwingWindowDisplayHandler() {
        return (SwingWindowDisplayHandler) resolveWindowDisplayHandler();
    }

    @Override
    protected void doAttach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.addWindowListener(windowHelper);
        window.addComponentListener(componentHelper);
    }

    @Override
    protected void doDetach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.removeWindowListener(windowHelper);
        window.removeComponentListener(componentHelper);
    }

    @Override
    protected boolean isWindowVisible(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        return window.isVisible();
    }

    /**
     * WindowAdapter that optionally invokes hide() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    protected class WindowHelper extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent event) {
            if (getApplication().getPhase() == ApplicationPhase.SHUTDOWN) {
                return;
            }
            int visibleWindows = countVisibleWindows();

            if (isHideBeforeHandler() || visibleWindows > 0) {
                hide(event.getWindow());
            }

            if (visibleWindows <= 1 && isAutoShutdown()) {
                LOG.debug("Attempting to shutdown application");
                if (!getApplication().shutdown()) { show(event.getWindow()); }
            }
        }
    }

    /**
     * ComponentAdapter that triggers application events when a window is shown/hidden.
     *
     * @author Andres Almiray
     */
    protected class ComponentHelper extends ComponentAdapter {
        /**
         * Triggers a <tt>WindowShown</tt> event with the window as sole argument
         */
        @Override
        public void componentShown(ComponentEvent event) {
            Window window = (Window) event.getSource();
            event(WindowShownEvent.of(findWindowName(window), window));
        }

        /**
         * Triggers a <tt>WindowHidden</tt> event with the window as sole argument
         */
        @Override
        public void componentHidden(ComponentEvent event) {
            Window window = (Window) event.getSource();
            event(WindowHiddenEvent.of(findWindowName(window), window));
        }
    }

    /**
     * InternalFrameAdapter that triggers application events when a window is shown/hidden,
     * it also invokes hide() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    protected class InternalFrameHelper extends InternalFrameAdapter {
        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
            hide(event.getInternalFrame());
        }

        /**
         * Triggers a <tt>WindowShown</tt> event with the internal frame as sole argument
         */
        @Override
        public void internalFrameOpened(InternalFrameEvent event) {
            JInternalFrame window = (JInternalFrame) event.getSource();
            event(WindowShownEvent.of(findInternalWindowName(window), window));

        }

        /**
         * Triggers a <tt>WindowHidden</tt> event with the internal frame as sole argument
         */
        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            JInternalFrame window = (JInternalFrame) event.getSource();
            event(WindowHiddenEvent.of(findInternalWindowName(window), window));
        }
    }
}
