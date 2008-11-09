package org.codehaus.griffon.cli

import org.codehaus.groovy.tools.LoaderConfiguration
import org.codehaus.groovy.tools.RootLoader
import org.codehaus.groovy.tools.LoaderConfiguration
//import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.griffon.commons.GriffonContext
//import org.codehaus.groovy.grails.plugins.GrailsPluginUtils
import org.codehaus.gant.GantBinding
import gant.Gant
import org.codehaus.griffon.commons.GriffonUtil

//import org.codehaus.griffon.plugins.PluginManagerHolder

abstract class AbstractCliTests extends GroovyTestCase {
    String scriptName

    protected appBase = "test/cliTestApp"
	protected ant = new AntBuilder()

    /**
     * Creates a new test case for the script whose name matches the
     * name of the test (minus any package and "Tests" suffix).
     */
    AbstractCliTests() {
        def name = getClass().name
        def pos = name.lastIndexOf(".")
        if (pos != -1) {
            name = name[pos+1..-1]
        }

        if (name.endsWith("Tests")) {
            name = name[0..-6]
        }

        this.scriptName = name
    }

    AbstractCliTests(String scriptName) {
        this.scriptName = scriptName
    }

    void setUp() {
        def griffonDir = System.getProperty("user.home") + '/.griffon/projects'



        ExpandoMetaClass.enableGlobally()
        ant.delete(dir:appBase, failonerror:false)
		System.setProperty("base.dir", appBase)
		System.setProperty("griffon.cli.args", "testapp")
		System.setProperty("griffon.cli.testing", "true")
		System.setProperty("env.GRIFFON_HOME", new File("").absolutePath)
	}

	void tearDown() {
        ExpandoMetaClass.disableGlobally()
        //PluginManagerHolder.pluginManager = null
        def griffonDir = System.getProperty("user.home") + '/.griffon/projects'
        ant.delete(dir:appBase, failonerror:false)
        ant = null
	}

	protected String createTestApp(appName = "testapp") {
        // Pass the name of the test project to the create-app script.
        System.setProperty("griffon.cli.args", appName)

        // Create the application.
	    gantRun("CreateApp")

	    // Update the base directory to the application dir.
        def appDir = appBase + File.separator + appName
        System.setProperty("base.dir", appDir)

		// Finally, clear the CLI arguments.
        System.setProperty("griffon.cli.args", "")

        // Return the path to the new app.
        return appDir
    }

	protected void gantRun() {
        gantRun(this.scriptName)
    }

    protected void gantRun(String scriptName) {
        def workDir = "${appBase}/work"
        //FIXME GriffonPluginUtils.clearCaches()
        System.setProperty(GriffonContext.WORK_DIR, workDir)
        System.setProperty(GriffonContext.PROJECT_WORK_DIR, "$workDir/projects")
        System.setProperty(GriffonContext.PROJECT_CLASSES_DIR, "$workDir/projects/classes")
        System.setProperty(GriffonContext.PROJECT_TEST_CLASSES_DIR, "$workDir/projects/test-classes")
        System.setProperty(GriffonContext.PROJECT_RESOURCES_DIR, "$workDir/projects/resources")
        //System.setProperty(GriffonContext.PLUGINS_DIR, "")
        //System.setProperty(GriffonContext.GLOBAL_PLUGINS_DIR, "")
        System.setProperty("griffon.script.profile","true")

        LoaderConfiguration loaderConfig = new LoaderConfiguration()
	    loaderConfig.setRequireMain(false);

	    def libDir = new File('lib')
	    assert libDir.exists()
	    assert libDir.isDirectory()

	    libDir.eachFileMatch(~/gant.*\.jar/) {jarFile ->
	        loaderConfig.addFile(jarFile)
	    }

        // Set up a binding for Gant and put some essential variables
        // in there.
        String basedir = System.getProperty("base.dir")
        File baseFile = new File(basedir)
        ConfigObject preInitConfig = new ConfigObject()
        def rootLoader = new RootLoader(loaderConfig)

        GantBinding binding = new GantBinding()
        binding.setVariable("basedir", basedir)
        binding.setVariable("baseFile", baseFile)
        binding.setVariable("baseName", baseFile.name)
        binding.setVariable("defaultEnv", true)
        binding.setVariable("defaultTarget", { String description, Closure body ->
            if (binding.variables['default'] == null) {
                def c = binding.target
                c(['default':description], body)
            }
        })
        binding.setVariable("griffonEnv", "")
        binding.setVariable("griffonHome", ".")
        binding.setVariable("griffonVersion", GriffonUtil.griffonVersion)
        binding.setVariable("griffonScript", { return new File("./scripts/${it}.groovy") })
        binding.setVariable("griffonUnpack", { Map args -> ant.unjar(dest: args["dest"], src: "./target/${args["src"]}") {
                patternset {
                    exclude(name: "META-INF/**")
                }
            } })
        binding.setVariable("preInitConfig", preInitConfig)
        binding.setVariable("preInitProperties", preInitConfig.toProperties())
        binding.setVariable("userHome", System.getProperty("user.home"))
        binding.setVariable("rootLoader", rootLoader)
        binding.setVariable("griffonWorkDir", System.getProperty(GriffonContext.WORK_DIR))
        binding.setVariable("projectWorkDir", System.getProperty(GriffonContext.PROJECT_WORK_DIR))
        binding.setVariable("classesDirPath", System.getProperty(GriffonContext.PROJECT_CLASSES_DIR))
        binding.setVariable("resourcesDirPath", System.getProperty(GriffonContext.PROJECT_RESOURCES_DIR))
        binding.setVariable("testDirPath", System.getProperty(GriffonContext.PROJECT_TEST_CLASSES_DIR))
//        binding.setVariable("pluginsDirPath", System.getProperty(GriffonContext.PLUGINS_DIR))
//        binding.setVariable("globalPluginsDirPath", System.getProperty(GriffonContext.GLOBAL_PLUGINS_DIR))

        def gant = new Gant(
                new File("./scripts/${scriptName}.groovy"),
                binding,
                rootLoader)
	    gant.processTargets()
	}
}
