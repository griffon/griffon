package org.codehaus.griffon.cli.support

import junit.framework.Assert
import junit.framework.AssertionFailedError

/**
 * Test case for {@link GriffonBuildHelper}.
 */
class GriffonBuildHelperTests extends GroovyTestCase {
    def testRunner
    def testSettings

    void testSetDepedenciesExternallyConfigured() {

        def testHelper = new GriffonBuildHelper(new CustomClassLoader(this))
        testHelper.setDependenciesExternallyConfigured(true)
    }

    void testExecution() {
        def testHelper = new GriffonBuildHelper(new CustomClassLoader(this))
        assertEquals 0, testHelper.execute("Compile")
        assertEquals "Compile", testRunner.lastScript["name"]
        assertNull testRunner.lastScript["args"]
        assertNull testRunner.lastScript["env"]

        assertEquals 1, testHelper.execute("TestApp", "-unit -rerun", "test")
        assertEquals "TestApp", testRunner.lastScript["name"]
        assertEquals "-unit -rerun", testRunner.lastScript["args"]
        assertEquals "test", testRunner.lastScript["env"]
    }

    void testExecutionWithCustomSettings() {
        def testCompileDeps = [ "1", "2" ]
        def testTestDeps = [ "3", "4", "5" ]
        def testRuntimeDeps = [ "7" ]

        def testHelper = new GriffonBuildHelper(new CustomClassLoader(this))
        testHelper.griffonWorkDir = new File("global-work")
        testHelper.projectWorkDir = new File("target")
        testHelper.classesDir = new File("target/classes")
        testHelper.testClassesDir = new File("target/test-classes")
        testHelper.resourcesDir = new File("target/res")
        testHelper.projectPluginsDir = new File("plugins")
        testHelper.globalPluginsDir = new File("global-work/plugins")
        testHelper.testReportsDir = new File("target/test-reports")
        testHelper.compileDependencies = testCompileDeps
        testHelper.testDependencies = testTestDeps
        testHelper.runtimeDependencies = testRuntimeDeps

        assertEquals new File("global-work"), testSettings.griffonWorkDir
        assertEquals new File("target"), testSettings.projectWorkDir
        assertEquals new File("target/classes"), testSettings.classesDir
        assertEquals new File("target/test-classes"), testSettings.testClassesDir
        assertEquals new File("target/res"), testSettings.resourcesDir
        assertEquals new File("plugins"), testSettings.projectPluginsDir
        assertEquals new File("global-work/plugins"), testSettings.globalPluginsDir
        assertEquals new File("target/test-reports"), testSettings.testReportsDir
        assertEquals testCompileDeps, testSettings.compileDependencies
        assertEquals testTestDeps, testSettings.testDependencies
        assertEquals testRuntimeDeps, testSettings.runtimeDependencies

        // Try executing a script with these settings.
        assertEquals 0, testHelper.execute("Compile")
        assertEquals "Compile", testRunner.lastScript["name"]
        assertNull testRunner.lastScript["args"]
        assertNull testRunner.lastScript["env"]
    }
}

class MockGriffonScriptRunner {
    static testCase

    def lastScript

    MockGriffonScriptRunner(MockBuildSettings settings) {
        testCase.testRunner = this

        Assert.assertSame testCase.testSettings, settings
    }
    
    int executeCommand(String scriptName, String args) {
        this.lastScript = [ name: scriptName, args: args ]
        return 0
    }

    int executeCommand(String scriptName, String args, String env) {
        this.lastScript = [ name: scriptName, args: args, env: env ]
        return 1
    }
}

class MockBuildSettings {
    static testCase

    File griffonWorkDir
    File projectWorkDir
    File classesDir
    File testClassesDir
    File resourcesDir
    File projectPluginsDir
    File globalPluginsDir
    File testReportsDir
    List compileDependencies
    List testDependencies
    List runtimeDependencies
    URLClassLoader rootLoader
    boolean dependenciesExternallyConfigured = false

    MockBuildSettings() {
        testCase.testSettings = this
    }

    MockBuildSettings(File griffonHome) {
        testCase.testSettings = this
    }

    MockBuildSettings(File griffonHome, File baseDir) {
        testCase.testSettings = this
    }
}

class CustomClassLoader extends URLClassLoader {
    def testCase

    CustomClassLoader(test) {
        super([] as URL[])
        this.testCase = test
    }

    Class loadClass(String name) {
        if (name == "org.codehaus.griffon.cli.GriffonScriptRunner") {
            MockGriffonScriptRunner.testCase = this.testCase
            return MockGriffonScriptRunner
        }
        else if (name == "griffon.util.BuildSettings") {
            MockBuildSettings.testCase = this.testCase
            return MockBuildSettings
        }
        else {
            throw new AssertionFailedError("Asked to load unrecognised class: ${name}")
        }
    }
}
