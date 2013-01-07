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

import griffon.core.GriffonApplication;
import griffon.core.GriffonService;
import griffon.core.ServiceManager;
import griffon.core.ShutdownHandler;
import griffon.util.ConfigUtils;
import groovy.util.ConfigObject;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static griffon.core.GriffonServiceClass.TRAILING;
import static java.util.Arrays.asList;

/**
 * Base implementation of the {@code ServiceManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractServiceManager implements ServiceManager {
    private final GriffonApplication app;
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceManager.class);

    public AbstractServiceManager(GriffonApplication app) {
        this.app = app;
        app.addShutdownHandler(new ServiceManagerShutdownHandler());
    }

    public GriffonApplication getApp() {
        return app;
    }

    @Override
    public GriffonService findService(String name) {
        if (!name.endsWith(TRAILING)) {
            name += TRAILING;
        }

        GriffonService service = doFindService(name);
        if (null == service) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Instantiating service identified by '" + name + "'");
            }
            service = doInstantiateService(name);
            if (null != service) {
                InvokerHelper.setProperty(service, "app", getApp());
                doSetConfigProperties(name, service);
                if (LOG.isInfoEnabled()) {
                    LOG.info("Initializing service identified by '" + name + "'");
                }
                service.serviceInit();
            }
        }
        return service;
    }

    protected void doSetConfigProperties(String name, GriffonService service) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Applying configuration to service identified by '" + name + "'");
        }
        name = name.substring(0, name.length() - TRAILING.length());
        ConfigObject config = (ConfigObject) ConfigUtils.getConfigValue(getApp().getConfig(), "services." + name);
        if (config != null && !config.isEmpty()) InvokerHelper.setProperties(service, config);
    }

    protected abstract GriffonService doFindService(String name);

    protected abstract GriffonService doInstantiateService(String name);

    private class ServiceManagerShutdownHandler implements ShutdownHandler {
        @Override
        public boolean canShutdown(GriffonApplication application) {
            return true;
        }

        @Override
        public void onShutdown(GriffonApplication application) {
            for (Map.Entry<String, GriffonService> entry : getServices().entrySet()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Destroying service identified by '" + entry.getKey() + "'");
                }
                GriffonService service = entry.getValue();
                application.removeApplicationEventListener(service);
                application.event(GriffonApplication.Event.DESTROY_INSTANCE.getName(), asList(service.getClass(), service.getGriffonClass().getArtifactType(), service));
                service.serviceDestroy();
            }
        }
    }
}
