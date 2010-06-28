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

package griffon.core

import griffon.util.internal.GriffonApplicationHelper
import griffon.util.EventRouter
import griffon.util.Metadata

import groovy.beans.Bindable

/**
 * Implements the basics for a skeleton GriffonApplication.<p>
 * It is recommended to use this class a starting point for any
 * custom GriffonApplication implementations, in conjuction with
 * Groovy's {@code @Delegate} AST transformation, as shown next:
 *
 * <pre>
 * class CustomGriffonApplication {
 *     {@code @Delegate} private final BaseGriffonApplication _base
 *     CustomGriffonApplication() {
 *         _base = new BaseGriffonApplication(this)
 *     }
 *     ...
 * }
 * </pre>
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class BaseGriffonApplication implements GriffonApplication {
    Map<String, ?> addons = [:]
    Map<String, String> addonPrefixes = [:]

    Map<String, Map<String, String>> mvcGroups = [:]
    Map models      = [:]
    Map views       = [:]
    Map controllers = [:]
    Map builders    = [:]
    Map groups      = [:]

    Binding bindings = new Binding()
    ConfigObject config
    ConfigObject builderConfig
    Object eventsConfig

    @Bindable Locale locale = Locale.getDefault()
    protected ApplicationPhase phase = ApplicationPhase.INITIALIZE

    private final EventRouter eventRouter = new EventRouter()
    private final List<ShutdownHandler> shutdownHandlers = []
    final GriffonApplication appDelegate
    private boolean shutdownInProcess = false
    private final Object shutdownLock = new Object()

    BaseGriffonApplication(GriffonApplication appDelegate) {
        this.appDelegate = appDelegate
    }

    Metadata getMetadata() {
        return Metadata.current 
    }

    Class getAppConfigClass() {
        return getClass().classLoader.loadClass('Application')
    }

    Class getConfigClass() {
        try{
           return getClass().classLoader.loadClass('Config')
        } catch( ignored ) {
           // ignore - no additional config available
        }
        return null
    }

    Class getBuilderClass() {
        return getClass().classLoader.loadClass('Builder')
    }

    Class getEventsClass() {
        try{
           return getClass().classLoader.loadClass('Events')
        } catch( ignored ) {
           // ignore - no global event handler will be used
        }
        return null
    }

    void initialize() {
        if(phase == ApplicationPhase.INITIALIZE) {
            GriffonApplicationHelper.prepare(appDelegate)
        }
    }

    void ready() {
        if(phase != ApplicationPhase.STARTUP) return

        phase = ApplicationPhase.READY
        event('ReadyStart',[appDelegate])
        GriffonApplicationHelper.runScriptInsideUIThread('Ready', appDelegate)
        event('ReadyEnd',[appDelegate])
        phase = ApplicationPhase.MAIN
    }

    boolean canShutdown() {
        event('ShutdownRequested',[appDelegate])
        for(handler in shutdownHandlers) {
            if(!handler.canShutdown(appDelegate)) {
                event('ShutdownAborted',[appDelegate])
                return false
            }
        }
        return true
    }

    boolean shutdown() {
        if(!canShutdown()) return false

        // signal that shutdown is in process
        // avoids reentrant calls to shutdown()
        // once permission to quit has been granted
        signalShutdownInProcess()
        phase = ApplicationPhase.SHUTDOWN
   
        // stage 1 - alert all app event handlers
        event('ShutdownStart',[appDelegate])
  
        // stage 2 - alert all shutdown handlers
        for(handler in shutdownHandlers) {
            handler.onShutdown(appDelegate)
        }
 
        // stage 3 - destroy all mvc groups
        List mvcNames = []
        mvcNames.addAll(groups.keySet())
        mvcNames.each { 
            GriffonApplicationHelper.destroyMVCGroup(appDelegate, it)
        }

        // stage 4 - call shutdown script
        GriffonApplicationHelper.runScriptInsideUIThread('Shutdown', appDelegate)
 
        true
    }

    private void signalShutdownInProcess() {
        synchronized(shutdownLock) {
            shutdownInProcess = true
        }
    }

    boolean isShutdownInProcess() {
        synchronized(shutdownLock) {
            return shutdownInProcess
        }
    }

    void startup() {
        if(phase != ApplicationPhase.INITIALIZE) return

        phase = phase.STARTUP
        event('StartupStart',[appDelegate])

        config.application.startupGroups.each {group ->
            GriffonApplicationHelper.createMVCGroup(appDelegate, group)
        }
        GriffonApplicationHelper.runScriptInsideUIThread('Startup', appDelegate)

        event('StartupEnd',[appDelegate])
    }

    void event( String eventName, List params = [] ) {
        eventRouter.publish(eventName, params)
    }

    void addApplicationEventListener( listener ) {
       eventRouter.addEventListener(listener)
    }

    void removeApplicationEventListener( listener ) {
       eventRouter.removeEventListener(listener)
    }

    void addApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.addEventListener(eventName,listener)
    }

    void removeApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.removeEventListener(eventName,listener)
    }

    void addMvcGroup(String mvcType, Map<String, String> mvcPortions) {
       mvcGroups[mvcType] = mvcPortions
    }

    Object createApplicationContainer() {
        null
    }

    void addShutdownHandler(ShutdownHandler handler) {
        if(handler && !shutdownHandlers.contains(handler)) shutdownHandlers << handler
    }

    void removeShutdownHandler(ShutdownHandler handler) {
        if(handler) shutdownHandlers.remove(handler)
    }

    ApplicationPhase getPhase() {
        phase
    }
}
