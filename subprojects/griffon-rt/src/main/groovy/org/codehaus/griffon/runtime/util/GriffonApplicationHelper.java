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
import griffon.core.controller.GriffonControllerAction;
import griffon.core.controller.GriffonControllerActionManager;
import griffon.core.factories.*;
import griffon.core.resources.ResourcesInjector;
import griffon.exceptions.GriffonException;
import griffon.util.*;
import groovy.lang.*;
import groovy.util.ConfigObject;
import groovy.util.FactoryBuilderSupport;
import org.apache.log4j.LogManager;
import org.codehaus.griffon.runtime.core.ControllerArtifactHandler;
import org.codehaus.griffon.runtime.core.ModelArtifactHandler;
import org.codehaus.griffon.runtime.core.ServiceArtifactHandler;
import org.codehaus.griffon.runtime.core.ViewArtifactHandler;
import org.codehaus.griffon.runtime.core.controller.NoopGriffonControllerActionManager;
import org.codehaus.griffon.runtime.logging.Log4jConfig;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

import static griffon.util.ConfigUtils.*;
import static griffon.util.GriffonExceptionHandler.handleThrowable;
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
        .e("linux64", "org.codehaus.griffon.runtime.util.DefaultLinuxPlatformHandler")
        .e("macosx", "org.codehaus.griffon.runtime.util.DefaultMacOSXPlatformHandler")
        .e("macosx64", "org.codehaus.griffon.runtime.util.DefaultMacOSXPlatformHandler")
        .e("solaris", "org.codehaus.griffon.runtime.util.DefaultSolarisPlatformHandler")
        .e("windows", "org.codehaus.griffon.runtime.util.DefaultWindowsPlatformHandler")
        .e("windows64", "org.codehaus.griffon.runtime.util.DefaultWindowsPlatformHandler");

    private static final String LOCATION_CLASSPATH = "classpath:";
    private static final String LOCATION_FILE = "file:";
    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String GROOVY_SUFFIX = ".groovy";

    private static final String KEY_MESSAGE_SOURCE_FACTORY = "app.messageSource.factory";
    private static final String KEY_RESOURCES_INJECTOR_FACTORY = "app.resourceInjector.factory";
    private static final String KEY_EVENT_ROUTER_FACTORY = "app.eventRouter.factory";
    private static final String KEY_ADDON_MANAGER_FACTORY = "app.addonManager.factory";
    private static final String KEY_ARTIFACT_MANAGER_FACTORY = "app.artifactManager.factory";
    private static final String KEY_ACTION_MANAGER_FACTORY = "app.actionManager.factory";
    private static final String KEY_MVCGROUP_MANAGER_FACTORY = "app.mvcGroupManager.factory";

    private static final String KEY_APP_LIFECYCLE_HANDLER_DISABLE = "app.lifecycle.handler.disable";
    private static final String KEY_GRIFFON_ACTION_MANAGER_DISABLE = "griffon.action.manager.disable";

    private static final String DEFAULT_MESSAGE_SOURCE_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultMessageSourceFactory";
    private static final String DEFAULT_RESOURCES_INJECTOR_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultResourcesInjectorFactory";
    private static final String DEFAULT_EVENT_ROUTER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultEventRouterFactory";
    private static final String DEFAULT_ADDON_MANAGER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultAddonManagerFactory";
    private static final String DEFAULT_ARTIFACT_MANAGER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultArtifactManagerFactory";
    private static final String DEFAULT_MVCGROUP_MANAGER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultMVCGroupManagerFactory";


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

        initializeMessageSource(app);
        initializeResourceResolver(app);
        initializeResourcesInjector(app);
        initializePropertyEditors(app);

        applyPlatformTweaks(app);
        runLifecycleHandler(GriffonApplication.Lifecycle.INITIALIZE.getName(), app);
        initializeArtifactManager(app);
        initializeMvcManager(app);
        initializeAddonManager(app);
        initializeActionManager(app);

        app.event(GriffonApplication.Event.BOOTSTRAP_END.getName(), asList(app));
    }

    private static ConfigObject doLoadConfig(ConfigReader configReader, Class configClass, String configFileName) {
        if (configClass != null) configFileName = configClass.getSimpleName();
        return loadConfig(configReader, configClass, configFileName);
    }

    private static ConfigObject doLoadConfigWithI18n(Locale locale, ConfigReader configReader, Class configClass, String configFileName) {
        if (configClass != null) configFileName = configClass.getSimpleName();
        return loadConfigWithI18n(locale, configReader, configClass, configFileName);
    }

    private static void readAndSetConfiguration(final GriffonApplication app) {
        ConfigReader configReader = createConfigReader();

        ConfigObject appConfig = doLoadConfig(configReader, app.getAppConfigClass(), GriffonApplication.Configuration.APPLICATION.getName());
        setApplicationLocale(app, getConfigValue(appConfig, "application.locale", Locale.getDefault()));
        appConfig = doLoadConfigWithI18n(app.getLocale(), configReader, app.getAppConfigClass(), GriffonApplication.Configuration.APPLICATION.getName());
        app.setConfig(appConfig);
        app.getConfig().merge(doLoadConfigWithI18n(app.getLocale(), configReader, app.getConfigClass(), GriffonApplication.Configuration.CONFIG.getName()));

        Object log4jConfig = app.getConfig().get("log4j");
        if (log4jConfig instanceof Closure) {
            LogManager.resetConfiguration();
            new Log4jConfig().configure((Closure) log4jConfig);
        }

        loadExternalConfig(app, configReader);
        GriffonExceptionHandler.configure(app.getConfig().flatten(new LinkedHashMap()));

        app.setBuilderConfig(doLoadConfigWithI18n(app.getLocale(), configReader, app.getBuilderClass(), GriffonApplication.Configuration.BUILDER.getName()));

        initializeEventRouter(app);

        Object events = safeNewInstance(app.getEventsClass(), false);
        if (events != null) {
            app.setEventsConfig(events);
            app.addApplicationEventListener(app.getEventsConfig());
        }

        log4jConfig = app.getConfig().get("log4j");
        if (log4jConfig instanceof Closure) {
            app.event(GriffonApplication.Event.LOG4J_CONFIG_START.getName(), asList(log4jConfig));
            LogManager.resetConfiguration();
            new Log4jConfig().configure((Closure) log4jConfig);
        }
    }

    private static void loadExternalConfig(GriffonApplication app, ConfigReader configReader) {
        List<String> locations = (List<String>) getConfigValue(app.getConfig(), "griffon.config.locations", Collections.emptyList());
        for (String location : locations) {
            boolean groovyScriptAllowed = false;

            String parsedLocation = location;
            if (location.startsWith(LOCATION_CLASSPATH)) {
                parsedLocation = location.substring(LOCATION_CLASSPATH.length()).trim();
            } else if (location.startsWith(LOCATION_FILE)) {
                parsedLocation = location.substring(LOCATION_FILE.length()).trim();
            } else {
                // assume it's a class definition
                groovyScriptAllowed = true;
            }

            if (groovyScriptAllowed) {
                Class locationScriptClass = safeLoadClass(parsedLocation);
                if (locationScriptClass != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Loading external configuration location '" + location + "'.");
                    }
                    app.getConfig().merge(loadConfigWithI18n(app.getLocale(), configReader, locationScriptClass, null));
                } else {
                    // invalid location. Log & skip
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Skipping invalid external configuration location '" + location + "'.");
                    }
                }
            } else if (parsedLocation.endsWith(PROPERTIES_SUFFIX) || parsedLocation.endsWith(GROOVY_SUFFIX)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Loading external configuration location '" + location + "'.");
                }
                app.getConfig().merge(loadConfigWithI18n(app.getLocale(), configReader, null, parsedLocation));
            } else {
                // invalid location. Log & skip
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Skipping invalid external configuration location '" + location + "'.");
                }
            }
        }
    }

    private static void initializeMessageSource(GriffonApplication app) {
        String className = getConfigValueAsString(app.getConfig(), KEY_MESSAGE_SOURCE_FACTORY, DEFAULT_MESSAGE_SOURCE_FACTORY);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using " + className + " as MessageSourceFactory");
        }
        MessageSourceFactory factory = (MessageSourceFactory) safeNewInstance(className);
        InvokerHelper.setProperty(app, "messageSource", factory.create(app));
    }

    private static final String KEY_RESOURCE_RESOLVER_FACTORY = "app.resourceResolver.factory";
    private static final String DEFAULT_RESOURCE_RESOLVER_FACTORY = "org.codehaus.griffon.runtime.core.factories.DefaultResourceResolverFactory";

    private static void initializeResourceResolver(GriffonApplication app) {
        String className = getConfigValueAsString(app.getConfig(), KEY_RESOURCE_RESOLVER_FACTORY, DEFAULT_RESOURCE_RESOLVER_FACTORY);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using " + className + " as ResourceResolverFactory");
        }
        ResourceResolverFactory factory = (ResourceResolverFactory) safeNewInstance(className);
        InvokerHelper.setProperty(app, "resourceResolver", factory.create(app));
    }

    private static void initializeResourcesInjector(GriffonApplication app) {
        String className = getConfigValueAsString(app.getConfig(), KEY_RESOURCES_INJECTOR_FACTORY, DEFAULT_RESOURCES_INJECTOR_FACTORY);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using " + className + " as ResourcesInjectorFactory");
        }
        ResourcesInjectorFactory factory = (ResourcesInjectorFactory) safeNewInstance(className);
        final ResourcesInjector injector = factory.create(app);
        app.addApplicationEventListener(GriffonApplication.Event.NEW_INSTANCE.getName(), new RunnableWithArgs() {
            public void run(Object[] args) {
                Object instance = args[2];
                injector.injectResources(instance);
            }
        });
    }

    private static void initializePropertyEditors(GriffonApplication app) {
        Enumeration<URL> urls = null;

        try {
            urls = ApplicationClassLoader.get().getResources("META-INF/services/" + PropertyEditor.class.getName());
        } catch (IOException ioe) {
            return;
        }

        if (urls == null) return;

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Reading " + PropertyEditor.class.getName() + " definitions from " + url);
            }

            try {
                eachLine(url, new RunnableWithArgsClosure(new RunnableWithArgs() {
                    @Override
                    public void run(Object[] args) {
                        String line = (String) args[0];
                        if (line.startsWith("#") || isBlank(line)) return;
                        try {
                            String[] parts = line.trim().split("=");
                            Class targetType = loadClass(parts[0].trim());
                            Class editorClass = loadClass(parts[1].trim());
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Registering " + editorClass.getName() + " as editor for " + targetType.getName());
                            }
                            PropertyEditorManager.registerEditor(targetType, editorClass);
                        } catch (Exception e) {
                            if (LOG.isWarnEnabled()) {
                                LOG.warn("Could not load PropertyEditor with " + line, sanitize(e));
                            }
                        }
                    }
                }));
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not load PropertyEditor definitions from " + url, sanitize(e));
                }
            }
        }
    }

    private static void initializeEventRouter(GriffonApplication app) {
        InvokerHelper.setProperty(app, "eventRouter", createEventRouter(app));
    }

    public static EventRouter createEventRouter(GriffonApplication app) {
        String className = getConfigValueAsString(app.getConfig(), KEY_EVENT_ROUTER_FACTORY, DEFAULT_EVENT_ROUTER_FACTORY);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using " + className + " as EventRouterFactory");
        }
        EventRouterFactory factory = (EventRouterFactory) safeNewInstance(className);
        return factory.create(app);
    }

    private static void setApplicationLocale(GriffonApplication app, Object localeValue) {
        if (localeValue instanceof Locale) {
            app.setLocale((Locale) localeValue);
        } else if (localeValue instanceof CharSequence) {
            app.setLocale(parseLocale(String.valueOf(localeValue)));
        }
    }

    public static Locale parseLocale(String locale) {
        if (isBlank(locale)) return Locale.getDefault();
        String[] parts = locale.split("_");
        switch (parts.length) {
            case 1:
                return new Locale(parts[0]);
            case 2:
                return new Locale(parts[0], parts[1]);
            case 3:
                return new Locale(parts[0], parts[1], parts[2]);
            default:
                return Locale.getDefault();
        }
    }

    public static void applyPlatformTweaks(GriffonApplication app) {
        String platform = GriffonApplicationUtils.platform;
        String handlerClassName = getConfigValueAsString(app.getConfig(), "platform.handler." + platform, DEFAULT_PLATFORM_HANDLERS.get(platform));
        PlatformHandler platformHandler = (PlatformHandler) safeNewInstance(handlerClassName);
        platformHandler.handle(app);
    }

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
        if (!ServiceArtifactHandler.isBasicInjectionDisabled()) {
            app.getArtifactManager().registerArtifactHandler(new ServiceArtifactHandler(app));
        }

        // load additional handlers
        loadArtifactHandlers(app);

        app.getArtifactManager().loadArtifactMetadata();
    }

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

    private static void initializeActionManager(GriffonApplication app) {
        InvokerHelper.setProperty(app, "actionManager", new NoopGriffonControllerActionManager(app));

        boolean disableActionManager = getConfigValueAsBoolean(app.getConfig(), KEY_GRIFFON_ACTION_MANAGER_DISABLE, false);
        if (disableActionManager) {
            if (LOG.isInfoEnabled()) {
                LOG.info("GriffonControllerActionManager is disabled.");
            }
            return;
        }

        String className = getConfigValueAsString(app.getConfig(), KEY_ACTION_MANAGER_FACTORY, null);
        if (isBlank(className) || "null".equals(className)) {
            URL url = ApplicationClassLoader.get().getResource("META-INF/services/" + GriffonControllerActionManagerFactory.class.getName());
            if (null == url) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("GriffonControllerActionManager is disabled.");
                }
                return;
            }
            try {
                className = DefaultGroovyMethods.getText(url).trim();
            } catch (IOException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cannot read GriffonControllerActionManager definition from " + url, sanitize(e));
                    className = null;
                }
            }
        }

        if (isBlank(className)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("GriffonControllerActionManager is disabled.");
            }
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Using " + className + " as GriffonControllerActionManagerFactory");
        }
        GriffonControllerActionManagerFactory factory = (GriffonControllerActionManagerFactory) safeNewInstance(className);
        final GriffonControllerActionManager actionManager = factory.create(app);
        InvokerHelper.setProperty(app, "actionManager", actionManager);

        app.addApplicationEventListener(GriffonApplication.Event.NEW_INSTANCE.getName(), new RunnableWithArgs() {
            public void run(Object[] args) {
                String type = (String) args[1];
                if (GriffonControllerClass.TYPE.equals(type)) {
                    GriffonController controller = (GriffonController) args[2];
                    actionManager.createActions(controller);
                }
            }
        });

        app.addApplicationEventListener(GriffonApplication.Event.INITIALIZE_MVC_GROUP.getName(), new RunnableWithArgs() {
            public void run(Object[] args) {
                MVCGroupConfiguration groupConfig = (MVCGroupConfiguration) args[0];
                MVCGroup group = (MVCGroup) args[1];
                GriffonController controller = group.getController();
                if (controller == null) return;
                FactoryBuilderSupport builder = group.getBuilder();
                Map<String, GriffonControllerAction> actions = actionManager.actionsFor(controller);
                for (Map.Entry<String, GriffonControllerAction> action : actions.entrySet()) {
                    String actionKey = actionManager.normalizeName(action.getKey()) + GriffonControllerActionManager.ACTION;
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Adding action " + actionKey + " to " + groupConfig.getMvcType() + ":" + group.getMvcId() + ":builder");
                    }
                    builder.setVariable(actionKey, action.getValue().getToolkitAction());
                }
            }
        });
    }


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
            urls = ApplicationClassLoader.get().getResources("META-INF/services/" + ArtifactHandler.class.getName());
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
        boolean skipHandler = getConfigValueAsBoolean(app.getConfig(), KEY_APP_LIFECYCLE_HANDLER_DISABLE, false);
        if(skipHandler && LOG.isDebugEnabled()) {
            LOG.info("Lifecycle handler '" + handlerName + "' has been disabled. SKIPPING.");
            return;
        }

        Class<?> handlerClass = null;
        try {
            handlerClass = loadConfigurationalClass(handlerName);
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

        Object instance = null;
        try {
            instance = klass.newInstance();
        } catch (InstantiationException e) {
            throw new GriffonException(e);
        } catch (IllegalAccessException e) {
            throw new GriffonException(e);
        }

        // GRIFFON-535
        if (instance != null) {
            GriffonClass griffonClass = app.getArtifactManager().findGriffonClass(klass);
            MetaClass mc = griffonClass != null ? griffonClass.getMetaClass() : expandoMetaClassFor(klass);
            enhance(app, klass, mc, instance);
            app.event(GriffonApplication.Event.NEW_INSTANCE.getName(), asList(klass, type, instance));
        }
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
            UIThreadManager.enhance(mc);
        }
    }

    public static Class<?> loadConfigurationalClass(String className) throws ClassNotFoundException {
        if (!className.contains(".")) {
            String fixedClassName = "config." + className;
            try {
                return loadClass(fixedClassName);
            } catch (ClassNotFoundException cnfe) {
                if (cnfe.getMessage().equals(fixedClassName)) {
                    return loadClass(className);
                } else {
                    throw new GriffonException(cnfe);
                }
            }
        }
        return loadClass(className);
    }

    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null;

        ClassLoader cl = GriffonApplicationHelper.class.getClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        cl = ApplicationClassLoader.get();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        if (cnfe != null) throw cnfe;
        return null;
    }

    public static Class<?> safeLoadClass(String className) {
        try {
            return loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Object safeNewInstance(String className) {
        try {
            return loadClass(className).newInstance();
        } catch (Exception e) {
            handleThrowable(e);
            return null;
        }
    }

    public static Object safeNewInstance(Class<?> clazz) {
        return safeNewInstance(clazz, true);
    }

    public static Object safeNewInstance(Class<?> clazz, boolean logException) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            if (logException) handleThrowable(e);
            return null;
        }
    }
}
