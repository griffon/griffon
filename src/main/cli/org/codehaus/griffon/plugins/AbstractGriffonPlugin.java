/*
 * Copyright 2004-2011 the original author or authors.
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
import groovy.lang.GroovyObjectSupport;
import groovy.util.slurpersupport.GPathResult;
import org.codehaus.griffon.commons.AbstractGriffonClass;
import org.codehaus.griffon.commons.GriffonContext;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation that provides some default behaviours
 *
 * @author Graeme Rocher
 */
public abstract class AbstractGriffonPlugin extends GroovyObjectSupport implements GriffonPlugin {
    protected GriffonContext application;
    protected boolean isBase = false;
    protected String version = "1.0";
    protected Map dependencies = new HashMap();
    protected String[] dependencyNames = new String[0];
    protected Class pluginClass;
    protected GriffonPluginManager manager;
    protected String[] evictionList = new String[0];

    /**
     * Wrapper Griffon class for plugins
     *
     * @author Graeme Rocher
     *
     */
    class GriffonPluginClass extends AbstractGriffonClass {
        public GriffonPluginClass(Class clazz) {
            super(clazz, TRAILING_NAME);
        }
    }

    public AbstractGriffonPlugin(Class pluginClass, GriffonContext application) {
        Assert.notNull(pluginClass, "Argument [pluginClass] cannot be null");
        Assert.isTrue(pluginClass.getName().endsWith(TRAILING_NAME),
                "Argument [pluginClass] with value ["+pluginClass+"] is not a Griffon plugin (class name must end with 'GriffonPlugin')");
        this.application = application;
        this.pluginClass = pluginClass;
    }

    public String getFileSystemName() {
        return getFileSystemShortName()+'-'+getVersion();
    }

    public String getFileSystemShortName() {
        return GriffonUtil.getScriptName(getName());
    }

    public Class getPluginClass() {
        return pluginClass;
    }

    public boolean isBasePlugin() {
        return isBase;
    }

    public void setBasePlugin(boolean isBase) {
        this.isBase = isBase;
    }

    public boolean checkForChanges() {
        return false;
    }

    public void doWithWebDescriptor(GPathResult webXml) {
        // do nothing
    }
    public String[] getDependencyNames() {
        return this.dependencyNames;
    }

    public String getDependentVersion(String name) {
        return null;
    }

    public String getName() {
        return pluginClass.getName();
    }

    public String getVersion() {
        return this.version;
    }

    public String getPluginPath() {
        return PLUGINS_PATH + '/' + GriffonUtil.getScriptName(getName()) + '-' + getVersion();
    }

    public GriffonPluginManager getManager() {
        return this.manager;
    }

    public void setManager(GriffonPluginManager manager) {
        this.manager = manager;
    }
    public void setApplication(GriffonContext application) {
        this.application = application;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractGriffonPlugin)) return false;

        AbstractGriffonPlugin that = (AbstractGriffonPlugin) o;

        if (!pluginClass.equals(that.pluginClass)) return false;
        if (!version.equals(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + pluginClass.hashCode();
        return result;
    }

    public int compareTo(Object o) {
        AbstractGriffonPlugin that = (AbstractGriffonPlugin) o;
        if(this.equals(that)) return 0;

        return getName().compareTo(that.getName());
    }
}
