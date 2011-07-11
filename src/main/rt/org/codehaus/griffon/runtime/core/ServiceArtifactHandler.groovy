/*
 * Copyright 2009-2011 the original author or authors.
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

package org.codehaus.griffon.runtime.core

import griffon.core.ArtifactInfo
import griffon.core.GriffonApplication
import griffon.core.GriffonClass
import griffon.core.GriffonServiceClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Handler for 'Service' artifacts.
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
class ServiceArtifactHandler extends ArtifactHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceArtifactHandler)
    private final Map<String, ?> serviceInstances = [:]
    
    ServiceArtifactHandler(GriffonApplication app) {
        super(app, GriffonServiceClass.TYPE, GriffonServiceClass.TRAILING)
        app.metaClass.getServices = {->
            Collections.unmodifiableMap(serviceInstances)
        }
    }

    protected GriffonClass newGriffonClassInstance(Class clazz) {
        return new DefaultGriffonServiceClass(getApp(), clazz)
    }

    void initialize(ArtifactInfo[] artifacts) {
        super.initialize(artifacts)
        if(app.config?.griffon?.basic_injection?.disable) return
        app.addApplicationEventListener(this)
    }

    /**
     * Application event listener.<p>
     * Lazily injects services instances if {@code app.config.griffon.basic_injection.disable}
     * is not set to true
     */
    def onNewInstance = { klass, t, instance ->
        if(type == t || app.config?.griffon?.basic_injection?.disable) return
        instance.metaClass.properties.name.each { propertyName ->
            def serviceInstance = serviceInstances[propertyName]
            if(!serviceInstance) {
                GriffonClass griffonClass = findClassFor(propertyName)
                if(griffonClass) {
                    serviceInstance = griffonClass.newInstance()
                    serviceInstance.metaClass.app = app
                    app.addApplicationEventListener(serviceInstance)
                    serviceInstances[propertyName] = serviceInstance
                }
            }
            
            if(serviceInstance) {
                if(LOG.debugEnabled) LOG.debug("Injecting service $serviceInstance on $instance using property '$propertyName'")
                instance[propertyName] = serviceInstance
            }
        }
    }
}
