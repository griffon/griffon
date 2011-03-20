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
package griffon.util

import java.util.regex.Pattern

import org.apache.ivy.plugins.repository.TransferListener
import org.apache.ivy.plugins.repository.TransferEvent
import org.apache.ivy.util.DefaultMessageLogger
import org.apache.ivy.util.Message

import org.codehaus.griffon.resolve.IvyDependencyManager
import org.codehaus.groovy.runtime.StackTraceUtils

/**
 * <p>Represents the project paths and other build settings
 * that the user can change when running the Griffon commands. Defaults
 * are provided for all settings, but the user can override those by
 * setting the appropriate system property or specifying a value for
 * it in the BuildConfig.groovy file.</p>
 * <p><b>Warning</b> The behaviour is poorly defined if you explicitly
 * set some of the project paths (such as {@link #projectWorkDir }),
 * but not others. If you set one of them explicitly, set all of them
 * to ensure consistent behaviour.</p>
 */
class BuildSettings extends AbstractBuildSettings {
    static final Pattern JAR_PATTERN = ~/^\S+\.jar$/

    /**
     * The base directory of the application
     */
    public static final String APP_BASE_DIR = "base.dir"

    /**
     * The name of the system property for {@link #griffonWorkDir}.
     */
    public static final String WORK_DIR = "griffon.work.dir"

    /**
     * The name of the system property for {@link #projectWorkDir}.
     */
    public static final String PROJECT_WORK_DIR = "griffon.project.work.dir"

    /**
     * The name of the system property for {@link #projectPluginsDir}.
     */
    public static final String PLUGINS_DIR = "griffon.project.plugins.dir"

    /**
     * The name of the system property for {@link #globalPluginsDir}.
     */
    public static final String GLOBAL_PLUGINS_DIR = "griffon.global.plugins.dir"

    /**
     * The name of the system property for {@link #resourcesDir}.
     */
    public static final String PROJECT_RESOURCES_DIR = "griffon.project.resource.dir"

    /**
     * The name of the system property for {@link #sourceDir}.
     */
    public static final String PROJECT_SOURCE_DIR = "griffon.project.source.dir"

    /**
     * The name of the system property for {@link #classesDir}.
     */
    public static final String PROJECT_CLASSES_DIR = "griffon.project.class.dir"

    /**
     * The name of the system property for {@link #pluginClassesDir}.
     */
    public static final String PROJECT_PLUGIN_CLASSES_DIR = "griffon.project.plugin.class.dir"

    /**
     * The name of the system property for {@link #testClassesDir}.
     */
    public static final String PROJECT_TEST_CLASSES_DIR = "griffon.project.test.class.dir"

    /**
     * The name of the system property for {@link #testResourcesDir}.
     */
    public static final String PROJECT_TEST_RESOURCES_DIR = "griffon.project.test.resource.dir"

    /**
     * The name of the system property for {@link #testReportsDir}.
     */
    public static final String PROJECT_TEST_REPORTS_DIR = "griffon.project.test.reports.dir"

    /**
     * The name of the system property for {@link #testReportsDir}.
     */
    public static final String PROJECT_DOCS_OUTPUT_DIR = "griffon.project.docs.output.dir"

    /**
     * The name of the system property for {@link #testSourceDir}.
     */
    public static final String PROJECT_TEST_SOURCE_DIR = "griffon.project.test.source.dir"

    /**
     * The name of the system property for {@link #projectTargetDir}.
     */
    public static final String PROJECT_TARGET_DIR = "griffon.project.target.dir"

    /**
     * The name of the system property for multiple {@link #buildListeners}.
     */
    public static final String BUILD_LISTENERS = "griffon.build.listeners"

    /**
     * The name of the system property for enabling verbose compilation {@link #verboseCompile}.
     */
    public static final String VERBOSE_COMPILE = "griffon.project.compile.verbose"

    /**
     * The base directory for the build, which is normally the root
     * directory of the current project. If a command is run outside
     * of a project, then this will be the current working directory
     * that the command was launched from.
     */
    File baseDir

