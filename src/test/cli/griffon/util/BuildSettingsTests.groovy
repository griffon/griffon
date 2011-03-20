package griffon.util

import griffon.build.GriffonBuildListener

/**
 * Test case for {@link BuildSettings}.
 */
class BuildSettingsTests extends GroovyTestCase {
    private String userHome
    private String version
    private File defaultWorkPath
    private Map savedSystemProps

    void setUp() {
        def props = new Properties()
        props.load(Thread.currentThread().contextClassLoader.getResourceAsStream('build.properties'))

        userHome = System.getProperty("user.home")
        version = props.getProperty("griffon.version")
        defaultWorkPath = new File(System.getProperty("user.home") + "/.griffon/" + version)

        savedSystemProps = [:] 
    }

    void tearDown() {
        // Restore any overridden system properties.
        savedSystemProps.each { String key, String value ->
            if (value == null) {
                System.clearProperty(key)
            }
            else {
                System.setProperty(key, value)
            }
        }
    }

    void testDefaultConstructor() {
        def cwd = new File(".").canonicalFile
        def settings = new BuildSettings()

        // Core properties first.
        assertEquals userHome, settings.userHome.path
        assertEquals cwd, settings.baseDir
        assertEquals version, settings.griffonVersion
        assertFalse settings.defaultEnv
        assertNull settings.griffonEnv
        assertNull settings.griffonHome

        // Project paths.
        assertEquals defaultWorkPath, settings.griffonWorkDir
        assertEquals new File("$defaultWorkPath/projects/${cwd.name}"), settings.projectWorkDir
        assertEquals new File("${settings.projectWorkDir}/classes"), settings.classesDir
        assertEquals new File("${settings.projectWorkDir}/test-classes"), settings.testClassesDir
        assertEquals new File("${settings.projectWorkDir}/resources"), settings.resourcesDir
        assertEquals new File("${settings.projectWorkDir}/plugins"), settings.projectPluginsDir
        assertEquals new File("$defaultWorkPath/global-plugins"), settings.globalPluginsDir

        // Dependencies.
        /*
        TODO Disabled for the moment until I can work out a reasonable way of doing this.
        assertTrue settings.compileDependencies.isEmpty()
        assertTrue settings.testDependencies.isEmpty()
        assertTrue settings.runtimeDependencies.isEmpty()

        // Set up a test "lib" directory and try again.
        def deleteLibDir = false
        def libDir = new File(cwd, "lib")
        if (!libDir.exists()) {
            libDir.mkdir()
            deleteLibDir = true
        }
        def libs = [ new File(libDir, "gwt.jar"), new File(libDir, "a.jar"), new File(libDir, "bcd.jar") ]
        libs.each { File file ->
            file.createNewFile()
        }

        // Check that the dependencies are picked up.
        try {
            settings = new BuildSettings()
            libs.each { File file ->
                assertTrue "Build missing compile dependency: $file", settings.compileDependencies.contains(libs[0])
                assertTrue "Build missing test dependency: $file", settings.testDependencies.contains(libs[0])
                assertTrue "Build missing runtime dependency: $file", settings.runtimeDependencies.contains(libs[0])
            }
        }
        finally {
            libs.each { it.delete() }
            if (deleteLibDir) libDir.delete()
        }
        */
    }

/*
    void testGriffonHomeConstructor() {
        def cwd = new File(".").canonicalFile
        def griffonHome = new File(cwd, "my-griffon")
        try {
            // Create the Griffon home directory and "lib" and "dist"
            // directories inside it, otherwise the tests will bomb.
            griffonHome.mkdir()
            new File(griffonHome, "lib").mkdir()
            new File(griffonHome, "dist").mkdir()

            def settings = new BuildSettings(new File("my-griffon"))

            // Core properties first.
            assertEquals userHome, settings.userHome.path
            assertEquals cwd, settings.baseDir
            assertEquals version, settings.griffonVersion
            assertEquals "my-griffon", settings.griffonHome.path
            assertFalse settings.defaultEnv
            assertNull settings.griffonEnv

            // Project paths.
            assertEquals defaultWorkPath, settings.griffonWorkDir
            assertEquals new File("$defaultWorkPath/projects/${cwd.name}"), settings.projectWorkDir
            assertEquals new File("${settings.projectWorkDir}/classes"), settings.classesDir
            assertEquals new File("${settings.projectWorkDir}/test-classes"), settings.testClassesDir
            assertEquals new File("${settings.projectWorkDir}/resources"), settings.resourcesDir
            assertEquals new File("${settings.projectWorkDir}/plugins"), settings.projectPluginsDir
            assertEquals new File("$defaultWorkPath/global-plugins"), settings.globalPluginsDir
        }
        finally {
            griffonHome.delete()
        }
    }
*/

