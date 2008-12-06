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

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import org.codehaus.griffon.commons.*

//import org.codehaus.griffon.commons.GriffonClassUtils as GCU
//import groovy.text.SimpleTemplateEngine
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver
//import org.springframework.core.io.*
//import org.codehaus.griffon.plugins.*
//import org.codehaus.griffon.commons.*
//import org.codehaus.griffon.commons.cfg.*
//import org.codehaus.groovy.control.*
//import org.springframework.util.Log4jConfigurer
//import griffon.util.*

defaultTarget ("Packages a Griffon application.") {
     depends( checkVersion)

     packageApp()
}

includeTargets << griffonScript("Compile" )
includeTargets << griffonScript("_PackagePlugins" )

//scaffoldDir = "${basedir}/web-app/WEB-INF/templates/scaffolding"
configFile = new File("${basedir}/griffon-app/conf/Config.groovy")
applicationFile = new File("${basedir}/griffon-app/conf/Application.groovy")
//webXmlFile = new File("${resourcesDirPath}/web.xml")
log4jFile = new File("${resourcesDirPath}/log4j.properties")
generateLog4jFile = false


String i18nDir = null
String jardir = null



target(createConfig: "Creates the configuration object") {
    depends(compile)
    if (configFile.exists()) {
        def configClass
        try {
            configClass = classLoader.loadClass("Config")
        } catch (ClassNotFoundException cnfe) {
            println "WARNING: No Config.groovy found for the application."
        }
        if (configClass) {
            try {
                config = configSlurper.parse(configClass)
                config.setConfigFile(configFile.toURI().toURL())

                ConfigurationHolder.setConfig(config)
            }
            catch (Exception e) {
                logError("Failed to compile configuration file", e)
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
            catch (Exception e) {
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
}

target( packageApp : "Implementation of package target") {
    depends(compile,createConfig, createStructure, packagePlugins)

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

        if (!(new File(Ant.antProject.replaceProperties(config.signingkey.params.keystore)).exists())) {
            println "Auto-generating a local self-signed key"
            Map genKeyParams = [:]
            genKeyParams.dname =  'CN=Auto Gen Self-Signed Key -- Not for Production, OU=Development, O=Griffon'
            for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigalg', 'keyalg', 'verbose', 'dname', 'validity', 'keysize']) {
                if (config.signingkey.params."$key") {
                    genKeyParams[key] = config.signingkey.params[key]
                }
            }
            Ant.genkey(genKeyParams)
        }
    }
}

target(jarFiles: "Jar up the package files") {
    boolean upToDate = Ant.antProject.properties.appJarUpToDate
    Ant.mkdir(dir:jardir)

    String destFileName = "$jardir/${config.griffon.jars.jarName}"
    if (!upToDate) {
        Ant.jar(destfile:destFileName) {
            fileset(dir:classesDirPath) {
                exclude(name:'Config*.class')
            }
            fileset(dir:i18nDir)
        }
    }
    griffonCopyDist(destFileName, jardir, !upToDate)
}

target(copyLibs: "Copy Library Files") {
    jardir = Ant.antProject.replaceProperties(config.griffon.jars.destDir)
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
    Ant.copy(todir:jardir) { fileset(dir:"${basedir}/lib/", includes:"*.dll") }
    Ant.copy(todir:jardir) { fileset(dir:"${basedir}/lib/", includes:"*.so") }

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

    Ant.copy(file:srcFile, toFile:targetFile, overwrite:force)

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
        Ant.exec(executable:'pack200') {
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
        Ant.signjar(signJarParams)
    }

    if (doJarPacking) {
        // do the for-real packing
        Ant.exec(executable:'pack200') {
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
        replacefilter(token:"@griffonAppVersion@", value:"${griffonAppVersion}" )
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
//recompileCheck = { lastModified, callback ->
//    try {
//        def ant = new AntBuilder()
//        ant.taskdef (     name : 'groovyc' ,
////                        classname : 'org.codehaus.griffon.compiler.GriffonCompiler' ,
//                        classname : 'org.codehaus.groovy.ant.Groovyc' ,
//        )
//        def griffonDir = resolveResources("file:${basedir}/griffon-app/*")
//        ant.path(id:"griffon.classpath",griffonClasspath.curry(getPluginLibDirs(), griffonDir))
//
//        ant.groovyc(destdir:classesDirPath,
//                    classpathref:"griffon.classpath",
//                    resourcePattern:"file:${basedir}/**/griffon-app/**/*.groovy",
//                    encoding:"UTF-8",
//                    projectName:baseName) {
//                    src(path:"${basedir}/griffon-app/models")
//                    src(path:"${basedir}/griffon-app/views")
//                    src(path:"${basedir}/griffon-app/controllers")
//                    src(path:"${basedir}/src/main")
//                    javac(classpathref:"griffon.classpath", debug:"yes")
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