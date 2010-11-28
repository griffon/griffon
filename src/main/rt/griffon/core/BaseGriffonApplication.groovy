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

import griffon.util.EventRouter
import griffon.util.Metadata
import griffon.util.ApplicationHolder
import griffon.util.UIThreadHelper
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper

import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.ExecutorService
import java.util.concurrent.CountDownLatch

import groovy.beans.Bindable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
    ArtifactManager artifactManager

    @Bindable Locale locale = Locale.getDefault()
    static final String[] EMPTY_ARGS = new String[0]
    protected ApplicationPhase phase = ApplicationPhase.INITIALIZE

    private final EventRouter eventRouter = new EventRouter()
    private final List<ShutdownHandler> shutdownHandlers = []
    final GriffonApplication appDelegate
    final String[] startupArgs
    private final Object shutdownLock = new Object()
    final Logger log

    BaseGriffonApplication(GriffonApplication appDelegate, String[] args = EMPTY_ARGS) {
        startupArgs = new String[args.length]
        System.arraycopy(args, 0, startupArgs, 0, args.length)

        this.appDelegate = appDelegate
        ApplicationHolder.application = appDelegate

        log = LoggerFactory.getLogger(appDelegate.class)
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
        } catch(ignored) {
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
        } catch(ignored) {
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
                if(log.isDebugEnabled()) {
                  try {
                    log.debug("Shutdown aborted by $handler")
                  }catch(e) {
                    log.debug("Shutdown aborted by an handler")
                  }
                }
                return false
            }
        }
        return true
    }

    boolean shutdown() {
        // avoids reentrant calls to shutdown()
        // once permission to quit has been granted
        if(phase == ApplicationPhase.SHUTDOWN) return

        if(!canShutdown()) return false
        log.info('Shutdown is in process')

        // signal that shutdown is in process
        phase = ApplicationPhase.SHUTDOWN
   
        // stage 1 - alert all app event handlers
        // wait for all handlers to complete before proceeding
        // with stage #2 if and only if the current thread is
        // the ui thread
        log.debug('Shutdown stage 1: notify all event listeners')
        CountDownLatch latch = isUIThread() ? new CountDownLatch(1) : null
        addApplicationEventListener('ShutdownStart') {latch?.countDown()}
        event('ShutdownStart',[appDelegate])
        latch?.await()
  
        // stage 2 - alert all shutdown handlers
        log.debug('Shutdown stage 2: notify all shutdown handlers')
        for(handler in shutdownHandlers) {
            handler.onShutdown(appDelegate)
        }
 
        // stage 3 - destroy all mvc groups
        List mvcNames = []
        mvcNames.addAll(groups.keySet())
        log.debug('Shutdown stage 3: destroy all MVC groups')
        mvcNames.each { destroyMVCGroup(it) }

        // stage 4 - call shutdown script
        log.debug('Shutdown stage 4: execute Shutdown script')
        GriffonApplicationHelper.runScriptInsideUIThread('Shutdown', appDelegate)

        true
    }

    void startup() {
        if(phase != ApplicationPhase.INITIALIZE) return

        phase = phase.STARTUP
        event('StartupStart',[appDelegate])
    
        log.info("Initializing all startup groups: ${config.application.startupGroups}")
        config.application.startupGroups.each {group -> createMVCGroup(group) }
        GriffonApplicationHelper.runScriptInsideUIThread('Startup', appDelegate)

        event('StartupEnd',[appDelegate])
    }

    void event(String eventName) {
        eventRouter.publish(eventName, [])
    }

    void event(String eventName, List params) {
        eventRouter.publish(eventName, params)
    }
    
    void eventAsync(String eventName) {
        eventRouter.publishAsync(eventName, [])
    }

    void eventAsync(String eventName, List params) {
        eventRouter.publishAsync(eventName, params)
    }

    void addApplicationEventListener(listener) {
       eventRouter.addEventListener(listener)
    }

    void removeApplicationEventListener(listener) {
       eventRouter.removeEventListener(listener)
    }

    void addApplicationEventListener(String eventName, Closure listener) {
       eventRouter.addEventListener(eventName,listener)
    }

    void removeApplicationEventListener(String eventName, Closure listener) {
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

    // -----------------------

    boolean isUIThread() {
        UIThreadHelper.instance.isUIThread()
    }

    void execAsync(Runnable runnable) {
        UIThreadHelper.instance.executeAsync(runnable)
    }

    void execSync(Runnable runnable) {
        UIThreadHelper.instance.executeSync(runnable)
    }

    void execOutside(Runnable runnable) {
        UIThreadHelper.instance.executeOutside(runnable)
    }

    Future execFuture(ExecutorService executorService, Closure closure) {
        UIThreadHelper.instance.executeFuture(executorService, closure)
    }

    Future execFuture(Closure closure) {
        UIThreadHelper.instance.executeFuture(closure)
    }

    Future execFuture(ExecutorService executorService, Callable callable) {
        UIThreadHelper.instance.executeFuture(executorService, callable)
    }

    Future execFuture(Callable callable) {
        UIThreadHelper.instance.executeFuture(callable)
    }

    Object newInstance(Class clazz, String type) {
        GriffonApplicationHelper.newInstance(appDelegate, clazz, type)
    }

    Map<String, ?> buildMVCGroup(String mvcType) {
        GriffonApplicationHelper.buildMVCGroup(appDelegate, [:], mvcType, mvcType)
    }

    Map<String, ?> buildMVCGroup(String mvcType, String mvcName) {
        GriffonApplicationHelper.buildMVCGroup(appDelegate, [:], mvcType, mvcName)
    }

    Map<String, ?> buildMVCGroup(Map<String, ?> args, String mvcType) {
        GriffonApplicationHelper.buildMVCGroup(appDelegate, args, mvcType, mvcType)
    }

    Map<String, ?> buildMVCGroup(Map<String, ?> args, String mvcType, String mvcName) {
        GriffonApplicationHelper.buildMVCGroup(appDelegate, args, mvcType, mvcName)
    }

    List<?> createMVCGroup(String mvcType) {
        GriffonApplicationHelper.createMVCGroup(appDelegate, mvcType)
    }

    List<?> createMVCGroup(Map<String, ?> args, String mvcType) {
        GriffonApplicationHelper.createMVCGroup(appDelegate, args, mvcType)
    }

    List<?> createMVCGroup(String mvcType, Map<String, ?> args) {
        GriffonApplicationHelper.createMVCGroup(appDelegate, args, mvcType)
    }

    List<?> createMVCGroup(String mvcType, String mvcName) {
        GriffonApplicationHelper.createMVCGroup(appDelegate, mvcType, mvcName)
    }

    List<?> createMVCGroup(Map<String, ?> args, String mvcType, String mvcName) {
        GriffonApplicationHelper.createMVCGroup(appDelegate, args, mvcType, mvcName)
    }

    List<?> createMVCGroup(String mvcType, String mvcName, Map<String, ?> args) {
        GriffonApplicationHelper.createMVCGroup(appDelegate, args, mvcType, mvcName)
    }

    void destroyMVCGroup(String mvcName) {
        GriffonApplicationHelper.destroyMVCGroup(appDelegate, mvcName)
    }
}
