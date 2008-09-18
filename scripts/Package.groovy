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

/**
 * Gant script that packages a Griffon application (note: does not create WAR)
 *
 * @author Graeme Rocher
 * @author Danno Ferrin
 *
 * @since 0.4
 */

//import org.codehaus.groovy.griffon.commons.GriffonClassUtils as GCU
//import groovy.text.SimpleTemplateEngine
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver
//import org.springframework.core.io.*
//import org.codehaus.groovy.griffon.plugins.*
//import org.codehaus.groovy.griffon.commons.*
//import org.codehaus.groovy.griffon.commons.cfg.*
//import org.codehaus.groovy.control.*
//import org.springframework.util.Log4jConfigurer
//import griffon.util.*


Ant.property(environment:"env")
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"

includeTargets << new File ( "${griffonHome}/scripts/Compile.groovy" )
//includeTargets << new File ( "${griffonHome}/scripts/_PackagePlugins.groovy" )

//scaffoldDir = "${basedir}/web-app/WEB-INF/templates/scaffolding"
configFile = new File("${basedir}/griffon-app/conf/Config.groovy")
applicationFile = new File("${basedir}/griffon-app/conf/Application.groovy")
//webXmlFile = new File("${resourcesDirPath}/web.xml")
log4jFile = new File("${resourcesDirPath}/log4j.properties")
generateLog4jFile = false


String i18nDir = null
String jardir = null


target ('default': "Packages a Griffon application.") {
     depends( checkVersion)

     packageApp()
}

target( createConfig: "Creates the configuration object") {
   if(configFile.exists()) {
        try {
            config = configSlurper.parse(classLoader.loadClass("Config"))
            config.setConfigFile(configFile.toURI().toURL())
//            ConfigurationHolder.setConfig(config)
        }
        catch(Exception e) {
            e.printStackTrace()
            event("StatusFinal", ["Failed to compile configuration file ${configFile}: ${e.message}"])
            exit(1)
        }

   }
    if(applicationFile.exists()) {
         try {
             applicationConfig = configSlurper.parse(classLoader.loadClass("Application"))
             applicationConfig.setConfigFile(applicationFile.toURI().toURL())
 //            ConfigurationHolder.setConfig(config)
         }
         catch(Exception e) {
             e.printStackTrace()
             event("StatusFinal", ["Failed to compile configuration file ${configFile}: ${e.message}"])
             exit(1)
         }

    }
//   def dataSourceFile = new File("${basedir}/griffon-app/conf/DataSource.groovy")
//   if(dataSourceFile.exists()) {
//        try {
//           def dataSourceConfig = configSlurper.parse(classLoader.loadClass("DataSource"))
//           config.merge(dataSourceConfig)
//           ConfigurationHolder.setConfig(config)
//        }
//        catch(Exception e) {
//            println "WARNING: DataSource.groovy not found, assuming dataSource bean is configured by Spring..."
//        }
//   }
//   ConfigurationHelper.initConfig(config, null, classLoader)
}