    /** Location of the current user's home directory - equivalent to "user.home" system property. */
    File userHome

    /**
     * Location of the Griffon distribution as usually identified by
     * the Griffon_HOME environment variable. This value may be
     * <code>null</code> if Griffon_HOME is not set, for example if a
     * project uses the Griffon JAR files directly.
     */
    File griffonHome

    /** The version of Griffon being used for the current script. */
    final String griffonVersion
    final String groovyVersion
    final String antVersion
    final String slf4jVersion
    final String springVersion

    /** The environment for the current script. */
    String griffonEnv

    /** <code>true</code> if the default environment for a script should be used. */
    boolean defaultEnv

    /**
     * Whether the project required build dependencies are externally configured (by Maven for example) or not
     */
    boolean dependenciesExternallyConfigured = false

    /** The location of the Griffon working directory where non-project-specific temporary files are stored. */
    File griffonWorkDir

    /** The location of the project working directory for project-specific temporary files. */
    File projectWorkDir

    /** The location of the project target directory where reports, artifacts and so on are output. */
    File projectTargetDir

    /** The location to which Griffon compiles a project's classes. */
    File classesDir

    /** The location to which Griffon compiles a project's test classes. */
    File testClassesDir

    /** The location to which Griffon writes a project's test resources. */
    File testResourcesDir

    /** The location to which Griffon compiles a project's plugin classes. */
    File pluginClassesDir

    /** The location where Griffon keeps temporary copies of a project's resources. */
    File resourcesDir

    /** The location of the plain source. */
    File sourceDir

    /** The location of the test reports. */
    File testReportsDir

    /** The location of the documentation output. */
    File docsOutputDir

    /** The location of the test source. */
    File testSourceDir

    /** The root loader for the build. This has the required libraries on the classpath. */
    URLClassLoader rootLoader

    /** The settings stored in the project's BuildConfig.groovy file if there is one. */
    ConfigObject config = new ConfigObject()

    /**
     * The settings used to establish the HTTP proxy to use for dependency resolution etc. 
     */
    ConfigObject proxySettings = new ConfigObject()

    /**
     * The file containing the proxy settings 
     */
    File proxySettingsFile;

    /** Implementation of the "griffonScript()" method used in Griffon scripts. */
    Closure griffonScriptClosure

    /** Implementation of the "includePluginScript()" method used in Griffon scripts. */
    Closure includePluginScriptClosure

    /**
     * A Set of plugin names that represent the default set of plugins installed when creating Griffon applications
     */
    Set defaultPluginSet

    /**
     * A Set of plugin names and versions that represent the default set of plugins installed when creating Griffon applications
     */
    Map defaultPluginMap

    /**
     * List of jars provided in the applications 'lib' directory
     */
    List applicationJars = []

    List buildListeners = []

    /**
     * Setting for whether or not to enable verbose compilation, can be overridden via -verboseCompile(=[true|false])?
     */
    boolean verboseCompile = false

    public void resetDependencies() {
        resetCompileDependencies()
        resetRuntimeDependencies()
        resetTestDependencies()
        resetProvidedDependencies()
        resetBuildDependencies()
    }

    public void resetCompileDependencies() {
        compileDependencies.clear()
        compileDependencies.addAll(defaultCompileDependenciesClosure())
    }

    public void resetRuntimeDependencies() {
        runtimeDependencies.clear()
        runtimeDependencies.addAll(defaultRuntimeDependenciesClosure())
    }

    public void resetTestDependencies() {
        testDependencies.clear()
        testDependencies.addAll(defaultTestDependenciesClosure())
    }

    public void resetProvidedDependencies() {
        providedDependencies.clear()
        providedDependencies.addAll(providedDependenciesClosure())
    }

    public void resetBuildDependencies() {
        buildDependencies.clear()
        buildDependencies.addAll(buildDependenciesClosure())
    }

    private List<File> compileDependencies = []
    private boolean defaultCompileDepsAdded = false

