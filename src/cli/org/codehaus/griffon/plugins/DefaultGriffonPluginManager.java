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

import groovy.lang.*;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.griffon.commons.GriffonContext;
import griffon.util.GriffonUtil;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

//import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

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
public class DefaultGriffonPluginManager extends AbstractGriffonPluginManager implements GriffonPluginManager {

    private static final Log LOG = LogFactory.getLog(DefaultGriffonPluginManager.class);
    private static final Class[] COMMON_CLASSES =
            new Class[]{Boolean.class, Byte.class, Character.class, Class.class, Double.class,Float.class, Integer.class, Long.class,
                        Number.class, Short.class, String.class, BigInteger.class, BigDecimal.class, URL.class, URI.class};

    //private final GriffonPluginChangeChecker pluginChangeScanner = new GriffonPluginChangeChecker(this);
    private static final int SCAN_INTERVAL = 1000; //in ms

    private List delayedLoadPlugins = new LinkedList();
    private ApplicationContext parentCtx;
    private PathMatchingResourcePatternResolver resolver;
    private Map delayedEvictions = new HashMap();
    //private ServletContext servletContext;
    private Map pluginToObserverMap = new HashMap();

    private long configLastModified;
    private PluginFilter pluginFilter;
    private static final String GRIFFON_PLUGIN_SUFFIX = "GriffonPlugin";

    public DefaultGriffonPluginManager(String resourcePath, GriffonContext application) throws IOException {
          super(application);
          if(application == null)
              throw new IllegalArgumentException("Argument [application] cannot be null!");

          resolver = new PathMatchingResourcePatternResolver();
          try {
              this.pluginResources = resolver.getResources(resourcePath);
          }
          catch(IOException ioe) {
              LOG.debug("Unable to load plugins for resource path " + resourcePath, ioe);
          }
          //this.corePlugins = new PathMatchingResourcePatternResolver().getResources("classpath:org/codehaus/groovy/griffon/**/plugins/**GriffonPlugin.groovy");
          this.application = application;
          setPluginFilter();

      }


    public DefaultGriffonPluginManager(String[] pluginResources, GriffonContext application) {
          super(application);
          resolver = new PathMatchingResourcePatternResolver();

          List resourceList = new ArrayList();
          for (int i = 0; i < pluginResources.length; i++) {
              String resourcePath = pluginResources[i];
              try {
                  Resource[] resources = resolver.getResources(resourcePath);
                  for (int j = 0; j < resources.length; j++) {
                      Resource resource = resources[j];
                      resourceList.add(resource);
                  }

              }
              catch(IOException ioe) {
                  LOG.debug("Unable to load plugins for resource path " + resourcePath, ioe);
              }

          }

          this.pluginResources = (Resource[])resourceList.toArray(new Resource[resourceList.size()]);
          this.application = application;
          setPluginFilter();

      }

      public DefaultGriffonPluginManager(Class[] plugins, GriffonContext application) throws IOException {
          super(application);
          this.pluginClasses = plugins;
          resolver = new PathMatchingResourcePatternResolver();
          //this.corePlugins = new PathMatchingResourcePatternResolver().getResources("classpath:org/codehaus/groovy/griffon/**/plugins/**GriffonPlugin.groovy");
          this.application = application;
          setPluginFilter();

      }

      public DefaultGriffonPluginManager(Resource[] pluginFiles, GriffonContext application) {
          super(application);
          resolver = new PathMatchingResourcePatternResolver();
          this.pluginResources = pluginFiles;
          this.application = application;
          setPluginFilter();

      }


    public void doPostProcessing(ApplicationContext applicationContext) {
        super.doPostProcessing(applicationContext);
//        startPluginChangeScanner();
    }

    private void setPluginFilter() {
           this.pluginFilter = new PluginFilterRetriever().getPluginFilter(this.application.getConfig().toProperties());
        }


      public void refreshPlugin(String name) {
          if(hasGriffonPlugin(name)) {
              GriffonPlugin plugin = getGriffonPlugin(name);
              plugin.refresh();
          }
      }

    public Collection getPluginObservers(GriffonPlugin plugin) {
        if(plugin == null) throw new IllegalArgumentException("Argument [plugin] cannot be null");

        Collection c = (Collection)this.pluginToObserverMap.get(plugin.getName());

        // Add any wildcard observers.
        Collection wildcardObservers = (Collection)this.pluginToObserverMap.get("*");
        if(wildcardObservers != null) {
            if(c != null) {
                c.addAll(wildcardObservers);
            }
            else {
                c = wildcardObservers;
            }
        }

        if(c != null) {
            // Make sure this plugin is not observing itself!
            c.remove(plugin);
            return c;
        }
        return Collections.EMPTY_SET;
    }

