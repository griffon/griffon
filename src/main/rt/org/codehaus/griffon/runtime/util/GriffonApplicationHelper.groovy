/*
 * Copyright 2008-2011 the original author or authors.
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
package org.codehaus.griffon.runtime.util

import griffon.core.GriffonClass
import griffon.core.GriffonApplication
import griffon.core.ArtifactManager
import griffon.core.GriffonArtifact
import griffon.core.GriffonMvcArtifact
import org.codehaus.griffon.runtime.builder.UberBuilder
import org.codehaus.griffon.runtime.core.DefaultAddonManager
import org.codehaus.griffon.runtime.core.DefaultArtifactManager
import org.codehaus.griffon.runtime.core.ModelArtifactHandler
import org.codehaus.griffon.runtime.core.ViewArtifactHandler
import org.codehaus.griffon.runtime.core.ControllerArtifactHandler
import org.codehaus.griffon.runtime.core.ServiceArtifactHandler
import griffon.util.Metadata
import griffon.util.Environment
import griffon.util.UIThreadHelper
import griffon.util.GriffonExceptionHandler
import org.codehaus.groovy.runtime.InvokerHelper

import org.codehaus.griffon.runtime.logging.Log4jConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.log4j.LogManager
import org.apache.log4j.helpers.LogLog

/**
 * Utility class for boostrapping an application and handling of MVC groups.</p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class GriffonApplicationHelper {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonApplicationHelper)

    /**
     * Creates, register and assings an ExpandoMetaClass for a target class.<p>
     * The newly created metaClass will accept changes after initialization.
     *
     * @param clazz the target class
     * @return an ExpandoMetaClass
     */
    static MetaClass expandoMetaClassFor(Class clazz) {
        MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(clazz)
        if(!(mc instanceof ExpandoMetaClass)) {
            mc = new ExpandoMetaClass(clazz, true, true)
            mc.initialize()
            GroovySystem.getMetaClassRegistry().setMetaClass(clazz, mc)
        }
        return mc
    }

    private static ConfigObject loadConfig(ConfigSlurper configSlurper, Class configClass, String configFileName) {
        ConfigObject config = new ConfigObject()
        try {
            if(configClass != null) {
                config.merge(configSlurper.parse(configClass))
            }
            InputStream is = Thread.currentThread().contextClassLoader.getResourceAsStream(configFileName + '.properties')
            if(is != null) {
                Properties p = new Properties()
                p.load(is)
                config.merge(configSlurper.parse(p))
            }
        } catch(x) {
            LogLog.warn("Cannot read configuration [class: $configClass?.name, file: $configFileName]", x)
        }
        config
    }

    /**
     * Setups an application.<p>
     * This method performs the following tasks<ul>
     * <li>Sets "griffon.start.dir" as system property.</li>
     * <li>Calls the Initialize life cycle script.</li>
     * <li>Reads runtime and builder configuration.</li>
     * <li>Setups basic artifact handlers.</li>
     * <li>Initializes available addons.</li>
     * </ul>
     *
     * @param app the current Griffon application
     */
    static void prepare(GriffonApplication app) {
        app.bindings.app = app

        Metadata.current.getGriffonStartDir()
        Metadata.current.getGriffonWorkingDir()

        ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
        app.config = loadConfig(configSlurper, app.appConfigClass, GriffonApplication.Configuration.APPLICATION.name)
        app.config.merge(loadConfig(configSlurper, app.configClass, GriffonApplication.Configuration.CONFIG.name))
        GriffonExceptionHandler.configure(app.config.flatten([:]))

        app.builderConfig = loadConfig(configSlurper, app.builderClass, GriffonApplication.Configuration.BUILDER.name)

        def eventsClass = app.eventsClass
        if (eventsClass) {
            app.eventsConfig = eventsClass.newInstance()
            app.addApplicationEventListener(app.eventsConfig)
        }

        def log4jConfig = app.config.log4j
        if (log4jConfig instanceof Closure) {
            app.event(GriffonApplication.Event.LOG4J_CONFIG_START.name, [log4jConfig])
            LogManager.resetConfiguration()
            new Log4jConfig().configure(log4jConfig)
        }

        runScriptInsideUIThread(GriffonApplication.Lifecycle.INITIALIZE.name, app)

        if(!app.artifactManager) { 
            app.artifactManager = new DefaultArtifactManager(app)
        }
        app.artifactManager.with {
            registerArtifactHandler(new ModelArtifactHandler(app))
            registerArtifactHandler(new ViewArtifactHandler(app))
            registerArtifactHandler(new ControllerArtifactHandler(app))
            registerArtifactHandler(new ServiceArtifactHandler(app))
            loadArtifactMetadata()
        }

        if(!app.addonManager) {
            app.addonManager = new DefaultAddonManager(app)
        }
        app.addonManager.initialize()

        // copy mvc groups in config to app, casting to strings in a new map
        app.config.mvcGroups.each {k, v->
            if(LOG.debugEnabled) LOG.debug("Adding MVC group $k")
            app.addMvcGroup(k, v.inject([:]) {m, e -> m[e.key as String] = e.value as String; m})
        }

        app.event(GriffonApplication.Event.BOOTSTRAP_END.name, [app])
    }

    /**
     * Sets a property value ignoring any MissingPropertyExceptions.<p>
     *
     * @param receiver the objet where the property will be set
     * @param property the name of the property to set
     * @param value the value to set on the property
     */
    static void safeSet(reciever, property, value) {
        try {
            reciever."$property" = value
        } catch (MissingPropertyException mpe) {
            if (mpe.property != property) {
                throw mpe
            }
            /* else ignore*/
        }
    }

    /**
     * Executes a script inside the UI Thread.<p>
     * On Swing this would be the Event Dispatch Thread.
     *
     */
    static void runScriptInsideUIThread(String scriptName, GriffonApplication app) {
        def script
        try {
            script = loadClass(app, scriptName).newInstance(app.bindings)
        } catch (ClassNotFoundException cnfe) {
            if (cnfe.message == scriptName) {
                // the script must not exist, do nothing
                //LOGME - may be because of chained failures
                return
            } else {
                throw cnfe
            }
        }
        
        script.isUIThread = UIThreadHelper.instance.&isUIThread
        script.execAsync = UIThreadHelper.instance.&executeAsync
        script.execSync = UIThreadHelper.instance.&executeSync
        script.execOutside = UIThreadHelper.instance.&executeOutside
        script.execFuture = {Object... args -> UIThreadHelper.instance.executeFuture(*args) }

        if(LOG.infoEnabled) LOG.info("Running script '$scriptName'")
        UIThreadHelper.instance.executeSync(script)
    }

    /**
     * Creates a new instance of the specified class.<p>
     * Publishes a <strong>NewInstance</strong> event with the following arguments<ul>
     * <li>klass - the target Class</li>
     * <li>type - the type of the instance (i.e, 'controller','service')</li>
     * <li>instance - the newly created instance</li>
     * </ul>
     *
     * @param app the current GriffonApplication
     * @param klass the target Class from which the instance will be created
     * @param type optional type parameter, used when publishing a 'NewInstance' event
     *
     * @return a newly created instance of type klass
     */
    static Object newInstance(GriffonApplication app, Class klass, String type = '') {
        if(LOG.debugEnabled) LOG.debug("Instantiating ${klass.name} with type '${type}'")
        def instance = klass.newInstance()

        GriffonClass griffonClass = app.artifactManager.findGriffonClass(klass)
        MetaClass mc = griffonClass?.getMetaClass() ?: expandoMetaClassFor(klass)
        enhance(app, klass, mc, instance)

        app.event(GriffonApplication.Event.NEW_INSTANCE.name, [klass, type, instance])
        return instance
    }

    static createMVCGroup(GriffonApplication app, String mvcType) {
        createMVCGroup(app, mvcType, mvcType, [:])
    }

    static createMVCGroup(GriffonApplication app, String mvcType, String mvcName) {
        createMVCGroup(app, mvcType, mvcName, [:])
    }

    static createMVCGroup(GriffonApplication app, String mvcType, Map bindArgs) {
        createMVCGroup(app, mvcType, mvcType, bindArgs)
    }

    static createMVCGroup(GriffonApplication app, Map bindArgs, String mvcType, String mvcName) {
        createMVCGroup(app, mvcType, mvcName, bindArgs)
    }

    static createMVCGroup(GriffonApplication app, Map bindArgs, String mvcType) {
        createMVCGroup(app, mvcType, mvcType, bindArgs)
    }

    static createMVCGroup(GriffonApplication app, String mvcType, String mvcName, Map bindArgs) {
        Map results = buildMVCGroup(app, bindArgs, mvcType, mvcName)
        return [results.model, results.view, results.controller]
    }

    static Map buildMVCGroup(GriffonApplication app, String mvcType, String mvcName = mvcType) {
        buildMVCGroup(app, [:], mvcType, mvcName)
    }

    static Map buildMVCGroup(GriffonApplication app, Map bindArgs, String mvcType, String mvcName = mvcType) {
        if (!app.mvcGroups.containsKey(mvcType)) {
            throw new IllegalArgumentException("Unknown MVC type \"$mvcType\".  Known types are ${app.mvcGroups.keySet()}")
        }

        if(LOG.infoEnabled) LOG.info("Building MVC group '${mvcType}' with name '${mvcName}'")
        def argsCopy = [app:app, mvcType:mvcType, mvcName:mvcName]
        argsCopy.putAll(app.bindings.variables)
        argsCopy.putAll(bindArgs)

        // figure out what the classes are and prep the metaclass
        Map<String, MetaClass> metaClassMap = [:]
        Map<String, Class> klassMap = [:]
        Map<String, GriffonClass> griffonClassMap = [:]
        app.mvcGroups[mvcType].each {k, v ->
            GriffonClass griffonClass = app.artifactManager.findGriffonClass(v)
            Class klass = griffonClass?.clazz ?: loadClass(app, v)
            MetaClass metaClass = griffonClass?.getMetaClass() ?: klass.getMetaClass()

            metaClassMap[k] = metaClass
            klassMap[k] = klass
            griffonClassMap[k] = griffonClass
        }

        // create the builder
        UberBuilder builder = CompositeBuilderHelper.createBuilder(app, metaClassMap)
        argsCopy.each {k, v -> builder.setVariable k, v }

        // instantiate the parts
        def instanceMap = [:]
        klassMap.each {k, v ->
            if (argsCopy.containsKey(k)) {
                // use provided value, even if null
                instanceMap[k] = argsCopy[k]
            } else {
                // otherwise create a new value
                GriffonClass griffonClass = griffonClassMap[k]
                // def instance = griffonClass?.newInstance() ?: newInstance(app, v, k)
                def instance = null
                if(griffonClass) {
                    instance = griffonClass.newInstance()
                } else {
                    instance = newInstance(app, v, k)
                }
                instanceMap[k] = instance
                argsCopy[k] = instance

                // all scripts get the builder as their binding
                if (instance instanceof Script) {
                    instance.binding = builder
                }
            }
        }
        instanceMap.builder = builder
        argsCopy.builder = builder
        
        // special case --
        // controllers are added as application listeners
        // addApplicationListener method is null safe
        app.addApplicationEventListener(instanceMap.controller)

        // mutually set each other to the available fields and inject args
        instanceMap.each {k, v ->
            // loop on the instance map to get just the instances
            if (v instanceof Script)  {
                v.binding.variables.putAll(argsCopy)
            } else {
                // set the args and instances
                InvokerHelper.setProperties(v, argsCopy)
            }
        }

        // store the refs in the app caches
        app.models[mvcName] = instanceMap.model
        app.views[mvcName] = instanceMap.view
        app.controllers[mvcName] = instanceMap.controller
        app.builders[mvcName] = instanceMap.builder
        app.groups[mvcName] = instanceMap

        // initialize the classes and call scripts
        if(LOG.debugEnabled) LOG.debug("Initializing each MVC member of group '${mvcName}'")
        instanceMap.each {k, v ->
            if (v instanceof Script) {
                // special case: view gets executed in the UI thread always
                if (k == 'view') {
                    UIThreadHelper.instance.executeSync { builder.build(v) }
                } else {
                    // non-view gets built in the builder
                    // they can switch into the UI thread as desired
                    builder.build(v)
                }
            } else if (k != 'builder') {
                try {
                    v.mvcGroupInit(argsCopy)
                } catch (MissingMethodException mme) {
                    if (mme.method != 'mvcGroupInit') {
                        throw mme
                    }
                    // MME on mvcGroupInit means they didn't define
                    // an init method.  This is not an error.
                }
            }
        }

        app.event(GriffonApplication.Event.CREATE_MVC_GROUP.name, [mvcType, mvcName, instanceMap])
        return instanceMap
    }

    static enhance(GriffonApplication app, Class klass, MetaClass metaClass, Object instance) {
        try {
            instance.setApp(app)
        } catch(MissingMethodException mme) {
            try {
                instance.app = app
            } catch(MissingPropertyException mpe) {
                metaClass.app = app
            }
        }

        if(!GriffonMvcArtifact.isAssignableFrom(klass)) {
            metaClass.createMVCGroup = {Object... args ->
                GriffonApplicationHelper.createMVCGroup(app, *args)
            }
            metaClass.buildMVCGroup = {Object... args ->
                GriffonApplicationHelper.buildMVCGroup(app, *args)
            }
            metaClass.destroyMVCGroup = GriffonApplicationHelper.&destroyMVCGroup.curry(app)
        }

        if(!GriffonArtifact.isAssignableFrom(klass)) {
            metaClass.newInstance = {Object... args ->
                GriffonApplicationHelper.newInstance(app, *args)
            }
            // metaClass.getGriffonClass = {c ->
            //     app.artifactManager.findGriffonClass(c)
            // }.curry(klass)
            UIThreadHelper.enhance(metaClass)
        }
    }

    /**
     * Cleanups and removes an MVC group from the application.<p>
     * Calls <strong>mvcGroupDestroy()</strong> on model, view and controller
     * if the method is defined.<p>
     * Publishes a <strong>DestroyMVCGroup</strong> event with the following arguments<ul>
     * <li>mvcName - the name of the group</li>
     * </ul>
     *
     * @param app the current Griffon application
     * @param mvcName name of the group to destroy
     */
    static destroyMVCGroup(GriffonApplication app, String mvcName) {
        if(LOG.infoEnabled) LOG.info("Destroying MVC group identified by '$mvcName'")
        app.removeApplicationEventListener(app.controllers[mvcName])
        [app.models, app.views, app.controllers].each {
            def part = it.remove(mvcName)
            if ((part != null)  & !(part instanceof Script)) {
                try {
                    part.mvcGroupDestroy()
                } catch (MissingMethodException mme) {
                    if (mme.method != 'mvcGroupDestroy') {
                        throw mme
                    }
                    // MME on mvcGroupDestroy means they didn't define
                    // a destroy method.  This is not an error.
                }
            }
        }

        try {
            app.builders[mvcName]?.dispose()
        } catch(MissingMethodException mme) {
            // TODO find out why this call breaks applet mode on shutdown
            if(LOG.errorEnabled) LOG.error("Application encountered an error while destroying group '$mvcName'", mme)
        }

        // remove the refs from the app caches
        app.models.remove(mvcName)
        app.views.remove(mvcName)
        app.controllers.remove(mvcName)
        app.builders.remove(mvcName)
        app.groups.remove(mvcName)

        app.event(GriffonApplication.Event.DESTROY_MVC_GROUP.name, [mvcName])
    }

    static Class loadClass(GriffonApplication app, String className) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null

        for(cl in [app.class.classLoader, GriffonApplicationHelper.class.classLoader, Thread.currentThread().contextClassLoader]) {
            try {
                return cl.loadClass(className)
            } catch(ClassNotFoundException e) {
                cnfe = e
            }
        }

        if(cnfe) throw cnfe
    }
}
