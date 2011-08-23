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

import java.lang.reflect.Constructor
import org.apache.log4j.LogManager
import org.apache.log4j.helpers.LogLog
import org.codehaus.griffon.runtime.logging.Log4jConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import griffon.core.*
import griffon.util.*
import org.codehaus.griffon.runtime.core.*
import griffon.core.factories.ArtifactManagerFactory
import griffon.core.factories.AddonManagerFactory
import griffon.core.factories.MVCGroupManagerFactory

/**
 * Utility class for bootstrapping an application and handling of MVC groups.</p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
class GriffonApplicationHelper {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonApplicationHelper)

    private static final Map DEFAULT_PLATFORM_HANDLERS = [
            linux: 'org.codehaus.griffon.runtime.util.DefaultLinuxPlatformHandler',
            macosx: 'org.codehaus.griffon.runtime.util.DefaultMacOSXPlatformHandler',
            solaris: 'org.codehaus.griffon.runtime.util.DefaultSolarisPlatformHandler',
            windows: 'org.codehaus.griffon.runtime.util.DefaultWindowsPlatformHandler'
    ]

    /**
     * Creates, register and assigns an ExpandoMetaClass for a target class.<p>
     * The newly created metaClass will accept changes after initialization.
     *
     * @param clazz the target class
     * @return an ExpandoMetaClass
     */
    static MetaClass expandoMetaClassFor(Class clazz) {
        MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(clazz)
        if (!(mc instanceof ExpandoMetaClass)) {
            mc = new ExpandoMetaClass(clazz, true, true)
            mc.initialize()
            GroovySystem.getMetaClassRegistry().setMetaClass(clazz, mc)
        }
        return mc
    }

    private static ConfigObject loadConfig(ConfigSlurper configSlurper, Class configClass, String configFileName) {
        ConfigObject config = new ConfigObject()
        try {
            if (configClass != null) {
                config.merge(configSlurper.parse(configClass))
            }
            InputStream is = Thread.currentThread().contextClassLoader.getResourceAsStream(configFileName + '.properties')
            if (is != null) {
                Properties p = new Properties()
                p.load(is)
                config.merge(configSlurper.parse(p))
            }
        } catch (x) {
            LogLog.warn("Cannot read configuration [class: $configClass?.name, file: $configFileName]", GriffonExceptionHandler.sanitize(x))
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

        readAndSetConfiguration(app)
        app.event(GriffonApplication.Event.BOOTSTRAP_START.name, [app])

        applyPlatformTweaks(app)
        runLifecycleHandler(GriffonApplication.Lifecycle.INITIALIZE.name, app)
        initializeArtifactManager(app)
        initializeAddonManager(app)
        readMvcConfiguration(app)

        app.event(GriffonApplication.Event.BOOTSTRAP_END.name, [app])
    }

    private static void readAndSetConfiguration(GriffonApplication app) {
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
    }

    static void applyPlatformTweaks(GriffonApplication app) {
        String platform = GriffonApplicationUtils.platform
        String handlerClassName = app.config.platform.handler?.get(platform) ?: DEFAULT_PLATFORM_HANDLERS[platform]
        PlatformHandler platformHandler = loadClass(app, handlerClassName).newInstance()
        platformHandler.handle(app)
    }

    private static void initializeArtifactManager(GriffonApplication app) {
        if (!app.artifactManager) {
            String className = app.config.app.artifactManager.factory ?: 'org.codehaus.griffon.runtime.core.factories.DefaultArtifactManagerFactory'
            if (LOG.debugEnabled) LOG.debug("Using $className as ArtifactManagerFactory")
            ArtifactManagerFactory factory = loadClass(app, className).newInstance()
            app.artifactManager = factory.create(app)
        }

        // initialize default Artifact handlers
        app.artifactManager.with {
            registerArtifactHandler(new ModelArtifactHandler(app))
            registerArtifactHandler(new ViewArtifactHandler(app))
            registerArtifactHandler(new ControllerArtifactHandler(app))
            registerArtifactHandler(new ServiceArtifactHandler(app))
        }

        // load additional handlers
        loadArtifactHandlers(app)

        app.artifactManager.loadArtifactMetadata()
    }

    private static void initializeAddonManager(GriffonApplication app) {
        if (!app.addonManager) {
            String className = app.config.app.artifactManager.factory ?: 'org.codehaus.griffon.runtime.core.factories.DefaultAddonManagerFactory'
            if (LOG.debugEnabled) LOG.debug("Using $className as AddonManagerFactory")
            AddonManagerFactory factory = loadClass(app, className).newInstance()
            app.addonManager = factory.create(app)
        }
        app.addonManager.initialize()
    }

    private static void readMvcConfiguration(GriffonApplication app) {
        if (!app.mvcGroupManager) {
            String className = app.config.app.artifactManager.factory ?: 'org.codehaus.griffon.runtime.core.factories.DefaultMVCGroupManagerFactory'
            if (LOG.debugEnabled) LOG.debug("Using $className as MVCGroupManagerFactory")
            MVCGroupManagerFactory factory = loadClass(app, className).newInstance()
            app.mvcGroupManager = factory.create(app)
        }

        Map<String, MVCGroupConfiguration> configurations = [:]
        app.config.mvcGroups.each {String type, Map members ->
            if (LOG.debugEnabled) LOG.debug("Adding MVC group $type")
            Map membersCopy = members.inject([:]) {m, e -> m[e.key as String] = e.value as String; m}
            configurations.put(type, app.mvcGroupManager.newMVCGroupConfiguration(app, type, membersCopy))
        }

        app.mvcGroupManager.initialize(configurations)
    }

    private static void loadArtifactHandlers(GriffonApplication app) {
        Enumeration<URL> urls = null

        try {
            urls = app.class.classLoader.getResources('META-INF/services/' + ArtifactHandler.class.name)
        } catch (IOException ioe) {
            return
        }

        if (urls?.hasMoreElements()) {
            URL url = urls.nextElement()
            url.eachLine { line ->
                if (line.startsWith('#')) return
                try {
                    Class artifactHandlerClass = loadClass(app, line)
                    Constructor ctor = artifactHandlerClass.getDeclaredConstructor(GriffonApplication)
                    ArtifactHandler handler = ctor ? ctor.newInstance(app) : artifactHandlerClass.newInstance()
                    app.artifactManager.registerArtifactHandler(handler)
                } catch (Exception e) {
                    if (LOG.warnEnabled) LOG.warn("Could not load ArtifactHandler with '$line'", GriffonExceptionHandler.sanitize(e))
                }
            }
        }
    }

    /**
     * Sets a property value ignoring any MissingPropertyExceptions.<p>
     *
     * @param receiver the object where the property will be set
     * @param property the name of the property to set
     * @param value the value to set on the property
     */
    static void safeSet(receiver, property, value) {
        try {
            receiver."$property" = value
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
     * @deprecated use runLifecycleHandler instead
     */
    @Deprecated
    static void runScriptInsideUIThread(String scriptName, GriffonApplication app) {
        runLifecycleHandler(scriptName, app)
    }

    /**
     * Executes a script inside the UI Thread.<p>
     * On Swing this would be the Event Dispatch Thread.
     *
     */
    static void runLifecycleHandler(String handlerName, GriffonApplication app) {
        Class<?> handlerClass = null
        try {
            handlerClass = loadClass(app, handlerName)
        } catch (ClassNotFoundException cnfe) {
            if (cnfe.message == handlerName) {
                // the script must not exist, do nothing
                //LOGME - may be because of chained failures
                return
            } else {
                throw cnfe
            }
        }

        if (Script.class.isAssignableFrom(handlerClass)) {
            runScript(handlerName, handlerClass, app)
        } else if (LifecycleHandler.class.isAssignableFrom(handlerClass)) {
            runLifecycleHandler(handlerName, handlerClass, app)
        }
    }

    private static void runScript(String scriptName, Class handlerClass, GriffonApplication app) {
        Script script = handlerClass.newInstance(app.bindings)
        UIThreadManager.enhance(script)
        if (LOG.infoEnabled) LOG.info("Running lifecycle handler (script) '$scriptName'")
        UIThreadManager.instance.executeSync(script)
    }

    private static void runLifecycleHandler(String handlerName, Class handlerClass, GriffonApplication app) {
        LifecycleHandler handler = handlerClass.newInstance()
        if (LOG.infoEnabled) LOG.info("Running lifecycle handler (class) '$handlerName'")
        UIThreadManager.instance.executeSync(handler)
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
        if (LOG.debugEnabled) LOG.debug("Instantiating ${klass.name} with type '${type}'")
        def instance = klass.newInstance()

        GriffonClass griffonClass = app.artifactManager.findGriffonClass(klass)
        MetaClass mc = griffonClass?.getMetaClass() ?: expandoMetaClassFor(klass)
        enhance(app, klass, mc, instance)

        app.event(GriffonApplication.Event.NEW_INSTANCE.name, [klass, type, instance])
        return instance
    }

    static void enhance(GriffonApplication app, Class klass, MetaClass metaClass, Object instance) {
        try {
            instance.setApp(app)
        } catch (MissingMethodException mme) {
            try {
                instance.app = app
            } catch (MissingPropertyException mpe) {
                metaClass.app = app
            }
        }

        if (!GriffonMvcArtifact.isAssignableFrom(klass)) {
            metaClass.createMVCGroup = {Object... args ->
                GriffonApplicationHelper.createMVCGroup(app, * args)
            }
            metaClass.buildMVCGroup = {Object... args ->
                GriffonApplicationHelper.buildMVCGroup(app, * args)
            }
            metaClass.destroyMVCGroup = GriffonApplicationHelper.&destroyMVCGroup.curry(app)
            metaClass.withMVCGroup = {Object... args ->
                GriffonApplicationHelper.withMVCGroup(app, * args)
            }
        }

        if (!GriffonArtifact.isAssignableFrom(klass)) {
            metaClass.newInstance = {Object... args ->
                GriffonApplicationHelper.newInstance(app, * args)
            }
            // metaClass.getGriffonClass = {c ->
            //     app.artifactManager.findGriffonClass(c)
            // }.curry(klass)
            UIThreadManager.enhance(metaClass)
        }
    }

    static Class loadClass(GriffonApplication app, String className) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null

        for (cl in [app.class.classLoader, GriffonApplicationHelper.class.classLoader, Thread.currentThread().contextClassLoader]) {
            try {
                return cl.loadClass(className)
            } catch (ClassNotFoundException e) {
                cnfe = e
            }
        }

        if (cnfe) throw cnfe
    }

    private static instantiateManager(String className, GriffonApplication app) {
        Class klass = loadClass(app, className)
        klass.newInstance([app] as Object[])
    }
}