target( packageApp : "Implementation of package target") {
    depends(createStructure) //,packagePlugins)

    try {
        profile("compile") {
            compile()
        }
    }
    catch(Exception e) {
        event("StatusFinal", ["Compilation error: ${e.message}"])
        e.printStackTrace()
        exit(1)
    }
    profile("creating config") {
        createConfig()
    }

    // flag if <application>.jar is up to date
    jardir = Ant.antProject.replaceProperties(config.griffon.jars.destDir)
    Ant.uptodate(property:'appJarUpToDate', targetfile:"${jardir}/${config.griffon.jars.jarName}") {
        srcfiles(dir:"${basedir}/griffon-app/", includes:"**/*")
        srcfiles(dir:"$classesDirPath", includes:"**/*")
    }

    i18nDir = "${resourcesDirPath}/griffon-app/i18n"
    Ant.mkdir(dir:i18nDir)

//    def files = Ant.fileScanner {
//        fileset(dir:"${basedir}/griffon-app/views", includes:"**/*.jsp")
//    }
//
//    if(files.iterator().hasNext()) {
//        Ant.mkdir(dir:"${basedir}/web-app/WEB-INF/griffon-app/views")
//        Ant.copy(todir:"${basedir}/web-app/WEB-INF/griffon-app/views") {
//            fileset(dir:"${basedir}/griffon-app/views", includes:"**/*.jsp")
//        }
//    }

    if(config.griffon.enable.native2ascii) {
        profile("converting native message bundles to ascii") {
            Ant.native2ascii(src:"${basedir}/griffon-app/i18n",
                             dest:i18nDir,
                             includes:"*.properties",
                             encoding:"UTF-8")
        }
    }
    else {
        Ant.copy(todir:i18nDir) {
            fileset(dir:"${basedir}/griffon-app/i18n", includes:"**/*.properties")
            fileset(dir:"${basedir}/griffon-app/resources", includes:"**/*.*")
        }
    }
    Ant.copy(todir:classesDirPath) {
        fileset(dir:"${basedir}", includes:"application.properties")
    }
    Ant.copy(todir:resourcesDirPath, failonerror:false) {
        fileset(dir:"${basedir}/griffon-app/conf", includes:"**", excludes:"*.groovy, log4j*, webstart")
        //fileset(dir:"${basedir}/griffon-app/conf/hibernate", includes:"**/**")
        //fileset(dir:"${basedir}/src/java") {
        fileset(dir:"${basedir}/src/main") {
            include(name:"**/**")
            exclude(name:"**/*.java")
            exclude(name:"**/*.groovy")
        }
    }


    if(configFile.lastModified() > log4jFile.lastModified() || generateLog4jFile) {
        generateLog4j()
    }
    else if(!log4jFile.exists()) {
        createDefaultLog4J(log4jFile)
    }
    //Log4jConfigurer.initLogging("file:${log4jFile.absolutePath}")

    //loadPlugins()
    //generateWebXml()

    copyLibs()
    jarFiles()
    signFiles()
    generateJNLP()
    event("PackagingEnd",[])
}

target(checkKey: "Check to see if the keystore exists")  {
    // check for passwords
    // pw is echoed, but jarsigner does that too...
    // when we go to 1.6 only we should use java.io.Console
    if (!config.signingkey.params.storepass) {
        print "Enter the keystore password:"
        config.signingkey.params.storepass = System.in.newReader().readLine()
    }
    if (!config.signingkey.params.keypass) {
        print "Enter the key password [blank if same as keystore] :"
        config.signingkey.params.keypass = System.in.newReader().readLine() ?: config.signingkey.params.storepass
    }

    if (!(new File(Ant.antProject.replaceProperties(config.signingkey.params.keystore)).exists())) {
        println "Auto-generating a local self-signed key"
        Map genKeyParams = [:]
        genKeyParams.dname =  'CN=Auto Gen Self-Signed Key -- Not for Production, OU=Development, O=Griffon'
        for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigalg', 'keyalg', 'verbose', 'dname', 'validity', 'keysize']) {
            if (config.signingkey.params."$key") {
                genKeyParams[key] = config.signingkey.params[key]
            }
        }
	println genKeyParams
        Ant.genkey(genKeyParams)
    }
}

target(jarFiles: "Jar up the package files") {
    if (Ant.antProject.properties.appJarUpToDate) return

    Ant.mkdir(dir:jardir)

    Ant.jar(destfile:"$jardir/${config.griffon.jars.jarName}") {
        fileset(dir:classesDirPath)
        fileset(dir:i18nDir)
    }
    //TODO pack200 these files as well...
    //TODO also unpack, so code signing will work.
}

target(copyLibs: "Copy Library Files") {
    jardir = Ant.antProject.replaceProperties(config.griffon.jars.destDir)

    Ant.copy(todir:jardir) { fileset(dir:"${griffonHome}/dist", includes:"griffon-rt-*.jar") }
    Ant.copy(todir:jardir) { fileset(dir:"${griffonHome}/lib/", includes:"groovy-all-*.jar") }

    Ant.copy(todir:jardir) { fileset(dir:"${basedir}/lib/", includes:"*.jar") }
    Ant.copy(todir:jardir) { fileset(dir:"${basedir}/lib/", includes:"*.dll") }
    Ant.copy(todir:jardir) { fileset(dir:"${basedir}/lib/", includes:"*.so") }

    //TODO pack200 these files as well...
    //TODO also unpack, so code signing will work.
}