    public void informObservers(String pluginName, Map event) {
        GriffonPlugin plugin = getGriffonPlugin(pluginName);
        if(plugin != null) {
            Collection observers = getPluginObservers(plugin);
            for (Iterator i = observers.iterator(); i.hasNext();) {
                GriffonPlugin observingPlugin = (GriffonPlugin) i.next();
                observingPlugin.notifyOfEvent(event);
            }
        }
    }


    /* (non-Javadoc)
    * @see org.codehaus.griffon.plugins.GriffonPluginManager#loadPlugins()
    */
  public void loadPlugins()
                  throws PluginException {
      if(!this.initialised) {
          GroovyClassLoader gcl = application.getClassLoader();

          attemptLoadPlugins(gcl);

          if(!delayedLoadPlugins.isEmpty()) {
              loadDelayedPlugins();
          }
          if(!delayedEvictions.isEmpty()) {
              processDelayedEvictions();
          }                                                   

          initializePlugins();
          initialised = true;
      }
  }


  private void attemptLoadPlugins(GroovyClassLoader gcl) {
      // retrieve load core plugins first
      List griffonCorePlugins = findCorePlugins();
      List griffonUserPlugins = findUserPlugins(gcl);

      List allPlugins = new ArrayList(griffonCorePlugins);
      allPlugins.addAll(griffonUserPlugins);

      //filtering applies to user as well as core plugins
      List filteredPlugins = getPluginFilter().filterPluginList(allPlugins);

      //make sure core plugins are loaded first
      List orderedCorePlugins = new ArrayList();
      List orderedUserPlugins = new ArrayList();

      for (Iterator iter = filteredPlugins.iterator(); iter.hasNext();) {
          GriffonPlugin plugin = (GriffonPlugin) iter.next();

          if (griffonCorePlugins.contains(plugin))
          {
              orderedCorePlugins.add(plugin);
          }
          else
          {
              orderedUserPlugins.add(plugin);
          }
      }

      List orderedPlugins = new ArrayList();
      orderedPlugins.addAll(orderedCorePlugins);
      orderedPlugins.addAll(orderedUserPlugins);

      for (Iterator iter = orderedPlugins.iterator(); iter.hasNext();) {
          GriffonPlugin plugin = (GriffonPlugin) iter.next();
          attemptPluginLoad(plugin);
      }
  }


  private List findCorePlugins() {
      CorePluginFinder finder = new CorePluginFinder(application);

      Set classes = finder.getPluginClasses();

      Iterator classesIterator = classes.iterator();
      List griffonCorePlugins = new ArrayList();

      while (classesIterator.hasNext())
      {
          Class pluginClass = (Class) classesIterator.next();

          if(pluginClass != null && !Modifier.isAbstract(pluginClass.getModifiers()) && pluginClass != DefaultGriffonPlugin.class) {
              GriffonPlugin plugin = new DefaultGriffonPlugin(pluginClass, application);
              plugin.setApplicationContext(applicationContext);
              griffonCorePlugins.add(plugin);
          }
        }
      return griffonCorePlugins;
  }


  private List findUserPlugins(GroovyClassLoader gcl) {
    List griffonUserPlugins = new ArrayList();

      LOG.info("Attempting to load ["+pluginResources.length+"] user defined plugins");
      for (int i = 0; i < pluginResources.length; i++) {
          Resource r = pluginResources[i];

          Class pluginClass = loadPluginClass(gcl, r);

          if(isGriffonPlugin(pluginClass)) {
              GriffonPlugin plugin = new DefaultGriffonPlugin(pluginClass, r, application);
              //attemptPluginLoad(plugin);
              griffonUserPlugins.add(plugin);
          }
          else {
              LOG.warn("Class ["+pluginClass+"] not loaded as plug-in. Griffon plug-ins must end with the convention 'GriffonPlugin'!");
          }
      }
      for (int i = 0; i < pluginClasses.length; i++) {
          Class pluginClass = pluginClasses[i];
          if(isGriffonPlugin(pluginClass)) {
              GriffonPlugin plugin = new DefaultGriffonPlugin(pluginClass, application);
              //attemptPluginLoad(plugin);
              griffonUserPlugins.add(plugin);
          }
          else {
              LOG.warn("Class ["+pluginClass+"] not loaded as plug-in. Griffon plug-ins must end with the convention 'GriffonPlugin'!");
          }

      }
    return griffonUserPlugins;
  }

    private boolean isGriffonPlugin(Class pluginClass) {
        return pluginClass != null && pluginClass.getName().endsWith(GRIFFON_PLUGIN_SUFFIX);
    }

