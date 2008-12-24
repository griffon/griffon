/*
* Copyright 2004-2005 the original author or authors.
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

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import org.apache.log4j.LogManager
import org.codehaus.griffon.commons.*
import org.codehaus.griffon.plugins.logging.Log4jConfig
//import org.codehaus.griffon.commons.cfg.ConfigurationHelper
//import org.springframework.core.io.FileSystemResource

/**
 * Gant script that packages a Griffon application (note: does not create WAR)
 *
 * @author Graeme Rocher
 * @author Danno Ferrin
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonCompile")
includeTargets << griffonScript("_PackagePlugins")


configTweaks = []

target( createConfig: "Creates the configuration object") {
   if(configFile.exists()) {
       def configClass
       try {
           configClass = classLoader.loadClass("Config")
       } catch (ClassNotFoundException cnfe) {
           println "WARNING: No config found for the application."
       }
       if(configClass) {
           try {
               config = configSlurper.parse(configClass)
               config.setConfigFile(configFile.toURI().toURL())

               ConfigurationHolder.setConfig(config)
           }
           catch(Exception e) {
               logError("Failed to compile configuration file",e)
               exit(1)
           }
       }
   }
    if (applicationFile.exists()) {
        def applicationConfigClass
        try {
            applicationConfigClass = classLoader.loadClass("Application")
        } catch (ClassNotFoundException cnfe) {
            println "WARNING: No Appliciton.groovy found for the application."
        }
        if (applicationConfigClass) {
            try {
                applicationConfig = configSlurper.parse(applicationConfigClass)
                applicationConfig.setConfigFile(applicationFile.toURI().toURL())

                //ConfigurationHolder.setConfig(config)
            }
            catch(Exception e) {
                logError("Failed to compile Application configuration file", e)
                exit(1)
            }
        }
    }
//   def dataSourceFile = new File("${basedir}/griffon-app/conf/DataSource.groovy")
//   if(dataSourceFile.exists()) {
//		try {
//		   def dataSourceConfig = configSlurper.parse(classLoader.loadClass("DataSource"))
//		   config.merge(dataSourceConfig)
//		   ConfigurationHolder.setConfig(config)
//		}
//		catch(ClassNotFoundException e) {
//			println "WARNING: DataSource.groovy not found, assuming dataSource bean is configured by Spring..."
//		}
//        catch(Exception e) {
//            logError("Error loading DataSource.groovy",e)
//            exit(1)
//        }
//   }
//   ConfigurationHelper.initConfig(config, null, classLoader)

    configTweaks.each {tweak -> tweak() }
}

target( packageApp : "Implementation of package target") {
	depends(createStructure, packagePlugins)

	try {
        profile("compile") {
            compile()
        }
	}
	catch(Exception e) {
        logError("Compilation error",e)
		exit(1)
	}
    profile("creating config") {
        createConfig()
    }

    // flag if <application>.jar is up to date
    jardir = ant.antProject.replaceProperties(config.griffon.jars.destDir)
    ant.uptodate(property:'appJarUpToDate', targetfile:"${jardir}/${config.griffon.jars.jarName}") {
        srcfiles(dir:"${basedir}/griffon-app/", includes:"**/*")
        srcfiles(dir:"$classesDirPath", includes:"**/*")
    }

//    configureServerContextPath()

    i18nDir = "${resourcesDirPath}/griffon-app/i18n"
    ant.mkdir(dir:i18nDir)

    resourcesDir = "${resourcesDirPath}/griffon-app/resources"
    ant.mkdir(dir:resourcesDir)

