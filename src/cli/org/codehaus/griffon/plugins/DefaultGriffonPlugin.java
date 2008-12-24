/*
 * Copyright 2004-2005 the original author or authors.
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

//import griffon.spring.BeanBuilder;
import org.codehaus.griffon.util.GriffonUtil;
import org.codehaus.griffon.util.BuildSettingsHolder;
import org.codehaus.griffon.util.BuildSettings;
import groovy.lang.*;
import groovy.util.slurpersupport.GPathResult;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.codehaus.griffon.commons.ArtefactHandler;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.commons.GriffonClassUtils;
import org.codehaus.griffon.commons.GriffonResourceUtils;
//import org.codehaus.griffon.commons.spring.RuntimeSpringConfiguration;
//import org.codehaus.griffon.compiler.GriffonClassLoader;
//import org.codehaus.griffon.compiler.support.GriffonResourceLoader;
//import org.codehaus.griffon.compiler.support.GriffonResourceLoaderHolder;
import org.codehaus.griffon.plugins.exceptions.PluginException;
//import org.codehaus.griffon.support.ParentApplicationContextAware;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.ApplicationContext;
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
public class DefaultGriffonPlugin extends AbstractGriffonPlugin implements GriffonPlugin/*, ParentApplicationContextAware*/ {

    private static final String PLUGIN_CHANGE_EVENT_CTX = "ctx";
    private static final String PLUGIN_CHANGE_EVENT_APPLICATION = "application";
    private static final String PLUGIN_CHANGE_EVENT_PLUGIN = "plugin";
    private static final String PLUGIN_CHANGE_EVENT_SOURCE = "source";
    private static final String PLUGIN_CHANGE_EVENT_MANAGER = "manager";
    private static final String PLUGIN_LOAD_AFTER_NAMES = "loadAfter";

    private static final String PLUGIN_OBSERVE = "observe";
    private static final Log LOG = LogFactory.getLog(DefaultGriffonPlugin.class);
    private GriffonPluginClass pluginGriffonClass;
    private GroovyObject plugin;

    protected BeanWrapper pluginBean;
    private Closure onChangeListener;
    private Resource[] watchedResources = new Resource[0];
    private long[] modifiedTimes = new long[0];

    private PathMatchingResourcePatternResolver resolver;
    private String[] resourcesReferences;
    private int[] resourceCount;
    private String[] loadAfterNames = new String[0];
    private String[] influencedPluginNames = new String[0];
    private String status = STATUS_ENABLED;
    private String[] observedPlugins;
    private long pluginLastModified = Long.MAX_VALUE;
    private URL pluginUrl;
    private Closure onConfigChangeListener;
    private Closure onShutdownListener;
    private Class[] providedArtefacts = new Class[0];


    public DefaultGriffonPlugin(Class pluginClass, Resource resource, GriffonContext application) {
        super(pluginClass, application);
        // create properties
        this.dependencies = Collections.EMPTY_MAP;
        this.resolver = new PathMatchingResourcePatternResolver();
        if(resource != null) {
            try {
                pluginUrl = resource.getURL();
                this.pluginLastModified = pluginUrl
                                            .openConnection()
                                            .getLastModified();
            } catch (IOException e) {
                LOG.warn("I/O error reading last modified date of plug-in ["+pluginClass+"], you won't be able to reload changes: " + e.getMessage(),e);
            }
        }


        initialisePlugin(pluginClass);
    }

    private void initialisePlugin(Class pluginClass) {
        this.pluginGriffonClass = new GriffonPluginClass(pluginClass);
        this.plugin = (GroovyObject)this.pluginGriffonClass.newInstance();
        this.pluginBean = new BeanWrapperImpl(this.plugin);

        // configure plugin
        evaluatePluginVersion();
        evaluatePluginDependencies();
        evaluatePluginLoadAfters();
        evaluateProvidedArtefacts();
        evaluatePluginEvictionPolicy();
        evaluatePluginInfluencePolicy();
        evaluateOnChangeListener();
        evaluateObservedPlugins();
        evaluatePluginStatus();
    }