target(signFiles: "Sign all of the files") {
    checkKey()

    Map signJarParams = [:]
    for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigfile', 'verbose', 'internalsf', 'sectionsonly', 'lazy', 'maxmemory', 'preservelastmodified', 'tsaurl', 'tsacert']) {
        if (config.signingkey.params."$key") {
            signJarParams[key] = config.signingkey.params[key]
        }
    }

    Ant.signjar(signJarParams) {
        fileset(dir:jardir, includes:"*.jar")
    }
}

target(generateJNLP:"Generates the JNLP File") {
    Ant.copy (todir:jardir, overwrite:true) {
        fileset(dir:"${basedir}/griffon-app/conf/webstart")
    }

    jnlpJars = ''
    appletJars = ''
    // griffon-rt has to come first, it's got the launch classes
    new File(jardir).eachFileMatch(~/griffon-rt-.*.jar/) { f ->
        jnlpJars += "        <jar href='$f.name'/>\n"
        appletJars += "$f.name"
    }
    new File(jardir).eachFileMatch(~/.*\.jar/) { f ->
        if (!(f.name =~ /griffon-rt-.*/)) {
            jnlpJars += "        <jar href='$f.name'/>\n"
            appletJars += ",$f.name"
        }
    }

    Ant.replace(dir:jardir, includes:"*.jnlp,*.html") {
        replacefilter(token:"@griffonAppName@", value:"${griffonAppName}" )
        replacefilter(token:"@griffonAppVersion@", value:"${griffonAppName}" )
        replacefilter(token:"@griffonAppCodebase@", value:"${config.griffon.webstart.codebase}")
        replacefilter(token:"@jnlpJars@", value:jnlpJars )
        replacefilter(token:"@appletJars@", value:appletJars )
    }
}


target(generateLog4j:"Generates the Log4j config File") {
    profile("log4j-generation") {
        def log4jConfig = config.log4j
        try {
            if(log4jConfig) {
                if(log4jConfig instanceof ConfigObject) {
                    def props = log4jConfig.toProperties("log4j")
                    log4jFile.withOutputStream { out ->
                        props.store(out, "Griffon' Log4j Configuration")
                    }
                }
                else {
                    log4jFile.write(log4jConfig.toString())
                }
            }
            else {
                // default log4j settings
                createDefaultLog4J(log4jFile)
            }
        }
        catch(Exception e) {
            event("StatusFinal", [ "Error creating Log4j config: " + e.message ])
            exit(1)
        }
    }
}

def createDefaultLog4J(logDest) {
    logDest <<  '''
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.rootLogger=error,stdout
'''

}

//target(loadPlugins:"Loads Griffon' plugins") {
//    if(!PluginManagerHolder.pluginManager) { // plugin manager already loaded?
//        compConfig.setTargetDirectory(classesDir)
//        def unit = new CompilationUnit ( compConfig , null , new GroovyClassLoader(classLoader) )
//        def pluginFiles = pluginResources.file
//
//        for(plugin in pluginFiles) {
//            def className = plugin.name - '.groovy'
//            def classFile = new File("${classesDirPath}/${className}.class")
//            if(plugin.lastModified() > classFile.lastModified())
//                  unit.addSource ( plugin )
//        }
//
//        try {
//            profile("compiling plugins") {
//                unit.compile ()
//            }
//            def application
//            def pluginClasses = []
//            profile("construct plugin manager with ${pluginFiles.inspect()}") {
//                for(plugin in pluginFiles) {
//                   def className = plugin.name - '.groovy'
//                   pluginClasses << classLoader.loadClass(className)
//                }
//                if(griffonApp == null) {
//                    griffonApp = new DefaultGriffonContext(new Class[0], new GroovyClassLoader(classLoader))
//                }
//                pluginManager = new DefaultGriffonPluginManager(pluginClasses as Class[], griffonApp)
//
//                PluginManagerHolder.setPluginManager(pluginManager)
//            }
//            profile("loading plugins") {
//                event("PluginLoadStart", [pluginManager])
//                pluginManager.loadPlugins()
//
//
//                def loadedPlugins = pluginManager.allPlugins?.findAll { pluginClasses.contains(it.instance.getClass()) }*.name
//                if(loadedPlugins)
//                    event("StatusUpdate", ["Loading with installed plug-ins: ${loadedPlugins}"])
//
//                pluginManager.doArtefactConfiguration()
//                griffonApp.initialise()
//                event("PluginLoadEnd", [pluginManager])
//            }
//        }
//        catch (Exception e) {
//            GriffonUtil.deepSanitize(e).printStackTrace()
//            event("StatusFinal", [ "Error loading plugin manager: " + e.message ])
//            exit(1)
//        }
//    }
//    else {
//        // Add the plugin manager to the binding so that it can be accessed
//        // from any target.
//        pluginManager = PluginManagerHolder.pluginManager
//    }
//}

