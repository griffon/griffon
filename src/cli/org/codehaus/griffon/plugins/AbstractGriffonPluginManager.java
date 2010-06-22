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

import griffon.util.BuildScope;
import groovy.lang.ExpandoMetaClass;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.commons.GriffonClassUtils;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.springframework.beans.BeansException;
import org.springframework.core.io.Resource;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Abstract implementation of the GriffonPluginManager interface
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public abstract class AbstractGriffonPluginManager implements GriffonPluginManager {
    private static final String BLANK = "";
    protected List<GriffonPlugin> pluginList = new ArrayList<GriffonPlugin>();
    protected GriffonContext application;
    protected Resource[] pluginResources = new Resource[0];
    protected Map<String, GriffonPlugin> plugins = new HashMap<String, GriffonPlugin>();
    protected Map<String, GriffonPlugin> classNameToPluginMap = new HashMap<String, GriffonPlugin>();
    protected Class[] pluginClasses = new Class[0];
    protected boolean initialised = false;
    protected Map failedPlugins = new HashMap();


    public AbstractGriffonPluginManager(GriffonContext application) {
        super();
        if(application == null)
            throw new IllegalArgumentException("Argument [application] cannot be null!");

        this.application = application;
    }

    public List<TypeFilter> getTypeFilters() {
        List<TypeFilter> list = new ArrayList<TypeFilter>();
        for (GriffonPlugin griffonPlugin : pluginList) {
            list.addAll(griffonPlugin.getTypeFilters());
        }
        return Collections.unmodifiableList(list);
    }
    public GriffonPlugin[] getAllPlugins() {
        return pluginList.toArray(new GriffonPlugin[pluginList.size()]);
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

    public GriffonPlugin getGriffonPluginForClassName(String name) {
        return this.classNameToPluginMap.get(name);
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

    public boolean supportsCurrentBuildScope(String pluginName) {
        GriffonPlugin plugin = getGriffonPlugin(pluginName);
        return plugin == null || plugin.supportsScope(BuildScope.getCurrent());
    }

    public String getPluginPath(String name) {
        GriffonPlugin plugin = getGriffonPlugin(name);
        if(plugin!=null && !plugin.isBasePlugin()) {
            return plugin.getPluginPath();
        }
        return BLANK;
    }

    public String getPluginPathForInstance(Object instance) {
        if(instance!=null) {
            final Class<? extends Object> theClass = instance.getClass();
            return getPluginPathForClass(theClass);
        }
        return null;
    }

    public String getPluginPathForClass(Class<? extends Object> theClass) {
        if(theClass!=null) {
            org.codehaus.griffon.plugins.metadata.GriffonPlugin ann = theClass.getAnnotation(org.codehaus.griffon.plugins.metadata.GriffonPlugin.class);
            if(ann != null) {
                return getPluginPath(ann.name());
            }
        }
        return null;
    }
}
