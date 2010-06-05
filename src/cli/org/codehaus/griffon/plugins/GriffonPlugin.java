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

import groovy.lang.GroovyObject;
import groovy.util.slurpersupport.GPathResult;
import org.codehaus.griffon.commons.GriffonContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Map;

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
 * @author Steven Devijver
 * @author Graeme Rocher
 *
 * @since 0.2
 * @see BeanDefinitionRegistry
 */
public interface GriffonPlugin {

    /**
     * The prefix used in plug-ins paths
     */
    String PLUGINS_PATH = "/plugins";

    /**
     * The status of the plugin
     */
    String STATUS = "status";

    /**
     * When a plugin is "enabled" it will be loaded as usual
     */
    String STATUS_ENABLED = "enabled";
    /**
     * When a plugin is "disabled" it will not be loaded
     */
    String STATUS_DISABLED = "disabled";
    /**
     * Defines the convention that appears within plugin class names
     */
    String TRAILING_NAME = "GriffonPlugin";
    /**
     * Defines the name of the property that specifies the plugin version
     */
    String VERSION = "version";
    /**
     * Defines the name of the property that specifies which plugins this plugin depends on
     */
    String DEPENDS_ON = "dependsOn";

    /**
     *
     * @return The name of the plug-in
     */
    String getName();

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
     * @return Whether the plugin is enabled or not
     */
    boolean isEnabled();
}
