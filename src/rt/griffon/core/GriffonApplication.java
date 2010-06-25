/*
 * Copyright 2009-2010 the original author or authors.
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

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.ConfigObject;
import griffon.util.Metadata;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Defines the basic contract of a Griffon application.<p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public interface GriffonApplication {
    /**
     * Gets the application's configuration set on 'aaplication.properties'.<p>
     */
    Metadata getMetadata();

    Class getAppConfigClass();
    Class getConfigClass();
    ConfigObject getConfig();
    void setConfig(ConfigObject config);

    Class getBuilderClass();
    ConfigObject getBuilderConfig();
    void setBuilderConfig(ConfigObject builderConfig);

    Class getEventsClass();
    Object getEventsConfig();
    void setEventsConfig(Object eventsConfig);

    Binding getBindings();
    void setBindings(Binding bindings);

    Map<String, Map<String, String>> getMvcGroups();
    void addMvcGroup(String mvcType, Map<String, String> mvcPortions);

    /**
     * Returns all currently available addon instances, keyed by addon name.<p>
     */
    Map<String, ?> getAddons();
    Map<String, String> getAddonPrefixes();

    /**
     * Returns all currently available model instances, keyed by group name.<p>
     */
    Map<String, ?> getModels();

    /**
     * Returns all currently available view instances, keyed by group name.<p>
     */
    Map<String, ?> getViews();

    /**
     * Returns all currently available controller instances, keyed by group name.<p>
     */
    Map<String, ?> getControllers();

    /**
     * Returns all currently available builder instances, keyed by group name.<p>
     */
    Map<String, ?> getBuilders();

    /**
     * Returns all currently available groups, keyed by group name.<p>
     */
    Map<String, Map<String, ?>> getGroups();

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
    void addApplicationEventListener(Object listener );

    /**
     * Adds a closure as an application event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener an application event listener
     */
    void addApplicationEventListener(String eventName, Closure listener);

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

    Locale getLocale();

    void setLocale(Locale locale);

    ApplicationPhase getPhase();
}
