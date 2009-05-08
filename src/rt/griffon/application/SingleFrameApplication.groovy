/*
 * Copyright 2008 the original author or authors.
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
package griffon.application

import griffon.util.GriffonApplicationHelper
import griffon.util.IGriffonApplication
import griffon.util.EventRouter
import java.awt.event.WindowEvent
import griffon.util.GriffonExceptionHandler
import java.awt.EventQueue

/**
 *@author Danno.Ferrin
 */
class SingleFrameApplication implements IGriffonApplication {

    Map models      = [:]
    Map views       = [:]
    Map controllers = [:]
    Map builders    = [:]
    Map groups      = [:]
    List appFrames  = []

    Binding bindings = new Binding()
    ConfigObject config
    ConfigObject builderConfig
    Object eventsConfig
    private EventRouter eventRouter = new EventRouter()

    public void bootstrap() {
        GriffonApplicationHelper.prepare(this);
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

    public Class getConfigClass() {
        return getClass().classLoader.loadClass("Application")
    }

    public Class getBuilderClass() {
        return getClass().classLoader.loadClass("Builder")
    }

    public Class getEventsClass() {
        try{
           return getClass().classLoader.loadClass("Events")
        } catch( ex ) {
           // ignore - no global event handler will be used
        }
        return null
    }

    public Object createApplicationContainer() {
        def appContainer = GriffonApplicationHelper.createJFrameApplication(this)
        try {
            appContainer.windowClosing = this.&handleWindowClosing
            appFrames += appContainer
        } catch (Throwable t) {
            // if it doesn't have a window closing event, ignore it
        }
        return appContainer
    }

    public void initialize() {
        GriffonApplicationHelper.runScriptInsideEDT("Initialize", this)
    }

    public void ready() {
        event("ReadyStart",[this])
        GriffonApplicationHelper.runScriptInsideEDT("Ready", this)
        event("ReadyEnd",[this])
    }

    public void shutdown() {
        event("ShutdownStart",[this])
        GriffonApplicationHelper.runScriptInsideEDT("Shutdown", this)
        System.exit(0)
    }

    public void startup() {
        event("StartupStart",[this])
        GriffonApplicationHelper.runScriptInsideEDT("Startup", this)
        event("StartupEnd",[this])
    }

    public void handleWindowClosing(WindowEvent evt = null) {
        appFrames.removeAll(appFrames.findAll {!it.visible})
        if (config.application?.autoShutdown && appFrames.size() <= 1) {
            shutdown()
        }
    }

    public static void main(String[] args) {
        GriffonExceptionHandler.registerExceptionHandler()
        SingleFrameApplication sfa = new SingleFrameApplication();
        sfa.bootstrap();
        sfa.realize();
        sfa.show();
    }

    public void event( String eventName, List params = [] ) {
        eventRouter.publish(eventName, params)
    }

    public void addApplicationEventListener( listener ) {
       eventRouter.addApplicationEventListener(listener)
    }

    public void removeApplicationEventListener( listener ) {
       eventRouter.removeApplicationEventListener(listener)
    }

    public void addApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.addApplicationEventListener(eventName,listener)
    }

    public void removeApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.removeApplicationEventListener(eventName,listener)
    }
}
