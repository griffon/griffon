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

package org.codehaus.griffon.runtime.core.service;

import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.ShutdownHandler;
import griffon.core.artifact.GriffonService;
import griffon.core.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

import static griffon.core.artifact.GriffonServiceClass.TRAILING;
import static griffon.util.GriffonClassUtils.setProperties;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code ServiceManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.4
 */
public abstract class AbstractServiceManager implements ServiceManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractServiceManager.class);

    private final GriffonApplication application;

    @Inject
    public AbstractServiceManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
        application.addShutdownHandler(new ServiceManagerShutdownHandler());
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    @Override
    public Map<String, GriffonService> getServices() {
        // TODO finish me!!
        return Collections.emptyMap();
    }

    @Nullable
    @Override
    public GriffonService findService(@Nonnull String name) {
        requireNonBlank(name, "Argument 'name' cannot be blank");
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
                doSetConfigProperties(name, service);
                if (LOG.isInfoEnabled()) {
                    LOG.info("Initializing service identified by '" + name + "'");
                }
                service.serviceInit();
            }
        }
        return service;
    }

    protected void doSetConfigProperties(@Nonnull String name, @Nonnull GriffonService service) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Applying configuration to service identified by '" + name + "'");
        }
        name = name.substring(0, name.length() - TRAILING.length());
        Map<String, Object> serviceProperties = application.getApplicationConfiguration().get("services." + name, Collections.<String, Object>emptyMap());
        setProperties(service, serviceProperties);
    }

    protected abstract GriffonService doFindService(String name);

    protected abstract GriffonService doInstantiateService(String name);

    private class ServiceManagerShutdownHandler implements ShutdownHandler {
        @Override
        public boolean canShutdown(@Nonnull GriffonApplication application) {
            return true;
        }

        @Override
        public void onShutdown(@Nonnull GriffonApplication application) {
            for (Map.Entry<String, GriffonService> entry : getServices().entrySet()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Destroying service identified by '" + entry.getKey() + "'");
                }
                GriffonService service = entry.getValue();
                application.getEventRouter().removeEventListener(service);
                application.getEventRouter().publish(ApplicationEvent.DESTROY_INSTANCE.getName(), asList(service.getClass(), service.getGriffonClass().getArtifactType(), service));
                service.serviceDestroy();
            }
        }
    }
}
