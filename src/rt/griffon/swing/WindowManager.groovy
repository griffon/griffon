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
package griffon.swing

import java.awt.Window
import java.awt.event.ComponentEvent
import java.awt.event.ComponentAdapter
import java.awt.event.WindowEvent
import java.awt.event.WindowAdapter
import java.util.concurrent.CopyOnWriteArrayList
import griffon.core.GriffonApplication
import griffon.core.ShutdownHandler

/**
 * Controls a set of windows that belong to the application.<p>
 * Windows that are controlled by a WindowManager can be shown/hidden
 * using a custom strategy ({@code WindowDisplayHandler})
 *
 * @see griffon.swing.WindowDisplayManager
 *
 * @author Andres Almiray
 */
final class WindowManager implements ShutdownHandler {
    private final GriffonApplication app
    private final WindowHelper windowHelper = new WindowHelper()
    private final ComponentHelper componentHelper = new ComponentHelper()
    private final List<Window> windows = new CopyOnWriteArrayList<Window>()

    /**
     * Creates a new WindowManager tied to an specific application.
     * @param app an application
     */
    WindowManager(GriffonApplication app) {
        this.app = app
    }

    /**
     * Returns the list of windows managed by this manager.
     *
     * @return a List of currently managed windows
     */
    List<Window> getWindows() {
        Collections.unmodifiableList(windows)
    }

    /**
     * Registers a window on this manager if an only if the window is not null
     * and it's not registered already.
     *
     * @param window the window to be added to the list of managed windows
     */
    void attach(Window window) {
         if(!window || (window in windows)) return
         window.addWindowListener(windowHelper)
         window.addComponentListener(componentHelper)
         this.@windows << window
    }

    /**
     * Removes the window from the list of manages windows if and only if it
     * is registered with this manager.
     *
     * @param window the window to be removed
     */
    void detach(Window window) {
        if(window in windows) {
            window.removeWindowListener(windowHelper)
            window.removeComponentListener(componentHelper)
            this.@windows.remove(window)
        }
    }

    /**
     * Shows the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param window the window to show
     */
    void show(Window window) {
        app.execSync {
            app.resolveWindowDisplayHandler().show(window, app)
        }
    }

    /**
     * Hides the window.<p>
     * This method is executed <b>SYNCHRONOUSLY</b> in the UI thread.
     *
     * @param window the window to hide
     */
    void hide(Window window) {
        app.execSync {
            app.resolveWindowDisplayHandler().hide(window, app)
        }
    }

    boolean canShutdown(GriffonApplication app) {
        true
    }

    /**
     * Hides all visible windows
     */
    void onShutdown(GriffonApplication app) {
        windows.findAll{it.visible}.each { window ->
            hide(window)
        }
    }

    /**
     * WindowAdapter that invokes hide() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    private class WindowHelper extends WindowAdapter {
        void windowClosing(WindowEvent event) {
            hide(event.window)
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
        void componentShown(ComponentEvent event) {
            app.event('WindowShown', [event.source])
        }

        /**
         * Triggers a <tt>WindowHidden</tt> event with the window as sole argument
         */
        void componentHidden(ComponentEvent event) {
            app.event('WindowHidden', [event.source])
        }
    }
}