//target( generateWebXml : "Generates the web.xml file") {
//    depends(classpath)
//
//    if(config.griffon.config.base.webXml) {
//        def customWebXml =resolveResources(config.griffon.config.base.webXml)
//        if(customWebXml)
//            webXml = customWebXml[0]
//        else {
//            event("StatusError", [ "Custom web.xml defined in config [${config.griffon.config.base.webXml}] could not be found." ])
//            exit(1)
//        }
//    }
//    else {
//        webXml = new FileSystemResource("${basedir}/src/templates/war/web.xml")
//        if(!webXml.exists()) {
//            def tmpWebXml = "${userHome}/.griffon/${griffonVersion}/projects/${baseName}/web.xml.tmp"
//            Ant.copy(file:"${griffonHome}/src/war/WEB-INF/web${servletVersion}.template.xml", tofile:tmpWebXml)
//
//            Ant.replace(file:tmpWebXml, token:"@griffon.project.key@", value:"${griffonAppName}")
//
//           webXml = new FileSystemResource(tmpWebXml)
//        }
//    }
//    def sw = new StringWriter()
//
//    try {
//        profile("generating web.xml from $webXml") {
//            event("WebXmlStart", [webXml.filename])
//            pluginManager.doWebDescriptor(webXml, sw)
//            webXmlFile.withWriter {
//                it << sw.toString()
//            }
//            event("WebXmlEnd", [webXml.filename])
//        }
//    }
//    catch(Exception e) {
//        event("StatusError", [ e.message ])
//        exit(1)
//    }
//
//}
//
//target(packageTemplates: "Packages templates into the app") {
//    Ant.mkdir(dir:scaffoldDir)
//    if(new File("${basedir}/src/templates/scaffolding").exists()) {
//        Ant.copy(todir:scaffoldDir, overwrite:true) {
//            fileset(dir:"${basedir}/src/templates/scaffolding", includes:"**")
//        }
//    }
//    else {
//        Ant.copy(todir:scaffoldDir, overwrite:true) {
//            fileset(dir:"${griffonHome}/src/griffon/templates/scaffolding", includes:"**")
//        }
//    }
//
//}


// Checks whether the project's sources have changed since the last
// compilation, and then performs a recompilation if this is the case.
// Returns the updated 'lastModified' value.
recompileCheck = { lastModified, callback ->
    try {
        def ant = new AntBuilder()
        ant.taskdef (     name : 'groovyc' ,
//                        classname : 'org.codehaus.groovy.griffon.compiler.GriffonCompiler' ,
                        classname : 'org.codehaus.groovy.ant.Groovyc' ,
        )
        def griffonDir = resolveResources("file:${basedir}/griffon-app/*")
        def pluginLibs = resolveResources("file:${basedir}/plugins/*/lib")
        ant.path(id:"griffon.classpath",griffonClasspath.curry(pluginLibs, griffonDir))

        ant.groovyc(destdir:classesDirPath,
                    classpathref:"griffon.classpath",
                    resourcePattern:"file:${basedir}/**/griffon-app/**/*.groovy",
                    encoding:"UTF-8",
                    projectName:baseName) {
                    src(path:"${basedir}/src/groovy")
                    src(path:"${basedir}/griffon-app/domain")
                    src(path:"${basedir}/src/java")
                    javac(classpathref:"griffon.classpath", debug:"yes")

                }
        ant = null
    }
    catch(Exception e) {
        compilationError = true
        event("StatusUpdate", ["Error automatically restarting container: ${e.message}"])
        GriffonUtil.sanitize(e)
        e.printStackTrace()
    }

    def tmp = classesDir.lastModified()
    if(lastModified < tmp) {

        // run another compile JIT
        try {
            callback()
        }
        catch(Exception e) {
            event("StatusUpdate", ["Error automatically restarting container: ${e.message}"])
            e.printStackTrace()
        }

        finally {
           lastModified = classesDir.lastModified()
        }
    }

    return lastModified
}