/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.javafx;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.annotations.event.EventHandler;
import griffon.core.ApplicationClassLoader;
import griffon.core.ApplicationConfigurer;
import griffon.core.Configuration;
import griffon.core.Context;
import griffon.core.ExecutorServiceManager;
import griffon.core.GriffonApplication;
import griffon.core.ShutdownHandler;
import griffon.core.addon.AddonManager;
import griffon.core.addon.GriffonAddon;
import griffon.core.artifact.ArtifactManager;
import griffon.core.configuration.ConfigurationManager;
import griffon.core.controller.ActionManager;
import griffon.core.env.ApplicationPhase;
import griffon.core.env.Lifecycle;
import griffon.core.event.Event;
import griffon.core.event.EventRouter;
import griffon.core.events.ReadyEndEvent;
import griffon.core.events.ReadyStartEvent;
import griffon.core.events.ShutdownAbortedEvent;
import griffon.core.events.ShutdownRequestedEvent;
import griffon.core.events.ShutdownStartEvent;
import griffon.core.events.StartupEndEvent;
import griffon.core.events.StartupStartEvent;
import griffon.core.i18n.MessageSource;
import griffon.core.injection.Injector;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.properties.PropertyChangeEvent;
import griffon.core.properties.PropertyChangeListener;
import griffon.core.resources.ResourceHandler;
import griffon.core.resources.ResourceInjector;
import griffon.core.resources.ResourceResolver;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.core.MVCGroupExceptionHandler;
import org.codehaus.griffon.runtime.core.properties.PropertyChangeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static griffon.core.util.GriffonApplicationUtils.parseLocale;
import static griffon.util.AnnotationUtils.named;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of {@code GriffonApplication} that runs in applet mode.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractJavaFXGriffonApplication extends Application implements GriffonApplication {
    public static final String[] EMPTY_ARGS = new String[0];
    private static final String ERROR_SHUTDOWN_HANDLER_NULL = "Argument 'shutdownHandler' must not be null";
    protected final Object[] lock = new Object[0];
    protected final PropertyChangeSupport pcs;
    protected final List<ShutdownHandler> shutdownHandlers = new ArrayList<>();
    protected final Object shutdownLock = new Object();
    protected final Logger log;
    protected Locale locale = Locale.getDefault();
    protected ApplicationPhase phase = ApplicationPhase.INITIALIZE;
    protected String[] startupArguments;
    protected Injector<?> injector;

    public AbstractJavaFXGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public AbstractJavaFXGriffonApplication(@Nonnull String[] args) {
        requireNonNull(args, "Argument 'args' must not be null");
        pcs = new PropertyChangeSupport(this);
        startupArguments = Arrays.copyOf(args, args.length);
        log = LoggerFactory.getLogger(getClass());
    }

    // ------------------------------------------------------

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnHidden(t -> shutdown());
    }

    @Override
    public void stop() throws Exception {
        shutdown();
    }

    public void addPropertyChangeListener(@Nullable PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(@Nullable PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(@Nullable String propertyName, @Nullable PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    @Nonnull
    public PropertyChangeListener[] getPropertyChangeListeners(@Nullable String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(@Nonnull PropertyChangeEvent event) {
        pcs.firePropertyChange(requireNonNull(event, "Argument 'event' must not be null"));
    }

    protected void firePropertyChange(@Nonnull String propertyName, @Nullable Object oldValue, @Nullable Object newValue) {
        pcs.firePropertyChange(requireNonBlank(propertyName, "Argument 'propertyName' must not be blank"), oldValue, newValue);
    }

    // ------------------------------------------------------

    @Nonnull
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(@Nonnull Locale locale) {
        Locale oldValue = this.locale;
        this.locale = locale;
        Locale.setDefault(locale);
        firePropertyChange(PROPERTY_LOCALE, oldValue, locale);
    }

    @Nonnull
    public String[] getStartupArguments() {
        return startupArguments;
    }

    @Nonnull
    public Logger getLog() {
        return log;
    }

    public void setLocaleAsString(@Nullable String locale) {
        setLocale(parseLocale(locale));
    }

    public void addShutdownHandler(@Nonnull ShutdownHandler handler) {
        requireNonNull(handler, ERROR_SHUTDOWN_HANDLER_NULL);
        if (!shutdownHandlers.contains(handler)) { shutdownHandlers.add(handler); }
    }

    public void removeShutdownHandler(@Nonnull ShutdownHandler handler) {
        requireNonNull(handler, ERROR_SHUTDOWN_HANDLER_NULL);
        shutdownHandlers.remove(handler);
    }

    @Nonnull
    public ApplicationPhase getPhase() {
        synchronized (lock) {
            return this.phase;
        }
    }

    protected void setPhase(@Nonnull ApplicationPhase phase) {
        requireNonNull(phase, "Argument 'phase' must not be null");
        synchronized (lock) {
            firePropertyChange(PROPERTY_PHASE, this.phase, this.phase = phase);
        }
    }

    @Nonnull
    @Override
    public ApplicationClassLoader getApplicationClassLoader() {
        return injector.getInstance(ApplicationClassLoader.class);
    }

    @Nonnull
    @Override
    public Context getContext() {
        return injector.getInstance(Context.class, named("applicationContext"));
    }

    @Nonnull
    @Override
    public Configuration getConfiguration() {
        return getConfigurationManager().getConfiguration();
    }

    @Nonnull
    @Override
    public ConfigurationManager getConfigurationManager() {
        return injector.getInstance(ConfigurationManager.class);
    }

    @Nonnull
    @Override
    public UIThreadManager getUIThreadManager() {
        return injector.getInstance(UIThreadManager.class);
    }

    @Nonnull
    @Override
    public EventRouter getEventRouter() {
        return injector.getInstance(EventRouter.class, named("applicationEventRouter"));
    }

    @Nonnull
    @Override
    public ArtifactManager getArtifactManager() {
        return injector.getInstance(ArtifactManager.class);
    }

    @Nonnull
    @Override
    public ActionManager getActionManager() {
        return injector.getInstance(ActionManager.class);
    }

    @Nonnull
    @Override
    public AddonManager getAddonManager() {
        return injector.getInstance(AddonManager.class);
    }

    @Nonnull
    @Override
    public MVCGroupManager getMvcGroupManager() {
        return injector.getInstance(MVCGroupManager.class);
    }

    @Nonnull
    @Override
    public MessageSource getMessageSource() {
        return injector.getInstance(MessageSource.class, named("applicationMessageSource"));
    }

    @Nonnull
    @Override
    public ResourceResolver getResourceResolver() {
        return injector.getInstance(ResourceResolver.class, named("applicationResourceResolver"));
    }

    @Nonnull
    @Override
    public ResourceHandler getResourceHandler() {
        return injector.getInstance(ResourceHandler.class);
    }

    @Nonnull
    @Override
    public ResourceInjector getResourceInjector() {
        return injector.getInstance(ResourceInjector.class, named("applicationResourceInjector"));
    }

    @Nonnull
    @Override
    public Injector<?> getInjector() {
        return injector;
    }

    public void setInjector(@Nonnull Injector<?> injector) {
        this.injector = requireNonNull(injector, "Argument 'injector' must not be null");
        this.injector.injectMembers(this);
        addShutdownHandler(getWindowManager());
        MVCGroupExceptionHandler.registerWith(this);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <W> WindowManager<W> getWindowManager() {
        return injector.getInstance(WindowManager.class);
    }

    protected ApplicationConfigurer getApplicationConfigurer() {
        return injector.getInstance(ApplicationConfigurer.class);
    }

    public void initialize() {
        if (getPhase() == ApplicationPhase.INITIALIZE) {
            Parameters parameters = getParameters();
            if (parameters != null && !parameters.getRaw().isEmpty()) {
                int length = parameters.getRaw().size();
                startupArguments = new String[length];
                System.arraycopy(parameters.getRaw().toArray(), 0, startupArguments, 0, length);
            }

            getApplicationConfigurer().init();
        }
    }

    public void ready() {
        if (getPhase() != ApplicationPhase.STARTUP) { return; }

        showStartingWindow();

        setPhase(ApplicationPhase.READY);
        event(ReadyStartEvent.of(this));

        getApplicationConfigurer().runLifecycleHandler(Lifecycle.READY);
        event(ReadyEndEvent.of(this));

        setPhase(ApplicationPhase.MAIN);
    }

    protected void showStartingWindow() {
        Object startingWindow = getWindowManager().getStartingWindow();
        if (startingWindow != null) {
            getWindowManager().show(startingWindow);
        }
    }

    public boolean canShutdown() {
        event(ShutdownRequestedEvent.of(this));
        synchronized (shutdownLock) {
            for (ShutdownHandler handler : shutdownHandlers) {
                if (!handler.canShutdown(this)) {
                    event(ShutdownAbortedEvent.of(this));
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
        boolean implicitExit = Platform.isImplicitExit();
        Platform.setImplicitExit(false);
        if (!doShutdown()) {
            Platform.setImplicitExit(implicitExit);
            return false;
        }
        return true;
    }

    private AtomicReference<CountDownLatch> latch = new AtomicReference<>();

    @EventHandler
    public void onShutdownStartEvent(ShutdownStartEvent event) {
        latch.get().countDown();
    }

    protected boolean doShutdown() {
        // avoids reentrant calls to shutdown()
        // once permission to quit has been granted
        if (getPhase() == ApplicationPhase.SHUTDOWN) { return false; }

        if (!canShutdown()) { return false; }
        log.info("Shutdown is in process");

        // signal that shutdown is in process
        setPhase(ApplicationPhase.SHUTDOWN);

        // stage 1 - alert all app event handlers
        // wait for all handlers to complete before proceeding
        // with stage #2 if and only if the current thread is
        // the ui thread
        log.debug("Shutdown stage 1: notify all event listeners");
        if (getEventRouter().isEventPublishingEnabled()) {
            getEventRouter().subscribe(this);
            latch.set(new CountDownLatch(getUIThreadManager().isUIThread() ? 1 : 0));
            event(ShutdownStartEvent.of(this));
            try {
                latch.get().await();
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
        List<String> mvcIds = new ArrayList<>(getMvcGroupManager().getGroups().keySet());
        for (String id : mvcIds) {
            getMvcGroupManager().destroyMVCGroup(id);
        }

        // stage 4 - call shutdown script
        log.debug("Shutdown stage 4: execute Shutdown script");
        getApplicationConfigurer().runLifecycleHandler(Lifecycle.SHUTDOWN);

        injector.getInstance(ExecutorServiceManager.class).shutdownAll();
        injector.close();

        return true;
    }

    @SuppressWarnings("unchecked")
    public void startup() {
        if (getPhase() != ApplicationPhase.INITIALIZE) { return; }

        setPhase(ApplicationPhase.STARTUP);
        event(StartupStartEvent.of(this));

        Object startupGroups = getConfiguration().get("application.startupGroups", null);
        if (startupGroups instanceof List) {
            log.info("Initializing all startup groups: {}", startupGroups);

            for (String groupName : (List<String>) startupGroups) {
                getMvcGroupManager().createMVC(groupName.trim());
            }
        } else if (startupGroups != null && startupGroups.getClass().isArray()) {
            Object[] groups = (Object[]) startupGroups;
            log.info("Initializing all startup groups: {}", Arrays.toString(groups));

            for (Object groupName : groups) {
                getMvcGroupManager().createMVC(String.valueOf(groupName).trim());
            }
        } else if (startupGroups != null && startupGroups instanceof CharSequence) {
            String[] groups = (String.valueOf(startupGroups)).split(",");
            log.info("Initializing all startup groups: {}", Arrays.toString(groups));

            for (String groupName : groups) {
                getMvcGroupManager().createMVC(groupName.trim());
            }
        }

        for (Map.Entry<String, GriffonAddon> e : getAddonManager().getAddons().entrySet()) {
            List<String> groups = e.getValue().getStartupGroups();
            if (groups.isEmpty()) {
                continue;
            }
            log.info("Initializing all {} startup groups: {}", e.getKey(), groups);
            Map<String, Map<String, Object>> mvcGroups = e.getValue().getMvcGroups();
            for (String groupName : groups) {
                if (mvcGroups.containsKey(groupName)) {
                    getMvcGroupManager().createMVC(groupName.trim());
                }
            }
        }

        getApplicationConfigurer().runLifecycleHandler(Lifecycle.STARTUP);

        event(StartupEndEvent.of(this));
    }

    protected <E extends Event> void event(@Nonnull E event) {
        getEventRouter().publishEvent(event);
    }
}
