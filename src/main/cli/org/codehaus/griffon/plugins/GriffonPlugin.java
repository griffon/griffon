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

import griffon.util.BuildScope;
import griffon.util.Environment;
import groovy.lang.GroovyObject;
import org.codehaus.griffon.commons.GriffonContext;
import org.springframework.core.type.filter.TypeFilter;

import java.util.List;
import java.util.Collection;

/**
 * <p>Plugin interface that adds Spring {@link org.springframework.beans.factory.config.BeanDefinition}s
 * to a registry based on a {@link GriffonContext} object. After all <code>GriffonPlugin</code> classes
 * have been processed the {@link org.springframework.beans.factory.config.BeanDefinition}s in the registry are
 * loaded in a Spring {@link org.springframework.context.ApplicationContext} that's the singular
 * configuration unit of Griffon applications.</p>
 *
 * <p>It's up to implementation classes to determine where <code>GriffonPlugin</code> instances are loaded
 * from.</p>
 *
 * @author Steven Devijver (Grails 0.2)
 * @author Graeme Rocher (Grails 0.2)
 */
public interface GriffonPlugin extends Comparable, GriffonPluginInfo {
    /**
     * The scopes to which this plugin applies
     */
    String SCOPES = "scopes";

    /**
     * The environments to which this plugin applies
     */
    String ENVIRONMENTS = "environments";

    /**
     * The prefix used in plug-ins paths
     */
    String PLUGINS_PATH = "/plugins";

    /**
     * Defines the name of the property that specifies the plugin version
     */
    String VERSION = "version";

    /**
     * Defines the name of the property that specifies which plugins this plugin depends on
     */
    String DEPENDS_ON = "dependsOn";

    /**
     * The field that represents the list of resources to exclude from plugin packaging
     */
    String PLUGIN_EXCLUDES = "pluginExcludes";

    /**
     * The field that represents the list of resources to include in plugin packaging
     */
    String PLUGIN_INCLUDES = "pluginIncludes";

    /**
     * The field that reperesents the list of type filters a plugin provides
     */
    String TYPE_FILTERS = "typeFilters";

    /**
     * Makes the plugin excluded for a particular BuildScope
     * @param buildScope The BuildScope
     */
    void addExclude(BuildScope buildScope);

    /**
     * Makes the plugin excluded for a particular Environment
     * @param env The Environment
     */
    void addExclude(Environment env);

    /**
     * Return whether this plugin supports the given PluginScope
     *
     * @param buildScope The PluginScope
     * @return True if it does
     */
    boolean supportsScope(BuildScope buildScope);

    /**
     * Returns whether this plugin supports the given environment name
     * @param environment The environment name
     * @return True if it does
     */
    boolean supportsEnvironment(Environment environment);

    /**
     * @return True if the current plugin supports the current BuildScope and Environment
     */
    boolean supportsCurrentScopeAndEnvironment();

    /**
     *
     * @return The name of the plug-in
     */
    String getName();

    /**
     * Write some documentation to the DocumentationContext
     */
    void doc(String text);

    /**
     *
     * @return The version of the plug-in
     */
    String getVersion();

    /**
     * Returns the path of the plug-in
     *
     * @return A String that makes up the path to the plug-in in the format /plugins/PLUGIN_NAME-PLUGIN_VERSION
     */
    String getPluginPath();

    /**
     *
     * @return The names of the plugins this plugin is dependant on
     */
    String[] getDependencyNames();

    /**
     * The version of the specified dependency
     *
     * @param name the name of the dependency
     * @return The version
     */
    String getDependentVersion(String name);

    /**
     * Retrieves the plugin manager if known, otherwise returns null
     * @return The PluginManager or null
     */
    GriffonPluginManager getManager();

    /**
     * Retrieves the wrapped plugin instance for this plugin
     * @return The plugin instance
     */
    GroovyObject getInstance();

    /**
     * Sets the plugin manager for this plugin
     *
     * @param manager A GriffonPluginManager instance
     */
    void setManager(GriffonPluginManager manager);


    void setApplication(GriffonContext application);

    /**
     * Returns the name of the plugin as represented in the file system including the version. For example TagLibGriffonPlugin would result in "tag-lib-0.1"
     * @return The file system representation of the plugin name
     */
    String getFileSystemName();

    /**
     * Returns the name of the plugin as represented on the file system without the version. For example TagLibGriffonPlugin would result in "tag-lib"
     * @return The file system name
     */
    String getFileSystemShortName();

    /**
     * Returns the underlying class that represents this plugin
     * @return The plugin class
     */
    Class getPluginClass();

    /**
     * A list of resources that the plugin should exclude from the packaged distribution
     * @return a List of resources
     */
    List<String> getPluginExcludes();

    /**
     * Returns whether this plugin is loaded from the current plugin. In other words when you execute griffon run-app from a plugin project
     * the plugin project's *GriffonPlugin.groovy file represents the base plugin and this method will return true for this plugin
     *
     * @return True if it is the base plugin
     */
    boolean isBasePlugin();

    /**
     * Sets whether this plugin is the base plugin
     *
     * @param isBase True if is
     * @see #isBasePlugin()
     */
    void setBasePlugin(boolean isBase);

    /**
     * Plugin can provide a list of Spring TypeFilters so that annotated components can
     * be scanned into the ApplicationContext
     * @return A collection of TypeFilter instance
     */
    Collection<? extends TypeFilter> getTypeFilters();
}