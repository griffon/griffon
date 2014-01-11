/*
 * Copyright 2008-2014 the original author or authors.
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
import org.codehaus.griffon.runtime.core.injection.NamedImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.*;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.getLogicalPropertyName;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Utility class for bootstrapping an application.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public final class GriffonApplicationSupport {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonApplicationSupport.class);

    private static final String ERROR_APPLICATION_NULL = "Argument 'application' cannot be null";
    private static final String KEY_APP_LIFECYCLE_HANDLER_DISABLE = "application.lifecycle.handler.disable";
    private static final String KEY_GRIFFON_CONTROLLER_ACTION_INTERCEPTOR_ORDER = "griffon.controller.action.interceptor.order";

    public static void init(@Nonnull GriffonApplication application) {
        requireNonNull(application, ERROR_APPLICATION_NULL);

        EventHandler eventHandler = application.getInjector().getInstance(EventHandler.class);
        application.getEventRouter().addEventListener(eventHandler);

        event(application, ApplicationEvent.BOOTSTRAP_START, asList(application));

        initializePropertyEditors(application);
        initializeResourcesInjector(application);
        runLifecycleHandler(Lifecycle.INITIALIZE, application);
        applyPlatformTweaks(application);
        initializeAddonManager(application);
        initializeMvcManager(application);
        initializeActionManager(application);
        initializeArtifactManager(application);

        event(application, ApplicationEvent.BOOTSTRAP_END, asList(application));
    }

    protected static void event(@Nonnull GriffonApplication application, @Nonnull ApplicationEvent event, @Nullable List<?> args) {
        application.getEventRouter().publish(event.getName(), args);
    }

    private static void initializePropertyEditors(final @Nonnull GriffonApplication application) {
        ServiceLoaderUtils.load(applicationClassLoader(application).get(), "META-INF/editors/", PropertyEditor.class, new ServiceLoaderUtils.LineProcessor() {
            @Override
            public void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                try {
                    String[] parts = line.trim().split("=");
                    Class<?> targetType = loadClass(parts[0].trim(), classLoader);
                    Class<?> editorClass = loadClass(parts[1].trim(), classLoader);
                    LOG.debug("Registering {} as editor for {}", editorClass.getName(), targetType.getName());

                    // Editor must have a no-args constructor
                    // CCE means the class can not be used
                    editorClass.newInstance();
                    PropertyEditorManager.registerEditor(targetType, editorClass);
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

    private static void initializeResourcesInjector(@Nonnull GriffonApplication application) {
        final ResourceInjector injector = application.getResourceInjector();
        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), new CallableWithArgs<Void>() {
            public Void call(@Nonnull Object... args) {
                Object instance = args[1];
                injector.injectResources(instance);
                return null;
            }
        });
    }

    private static void initializeArtifactManager(@Nonnull GriffonApplication application) {
        Injector<?> injector = application.getInjector();
        ArtifactManager artifactManager = application.getArtifactManager();
        for (ArtifactHandler<?> artifactHandler : injector.getInstances(ArtifactHandler.class)) {
            artifactManager.registerArtifactHandler(artifactHandler);
        }
        artifactManager.loadArtifactMetadata(injector);
        application.addShutdownHandler(artifactManager);
    }

    private static void applyPlatformTweaks(@Nonnull GriffonApplication application) {
        PlatformHandler platformHandler = application.getInjector().getInstance(PlatformHandler.class);
        platformHandler.handle(application);
    }

    private static void initializeAddonManager(@Nonnull GriffonApplication application) {
        application.getAddonManager().initialize();
    }

    @SuppressWarnings("unchecked")
    private static void initializeMvcManager(@Nonnull GriffonApplication application) {
        Map<String, MVCGroupConfiguration> configurations = new LinkedHashMap<>();
        Map<String, Map<String, Object>> mvcGroups = application.getApplicationConfiguration().get("mvcGroups", Collections.<String, Map<String, Object>>emptyMap());
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

    private static void initializeActionManager(final @Nonnull GriffonApplication application) {
        if (application.getActionManager() instanceof NoopActionManager) {
            return;
        }

        application.getEventRouter().addEventListener(ApplicationEvent.NEW_INSTANCE.getName(), new CallableWithArgs<Void>() {
            public Void call(@Nonnull Object... args) {
                Class<?> klass = (Class) args[0];
                if (GriffonController.class.isAssignableFrom(klass)) {
                    GriffonController controller = (GriffonController) args[1];
                    application.getActionManager().createActions(controller);
                }
                return null;
            }
        });

        Injector<?> injector = application.getInjector();
        Map<String, ActionInterceptor> actionInterceptors = new LinkedHashMap<>();
        for (ActionInterceptor actionInterceptor : injector.getInstances(ActionInterceptor.class)) {
            actionInterceptors.put(nameFor(actionInterceptor), actionInterceptor);
        }

        // grab application specific order
        List<String> interceptorOrder = application.getApplicationConfiguration().get(KEY_GRIFFON_CONTROLLER_ACTION_INTERCEPTOR_ORDER, Collections.<String>emptyList());
        Map<String, ActionInterceptor> tmp = new LinkedHashMap<>(actionInterceptors);
        Map<String, ActionInterceptor> map = new LinkedHashMap<>();
        //noinspection ConstantConditions
        for (String interceptorName : interceptorOrder) {
            if (tmp.containsKey(interceptorName)) {
                map.put(interceptorName, tmp.remove(interceptorName));
            }
        }
        map.putAll(tmp);
        actionInterceptors.clear();
        actionInterceptors.putAll(map);
        LOG.debug("Chosen interceptor order is {}", map.keySet());

        List<ActionInterceptor> sortedInterceptors = new ArrayList<>();
        Set<String> addedDeps = new LinkedHashSet<>();

        while (!map.isEmpty()) {
            int processed = 0;

            LOG.trace("Current interceptor order is {}", actionInterceptors.keySet());

            for (Iterator<Map.Entry<String, ActionInterceptor>> iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, ActionInterceptor> entry = iter.next();
                String interceptorName = entry.getKey();
                List<String> dependsOn = entry.getValue().dependsOn();

                LOG.trace("Processing interceptor '{}'", interceptorName);
                LOG.trace("    depends on '{}'", dependsOn);

                if (!dependsOn.isEmpty()) {
                    LOG.trace("  Checking interceptor '" + interceptorName + "' dependencies (" + dependsOn.size() + ")");

                    boolean failedDep = false;
                    for (String dep : dependsOn) {
                        LOG.trace("  Checking interceptor '{}' dependencies: {}", interceptorName, dep);
                        if (!addedDeps.contains(dep)) {
                            // dep not in the list yet, we need to skip adding this to the list for now
                            LOG.trace("  Skipped interceptor '{}', since dependency '{}' not yet added", interceptorName, dep);
                            failedDep = true;
                            break;
                        } else {
                            LOG.trace("  Interceptor '{}' dependency '{}' already added", interceptorName, dep);
                        }
                    }

                    if (failedDep) {
                        // move on to next dependency
                        continue;
                    }
                }

                LOG.trace("  Adding interceptor '{}', since all dependencies have been added", interceptorName);
                sortedInterceptors.add(entry.getValue());
                addedDeps.add(interceptorName);
                iter.remove();
                processed++;
            }

            if (processed == 0) {
                // we have a cyclical dependency, warn the user and load in the order they appeared originally
                if (LOG.isWarnEnabled()) {
                    LOG.warn("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    LOG.warn("::   Unresolved interceptor dependencies detected   ::");
                    LOG.warn("::   Continuing with original interceptor order     ::");
                    LOG.warn("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                }
                for (Map.Entry<String, ActionInterceptor> entry : map.entrySet()) {
                    String interceptorName = entry.getKey();
                    List<String> dependsOn = entry.getValue().dependsOn();

                    // display this as a cyclical dep
                    LOG.warn("::   Interceptor {}", interceptorName);
                    if (!dependsOn.isEmpty()) {
                        for (String dep : dependsOn) {
                            LOG.warn("::     depends on {}", dep);
                        }
                    } else {
                        // we should only have items left in the list with deps, so this should never happen
                        // but a wise man once said...check for true, false and otherwise...just in case
                        LOG.warn("::   Problem while resolving dependencies.");
                        LOG.warn("::   Unable to resolve dependency hierarchy.");
                    }
                    LOG.warn("::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                }
                break;
                // if we have processed all the interceptors, we are done
            } else if (sortedInterceptors.size() == actionInterceptors.size()) {
                LOG.trace("Interceptor dependency ordering complete");
                break;
            }
        }

        if (LOG.isDebugEnabled()) {
            List<String> sortedInterceptorNames = new ArrayList<>();
            for (ActionInterceptor interceptor : sortedInterceptors) {
                sortedInterceptorNames.add(nameFor(interceptor));
            }
            LOG.debug("Computed interceptor order is {}", sortedInterceptorNames);
        }

        for (ActionInterceptor interceptor : sortedInterceptors) {
            application.getActionManager().addActionInterceptor(interceptor);
        }
    }

    @Nonnull
    private static String nameFor(@Nonnull ActionInterceptor actionInterceptor) {
        Named named = actionInterceptor.getClass().getAnnotation(Named.class);
        if (named != null && !isBlank(named.value())) {
            return named.value();
        }
        return getLogicalPropertyName(actionInterceptor.getClass().getName(), "ActionInterceptor");
    }

    public static void runLifecycleHandler(@Nonnull Lifecycle lifecycle, @Nonnull GriffonApplication application) {
        requireNonNull(application, ERROR_APPLICATION_NULL);
        requireNonNull(lifecycle, "Argument 'lifecycle' cannot be null");

        boolean skipHandler = application.getApplicationConfiguration().getAsBoolean(KEY_APP_LIFECYCLE_HANDLER_DISABLE, false);
        if (skipHandler) {
            LOG.info("Lifecycle handler '{}' has been disabled. SKIPPING.", lifecycle.getName());
            return;
        }

        LifecycleHandler handler;
        try {
            handler = application.getInjector().getInstance(LifecycleHandler.class, new NamedImpl(lifecycle.getName()));
        } catch (Exception e) {
            // the script must not exist, do nothing
            //LOGME - may be because of chained failures
            return;
        }

        handler.execute();
    }

    public static Class<?> loadClass(@Nonnull String className, @Nonnull ClassLoader classLoader) throws ClassNotFoundException {
        ClassNotFoundException cnfe;

        ClassLoader cl = GriffonApplicationSupport.class.getClassLoader();
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

    private static ApplicationClassLoader applicationClassLoader(@Nonnull GriffonApplication application) {
        return application.getInjector().getInstance(ApplicationClassLoader.class);
    }
}
