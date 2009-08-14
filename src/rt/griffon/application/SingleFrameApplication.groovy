/*
 * Copyright 2008-2009 the original author or authors.
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
package griffon.application

import griffon.util.GriffonApplicationHelper
import griffon.util.IGriffonApplication
import griffon.util.BaseGriffonApplication
import griffon.util.GriffonExceptionHandler
import java.awt.event.WindowEvent
import java.awt.EventQueue

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
 */
class SingleFrameApplication /*implements IGriffonApplication*/ {
    @Delegate private final BaseGriffonApplication _base

    List appFrames  = []

    SingleFrameApplication() {
       _base = new BaseGriffonApplication(this)
    }

    public void bootstrap() {
        applicationProperties = new Properties()
        applicationProperties.load(getClass().getResourceAsStream('/application.properties'))
        GriffonApplicationHelper.prepare(this)
        event("BootstrapEnd",[this])
    }

    public void realize() {
        GriffonApplicationHelper.startup(this)
    }

    public void show() {
        if (appFrames.size() > 0) {
            EventQueue.invokeAndWait { appFrames[0].show() }
        }

        GriffonApplicationHelper.callReady(this)
    }

    public void shutdown() {
        _base.shutdown()
        System.exit(0)
    }

    public void handleWindowClosing(WindowEvent evt = null) {
        appFrames.removeAll(appFrames.findAll {!it.visible})
        if (config.application?.autoShutdown && appFrames.size() <= 1) {
            shutdown()
        }
    }

    public Object createApplicationContainer() {
        def appContainer = GriffonApplicationHelper.createJFrameApplication(this)
        try {
            appContainer.windowClosing = this.&handleWindowClosing
            appFrames += appContainer
        } catch (Throwable ignored) {
            // if it doesn't have a window closing event, ignore it
        }
        return appContainer
    }

    public static void main(String[] args) {
        GriffonExceptionHandler.registerExceptionHandler()
        SingleFrameApplication sfa = new SingleFrameApplication()
        sfa.bootstrap()
        sfa.realize()
        sfa.show()
    }
}