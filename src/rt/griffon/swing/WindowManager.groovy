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
 * @author Andres.Almiray
 */
final class WindowManager implements ShutdownHandler {
    private final GriffonApplication app
    private final WindowHelper windowHelper = new WindowHelper()
    private final ComponentHelper componentHelper = new ComponentHelper()
    private final List<Window> windows = new CopyOnWriteArrayList<Window>()

    /**
     *
     * @param app
     */
    WindowManager(GriffonApplication app) {
        this.app = app
    }

    /**
     *
     *
     * @return a List of currenntly managed windows
     */
    List<Window> getWindows() {
        Collections.unmodifiableList(windows)
    }

    /**
     *
     * @param window
     */
    void attach(Window window) {
         if(!window || (window in windows)) return
         window.addWindowListener(windowHelper)
         window.addComponentListener(componentHelper)
         this.@windows << window
    }

    /**
     *
     * @param window
     */
    void detach(Window window) {
        if(window in windows) {
            window.removeWindowListener(windowHelper)
            window.removeComponentListener(componentHelper)
            this.@windows.remove(window)
        }
    }

    /**
     *
     * @param window
     */
    void show(Window window) {
        app.execSync {
            app.resolveWindowDisplayHandler().show(window, app)
        }
    }

    /**
     *
     * @param window
     */
    void hide(Window window) {
        app.execSync {
            app.resolveWindowDisplayHandler().hide(window, app)
        }
    }

    boolean canShutdown(GriffonApplication app) {
        true
    }

    void onShutdown(GriffonApplication app) {
        windows.findAll{it.visible}.each { window ->
            hide(window)
        }
    }

    /**
     * @author Andres.Almiray
     */
    private class WindowHelper extends WindowAdapter {
        void windowClosing(WindowEvent event) {
            hide(event.window)
        }
    }

    /**
     * @author Andres.Almiray
     */
    private class ComponentHelper extends ComponentAdapter {
        void componentShown(ComponentEvent event) {
            app.event('WindowShown', [event.source])
        }

        void componentHidden(ComponentEvent event) {
            app.event('WindowHidden', [event.source])
        }
    }
}
