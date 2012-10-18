/*
 * Copyright 2009-2012 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.*;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;

/**
 * Handler for 'Service' artifacts.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public class ServiceArtifactHandler extends ArtifactHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceArtifactHandler.class);
    private final ServiceManager serviceManager;

    private class DefaultServiceManager extends AbstractServiceManager {
        private final Map<String, GriffonService> serviceInstances = new ConcurrentHashMap<String, GriffonService>();

        public DefaultServiceManager(GriffonApplication app) {
            super(app);
            app.addShutdownHandler(new ShutdownHandler() {
                @Override
                public boolean canShutdown(GriffonApplication application) {
                    return true;
                }

                @Override
                public void onShutdown(GriffonApplication application) {
                    for (Map.Entry<String, GriffonService> entry : serviceInstances.entrySet()) {
                        if (LOG.isInfoEnabled()) LOG.info("Destroying service identified by '" + entry.getKey() + "'");
                        entry.getValue().serviceDestroy();
                    }
                }
            });
        }

        public Map<String, GriffonService> getServices() {
            return Collections.unmodifiableMap(serviceInstances);
        }

        public GriffonService findService(String name) {
            if (!name.endsWith(getTrailing())) {
                name += getTrailing();
            }
            GriffonService serviceInstance = serviceInstances.get(name);
            if (serviceInstance == null) {
                GriffonClass griffonClass = findClassFor(name);
                if (griffonClass != null) {
                    serviceInstance = instantiateService(name, griffonClass);
                    serviceInstances.put(name, serviceInstance);
                }
            }
            return serviceInstance;
        }

        private GriffonService instantiateService(String serviceName, GriffonClass griffonClass) {
            GriffonService serviceInstance = (GriffonService) griffonClass.newInstance();
            InvokerHelper.setProperty(serviceInstance, "app", getApp());
            getApp().addApplicationEventListener(serviceInstance);
            if (LOG.isInfoEnabled()) LOG.info("Initializing service identified by '" + serviceName + "'");
            serviceInstance.serviceInit();
            return serviceInstance;
        }
    }

    public ServiceArtifactHandler(GriffonApplication app) {
        super(app, GriffonServiceClass.TYPE, GriffonServiceClass.TRAILING);
        serviceManager = new DefaultServiceManager(app);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering " + serviceManager + " as ServiceManager.");
        }
        InvokerHelper.setProperty(app, "serviceManager", serviceManager);
    }

    protected GriffonClass newGriffonClassInstance(Class clazz) {
        return new DefaultGriffonServiceClass(getApp(), clazz);
    }

    public void initialize(ArtifactInfo[] artifacts) {
        super.initialize(artifacts);
        if (isBasicInjectionDisabled()) return;
        if (isEagerInstantiationEnabled()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Instantiating service instances eagerly");
            }
            for (ArtifactInfo artifactInfo : artifacts) {
                GriffonClass griffonClass = getClassFor(artifactInfo.getClazz());
                serviceManager.findService(griffonClass.getPropertyName());
            }
        }
        getApp().addApplicationEventListener(this);
    }

    /**
     * Application event listener.<p>
     * Lazily injects services instances if {@code app.config.griffon.basic_injection.disable}
     * is not set to true
     */
    public void onNewInstance(Class klass, String t, Object instance) {
        if (isBasicInjectionDisabled()) return;
        MetaClass metaClass = InvokerHelper.getMetaClass(instance);
        for (MetaProperty property : metaClass.getProperties()) {
            String propertyName = property.getName();
            if (!propertyName.endsWith(getTrailing())) continue;
            GriffonService serviceInstance = serviceManager.findService(propertyName);

            if (serviceInstance != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Injecting service " + serviceInstance + " on " + instance + " using property '" + propertyName + "'");
                }
                InvokerHelper.setProperty(instance, propertyName, serviceInstance);
            }
        }
    }

    private boolean isBasicInjectionDisabled() {
        return getConfigValueAsBoolean(getApp().getConfig(), "griffon.basic_injection.disable", false);
    }

    private boolean isEagerInstantiationEnabled() {
        return getConfigValueAsBoolean(getApp().getConfig(), "griffon.services.eager.instantiation", false);
    }
}
