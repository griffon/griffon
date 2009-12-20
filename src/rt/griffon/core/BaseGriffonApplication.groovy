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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.core

import griffon.util.GriffonApplicationHelper
import griffon.util.EventRouter

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
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
    Properties applicationProperties
    ConfigObject config
    ConfigObject builderConfig
    Object eventsConfig

    private final EventRouter eventRouter = new EventRouter()
    final GriffonApplication appDelegate

    BaseGriffonApplication(GriffonApplication appDelegate) {
        this.appDelegate = appDelegate
    }

    // define getter/setter otherwise it will be treated as a read-only property
    // because only the getter was defined in GriffonApplication
    public Properties getApplicationProperties() {
        return applicationProperties
    }
    public void setApplicationProperties(Properties applicationProperties) {
        this.applicationProperties = applicationProperties
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
        } catch( ignored ) {
           // ignore - no global event handler will be used
        }
        return null
    }

    public void initialize() {
        GriffonApplicationHelper.runScriptInsideUIThread("Initialize", appDelegate)
    }

    public void ready() {
        event("ReadyStart",[appDelegate])
        GriffonApplicationHelper.runScriptInsideUIThread("Ready", appDelegate)
        event("ReadyEnd",[appDelegate])
    }

    public void shutdown() {
        event("ShutdownStart",[appDelegate])
        List mvcNames = []
        mvcNames.addAll(groups.keySet())
        mvcNames.each { 
            GriffonApplicationHelper.destroyMVCGroup(appDelegate, it)
        }
        GriffonApplicationHelper.runScriptInsideUIThread("Shutdown", appDelegate)
    }

    public void startup() {
        event("StartupStart",[appDelegate])
        GriffonApplicationHelper.runScriptInsideUIThread("Startup", appDelegate)
        event("StartupEnd",[appDelegate])
    }

    public void event( String eventName, List params = [] ) {
        eventRouter.publish(eventName, params)
    }

    public void addApplicationEventListener( listener ) {
       eventRouter.addEventListener(listener)
    }

    public void removeApplicationEventListener( listener ) {
       eventRouter.removeEventListener(listener)
    }

    public void addApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.addEventListener(eventName,listener)
    }

    public void removeApplicationEventListener( String eventName, Closure listener ) {
       eventRouter.removeEventListener(eventName,listener)
    }

    public void addMvcGroup(String mvcType, Map<String, String> mvcPortions) {
       mvcGroups[mvcType] = mvcPortions
    }

    public Object createApplicationContainer() {
        null
    }
}
