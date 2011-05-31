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

import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * @author Graeme Rocher (Grails 0.4)
 */
public class DefaultGriffonPluginManager extends AbstractGriffonPluginManager implements GriffonPluginManager {

    private static final Log LOG = LogFactory.getLog(DefaultGriffonPluginManager.class);
    private static final Class[] COMMON_CLASSES =
            new Class[]{Boolean.class, Byte.class, Character.class, Class.class, Double.class,Float.class, Integer.class, Long.class,
                        Number.class, Short.class, String.class, BigInteger.class, BigDecimal.class, URL.class, URI.class};

    private List delayedLoadPlugins = new LinkedList();
    private PathMatchingResourcePatternResolver resolver;

    private List<GriffonPlugin> userPlugins = new ArrayList<GriffonPlugin>();

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
          this.application = application;
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
      }

      public DefaultGriffonPluginManager(Class[] plugins, GriffonContext application) throws IOException {
          super(application);
          this.pluginClasses = plugins;
          resolver = new PathMatchingResourcePatternResolver();
          this.application = application;
      }

      public DefaultGriffonPluginManager(Resource[] pluginFiles, GriffonContext application) {
          super(application);
          resolver = new PathMatchingResourcePatternResolver();
          this.pluginResources = pluginFiles;
          this.application = application;
      }

      public void loadPlugins()
                      throws PluginException {
          if(!this.initialised) {
              GroovyClassLoader gcl = application.getClassLoader();

              attemptLoadPlugins(gcl);

              if(!delayedLoadPlugins.isEmpty()) {
                  loadDelayedPlugins();
              }

              initialised = true;
          }
      }

      private void attemptLoadPlugins(GroovyClassLoader gcl) {
          List userPlugins = findUserPlugins(gcl);

          for (Iterator iter = userPlugins.iterator(); iter.hasNext();) {
              GriffonPlugin plugin = (GriffonPlugin) iter.next();
              attemptPluginLoad(plugin);
          }
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
        return pluginClass != null && pluginClass.getName().endsWith(GriffonPluginInfo.TRAILING_NAME);
    }

    /**
     * This method will attempt to load that plug-ins not loaded in the first pass
     *
     */
    private void loadDelayedPlugins() {
        while(!delayedLoadPlugins.isEmpty()) {
            GriffonPlugin plugin = (GriffonPlugin)delayedLoadPlugins.remove(0);
            if(areDependenciesResolved(plugin)) {
                registerPlugin(plugin);
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
        if(areDependenciesResolved(plugin)) {
            registerPlugin(plugin);
        }
        else {
            delayedLoadPlugins.add(plugin);
        }
    }

    private void registerPlugin(GriffonPlugin plugin) {
        if(LOG.isInfoEnabled()) {
            LOG.info("Griffon plug-in ["+plugin.getName()+"] with version ["+plugin.getVersion()+"] loaded successfully");
        }

        plugin.setManager(this);
        pluginList.add(plugin);
        plugins.put(plugin.getName(), plugin);
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

    public void setApplication(GriffonContext application) {
        if(application == null) throw new IllegalArgumentException("Argument [application] cannot be null");
        this.application = application;
        for (Iterator i = pluginList.iterator(); i.hasNext();) {
            GriffonPlugin plugin = (GriffonPlugin) i.next();
            plugin.setApplication(application);

        }
    }

    List getPluginList() {
        return Collections.unmodifiableList(pluginList);
    }
}