    /** List containing the compile-time dependencies of the app as File instances. */
    List<File> getCompileDependencies() {
        if (!defaultCompileDepsAdded) {
            compileDependencies += defaultCompileDependencies
            defaultCompileDepsAdded = true
        }
        return compileDependencies
    }

    /**
     * Sets the compile time dependencies for the project
     */
    void setCompileDependencies(List<File> deps) {
        compileDependencies = deps
    }

    /** List containing the default (resolved via the dependencyManager) compile-time dependencies of the app as File instances. */
    private defaultCompileDependenciesClosure = {
        def jarFiles = dependencyManager
                            .resolveDependencies(IvyDependencyManager.COMPILE_CONFIGURATION)
                            .allArtifactsReports
                            .localFile + applicationJars
        Message.debug("Resolved jars for [compile]: ${{->jarFiles.join('\n')}}")
        return jarFiles
    }
    /** List containing the default (resolved via the dependencyManager) compile-time dependencies of the app as File instances. */
    @Lazy List<File> defaultCompileDependencies = defaultCompileDependenciesClosure()

    private List<File> testDependencies = []
    private boolean defaultTestDepsAdded = false

    /** List containing the test-time dependencies of the app as File instances. */
    List<File> getTestDependencies() {
        if (!defaultTestDepsAdded) {
            testDependencies += defaultTestDependencies
            defaultTestDepsAdded = true
        }
        return testDependencies
    }

    /**
     * Sets the test time dependencies for the project
     */
    void setTestDependencies(List<File> deps) {
        testDependencies = deps
    }

    private defaultTestDependenciesClosure = {
        def jarFiles = dependencyManager
                            .resolveDependencies(IvyDependencyManager.TEST_CONFIGURATION)
                            .allArtifactsReports
                            .localFile + applicationJars
        Message.debug("Resolved jars for [test]: ${{->jarFiles.join('\n')}}")
        return jarFiles
    }
    /** List containing the default test-time dependencies of the app as File instances. */
    @Lazy List<File> defaultTestDependencies = defaultTestDependenciesClosure()

    private List<File> runtimeDependencies = []
    private boolean defaultRuntimeDepsAdded = false

    /** List containing the runtime dependencies of the app as File instances. */
    List<File> getRuntimeDependencies() {
        if (!defaultRuntimeDepsAdded) {
            runtimeDependencies += defaultRuntimeDependencies
            defaultRuntimeDepsAdded = true
        }
        return runtimeDependencies
    }

    /**
     * Sets the runtime dependencies for the project
     */
    void setRuntimeDependencies(List<File> deps) {
        runtimeDependencies = deps
    }

    private defaultRuntimeDependenciesClosure = {
        def jarFiles = dependencyManager
                   .resolveDependencies(IvyDependencyManager.RUNTIME_CONFIGURATION)
                   .allArtifactsReports
                   .localFile + applicationJars
        Message.debug("Resolved jars for [runtime]: ${{->jarFiles.join('\n')}}")
        return jarFiles
    }
    /** List containing the default runtime-time dependencies of the app as File instances. */
    @Lazy List<File> defaultRuntimeDependencies = defaultRuntimeDependenciesClosure()

    private providedDependenciesClosure = {
        if (dependenciesExternallyConfigured) {
            return []
        }
        def jarFiles = dependencyManager
                       .resolveDependencies(IvyDependencyManager.PROVIDED_CONFIGURATION)
                       .allArtifactsReports
                       .localFile

        Message.debug("Resolved jars for [provided]: ${{->jarFiles.join('\n')}}")
        return jarFiles
    }
    /** List containing the dependencies needed at development time, but provided by the container at runtime **/
    @Lazy List<File> providedDependencies = providedDependenciesClosure()

    private buildDependenciesClosure = {
        if (dependenciesExternallyConfigured) {
            return []
        }
        def jarFiles = dependencyManager
                           .resolveDependencies(IvyDependencyManager.BUILD_CONFIGURATION)
                           .allArtifactsReports
                           .localFile + applicationJars

        Message.debug("Resolved jars for [build]: ${{->jarFiles.join('\n')}}")
        return jarFiles
    }
    /** List containing the dependencies required for the build system only */
    @Lazy List<File> buildDependencies = buildDependenciesClosure()

