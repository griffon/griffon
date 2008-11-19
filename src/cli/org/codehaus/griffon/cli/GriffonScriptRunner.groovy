/*
 * Copyright 2004-2008 the original author or authors.
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

package org.codehaus.griffon.cli;

import gant.Gant
import org.codehaus.griffon.commons.GriffonClassUtils as GCU
import org.codehaus.griffon.commons.GriffonContext
import org.codehaus.griffon.commons.GriffonUtil
import org.codehaus.gant.GantBinding
import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils

/**
 * Class that handles Griffon command line interface for running scripts
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

class GriffonScriptRunner {
    static {
        ExpandoMetaClass.enableGlobally()
    }
    static final ANT = new AntBuilder()
    static final RESOLVER  = new PathMatchingResourcePatternResolver()

    static String griffonHome
    static String griffonVersion
    static String userHome
    static ClassLoader rootLoader

    private static String basedirPath
    private static Map bindingVars
    private static Map projectPaths
    private static Properties preInitProps


    static main(String[] args) {

        // Get hold of the Griffon_HOME environment variable if it is
        // available.
        ANT.property(environment:"env")
        griffonHome = ANT.antProject.properties.'env.GRIFFON_HOME'

        // Load the Griffon build properties, either from Griffon_HOME
        // or the classpath.
        if(!griffonHome) {
            //ANT.property(resource:"build.properties")
          println "Environment variable GRIFFON_HOME not set. Please set it to the location of your Griffon installation and try again."
          System.exit(0)
        }
        else {
            ANT.property(file:"${griffonHome}/build.properties")
        }

        // Now we can pick up the Griffon version from the Ant project
        // properties.
        griffonVersion = ANT.antProject.properties.'griffon.version'

        // Show a nice header in the console when running commands.
        println """
Welcome to Griffon ${griffonVersion} - http://griffon.codehaus.org/
Licensed under Apache Standard License 2.0
Griffon home is ${ !griffonHome ? 'not set' : 'set to: ' + griffonHome}
"""

        // If there aren't any arguments, then we don't have a command
        // to execute. So we have to exit.
        if (args.size() == 0 || !args[0].trim()) {
            println "No script name specified. Use 'griffon help' for more info or 'griffon interactive' to enter interactive mode"
            System.exit(0)
        }

        userHome = System.getProperty("user.home").replace('\\', '/') // replace is to deal with windows paths
        rootLoader = getClass().classLoader?.rootLoader ?: Thread.currentThread().getContextClassLoader().rootLoader

        // Work out the base directory for this project.
        def basedir = establishBaseDir().canonicalFile
        basedirPath = basedir.absolutePath

        println "Base Directory: ${basedirPath}"
        System.setProperty("base.dir", basedirPath)

        // Core properties that cannot be overridden.
        bindingVars = [:]
        bindingVars["basedir"] = basedirPath
        bindingVars["baseFile"] = basedir
        bindingVars["baseName"] = basedir.name
        bindingVars["griffonHome"] = griffonHome
        bindingVars["griffonVersion"] = griffonVersion
        bindingVars["userHome"] = userHome
        bindingVars["rootLoader"] = rootLoader

        // Evaluate the arguments to get the name of the script to
        // execute, which environment to run it in, and the arguments
        // to pass to the script. This also evaluates arguments of the
        // form "-Dprop=value" and creates system properties from each
        // one.
        def allArgs = args[0].trim()
        def scriptName = processArgumentsAndReturnScriptName(allArgs)

        // Load the PreInit file for this project if it exists. Note
        // that this does not load any environment-specific settings.
        def preInitConfig = loadPreInit(basedir, bindingVars)
        preInitProps = preInitConfig.toProperties()

        // Work out the file paths associated with different parts of
        // the project, such as where the compiled classes go.
        projectPaths = establishProjectStructure(basedir, userHome)

        // In case parts of the application require access to the various
        // project paths, we add them as system properties.
        System.setProperty(GriffonContext.WORK_DIR, projectPaths["griffonWorkDir"])
        System.setProperty(GriffonContext.PROJECT_CLASSES_DIR, projectPaths["classesDirPath"])
        System.setProperty(GriffonContext.PROJECT_TEST_CLASSES_DIR, projectPaths["testDirPath"])
        System.setProperty(GriffonContext.PROJECT_RESOURCES_DIR, projectPaths["resourcesDirPath"])
        System.setProperty(GriffonContext.PROJECT_WORK_DIR, projectPaths["projectWorkDir"])
        System.setProperty(GriffonContext.PLUGINS_DIR, projectPaths["pluginsDirPath"])
        System.setProperty(GriffonContext.GLOBAL_PLUGINS_DIR, projectPaths["globalPluginsDirPath"])

        // Add some extra binding variables that are now available.
        bindingVars["griffonEnv"] = System.getProperty(GriffonContext.ENVIRONMENT)
        bindingVars["defaultEnv"] = System.getProperty(GriffonContext.ENVIRONMENT_DEFAULT) == "true"
        bindingVars["preInitConfig"] = preInitConfig
        bindingVars["preInitProperties"] = preInitProps

        // Check whether we are currently inside a Griffon project and
        // if not, check whether the script can be run outside a project.
        def scriptsAllowedOutsideProject = ['CreateApp','CreatePlugin','PackagePlugin','Help','ListPlugins','PluginInfo','SetProxy']
        if(!new File(basedirPath, "griffon-app").exists() && (!scriptsAllowedOutsideProject.contains(scriptName))) {
            println "${basedirPath} does not appear to be part of a Griffon application."
            println 'The following commands are supported outside of a project:'
            scriptsAllowedOutsideProject.sort().each {
                println "\t${GCU.getScriptName(it)}"
            }
            println "Run 'griffon help' for a complete list of available scripts."
            println 'Exiting.'
            System.exit(-1)
        }

        // Either run the script or enter interactive mode.
        try {
            if(scriptName.equalsIgnoreCase('interactive')) {
                // This never exits unless an exception is thrown or
                // the process is interrupted via a signal.
                runInteractive()
            }
            else {
                System.exit(callPluginOrGriffonScript(scriptName))
            }
        }
        catch(Throwable t) {
            println "Error executing script ${scriptName}: ${t.message}"
            GriffonUtil.deepSanitize(t)
            t.printStackTrace(System.out)
            System.exit(1)
        }
	}

    /**
     * Runs Griffon in interactive mode.
     */
    static runInteractive() {
        //disable exiting
        System.metaClass.static.exit = { int code ->}
        System.setProperty("griffon.interactive.mode", "true")
        int messageNumber = 0
        def scriptName = ""
        while(true) {
            println "--------------------------------------------------------"
            ANT.input(message:"Interactive mode ready, type your command name in to continue (hit ENTER to run the last command):", addproperty:"griffon.script.name${messageNumber}")

            def enteredName = ANT.antProject.properties."griffon.script.name${messageNumber++}"
            if(enteredName) {
                scriptName = processArgumentsAndReturnScriptName(enteredName)
            }

            if(!scriptName) {
                println "You must enter a command.\n"
                continue
            }

            def now = System.currentTimeMillis()
            callPluginOrGriffonScript(scriptName)
            def end = System.currentTimeMillis()
            println "--------------------------------------------------------"
            println "Command [$scriptName] completed in ${end-now}ms"
        }
    }

    static processArgumentsAndReturnScriptName(allArgs) {
        allArgs = processSystemArguments(allArgs).trim().split(" ")
        def currentParamIndex = 0
        if( isEnvironmentArgs(allArgs[currentParamIndex]) ) {
            // use first argument as environment name and step further
            calculateEnvironment(allArgs[currentParamIndex++])
        } else {
            // first argument is a script name so check for default environment
            setDefaultEnvironment(allArgs[currentParamIndex])
        }

        if( currentParamIndex >= allArgs.size() ) {
            println "You should specify a script to run. Run 'griffon help' for a complete list of available scripts."
            System.exit(0)
        }

        // use current argument as script name and step further
        def paramName = allArgs[currentParamIndex++]
        if (paramName[0] == '-') {
            paramName = paramName[1..-1]
        }
        System.setProperty("current.gant.script", paramName)
        def scriptName = GCU.getNameFromScript(paramName)

        if( currentParamIndex < allArgs.size() ) {
            // if we have additional params provided - store it in system property
            System.setProperty("griffon.cli.args", allArgs[currentParamIndex..-1].join("\n"))
        }
        return scriptName
	}

	static ENV_ARGS = [
            dev:GriffonContext.ENV_DEVELOPMENT,
            prod:GriffonContext.ENV_PRODUCTION,
            test:GriffonContext.ENV_TEST]
    // this map contains default environments for several scripts in form 'script-name':'env-code'
    static DEFAULT_ENVS = [
            'test-app':GriffonContext.ENV_TEST,
            'console':GriffonContext.ENV_TEST,
            'shell':GriffonContext.ENV_TEST,
            'run-webtest':GriffonContext.ENV_TEST]
    private static isEnvironmentArgs(env) {
		ENV_ARGS.keySet().contains(env)
	}
    private static setDefaultEnvironment(args) {
        if(!System.properties."${GriffonContext.ENVIRONMENT}") {
            def environment = DEFAULT_ENVS[args.toLowerCase()]
            environment = environment ?: GriffonContext.ENV_DEVELOPMENT
            System.setProperty(GriffonContext.ENVIRONMENT, environment )
            System.setProperty(GriffonContext.ENVIRONMENT_DEFAULT, "true")
        }
    }
    private static calculateEnvironment(env) {
        def environment = ENV_ARGS[env]
        if( environment ) {
            System.setProperty(GriffonContext.ENVIRONMENT, environment)
        } else {
            setDefaultEnvironment("prod")
        }
    }

	static SCRIPT_CACHE = [:]
	public static callPluginOrGriffonScript(scriptName) {
		def potentialScripts
		def binding
		if(SCRIPT_CACHE[scriptName]) {
			def cachedScript = SCRIPT_CACHE[scriptName]
			potentialScripts = cachedScript.potentialScripts
			binding = cachedScript.binding
		}
		else if (new File(griffonHome).exists()) {
            def pluginsDirPath = projectPaths["pluginsDirPath"]

            potentialScripts = []
            def resourceResolver = { path ->
                try {
                    return RESOLVER.getResources(path)
                } catch (Throwable t) {
                    return [] as Resource[]
                }
            }
            def availableScripts = GriffonPluginUtils.getAvailableScripts(griffonHome,pluginsDirPath,basedirPath,resourceResolver)

            availableScripts.each { Resource res ->
                if(res.file.name == "${scriptName}.groovy") {
                    potentialScripts << res.file
                }
            }
            // Get the paths of any installed plugins and add them to the
	        // initial binding as '<pluginName>PluginDir'.
	        binding = new GantBinding()
	        try {

	            def plugins = GriffonPluginUtils.getPluginDescriptors(basedirPath, pluginsDirPath, resourceResolver)

	            plugins.each { resource ->
	                def matcher = resource.filename =~ /(\S+)GriffonPlugin.groovy/
	                def pluginName = GriffonClassUtils.getPropertyName(matcher[0][1])

	                // Add the plugin path to the binding.
	                binding.setVariable("${pluginName}PluginDir", resource.file.parentFile)
	            }
	        }
	        catch(Exception e) {
	            // No plugins found.
	        }
			SCRIPT_CACHE[scriptName] = new CachedScript(binding:binding, potentialScripts:potentialScripts)
		}

        // Prep the binding with important variables.
        initBinding(binding)

        // The class loader we will use to run Gant. It's the root
        // loader plus all the application's compiled classes.
        def classesDir = new File(projectPaths["classesDirPath"])
        def classLoader = new URLClassLoader([classesDir.toURI().toURL()] as URL[], rootLoader)

        // First try to load the script from its file. If there is no
        // file, then attempt to load it as a pre-compiled script. If
        // that fails, then let the user know and then exit.
        if(potentialScripts.size() > 0) {
			potentialScripts = potentialScripts.unique()
            if(potentialScripts.size() == 1) {
				println "Running script ${potentialScripts[0].absolutePath}"

                // Enable the cache for Gant.
                binding.cacheEnabled = true
                binding.cacheDirectory = new File("${userHome}/.griffon/${griffonVersion}/scriptCache")

                // Setup the script to call.
                def gant = new Gant(
                        potentialScripts[0].absolutePath,
                        binding,
                        classLoader)

                // Invoke the default target.
                return gant.processTargets()
			}
			else {
				println "Multiple options please select:"
				def validArgs = []
				potentialScripts.eachWithIndex { f, i ->
					println "[${i+1}] $f "
					validArgs << i+1
				}
				ANT.input(message: "Enter # ",validargs:validArgs.join(","), addproperty:"griffon.script.number")
                def number = ANT.antProject.properties."griffon.script.number".toInteger()

				println "Running script ${potentialScripts[number-1].absolutePath}"

                // Setup the script to call.
				def gant = new Gant(
                        potentialScripts[number-1].absolutePath,
                        binding,
                        classLoader)

                // Invoke the default target.
				return gant.processTargets()
			}
		}
		else {
            try {
                // Try loading the script as a class.
                Class scriptClass = rootLoader.loadClass(scriptName)

                // OK, the script is pre-compiled - now make sure that
                // it is a script.
                if (Script.isAssignableFrom(scriptClass)) {
                    println "Running pre-compiled script"

                    // Instantiate the script and pass it to Gant.
                    def gant = new Gant(scriptClass.newInstance(), binding, classLoader)
                    return gant.processTargets()
                }
            }
            catch(ClassNotFoundException ex) {
                // No pre-compiled script. We just need to drop through
                // to the following code, which exits the app.
            }

            // No pre-compiled script - we've run out of options.
            println "Script $scriptName not found."
            println "Run 'griffon help' for a complete list of available scripts."
            return 0
		}
	}

    /**
     * Prep the binding. We add the location of Griffon_HOME under
     * the variable name "griffonHome". We also add a closure that
     * should be used with "includeTargets <<" - it takes a string
     * and returns either a file containing the named Griffon script
     * or the script class.
     *
     * So, this:
     *
     *   includeTargets << griffonScript("Init")
     *
     * will load the "Init" script from $Griffon_HOME/scripts if it
     * exists there; otherwise it will load the Init class.
     */
    private static void initBinding(GantBinding binding) {
        def refClosure = { String name ->
            def potentialScript = new File("${griffonHome}/scripts/${name}.groovy")
            return potentialScript.exists() ? potentialScript : rootLoader.loadClass(name)
        }
        binding.setVariable("griffonScript", refClosure)
        def defaultTargetClosure = { String description, Closure body ->
            if (binding.variables['default'] == null) {
                def c = binding.target
                c(['default':description], body)
            }
        }
        binding.setVariable("defaultTarget", defaultTargetClosure)
        binding.setVariable("griffonHome", griffonHome)

        // Add other binding variables, such as Griffon version and
        // environment.
        bindingVars.each { name, value ->
            binding.setVariable(name, value)
        }

        // Add the project paths too!
        projectPaths.each { name, value ->
            binding.setVariable(name, value)
        }

        def resourceClosure = { String destDir, String pattern ->
            println ">> Getting resources for '${pattern}'"
            println ">> Creating directory: $destDir"
            new File(destDir).mkdirs()
            Resource[] resources = RESOLVER.getResources("classpath:${pattern}")
            resources.each {
                println ">>  Resource: $it"
                if (it.readable) {
                    FileCopyUtils.copy(it.inputStream, new FileOutputStream("${destDir}/${it.filename}"))
                }
            }
        }
        resourceClosure.setDelegate(binding)
        binding.setVariable("copyGriffonResources", resourceClosure)

        // Closure for unpacking a JAR file that's on the classpath.
        def unpackClosure = { Map args ->
            def dir = args["dest"] ?: "."
            def src = args["src"]

            // Can't unjar a file from within a JAR, so we copy it to
            // the destination directory first.
            ant.copy(todir: dir) {
                javaresource(name: src)
            }

            // Now unjar it, excluding the META-INF directory.
            ant.unjar(dest: dir, src: "${dir}/${src}") {
                patternset {
                    exclude(name: "META-INF/**")
                }
            }

            // Don't need the JAR file any more, so remove it.
            ant.delete(file: "${dir}/${src}")
        }
        unpackClosure.setDelegate(binding)
        binding.setVariable("griffonUnpack", unpackClosure)
    }

    private static processSystemArguments(allArgs) {
		def lastMatch = null
		allArgs.eachMatch( /-D(.+?)=(.+?)\s+?/ ) { match ->
		   System.setProperty(match[1].trim(),match[2].trim())
           lastMatch = match[0]
		}

		if(lastMatch) {
		   def i = allArgs.lastIndexOf(lastMatch)+lastMatch.size()
		   allArgs = allArgs[i..-1]
		}
		return allArgs
	}

	private static File establishBaseDir() {
		def sysProp = System.getProperty("base.dir")
		def baseDir
		if(sysProp) {
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
	    		while(parentDir != null && !new File(parentDir, "griffon-app").exists()) {
	    			parentDir = parentDir.parentFile
	    		}

	    		if(parentDir != null) {
	    			// if we found the project root, use it
	    			baseDir = parentDir
	    		}
	        }

		}
		return baseDir
	}


    /**
     * Loads the application's PreInit.groovy file if it exists and returns
     * the corresponding config object. If the file does not exist, this
     * returns an empty config.
     */
    private static ConfigObject loadPreInit(File basedir, Map variables = [:]) {
        // Find out whether the file exists, and if so parse it.
        def config
        def preInitFile = new File(basedir, "griffon-app/conf/PreInit.groovy")
        if(preInitFile.exists()) {
            URL preInitUrl = preInitFile.toURI().toURL()
            def slurper = new ConfigSlurper()
            slurper.setBinding(variables)

            config = slurper.parse(preInitUrl)
            config.setConfigFile(preInitUrl)
        }
        else {
            config = new ConfigObject()
        }

        return config
    }

    private static Map establishProjectStructure(File basedir, String userHome) {
        def griffonWorkDir = getPropertyValue(GriffonContext.WORK_DIR, "${userHome}/.griffon/${griffonVersion}")
        def projectWorkDir = "${griffonWorkDir}/projects/${basedir.name}"
        def classesDir = getPropertyValue(GriffonContext.PROJECT_CLASSES_DIR, "$projectWorkDir/classes")
        def resourcesDirPath = getPropertyValue(GriffonContext.PROJECT_RESOURCES_DIR, "$projectWorkDir/resources")
        def testDirPath = getPropertyValue(GriffonContext.PROJECT_TEST_CLASSES_DIR, "$projectWorkDir/test-classes")
        def pluginsDirPath = getPropertyValue(GriffonContext.PLUGINS_DIR, "$projectWorkDir/plugins")
        def globalPluginsDirPath = getPropertyValue(GriffonContext.GLOBAL_PLUGINS_DIR, "$griffonWorkDir/global-plugins")

        def paths = [:]
        paths["griffonWorkDir"] = griffonWorkDir
        paths["projectWorkDir"] = projectWorkDir
        paths["classesDirPath"] = classesDir
        paths["resourcesDirPath"] = resourcesDirPath
        paths["testDirPath"] = testDirPath
        paths["pluginsDirPath"] = pluginsDirPath
        paths["globalPluginsDirPath"] = globalPluginsDirPath
        return paths
    }

    private static getPropertyValue(String propertyName, defaultValue) {
        // First check whether we have a system property with the given name.
        def value = System.getProperty(propertyName)
        if (value != null) return value

        // Now try the PreInit settings.
        value = preInitProps[propertyName]

        // Return the PreInit value if there is one, otherwise use the
        // default.
        return value != null ? value : defaultValue
    }
}

class CachedScript {
	GantBinding binding
	List potentialScripts
}