//    def files = ant.fileScanner {
//        fileset(dir:"${basedir}/griffon-app/views", includes:"**/*.jsp")
//    }
//
//    if(files.iterator().hasNext()) {
//        ant.mkdir(dir:"${basedir}/web-app/WEB-INF/griffon-app/views")
//        ant.copy(todir:"${basedir}/web-app/WEB-INF/griffon-app/views") {
//            fileset(dir:"${basedir}/griffon-app/views", includes:"**/*.jsp")
//        }
//    }

	if(config.griffon.enable.native2ascii) {
		profile("converting native message bundles to ascii") {
			ant.native2ascii(src:"${basedir}/griffon-app/i18n",
							 dest:i18nDir,
							 includes:"*.properties",
							 encoding:"UTF-8")
		}
	}
	else {
	    ant.copy(todir:i18nDir) {
			fileset(dir:"${basedir}/griffon-app/i18n", includes:"*.properties")
		}
	}
    ant.copy(todir:resourcesDir) {
        fileset(dir:"${basedir}/griffon-app/resources", includes:"**/*.*")
    }
    ant.copy(todir:classesDirPath) {
		fileset(dir:"${basedir}", includes:metadataFile.name)
	}
	ant.copy(todir:resourcesDirPath, failonerror:false) {
		fileset(dir:"${basedir}/griffon-app/conf", includes:"**", excludes:"*.groovy, log4j*, webstart"/*hibernate, spring"*/)
//		fileset(dir:"${basedir}/griffon-app/conf/hibernate", includes:"**/**")
//		fileset(dir:"${basedir}/src/java") {
        fileset(dir:"${basedir}/src/main") {
			include(name:"**/**")
			exclude(name:"**/*.java")
            exclude(name:"**/*.groovy")
		}
	}


    startLogging()

    loadPlugins()
    //generateWebXml()

    checkKey()
    copyLibs()
    jarFiles()
    generateJNLP()
    event("PackagingEnd",[])
}

target(checkKey: "Check to see if the keystore exists")  {
    if (config.griffon.jars.sign) {
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

        if (!(new File(ant.antProject.replaceProperties(config.signingkey.params.keystore)).exists())) {
            println "Auto-generating a local self-signed key"
            Map genKeyParams = [:]
            genKeyParams.dname =  'CN=Auto Gen Self-Signed Key -- Not for Production, OU=Development, O=Griffon'
            for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigalg', 'keyalg', 'verbose', 'dname', 'validity', 'keysize']) {
                if (config.signingkey.params."$key") {
                    genKeyParams[key] = config.signingkey.params[key]
                }
            }
            ant.genkey(genKeyParams)
        }
    }
}

target(jarFiles: "Jar up the package files") {
    boolean upToDate = ant.antProject.properties.appJarUpToDate
    ant.mkdir(dir:jardir)

    String destFileName = "$jardir/${config.griffon.jars.jarName}"
    if (!upToDate) {
        ant.jar(destfile:destFileName) {
            fileset(dir:classesDirPath) {
                exclude(name:'Config*.class')
            }
            fileset(dir:i18nDir)
            fileset(dir:resourcesDir)
        }
    }
    griffonCopyDist(destFileName, jardir, !upToDate)
}

target(copyLibs: "Copy Library Files") {
    jardir = ant.antProject.replaceProperties(config.griffon.jars.destDir)
    event("CopyLibsStart", [jardir])

    fileset(dir:"${griffonHome}/dist", includes:"griffon-rt-*.jar").each {
        griffonCopyDist(it.toString(), jardir)
    }
    fileset(dir:"${griffonHome}/lib", includes:"groovy-all-*.jar").each {
        griffonCopyDist(it.toString(), jardir)
    }

    fileset(dir:"${basedir}/lib/", includes:"*.jar").each {
        griffonCopyDist(it.toString(), jardir)
    }
    
//FIXME    ant.copy(todir:jardir) { fileset(dir:"${basedir}/lib/", includes:"*.dll") }
//FIXME    ant.copy(todir:jardir) { fileset(dir:"${basedir}/lib/", includes:"*.so") }

    event("CopyLibsEnd", [jardir])
}

/**
 * The presence of a .SF, .DSA, or .RSA file in meta-inf means yes
 */
boolean isJarSigned(File jarFile, File targetFile) {
    File fileToSearch  = targetFile.exists() ? targetFile : jarFile;

    ZipFile zf = new ZipFile(fileToSearch)
    try {
        // don't use .each {}, cannot break out of closure
        Enumeration<ZipEntry> entriesEnum = zf.entries()
        while (entriesEnum.hasMoreElements()) {
            ZipEntry ze = entriesEnum.nextElement()
            if (ze.name ==~ 'META-INF/\\w{1,8}\\.(SF|RSA|DSA)') {
                // found a signature file
                return true
            }
            // possible optimization, expect META-INF first?  stop looking when we see other dirs?
        }
        // found no signature files
        return false
    } finally {
        zf.close()
    }
}