    /**
     * Manages dependencies and dependency resolution in a Griffon application
     */
    IvyDependencyManager dependencyManager

    /*
     * This is an unclever solution for handling "sticky" values in the
     * project paths, but trying to be clever so far has failed. So, if
     * the values of properties such as "griffonWorkDir" are set explicitly
     * (from outside the class), then they are not overridden by system
     * properties/build config.
     *
     * TODO Sort out this mess. Must decide on what can set this properties,
     * when, and how. Also when and how values can be overridden. This
     * is critically important for the Maven and Ant support.
     */
    private boolean griffonWorkDirSet
    private boolean projectWorkDirSet
    private boolean projectTargetDirSet
    private boolean classesDirSet
    private boolean testClassesDirSet
    private boolean pluginClassesDirSet
    private boolean resourcesDirSet
    private boolean testResourcesDirSet
    private boolean sourceDirSet
    private boolean testReportsDirSet
    private boolean docsOutputDirSet
    private boolean testSourceDirSet
    private boolean buildListenersSet
    private boolean verboseCompileSet

    BuildSettings() {
        this(null, null)
    }

    BuildSettings(String griffonHome) {
        this(new File(griffonHome), null)
    }

    BuildSettings(File griffonHome) {
        this(griffonHome, null)
    }

    BuildSettings(File griffonHome, File baseDir) {
        userHome = new File(System.getProperty("user.home"))

        if (griffonHome) this.griffonHome = griffonHome

        // Load the 'build.properties' file from the classpath and
        // retrieve the Griffon version from it.
        Properties buildProps = new Properties()
        try {
            loadBuildPropertiesFromClasspath(buildProps)
            griffonVersion = buildProps.'griffon.version'
            groovyVersion = buildProps.'groovy.version'
            antVersion = buildProps.'ant.version'
            slf4jVersion = buildProps.'slf4j.version'
            springVersion = buildProps.'spring.version'
        }
        catch (IOException ex) {
            StackTraceUtils.deepSanitize(ex).printStackTrace()
            throw new IOException("Unable to find 'build.properties' - make " +
                    "that sure the 'griffon-cli-*.jar' file is on the classpath.")
        }

        // Update the base directory. This triggers some extra config.
        setBaseDir(baseDir)

        // The "griffonScript" closure definition. Returns the location
        // of the corresponding script file if GRIFFON_HOME is set,
        // otherwise it loads the script class using the Gant classloader.
        griffonScriptClosure = {String name ->
            def potentialScript = new File("${griffonHome}/scripts/${name}.groovy")
            potentialScript = potentialScript.exists() ? potentialScript : new File("${griffonHome}/scripts/${name}_.groovy")
            if(potentialScript.exists()) {
                return potentialScript
            }
            else {
                try {
                    return classLoader.loadClass("${name}_")
                }
                catch (e) {
                    return classLoader.loadClass(name)
                }
            }
        }

        includePluginScriptClosure = {String pluginName, String scriptName ->
            def pluginHome = pluginSettings.getPluginDirForName(pluginName)?.file
            if(!pluginHome) return
            def scriptFile = new File(pluginHome,"/scripts/${scriptName}.groovy")
            if(scriptFile.exists()) includeTargets << scriptFile
        }
    }

    private def loadBuildPropertiesFromClasspath(Properties buildProps) {
        InputStream stream = getClass().classLoader.getResourceAsStream("griffon.build.properties")
        if(stream == null) {
            stream = getClass().classLoader.getResourceAsStream("build.properties")
        }
        if (stream) {
            buildProps.load(stream)
        }
    }

    /**
     * Returns the current base directory of this project.
     */
    File getBaseDir() { baseDir }

