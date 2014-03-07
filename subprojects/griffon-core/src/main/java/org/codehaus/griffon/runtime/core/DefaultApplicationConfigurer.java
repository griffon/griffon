/*
 * Copyright 2008-2014 the original author or authors.
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

import griffon.core.*;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.ArtifactManager;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.ActionInterceptor;
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
import java.beans.PropertyEditorManager;
import java.util.*;

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
    private static final String KEY_GRIFFON_CONTROLLER_ACTION_INTERCEPTOR_ORDER = "griffon.controller.action.interceptor.order";

    private final Object lock = new Object();
    @GuardedBy("lock")
    private boolean initialized;

    private final GriffonApplication application;

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
        EventHandler eventHandler = application.getInjector().getInstance(EventHandler.class);
        application.getEventRouter().addEventListener(eventHandler);
    }

    protected void event(@Nonnull ApplicationEvent event, @Nullable List<?> args) {
        application.getEventRouter().publishEvent(event.getName(), args);
    }

    protected void initializePropertyEditors() {
        ServiceLoaderUtils.load(applicationClassLoader().get(), "META-INF/editors/", PropertyEditor.class, new ServiceLoaderUtils.LineProcessor() {
            @Override
            public void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                try {
                    String[] parts = line.trim().split("=");
                    Class<?> targetType = loadClass(parts[0].trim(), classLoader);
                    Class<?> editorClass = loadClass(parts[1].trim(), classLoader);

                    // Editor must have a no-args constructor
                    // CCE means the class can not be used
                    editorClass.newInstance();
                    PropertyEditorManager.registerEditor(targetType, editorClass);
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
            PropertyEditor editor = PropertyEditorManager.findEditor(pair[0]);
            LOG.debug("Registering {} as editor for {}", editor.getClass().getName(), pair[1].getName());
            PropertyEditorManager.registerEditor(pair[1], editor.getClass());
        }
    }

    protected void initializeResourcesInjector() {
        final ResourceInjector injector = application.getResourceInjector();
        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), new CallableWithArgs<Void>() {
            public Void call(@Nonnull Object... args) {
                Object instance = args[1];
                injector.injectResources(instance);
                return null;
            }
        });
    }

    protected void initializeArtifactManager() {
        Injector<?> injector = application.getInjector();
        ArtifactManager artifactManager = application.getArtifactManager();
        for (ArtifactHandler<?> artifactHandler : injector.getInstances(ArtifactHandler.class)) {
            artifactManager.registerArtifactHandler(artifactHandler);
        }
        artifactManager.loadArtifactMetadata(injector);
        application.addShutdownHandler(artifactManager);
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

        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), new CallableWithArgs<Void>() {
            @Nullable
            public Void call(@Nullable Object... args) {
                Class<?> klass = (Class) args[0];
                if (GriffonController.class.isAssignableFrom(klass)) {
                    GriffonController controller = (GriffonController) args[1];
                    application.getActionManager().createActions(controller);
                }
                return null;
            }
        });

        Injector<?> injector = application.getInjector();
        Collection<ActionInterceptor> interceptorInstances = injector.getInstances(ActionInterceptor.class);
        List<String> interceptorOrder = application.getConfiguration().get(KEY_GRIFFON_CONTROLLER_ACTION_INTERCEPTOR_ORDER, Collections.<String>emptyList());
        Map<String, ActionInterceptor> sortedInterceptors = sortByDependencies(interceptorInstances, ActionInterceptor.SUFFIX, "interceptor", interceptorOrder);

        for (ActionInterceptor interceptor : sortedInterceptors.values()) {
            application.getActionManager().addActionInterceptor(interceptor);
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
