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

import griffon.util.BuildScope;
import griffon.util.Environment;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.commons.GriffonClassUtils;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.codehaus.griffon.documentation.DocumentationContext;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Implementation of the GriffonPlugin interface that wraps a Groovy plugin class
 * and provides the magic to invoke its various methods from Java
 *
 * @author Graeme Rocher
 * @since 0.4
 *
 */
public class DefaultGriffonPlugin extends AbstractGriffonPlugin {
    private static final Log LOG = LogFactory.getLog(DefaultGriffonPlugin.class);
    private static final String INCLUDES = "includes";
    private static final String EXCLUDES = "excludes";
    private GriffonPluginClass pluginGriffonClass;
    private GroovyObject plugin;

    protected BeanWrapper pluginBean;

    private PathMatchingResourcePatternResolver resolver;
    private String[] resourcesReferences;
    private int[] resourceCount;
    private Map pluginScopes;
    private Map pluginEnvs;
    private List<String> pluginExcludes = new ArrayList<String>();
    private List<String> pluginIncludes = new ArrayList<String>();
    private Collection<? extends TypeFilter> typeFilters = new ArrayList<TypeFilter>();
    private Resource pluginDescriptor;

    public DefaultGriffonPlugin(Class pluginClass, Resource resource, GriffonContext application) {
        super(pluginClass, application);
        // create properties
        this.dependencies = Collections.EMPTY_MAP;
        this.pluginDescriptor = resource;
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
        evaluatePluginScopes();
        evaluatePluginExcludes();
        evaluateTypeFilters();
    }

    public DefaultGriffonPlugin(Class pluginClass, GriffonContext application) {
        this(pluginClass, null,application);
    }

