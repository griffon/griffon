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

import griffon.core.GriffonApplication
import griffon.core.BaseGriffonApplication
import griffon.util.UIThreadHelper
import javax.swing.JApplet
import java.awt.Window
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper

/**
 * Simple implementation of {@code GriffonApplication} that runs in applet mode
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 * @since 0.1
 */
class SwingApplet extends JApplet {
    private boolean appletContainerDispensed = false

    @Delegate private final BaseGriffonApplication _base
    final WindowManager windowManager
    WindowDisplayHandler windowDisplayHandler
    private final WindowDisplayHandler defaultWindowDisplayHandler = new DefaultWindowDisplayHandler()

    SwingApplet() {
        UIThreadHelper.instance.setUIThreadHandler(new SwingUIThreadHandler())
        _base = new BaseGriffonApplication(this)
        windowManager = new WindowManager(this)
    }

    final WindowDisplayHandler resolveWindowDisplayHandler() {
        windowDisplayHandler ?: defaultWindowDisplayHandler
    }

    void init() {
        initialize()
        startup()
    }

    void start() {
        ready()
    }

    void stop() {
        event('StopStart',[this])
        GriffonApplicationHelper.runScriptInsideUIThread('Stop', this)
        event('StopEnd',[this])
    }

    void destroy() {
        shutdown()
    }

    Object createApplicationContainer() {
        if (appletContainerDispensed) {
            Window window = SwingUtils.createApplicationFrame(this)
            windowManager.attach(window)
            return window
        } else {
            appletContainerDispensed = true
            return this;
        }
    }
}