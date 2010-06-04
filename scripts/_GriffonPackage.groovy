/*
 * Copyright 2004-2010 the original author or authors.
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
import griffon.util.RunMode
import griffon.util.BuildSettings
import static griffon.util.GriffonApplicationUtils.osArch

/**
 * Gant script that packages a Griffon application (note: does not create WAR)
 *
 * @author Graeme Rocher
 * @author Danno Ferrin
 *
 * @since 0.4
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_package_called")) return
_griffon_package_called = true

includeTargets << griffonScript("_GriffonCompile")
includeTargets << griffonScript("_PackagePlugins")

configTweaks = []

target(createConfig: "Creates the configuration object") {
    depends(compile)
    event("CreateConfigStart",[])
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
            println "WARNING: No Application.groovy found for the application."
        }
        if (applicationConfigClass) {
            try {
                applicationConfig = configSlurper.parse(applicationConfigClass)
                applicationConfig.setConfigFile(applicationFile.toURI().toURL())
            }
            catch(Exception e) {
                logError("Failed to compile Application configuration file", e)
                exit(1)
            }
        }
    }
    configTweaks.each {tweak -> tweak() }
    event("CreateConfigEnd",[])
}

target(packageApp : "Implementation of package target") {
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

    packageResources()

    startLogging()
    loadPlugins()
    checkKey()
    copyLibs()
    jarFiles()

// XXX -- NATIVE 
    copyPlatformJars(basedir + File.separator + 'lib', new File(jardir).absolutePath) 
    copyNativeLibs(basedir + File.separator + 'lib', new File(jardir).absolutePath) 
// XXX -- NATIVE 

    event("PackagingEnd",[])
}

target(packageResources : "Presp app/plugin resources for packaging") {
    i18nDir = new File("${resourcesDirPath}/griffon-app/i18n")
    ant.mkdir(dir:i18nDir)

    resourcesDir = new File("${resourcesDirPath}/griffon-app/resources")
    ant.mkdir(dir:resourcesDir)

    if(!isPluginProject && !isAddonPlugin) collectArtifactMetadata()

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
        fileset(dir:"${basedir}/src/main") {
            include(name:"**/*")
            exclude(name:"**/*.java")
            exclude(name:"**/*.groovy")
        }
    }
    ant.copy(todir:classesDirPath) {
        fileset(dir:"${basedir}", includes:metadataFile.name)
    }
    ant.copy(todir:resourcesDirPath, failonerror:false) {
        fileset(dir:"${basedir}/griffon-app/conf", includes:"**", excludes:"*.groovy, log4j*, webstart")
        fileset(dir:"${basedir}/src/main") {
            include(name:"**/**")
            exclude(name:"**/*.java")
            exclude(name:"**/*.groovy")
        }
    }
}

