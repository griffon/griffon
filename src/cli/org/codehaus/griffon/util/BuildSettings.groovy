/*
 * Copyright 2008 the original author or authors.
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
package org.codehaus.griffon.util

import org.codehaus.groovy.runtime.StackTraceUtils;

/**
 * This class represents the project paths and other build settings
 * that the user can change when running the Griffon commands. Defaults
 * are provided for all settings, but the user can override those by
 * setting the appropriate system property or specifying a value for
 * it in the BuildSettings.groovy file.
 */
class BuildSettings {

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
    public static final String PLUGINS_DIR = "griffon.plugins.dir"

    /**
     * The name of the system property for {@link #globalPluginsDir}.
     */
    public static final String GLOBAL_PLUGINS_DIR = "griffon.global.plugins.dir"

    /**
     * The name of the system property for {@link #resourcesDir}.
     */
    public static final String PROJECT_RESOURCES_DIR = "griffon.project.resource.dir"

    /**
     * The name of the system property for {@link #classesDir}.
     */
    public static final String PROJECT_CLASSES_DIR = "griffon.project.class.dir"

    /**
     * The name of the system property for {@link #testClassesDir}.
     */
    public static final String PROJECT_TEST_CLASSES_DIR = "griffon.project.test.class.dir"

    /**
     * The name of the system property for {@link #testReportsDir}.
     */
    public static final String PROJECT_TEST_REPORTS_DIR = "griffon.project.test.reports.dir"

    /**
     * Constant used to resolve the environment via System.getProperty(ENVIRONMENT)
     */
    public static final String ENVIRONMENT = "griffon.env"

    /**
     * Constants that indicates whether this GriffonApplication is running in the default environment
     */
    public static final String ENVIRONMENT_DEFAULT = "griffon.env.default"

    /**
     * Constant for the development environment
     */
    public static String ENV_DEVELOPMENT = "development"

    /**
     * Constant for the application data source, primarly for backward compatability for those applications
     * that use ApplicationDataSource.groovy
     */
    public static String ENV_APPLICATION = "application"

    /**
     * Constant for the production environment
     */
    public static String ENV_PRODUCTION = "production"

    /*
     * Constant for the test environment
     */
    public static String ENV_TEST  = "test"

    /**
     * The base directory for the build, which is normally the root
     * directory of the current project. If a command is run outside
     * of a project, then this will be the current working directory
     * that the command was launched from.
     */
    File baseDir

    /** Location of the current user's home directory - equivalen to "user.home" system property. */
    File userHome

    /**
     * Location of the Griffon distribution as usually identified by
     * the Griffon_HOME environment variable. This value may be
     * <code>null</code> if Griffon_HOME is not set, for example if a
     * project uses the Griffon JAR files directly.
     */
    File griffonHome

    /** The version of Griffon being used for the current script. */
    String griffonVersion

    /** The environment for the current script. */
    String griffonEnv

    /** <code>true</code> if the default environment for a script should be used. */
    boolean defaultEnv

    /** The location of the Griffon working directory where non-project-specific temporary files are stored. */
    File griffonWorkDir

    /** The location of the project working directory for project-specific temporary files. */
    File projectWorkDir

    /** The location to which Griffon compiles a project's classes. */
    File classesDir

    /** The location to which Griffon compiles a project's test classes. */
    File testClassesDir

    /** The location where Griffon keeps temporary copies of a project's resources. */
    File resourcesDir

    /** The location where project-specific plugins are installed to. */
    File projectPluginsDir

    /** The location where global plugins are installed to. */
    File globalPluginsDir

    /** The location of the test reports. */
    File testReportsDir

    /** The root loader for the build. This has the required libraries on the classpath. */
    URLClassLoader rootLoader

    /** The settings stored in the project's BuildSettings.groovy file if there is one. */
    ConfigObject config

    /** Implementation of the "griffonScript()" method used in Griffon scripts. */
    Closure griffonScriptClosure;

    /** List containing the compile-time dependencies of the app as File instances. */
    List compileDependencies

    /** List containing the test-time dependencies of the app as File instances. */
    List testDependencies

    /** List containing the runtime-time dependencies of the app as File instances. */
    List runtimeDependencies

    BuildSettings() {
        this(null)
    }

