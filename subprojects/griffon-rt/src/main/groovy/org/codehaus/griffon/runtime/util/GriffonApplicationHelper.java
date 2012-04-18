/*
 * Copyright 2008-2012 the original author or authors.
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
package org.codehaus.griffon.runtime.util;

import griffon.core.*;
import griffon.core.factories.AddonManagerFactory;
import griffon.core.factories.ArtifactManagerFactory;
import griffon.core.factories.MVCGroupManagerFactory;
import griffon.exceptions.GriffonException;
import griffon.util.*;
import groovy.lang.*;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.codehaus.griffon.runtime.core.ControllerArtifactHandler;
import org.codehaus.griffon.runtime.core.ModelArtifactHandler;
import org.codehaus.griffon.runtime.core.ServiceArtifactHandler;
import org.codehaus.griffon.runtime.core.ViewArtifactHandler;
import org.codehaus.griffon.runtime.logging.Log4jConfig;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static griffon.util.ConfigUtils.getConfigValueAsString;
import static griffon.util.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.eachLine;

/**
 * Utility class for bootstrapping an application and handling of MVC groups.</p>
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public class GriffonApplicationHelper {
    private static final Logger LOG = LoggerFactory.getLogger(GriffonApplicationHelper.class);

    private static final Map<String, String> DEFAULT_PLATFORM_HANDLERS = CollectionUtils.<String, String>map()
            .e("linux", "org.codehaus.griffon.runtime.util.DefaultLinuxPlatformHandler")
            .e("macosx", "org.codehaus.griffon.runtime.util.DefaultMacOSXPlatformHandler")
            .e("solaris", "org.codehaus.griffon.runtime.util.DefaultSolarisPlatformHandler")
            .e("windows", "org.codehaus.griffon.runtime.util.DefaultWindowsPlatformHandler");

    static {
        ExpandoMetaClassCreationHandle.enable();
    }

    /**
     * Creates, register and assigns an ExpandoMetaClass for a target class.<p>
     * The newly created metaClass will accept changes after initialization.
     *
     * @param clazz the target class
     * @return an ExpandoMetaClass
     */
    public static MetaClass expandoMetaClassFor(Class clazz) {
        MetaClass mc = GroovySystem.getMetaClassRegistry().getMetaClass(clazz);
        if (!(mc instanceof ExpandoMetaClass)) {
            mc = new ExpandoMetaClass(clazz, true, true);
            mc.initialize();
            GroovySystem.getMetaClassRegistry().setMetaClass(clazz, mc);
        }
        return mc;
    }

    private static ConfigObject loadConfig(ConfigSlurper configSlurper, Class configClass, String configFileName) {
        ConfigObject config = new ConfigObject();
        try {
            if (configClass != null) {
                config.merge(configSlurper.parse(configClass));
            }
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName + ".properties");
            if (is != null) {
                Properties p = new Properties();
                p.load(is);
                config.merge(configSlurper.parse(p));
            }
        } catch (Exception x) {
            LogLog.warn("Cannot read configuration [class: " + configClass + ", file: " + configFileName + "]", sanitize(x));
        }
        return config;
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
    public static void prepare(GriffonApplication app) {
        app.getBindings().setVariable("app", app);

        Metadata.getCurrent().getGriffonStartDir();
        Metadata.getCurrent().getGriffonWorkingDir();

        readAndSetConfiguration(app);
        app.event(GriffonApplication.Event.BOOTSTRAP_START.getName(), asList(app));

        applyPlatformTweaks(app);
        runLifecycleHandler(GriffonApplication.Lifecycle.INITIALIZE.getName(), app);
        initializeArtifactManager(app);
        initializeMvcManager(app);
        initializeAddonManager(app);

        app.event(GriffonApplication.Event.BOOTSTRAP_END.getName(), asList(app));
    }

    private static void readAndSetConfiguration(GriffonApplication app) {
        ConfigSlurper configSlurper = new ConfigSlurper(Environment.getCurrent().getName());
        app.setConfig(loadConfig(configSlurper, app.getAppConfigClass(), GriffonApplication.Configuration.APPLICATION.getName()));
        app.getConfig().merge(loadConfig(configSlurper, app.getConfigClass(), GriffonApplication.Configuration.CONFIG.getName()));
        GriffonExceptionHandler.configure(app.getConfig().flatten(new LinkedHashMap()));

        app.setBuilderConfig(loadConfig(configSlurper, app.getBuilderClass(), GriffonApplication.Configuration.BUILDER.getName()));

        Object events = safeNewInstance(app.getEventsClass());
        if (events != null) {
            app.setEventsConfig(events);
            app.addApplicationEventListener(app.getEventsConfig());
        }

        Object log4jConfig = app.getConfig().get("log4j");
        if (log4jConfig instanceof Closure) {
            app.event(GriffonApplication.Event.LOG4J_CONFIG_START.getName(), asList(log4jConfig));
            LogManager.resetConfiguration();
            new Log4jConfig().configure((Closure) log4jConfig);
        }
    }

    public static void applyPlatformTweaks(GriffonApplication app) {
        String platform = GriffonApplicationUtils.platform;
        String handlerClassName = getConfigValueAsString(app.getConfig(), "platform.handler." + platform, DEFAULT_PLATFORM_HANDLERS.get(platform));
        PlatformHandler platformHandler = (PlatformHandler) safeNewInstance(handlerClassName);
        platformHandler.handle(app);
    }

    private static final String KEY_ARTIFACT_MANAGER_FACTORY = "app.artifactManager.factory";
    private static final String DEFAULT_ARTIFACT_MANAGER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultArtifactManagerFactory";

    private static void initializeArtifactManager(GriffonApplication app) {
        if (app.getArtifactManager() == null) {
            String className = getConfigValueAsString(app.getConfig(), KEY_ARTIFACT_MANAGER_FACTORY, DEFAULT_ARTIFACT_MANAGER_FACTORY);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using " + className + " as ArtifactManagerFactory");
            }
            ArtifactManagerFactory factory = (ArtifactManagerFactory) safeNewInstance(className);
            InvokerHelper.setProperty(app, "artifactManager", factory.create(app));
        }

        // initialize default Artifact handlers
        app.getArtifactManager().registerArtifactHandler(new ModelArtifactHandler(app));
        app.getArtifactManager().registerArtifactHandler(new ViewArtifactHandler(app));
        app.getArtifactManager().registerArtifactHandler(new ControllerArtifactHandler(app));
        app.getArtifactManager().registerArtifactHandler(new ServiceArtifactHandler(app));

        // load additional handlers
        loadArtifactHandlers(app);

        app.getArtifactManager().loadArtifactMetadata();
    }

    private static final String KEY_ADDON_MANAGER_FACTORY = "app.addonManager.factory";
    private static final String DEFAULT_ADDON_MANAGER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultAddonManagerFactory";

    private static void initializeAddonManager(GriffonApplication app) {
        if (app.getAddonManager() == null) {
            String className = getConfigValueAsString(app.getConfig(), KEY_ADDON_MANAGER_FACTORY, DEFAULT_ADDON_MANAGER_FACTORY);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using " + className + " as AddonManagerFactory");
            }
            AddonManagerFactory factory = (AddonManagerFactory) safeNewInstance(className);
            InvokerHelper.setProperty(app, "addonManager", factory.create(app));
        }
        app.getAddonManager().initialize();
    }

    private static final String KEY_MVCGROUP_MANAGER_FACTORY = "app.mvcGroupManager.factory";
    private static final String DEFAULT_MVCGROUP_MANAGER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultMVCGroupManagerFactory";

    private static void initializeMvcManager(GriffonApplication app) {
        if (app.getMvcGroupManager() == null) {
            String className = getConfigValueAsString(app.getConfig(), KEY_MVCGROUP_MANAGER_FACTORY, DEFAULT_MVCGROUP_MANAGER_FACTORY);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Using " + className + " as MVCGroupManagerFactory");
            }
            MVCGroupManagerFactory factory = (MVCGroupManagerFactory) safeNewInstance(className);
            InvokerHelper.setProperty(app, "mvcGroupManager", factory.create(app));
        }

        Map<String, MVCGroupConfiguration> configurations = new LinkedHashMap<String, MVCGroupConfiguration>();
        Map<String, Map<String, Object>> mvcGroups = (Map<String, Map<String, Object>>) app.getConfig().get("mvcGroups");
        if (mvcGroups != null) {
            for (Map.Entry<String, Map<String, Object>> groupEntry : mvcGroups.entrySet()) {
                String type = groupEntry.getKey();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Adding MVC group " + type);
                }
                Map<String, Object> members = groupEntry.getValue();
                Map<String, Object> configMap = new LinkedHashMap<String, Object>();
                Map<String, String> membersCopy = new LinkedHashMap<String, String>();
                for (Object o : members.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String key = String.valueOf(entry.getKey());
                    if ("config".equals(key) && entry.getValue() instanceof Map) {
                        configMap = (Map<String, Object>) entry.getValue();
                    } else {
                        membersCopy.put(key, String.valueOf(entry.getValue()));
                    }
                }
                configurations.put(type, app.getMvcGroupManager().newMVCGroupConfiguration(type, membersCopy, configMap));
            }
        }

        app.getMvcGroupManager().initialize(configurations);
    }

    private static void loadArtifactHandlers(final GriffonApplication app) {
        Enumeration<URL> urls = null;

        try {
            urls = app.getClass().getClassLoader().getResources("META-INF/services/" + ArtifactHandler.class.getName());
        } catch (IOException ioe) {
            return;
        }

        if (urls == null) return;

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Reading " + ArtifactHandler.class.getName() + " definitions from " + url);
            }

            try {
                eachLine(url, new RunnableWithArgsClosure(new RunnableWithArgs() {
                    @Override
                    public void run(Object[] args) {
                        String line = (String) args[0];
                        if (line.startsWith("#") || isBlank(line)) return;
                        try {
                            Class artifactHandlerClass = loadClass(line);
                            Constructor ctor = artifactHandlerClass.getDeclaredConstructor(GriffonApplication.class);
                            ArtifactHandler handler = null;
                            if (ctor != null) {
                                handler = (ArtifactHandler) ctor.newInstance(app);
                            } else {
                                handler = (ArtifactHandler) safeNewInstance(artifactHandlerClass);
                            }
                            app.getArtifactManager().registerArtifactHandler(handler);
                        } catch (Exception e) {
                            if (LOG.isWarnEnabled()) {
                                LOG.warn("Could not load ArtifactHandler with " + line, sanitize(e));
                            }
                        }
                    }
                }));
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not load ArtifactHandler from " + url, sanitize(e));
                }
            }
        }
    }

    /**
     * Executes a script inside the UI Thread.<p>
     * On Swing this would be the Event Dispatch Thread.
     */
    public static void runLifecycleHandler(String handlerName, GriffonApplication app) {
        Class<?> handlerClass = null;
        try {
            handlerClass = loadClass(handlerName);
        } catch (ClassNotFoundException cnfe) {
            if (cnfe.getMessage().equals(handlerName)) {
                // the script must not exist, do nothing
                //LOGME - may be because of chained failures
                return;
            } else {
                throw new GriffonException(cnfe);
            }
        }

        if (Script.class.isAssignableFrom(handlerClass)) {
            doRunScript(handlerName, handlerClass, app);
        } else if (LifecycleHandler.class.isAssignableFrom(handlerClass)) {
            doRunLifecycleHandler(handlerName, handlerClass, app);
        }
    }

    private static void doRunScript(String scriptName, Class handlerClass, GriffonApplication app) {
        Script script = (Script) safeNewInstance(handlerClass);
        script.setBinding(app.getBindings());
        UIThreadManager.enhance(script);
        if (LOG.isInfoEnabled()) {
            LOG.info("Running lifecycle handler (script) '" + scriptName + "'");
        }
        UIThreadManager.getInstance().executeSync(script);
    }

    private static void doRunLifecycleHandler(String handlerName, Class handlerClass, GriffonApplication app) {
        LifecycleHandler handler = (LifecycleHandler) safeNewInstance(handlerClass);
        if (LOG.isInfoEnabled()) {
            LOG.info("Running lifecycle handler (class) '" + handlerName + "'");
        }
        UIThreadManager.getInstance().executeSync(handler);
    }

    /**
     * Creates a new instance of the specified class.<p>
     * Publishes a <strong>NewInstance</strong> event with the following arguments<ul>
     * <li>klass - the target Class</li>
     * <li>type - the type of the instance (i.e, 'controller','service')</li>
     * <li>instance - the newly created instance</li>
     * </ul>
     *
     * @param app   the current GriffonApplication
     * @param klass the target Class from which the instance will be created
     * @return a newly created instance of type klass
     */
    public static Object newInstance(GriffonApplication app, Class klass) {
        return newInstance(app, klass, "");
    }

    /**
     * Creates a new instance of the specified class.<p>
     * Publishes a <strong>NewInstance</strong> event with the following arguments<ul>
     * <li>klass - the target Class</li>
     * <li>type - the type of the instance (i.e, 'controller','service')</li>
     * <li>instance - the newly created instance</li>
     * </ul>
     *
     * @param app   the current GriffonApplication
     * @param klass the target Class from which the instance will be created
     * @param type  optional type parameter, used when publishing a 'NewInstance' event
     * @return a newly created instance of type klass
     */
    public static Object newInstance(GriffonApplication app, Class klass, String type) {
        if (isBlank(type)) type = "";
        if (LOG.isDebugEnabled()) {
            LOG.debug("Instantiating " + klass.getName() + " with type '" + type + "'");
        }

        Object instance = safeNewInstance(klass);

        GriffonClass griffonClass = app.getArtifactManager().findGriffonClass(klass);
        MetaClass mc = griffonClass != null ? griffonClass.getMetaClass() : expandoMetaClassFor(klass);
        enhance(app, klass, mc, instance);

        app.event(GriffonApplication.Event.NEW_INSTANCE.getName(), asList(klass, type, instance));
        return instance;
    }

    public static void enhance(GriffonApplication app, Class klass, MetaClass mc, Object instance) {
        try {
            InvokerHelper.invokeMethod(instance, "setApp", app);
        } catch (MissingMethodException mme) {
            try {
                InvokerHelper.setProperty(instance, "app", app);
            } catch (MissingPropertyException mpe) {
                if (mc instanceof ExpandoMetaClass) {
                    ((ExpandoMetaClass) mc).registerBeanProperty("app", app);
                }
            }
        }

        if (!GriffonArtifact.class.isAssignableFrom(klass)) {
            /*
            mc.createMVCGroup = {Object...args - >
                    app.mvcGroupManager.createMVCGroup( * args)
            }
            mc.buildMVCGroup = {Object...args - >
                    app.mvcGroupManager.buildMVCGroup( * args)
            }
            mc.destroyMVCGroup = {String mvcName - >
                    app.mvcGroupManager.destroyMVCGroup(mvcName)
            }
            mc.withMVCGroup = {Object...args - >
                    app.mvcGroupManager.withMVCGroup( * args)
            }
            mc.newInstance = {Object...args - >
                    GriffonApplicationHelper.newInstance(app, * args)
            }
            */
            UIThreadManager.enhance(mc);
        }
    }

    public static Class loadClass(String className) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null;

        ClassLoader cl = GriffonApplicationHelper.class.getClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        cl = Thread.currentThread().getContextClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        if (cnfe != null) throw cnfe;
        return null;
    }

    private static Object safeNewInstance(String className) {
        try {
            return loadClass(className).newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private static Object safeNewInstance(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