    private void processDelayedEvictions() {
      for (Iterator i = delayedEvictions.keySet().iterator(); i.hasNext();) {
          GriffonPlugin plugin = (GriffonPlugin) i.next();
          String[] pluginToEvict = (String[])delayedEvictions.get(plugin);

          for (int j = 0; j < pluginToEvict.length; j++) {
              String pluginName = pluginToEvict[j];
              evictPlugin(plugin, pluginName);
          }
      }
  }

  private void initializePlugins() {
      for (Iterator i = plugins.values().iterator(); i.hasNext();) {
          Object plugin = i.next();
          if(plugin instanceof ApplicationContextAware) {
              ((ApplicationContextAware)plugin).setApplicationContext(applicationContext);
          }
      }
  }

  /**
   * This method will attempt to load that plug-ins not loaded in the first pass
   *
   */
  private void loadDelayedPlugins() {
      while(!delayedLoadPlugins.isEmpty()) {
          GriffonPlugin plugin = (GriffonPlugin)delayedLoadPlugins.remove(0);
          if(areDependenciesResolved(plugin)) {
              if(!hasValidPluginsToLoadBefore(plugin)) {
                  registerPlugin(plugin);
              }
              else {
                  delayedLoadPlugins.add(plugin);
              }
          }
          else {
              // ok, it still hasn't resolved the dependency after the initial
              // load of all plugins. All hope is not lost, however, so lets first
              // look inside the remaining delayed loads before giving up
              boolean foundInDelayed = false;
              for (Iterator i = delayedLoadPlugins.iterator(); i.hasNext();) {
                  GriffonPlugin remainingPlugin = (GriffonPlugin) i.next();
                  if(isDependentOn(plugin, remainingPlugin)) {
                      foundInDelayed = true;
                      break;
                  }
              }
              if(foundInDelayed)
                  delayedLoadPlugins.add(plugin);
              else {
                  failedPlugins.put(plugin.getName(),plugin);
                  LOG.warn("WARNING: Plugin ["+plugin.getName()+"] cannot be loaded because its dependencies ["+ArrayUtils.toString(plugin.getDependencyNames())+"] cannot be resolved");
              }

          }
      }
  }

  private boolean hasValidPluginsToLoadBefore(GriffonPlugin plugin) {
      String[] loadAfterNames = plugin.getLoadAfterNames();
      for (Iterator i = this.delayedLoadPlugins.iterator(); i.hasNext();) {
          GriffonPlugin other = (GriffonPlugin) i.next();
          for (int j = 0; j < loadAfterNames.length; j++) {
              String name = loadAfterNames[j];
              if(other.getName().equals(name)) {
                  return hasDelayedDependencies(other) || areDependenciesResolved(other);

              }
          }
      }
      return false;
  }

    private boolean hasDelayedDependencies(GriffonPlugin other) {
        String[] dependencyNames = other.getDependencyNames();
        for (int i = 0; i < dependencyNames.length; i++) {
            String dependencyName = dependencyNames[i];
            for (Iterator j = delayedLoadPlugins.iterator(); j.hasNext();) {
                GriffonPlugin griffonPlugin = (GriffonPlugin) j.next();
                if(griffonPlugin.getName().equals(dependencyName)) return true;
            }
        }
        return false;
    }


    /**
   * Checks whether the first plugin is dependant on the second plugin
   * @param plugin The plugin to check
   * @param dependancy The plugin which the first argument may be dependant on
   * @return True if it is
   */
  private boolean isDependentOn(GriffonPlugin plugin, GriffonPlugin dependancy) {
      String[] dependencies = plugin.getDependencyNames();
      for (int i = 0; i < dependencies.length; i++) {
          String name = dependencies[i];
          String requiredVersion = plugin.getDependentVersion(name);

          if(name.equals(dependancy.getName()) &&
                GriffonPluginUtils.isValidVersion(dependancy.getVersion(), requiredVersion))
              return true;
      }
      return false;
  }

  private boolean areDependenciesResolved(GriffonPlugin plugin) {
      String[] dependencies = plugin.getDependencyNames();
      if(dependencies.length > 0) {
          for (int i = 0; i < dependencies.length; i++) {
              String name = dependencies[i];
              String version = plugin.getDependentVersion(name);
              if(!hasGriffonPluginWithCompatibleVersion(name, version)) {
                  return false;
              }
          }
      }
      return true;
  }