    /**
     * <p>Changes the base directory, making sure that everything that
     * depends on it gets refreshed too. If you have have previously
     * loaded a configuration file, you should load it again after
     * calling this method.</p>
     * <p><b>Warning</b> This method resets the project paths, so if
     * they have been set manually by the caller, then that information
     * will be lost!</p>
     */
    void setBaseDir(File newBaseDir) {
        baseDir = newBaseDir ?: establishBaseDir()
        // Initialize Metadata
        Metadata.getInstance(new File(baseDir, "application.properties"))

        // Set up the project paths, using an empty config for now. The
        // paths will be updated if and when a BuildConfig configuration
        // file is loaded.
        config = new ConfigObject()
        establishProjectStructure()

        if (baseDir) {
            // Add the application's libraries.
            def appLibDir = new File(baseDir, "lib")
            if (appLibDir.exists()) {
                appLibDir.eachFileMatch(JAR_PATTERN) {
                    applicationJars << it
                }
            }
        }
    }

    File getGriffonWorkDir() { griffonWorkDir }

    void setGriffonWorkDir(File dir) {
        griffonWorkDir = dir
        griffonWorkDirSet = true
    }

    File getProjectWorkDir() { projectWorkDir }

    void setProjectWorkDir(File dir) {
        projectWorkDir = dir
        projectWorkDirSet = true
    }

    File getProjectTargetDir() { projectTargetDir }

    void setProjectTargetDir(File dir) {
        projectTargetDir = dir
        projectTargetDirSet = true
    }

    File getClassesDir() { classesDir }

    void setClassesDir(File dir) {
        classesDir = dir
        classesDirSet = true
    }

    File getTestClassesDir() { testClassesDir }

    void setTestClassesDir(File dir) {
        testClassesDir = dir
        testClassesDirSet = true
    }

    File getPluginClassesDir() { pluginClassesDir }

    void setPluginClassesDir(File dir) {
        pluginClassesDir = dir
        pluginClassesDirSet = true
    }

    File getResourcesDir() { resourcesDir }

    void setResourcesDir(File dir) {
        resourcesDir = dir
        resourcesDirSet = true
    }

    File getTestResourcesDir() { testResourcesDir }

    void setTestResourcesDir(File dir) {
        testResourcesDir = dir
        testResourcesDirSet = true
    }

    File getSourceDir() { sourceDir }

    void setSourceDir(File dir) {
        sourceDir = dir
        sourceDirSet = true
    }

    File getTestReportsDir() { testReportsDir }

    void setTestReportsDir(File dir) {
        testReportsDir = dir
        testReportsDirSet = true
    }

    File getTestSourceDir() { testSourceDir }

    void setTestSourceDir(File dir) {
        testSourceDir = dir
        testSourceDirSet = true
    }

    void setBuildListeners(buildListeners) {
        this.buildListeners = buildListeners.toList()
        buildListenersSet = true
    }

    Object[] getBuildListeners() { buildListeners.toArray() }

    void setVerboseCompile(boolean flag) {
        verboseCompile = flag
        verboseCompileSet = true
    }

    /**
     * Loads the application's BuildConfig.groovy file if it exists
     * and returns the corresponding config object. If the file does
     * not exist, this returns an empty config.
     */
    ConfigObject loadConfig() {
        loadConfig(new File(baseDir, "griffon-app/conf/BuildConfig.groovy"))
    }

    /**
     * Loads the given configuration file if it exists and returns the
     * corresponding config object. If the file does not exist, this
     * returns an empty config.
     */
    ConfigObject loadConfig(File configFile) {
        try {
            loadSettingsFile()
            if (configFile.exists()) {
                // To avoid class loader issues, we make sure that the
                // Groovy class loader used to parse the config file has
                // the root loader as its parent. Otherwise we get something
                // like NoClassDefFoundError for Script.
                GroovyClassLoader gcl = obtainGroovyClassLoader()
                ConfigSlurper slurper = createConfigSlurper()

                URL configUrl = configFile.toURI().toURL()
                Script script = gcl.parseClass(configFile)?.newInstance()

                config.setConfigFile(configUrl)
                loadConfig(slurper.parse(script))
            } else {
                postLoadConfig()
            }
        }
        catch(e) {
            StackTraceUtils.deepSanitize e
            throw e
        }

    }

