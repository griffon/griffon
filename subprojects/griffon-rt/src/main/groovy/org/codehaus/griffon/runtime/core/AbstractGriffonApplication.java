/*
 * Copyright 2008-2012 the original author or authors.
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
import griffon.util.ApplicationHolder;
import griffon.util.ConfigUtils;
import griffon.util.Metadata;
import griffon.util.RunnableWithArgs;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.ConfigObject;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper;
import org.codehaus.griffon.runtime.util.MVCGroupExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;

/**
 * Implements the basics for a skeleton GriffonApplication.<p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public abstract class AbstractGriffonApplication extends AbstractObservable implements GriffonApplication {
    private Binding bindings = new Binding();
    private ConfigObject config;
    private ConfigObject builderConfig;
    private Object eventsConfig;
    private AddonManager addonManager;
    private ArtifactManager artifactManager;
    private MVCGroupManager mvcGroupManager;
    private ServiceManager serviceManager;

    private Locale locale = Locale.getDefault();
    public static final String[] EMPTY_ARGS = new String[0];
    protected final Object lock = new Object();
    private ApplicationPhase phase = ApplicationPhase.INITIALIZE;

    private final EventRouter eventRouter = new EventRouter();
    private final ResourceLocator resourceLocator = new ResourceLocator();
    private final List<ShutdownHandler> shutdownHandlers = new ArrayList<ShutdownHandler>();
    private final String[] startupArgs;
    private final Object shutdownLock = new Object();
    private final Logger log;

    public AbstractGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public AbstractGriffonApplication(String[] args) {
        startupArgs = new String[args.length];
        System.arraycopy(args, 0, startupArgs, 0, args.length);
        ApplicationHolder.setApplication(this);
        log = LoggerFactory.getLogger(getClass());
        MVCGroupExceptionHandler.registerWith(this);
    }

    public Binding getBindings() {
        return bindings;
    }

    public void setBindings(Binding bindings) {
        this.bindings = bindings;
    }

    public ConfigObject getConfig() {
        return config;
    }

    public void setConfig(ConfigObject config) {
        this.config = config;
    }

    public ConfigObject getBuilderConfig() {
        return builderConfig;
    }

    public void setBuilderConfig(ConfigObject builderConfig) {
        this.builderConfig = builderConfig;
    }

    public Object getEventsConfig() {
        return eventsConfig;
    }

    public void setEventsConfig(Object eventsConfig) {
        this.eventsConfig = eventsConfig;
    }

    public Map<String, ? extends GriffonModel> getModels() {
        return getMvcGroupManager().getModels();
    }

    public Map<String, ? extends GriffonView> getViews() {
        return getMvcGroupManager().getViews();
    }

    public Map<String, ? extends GriffonController> getControllers() {
        return getMvcGroupManager().getControllers();
    }

    public Map<String, ? extends FactoryBuilderSupport> getBuilders() {
        return getMvcGroupManager().getBuilders();
    }

    public Map<String, MVCGroup> getGroups() {
        return getMvcGroupManager().getGroups();
    }

    public AddonManager getAddonManager() {
        return addonManager;
    }

    public void setAddonManager(AddonManager addonManager) {
        this.addonManager = addonManager;
    }

    public ArtifactManager getArtifactManager() {
        return artifactManager;
    }

    public void setArtifactManager(ArtifactManager artifactManager) {
        this.artifactManager = artifactManager;
    }

    public MVCGroupManager getMvcGroupManager() {
        return mvcGroupManager;
    }

    public void setMvcGroupManager(MVCGroupManager mvcGroupManager) {
        this.mvcGroupManager = mvcGroupManager;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public Map<String, ? extends GriffonService> getServices() {
        return serviceManager.getServices();
    }

    public Locale getLocale() {
        return locale;
    }

    public String[] getStartupArgs() {
        return startupArgs;
    }

    public Logger getLog() {
        return log;
    }

    public void setLocale(Locale locale) {
        firePropertyChange("locale", this.locale, this.locale = locale);
    }

    public Metadata getMetadata() {
        return Metadata.getCurrent();
    }

    public Class getAppConfigClass() {
        return loadClass(GriffonApplication.Configuration.APPLICATION.getName());
    }

    public Class getConfigClass() {
        return loadClass(GriffonApplication.Configuration.CONFIG.getName());
    }

    public Class getBuilderClass() {
        return loadClass(GriffonApplication.Configuration.BUILDER.getName());
    }

    public Class getEventsClass() {
        return loadClass(GriffonApplication.Configuration.EVENTS.getName());
    }

    public void initialize() {
        if (phase == ApplicationPhase.INITIALIZE) {
            GriffonApplicationHelper.prepare(this);
        }
    }

    public void ready() {
        if (phase != ApplicationPhase.STARTUP) return;

        phase = ApplicationPhase.READY;
        event(GriffonApplication.Event.READY_START.getName(), asList(this));
        GriffonApplicationHelper.runLifecycleHandler(GriffonApplication.Lifecycle.READY.getName(), this);
        event(GriffonApplication.Event.READY_END.getName(), asList(this));
        phase = ApplicationPhase.MAIN;
    }

    public boolean canShutdown() {
        event(GriffonApplication.Event.SHUTDOWN_REQUESTED.getName(), asList(this));
        synchronized (shutdownLock) {
            for (ShutdownHandler handler : shutdownHandlers) {
                if (!handler.canShutdown(this)) {
                    event(GriffonApplication.Event.SHUTDOWN_ABORTED.getName(), asList(this));
                    if (log.isDebugEnabled()) {
                        try {
                            log.debug("Shutdown aborted by " + handler);
                        } catch (UnsupportedOperationException uoe) {
                            log.debug("Shutdown aborted by a handler");
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public boolean shutdown() {
        // avoids reentrant calls to shutdown()
        // once permission to quit has been granted
        if (phase == ApplicationPhase.SHUTDOWN) return false;

        if (!canShutdown()) return false;
        log.info("Shutdown is in process");

        // signal that shutdown is in process
        phase = ApplicationPhase.SHUTDOWN;

        // stage 1 - alert all app event handlers
        // wait for all handlers to complete before proceeding
        // with stage #2 if and only if the current thread is
        // the ui thread
        log.debug("Shutdown stage 1: notify all event listeners");
        if (isEventPublishingEnabled()) {
            final CountDownLatch latch = new CountDownLatch(isUIThread() ? 1 : 0);
            addApplicationEventListener(GriffonApplication.Event.SHUTDOWN_START.getName(), new RunnableWithArgs() {
                @Override
                public void run(Object[] args) {
                    latch.countDown();
                }
            });
            event(GriffonApplication.Event.SHUTDOWN_START.getName(), asList(this));
            try {
                latch.await();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        // stage 2 - alert all shutdown handlers
        log.debug("Shutdown stage 2: notify all shutdown handlers");
        synchronized (shutdownLock) {
            for (ShutdownHandler handler : shutdownHandlers) {
                handler.onShutdown(this);
            }
        }

        // stage 3 - destroy all mvc groups
        log.debug("Shutdown stage 3: destroy all MVC groups");
        List<String> mvcNames = new ArrayList<String>();
        if (getMvcGroupManager() != null) {
            mvcNames.addAll(getMvcGroupManager().getGroups().keySet());
            for (String name : mvcNames) {
                destroyMVCGroup(name);
            }
        }

        // stage 4 - call shutdown script
        log.debug("Shutdown stage 4: execute Shutdown script");
        GriffonApplicationHelper.runLifecycleHandler(GriffonApplication.Lifecycle.SHUTDOWN.getName(), this);

        return true;
    }

    public void startup() {
        if (phase != ApplicationPhase.INITIALIZE) return;

        phase = ApplicationPhase.STARTUP;
        event(GriffonApplication.Event.STARTUP_START.getName(), asList(this));

        Object startupGroups = ConfigUtils.getConfigValue(getConfig(), "application.startupGroups");
        if (startupGroups instanceof List) {
            if (log.isInfoEnabled()) {
                log.info("Initializing all startup groups: " + startupGroups);
            }

            for (String groupName : (List<String>) startupGroups) {
                createMVCGroup(groupName);
            }
        } else if (startupGroups != null && startupGroups.getClass().isArray()) {
            Object[] groups = (Object[]) startupGroups;
            if (log.isInfoEnabled()) {
                log.info("Initializing all startup groups: " + Arrays.toString(groups));
            }

            for (Object groupName : groups) {
                createMVCGroup(String.valueOf(groupName));
            }
        }

        GriffonApplicationHelper.runLifecycleHandler(GriffonApplication.Lifecycle.STARTUP.getName(), this);

        event(GriffonApplication.Event.STARTUP_END.getName(), asList(this));
    }

    public void event(String eventName) {
        eventRouter.publish(eventName, Collections.emptyList());
    }

    public void event(String eventName, List params) {
        eventRouter.publish(eventName, params);
    }

    public void eventOutsideUI(String eventName) {
        eventRouter.publishOutsideUI(eventName, Collections.emptyList());
    }

    public void eventOutsideUI(String eventName, List params) {
        eventRouter.publishOutsideUI(eventName, params);
    }

    public void eventAsync(String eventName) {
        eventRouter.publishAsync(eventName, Collections.emptyList());
    }

    public void eventAsync(String eventName, List params) {
        eventRouter.publishAsync(eventName, params);
    }

    public void addApplicationEventListener(Object listener) {
        eventRouter.addEventListener(listener);
    }

    public void removeApplicationEventListener(Object listener) {
        eventRouter.removeEventListener(listener);
    }

    public void addApplicationEventListener(String eventName, Closure listener) {
        eventRouter.addEventListener(eventName, listener);
    }

    public void removeApplicationEventListener(String eventName, Closure listener) {
        eventRouter.removeEventListener(eventName, listener);
    }

    public void addApplicationEventListener(String eventName, RunnableWithArgs listener) {
        eventRouter.addEventListener(eventName, listener);
    }

    public void removeApplicationEventListener(String eventName, RunnableWithArgs listener) {
        eventRouter.removeEventListener(eventName, listener);
    }

    public boolean isEventPublishingEnabled() {
        return eventRouter.isEnabled();
    }

    public void setEventPublishingEnabled(boolean enabled) {
        eventRouter.setEnabled(enabled);
    }

    public Object createApplicationContainer() {
        return null;
    }

    public void addShutdownHandler(ShutdownHandler handler) {
        if (handler != null && !shutdownHandlers.contains(handler)) shutdownHandlers.add(handler);
    }

    public void removeShutdownHandler(ShutdownHandler handler) {
        if (handler != null) shutdownHandlers.remove(handler);
    }

    public ApplicationPhase getPhase() {
        synchronized (lock) {
            return this.phase;
        }
    }

    protected void setPhase(ApplicationPhase phase) {
        synchronized (lock) {
            this.phase = phase;
        }
    }

    // -----------------------

    public boolean isUIThread() {
        return UIThreadManager.getInstance().isUIThread();
    }

    public void execInsideUIAsync(Runnable runnable) {
        UIThreadManager.getInstance().executeAsync(runnable);
    }

    public void execInsideUISync(Runnable runnable) {
        UIThreadManager.getInstance().executeSync(runnable);
    }

    public void execOutsideUI(Runnable runnable) {
        UIThreadManager.getInstance().executeOutside(runnable);
    }

    public Future execFuture(ExecutorService executorService, Closure closure) {
        return UIThreadManager.getInstance().executeFuture(executorService, closure);
    }

    public Future execFuture(Closure closure) {
        return UIThreadManager.getInstance().executeFuture(closure);
    }

    public Future execFuture(ExecutorService executorService, Callable callable) {
        return UIThreadManager.getInstance().executeFuture(executorService, callable);
    }

    public Future execFuture(Callable callable) {
        return UIThreadManager.getInstance().executeFuture(callable);
    }

    public Object newInstance(Class clazz, String type) {
        return GriffonApplicationHelper.newInstance(this, clazz, type);
    }

    public MVCGroup buildMVCGroup(String mvcType) {
        return getMvcGroupManager().buildMVCGroup(mvcType, null, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName) {
        return getMvcGroupManager().buildMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap());
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType) {
        return getMvcGroupManager().buildMVCGroup(mvcType, null, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, Map<String, Object> args) {
        return getMvcGroupManager().buildMVCGroup(mvcType, null, args);
    }

    public MVCGroup buildMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return getMvcGroupManager().buildMVCGroup(mvcType, mvcName, args);
    }

    public MVCGroup buildMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return getMvcGroupManager().buildMVCGroup(mvcType, mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType) {
        return getMvcGroupManager().createMVCGroup(mvcType, null, Collections.<String, Object>emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType) {
        return getMvcGroupManager().createMVCGroup(mvcType, null, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, Map<String, Object> args) {
        return getMvcGroupManager().createMVCGroup(mvcType, null, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName) {
        return getMvcGroupManager().createMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap());
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType, String mvcName) {
        return getMvcGroupManager().createMVCGroup(mvcType, mvcName, args);
    }

    public List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName, Map<String, Object> args) {
        return getMvcGroupManager().createMVCGroup(mvcType, mvcName, args);
    }

    public void destroyMVCGroup(String mvcName) {
        getMvcGroupManager().destroyMVCGroup(mvcName);
    }

    public void withMVCGroup(String mvcType, Closure handler) {
        getMvcGroupManager().withMVCGroup(mvcType, null, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Closure handler) {
        getMvcGroupManager().withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public void withMVCGroup(String mvcType, Map<String, Object> args, Closure handler) {
        getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, Closure handler) {
        getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, Closure handler) {
        getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, Closure handler) {
        getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, MVCClosure<M, V, C> handler) {
        getMvcGroupManager().withMVCGroup(mvcType, null, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        getMvcGroupManager().withMVCGroup(mvcType, mvcName, Collections.<String, Object>emptyMap(), handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, MVCClosure<M, V, C> handler) {
        getMvcGroupManager().withMVCGroup(mvcType, null, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler) {
        getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    public <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(Map<String, Object> args, String mvcType, String mvcName, MVCClosure<M, V, C> handler) {
        getMvcGroupManager().withMVCGroup(mvcType, mvcName, args, handler);
    }

    private Class loadClass(String className) {
        try {
            return getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            // ignored
        }
        return null;
    }

    public InputStream getResourceAsStream(String name) {
        return resourceLocator.getResourceAsStream(name);
    }

    public URL getResourceAsURL(String name) {
        return resourceLocator.getResourceAsURL(name);
    }

    public List<URL> getResources(String name) {
        return resourceLocator.getResources(name);
    }
}
