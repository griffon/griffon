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
 * See the License for the specific language govnerning permissions and
 * limitations under the License.
 */
package griffon.swing

import griffon.core.BaseGriffonApplication
import griffon.util.GriffonExceptionHandler
import griffon.util.UIThreadHelper
import java.awt.EventQueue
import java.awt.Toolkit
import java.awt.Window

/**
 * Basic implementation of {@code GriffonApplication} that runs in standalone/webstart mode.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 * @since 0.1
 */
class SwingApplication implements SwingGriffonApplication, griffon.application.StandaloneGriffonApplication {
    @Delegate private final BaseGriffonApplication _base
    final WindowManager windowManager
    WindowDisplayHandler windowDisplayHandler
    private final WindowDisplayHandler defaultWindowDisplayHandler = new ConfigurableWindowDisplayHandler()

    SwingApplication(String[] args = BaseGriffonApplication.EMPTY_ARGS) {
        UIThreadHelper.instance.setUIThreadHandler(new SwingUIThreadHandler())
        _base = new BaseGriffonApplication(this, args)
        windowManager = new WindowManager(this)
        addShutdownHandler(windowManager)
    }

    final WindowDisplayHandler resolveWindowDisplayHandler() {
        windowDisplayHandler ?: defaultWindowDisplayHandler
    }

    void bootstrap() {
        initialize()
    }

    void realize() {
        startup()
    }

    void show() {
        windowManager.show(windowManager.startingWindow)

        callReady()
    }

    boolean shutdown() {
        if(_base.shutdown()) {
            exit()
        }
        false
    }

    void exit() {
        System.exit(0)
    }

    Object createApplicationContainer() {
        Window window = SwingUtils.createApplicationFrame(this)
        windowManager.attach(window)
        return window
    }

    /**
     * Calls the ready lifecycle mhetod after the UI thread calms down
     */
    private void callReady() {
        // wait for EDT to empty out.... somehow
        boolean empty = false
        while (true) {
            UIThreadHelper.instance.executeSync {empty = Toolkit.defaultToolkit.systemEventQueue.peekEvent() == null}
            if (empty) break
            sleep(100)
        }

        ready()
    }

    public static void main(String[] args) {
        GriffonExceptionHandler.registerExceptionHandler()
        SwingApplication sa = new SwingApplication(args)
        sa.bootstrap()
        sa.realize()
        sa.show()
    }
}