    ConfigObject loadConfig(ConfigObject config) {
        try {
            this.config.merge(config)
            return this.config
        }
        finally {
            postLoadConfig()
        }
    }

    protected void postLoadConfig() {
        establishProjectStructure()
        parseGriffonBuildListeners()
        if (config.griffon.default.plugin.set instanceof List) {
            defaultPluginSet = config.griffon.default.plugin.set
        }
        flatConfig = config.flatten()
        configureDependencyManager(config)
    }

    protected boolean settingsFileLoaded = false
    protected ConfigObject loadSettingsFile() {
        if (!settingsFileLoaded) {
            def settingsFile = new File("$userHome/.griffon/settings.groovy")
            def gcl = obtainGroovyClassLoader()
            def slurper = createConfigSlurper()
            if (settingsFile.exists()) {
                Script script = gcl.parseClass(settingsFile)?.newInstance()
                if (script) {
                    config = slurper.parse(script)
                }
            }

            this.proxySettingsFile = new File("$userHome/.griffon/ProxySettings.groovy")
            if(proxySettingsFile.exists()) {
                slurper = createConfigSlurper()
                try {
                    Script script = gcl.parseClass(proxySettingsFile)?.newInstance()
                    if (script) {
                        proxySettings = slurper.parse(script)
                        def current = proxySettings.currentProxy
                        if(current) {
                            proxySettings[current]?.each { key, value ->
                                System.setProperty(key, value)
                            }
                        }
                    }
                }
                catch (e) {
                    println "WARNING: Error configuring proxy settings: ${e.message}"
                }

            }

            settingsFileLoaded = true
        }
        config
    }

    private GroovyClassLoader gcl
    GroovyClassLoader obtainGroovyClassLoader() {
        if (gcl == null) {
            gcl = rootLoader != null ? new GroovyClassLoader(rootLoader) : new GroovyClassLoader(ClassLoader.getSystemClassLoader())
        }
        return gcl
    }

    def configureDependencyManager(ConfigObject config) {
        Message.setDefaultLogger new DefaultMessageLogger(Message.MSG_WARN)

        Metadata metadata = Metadata.current
        def appName = metadata.getApplicationName() ?: "griffon"
        def appVersion = metadata.getApplicationVersion() ?: griffonVersion

        dependencyManager = new IvyDependencyManager(appName,
                appVersion, this, metadata)

        dependencyManager.transferListener = { TransferEvent e ->
            switch(e.eventType) {
                case TransferEvent.TRANSFER_STARTED:
                    println "Downloading: ${e.resource.name} ..."
                break
                case TransferEvent.TRANSFER_COMPLETED:
                    println "Download complete."
                break
            }
        } as TransferListener

        if (dependenciesExternallyConfigured) {
            // Even if the dependencies are handled externally, we still
            // need to handle plugin dependencies.
            config.griffon.global.dependency.resolution = {
                repositories {
                    griffonPlugins()
                }
            }
        } else {
            config.griffon.global.dependency.resolution = IvyDependencyManager.getDefaultDependencies(griffonVersion)
            def credentials = config.griffon.project.ivy.authentication
            if (credentials instanceof Closure) {
                dependencyManager.parseDependencies credentials
            }
        }

        def dependencyConfig = config.griffon.project.dependency.resolution
        if (!dependencyConfig) {
            dependencyConfig = config.griffon.global.dependency.resolution
            dependencyManager.inheritsAll = true
        }
        if (dependencyConfig) {
            dependencyManager.parseDependencies dependencyConfig
        }

        // All projects need the plugins to be resolved.
        def handlePluginDirectory = pluginDependencyHandler()
        def pluginDirs = getPluginDirectories()
		for(dir in pluginDirs) {
			handlePluginDirectory(dir)
		}
    }