  /**
   * Returns true if there are no plugins left that should, if possible, be loaded before this plugin
   *
   * @param plugin The plugin
   * @return True if there are
   */
  private boolean areNoneToLoadBefore(GriffonPlugin plugin) {
      String[] loadAfterNames = plugin.getLoadAfterNames();
      if(loadAfterNames.length > 0) {
          for (int i = 0; i < loadAfterNames.length; i++) {
              String name = loadAfterNames[i];
              if(getGriffonPlugin(name) == null)
                  return false;
          }
      }
      return true;
  }


  private Class loadPluginClass(GroovyClassLoader gcl, Resource r) {
      Class pluginClass;
      try {
          pluginClass = gcl.parseClass(r.getFile());
      } catch (CompilationFailedException e) {
          throw new PluginException("Error compiling plugin ["+r.getFilename()+"] " + e.getMessage(), e);
      } catch (IOException e) {
          throw new PluginException("Error reading plugin ["+r.getFilename()+"] " + e.getMessage(), e);
      }
      return pluginClass;
  }

  /**
   * This method attempts to load a plugin based on its dependencies. If a plugin's
   * dependencies cannot be resolved it will add it to the list of dependencies to
   * be resolved later
   *
   * @param plugin The plugin
   */
  private void attemptPluginLoad(GriffonPlugin plugin) {
      if(areDependenciesResolved(plugin) && areNoneToLoadBefore(plugin)) {
          registerPlugin(plugin);
      }
      else {
          delayedLoadPlugins.add(plugin);
      }
  }


  private void registerPlugin(GriffonPlugin plugin) {
      if(plugin.isEnabled()) {
          if(LOG.isInfoEnabled()) {
              LOG.info("Griffon plug-in ["+plugin.getName()+"] with version ["+plugin.getVersion()+"] loaded successfully");
          }

//          if(plugin instanceof ParentApplicationContextAware) {
//              ((ParentApplicationContextAware)plugin).setParentApplicationContext(parentCtx);
//          }
          plugin.setManager(this);
          String[] evictionNames = plugin.getEvictionNames();
          if(evictionNames.length > 0)
              delayedEvictions.put(plugin, evictionNames);

          String[] observedPlugins = plugin.getObservedPluginNames();
          for (int i = 0; i < observedPlugins.length; i++) {
              String observedPlugin = observedPlugins[i];
              Set observers = (Set)pluginToObserverMap.get(observedPlugin);
              if(observers == null) {
                  observers = new HashSet();
                  pluginToObserverMap.put(observedPlugin, observers);
              }
              observers.add(plugin);
          }
          pluginList.add(plugin);
          plugins.put(plugin.getName(), plugin);
      }
      else {
          if(LOG.isInfoEnabled()) {
              LOG.info("Griffon plugin " + plugin + " is disabled and was not loaded");
          }
      }
  }

  protected void evictPlugin(GriffonPlugin evictor, String evicteeName) {
      GriffonPlugin pluginToEvict = (GriffonPlugin)plugins.get(evicteeName);
      if(pluginToEvict!=null) {
          pluginList.remove(pluginToEvict);
          plugins.remove(pluginToEvict.getName());

          if(LOG.isInfoEnabled()) {
              LOG.info("Griffon plug-in "+pluginToEvict+" was evicted by " + evictor);
          }
      }
  }

  private boolean hasGriffonPlugin(String name, String version) {
      return getGriffonPlugin(name, version) != null;
  }
 
  private boolean hasGriffonPluginWithCompatibleVersion(String name, String version) {
      if(hasGriffonPlugin(name)) {
          GriffonPlugin plugin = getGriffonPlugin(name);
          int result = GriffonPluginUtils.compareVersions(plugin.getVersion(), version);
          return result >= 0;
      }
      return false;
  }

  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
      for (Iterator i = pluginList.iterator(); i.hasNext();) {
          GriffonPlugin plugin = (GriffonPlugin) i.next();
          plugin.setApplicationContext(applicationContext);
      }
  }

  public void setParentApplicationContext(ApplicationContext parent) {
      this.parentCtx = parent;
  }

  public void setApplication(GriffonContext application) {
      if(application == null) throw new IllegalArgumentException("Argument [application] cannot be null");
      this.application = application;
      for (Iterator i = pluginList.iterator(); i.hasNext();) {
          GriffonPlugin plugin = (GriffonPlugin) i.next();
          plugin.setApplication(application);

      }
  }

 void setPluginFilter(PluginFilter pluginFilter)
  {
      this.pluginFilter = pluginFilter;
  }

  private PluginFilter getPluginFilter() {
     if (pluginFilter == null)
     {
        pluginFilter = new IdentityPluginFilter();
     }
     return pluginFilter;
  }

  List getPluginList()
  {
      return Collections.unmodifiableList(pluginList);
  }
}
