/*
 * Copyright 2008-2013 the original author or authors.
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

package griffon.swing;

import griffon.core.*;
import griffon.core.addon.AddonManager;
import griffon.core.artifact.ArtifactManager;
import griffon.core.controller.ActionManager;
import griffon.core.env.ApplicationPhase;
import griffon.core.event.EventRouter;
import griffon.core.i18n.MessageSource;
import griffon.core.injection.Injector;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.resources.ResourceHandler;
import griffon.core.resources.ResourceResolver;
import griffon.core.resources.ResourcesInjector;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import org.codehaus.griffon.runtime.core.GriffonApplicationSupport;
import org.codehaus.griffon.runtime.core.MVCGroupExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JApplet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import static griffon.util.GriffonApplicationUtils.parseLocale;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of {@code GriffonApplication} that runs in applet mode.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public abstract class AbstractGriffonApplet extends JApplet implements GriffonApplication {
    private static final String ERROR_SHUTDOWN_HANDLER_NULL = "Argument 'shutdownHandler' cannot be null";
    private Locale locale = Locale.getDefault();
    public static final String[] EMPTY_ARGS = new String[0];
    protected final Object[] lock = new Object[0];
    private ApplicationPhase phase = ApplicationPhase.INITIALIZE;

    private final List<ShutdownHandler> shutdownHandlers = new ArrayList<>();
    private final String[] startupArgs;
    private final Object shutdownLock = new Object();
    private final Logger log;
    private Injector<?> injector;

    public AbstractGriffonApplet() {
        this(EMPTY_ARGS);
    }

    public AbstractGriffonApplet(@Nonnull String[] args) {
        startupArgs = new String[args.length];
        System.arraycopy(args, 0, startupArgs, 0, args.length);
        log = LoggerFactory.getLogger(getClass());
    }

    // ------------------------------------------------------

    public void init() {
        initialize();
        startup();
    }

    public void start() {
        ready();
    }

    public void stop() {
        event(ApplicationEvent.STOP_START, asList(this));
        // TODO GriffonApplicationHelper.runLifecycleHandler(Lifecycle.STOP.getName(), this);
        event(ApplicationEvent.STOP_END, asList(this));
    }

    public void destroy() {
        shutdown();
    }

    // ------------------------------------------------------

    public void setInjector(@Nonnull Injector<?> injector) {
        this.injector = requireNonNull(injector, "Argument 'injector' cannot be bull");
        this.injector.injectMembers(this);
        addShutdownHandler(getWindowManager());
        MVCGroupExceptionHandler.registerWith(this);
    }

    @Nonnull
    public Locale getLocale() {
        return locale;
    }

    @Nonnull
    public String[] getStartupArgs() {
        return startupArgs;
    }

    @Nonnull
    public Logger getLog() {
        return log;
    }

    public void setLocaleAsString(@Nullable String locale) {
        setLocale(parseLocale(locale));
    }

    public void setLocale(@Nonnull Locale locale) {
        Locale oldValue = this.locale;
        this.locale = locale;
        Locale.setDefault(locale);
        firePropertyChange("locale", oldValue, locale);
    }

    public void addShutdownHandler(@Nonnull ShutdownHandler handler) {
        requireNonNull(handler, ERROR_SHUTDOWN_HANDLER_NULL);
        if (!shutdownHandlers.contains(handler)) shutdownHandlers.add(handler);
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
        requireNonNull(phase, "Argument 'phase' cannot be null");
        synchronized (lock) {
            firePropertyChange("phase", this.phase, this.phase = phase);
        }
    }

    @Nonnull
    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return injector.getInstance(ApplicationConfiguration.class);
    }

    @Override
    public UIThreadManager getUIThreadManager() {
        return injector.getInstance(UIThreadManager.class);
    }

    @Nonnull
    @Override
    public EventRouter getEventRouter() {
        return injector.getInstance(EventRouter.class);
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
        return injector.getInstance(MessageSource.class);
    }

    @Nonnull
    @Override
    public ResourceResolver getResourceResolver() {
        return injector.getInstance(ResourceResolver.class);
    }

    @Nonnull
    @Override
    public ResourceHandler getResourceHandler() {
        return injector.getInstance(ResourceHandler.class);
    }

    @Nonnull
    @Override
    public ResourcesInjector getResourcesInjector() {
        return injector.getInstance(ResourcesInjector.class);
    }

    @Nonnull
    @Override
    public Injector<?> getInjector() {
        return injector;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <W> WindowManager<W> getWindowManager() {
        return injector.getInstance(WindowManager.class);
    }

    /*
    @Override
    public void configure() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    */

    public void initialize() {
        if (getPhase() == ApplicationPhase.INITIALIZE) {
            GriffonApplicationSupport.init(this);
        }
    }

    public void ready() {
        if (getPhase() != ApplicationPhase.STARTUP) return;

        showStartingWindow();

        setPhase(ApplicationPhase.READY);
        event(ApplicationEvent.READY_START, asList(this));

        // TODO GriffonApplicationHelper.runLifecycleHandler(Lifecycle.READY, this);
        event(ApplicationEvent.READY_END, asList(this));
        setPhase(ApplicationPhase.MAIN);
    }

    protected void showStartingWindow() {
        Object startingWindow = getWindowManager().getStartingWindow();
        if (startingWindow != null) {
            getWindowManager().show(startingWindow);
        }
    }

    public boolean canShutdown() {
        event(ApplicationEvent.SHUTDOWN_REQUESTED, asList(this));
        synchronized (shutdownLock) {
            for (ShutdownHandler handler : shutdownHandlers) {
                if (!handler.canShutdown(this)) {
                    event(ApplicationEvent.SHUTDOWN_ABORTED, asList(this));
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
        if (getPhase() == ApplicationPhase.SHUTDOWN) return false;

        if (!canShutdown()) return false;
        log.info("Shutdown is in process");

        // signal that shutdown is in process
        setPhase(ApplicationPhase.SHUTDOWN);

        // stage 1 - alert all app event handlers
        // wait for all handlers to complete before proceeding
        // with stage #2 if and only if the current thread is
        // the ui thread
        log.debug("Shutdown stage 1: notify all event listeners");
        if (getEventRouter().isEnabled()) {
            final CountDownLatch latch = new CountDownLatch(getUIThreadManager().isUIThread() ? 1 : 0);
            getEventRouter().addEventListener(ApplicationEvent.SHUTDOWN_START.getName(), new CallableWithArgs<Void>() {
                @Override
                public Void call(@Nonnull Object[] args) {
                    latch.countDown();
                    return null;
                }
            });
            event(ApplicationEvent.SHUTDOWN_START, asList(this));
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
        List<String> mvcNames = new ArrayList<>();
        if (getMvcGroupManager() != null) {
            mvcNames.addAll(getMvcGroupManager().getGroups().keySet());
            for (String name : mvcNames) {
                getMvcGroupManager().destroyMVCGroup(name);
            }
        }

        // stage 4 - call shutdown script
        log.debug("Shutdown stage 4: execute Shutdown script");
        // TODO GriffonApplicationHelper.runLifecycleHandler(Lifecycle.SHUTDOWN, this);

        injector.getInstance(ExecutorServiceManager.class).shutdownAll();
        injector.close();

        return true;
    }

    @SuppressWarnings("unchecked")
    public void startup() {
        if (getPhase() != ApplicationPhase.INITIALIZE) return;

        setPhase(ApplicationPhase.STARTUP);
        event(ApplicationEvent.STARTUP_START, asList(this));

        Object startupGroups = getApplicationConfiguration().get("application.startupGroups", null);
        if (startupGroups instanceof List) {
            if (log.isInfoEnabled()) {
                log.info("Initializing all startup groups: " + startupGroups);
            }

            for (String groupName : (List<String>) startupGroups) {
                getMvcGroupManager().createMVCGroup(groupName);
            }
        } else if (startupGroups != null && startupGroups.getClass().isArray()) {
            Object[] groups = (Object[]) startupGroups;
            if (log.isInfoEnabled()) {
                log.info("Initializing all startup groups: " + Arrays.toString(groups));
            }

            for (Object groupName : groups) {
                getMvcGroupManager().createMVCGroup(String.valueOf(groupName));
            }
        }

        // TODO GriffonApplicationHelper.runLifecycleHandler(Lifecycle.STARTUP, this);

        event(ApplicationEvent.STARTUP_END, asList(this));
    }

    protected void event(@Nonnull ApplicationEvent event, @Nullable List<?> args) {
        getEventRouter().publish(event.getName(), args);
    }
}
