/*
 * Copyright 2004-2010 the original author or authors.
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
package org.codehaus.griffon.plugins;

import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.filter.TypeFilter;

import java.io.File;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public interface GriffonPluginManager {
    /**
     * Returns an array of all the loaded plug-ins
     * @return An array of plug-ins
     */
    GriffonPlugin[] getAllPlugins();

    /**
     * @return An array of plugins that failed to load due to dependency resolution errors
     */
    GriffonPlugin[] getFailedLoadPlugins();

    /**
     * Performs the initial load of plug-ins throwing an exception if any dependencies
     * don't resolve
     *
     * @throws PluginException Thrown when an error occurs loading the plugins
     */
    void loadPlugins() throws PluginException;

    /**
     * Retrieves a name Griffon plugin instance
     *
     * @param name The name of the plugin
     * @return The GriffonPlugin instance or null if it doesn't exist
     */
    GriffonPlugin getGriffonPlugin(String name);

    /**
     * Obtains a GriffonPlugin for the given classname
     * @param name The name of the plugin
     * @return The instance
     */
    GriffonPlugin getGriffonPluginForClassName(String name);

    /**
     *
     * @param name The name of the plugin
     * @return True if the the manager has a loaded plugin with the given name
     */
    boolean hasGriffonPlugin(String name);

    /**
     * Retrieves a plug-in that failed to load, or null if it doesn't exist
     *
     * @param name The name of the plugin
     * @return A GriffonPlugin or null
     */
    GriffonPlugin getFailedPlugin(String name);

    /**
     * Retrieves a plug-in for its name and version
     * 
     * @param name The name of the plugin
     * @param version The version of the plugin
     * @return The GriffonPlugin instance or null if it doesn't exist
     */
    GriffonPlugin getGriffonPlugin(String name, Object version);

    /**
     * Sets the GriffonContext used be this plugin manager
     * @param application The GriffonContext instance
     */
    void setApplication(GriffonContext application);

    /**
     * @return the initialised
     */
    boolean isInitialised();

    /**
     * Returns true if the given plugin supports the current BuildScope
     * @param pluginName The name of the plugin
     *
     * @return True if the plugin supports the current build scope
     * @see griffon.util.BuildScope#getCurrent()
     */
    boolean supportsCurrentBuildScope(String pluginName);

    /**
     * Get all of the TypeFilter definitions defined by the plugins
     * @return A list of TypeFilter definitions
     */
    List<TypeFilter> getTypeFilters();
    
    /**
     * Returns the pluginContextPath for the given plugin
     * 
     * @param name The plugin name
     * @return the context path
     */
    String getPluginPath(String name);

    /**
     * Returns the pluginContextPath for the given instance
     * @param instance The instance
     * @return The pluginContextPath
     */
    String getPluginPathForInstance(Object instance);

    /**
     * Returns the plugin path for the given class
     * @param theClass The class
     * @return The pluginContextPath
     */
    String getPluginPathForClass(Class<? extends Object> theClass);
}