    private void evaluateProvidedArtefacts() {
        Object providedArtefacts = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, PROVIDED_ARTEFACTS);
        if(providedArtefacts instanceof Collection) {
            final Collection artefactList = (Collection) providedArtefacts;
            this.providedArtefacts = (Class[])artefactList.toArray(new Class[artefactList.size()]);
        }
    }

    public DefaultGriffonPlugin(Class pluginClass, GriffonContext application) {
        this(pluginClass, null,application);
    }

    private void evaluateObservedPlugins() {
        if(this.pluginBean.isReadableProperty(PLUGIN_OBSERVE)) {
            Object observeProperty = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, PLUGIN_OBSERVE);
            if(observeProperty instanceof Collection) {
                Collection  observeList = (Collection)observeProperty;
                observedPlugins = new String[observeList.size()];
                int j = 0;
                for (Iterator i = observeList.iterator(); i.hasNext();) {
                    String pluginName = i.next().toString();
                    observedPlugins[j++] = pluginName;
                }
            }
        }
        if(observedPlugins == null) observedPlugins = new String[0];
    }

    private void evaluatePluginStatus() {
        if(this.pluginBean.isReadableProperty(STATUS)) {
            Object statusObj = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, STATUS);
            if(statusObj != null) {
                this.status = statusObj.toString().toLowerCase();
            }
        }
    }

    private void evaluateOnChangeListener() {
        if(this.pluginBean.isReadableProperty(ON_CONFIG_CHANGE)) {
            this.onConfigChangeListener = (Closure)GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, ON_CONFIG_CHANGE);
        }
        if(this.pluginBean.isReadableProperty(ON_SHUTDOWN)) {
            this.onShutdownListener= (Closure)GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, ON_SHUTDOWN);
        }
        if(this.pluginBean.isReadableProperty(ON_CHANGE)) {
            this.onChangeListener = (Closure) GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, ON_CHANGE);
            Object referencedResources = GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, WATCHED_RESOURCES);


            try {
                List resourceList = null;
                if(referencedResources instanceof String) {
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("Configuring plugin "+this+" to watch resources with pattern: " + referencedResources);
                    }
                    resourceList = new ArrayList();
                    resourceList.add(referencedResources.toString());
                }
                else if(referencedResources instanceof List) {
                    resourceList = (List)referencedResources;
                }

                if(resourceList!=null) {

                    this.resourcesReferences = new String[resourceList.size()];
                    this.resourceCount = new int[resourceList.size()];
                    for (int i = 0; i < resourcesReferences.length; i++) {
                        String resRef = resourceList.get(i).toString();
                        resourcesReferences[i]=resRef;
                    }
                    for (int i = 0; i < resourcesReferences.length; i++) {
                        String res = resourcesReferences[i];
                        Resource[] tmp = resolver.getResources(res);
                        resourceCount[i] = tmp.length;

                        if(LOG.isDebugEnabled()) {
                            LOG.debug("Watching resource set ["+(i+1)+"]: " + ArrayUtils.toString(tmp));
                        }
                        if(tmp.length == 0)
                            tmp = resolver.getResources("classpath*:" + res);

                        if(tmp.length > 0){
                            watchedResources = (Resource[])ArrayUtils.addAll(this.watchedResources, tmp);
                        }
                    }
                }

            }
            catch (IllegalArgumentException e) {
            	if(GriffonUtil.isDevelopmentEnv())
            		LOG.debug("Cannot load plug-in resource watch list from ["+ ArrayUtils.toString(resourcesReferences) +"]. This means that the plugin "+this+", will not be able to auto-reload changes effectively. Try runnng griffon upgrade.: " + e.getMessage());
            }
            catch (IOException e) {
            	if(GriffonUtil.isDevelopmentEnv())
            		LOG.debug("Cannot load plug-in resource watch list from ["+ ArrayUtils.toString(resourcesReferences) +"]. This means that the plugin "+this+", will not be able to auto-reload changes effectively. Try runnng griffon upgrade.: " + e.getMessage());
            }
            if(LOG.isDebugEnabled()) {
                LOG.debug("Plugin "+this+" found ["+watchedResources.length+"] to watch");
            }
            try {
                initializeModifiedTimes();
            } catch (IOException e) {
                LOG.warn("I/O exception initializing modified times for watched resources: " + e.getMessage(), e);
            }

        }
    }

    private void evaluatePluginInfluencePolicy() {
        if(this.pluginBean.isReadableProperty(INFLUENCES)) {
            List influencedList = (List)this.pluginBean.getPropertyValue(INFLUENCES);
            if(influencedList != null) {
                this.influencedPluginNames = (String[])influencedList.toArray(new String[influencedList.size()]);
            }
        }
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

    private void evaluatePluginEvictionPolicy() {
        if(this.pluginBean.isReadableProperty(EVICT)) {
            List pluginsToEvict = (List) GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, EVICT);
            if(pluginsToEvict != null) {
                this.evictionList = new String[pluginsToEvict.size()];
                int index = 0;
                for (Iterator i = pluginsToEvict.iterator(); i.hasNext();) {
                    Object o = i.next();
                    evictionList[index++] = o != null ? o.toString() : "";
                }
            }
        }
    }

    private void evaluatePluginLoadAfters() {
        if(this.pluginBean.isReadableProperty(PLUGIN_LOAD_AFTER_NAMES)) {
            List loadAfterNamesList = (List) GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, PLUGIN_LOAD_AFTER_NAMES);
            if(loadAfterNamesList != null) {
                this.loadAfterNames = (String[])loadAfterNamesList.toArray(new String[loadAfterNamesList.size()]);
            }
        }
    }

    private void evaluatePluginDependencies() {
        if(this.pluginBean.isReadableProperty(DEPENDS_ON)) {
            this.dependencies = (Map) GriffonClassUtils.getPropertyOrStaticPropertyOrFieldValue(this.plugin, DEPENDS_ON);
            this.dependencyNames = (String[])this.dependencies.keySet().toArray(new String[this.dependencies.size()]);
        }
    }


    public String[] getLoadAfterNames() {
        return this.loadAfterNames;
    }


    /**
     * @return the resolver
     */
    public PathMatchingResourcePatternResolver getResolver() {
        return resolver;
    }



