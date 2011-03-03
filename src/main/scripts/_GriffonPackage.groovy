/*
 * Copyright 2004-2011 the original author or authors.
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
import org.codehaus.griffon.commons.*
import griffon.util.Environment
import griffon.util.Metadata
import griffon.util.RunMode
import griffon.util.PlatformUtils
import static griffon.util.GriffonApplicationUtils.osArch
import static griffon.util.GriffonApplicationUtils.is64Bit

/**
 * Gant script that packages a Griffon application (note: does not create WAR)
 *
 * @author Graeme Rocher (Grails 0.4)
 * @author Danno Ferrin
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_package_called")) return
_griffon_package_called = true

includeTargets << griffonScript("_GriffonCompile")
includeTargets << griffonScript("_PackagePlugins")

configTweaks = []

target(createConfig: "Creates the configuration object") {
    depends(compile)
    configTweaks.each {tweak -> tweak() }
}

target(packageApp : "Implementation of package target") {
    depends(createStructure)

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
    jardir = ant.antProject.replaceProperties(buildConfig.griffon.jars.destDir)
    ant.uptodate(property:'appJarUpToDate', targetfile:"${jardir}/${buildConfig.griffon.jars.jarName}") {
        srcfiles(dir:"${basedir}/griffon-app/", includes:"**/*")
        srcfiles(dir:"$classesDirPath", includes:"**/*")
    }

    packageResources()

    loadPlugins()
    checkKey()
    _copyLibs()
    jarFiles()

    event("PackagingEnd",[])
}