    void testSystemPropertyOverride() {
        setSystemProperty("griffon.project.work.dir", "work")
        setSystemProperty("griffon.project.plugins.dir", "$userHome/my-plugins")

        def settings = new BuildSettings()

        // Project paths.
        assertEquals defaultWorkPath, settings.griffonWorkDir
        assertEquals new File("work"), settings.projectWorkDir
        assertEquals new File("${settings.projectWorkDir}/classes"), settings.classesDir
        assertEquals new File("${settings.projectWorkDir}/test-classes"), settings.testClassesDir
        assertEquals new File("${settings.projectWorkDir}/resources"), settings.resourcesDir
        assertEquals new File("${userHome}/my-plugins"), settings.projectPluginsDir
        assertEquals new File("$defaultWorkPath/global-plugins"), settings.globalPluginsDir
    }

/*
    void testExplicitValues() {
        def settings = new BuildSettings()
        settings.griffonWorkDir = new File("workDir")
        settings.projectWorkDir = new File("projectDir")
        settings.projectPluginsDir = new File("target/pluginsDir")

        // Check that these values have been set.
        String defaultProjectWorkDir = "${defaultWorkPath}/projects/${settings.baseDir.name}"
        assertEquals new File("workDir"), settings.griffonWorkDir
        assertEquals new File("projectDir"), settings.projectWorkDir
        assertEquals new File("${defaultProjectWorkDir}/classes"), settings.classesDir
        assertEquals new File("${defaultProjectWorkDir}/test-classes"), settings.testClassesDir
        assertEquals new File("${defaultProjectWorkDir}/resources"), settings.resourcesDir
        assertEquals new File("target/pluginsDir"), settings.projectPluginsDir
        assertEquals new File("$defaultWorkPath/global-plugins"), settings.globalPluginsDir
        assertEquals new File("target").canonicalFile, settings.projectTargetDir

        // Load a configuration file and check that the values we set
        // explicitly haven't changed.
        settings.rootLoader = new URLClassLoader(new URL[0], getClass().classLoader)
        settings.loadConfig(new File("test/resources/griffon-app/conf/BuildConfig.groovy"))

        assertEquals new File("workDir"), settings.griffonWorkDir
        assertEquals new File("projectDir"), settings.projectWorkDir
        assertEquals new File("build/classes"), settings.classesDir
        assertEquals new File("build/test-classes"), settings.testClassesDir
        assertEquals new File("projectDir/resources"), settings.resourcesDir
        assertEquals new File("target/pluginsDir"), settings.projectPluginsDir
        assertEquals new File("workDir/global-plugins"), settings.globalPluginsDir
        assertEquals new File("target").canonicalFile, settings.projectTargetDir
    }
*/

