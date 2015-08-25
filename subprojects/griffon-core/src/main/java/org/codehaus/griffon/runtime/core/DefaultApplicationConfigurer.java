/*
 * Copyright 2008-2015 the original author or authors.
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

import griffon.core.ApplicationClassLoader;
import griffon.core.ApplicationConfigurer;
import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.LifecycleHandler;
import griffon.core.PlatformHandler;
import griffon.core.RunnableWithArgs;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.ArtifactManager;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionHandler;
import griffon.core.controller.ActionInterceptor;
import griffon.core.editors.PropertyEditorResolver;
import griffon.core.env.Lifecycle;
import griffon.core.event.EventHandler;
import griffon.core.injection.Injector;
import griffon.core.mvc.MVCGroupConfiguration;
import griffon.core.resources.ResourceInjector;
import griffon.util.ServiceLoaderUtils;
import org.codehaus.griffon.runtime.core.controller.NoopActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;
import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.AnnotationUtils.named;
import static griffon.util.AnnotationUtils.sortByDependencies;
import static java.util.Arrays.asList;
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
    @GuardedBy("lock")
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

        event(ApplicationEvent.BOOTSTRAP_START, asList(application));

        initializePropertyEditors();
        initializeResourcesInjector();
        runLifecycleHandler(Lifecycle.INITIALIZE);
        applyPlatformTweaks();
        initializeAddonManager();
        initializeMvcManager();
        initializeActionManager();
        initializeArtifactManager();

        event(ApplicationEvent.BOOTSTRAP_END, asList(application));
    }

    protected void initializeEventHandler() {
        Collection<EventHandler> handlerInstances =  application.getInjector().getInstances(EventHandler.class);
        Map<String, EventHandler> sortedHandlers = sortByDependencies(handlerInstances, "EventHandler", "handler");
        for (EventHandler handler : sortedHandlers.values()) {
            application.getEventRouter().addEventListener(handler);
        }
    }

    protected void event(@Nonnull ApplicationEvent event, @Nullable List<?> args) {
        application.getEventRouter().publishEvent(event.getName(), args);
    }

    protected void initializePropertyEditors() {
        ServiceLoaderUtils.load(applicationClassLoader().get(), "META-INF/editors/", PropertyEditor.class, new ServiceLoaderUtils.LineProcessor() {
            @Override
            @SuppressWarnings("unchecked")
            public void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                try {
                    String[] parts = line.trim().split("=");
                    Class<?> targetType = loadClass(parts[0].trim(), classLoader);
                    Class<? extends PropertyEditor> editorClass = (Class<? extends PropertyEditor>) loadClass(parts[1].trim(), classLoader);

                    // Editor must have a no-args constructor
                    // CCE means the class can not be used
                    editorClass.newInstance();
                    PropertyEditorResolver.registerEditor(targetType, editorClass);
                    LOG.debug("Registering {} as editor for {}", editorClass.getName(), targetType.getName());
                } catch (Exception e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Could not load " + type.getName() + " with " + line, sanitize(e));
                    }
                }
            }
        });

        Class<?>[][] pairs = new Class<?>[][]{
            new Class<?>[]{Boolean.class, Boolean.TYPE},
            new Class<?>[]{Byte.class, Byte.TYPE},
            new Class<?>[]{Short.class, Short.TYPE},
            new Class<?>[]{Integer.class, Integer.TYPE},
            new Class<?>[]{Long.class, Long.TYPE},
            new Class<?>[]{Float.class, Float.TYPE},
            new Class<?>[]{Double.class, Double.TYPE}
        };

        for (Class<?>[] pair : pairs) {
            PropertyEditor editor = PropertyEditorResolver.findEditor(pair[0]);
            LOG.debug("Registering {} as editor for {}", editor.getClass().getName(), pair[1].getName());
            PropertyEditorResolver.registerEditor(pair[1], editor.getClass());
        }
    }

    protected void initializeResourcesInjector() {
        final ResourceInjector injector = application.getResourceInjector();
        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), new RunnableWithArgs() {
            public void run(@Nullable Object... args) {
                Object instance = args[1];
                injector.injectResources(instance);
            }
        });
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

        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), new RunnableWithArgs() {
            public void run(@Nullable Object... args) {
                Class<?> klass = (Class) args[0];
                if (GriffonController.class.isAssignableFrom(klass)) {
                    GriffonController controller = (GriffonController) args[1];
                    application.getActionManager().createActions(controller);
                }
            }
        });

        Injector<?> injector = application.getInjector();
        Collection<ActionHandler> handlerInstances = injector.getInstances(ActionHandler.class);
        List<String> handlerOrder = application.getConfiguration().get(KEY_GRIFFON_CONTROLLER_ACTION_HANDLER_ORDER, Collections.<String>emptyList());
        Map<String, ActionHandler> sortedHandlers = sortByDependencies(handlerInstances, ActionHandler.SUFFIX, "handler", handlerOrder);

        for (ActionHandler handler : sortedHandlers.values()) {
            application.getActionManager().addActionHandler(handler);
        }

        Collection<ActionInterceptor> interceptorInstances = injector.getInstances(ActionInterceptor.class);
        if (!interceptorInstances.isEmpty()) {
            application.getLog().error(ActionInterceptor.class.getName() + " has been deprecated and is no longer supported");
            throw new UnsupportedOperationException(ActionInterceptor.class.getName() + " has been deprecated and is no longer supported");
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
