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
package org.codehaus.griffon.runtime.core.view;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.GriffonApplication;
import griffon.core.env.ApplicationPhase;
import griffon.core.event.Event;
import griffon.core.events.WindowAttachedEvent;
import griffon.core.events.WindowDetachedEvent;
import griffon.core.view.WindowDisplayHandler;
import griffon.core.view.WindowManager;
import griffon.exceptions.InstanceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
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
public abstract class AbstractWindowManager<W> implements WindowManager<W> {
    protected static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";
    protected static final String ERROR_WINDOW_NULL = "Argument 'window' must not be null";
    private static final Logger LOG = LoggerFactory.getLogger(AbstractWindowManager.class);
    private final Map<String, W> windows = Collections.synchronizedMap(new LinkedHashMap<String, W>());

    private final GriffonApplication application;
    private final WindowDisplayHandler<W> windowDisplayHandler;

    @Inject
    public AbstractWindowManager(@Nonnull GriffonApplication application, @Nonnull WindowDisplayHandler<W> windowDisplayHandler) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
        requireNonNull(application.getConfiguration(), "Argument 'application.configuration' must not be null");
        requireNonNull(application.getUIThreadManager(), "Argument 'application.uiThreadManager' must not be null");
        this.windowDisplayHandler = requireNonNull(windowDisplayHandler, "Argument 'windowDisplayHandler' must not be null");
    }

    protected GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    @Override
    public Set<String> getWindowNames() {
        return Collections.unmodifiableSet(windows.keySet());
    }

    @Nullable
    @Override
    public String findWindowName(@Nonnull W window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        synchronized (windows) {
            for (Map.Entry<String, W> e : windows.entrySet()) {
                if (e.getValue().equals(window)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public int indexOf(@Nonnull W window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        synchronized (windows) {
            int index = 0;
            for (W w : windows.values()) {
                if (window.equals(w)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    @Override
    @Nullable
    public W findWindow(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        return windows.get(name);
    }

    @Override
    @Nullable
    public W getAt(int index) {
        synchronized (windows) {
            int size = windows.size();
            if (index < 0 || index >= size) {
                throw new ArrayIndexOutOfBoundsException(index);
            }

            int i = 0;
            for (W window : windows.values()) {
                if (index == i++) {
                    return window;
                }
            }
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    @Override
    @Nullable
    public W getStartingWindow() {
        W window = null;
        Object value = resolveStartingWindowFromConfiguration();
        LOG.debug("windowManager.startingWindow configured to {}", value);

        if (value instanceof String) {
            String windowName = (String) value;
            LOG.debug("Selecting window {} as starting window", windowName);
            window = findWindow(windowName);
        } else if (value instanceof Number) {
            int index = ((Number) value).intValue();
            LOG.debug("Selecting window at index {} as starting window", index);
            try {
                window = getAt(index);
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.warn("Window at index {} was not found", index);
            }
        } else {
            LOG.debug("No startingWindow configured, selecting the first one from the windows list");
            try {
                window = getAt(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.warn("Window at index 0 was not found");
            }
        }

        LOG.debug("Starting Window is {}", window);

        return window;
    }

    @Nullable
    protected Object resolveStartingWindowFromConfiguration() {
        return application.getConfiguration().get("windowManager.startingWindow", null);
    }

    @Override
    @Nonnull
    public Collection<W> getWindows() {
        return unmodifiableCollection(windows.values());
    }

    @Override
    public void attach(@Nonnull String name, @Nonnull W window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);
        if (windows.containsKey(name)) {
            W window2 = windows.get(name);
            if (window2 != window) {
                detach(name);
            }
        }

        doAttach(window);

        LOG.debug("Attaching window with name: '{}' at index {} {}", name, windows.size(), window);
        windows.put(name, window);
        event(WindowAttachedEvent.of(name, window));
    }

    protected abstract void doAttach(@Nonnull W window);

    @Override
    public void detach(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        if (windows.containsKey(name)) {
            W window = windows.get(name);

            doDetach(window);

            LOG.debug("Detaching window with name: '{}' {}", name, window);
            windows.remove(name);
            event(WindowDetachedEvent.of(name, window));
        }
    }

    protected abstract void doDetach(@Nonnull W window);

    @Override
    public void show(@Nonnull final W window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        if (!windows.containsValue(window)) {
            return;
        }

        String windowName = null;
        int windowIndex = -1;
        synchronized (windows) {
            int i = 0;
            for (Map.Entry<String, W> entry : windows.entrySet()) {
                if (entry.getValue() == window) {
                    windowName = entry.getKey();
                    windowIndex = i;
                    break;
                }
                i++;
            }
        }

        final String name = windowName;
        final int index = windowIndex;

        application.getUIThreadManager().executeInsideUIAsync(() -> {
            LOG.debug("Showing window with name: '{}' at index {} {}", name, index, window);
            //noinspection ConstantConditions
            resolveWindowDisplayHandler().show(name, window);
        });
    }

    @Override
    public void show(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        W window = findWindow(name);
        if (window != null) {
            show(window);
        }
    }

    @Override
    public void hide(@Nonnull final W window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        if (!windows.containsValue(window)) {
            return;
        }

        String windowName = null;
        int windowIndex = -1;
        synchronized (windows) {
            int i = 0;
            for (Map.Entry<String, W> entry : windows.entrySet()) {
                if (entry.getValue() == window) {
                    windowName = entry.getKey();
                    windowIndex = i;
                    break;
                }
                i++;
            }
        }

        final String name = windowName;
        final int index = windowIndex;

        application.getUIThreadManager().executeInsideUIAsync(() -> {
            LOG.debug("Hiding window with name: '{}' at index {} {}", name, index, window);
            //noinspection ConstantConditions
            resolveWindowDisplayHandler().hide(name, window);
        });
    }

    @Nonnull
    protected WindowDisplayHandler<W> resolveWindowDisplayHandler() {
        return windowDisplayHandler;
    }

    @Override
    public void hide(@Nonnull String name) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        W window = findWindow(name);
        if (window != null) {
            hide(window);
        }
    }

    @Override
    public boolean canShutdown(@Nonnull GriffonApplication app) {
        return true;
    }

    @Override
    public void onShutdown(@Nonnull GriffonApplication app) {
        for (W window : windows.values()) {
            if (isWindowVisible(window)) {
                hide(window);
            }
        }
    }

    protected abstract boolean isWindowVisible(@Nonnull W window);

    @Override
    public int countVisibleWindows() {
        int visibleWindows = 0;
        for (W window : windows.values()) {
            if (isWindowVisible(window)) {
                visibleWindows++;
            }
        }
        return visibleWindows;
    }

    @Override
    public boolean isAutoShutdown() {
        return application.getConfiguration().getAsBoolean("application.autoShutdown", true);
    }

    protected <E extends Event> void event(@Nonnull E event) {
        try {
            getApplication().getEventRouter().publishEvent(event);
        } catch (InstanceNotFoundException infe) {
            if (getApplication().getPhase() != ApplicationPhase.SHUTDOWN) {
                throw infe;
            }
        }
    }
}