griffonCopyDist =  { jarname, targetDir, boolean force = false ->
    File srcFile = new File(jarname);
    if (!srcFile.exists()) {
        event("StatusFinal", ["Source jar does not exist: ${srcFile.getName()}"])
        exit(1)
    }
    File targetFile = new File(targetDir + File.separator + srcFile.getName());

    // first do a copy
    long originalLastMod = targetFile.lastModified()
    force = force || !(config.signingkey?.params?.lazy)

    ant.copy(file:srcFile, toFile:targetFile, overwrite:force)

    // we may already be copied, but not packed or signed
    // first see if the config calls for packing or signing
    // (do this funny dance because unset == true)
    boolean configSaysJarPacking = config.griffon.jars.pack
    boolean configSaysJarSigning = config.griffon.jars.sign

    boolean doJarSigning = configSaysJarSigning
    boolean doJarPacking = configSaysJarPacking

    // if we should sign, check if the jar is already signed
    // don't sign if it appears signed and we're not forced
    if (doJarSigning && !force) {
        doJarSigning = !isJarSigned(srcFile, targetFile)
    }

    // if we should pack, check for forcing or a newer .pack.gz file
    // don't pack if it appears newer and we're not forced
    if (doJarPacking && !force) {
        doJarPacking = !new File(targetFile.path + ".pack.gz").exists()
    }

    // packaging quirk, if we sign or pack, we must do both if either calls for a re-do
    doJarSigning = doJarSigning || (configSaysJarSigning && doJarPacking)
    doJarPacking = doJarPacking || (configSaysJarPacking && doJarSigning)

    //TODO strip old signatures?

    def packOptions = [
        '-S-1', // bug fix, signing large (1MB+) files will validate
        '-mlatest', // smaller files, set modification time on the files to latest
        '-Htrue', // smaller files, always use DEFLATE hint
        '-O', // smaller files, reorder files if it makes things smaller
    ]
    // repack so we can sign pack200
    if (doJarPacking) {
        ant.exec(executable:'pack200') {
            for (option in packOptions) {
                arg(value:option)
            }
            arg(value:'--repack')
            arg(value:targetFile)
        }
    }

    if (doJarSigning) {
        // sign jar
        Map signJarParams = [:]
        for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigfile', 'verbose', 'internalsf', 'sectionsonly', 'lazy', 'maxmemory', 'preservelastmodified', 'tsaurl', 'tsacert']) {
            if (config.signingkey.params."$key") {
                signJarParams[key] = config.signingkey.params[key]
            }
        }

	    signJarParams.jar = targetFile.path
        ant.signjar(signJarParams)
    }

    if (doJarPacking) {
        // do the for-real packing
        ant.exec(executable:'pack200') {
            for (option in packOptions) {
                arg(value:option)
            }
            arg(value:"${targetFile}.pack.gz")
            arg(value:targetFile)
        }

        //TODO? validate packed jar is signed properly

        //TODO? versioning
        // check for version number
        //   copy to version numberd file if version # available

    }

    return targetFile
}

