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
package org.codehaus.griffon.cli

import gant.Gant
import org.codehaus.griffon.commons.GriffonClassUtils
import org.codehaus.griffon.commons.GriffonContext
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

//import griffon.util.GriffonUtil
/**
 * Class that handles Griffon command line interface for running scripts
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

class GriffonScriptRunner {
    static final ANT = new AntBuilder()
    static final RESOLVER  = new PathMatchingResourcePatternResolver()

    static griffonHome
    static baseDir
    static userHome = System.properties.'user.home'
    static version = "0.1-SNAPSHOT"; //GriffonUtil.getGriffonVersion()
    static classesDir
    static rootLoader

    static main(String[] args) {
        MetaClassRegistry registry = GroovySystem.metaClassRegistry

        if(!(registry.getMetaClassCreationHandler() instanceof ExpandoMetaClassCreationHandle))
            registry.setMetaClassCreationHandle(new ExpandoMetaClassCreationHandle());

        ANT.property(environment:"env")
        griffonHome = ANT.antProject.properties.'env.GRIFFON_HOME'

        if(!griffonHome) {
            println "Environment variable GRIFFON_HOME not set. Please set it to the location of your Griffon installation and try again."
            System.exit(0)
        }

        ANT.property(file:"${griffonHome}/build.properties")
        def griffonVersion =  ANT.antProject.properties.'griffon.version'

        println """
Welcome to Griffon ${griffonVersion} - http://docs.codehaus.org/GROOVY/Griffon
Licensed under Apache Standard License 2.0
Griffon home is set to: ${griffonHome}
        """
        if(args.size() && args[0].trim()) {


            baseDir = establishBaseDir()
            println "Base Directory: ${baseDir.absolutePath}"
            System.setProperty("base.dir", baseDir.absolutePath)

            rootLoader = getClass().classLoader ? getClass().classLoader.rootLoader : Thread.currentThread().getContextClassLoader().rootLoader
            def baseName = new File(baseDir.absolutePath).name
            classesDir = new File("${userHome}/.griffon/${griffonVersion}/projects/${baseName}/classes")

            def allArgs = args[0].trim()

            def scriptName = processArgumentsAndReturnScriptName(allArgs)



            //def scriptsAllowedOutsideProject = ['CreateApp','CreatePlugin','PackagePlugin','Help','ListPlugins','PluginInfo','SetProxy']
            def scriptsAllowedOutsideProject = ['CreateApp',/*'CreatePlugin','PackagePlugin',*/'HelloWorld','Help','SetProxy']
            if(!new File(baseDir.absolutePath, "griffon-app").exists() && (!scriptsAllowedOutsideProject.contains(scriptName))) {
                println "${baseDir.absolutePath} does not appear to be part of a Griffon application."
                println 'The following commands are supported outside of a project:'
                scriptsAllowedOutsideProject.sort().each {
                    println "\t${GriffonClassUtils.getScriptName(it)}"
                }
                println "Run 'griffon help' for a complete list of available scripts."
                println 'Exiting.'
                System.exit(-1)
            }



            try {
                if(scriptName.equalsIgnoreCase('interactive')) {
                    //disable exiting
                    System.metaClass.static.exit = { int code ->}
                    System.setProperty("griffon.interactive.mode", "true")
                    int messageNumber = 0
                    while(true) {
                        println "--------------------------------------------------------"
                        ANT.input(message:"Interactive mode ready, type your command name in to continue (hit ENTER to run the last command):", addproperty:"griffon.script.name${messageNumber}")

                        def enteredName = ANT.antProject.properties."griffon.script.name${messageNumber++}"
                        if(enteredName) {
                            scriptName = processArgumentsAndReturnScriptName(enteredName)
                        }
                        def now = System.currentTimeMillis()
                        callPluginOrGriffonScript(scriptName)
                        def end = System.currentTimeMillis()
                        println "--------------------------------------------------------"
                        println "Command [$scriptName] completed in ${end-now}ms"
                    }
                }
                else {
                    System.exit(callPluginOrGriffonScript(scriptName))
                }


            }
            catch(Throwable t) {
                println "Error executing script ${scriptName}: ${t.message}"
                t.printStackTrace(System.out)
            }

        }
        else {
            println "No script name specified. Use 'griffon help' for more info or 'griffon interactive' to enter interactive mode"
            System.exit(0)
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
        def scriptName = GriffonClassUtils.getNameFromScript(paramName)

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
    static callPluginOrGriffonScript(scriptName) {
        def potentialScripts
        def binding
        if(SCRIPT_CACHE[scriptName]) {
            def cachedScript = SCRIPT_CACHE[scriptName]
            potentialScripts = cachedScript.potentialScripts
            binding = cachedScript.binding
        }
        else {
            potentialScripts = []
            def userHome = ANT.antProject.properties."user.home"

            def scriptLocations = ["${baseDir.absolutePath}/scripts", "${griffonHome}/scripts", "${userHome}/.griffon/scripts"]
            scriptLocations.each {
                def scriptFile = new File("${it}/${scriptName}.groovy")
                if(scriptFile.exists()) {
                    potentialScripts << scriptFile
                }
            }

//             try {
//                 // framework plugins
//                 def basePluginsDir = new File("${griffonHome}/plugins")
//                 if( basePluginsDir.exists() ) {
//                     basePluginsDir.eachDir { pluginInstallDir ->
//                         def latestPluginVersionFile = new File(pluginInstallDir,"latest")
//                         if( latestPluginVersionFile.exists() ){
//                             def latestPluginVersion = latestPluginVersionFile.text.trim()
//                             def scriptFile = new File("${pluginInstallDir}/${latestPluginVersion}/scripts/${scriptName}.groovy")
//                             if(scriptFile.exists()) {
//                                 potentialScripts << scriptFile
//                             }
//                         }
//                     }
//                 }
//             }

//             try {
//                 // application plugins
//                 def pluginScripts = RESOLVER.getResources("file:${baseDir.absolutePath}/plugins/*/scripts/${scriptName}.groovy")
//                 potentialScripts += pluginScripts.collect { it.file }
//             }
//             catch(Exception e) {
//                 println "Note: No plugin scripts found"
//             }
//
//             // Get the paths of any installed plugins and add them to the
//             // initial binding as '<pluginName>PluginDir'.
//             binding = new Binding()
//             try {
//
//                 def plugins = RESOLVER.getResources("file:${baseDir.absolutePath}/plugins/*/*GriffonPlugin.groovy")
//
//                 plugins.each { resource ->
//                     def matcher = resource.filename =~ /(\S+)GriffonPlugin.groovy/
//                     def pluginName = GriffonClassUtils.getPropertyName(matcher[0][1])
//
//                     // Add the plugin path to the binding.
//                     binding.setVariable("${pluginName}PluginDir", resource.file.parentFile)
//                 }
//             }
//             catch(Exception e) {
//                 // No plugins found.
//             }
            SCRIPT_CACHE[scriptName] = new CachedScript(binding:binding, potentialScripts:potentialScripts)
        }



        if(potentialScripts.size()>0) {
            potentialScripts = potentialScripts.unique()
            if(potentialScripts.size() == 1) {
                println "Running script ${potentialScripts[0].absolutePath}"

                def gant = new Gant(binding, new URLClassLoader([classesDir.toURI().toURL()] as URL[], rootLoader))
                return gant.processArgs(["-f", potentialScripts[0].absolutePath,"-c","-d","${userHome}/.griffon/${version}/scriptCache"] as String[])
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
                def gant = new Gant(binding, new URLClassLoader([classesDir.toURI().toURL()] as URL[], rootLoader))
                return gant.processArgs(["-f", potentialScripts[number-1].absolutePath] as String[])
            }
        }
        else {
            println "Script $scriptName not found."
            println "Run 'griffon help' for a complete list of available scripts."
            return 0
        }
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

    private static establishBaseDir() {
        def sysProp = System.getProperty("base.dir")
        def baseDir
        if(sysProp) {
            baseDir = sysProp == '.' ? new File("") : new File(sysProp)
        }
        else {
            baseDir = new File("")
            if(!new File(baseDir.absolutePath, "griffon-app").exists()) {

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

}

class CachedScript {
    Binding binding
    List potentialScripts
}