    void testSetBaseDir() {
        def settings = new BuildSettings()
        settings.baseDir = new File("base/dir")

        assertEquals new File("base/dir"), settings.baseDir
        assertEquals defaultWorkPath, settings.griffonWorkDir
        assertEquals new File("$defaultWorkPath/projects/dir"), settings.projectWorkDir
        assertEquals new File("${settings.projectWorkDir}/classes"), settings.classesDir
        assertEquals new File("${settings.projectWorkDir}/test-classes"), settings.testClassesDir
        assertEquals new File("${settings.projectWorkDir}/resources"), settings.resourcesDir
        assertEquals new File("${settings.projectWorkDir}/plugins"), settings.projectPluginsDir
        assertEquals new File("$defaultWorkPath/global-plugins"), settings.globalPluginsDir
    }

/*
    void testLoadConfig() {
        setSystemProperty("griffon.project.work.dir", "work")
        setSystemProperty("griffon.project.plugins.dir", "$userHome/my-plugins")

        def settings = new BuildSettings()
        settings.rootLoader = new URLClassLoader(new URL[0], getClass().classLoader)
        settings.loadConfig(new File("test/resources/griffon-app/conf/BuildConfig.groovy"))

        // Project paths.
        assertEquals defaultWorkPath, settings.griffonWorkDir
        assertEquals new File("work"), settings.projectWorkDir
        assertEquals new File("build/classes"), settings.classesDir
        assertEquals new File("build/test-classes"), settings.testClassesDir
        assertEquals new File("${settings.projectWorkDir}/resources"), settings.resourcesDir
        assertEquals new File("${userHome}/my-plugins"), settings.projectPluginsDir
        assertEquals new File("$defaultWorkPath/global-plugins"), settings.globalPluginsDir
    }
*/

    void testLoadConfigNoFile() {
        setSystemProperty("griffon.project.work.dir", "work")
        setSystemProperty("griffon.project.plugins.dir", "$userHome/my-plugins")

        def settings = new BuildSettings()
        settings.loadConfig(new File("test/BuildConfig.groovy"))

        // Project paths.
        assertEquals defaultWorkPath, settings.griffonWorkDir
        assertEquals new File("work"), settings.projectWorkDir
        assertEquals new File("${settings.projectWorkDir}/classes"), settings.classesDir
        assertEquals new File("${settings.projectWorkDir}/test-classes"), settings.testClassesDir
        assertEquals new File("${settings.projectWorkDir}/resources"), settings.resourcesDir
        assertEquals new File("${userHome}/my-plugins"), settings.projectPluginsDir
        assertEquals new File("$defaultWorkPath/global-plugins"), settings.globalPluginsDir
    }

    private void setSystemProperty(String name, String value) {
        if (!savedSystemProps[name]) {
            savedSystemProps[name] = System.getProperty(name)
        }

        System.setProperty(name, value)
    }
    
    void testBuildListenersViaSystemProperty() {
        try {
            def config = new ConfigObject()
            config.griffon.build.listeners = 'java.lang.String' // anything, just verify that the system property trumps.
            System.setProperty(BuildSettings.BUILD_LISTENERS, 'java.lang.Exception')

            def settings = new BuildSettings()
            settings.loadConfig(config)

            assertEquals(['java.lang.Exception'] as Object[], settings.buildListeners as List)
        } finally {
            System.clearProperty(BuildSettings.BUILD_LISTENERS)
        }
    }
    
    void testBuildListenersMultipleClassNames() {
        def config = new ConfigObject()
        config.griffon.build.listeners = 'java.lang.String,java.lang.String'
        def settings = new BuildSettings()
        settings.loadConfig(config)
        
        assertEquals(['java.lang.String', 'java.lang.String'], settings.buildListeners as List)
    }

    void testBuildListenersCollection() {
        def config = new ConfigObject()
        config.griffon.build.listeners = [String, String]
        def settings = new BuildSettings()
        settings.loadConfig(config)
        assertEquals([String, String], settings.buildListeners as List)
        
        config = new ConfigObject()
        config.griffon.build.listeners = ['java.lang.String', 'java.lang.String']
        settings = new BuildSettings()
        settings.loadConfig(config)
        assertEquals(['java.lang.String', 'java.lang.String'], settings.buildListeners as List)
    }
    
    void testBuildListenersBadValue() {
        def config = new ConfigObject()
        config.griffon.build.listeners = 1
        def settings = new BuildSettings()
        
        shouldFail(IllegalArgumentException) {
            settings.loadConfig(config)
        }
    }
}

class BuildSettingsTestsGriffonBuildListener implements GriffonBuildListener {
    void receiveGriffonBuildEvent(String name, Object[] args) {}
}
