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
import griffon.util.internal.GriffonApplicationHelper
import griffon.util.GriffonExceptionHandler
import griffon.util.UIThreadHelper
import griffon.util.SwingUIThreadHandler
import java.awt.event.WindowEvent
import java.awt.EventQueue
import java.awt.Toolkit
import javax.swing.WindowConstants

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
 */
class SwingApplication implements griffon.application.StandaloneGriffonApplication {
    @Delegate private final BaseGriffonApplication _base

    List appFrames  = []

    SwingApplication() {
        _base = new BaseGriffonApplication(this)
        UIThreadHelper.instance.setUIThreadHandler(new SwingUIThreadHandler())
    }

    void bootstrap() {
        GriffonApplicationHelper.prepare(this)
        event("BootstrapEnd",[this])
    }

    void realize() {
        GriffonApplicationHelper.startup(this)
    }

    void show() {
        if (appFrames.size() > 0) {
            EventQueue.invokeAndWait { appFrames[0].show() }
        }

        callReady()
    }

    void shutdown() {
        _base.shutdown()
        System.exit(0)
    }

    void handleWindowClosing(WindowEvent evt = null) {
        boolean proceed = canShutdown()
        if(config.application?.autoShutdown && proceed && appFrames.findAll{!it.visible}.size() <= 1) {
            shutdown()
        } else {
            evt?.source?.visible = true
        }
    }

    Object createApplicationContainer() {
        def appContainer = SwingUtils.createApplicationFrame(this)
        try {
            appContainer.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
            appContainer.windowClosing = this.&handleWindowClosing
            appFrames += appContainer
        } catch (Throwable ignored) {
            // if it doesn't have a window closing event, ignore it
        }
        return appContainer
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
        SwingApplication sa = new SwingApplication()
        sa.bootstrap()
        sa.realize()
        sa.show()
    }
}
