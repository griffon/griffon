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
package griffon.core;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.addon.AddonManager;
import griffon.core.artifact.ArtifactManager;
import griffon.core.configuration.ConfigurationManager;
import griffon.core.controller.ActionManager;
import griffon.core.env.ApplicationPhase;
import griffon.core.event.EventRouter;
import griffon.core.i18n.MessageSource;
import griffon.core.injection.Injector;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.properties.PropertySource;
import griffon.core.resources.ResourceHandler;
import griffon.core.resources.ResourceInjector;
import griffon.core.resources.ResourceResolver;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Map;

/**
 * Defines the basic contract of a Griffon application.<p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 * @since 0.0.0
 */
public interface GriffonApplication extends PropertySource {
    String PROPERTY_LOCALE = "locale";
    String PROPERTY_PHASE = "phase";

    @Nonnull
    Object createApplicationContainer(@Nonnull Map<String, Object> attributes);

    @Nonnull
    ApplicationClassLoader getApplicationClassLoader();

    /**
     * Retrieves the {@code Configuration} of this application.
     *
     * @return the {@code Configuration} used by this application. Never returns {@code null}.
     */
    @Nonnull
    Configuration getConfiguration();

    @Nonnull
    UIThreadManager getUIThreadManager();

    @Nonnull
    EventRouter getEventRouter();

    @Nonnull
    ConfigurationManager getConfigurationManager();

    @Nonnull
    ArtifactManager getArtifactManager();

    @Nonnull
    ActionManager getActionManager();

    @Nonnull
    AddonManager getAddonManager();

    @Nonnull
    MVCGroupManager getMvcGroupManager();

    @Nonnull
    MessageSource getMessageSource();

    @Nonnull
    ResourceResolver getResourceResolver();

    @Nonnull
    ResourceHandler getResourceHandler();

    @Nonnull
    ResourceInjector getResourceInjector();

    @Nonnull
    Injector<?> getInjector();

    @Nonnull
    Context getContext();

    @Nonnull
    <W> WindowManager<W> getWindowManager();

    // --== Lifecycle ==--

    /**
     * Lifecycle method. Signals the application to bootstrap itself and load its configuration.
     * {@code ApplicationPhase} should be set automatically to {@code ApplicationPhase.INITIALIZE}.
     */
    void initialize();

    /**
     * Lifecycle method. Signals the application to assemble its components/artifacts.
     * {@code ApplicationPhase} should be set automatically to {@code ApplicationPhase.STARTUP}.
     */
    void startup();

    /**
     * Lifecycle method. Signals the application to display its main entry point (Window).
     * {@code ApplicationPhase} should be set automatically to {@code ApplicationPhase.READY}, followed
     * immediately with {@code ApplicationPhase.MAIN} once the ready sequence has finished.
     */
    void ready();

    /**
     * Lifecycle method. Shutdowns the application gracefully.
     * {@code ApplicationPhase} should be set automatically to {@code ApplicationPhase.SHUTDOWN}.
     *
     * @return the exit code that may be sent to the underlying platform process as exit value. Never returns {@code null}.
     */
    boolean shutdown();

    /**
     * Queries any available {@code ShutdownHandler}s do determine if the application can be shutdown.
     *
     * @return {@code true} if the shutdown sequence can proceed, (@code false} otherwise
     */
    boolean canShutdown();

    /**
     * Registers a {@code ShutdownHandler} on this application
     *
     * @param handler the shutdown handler to be registered. Must not be {@code null}.
     *                Duplicate values must be ignored.
     */
    void addShutdownHandler(@Nonnull ShutdownHandler handler);

    /**
     * Removes a {@code ShutdownHandler} from this application
     *
     * @param handler the shutdown handler to be removed. Must not be {@code null}.
     *                Duplicate values must be ignored.
     */
    void removeShutdownHandler(@Nonnull ShutdownHandler handler);

    // --== Properties ==--

    /**
     * Gets the application locale.
     *
     * @return the current Locale used by the application. Never returns {@code null}.
     */
    @Nonnull
    Locale getLocale();

    /**
     * Sets the application locale.<p>
     * This is a bound property.
     *
     * @param locale the Locale value to use
     */
    void setLocale(@Nonnull Locale locale);

    /**
     * Sets the application locale.<p>
     * This is a bound property.
     *
     * @param locale a literal representation of a Locale
     */
    void setLocaleAsString(@Nullable String locale);

    /**
     * Returns the current phase.
     *
     * @return returns the current {@code ApplicationPhase}. Never returns {@code null}.
     */
    @Nonnull
    ApplicationPhase getPhase();

    /**
     * Returns the arguments set on the command line (if any).<p>
     *
     * @return an array of command line arguments. Never returns {@code null}.
     */
    @Nonnull
    String[] getStartupArguments();

    /**
     * Returns a Logger instance suitable for this application.
     *
     * @return a Logger instance.
     */
    @Nonnull
    Logger getLog();
}
