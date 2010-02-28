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
import java.util.Properties;

/**
 * Defines the basic contract of a Griffon application.<p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public interface GriffonApplication extends griffon.util.IGriffonApplication {

    /**
     * Gets the application's configuration set on 'aaplication.properties'.<p>
     *
     * @deprecated use Metadata.getCurrent() instead
     */
    @Deprecated
    public Properties getApplicationProperties();

    /**
     * Gets the application's configuration set on 'aaplication.properties'.<p>
     */
    public Metadata getMetadata();

    public Class getConfigClass();
    public ConfigObject getConfig();
    public void setConfig(ConfigObject config);

    public Class getBuilderClass();
    public ConfigObject getBuilderConfig();
    public void setBuilderConfig(ConfigObject builderConfig);

    public Class getEventsClass();
    public Object getEventsConfig();
    public void setEventsConfig(Object eventsConfig);

    public Binding getBindings();
    public void setBindings(Binding bindings);

    public Map<String, Map<String, String>> getMvcGroups();
    public void addMvcGroup(String mvcType, Map<String, String> mvcPortions);

    /**
     * Returns all currently available addon instances, keyed by addon name.<p>
     */
    public Map<String, ?> getAddons();
    public Map<String, String> getAddonPrefixes();

    /**
     * Returns all currently available model instances, keyed by group name.<p>
     */
    public Map<String, ?> getModels();

    /**
     * Returns all currently available view instances, keyed by group name.<p>
     */
    public Map<String, ?> getViews();

    /**
     * Returns all currently available controller instances, keyed by group name.<p>
     */
    public Map<String, ?> getControllers();

    /**
     * Returns all currently available builder instances, keyed by group name.<p>
     */
    public Map<String, ?> getBuilders();

    /**
     * Returns all currently available groups, keyed by group name.<p>
     */
    public Map<String, Map<String, ?>> getGroups();

    public Object createApplicationContainer();

    /**
     * Executes the 'Initilaize' life cycle phase.
     */
    public void initialize();

    /**
     * Executes the 'Startup' life cycle phase.
     */
    public void startup();

    /**
     * Executes the 'Ready' life cycle phase.
     */
    public void ready();

    /**
     * Executes the 'Shutdown' life cycle phase.
     */
    public void shutdown();

    /**
     * Adds an application event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an application event listener
     */
    public void addApplicationEventListener(Object listener );

    /**
     * Adds a closure as an application event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener an application event listener
     */
    public void addApplicationEventListener(String eventName, Closure listener);

    /**
     * Removes an application event listener.<p>
     * Accepted types are: Script, Map and Object.
     *
     * @param listener an application event listener
     */
    public void removeApplicationEventListener(Object listener );

    /**
     * Removes a closure as an application event listener.<p>
     *
     * @param eventName the name of the event
     * @param listener an application event listener
     */
    public void removeApplicationEventListener(String eventName, Closure listener);

    /**
     * Publishes an applicaiton event.<p>
     *
     * @param eventName the name of the event
     */
    public void event(String eventName);

    /**
     * Publishes an applicaiton event.<p>
     *
     * @param eventName the name of the event
     * @param params event arguments sent to listeners
     */
    public void event(String eventName, List params);
}
