/*
 * Copyright 2009-2011 the original author or authors.
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
package griffon.core;

import griffon.util.GriffonNameUtils;
import griffon.util.Metadata;
import griffon.util.RunnableWithArgs;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.ConfigObject;
import groovy.util.FactoryBuilderSupport;
import org.slf4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines the basic contract of a Griffon application.<p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public interface GriffonApplication extends ThreadingHandler, MVCHandler {
    /**
     * Defines the names of the configuration scripts.
     *
     * @author Andres Almiray
     * @since 0.9.2
     */
    public enum Configuration {
        APPLICATION, CONFIG, BUILDER, EVENTS;

        /** Display friendly name */
        private String name;

        /**
         * Returns the capitalized String representation of this Configuration object.
         *
         * @return a capitalized String
         */
        public String getName() {
            if(name == null) {
                return GriffonNameUtils.capitalize(this.toString().toLowerCase(Locale.getDefault()));
            }
            return name;
        }
    }

    /**
     * Defines the names of the lifecycle scripts.
     *
     * @author Andres Almiray
     * @since 0.9.2
     */
    public enum Lifecycle {
        INITIALIZE, STARTUP, READY, SHUTDOWN, STOP;

        /** Display friendly name */
        private String name;

        /**
         * Returns the capitalized String representation of this Lifecycle object.
         *
         * @return a capitalized String
         */
        public String getName() {
            if(name == null) {
                return GriffonNameUtils.capitalize(this.toString().toLowerCase(Locale.getDefault()));
            }
            return name;
        }
    }

    /**
     * Defines all the events triggered by the application.
     *
     * @author Andres Almiray
     * @since 0.9.2
     */
    public enum Event {
        LOG4J_CONFIG_START("Log4jConfigStart"), UNCAUGHT_EXCEPTION_THROWN,
        LOAD_ADDONS_START, LOAD_ADDONS_END, LOAD_ADDON_START, LOAD_ADDON_END,
        BOOTSTRAP_START, BOOTSTRAP_END,
        STARTUP_START, STARTUP_END,
        READY_START, READY_END,
        SHUTDOWN_REQUESTED, SHUTDOWN_ABORTED, SHUTDOWN_START,
        NEW_INSTANCE,
        CREATE_MVC_GROUP("CreateMVCGroup"), DESTROY_MVC_GROUP("DestroyMVCGroup"),
        WINDOW_SHOWN, WINDOW_HIDDEN;

        /** Display friendly name */
        private String name;

        Event() {
            String name = name().toLowerCase().replaceAll("_","-");
            this.name = GriffonNameUtils.getClassNameForLowerCaseHyphenSeparatedName(name);
        }

        Event(String name) {
            this.name = name;
        }

        /**
         * Returns the capitalized String representation of this Event object.
         *
         * @return a capitalized String
         */
        public String getName() {
            return this.name;
        }
    }

    /**
     * Gets the application's configuration set on 'application.properties'.<p>
     */
    Metadata getMetadata();

    /**
     * Gets the script class that holds the MVC configuration (i.e. {@code Application.groovy})
     */
    Class getAppConfigClass();
    /**
     * Gets the script class that holds additional configuration (i.e. {@code Config.groovy})
     */
    Class getConfigClass();
    /**
     * Returns the merged runtime configuration from {@code appConfig} and {@code config}
     */
    ConfigObject getConfig();
    void setConfig(ConfigObject config);

    /**
     * Gets the script class that holds builder configuration (i.e. {@code Builder.groovy})
     */
    Class getBuilderClass();
    /**
     * Returns the runtime configuration required for instantiating a {@CompositeBuilder}
     */
    ConfigObject getBuilderConfig();
    void setBuilderConfig(ConfigObject builderConfig);

    /**
     * Gets the script class that holds global event handler configuration (i.e. {@code Events.groovy})
     */
    Class getEventsClass();
    /**
     * Returns the runtime configuration for global event handlers.
     */
    Object getEventsConfig();
    void setEventsConfig(Object eventsConfig);

    Binding getBindings();
    void setBindings(Binding bindings);

    /**
     * Return's the set of available MVC groups.
     */
    Map<String, Map<String, String>> getMvcGroups();

    /**
     * Register an MVC group for instantiation.<p>
     */
    void addMvcGroup(String mvcType, Map<String, String> mvcPortions);

    /**
     * Returns all currently available addon instances, keyed by addon name.<p>
     * @deprecated use getAddonManager().getAddons()
     */
    @Deprecated
    Map<String, ?> getAddons();

    /**
     * @deprecated without replacement. Use the AddonManager to query for addons
     */
    @Deprecated
    Map<String, String> getAddonPrefixes();

    /**
     * Returns the application's AddonManager instance.
     *
     * @return the application's AddonManager
     */
    AddonManager getAddonManager();

    /**
     * Returns all currently available model instances, keyed by group name.<p>
     */
    Map<String, ? extends GriffonModel> getModels();

    /**
     * Returns all currently available view instances, keyed by group name.<p>
     */
    Map<String, ? extends GriffonView> getViews();

    /**
     * Returns all currently available controller instances, keyed by group name.<p>
     */
    Map<String, ? extends GriffonController> getControllers();

    /**
     * Returns all currently available builder instances, keyed by group name.<p>
     */
    Map<String, ? extends FactoryBuilderSupport> getBuilders();

    /**
     * Returns all currently available groups, keyed by group name.<p>
     */
    Map<String, Map<String, Object>> getGroups();

    Object createApplicationContainer();

    /**
     * Executes the 'Initilaize' life cycle phase.
     */
    void initialize();

    /**
     * Executes the 'Startup' life cycle phase.
     */
    void startup();

    /**
     * Executes the 'Ready' life cycle phase.
     */
    void ready();

    /**
     * Executes the 'Shutdown' life cycle phase.
     *
     * @return false if the shutdown sequence was aborted
     */
    boolean shutdown();

    /**
     * Queries any available ShutdownHandlers.
     *
     * @return true if the shutdown sequence can proceed, false otherwise
     */
    boolean canShutdown();

    /**
     * Adds an application event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an application event listener
     */
    void addApplicationEventListener(Object listener);

    /**
     * Adds a closure as an application event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener an application event listener
     */
    void addApplicationEventListener(String eventName, Closure listener);

    /**
     * Adds a runnable as an application event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener an application event listener
     */
    void addApplicationEventListener(String eventName, RunnableWithArgs listener);

    /**
     * Removes an application event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an application event listener
     */
    void removeApplicationEventListener(Object listener );

    /**
     * Removes a closure as an application event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener an application event listener
     */
    void removeApplicationEventListener(String eventName, Closure listener);

    /**
     * Removes a runnable as an application event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener an application event listener
     */
    void removeApplicationEventListener(String eventName, RunnableWithArgs listener);

    /**
     * Publishes an application event.<p>
     *
     * @param eventName the name of the event
     */
    void event(String eventName);

    /**
     * Publishes an application event.<p>
     *
     * @param eventName the name of the event
     * @param params event arguments sent to listeners
     */
    void event(String eventName, List params);

    /**
     * Publishes an application event asynchronously off the UI thread.<p>
     *
     * @param eventName the name of the event
     */
    void eventOutside(String eventName);

    /**
     * Publishes an application event asynchronously off the UI thread.<p>
     *
     * @param eventName the name of the event
     * @param params event arguments sent to listeners
     */
    void eventOutside(String eventName, List params);

    /**
     * Publishes an application event asynchronously off the publisher's thread.<p>
     *
     * @param eventName the name of the event
     */
    void eventAsync(String eventName);

    /**
     * Publishes an application event asynchronously off the publisher's thread.<p>
     *
     * @param eventName the name of the event
     * @param params event arguments sent to listeners
     */
    void eventAsync(String eventName, List params);

    /**
     * Registers a ShutdownHandler on this application
     *
     * @param handler the shutdown handler to be registered; null and/or
     *                duplicated values should be ignored
     */
    void addShutdownHandler(ShutdownHandler handler);    

    /**
     * Removes a ShutdownHandler from this application
     *
     * @param handler the shutdown handler to be removed; null and/or
     *                duplicated values should be ignored
     */
    void removeShutdownHandler(ShutdownHandler handler);    

    /**
     * Gets the application locale.
     */    
    Locale getLocale();

    /**
     * Sets the application locale.<p>
     * This is a bound property.
     */
    void setLocale(Locale locale);

    /**
     * Returns the current phase.
     */
    ApplicationPhase getPhase();

    // ----------------------------

    /**
     * Returns the application's ArtifactManager instance.
     *
     * @return the application's ArtifactManager
     */
    ArtifactManager getArtifactManager();

    /**
     * Creates a new instance of the specified class and type.
     */
    Object newInstance(Class clazz, String type);

    /**
     * Returns the arguments set on the command line (if any).<p>
     *
     * @since 0.9.2
     * @return an array of command line arguments. Never returns null.
     */
    String[] getStartupArgs();

    /**
     * Returns a Logger instance suitable for this application.
     *
     * @since 0.9.2
     * @return a Logger instance.
     */
    Logger getLog();
}