collectArtifactMetadata = {
    def artifactPaths = [
        [type: "model",      path: "models",      suffix: "Model"],
        [type: "view",       path: "views",       suffix: "View"],
        [type: "controller", path: "controllers", suffix: "Controller"],
        [type: "service",    path: "services",    suffix: "Service"]
    ]

    event("CollectArtifacts", [artifactPaths])

    def artifacts = [:]
    def pluginDirectories = getPluginDirectories().file
    ([new File(basedir)] + pluginDirectories).each { searchPath ->
        if(!searchPath) return
        searchPath = new File(searchPath.absolutePath, 'griffon-app')
        if(!searchPath.exists()) return
        searchPath.eachFileRecurse { file ->
            artifactPaths.find { entry ->
                def fixedPath = file.path - searchPath.canonicalPath //fix problem when project inside dir "jobs" (eg. hudson stores projects under jobs-directory)
                if(fixedPath =~ entry.path && file.isFile()) {
                    def klass = fixedPath.substring(2 + entry.path.size()).replace(File.separator,".")
                    klass = klass.substring(0, klass.lastIndexOf("."))
                    if(entry.suffix) {
                        if(klass.endsWith(entry.suffix)) artifacts.get(entry.type, []) << klass
                    } else {
                        artifacts.get(entry.type, []) << klass
                    }
                }
            }
        }
    }

    def artifactMetadataFile = new File("${resourcesDirPath}/griffon-app/resources/artifacts.properties")
    artifactMetadataFile.withPrintWriter { writer ->
        artifacts.each { type, list ->
           writer.println("$type = '${list.join(',')}'")
        }
    }
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
    if(argsMap['jar']) return
    boolean upToDate = ant.antProject.properties.appJarUpToDate
    ant.mkdir(dir:jardir)

    String destFileName = "$jardir/${config.griffon.jars.jarName}"
    metainfDirPath = new File("${basedir}/griffon-app/conf/metainf")
    if(!metainfDirPath.list() && RunMode.current == RunMode.STANDALONE) {
        ant.delete(file: destFileName, quiet: true, failonerror: false)
        return
    }

    if (!upToDate) {
        ant.jar(destfile:destFileName) {
            fileset(dir:classesDirPath) {
                exclude(name:'Config*.class')
                exclude(name:'*GriffonPlugin.class')
            }
            fileset(dir:i18nDir)
            fileset(dir:resourcesDir)
            if(metainfDirPath.list()) {
                metainf(dir: metainfDirPath)
            }
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

    copyPlatformJars("${basedir}/lib", jardir)
    
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

    maybePackAndSign(srcFile, targetFile, force)
}

maybePackAndSign = {srcFile, targetFile = srcFile, boolean force = false ->
    // GRIFFON-118 required for avoiding signing jars twice when using jar package target
    if(_skipSigning && !force) return

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
        '-mlatest', // smaller files, set modification time on the files to latest
        '-Htrue', // smaller files, always use DEFLATE hint
        '-O', // smaller files, reorder files if it makes things smaller
    ]


    def signJarParams = [:]
    // prep sign jar params
    if (doJarSigning) {
        // sign jar
        for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigfile', 'verbose', 'internalsf', 'sectionsonly', 'lazy', 'maxmemory', 'preservelastmodified', 'tsaurl', 'tsacert']) {
            if (config.signingkey.params."$key") {
                signJarParams[key] = config.signingkey.params[key]
            }
        }
        signJarParams.jar = targetFile.path
    }

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

    // sign before packing to create accurage space
    if (doJarSigning && doJarPacking) {
        ant.signjar(signJarParams)
    }

    
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

    // sign jar for real
    if (doJarSigning) {
        ant.signjar(signJarParams)
    }

    // pack jar for real
    if (doJarPacking) {
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

    jnlpJars = []
    jnlpUrls = []
    jnlpExtensions = []
    jnlpResources = []
    jnlpProperties = []
    appletJars = []
    remoteJars = []
    appletTagParams = []
    appletScriptParams = []
    config.griffon.extensions?.jarUrls.each {
        def filename = new File(it).getName()
        remoteJars << filename
    }
    // griffon-rt has to come first, it's got the launch classes
    new File(jardir).eachFileMatch(~/griffon-rt-.*.jar/) { f ->
        jnlpJars << "        <jar href='$f.name'/>"
        appletJars << "$f.name"
    }
    config.griffon.extensions?.jarUrls.each {
        appletJars << it
    }
    if (config.griffon.extensions?.jnlpUrls.size() > 0) {
        config.griffon.extensions?.jnlpUrls.each {
            jnlpExtensions << "<extension href='$it' />"
        }
    }
    new File(jardir).eachFileMatch(~/.*\.jar/) { f ->
        if (!(f.name =~ /griffon-rt-.*/) && !remoteJars.contains(f.name)) {
            if(config.griffon.jars.jarName == f.name){
                jnlpJars << "        <jar href='$f.name' main='true' />"
            }else{
                jnlpJars << "        <jar href='$f.name'/>"
            }

            appletJars << "$f.name"
        }
    }
    config.griffon.extensions?.resources?.each { osKey, values ->
        jnlpResources << "<resources os='${PLATFORMS[osKey].webstartName}'>" // TODO resolve arch
        for(j in values?.jars) jnlpResources << "    <jar href='$j' />"
        for(l in values?.nativelibs) jnlpResources << "    <nativelib href='$l' />"
        for(p in values?.props) jnlpResources << "    <property name='${p.key}' value='${p.value}' />"
        if(values.j2se) {
            jnlpResources << "    <j2se "
            values.j2se.each { k, v -> jnlpResources << "        $k='$v'" }
            jnlpResources << "    />"
        }
        jnlpResources << "</resources>"
    }
    config.griffon?.extensions?.props?.each { propName, propValue ->
        jnlpProperties << "    <property name='$propName' value='$propValue' />"
    }
    if(config.griffon?.extensions?.j2se) {
        jnlpProperties << "    <j2se "
        config.griffon.extensions.j2se.each { k, v -> jnlpProperties << "        $k='$v'" }
        jnlpProperties << "    />"
    }
    if (config.griffon.applet?.params?.size() > 0) {
        config.griffon.applet.params.each { paramKey, paramValue ->
            appletTagParams << "    <PARAM NAME='$paramKey' VALUE='$paramValue'/>"
            appletScriptParams << ", ${paramKey}: '$paramValue'"
        }
    }

// XXX -- NATIVE
    doForAllPlatforms { platformDir, platformOs ->
        if(platformDir.list()) {
            jnlpResources << "<resources os='${PLATFORMS[platformOs].webstartName}' arch='${osArch}'>"
            platformDir.eachFileMatch(~/.*\.jar/) { f ->
                jnlpResources << "    <jar href='${platformOs}/${f.name}' />"
            }
            def nativeLibDir = new File(platformDir.absolutePath, 'native')
            if(nativeLibDir.exists() && nativeLibDir.list()) {
                nativeLibDir.eachFileMatch(~/.*\.jar/) { f ->
                    jnlpResources << "    <nativelib href='${platformOs}/native/${f.name}' />"
                    maybePackAndSign(f, f, true)
                }
            }
            jnlpResources << "</resources>"
        }
    }
// XXX -- NATIVE

    memOptions = []
    if (config.griffon.memory?.min) {
        memOptions << "initial-heap-size='$config.griffon.memory.min'"
    }
    if (config.griffon.memory?.max) {
        memOptions << "max-heap-size='$config.griffon.memory.max'"
    }
    if (config.griffon.memory?.maxPermSize) {
        // may be fragile
        memOptions << "java-vm-args='-XX:maxPermSize=$config.griffon.memory.maxPermSize'"
    }

    doPackageTextReplacement(jardir, "*.jnlp,*.html")
}

doPackageTextReplacement = {dir, fileFilters ->
    ant.fileset(dir:dir, includes:fileFilters).each {
        String fileName = it.toString()
        ant.replace(file: fileName) {
            replacefilter(token:"@griffonAppletClass@", value: griffonAppletClass)
            replacefilter(token:"@griffonApplicationClass@", value: griffonApplicationClass)
            replacefilter(token:"@griffonAppName@", value:"${griffonAppName}" )
            replacefilter(token:"@griffonAppVersion@", value:"${griffonAppVersion}" )
            replacefilter(token:"@griffonAppCodebase@", value:"${config.griffon.webstart.codebase}")
            replacefilter(token:"@jnlpFileName@", value: new File(fileName).name )
            replacefilter(token:"@jnlpJars@", value:jnlpJars.join('\n') )
            replacefilter(token:"@jnlpExtensions@", value:jnlpExtensions.join('\n'))
            replacefilter(token:"@jnlpProperties@", value:jnlpProperties.join('\n'))
            replacefilter(token:"@jnlpResources@", value:jnlpResources.join('\n'))
            replacefilter(token:"@appletJars@", value:appletJars.join(',') )
            replacefilter(token:"@memoryOptions@", value:memOptions.join(' ') )
            replacefilter(token:"@applet.width@", value: argsMap.appletWidth ?: defaultAppletWidth )
            replacefilter(token:"@applet.height@", value: argsMap.appletHeight ?: defaultAppletHeight )
            replacefilter(token:"@applet.tag.params@", value: appletTagParams.join('\n') )
            replacefilter(token:"@applet.script.params@", value: appletScriptParams.join(' ') )
        }
    }
}

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

copyPlatformJars = { srcdir, destdir ->
    def env = System.getProperty(BuildSettings.ENVIRONMENT)
    if(env == BuildSettings.ENV_DEVELOPMENT || env == BuildSettings.ENV_TEST) {
        _copyPlatformJars(srcdir.toString(), destdir.toString(), platform)
    } else {
        PLATFORMS.each { entry ->
            _copyPlatformJars(srcdir.toString(), destdir.toString(), entry.key)
        }
    }
}

_copyPlatformJars = { srcdir, destdir, os ->
    File src = new File(srcdir + File.separator + os)
    File dest = new File(destdir + File.separator + os)
    if(src.exists()) {
        ant.mkdir(dir: dest)
        src.eachFileMatch(~/.*\.jar/) { jarfile ->
            griffonCopyDist(jarfile.toString(), dest.toString())
        }
    }
}

copyNativeLibs = { srcdir, destdir ->
    def env = System.getProperty(BuildSettings.ENVIRONMENT)
    if(env == BuildSettings.ENV_DEVELOPMENT || env == BuildSettings.ENV_TEST) {
        _copyNativeLibs(srcdir.toString(), destdir.toString(), platform)
    } else {
        PLATFORMS.each { entry ->
            _copyNativeLibs(srcdir.toString(), destdir.toString(), entry.key)
        }
    }
}

_copyNativeLibs = { srcdir, destdir, os ->
    File src = new File([srcdir, os, 'native'].join(File.separator))
    File dest = new File([destdir, os, 'native'].join(File.separator))
    if(src.exists()) {
        ant.mkdir(dir: dest)
        src.eachFile { srcFile ->
            if(srcFile.toString().endsWith(PLATFORMS[os].nativelib) || srcFile.toString().endsWith('.jar')) {
                griffonCopyDist(srcFile.toString(), dest.absolutePath)
            }
        }
    }
}
