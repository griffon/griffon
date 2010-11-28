/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.swing;

import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import griffon.core.GriffonApplication;
import griffon.core.ApplicationPhase;
import griffon.core.ShutdownHandler;

/**
 * Controls a set of windows that belong to the application.<p>
 * Windows that are controlled by a WindowManager can be shown/hidden
 * using a custom strategy ({@code WindowDisplayHandler})
 *
 * @see griffon.swing.WindowDisplayHandler
 *
 * @author Andres Almiray
 * @since 0.3.1
 */
public final class WindowManager implements ShutdownHandler {
    private final SwingGriffonApplication app;
    private final WindowHelper windowHelper = new WindowHelper();
    private final ComponentHelper componentHelper = new ComponentHelper();
    private final List<Window> windows = new CopyOnWriteArrayList<Window>();

    private boolean hideBeforeHandler = false;

    /**
     * Creates a new WindowManager tied to an specific application.
     * @param app an application
     */
    public WindowManager(SwingGriffonApplication app) {
        this.app = app;
    }

    /**
     * Returns the list of windows managed by this manager.
     *
     * @return a List of currently managed windows
     */
    public List<Window> getWindows() {
        return Collections.<Window>unmodifiableList(windows);
    }

    /**
     * Registers a window on this manager if an only if the window is not null
     * and it's not registered already.
     *
     * @param window the window to be added to the list of managed windows
     */
    public void attach(Window window) {
         if(window == null || windows.contains(window)) return;
         window.addWindowListener(windowHelper);
         window.addComponentListener(componentHelper);
         windows.add(window);
    }

    /**
     * Removes the window from the list of manages windows if and only if it
     * is registered with this manager.
     *
     * @param window the window to be removed
     */
    public void detach(Window window) {
        if(windows.contains(window)) {
            window.removeWindowListener(windowHelper);
            window.removeComponentListener(componentHelper);
            windows.remove(window);
        }
    }

    /**
     * Shows the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param window the window to show
     */
    public void show(final Window window) {
        app.execSync(new Runnable() {
            public void run() {
                app.resolveWindowDisplayHandler().show(window, app);
            }
        });
    }

    /**
     * Hides the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param window the window to hide
     */
    public void hide(final Window window) {
        app.execSync(new Runnable() {
            public void run() {
                app.resolveWindowDisplayHandler().hide(window, app);
            }
        });
    }

    public boolean canShutdown(GriffonApplication app) {
        return true;
    }

    /**
     * Hides all visible windows
     */
    public void onShutdown(GriffonApplication app) {
        for(Window window : windows) {
            if(window.isVisible()) hide(window);
        }
    }

    /**
     * Should the window be hidden before all ShutdownHandlers will be called ?
     * @return current value
     */
    public boolean isHideBeforeHandler() {
        return hideBeforeHandler;
    }

    /**
     * Set if the window should be hidden before all ShutdownHandler will be called.
     * @param hideBeforeHandler new value
     */
    public void setHideBeforeHandler(boolean hideBeforeHandler) {
        this.hideBeforeHandler = hideBeforeHandler;
    }

    /**
     * WindowAdapter that optionally invokes hide() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    private class WindowHelper extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            if(isHideBeforeHandler()) hide(event.getWindow());
            
            if(app.getPhase() == ApplicationPhase.SHUTDOWN) return;
            int visibleWindows = 0;
            for(Window window : windows) {
                if(window.isVisible()) {
                    visibleWindows++;
                }
            }

            Boolean autoShutdown = (Boolean) app.getConfig().flatten().get("application.autoShutdown");
            if(visibleWindows <= 1 && autoShutdown != null && autoShutdown.booleanValue()) {
                if(!app.shutdown()) show(event.getWindow());
            }
        }
    }

    /**
     * ComponentAdapter that triggers application events when a window is shown/hidden.
     *
     * @author Andres Almiray
     */
    private class ComponentHelper extends ComponentAdapter {
        /**
         * Triggers a <tt>WindowShown</tt> event with the window as sole argument
         */
        public void componentShown(ComponentEvent event) {
            app.event("WindowShown", Arrays.asList(event.getSource()));
        }

        /**
         * Triggers a <tt>WindowHidden</tt> event with the window as sole argument
         */
        public void componentHidden(ComponentEvent event) {
            app.event("WindowHidden", Arrays.asList(event.getSource()));
        }
    }
}