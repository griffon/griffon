/**
 *
 */
package org.codehaus.griffon.plugins;

import groovy.lang.ExpandoMetaClass;
import org.codehaus.griffon.commons.GriffonContext;
import org.codehaus.griffon.commons.GriffonClassUtils;
//import org.codehaus.griffon.commons.spring.RuntimeSpringConfiguration;
import org.codehaus.griffon.plugins.exceptions.PluginException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
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
	protected ApplicationContext applicationContext;
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

//    /**
//	 * Base implementation that simply goes through the list of plugins and calls doWithRuntimeConfiguration on each
//	 * @param springConfig The RuntimeSpringConfiguration instance
//	 */
//	public void doRuntimeConfiguration(RuntimeSpringConfiguration springConfig) {
//		checkInitialised();
//		for (Iterator i = pluginList.iterator(); i.hasNext();) {
//			GriffonPlugin plugin = (GriffonPlugin) i.next();
//			plugin.doWithRuntimeConfiguration(springConfig);
//		}
//	}

//	/**
//	 * Base implementation that will perform runtime configuration for the specified plugin
//	 * name
//	 */
//     public void doRuntimeConfiguration(String pluginName, RuntimeSpringConfiguration springConfig) {
//          checkInitialised();
//          GriffonPlugin plugin = getGriffonPlugin(pluginName);
//          if(plugin == null) throw new PluginException("Plugin ["+pluginName+"] not found");
//
//          String[] dependencyNames = plugin.getDependencyNames();
//          doRuntimeConfigurationForDependencies(dependencyNames, springConfig);
//          String[] loadAfters = plugin.getLoadAfterNames();
//          for (int i = 0; i < loadAfters.length; i++) {
//              String name = loadAfters[i];
//              GriffonPlugin current = getGriffonPlugin(name);
//              if(current != null) {
//                  current.doWithRuntimeConfiguration(springConfig);
//              }
//          }
//          plugin.doWithRuntimeConfiguration(springConfig);
//      }

//      private void doRuntimeConfigurationForDependencies(String[] dependencyNames, RuntimeSpringConfiguration springConfig) {
//          for (int i = 0; i < dependencyNames.length; i++) {
//              String dn = dependencyNames[i];
//              GriffonPlugin current = getGriffonPlugin(dn);
//              if(current == null) throw new PluginException("Cannot load Plugin. Dependency ["+current+"] not found");
//              String[] pluginDependencies = current.getDependencyNames();
//              if(pluginDependencies.length > 0)
//                  doRuntimeConfigurationForDependencies(pluginDependencies, springConfig);
//              current.doWithRuntimeConfiguration(springConfig);
//          }
//      }

	/**
	 * Base implementation that will simply go through each plugin and call doWithApplicationContext
	 * on each
	 */
	public void doPostProcessing(ApplicationContext applicationContext) {
		checkInitialised();
		for (Iterator i = pluginList.iterator(); i.hasNext();) {
			GriffonPlugin plugin = (GriffonPlugin) i.next();
			plugin.doWithApplicationContext(applicationContext);
		}
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
	public void doDynamicMethods() {
		checkInitialised();
		Class[] allClasses = application.getAllClasses();
		if(allClasses != null) {
			ExpandoMetaClass[] metaClasses = new ExpandoMetaClass[allClasses.length];
			for (int i = 0; i < allClasses.length; i++) {
				Class c = allClasses[i];
                ExpandoMetaClass emc = new ExpandoMetaClass(c,true, true);
                emc.initialize();
                metaClasses[i] = emc;
			}
			for (Iterator i = pluginList.iterator(); i.hasNext();) {
				GriffonPlugin plugin = (GriffonPlugin) i.next();
				plugin.doWithDynamicMethods(applicationContext);
			}
		}
	}
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		for (Iterator i = pluginList.iterator(); i.hasNext();) {
			GriffonPlugin plugin = (GriffonPlugin) i.next();
			plugin.setApplicationContext(applicationContext);
		}
	}
	public void setApplication(GriffonContext application) {
		if(application == null) throw new IllegalArgumentException("Argument [application] cannot be null");
		this.application = application;
		for (Iterator i = pluginList.iterator(); i.hasNext();) {
			GriffonPlugin plugin = (GriffonPlugin) i.next();
			plugin.setApplication(application);
		}
	}

//    public void registerProvidedArtefacts(GriffonContext application) {
//        checkInitialised();
//        for (Iterator i = pluginList.iterator(); i.hasNext();) {
//            GriffonPlugin plugin = (GriffonPlugin) i.next();
//            final Class[] artefacts = plugin.getProvidedArtefacts();
//            for (int j = 0; j < artefacts.length; j++) {
//                Class artefact = artefacts[j];
//
//                String shortName = GriffonClassUtils.getShortName(artefact);
//                if(!isAlreadyRegistered(application, artefact, shortName)) {
//                    application.addArtefact(artefact);
//                }
//
//            }
//        }
//
//    }

    private boolean isAlreadyRegistered(GriffonContext application, Class artefact, String shortName) {
        return application.getClassForName(shortName) != null || application.getClassForName(artefact.getName()) != null;
    }

//    public void doArtefactConfiguration() {
//        checkInitialised();
//        for (Iterator i = pluginList.iterator(); i.hasNext();) {
//            GriffonPlugin plugin = (GriffonPlugin) i.next();
//            plugin.doArtefactConfiguration();
//        }
//    }

    public void shutdown() {
        checkInitialised();
        for (Iterator i = pluginList.iterator(); i.hasNext();) {
            GriffonPlugin plugin = (GriffonPlugin) i.next();
            plugin.notifyOfEvent(GriffonPlugin.EVENT_ON_SHUTDOWN, plugin);
        }
    }
}