    BuildSettings(String griffonHome) {
        baseDir = establishBaseDir()
        userHome = new File(System.getProperty("user.home"))

        if (griffonHome) this.griffonHome = new File(griffonHome)

        // Load the 'build.properties' file from the classpath and
        // retrieve the Griffon version from it.
        Properties buildProps = new Properties()
        try {
            loadBuildPropertiesFromClasspath(buildProps)
            griffonVersion = buildProps.'griffon.version'
        }
        catch (IOException ex) {
            StackTraceUtils.deepSanitize(ex).printStackTrace()
            throw new IOException("Unable to find 'build.properties' - make " +
                    "that sure the 'griffon-cli-*.jar' file is on the classpath.")
        }

        // Set up the project paths, using an empty config for now. The
        // paths will be updated if and when a BuildSettings configuration
        // file is loaded.
        config = new ConfigObject()
        establishProjectStructure()

        // The "griffonScript" closure definition. Returns the location
        // of the corresponding script file if Griffon_HOME is set,
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

        // If 'griffonHome' is set, add the JAR file dependencies.
        def jarPattern = ~/^\S+\.jar$/
        def addJars = { File jar ->
            this.compileDependencies << jar
            this.testDependencies << jar
            this.runtimeDependencies << jar
        }

        this.compileDependencies = []
        this.testDependencies = []
        this.runtimeDependencies = []

        if (griffonHome) {
            // Currently all JARs are added to each of the dependency
            // lists.
            new File(this.griffonHome, "lib").eachFileMatch(jarPattern, addJars)
            new File(this.griffonHome, "dist").eachFileMatch(jarPattern, addJars)
        }

        // Add the application's libraries.
        def appLibDir = new File(this.baseDir, "lib")
        if (appLibDir.exists()) {
            appLibDir.eachFileMatch(jarPattern, addJars)
        }
    }

    private def loadBuildPropertiesFromClasspath(Properties buildProps) {
        InputStream stream = getClass().classLoader.getResourceAsStream("build.properties")
        if(stream) {            
            buildProps.load(stream)
        }
    }

    /**
     * Loads the application's BuildSettings.groovy file if it exists
     * and returns the corresponding config object. If the file does
     * not exist, this returns an empty config.
     */
    public ConfigObject loadConfig() {
        loadConfig(new File(baseDir, "griffon-app/conf/BuildSettings.groovy"))
    }

    /**
     * Loads the given configuration file if it exists and returns the
     * corresponding config object. If the file does not exist, this
     * returns an empty config.
     */
    public ConfigObject loadConfig(File configFile) {
        // To avoid class loader issues, we make sure that the
        // Groovy class loader used to parse the config file has
        // the root loader as its parent. Otherwise we get something
        // like NoClassDefFoundError for Script.
        GroovyClassLoader gcl = this.rootLoader != null ? new GroovyClassLoader(this.rootLoader) : new GroovyClassLoader(ClassLoader.getSystemClassLoader());
        def slurper = new ConfigSlurper()
        slurper.setBinding(
                    basedir: baseDir.path,
                    baseFile: baseDir,
                    baseName: baseDir.name,
                    griffonHome: griffonHome?.path,
                    griffonVersion: griffonVersion,
                    userHome: userHome)
      
        // Find out whether the file exists, and if so parse it.
        def settingsFile = new File("${griffonWorkDir}/settings.groovy")
        if (settingsFile.exists()) {
            Script script = gcl.parseClass(settingsFile).newInstance();
            config = slurper.parse(script)
        }

        if (configFile.exists()) {
            URL configUrl = configFile.toURI().toURL()
            Script script = gcl.parseClass(configFile).newInstance();

            if (!config)
               config = slurper.parse(script)
            else
               config.merge(slurper.parse(script))

            config.setConfigFile(configUrl)

            establishProjectStructure()
        }

        return config
    }

    private void establishProjectStructure() {
        def props = config.toProperties()
        griffonWorkDir = new File(getPropertyValue(WORK_DIR, props, "${userHome}/.griffon/${griffonVersion}"))
        projectWorkDir = new File(getPropertyValue(PROJECT_WORK_DIR, props, "$griffonWorkDir/projects/${baseDir.name}"))
        classesDir = new File(getPropertyValue(PROJECT_CLASSES_DIR, props, "$projectWorkDir/classes"))
        testClassesDir = new File(getPropertyValue(PROJECT_TEST_CLASSES_DIR, props, "$projectWorkDir/test-classes"))
        resourcesDir = new File(getPropertyValue(PROJECT_RESOURCES_DIR, props, "$projectWorkDir/resources"))
        projectPluginsDir = new File(getPropertyValue(PLUGINS_DIR, props, "$projectWorkDir/plugins"))
        globalPluginsDir = new File(getPropertyValue(GLOBAL_PLUGINS_DIR, props, "$griffonWorkDir/global-plugins"))
        testReportsDir = new File(getPropertyValue(PROJECT_TEST_REPORTS_DIR, props, "${baseDir}/test/reports"))
    }

    private getPropertyValue(String propertyName, Properties props, String defaultValue) {
        // First check whether we have a system property with the given name.
        def value = System.getProperty(propertyName)
        if (value != null) return value

        // Now try the BuildSettings config.
        value = props[propertyName]

        // Return the BuildSettings value if there is one, otherwise
        // use the default.
        return value != null ? value : defaultValue
    }

    private File establishBaseDir() {
        def sysProp = System.getProperty("base.dir")
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