//    public ApplicationContext getParentCtx() {
//        return application.getParentContext();
//    }
//
//    public BeanBuilder beans(Closure closure) {
//        BeanBuilder bb = new BeanBuilder(getParentCtx(), new GroovyClassLoader(application.getClassLoader()));
//        bb.invokeMethod("beans", new Object[]{closure});
//        return bb;
//    }

    private void initializeModifiedTimes() throws IOException {
        modifiedTimes = new long[watchedResources.length];
        for (int i = 0; i < watchedResources.length; i++) {
            Resource r = watchedResources[i];
            URLConnection c = r.getURL().openConnection();
            c.setDoInput(false);
            c.setDoOutput(false);
            modifiedTimes[i] = c.getLastModified();
        }
    }

    public void doWithApplicationContext(ApplicationContext applicationContext) {
        if(this.pluginBean.isReadableProperty(DO_WITH_APPLICATION_CONTEXT)) {
            Closure c = (Closure)this.plugin.getProperty(DO_WITH_APPLICATION_CONTEXT);
            c.setDelegate(this);
            c.call(new Object[]{applicationContext});
        }
    }

//    public void doWithRuntimeConfiguration(
//            RuntimeSpringConfiguration springConfig) {
//
//        if(this.pluginBean.isReadableProperty(DO_WITH_SPRING)) {
//            if(LOG.isDebugEnabled()) {
//                LOG.debug("Plugin " + this + " is participating in Spring configuration...");
//            }
//            Closure c = (Closure)this.plugin.getProperty(DO_WITH_SPRING);
//            BeanBuilder bb = new BeanBuilder(getParentCtx(), application.getClassLoader());
//            bb.setSpringConfig(springConfig);
//            Binding b = new Binding();
//            b.setVariable("application", application);
//            b.setVariable("manager", getManager());
//            b.setVariable("plugin", this);
//            b.setVariable("parentCtx", getParentCtx());
//            b.setVariable("resolver", getResolver());
//            bb.setBinding(b);
//            c.setDelegate(bb);
//            bb.invokeMethod("beans", new Object[]{c});
//        }
//
//    }

    public String getName() {
        return this.pluginGriffonClass.getLogicalPropertyName();
    }

    public String getVersion() {
        return this.version;
    }
    public String[] getDependencyNames() {
        return this.dependencyNames;
    }

    /**
     * @return the watchedResources
     */
    public Resource[] getWatchedResources() {
        return watchedResources;
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

    public void doWithWebDescriptor(GPathResult webXml) {
        if(this.pluginBean.isReadableProperty(DO_WITH_WEB_DESCRIPTOR)) {
            Closure c = (Closure)this.plugin.getProperty(DO_WITH_WEB_DESCRIPTOR);
            c.setResolveStrategy(Closure.DELEGATE_FIRST);
            c.setDelegate(this);
             c.call(webXml);
        }

    }

    /**
     * Monitors the plugin resources defined in the watchResources property for changes and
     * fires onChange events by calling an onChange closure defined in the plugin (if it exists)
     */
//    public boolean checkForChanges() {
//        if(pluginUrl != null) {
//            long currentModified = -1;
//            URLConnection conn = null;
//            try {
//                conn = pluginUrl
//                             .openConnection();
//
//                currentModified = conn
//                                    .getLastModified();
//                if(currentModified > pluginLastModified) {
//
//                    if(LOG.isInfoEnabled())
//                        LOG.info("Griffon plug-in "+this+" changed, reloading changes..");
//
//                    GroovyClassLoader gcl = new GroovyClassLoader(application.getClassLoader());
//                    initialisePlugin(gcl.parseClass(conn.getInputStream()));
//                    pluginLastModified = currentModified;
//                    return true;
//                }
//            } catch (IOException e) {
//                LOG.warn("Error reading plugin ["+pluginClass+"] last modified date, cannot reload following change: " + e.getMessage());
//            }
//            finally {
//                if(conn!=null) {
//                    try {
//                        conn.getInputStream().close();
//                    } catch (IOException e) {
//                        LOG.warn("Error closing URL connection to plugin resource ["+pluginUrl+"]: " + e.getMessage(), e);
//                    }
//                }
//
//            }
//
//        }
//        if(onChangeListener!=null) {
//                checkForNewResources(this);
//
//                if(LOG.isDebugEnabled()) {
//                    LOG.debug("Plugin "+this+" checking ["+watchedResources.length+"] resources for changes..");
//                }
//                for (int i = 0; i < watchedResources.length; i++) {
//                    final Resource r = watchedResources[i];
//                    long modifiedFlag = checkModified(r, modifiedTimes[i]) ;
//                    if( modifiedFlag > -1) {
//                        if(LOG.isInfoEnabled())
//                            LOG.info("Griffon plug-in resource ["+r+"] changed, reloading changes..");
//
//                        modifiedTimes[i] = modifiedFlag;
//                        fireModifiedEvent(r, this);
//                        refreshInfluencedPlugins();
//                    }
//                }
//
//        }
//        return false;
//    }

    /**
     * This method will retrieve all the influenced plugins from the manager and
     * call refresh() on each one
     */
    private void refreshInfluencedPlugins() {
        GriffonPluginManager manager = getManager();
        if(LOG.isDebugEnabled())
            LOG.debug("Plugin "+this+" starting refresh of influenced plugins " + ArrayUtils.toString(influencedPluginNames));
        if(manager != null) {
            for (int i = 0; i < influencedPluginNames.length; i++) {
                String name = influencedPluginNames[i];
                GriffonPlugin plugin = manager.getGriffonPlugin(name);

                if(plugin!=null) {
                    if(LOG.isDebugEnabled())
                        LOG.debug(this+" plugin is refreshing influenced plugin " + plugin +" following change to resource");

                    plugin.refresh();
                }
            }
        }
        else if(LOG.isDebugEnabled()) {
            LOG.debug("Plugin "+this+" cannot refresh influenced plugins, manager is not found");
        }
    }



    /**
     * This method will take a Resource and check it against the previous modified time passed
     * in the arguments. If the resource was modified it will return the new modified time, otherwise
     * it will return -1
     *
     * @param r The Resource instance
     * @param previousModifiedTime The last time the Resource was modified
     * @return The new modified time or -1
     */
    private long checkModified(Resource r, long previousModifiedTime) {
        // If the resource doesn't exist, skip the check.
        if (!r.exists()) return -1;

        try {
            if(LOG.isDebugEnabled())
                LOG.debug("Checking modified for resource " + r);

            long lastModified = r.lastModified();

            if( previousModifiedTime < lastModified ) {
                return lastModified;
            }
        } catch (IOException e) {
            LOG.debug("Unable to read last modified date of plugin resource" +e.getMessage(),e);
        }
        return -1;
    }

//    private void checkForNewResources(final GriffonPlugin plugin) {
//
//
//        if(resourcesReferences != null) {
//            if(LOG.isDebugEnabled()) {
//                LOG.debug("Plugin "+plugin+" checking ["+ArrayUtils.toString(resourcesReferences)+"] resource references new resources that have been added..");
//            }
//
//            for (int i = 0; i < resourcesReferences.length; i++) {
//                String resourcesReference = resourcesReferences[i];
//                try {
//                    Resource[] tmp = resolver.getResources(resourcesReference);
//                    if(resourceCount[i] < tmp.length) {
//                        Resource newResource = null;
//                        resourceCount[i] = tmp.length;
//
//                        for (int j = 0; j < tmp.length; j++) {
//                            Resource resource = tmp[j];
//                            if(!ArrayUtils.contains(watchedResources, resource)) {
//                                newResource = resource;
//                                break;
//                            }
//                        }
//
//                        if(newResource!=null) {
//                            watchedResources = (Resource[])ArrayUtils.add(watchedResources, newResource);
//
//                            if(LOG.isInfoEnabled())
//                                LOG.info("Found new Griffon plug-in resource ["+newResource+"], adding to application..");
//
//
//                            if(newResource.getFilename().endsWith(".groovy")) {
//                                if(LOG.isDebugEnabled())
//                                    LOG.debug("[GriffonPlugin] plugin resource ["+newResource+"] added, registering resource with class loader...");
//
//                                GroovyClassLoader classLoader = this.application.getClassLoader();
//
//                                GriffonResourceLoader resourceLoader = GriffonResourceLoaderHolder.getResourceLoader();
//
//                                Resource[] classLoaderResources = resourceLoader.getResources();
//                                classLoaderResources = (Resource[])ArrayUtils.add(classLoaderResources, newResource);
//                                resourceLoader.setResources(classLoaderResources);
//
//                                if(classLoader instanceof GriffonClassLoader) {
//                                    ((GriffonClassLoader)classLoader).setGriffonResourceLoader(resourceLoader);
//                                }
//                            }
//
//                            initializeModifiedTimes();
//
//                            if(LOG.isDebugEnabled())
//                                LOG.debug("[GriffonPlugin] plugin resource ["+newResource+"] added, firing event if possible..");
//                            fireModifiedEvent(newResource, plugin);
//                        }
//                    }
//                }
//                catch (IllegalArgumentException e) {
//                    LOG.debug("Plugin "+this+"  was unable to check for new plugin resources: " + e.getMessage());
//                }
//                catch (IOException e) {
//                    LOG.debug("Plugin "+this+"  was unable to check for new plugin resources: " + e.getMessage());
//                }
//
//            }
//        }
//    }

//    protected void fireModifiedEvent(final Resource resource, final GriffonPlugin plugin) {
//
//        Class loadedClass = null;
//        String className = GriffonResourceUtils.getClassName(resource);
//
//        if(className != null) {
//            Class oldClass = application.getClassForName(className);
//            loadedClass = attemptClassReload(className);
//            if(oldClass != null) {
//                GroovySystem.getMetaClassRegistry().removeMetaClass(oldClass);
//            }
//        }
//
//        final Class resourceClass = loadedClass;
//        Object source;
//        if(resourceClass != null)source=resourceClass;
//        else source = resource;
//
//        if(loadedClass != null && Modifier.isAbstract(loadedClass.getModifiers())) {
//            restartContainer();
//        }
//        else {
//            Map event = notifyOfEvent(EVENT_ON_CHANGE, source);
//            if(LOG.isDebugEnabled()) {
//                LOG.debug("Firing onChange event listener with event object ["+event+"]");
//            }
//
//            getManager().informObservers(getName(), event);
//        }
//
//    }

    public void restartContainer() {
        // here we touch the classes directory if the file is abstract to force the Griffon server
        // to restart the web application
        try {
            BuildSettings settings = BuildSettingsHolder.getSettings();
            File classesDir = settings != null ? settings.getClassesDir() : null;
            if(classesDir == null) {
                Resource r = this.applicationContext.getResource("/WEB-INF/classes");
                classesDir = r.getFile();
            }
            classesDir.setLastModified(System.currentTimeMillis());
        } catch (IOException e) {
            LOG.error("Error retrieving /WEB-INF/classes directory: " + e.getMessage(),e);
        }
    }

//    private Class attemptClassReload(final Resource resource) {
//        String className = GriffonResourceUtils.getClassName(resource);
//        if(className != null) {
//            return attemptClassReload(className);
//        }
//        return null;
//    }



//    private Class attemptClassReload(String className) {
//        final GroovyClassLoader loader = application.getClassLoader();
//        if(loader instanceof GriffonClassLoader) {
//            GriffonClassLoader griffonLoader = (GriffonClassLoader)loader;
//
//            return griffonLoader.reloadClass(className);
//        }
//        return null;
//    }


    public void setWatchedResources(Resource[] watchedResources) throws IOException {
        this.watchedResources = watchedResources;
        initializeModifiedTimes();
    }

    /*
     * These two properties help the closures to resolve a log and plugin variable during executing
     */
    public Log getLog() {
        return LOG;
    }
    public GriffonPlugin getPlugin() {
        return this;
    }


    public void setParentApplicationContext(ApplicationContext parent) {
        // do nothing for the moment
    }



    /* (non-Javadoc)
     * @see org.codehaus.griffon.plugins.AbstractGriffonPlugin#refresh()
     */
//    public void refresh() {
//        refresh(true);
//    }
//
//    public void refresh(boolean fireEvent) {
//        for (int i = 0; i < watchedResources.length; i++) {
//            Resource r = watchedResources[i];
//            try {
//                r.getFile().setLastModified(System.currentTimeMillis());
//            } catch (IOException e) {
//                // ignore
//            }
//            if(fireEvent)
//                fireModifiedEvent(r, this);
//        }
//    }


    public GroovyObject getInstance() {
        return this.plugin;
    }

    public void doWithDynamicMethods(ApplicationContext applicationContext) {
        if(this.pluginBean.isReadableProperty(DO_WITH_DYNAMIC_METHODS)) {
            Closure c = (Closure)this.plugin.getProperty(DO_WITH_DYNAMIC_METHODS);
            c.setDelegate(this);
            c.call(new Object[]{applicationContext});
        }
    }

    public boolean isEnabled() {
        return STATUS_ENABLED.equals(this.status);
    }

    public String[] getObservedPluginNames() {
        return this.observedPlugins;
    }

    public void notifyOfEvent(Map event) {
        if(onChangeListener != null) {
            invokeOnChangeListener(event);
        }
    }

    public Map notifyOfEvent(int eventKind, final Object source) {
        Map event = new HashMap() {{
            put(PLUGIN_CHANGE_EVENT_SOURCE, source);
            put(PLUGIN_CHANGE_EVENT_PLUGIN, plugin);
            put(PLUGIN_CHANGE_EVENT_APPLICATION, application);
            put(PLUGIN_CHANGE_EVENT_MANAGER, getManager());
            put(PLUGIN_CHANGE_EVENT_CTX, applicationContext);
        }};

        switch (eventKind) {
            case EVENT_ON_CHANGE:
                notifyOfEvent(event);
            break;
            case EVENT_ON_SHUTDOWN:
                invokeOnShutdownEventListener(event);
            break;

            case EVENT_ON_CONFIG_CHANGE:
                invokeOnConfigChangeListener(event);
            break;
            default:
                notifyOfEvent(event);
        }

        return event;
    }

    private void invokeOnShutdownEventListener(Map event) {
        callEvent(onShutdownListener,event);    }

    private void invokeOnConfigChangeListener(Map event) {
        callEvent(onConfigChangeListener,event);
    }

    private void callEvent(Closure closureHook, Map event) {
        if(closureHook !=null) {
            closureHook.setDelegate(this);
            closureHook.call(new Object[]{event});
        }
    }

    private void invokeOnChangeListener(Map event) {
        onChangeListener.setDelegate(this);
        onChangeListener.call(new Object[]{event});
    }

//	public void doArtefactConfiguration() {
//		if(this.pluginBean.isReadableProperty(ARTEFACTS)) {
//			List l = (List)this.plugin.getProperty(ARTEFACTS);
//			for (Iterator iter = l.iterator(); iter.hasNext();) {
//				Object artefact = iter.next();
//				if(artefact instanceof Class) {
//					Class artefactClass = (Class)artefact;
//					if(ArtefactHandler.class.isAssignableFrom(artefactClass)) {
//						try {
//							this.application.registerArtefactHandler((ArtefactHandler)artefactClass.newInstance());
//						} catch (InstantiationException e) {
//				            LOG.error("Cannot instantiate an Artefact Handler:" + e.getMessage(),e);
//						} catch (IllegalAccessException e) {
//				            LOG.error("The constructor of the Artefact Handler is not accessible:" + e.getMessage(),e);
//						}
//					} else {
//			            LOG.error("This class is not an ArtefactHandler:" + artefactClass.getName());
//					}
//				} else {
//					if(artefact instanceof ArtefactHandler) {
//						this.application.registerArtefactHandler((ArtefactHandler)artefact);
//					} else {
//						LOG.error("This object is not an ArtefactHandler:" + artefact + "[" + artefact.getClass().getName()+"]");
//					}
//				}
//			}
//		}
//	}
//
//    public Class[] getProvidedArtefacts() {
//        return this.providedArtefacts;
//    }

}