target(packageResources : "Presp app/plugin resources for packaging") {
    i18nDir = new File("${resourcesDirPath}/griffon-app/i18n")
    ant.mkdir(dir:i18nDir)

    resourcesDir = new File("${resourcesDirPath}/griffon-app/resources")
    ant.mkdir(dir:resourcesDir)

    if(!isPluginProject || isAddonPlugin) collectArtifactMetadata()

    if(buildConfig.griffon.enable.native2ascii) {
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

    // GRIFFON-189 add environment info to metadata
    def metaFile = new File(classesDirPath, metadataFile.name)
    updateMetadata(metaFile, (Environment.KEY): Environment.current.name)

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
    // def pluginDirectories = pluginSettings.pluginDirectories.file
    // ([new File(basedir)] + pluginDirectories).each { searchPath ->
    searchPath = new File(basedir, 'griffon-app')
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

    if(artifacts) {
        File artifactMetadataDir = new File("${resourcesDirPath}/griffon-app/resources/META-INF")
        artifactMetadataDir.mkdirs()
        File artifactMetadataFile = new File(artifactMetadataDir, '/griffon-artifacts.properties')
        artifactMetadataFile.withPrintWriter { writer ->
            artifacts.each { type, list ->
               writer.println("$type = '${list.join(',')}'")
            }
        }
    }
}

target(checkKey: "Check to see if the keystore exists")  {
    if (buildConfig.griffon.jars.sign) {
        // check for passwords
        // pw is echoed, but jarsigner does that too...
        // when we go to 1.6 only we should use java.io.Console
        if (!buildConfig.signingkey.params.storepass) {
            print "Enter the keystore password:"
            buildConfig.signingkey.params.storepass = System.in.newReader().readLine()
        }
        if (!buildConfig.signingkey.params.keypass) {
            print "Enter the key password [blank if same as keystore] :"
            buildConfig.signingkey.params.keypass = System.in.newReader().readLine() ?: buildConfig.signingkey.params.storepass
        }

        if (!(new File(ant.antProject.replaceProperties(buildConfig.signingkey.params.keystore)).exists())) {
            println "Auto-generating a local self-signed key"
            Map genKeyParams = [:]
            genKeyParams.dname =  'CN=Auto Gen Self-Signed Key -- Not for Production, OU=Development, O=Griffon'
            for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigalg', 'keyalg', 'verbose', 'dname', 'validity', 'keysize']) {
                if (buildConfig.signingkey.params."$key") {
                    genKeyParams[key] = buildConfig.signingkey.params[key]
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

    String destFileName = "$jardir/${buildConfig.griffon.jars.jarName}"
    metainfDirPath = new File("${basedir}/griffon-app/conf/metainf")
    if(Environment.current == Environment.DEVELOPMENT && !metainfDirPath.list() && RunMode.current == RunMode.STANDALONE) {
        ant.delete(file: destFileName, quiet: true, failonerror: false)
        return
    }

    if (!upToDate) {
        ant.jar(destfile:destFileName) {
            fileset(dir:classesDirPath) {
                exclude(name:'BuildConfig*.class')
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

_copyLibs = {
    // jardir = ant.antProject.replaceProperties(buildConfig.griffon.jars.destDir)
    event("CopyLibsStart", [jardir])

// XXX -- NATIVE 
    copyPlatformJars("${basedir}/lib", new File(jardir).absolutePath) 
    copyNativeLibs("${basedir}/lib", new File(jardir).absolutePath) 
    doWithPlugins { pluginName, pluginVersion, pluginDir ->
        copyPlatformJars("${pluginDir}/lib", new File(jardir).absolutePath) 
        copyNativeLibs("${pluginDir}/lib", new File(jardir).absolutePath) 
    }
// XXX -- NATIVE 

    griffonSettings.runtimeDependencies?.each { File f ->
        griffonCopyDist(f.absolutePath, jardir)
    }
    
    event("CopyLibsEnd", [jardir])
}

/**
 * The presence of a .SF, .DSA, or .RSA file in meta-inf means yes
 */
boolean isJarSigned(File jarFile, File targetFile) {
    File fileToSearch  = targetFile.exists() ? targetFile : jarFile

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

griffonCopyDist = { jarname, targetDir, boolean force = false ->
    File srcFile = new File(jarname)
    if (!srcFile.exists()) {
        event("StatusFinal", ["Source jar does not exist: ${srcFile.getName()}"])
        exit(1)
    }

    File targetFile = new File(targetDir + File.separator + srcFile.getName())

    // first do a copy
    // long originalLastMod = targetFile.lastModified()
    force = force || !(buildConfig.signingkey?.params?.lazy)

    ant.copy(file:srcFile, toFile:targetFile, overwrite:force)

    maybePackAndSign(srcFile, targetFile, force)
}

maybePackAndSign = {srcFile, targetFile = srcFile, boolean force = false ->
    if(!srcFile.name.endsWith('.jar')) return
    // GRIFFON-118 required for avoiding signing jars twice when using jar package target
    if(_skipSigning/* && !force*/) return

    // we may already be copied, but not packed or signed
    // first see if the config calls for packing or signing
    // (do this funny dance because unset == true)
    boolean configSaysJarPacking = buildConfig.griffon.jars.pack
    boolean configSaysJarSigning = buildConfig.griffon.jars.sign
    boolean configSaysUnsignJar = !buildConfig.griffon.signingkey.params.lazy

    boolean doJarSigning = configSaysJarSigning
    boolean doJarPacking = configSaysJarPacking

    // if we should sign, check if the jar is already signed
    // don't sign if it appears signed and we're not forced
    boolean jarIsSigned = isJarSigned(srcFile, targetFile)
    if (doJarSigning && !force) {
        doJarSigning = !jarIsSigned
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
        '--segment-limit=-1', // unlimited segmenting
    ]

    debug("Jar $targetFile")
    debug("pack: $doJarPacking, sign: $doJarSigning")
    if(doJarPacking) debug("Pack options $packOptions")

    def signJarParams = [:]
    // prep sign jar params
    if (doJarSigning) {
        // sign jar
        for (key in ['alias', 'storepass', 'keystore', 'storetype', 'keypass', 'sigfile', 'verbose', 'internalsf', 'sectionsonly', 'lazy', 'maxmemory', 'preservelastmodified', 'tsaurl', 'tsacert']) {
            if (buildConfig.signingkey.params."$key") {
                signJarParams[key] = buildConfig.signingkey.params[key]
            }
        }
        signJarParams.jar = targetFile.path

        // GRIFFON-294 remove signatures from jar
        if(jarIsSigned && configSaysUnsignJar) {
            def unpackDir = new File(griffonTmp, srcFile.name[0..-5])
            ant.delete(dir: unpackDir, quiet: true, failonerror: false)
            ant.unjar(src: targetFile, dest: unpackDir)
            ant.delete(quiet: true, failonerror: false) {
                fileset(dir: "${unpackDir}/META-INF", includes: '*.DSA')
                fileset(dir: "${unpackDir}/META-INF", includes: '*.RSA')
                fileset(dir: "${unpackDir}/META-INF", includes: '*.SF')
            }
            ant.jar(basedir: unpackDir, destfile: targetFile)
            ant.delete(dir: unpackDir, quiet: true, failonerror: false)
        }
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
    buildConfig.griffon.extensions?.jarUrls.each {
        def filename = new File(it).getName()
        remoteJars << filename
    }
    // griffon-rt has to come first, it's got the launch classes
    def appJarIsMain = buildConfig.griffon.jars.application.main == true
    new File(jardir).eachFileMatch(~/griffon-rt-.*.jar/) { f ->
        jnlpJars << "        <jar href='$f.name' main='${!appJarIsMain}'/>"
        appletJars << "$f.name"
    }
    buildConfig.griffon.extensions?.jarUrls.each {
        appletJars << it
    }
    if (buildConfig.griffon.extensions?.jnlpUrls.size() > 0) {
        buildConfig.griffon.extensions?.jnlpUrls.each {
            jnlpExtensions << "<extension href='$it' />"
        }
    }
    new File(jardir).eachFileMatch(~/.*\.jar/) { f ->
        if (!(f.name =~ /griffon-rt-.*/) && !remoteJars.contains(f.name)) {
            if(buildConfig.griffon.jars.jarName == f.name){
                jnlpJars << "        <jar href='$f.name' main='${appJarIsMain}' />"
            } else {
                jnlpJars << "        <jar href='$f.name'/>"
            }

            appletJars << "$f.name"
        }
    }
    buildConfig.griffon.extensions?.resources?.each { osKey, values ->
        jnlpResources << "<resources os='${PlatformUtils.PLATFORMS[osKey].webstartName}'>" // TODO resolve arch
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
    buildConfig.griffon?.extensions?.props?.each { propName, propValue ->
        jnlpProperties << "    <property name='$propName' value='$propValue' />"
    }
    if(buildConfig.griffon?.extensions?.j2se) {
        jnlpProperties << "    <j2se "
        buildConfig.griffon.extensions.j2se.each { k, v -> jnlpProperties << "        $k='$v'" }
        jnlpProperties << "    />"
    }
    if (buildConfig.griffon.applet?.params?.size() > 0) {
        buildConfig.griffon.applet.params.each { paramKey, paramValue ->
            appletTagParams << "    <PARAM NAME='$paramKey' VALUE='$paramValue'/>"
            appletScriptParams << ", ${paramKey}: '$paramValue'"
        }
    }

// XXX -- NATIVE
    doForAllPlatforms { platformDir, platformOs ->
        if(platformDir.list()) {
            jnlpResources << "<resources os='${PlatformUtils.PLATFORMS[platformOs].webstartName}' arch='${osArch}'>"
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
    if (buildConfig.griffon.memory?.min) {
        memOptions << "initial-heap-size='$buildConfig.griffon.memory.min'"
    }
    if (buildConfig.griffon.memory?.max) {
        memOptions << "max-heap-size='$buildConfig.griffon.memory.max'"
    }
    if (buildConfig.griffon.memory?.maxPermSize) {
        // may be fragile
        memOptions << "java-vm-args='-XX:maxPermSize=$buildConfig.griffon.memory.maxPermSize'"
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
            replacefilter(token:"@griffonAppCodebase@", value:"${buildConfig.griffon.webstart.codebase}")
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

copyPlatformJars = { srcdir, destdir ->
    def env = Environment.current
    if(env == Environment.DEVELOPMENT || env == Environment.TEST) {
        String plf = platform
        File platformDir = new File(srcdir.toString() + File.separator + plf)
        _copyPlatformJars(srcdir.toString(), destdir.toString(), plf)
        if(!platformDir.exists() && is64Bit) plf -= '64'
        _copyPlatformJars(srcdir.toString(), destdir.toString(), plf)
    } else {
        doForAllPlatforms { key, value ->
            _copyPlatformJars(srcdir.toString(), destdir.toString(), key)
        }
    }
}

_copyPlatformJars = { srcdir, destdir, os ->
    File src = new File(srcdir + File.separator + os)
    File dest = new File(destdir + File.separator + os)
    dest.mkdirs()
    if(src.exists()) {
        ant.mkdir(dir: dest)
        src.eachFileMatch(~/.*\.jar/) { jarfile ->
            griffonCopyDist(jarfile.toString(), dest.toString())
        }
    }
}

copyNativeLibs = { srcdir, destdir ->
    def env = Environment.current
    if(env == Environment.DEVELOPMENT || env == Environment.TEST) {
        String plf = platform
        File platformDir = new File(srcdir.toString() + File.separator + plf)
        _copyNativeLibs(srcdir.toString(), destdir.toString(), plf)
        if(!platformDir.exists() && is64Bit) plf -= '64'
        _copyNativeLibs(srcdir.toString(), destdir.toString(), plf)
    } else {
        doForAllPlatforms { key, value ->
            _copyNativeLibs(srcdir.toString(), destdir.toString(), key)
        }
    }
}

_copyNativeLibs = { srcdir, destdir, os ->
    File src = new File([srcdir, os, 'native'].join(File.separator))
    File dest = new File([destdir, os, 'native'].join(File.separator))
    dest.mkdirs()
    if(src.exists()) {
        ant.mkdir(dir: dest)
        src.eachFile { srcFile ->
            if(srcFile.toString().endsWith(PlatformUtils.PLATFORMS[os].nativelib) || srcFile.toString().endsWith('.jar')) {
                griffonCopyDist(srcFile.toString(), dest.absolutePath)
            }
        }
    }
}