    Closure pluginDependencyHandler() {
        return pluginDependencyHandler(dependencyManager)
    }

    Closure pluginDependencyHandler(IvyDependencyManager dependencyManager) {
        def pluginSlurper = createConfigSlurper()

        return { File dir ->
            def pluginName = dir.name
            def matcher = pluginName =~ /(\S+?)-(\d\S+)/
            pluginName = matcher ? matcher[0][1] : pluginName
            // Try BuildConfig.groovy first, which should
            // work for in-place plugins.
            def path = dir.absolutePath
            def pluginDependencyDescriptor = new File("${path}/griffon-app/conf/BuildConfig.groovy")

            if (!pluginDependencyDescriptor.exists()) {
                // OK, that doesn't exist, so try dependencies.groovy.
                pluginDependencyDescriptor = new File("$path/dependencies.groovy")
            }

            if (pluginDependencyDescriptor.exists()) {
                def gcl = obtainGroovyClassLoader()

                try {
                    Script script = gcl.parseClass(pluginDependencyDescriptor)?.newInstance()
                    def pluginConfig = pluginSlurper.parse(script)
                    def pluginDependencyConfig = pluginConfig.griffon.project.dependency.resolution
                    if (pluginDependencyConfig instanceof Closure) {
                        dependencyManager.parseDependencies(pluginName, pluginDependencyConfig)
                    }

                    def inlinePlugins = getInlinePluginsFromConfiguration(pluginConfig, dir)
                    if(inlinePlugins) {
                        for(File inlinePlugin in inlinePlugins) {
                            addPluginDirectory inlinePlugin, true
                            // recurse
                            def handleInlinePlugin = pluginDependencyHandler()
                            handleInlinePlugin(inlinePlugin)
                        }
                    }
                }
                catch (e) {
                    println "WARNING: Dependencies cannot be resolved for plugin [$pluginName] due to error: ${e.message}"
                }

            }
        }
    }

    ConfigSlurper createConfigSlurper() {
        def slurper = new ConfigSlurper(Environment.current.name)
        slurper.setBinding(
                basedir: baseDir.path,
                baseFile: baseDir,
                baseName: baseDir.name,
                griffonHome: griffonHome?.path,
                griffonVersion: griffonVersion,
                userHome: userHome,
                griffonSettings: this,
                appName:Metadata.current.getApplicationName(),
                appVersion:Metadata.current.getApplicationVersion())
        return slurper
    }

    def isPluginProject() {
        baseDir.listFiles().find { it.name.endsWith("GriffonPlugin.groovy") }
    }

    def isAddonPlugin() {
        baseDir.listFiles().find { it.name.endsWith("GriffonAddon.groovy") }
    }