target(generateJNLP:"Generates the JNLP File") {
    ant.copy (todir:jardir, overwrite:true) {
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

    ant.replace(dir:jardir, includes:"*.jnlp,*.html") {
        replacefilter(token:"@griffonAppName@", value:"${griffonAppName}" )
        replacefilter(token:"@griffonAppVersion@", value:"${griffonAppVersion}" )
        replacefilter(token:"@griffonAppCodebase@", value:"${config.griffon.webstart.codebase}")
        replacefilter(token:"@jnlpJars@", value:jnlpJars )
        replacefilter(token:"@appletJars@", value:appletJars )
    }
}


//
//target(configureServerContextPath: "Configuring server context path") {
//    // Get the application context path by looking for a property named 'app.context' in the following order of precedence:
//    //	System properties
//    //	application.properties
//    //	config
//    //	default to griffonAppName if not specified
//
//    serverContextPath = System.getProperty("app.context")
//    serverContextPath = serverContextPath ?: metadata.'app.context'
//    serverContextPath = serverContextPath ?: config.griffon.app.context
//    serverContextPath = serverContextPath ?: griffonAppName
//
//    if(!serverContextPath.startsWith('/')) {
//        serverContextPath = "/${serverContextPath}"
//    }
//}
//

target(startLogging:"Bootstraps logging") {
    LogManager.resetConfiguration()
    if(config.log4j instanceof Closure) {
        profile("configuring log4j") {
            new Log4jConfig().configure(config.log4j)
        }
    }
    else {
        // setup default logging
        new Log4jConfig().configure()
    }
}

//target( generateWebXml : "Generates the web.xml file") {
//	depends(classpath)
//
//	if(config.griffon.config.base.webXml) {
//		def customWebXml =resolveResources(config.griffon.config.base.webXml)
//		if(customWebXml)
//			webXml = customWebXml[0]
//		else {
//			event("StatusError", [ "Custom web.xml defined in config [${config.griffon.config.base.webXml}] could not be found." ])
//			exit(1)
//		}
//	}
//	else {
//	    webXml = new FileSystemResource("${basedir}/src/templates/war/web.xml")
//        def tmpWebXml = "${projectWorkDir}/web.xml.tmp"
//        if(!webXml.exists()) {
//            copyGriffonResource(tmpWebXml, griffonResource("src/war/WEB-INF/web${servletVersion}.template.xml"))
//        }
//        else {
//            ant.copy(file:webXml.file, tofile:tmpWebXml, overwrite:true)
//        }
//        webXml = new FileSystemResource(tmpWebXml)
//        ant.replace(file:tmpWebXml, token:"@griffon.project.key@", value:"${griffonAppName}-${griffonEnv}-${griffonAppVersion}")
//    }
//	def sw = new StringWriter()
//
//    try {
//        profile("generating web.xml from $webXml") {
//			event("WebXmlStart", [webXml.filename])
//            pluginManager.doWebDescriptor(webXml, sw)
//            webXmlFile.withWriter {
//                it << sw.toString()
//            }
//			event("WebXmlEnd", [webXml.filename])
//        }
//    }
//    catch(Exception e) {
//        logError("Error generating web.xml file",e)
//        exit(1)
//    }
//
//}

//target(packageTemplates: "Packages templates into the app") {
//	ant.mkdir(dir:scaffoldDir)
//	if(new File("${basedir}/src/templates/scaffolding").exists()) {
//		ant.copy(todir:scaffoldDir, overwrite:true) {
//			fileset(dir:"${basedir}/src/templates/scaffolding", includes:"**")
//		}
//	}
//	else {
//        copyGriffonResources(scaffoldDir, "src/griffon/templates/scaffolding/*")
//	}
//}
//
//
//// Checks whether the project's sources have changed since the last
//// compilation, and then performs a recompilation if this is the case.
//// Returns the updated 'lastModified' value.
//recompileCheck = { lastModified, callback ->
//    try {
//        def ant = new AntBuilder()
//        def classpathId = "griffon.compile.classpath"
//        ant.taskdef (name: 'groovyc', classname : 'org.codehaus.groovy.griffon.compiler.GriffonCompiler')
//        ant.path(id:classpathId,compileClasspath)
//
//        ant.groovyc(destdir:classesDirPath,
//                    classpathref:classpathId,
//                    encoding:"UTF-8",
//                    projectName:baseName) {
//                    src(path:"${basedir}/src/groovy")
//                    src(path:"${basedir}/griffon-app/domain")
//                    src(path:"${basedir}/griffon-app/utils")
//                    src(path:"${basedir}/src/java")
//                    javac(classpathref:classpathId, debug:"yes")
//
//                }
//        ant = null
//    }
//    catch(Exception e) {
//        compilationError = true
//        logError("Error automatically restarting container",e)
//    }
//
//    def tmp = classesDir.lastModified()
//    if(lastModified < tmp) {
//
//        // run another compile JIT
//        try {
//            callback()
//        }
//        catch(Exception e) {
//            logError("Error automatically restarting container",e)
//        }
//
//        finally {
//           lastModified = classesDir.lastModified()
//        }
//    }
//
//    return lastModified
//}