    private void evaluateTypeFilters() {
        Object result = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, TYPE_FILTERS);
        if(result instanceof List) {
            this.typeFilters = (List<TypeFilter>) result;
        }
    }

    private void evaluatePluginExcludes() {
        Object result = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, PLUGIN_EXCLUDES);
        if (result instanceof List) {
            this.pluginExcludes = (List<String>) result;
        }
    }

    private void evaluatePluginScopes() {
        // Damn I wish Java had closures
        this.pluginScopes = evaluateIncludeExcludeProperty(SCOPES, new Closure(this) {
            private static final long serialVersionUID = 1;
            @Override
            public Object call(Object arguments) {
                final String scopeName = ((String) arguments).toUpperCase();
                try {
                    return BuildScope.valueOf(scopeName);
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Plugin "+this+" specifies invalid scope ["+scopeName+"]");
                }
            }
        });
        this.pluginEnvs = evaluateIncludeExcludeProperty(ENVIRONMENTS, new Closure(this) {
            private static final long serialVersionUID = 1;
            @Override
            public Object call(Object arguments) {
                String envName = (String)arguments;
                Environment env = Environment.getEnvironment(envName);
                if(env != null) return env.getName();
                return arguments;
            }
        });
    }

    private Map evaluateIncludeExcludeProperty(String name, Closure converter) {
        Map resultMap = new HashMap();
        Object propertyValue = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, name);
        if(propertyValue instanceof Map) {
            Map containedMap = (Map)propertyValue;

            Object includes = containedMap.get(INCLUDES);
            evaluateAndAddIncludeExcludeObject(resultMap, includes, true, converter);

            Object excludes = containedMap.get(EXCLUDES);
            evaluateAndAddIncludeExcludeObject(resultMap, excludes, false, converter);
        }
        else {
            evaluateAndAddIncludeExcludeObject(resultMap, propertyValue, true, converter);
        }
        return resultMap;
    }

    private void evaluateAndAddIncludeExcludeObject(Map targetMap, Object includeExcludeObject, boolean include, Closure converter) {
        if(includeExcludeObject instanceof String) {
            final String includeExcludeString = (String) includeExcludeObject;
            evaluateAndAddToIncludeExcludeSet(targetMap,includeExcludeString, include, converter);
        }
        else if(includeExcludeObject instanceof List) {
            List includeExcludeList = (List) includeExcludeObject;
            evaluateAndAddListOfValues(targetMap,includeExcludeList, include, converter);
        }
    }

    private void evaluateAndAddListOfValues(Map targetMap, List includeExcludeList, boolean include, Closure converter) {
        for (Object scope : includeExcludeList) {
            if (scope instanceof String) {
                final String scopeName = (String) scope;
                evaluateAndAddToIncludeExcludeSet(targetMap, scopeName, include, converter);
            }
        }
    }

    private void evaluateAndAddToIncludeExcludeSet(Map targetMap, String includeExcludeString, boolean include, Closure converter) {
        Set set = lazilyCreateIncludeOrExcludeSet(targetMap,include);
        set.add(converter.call(includeExcludeString));
    }

    private Set lazilyCreateIncludeOrExcludeSet(Map targetMap, boolean include) {
        String key = include ? INCLUDES : EXCLUDES ;
        Set set = (Set) targetMap.get(key);
        if(set == null) {
            set = new HashSet();
            targetMap.put(key, set);
        }
        return set;
    }

    private void evaluatePluginVersion() {
        if (!this.pluginBean.isReadableProperty(VERSION)) {
           throw new PluginException("Plugin ["+getName()+"] must specify a version!");
        }

        Object vobj = this.plugin.getProperty(VERSION);
        if (vobj != null) {
            this.version = vobj.toString();
        }
        else {
            throw new PluginException("Plugin "+this+" must specify a version. eg: def version = 0.1");
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

    private boolean enableDocumentationGeneration() {
        return isBasePlugin();
    }

    public String getName() {
        return this.pluginGriffonClass.getLogicalPropertyName();
    }

    public void addExclude(BuildScope buildScope) {
        final Map map = this.pluginScopes;
        addExcludeRuleInternal(map, buildScope);
    }

    private void addExcludeRuleInternal(Map map, Object o) {
        Collection excludes = (Collection) map.get(EXCLUDES);
        if(excludes == null) {
            excludes = new ArrayList();
            map.put(EXCLUDES, excludes);
        }
        Collection includes = (Collection) map.get(INCLUDES);
        if(includes!=null) includes.remove(o);
        excludes.add(o);
    }

    public void addExclude(Environment env) {
        final Map map = this.pluginEnvs;
        addExcludeRuleInternal(map, env);        
    }

    public boolean supportsScope(BuildScope buildScope) {
        return supportsValueInIncludeExcludeMap(pluginScopes, buildScope);
    }

    public boolean supportsEnvironment(Environment environment) {
        return supportsValueInIncludeExcludeMap(pluginEnvs, environment.getName());
    }

    public boolean supportsCurrentScopeAndEnvironment() {
        BuildScope bs = BuildScope.getCurrent();
        Environment e = Environment.getCurrent();

        return supportsEnvironment(e) && supportsScope(bs);
    }

    private boolean supportsValueInIncludeExcludeMap(Map includeExcludeMap, Object value) {
        if(includeExcludeMap.isEmpty()) {
            return true;
        }

        Set includes = (Set) includeExcludeMap.get(INCLUDES);
        if (includes != null) {
            return includes.contains(value);
        }

        Set excludes = (Set)includeExcludeMap.get(EXCLUDES);
        return !(excludes != null && excludes.contains(value));
    }

    public void doc(String text) {
        if(enableDocumentationGeneration()) {
            DocumentationContext.getInstance().document(text);
        }
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

    public List<String> getPluginExcludes() {
        return this.pluginExcludes;
    }

    public List<String> getPluginIncludes() {
        return this.pluginIncludes;
    }

    public Collection<? extends TypeFilter> getTypeFilters() {
        return this.typeFilters;
    }

    public String getFullName() {        
        return getName() + '-' + getVersion();
    }

    public Resource getDescriptor() {
        return pluginDescriptor;
    }
    
    public Resource getPluginDir() {
        try {
            return pluginDescriptor.createRelative(".");
        } catch (IOException e) {
            return null;
        }
    }

    public Map getProperties() {
        return DefaultGroovyMethods.getProperties(plugin);
    }
}
