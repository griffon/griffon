/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.event.EventHandler;
import griffon.core.ApplicationClassLoader;
import griffon.core.ApplicationConfigurer;
import griffon.core.GriffonApplication;
import griffon.core.LifecycleHandler;
import griffon.core.PlatformHandler;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.ArtifactManager;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionHandler;
import griffon.core.env.Lifecycle;
import griffon.core.event.Event;
import griffon.core.event.XEventHandler;
import griffon.core.events.BootstrapEndEvent;
import griffon.core.events.BootstrapStartEvent;
import griffon.core.events.NewInstanceEvent;
import griffon.core.injection.Injector;
import griffon.core.mvc.MVCGroupConfiguration;
import org.codehaus.griffon.runtime.core.controller.NoopActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.util.AnnotationUtils.named;
import static griffon.util.AnnotationUtils.sortByDependencies;
import static java.util.Objects.requireNonNull;

/**
 * Utility class for bootstrapping an application.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public class DefaultApplicationConfigurer implements ApplicationConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultApplicationConfigurer.class);

    private static final String ERROR_APPLICATION_NULL = "Argument 'application' must not be null";
    private static final String KEY_APP_LIFECYCLE_HANDLER_DISABLE = "application.lifecycle.handler.disable";
    private static final String KEY_GRIFFON_CONTROLLER_ACTION_HANDLER_ORDER = "griffon.controller.action.handler.order";

    private final Object lock = new Object();
    private final GriffonApplication application;
    // @GuardedBy("lock")
    private boolean initialized;

    @Inject
    public DefaultApplicationConfigurer(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, ERROR_APPLICATION_NULL);
    }

    @Override
    public final void init() {
        synchronized (lock) {
            if (!initialized) {
                doInitialize();
                initialized = true;
            }
        }
    }

    @Override
    public void runLifecycleHandler(@Nonnull Lifecycle lifecycle) {
        requireNonNull(lifecycle, "Argument 'lifecycle' must not be null");

        boolean skipHandler = application.getConfiguration().getAsBoolean(KEY_APP_LIFECYCLE_HANDLER_DISABLE, false);
        if (skipHandler) {
            LOG.info("Lifecycle handler '{}' has been disabled. SKIPPING.", lifecycle.getName());
            return;
        }

        LifecycleHandler handler;
        try {
            handler = application.getInjector().getInstance(LifecycleHandler.class, named(lifecycle.getName()));
        } catch (Exception e) {
            // the script must not exist, do nothing
            //LOGME - may be because of chained failures
            return;
        }

        handler.execute();
    }

    protected void doInitialize() {
        initializeEventHandler();

        event(BootstrapStartEvent.of(application));

        initializeConfigurationManager();
        runLifecycleHandler(Lifecycle.INITIALIZE);
        applyPlatformTweaks();
        initializeAddonManager();
        initializeMvcManager();
        initializeActionManager();
        initializeArtifactManager();

        event(BootstrapEndEvent.of(application));
    }

    protected void initializeEventHandler() {
        Collection<XEventHandler> handlerInstances = application.getInjector().getInstances(XEventHandler.class);
        Map<String, XEventHandler> sortedHandlers = sortByDependencies(handlerInstances, "EventHandler", "handler");
        for (XEventHandler handler : sortedHandlers.values()) {
            application.getEventRouter().subscribe(handler);
        }
        application.getEventRouter().subscribe(this);
    }

    protected <E extends Event> void event(@Nonnull E event) {
        application.getEventRouter().publishEvent(event);
    }

    @EventHandler
    public void handleNewInstanceEvent(@Nonnull NewInstanceEvent event) {
        application.getResourceInjector().injectResources(event.getInstance());

        if (GriffonController.class.isAssignableFrom(event.getType())) {
            application.getActionManager().createActions((GriffonController) event.getInstance());
        }
    }

    protected void initializeArtifactManager() {
        Injector<?> injector = application.getInjector();
        ArtifactManager artifactManager = application.getArtifactManager();
        for (ArtifactHandler<?> artifactHandler : injector.getInstances(ArtifactHandler.class)) {
            artifactManager.registerArtifactHandler(artifactHandler);
        }
        artifactManager.loadArtifactMetadata();
    }

    protected void applyPlatformTweaks() {
        PlatformHandler platformHandler = application.getInjector().getInstance(PlatformHandler.class);
        platformHandler.handle(application);
    }

    protected void initializeConfigurationManager() {
        application.getConfigurationManager();
    }

    protected void initializeAddonManager() {
        application.getAddonManager().initialize();
    }

    @SuppressWarnings("unchecked")
    protected void initializeMvcManager() {
        Map<String, MVCGroupConfiguration> configurations = new LinkedHashMap<>();
        Map<String, Map<String, Object>> mvcGroups = application.getConfiguration().get("mvcGroups", Collections.<String, Map<String, Object>>emptyMap());
        if (mvcGroups != null) {
            for (Map.Entry<String, Map<String, Object>> groupEntry : mvcGroups.entrySet()) {
                String type = groupEntry.getKey();
                LOG.debug("Adding MVC group {}", type);
                Map<String, Object> members = groupEntry.getValue();
                Map<String, Object> configMap = new LinkedHashMap<>();
                Map<String, String> membersCopy = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : members.entrySet()) {
                    String key = String.valueOf(entry.getKey());
                    if ("config".equals(key) && entry.getValue() instanceof Map) {
                        configMap = (Map<String, Object>) entry.getValue();
                    } else {
                        membersCopy.put(key, String.valueOf(entry.getValue()));
                    }
                }
                configurations.put(type, application.getMvcGroupManager().newMVCGroupConfiguration(type, membersCopy, configMap));
            }
        }

        application.getMvcGroupManager().initialize(configurations);
    }

    protected void initializeActionManager() {
        if (application.getActionManager() instanceof NoopActionManager) {
            return;
        }

        Injector<?> injector = application.getInjector();
        Collection<ActionHandler> handlerInstances = injector.getInstances(ActionHandler.class);
        List<String> handlerOrder = application.getConfiguration().get(KEY_GRIFFON_CONTROLLER_ACTION_HANDLER_ORDER, Collections.<String>emptyList());
        Map<String, ActionHandler> sortedHandlers = sortByDependencies(handlerInstances, ActionHandler.SUFFIX, "handler", handlerOrder);

        for (ActionHandler handler : sortedHandlers.values()) {
            application.getActionManager().addActionHandler(handler);
        }
    }

    protected Class<?> loadClass(@Nonnull String className, @Nonnull ClassLoader classLoader) throws ClassNotFoundException {
        ClassNotFoundException cnfe;

        ClassLoader cl = DefaultApplicationConfigurer.class.getClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        cl = classLoader;
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        throw cnfe;
    }

    private ApplicationClassLoader applicationClassLoader() {
        return application.getInjector().getInstance(ApplicationClassLoader.class);
    }
}