    private void establishProjectStructure() {
        // The third argument to "getPropertyValue()" is either the
        // existing value of the corresponding field, or if that's
        // null, a default value. This ensures that we don't override
        // settings provided by, for example, the Maven plugin.
        def props = config.toProperties()
        // read metadata file
        Metadata.current
        if (!griffonWorkDirSet) {
            griffonWorkDir = new File(getPropertyValue(WORK_DIR, props, "${userHome}/.griffon/${griffonVersion}"))
        }

        if (!projectWorkDirSet) {
            projectWorkDir = new File(getPropertyValue(PROJECT_WORK_DIR, props, "$griffonWorkDir/projects/${baseDir.name}"))
        }

        if (!projectTargetDirSet) {
            projectTargetDir = new File(getPropertyValue(PROJECT_TARGET_DIR, props, "$baseDir/target"))
        }

        if (!classesDirSet) {
            classesDir = new File(getPropertyValue(PROJECT_CLASSES_DIR, props, "$projectWorkDir/classes"))
        }

        if (!testClassesDirSet) {
            testClassesDir = new File(getPropertyValue(PROJECT_TEST_CLASSES_DIR, props, "$projectWorkDir/test-classes"))
        }

        if (!pluginClassesDirSet) {
            pluginClassesDir = new File(getPropertyValue(PROJECT_PLUGIN_CLASSES_DIR, props, "$projectWorkDir/plugin-classes"))
        }

        if (!resourcesDirSet) {
            resourcesDir = new File(getPropertyValue(PROJECT_RESOURCES_DIR, props, "$projectWorkDir/resources"))
        }

        if (!testResourcesDirSet) {
            testResourcesDir = new File(getPropertyValue(PROJECT_TEST_RESOURCES_DIR, props, "$projectWorkDir/test-resources"))
        }

        if (!sourceDirSet) {
            sourceDir = new File(getPropertyValue(PROJECT_SOURCE_DIR, props, "$baseDir/src"))
        }

        if (!projectPluginsDirSet) {
            this.@projectPluginsDir = new File(getPropertyValue(PLUGINS_DIR, props, "$projectWorkDir/plugins"))
        }

        if (!globalPluginsDirSet) {
            this.@globalPluginsDir = new File(getPropertyValue(GLOBAL_PLUGINS_DIR, props, "$griffonWorkDir/global-plugins"))
        }

        if (!testReportsDirSet) {
            testReportsDir = new File(getPropertyValue(PROJECT_TEST_REPORTS_DIR, props, "${projectTargetDir}/test-reports"))
        }

        if (!docsOutputDirSet) {
            docsOutputDir = new File(getPropertyValue(PROJECT_DOCS_OUTPUT_DIR, props, "${projectTargetDir}/docs"))
        }

        if (!testSourceDirSet) {
            testSourceDir = new File(getPropertyValue(PROJECT_TEST_SOURCE_DIR, props, "${baseDir}/test"))
        }

        if (!verboseCompileSet) {
            verboseCompile = getPropertyValue(VERBOSE_COMPILE, props, '').toBoolean()
        }  
    }

    protected void parseGriffonBuildListeners() {
        if (!buildListenersSet) {
            def listenersValue = System.getProperty(BUILD_LISTENERS) ?: config.griffon.build.listeners // Anyway to use the constant to do this?
            if (listenersValue) {
                def add = {
                    if (it instanceof String) {
                        it.split(',').each { this.@buildListeners << it }
                    } else if (it instanceof Class) {
                        this.@buildListeners << it
                    } else {
                        throw new IllegalArgumentException("$it is not a valid value for $BUILD_LISTENERS")
                    }
                }

                (listenersValue instanceof Collection) ? listenersValue.each(add) : add(listenersValue)
            }
            buildListenersSet = true
        }
    }

    private getPropertyValue(String propertyName, Properties props, String defaultValue) {
        // First check whether we have a system property with the given name.
        def value = getValueFromSystemOrBuild(propertyName, props)

        // Return the BuildSettings value if there is one, otherwise
        // use the default.
        return value != null ? value : defaultValue
    }

    private getValueFromSystemOrBuild(String propertyName, Properties props) {
        def value = System.getProperty(propertyName)
        if (value != null) return value

        // Now try the BuildSettings config.
        value = props[propertyName]
        return value
    }

    private File establishBaseDir() {
        def sysProp = System.getProperty(APP_BASE_DIR)
        def baseDir
        if (sysProp) {
            baseDir = sysProp == '.' ? new File("") : new File(sysProp)
        }
        else {
            baseDir = new File("")
            if(!new File(baseDir, "griffon-app").exists()) {
                // be careful with this next step...
                // baseDir.parentFile will return null since baseDir is new File("")
                // baseDir.absoluteFile needs to happen before retrieving the parentFile
                def parentDir = baseDir.absoluteFile.parentFile

                // keep moving up one directory until we find
                // one that contains the griffon-app dir or get
                // to the top of the filesystem...
                while (parentDir != null && !new File(parentDir, "griffon-app").exists()) {
                    parentDir = parentDir.parentFile
                }

                if (parentDir != null) {
                    // if we found the project root, use it
                    baseDir = parentDir
                }
            }

        }
        return baseDir.canonicalFile
    }
}
