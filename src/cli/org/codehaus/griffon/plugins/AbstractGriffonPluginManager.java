/*
 * Copyright 2008-2010 the original author or authors.
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

import groovy.lang.ExpandoMetaClass;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.commons.GriffonClassUtils;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.springframework.beans.BeansException;
import org.springframework.core.io.Resource;

import java.util.*;

/**
 * Abstract implementation of the GriffonPluginManager interface
 *
 * @author Graeme Rocher
 * @since 0.4
 *
 */
public abstract class AbstractGriffonPluginManager implements GriffonPluginManager {

    protected List pluginList = new ArrayList();
    protected GriffonContext application;
    protected Resource[] pluginResources = new Resource[0];
    protected Map plugins = new HashMap();
    protected Class[] pluginClasses = new Class[0];
    protected boolean initialised = false;
    protected Map failedPlugins = new HashMap();


    public AbstractGriffonPluginManager(GriffonContext application) {
        super();
        if(application == null)
            throw new IllegalArgumentException("Argument [application] cannot be null!");

        this.application = application;
    }

    public GriffonPlugin[] getAllPlugins() {
        return (GriffonPlugin[])pluginList.toArray(new GriffonPlugin[pluginList.size()]);
    }

    public GriffonPlugin[] getFailedLoadPlugins() {
        return (GriffonPlugin[])failedPlugins.values().toArray(new GriffonPlugin[failedPlugins.size()]);
    }


    /**
     * @return the initialised
     */
    public boolean isInitialised() {
        return initialised;
    }
    protected void checkInitialised() {
        if(!initialised)
            throw new IllegalStateException("Must call loadPlugins() before invoking configurational methods on GriffonPluginManager");
    }

    public GriffonPlugin getFailedPlugin(String name) {
        if(name.indexOf('-') > -1) name = GriffonClassUtils.getPropertyNameForLowerCaseHyphenSeparatedName(name);
        return (GriffonPlugin)this.failedPlugins.get(name);
    }

    public Resource[] getPluginResources() {
        return this.pluginResources;
    }
    public GriffonPlugin getGriffonPlugin(String name) {
        if(name.indexOf('-') > -1) name = GriffonClassUtils.getPropertyNameForLowerCaseHyphenSeparatedName(name);
        return (GriffonPlugin)this.plugins.get(name);
    }
    public GriffonPlugin getGriffonPlugin(String name, Object version) {
      if(name.indexOf('-') > -1) name = GriffonClassUtils.getPropertyNameForLowerCaseHyphenSeparatedName(name);
        GriffonPlugin plugin = (GriffonPlugin)this.plugins.get(name);
        if(plugin != null) {
            if(GriffonPluginUtils.isValidVersion(plugin.getVersion(), version.toString()))
                return plugin;
        }
        return null;
    }
    public boolean hasGriffonPlugin(String name) {
        if(name.indexOf('-') > -1) name = GriffonClassUtils.getPropertyNameForLowerCaseHyphenSeparatedName(name);
        return this.plugins.containsKey(name);
    }
    public void setApplication(GriffonContext application) {
        if(application == null) throw new IllegalArgumentException("Argument [application] cannot be null");
        this.application = application;
        for (Iterator i = pluginList.iterator(); i.hasNext();) {
            GriffonPlugin plugin = (GriffonPlugin) i.next();
            plugin.setApplication(application);
        }
    }

    private boolean isAlreadyRegistered(GriffonContext application, Class artefact, String shortName) {
        return application.getClassForName(shortName) != null || application.getClassForName(artefact.getName()) != null;
    }

    public void shutdown() {
        checkInitialised();
    }
}
