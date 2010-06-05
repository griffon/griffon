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

import java.io.File;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

/**
 * <p>A class that handles the loading and management of plug-ins in the Griffon system.
 * A plugin a just like a normal Griffon application except that it contains a file ending
 * in *Plugin.groovy  in the root of the directory.
 *
 * <p>A Plugin class is a Groovy class that has a version and optionally closures
 * called doWithSpring, doWithContext and doWithWebDescriptor
 *
 * <p>The doWithSpring closure uses the BeanBuilder syntax (@see griffon.spring.BeanBuilder) to
 * provide runtime configuration of Griffon via Spring
 *
 * <p>The doWithContext closure is called after the Spring ApplicationContext is built and accepts
 * a single argument (the ApplicationContext)
 *
 * <p>The doWithWebDescriptor uses mark-up building to provide additional functionality to the web.xml
 * file
 *
 *<p> Example:
 * <pre>
 * class ClassEditorGriffonPlugin {
 *      def version = 1.1
 *      def doWithSpring = { application ->
 *          classEditor(org.springframework.beans.propertyeditors.ClassEditor, application.classLoader)
 *      }
 * }
 * </pre>
 *
 * <p>A plugin can also define "dependsOn" and "evict" properties that specify what plugins the plugin
 * depends on and which ones it is incompatable with and should evict
 *
 * @author Graeme Rocher
 * @since 0.4
 *
 */
public interface GriffonPluginManager {

    String BEAN_NAME = "pluginManager";

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
    public abstract void loadPlugins() throws PluginException;

    /**
     * Retrieves a name Griffon plugin instance
     *
     * @param name The name of the plugin
     * @return The GriffonPlugin instance or null if it doesn't exist
     */
    public abstract GriffonPlugin getGriffonPlugin(String name);

    /**
     *
     * @param name The name of the plugin
     * @return True if the the manager has a loaded plugin with the given name
     */
    public boolean hasGriffonPlugin(String name);

    /**
     * Retrieves a plug-in that failed to load, or null if it doesn't exist
     *
     * @param name The name of the plugin
     * @return A GriffonPlugin or null
     */
    public GriffonPlugin getFailedPlugin(String name);

    /**
     * Retrieves a plug-in for its name and version
     *
     * @param name The name of the plugin
     * @param version The version of the plugin
     * @return The GriffonPlugin instance or null if it doesn't exist
     */
    public abstract GriffonPlugin getGriffonPlugin(String name, Object version);

    /**
     * Sets the GriffonContext used be this plugin manager
     * @param application The GriffonContext instance
     */
    public abstract void setApplication(GriffonContext application);

    /**
     * @return the initialised
     */
    public boolean isInitialised();

    /**
     * Shuts down the PluginManager
     */
    void shutdown();
}
