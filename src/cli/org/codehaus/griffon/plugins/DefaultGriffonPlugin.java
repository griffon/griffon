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

import griffon.util.GriffonUtil;
import griffon.util.BuildSettingsHolder;
import griffon.util.BuildSettings;
import groovy.lang.*;
import groovy.util.slurpersupport.GPathResult;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.commons.GriffonClassUtils;
import org.codehaus.griffon.commons.GriffonResourceUtils;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Implementation of the GriffonPlugin interface that wraps a Groovy plugin class
 * and provides the magic to invoke its various methods from Java
 *
 * @author Graeme Rocher
 * @since 0.4
 *
 */
public class DefaultGriffonPlugin extends AbstractGriffonPlugin implements GriffonPlugin {
    private static final Log LOG = LogFactory.getLog(DefaultGriffonPlugin.class);
    private GriffonPluginClass pluginGriffonClass;
    private GroovyObject plugin;

    protected BeanWrapper pluginBean;

    private PathMatchingResourcePatternResolver resolver;
    private String[] resourcesReferences;
    private int[] resourceCount;
    private String status = STATUS_ENABLED;

    public DefaultGriffonPlugin(Class pluginClass, Resource resource, GriffonContext application) {
        super(pluginClass, application);
        // create properties
        this.dependencies = Collections.EMPTY_MAP;
        this.resolver = new PathMatchingResourcePatternResolver();
        initialisePlugin(pluginClass);
    }

    private void initialisePlugin(Class pluginClass) {
        this.pluginGriffonClass = new GriffonPluginClass(pluginClass);
        this.plugin = (GroovyObject)this.pluginGriffonClass.newInstance();
        this.pluginBean = new BeanWrapperImpl(this.plugin);

        // configure plugin
        evaluatePluginVersion();
        evaluatePluginDependencies();
    }

    public DefaultGriffonPlugin(Class pluginClass, GriffonContext application) {
        this(pluginClass, null,application);
    }

    private void evaluatePluginVersion() {
        if(this.pluginBean.isReadableProperty(VERSION)) {
            Object vobj = this.plugin.getProperty(VERSION);
            if(vobj != null)
                this.version = vobj.toString();
            else
                throw new PluginException("Plugin "+this+" must specify a version. eg: def version = 0.1");
        }
        else {
            throw new PluginException("Plugin ["+getName()+"] must specify a version!");
        }
    }

    private void evaluatePluginDependencies() {
        if(this.pluginBean.isReadableProperty(DEPENDS_ON)) {
            this.dependencies = (Map) GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, DEPENDS_ON);
            this.dependencyNames = (String[])this.dependencies.keySet().toArray(new String[this.dependencies.size()]);
        }
    }

    /**
     * @return the resolver
     */
    public PathMatchingResourcePatternResolver getResolver() {
        return resolver;
    }


    public String getName() {
        return this.pluginGriffonClass.getLogicalPropertyName();
    }

    public String getVersion() {
        return this.version;
    }
    public String[] getDependencyNames() {
        return this.dependencyNames;
    }


    public String getDependentVersion(String name) {
        Object dependentVersion = this.dependencies.get(name);
        if(dependentVersion == null)
            throw new PluginException("Plugin ["+getName()+"] referenced dependency ["+name+"] with no version!");
        else
            return dependentVersion.toString();
    }

    public String toString() {
        return "["+getName()+":"+getVersion()+"]";
    }

    public Log getLog() {
        return LOG;
    }
    public GriffonPlugin getPlugin() {
        return this;
    }

    public GroovyObject getInstance() {
        return this.plugin;
    }

    public boolean isEnabled() {
        return STATUS_ENABLED.equals(this.status);
    }
}
