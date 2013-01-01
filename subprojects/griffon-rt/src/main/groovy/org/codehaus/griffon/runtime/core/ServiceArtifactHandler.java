/*
 * Copyright 2009-2013 the original author or authors.
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
import griffon.exceptions.NewInstanceCreationException;
import griffon.util.ApplicationHolder;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static griffon.util.GriffonExceptionHandler.sanitize;
import static java.util.Arrays.asList;

/**
 * Handler for 'Service' artifacts.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public class ServiceArtifactHandler extends ArtifactHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceArtifactHandler.class);
    private final DefaultServiceManager serviceManager;

    private class DefaultServiceManager extends AbstractServiceManager {
        private final Map<String, GriffonService> serviceInstances = new ConcurrentHashMap<String, GriffonService>();

        public DefaultServiceManager(GriffonApplication app) {
            super(app);
        }

        public Map<String, GriffonService> getServices() {
            return Collections.unmodifiableMap(serviceInstances);
        }

        protected GriffonService doFindService(String name) {
            return serviceInstances.get(name);
        }

        protected GriffonService doInstantiateService(String name) {
            return doInstantiateService0(name, true);
        }

        private GriffonService doInstantiateService0(String name, boolean triggerEvent) {
            GriffonService serviceInstance = null;
            GriffonClass griffonClass = findClassFor(name);
            if (griffonClass != null) {
                serviceInstance = instantiateService(griffonClass);
                serviceInstances.put(name, serviceInstance);
                getApp().addApplicationEventListener(serviceInstance);
                if (triggerEvent) {
                    getApp().event(GriffonApplication.Event.NEW_INSTANCE.getName(),
                        asList(griffonClass.getClazz(), GriffonServiceClass.TYPE, serviceInstance));
                }
            }
            return serviceInstance;
        }

        private GriffonService instantiateService(GriffonClass griffonClass) {
            try {
                GriffonService serviceInstance = (GriffonService) griffonClass.getClazz().newInstance();
                InvokerHelper.setProperty(serviceInstance, "app", getApp());
                return serviceInstance;
            } catch (Exception e) {
                Throwable targetException = null;
                if (e instanceof InvocationTargetException) {
                    targetException = ((InvocationTargetException) e).getTargetException();
                } else {
                    targetException = e;
                }
                throw new NewInstanceCreationException("Could not create a new instance of class " + griffonClass.getClazz().getName(), sanitize(targetException));
            }
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
        getApp().addApplicationEventListener(this);
        if (isEagerInstantiationEnabled()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Instantiating service instances eagerly");
            }
            for (ArtifactInfo artifactInfo : artifacts) {
                GriffonClass griffonClass = getClassFor(artifactInfo.getClazz());
                serviceManager.doInstantiateService0(griffonClass.getPropertyName(), false);
            }
            for (ArtifactInfo artifactInfo : artifacts) {
                GriffonClass griffonClass = getClassFor(artifactInfo.getClazz());
                GriffonService serviceInstance = serviceManager.findService(griffonClass.getPropertyName());
                getApp().event(GriffonApplication.Event.NEW_INSTANCE.getName(),
                    asList(griffonClass.getClazz(), GriffonServiceClass.TYPE, serviceInstance));
            }
        }
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

    public static boolean isBasicInjectionDisabled() {
        return getConfigValueAsBoolean(ApplicationHolder.getApplication().getConfig(), "griffon.services.basic.disabled", false);
    }

    private boolean isEagerInstantiationEnabled() {
        return getConfigValueAsBoolean(getApp().getConfig(), "griffon.services.eager.instantiation", false);
    }
}
