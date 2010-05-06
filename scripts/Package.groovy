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

/**
 * Gant script that packages a Griffon application (note: does not create WAR)
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */
import org.codehaus.griffon.util.BuildSettings
import griffon.util.RunMode

includeTargets << griffonScript("_GriffonPackage")

target('default': "Packages a Griffon application.") {
     depends(checkVersion, parseArguments)

     if(!argsMap.params) {
          depends(package_zip,
                  package_jar,
                  package_applet,
                  package_webstart)
     } else {
        // TODO make sure additional package_* targets are
        // available before the following loop
        def internal = ['zip', 'jar', 'applet', 'webstart']
        argsMap.params.each { type ->
           try {
               if(type in internal) {
                   System.setProperty(RunMode.KEY, RunMode.STANDLONE.name)
                   depends("package_"+type)
               } else {
                   event("MakePackage",[type])
               }
           } catch(Exception x) {
               ant.echo(message: "Could not handle package type '${type}'")
               ant.echo(message: x.message)
           }
        }
    }
}


target(prepackage: "packaging steps all standard packaging options do") {
    event("PrepackageStart", [])

    createConfig()

    griffonAppletClass = config.griffon.applet.mainClass ?: defaultGriffonAppletClass
    griffonApplicationClass = config.griffon.application.mainClass ?: defaultGriffonApplicationClass

    packageApp()

    // create general dist dir
    distDir = config.griffon.dist.dir ?: "${basedir}/dist"
    ant.mkdir(dir: distDir)

    // make codebase relative
    if(config.griffon.webstart.codebase == "CHANGE ME") config.griffon.webstart.codebase = "file:./"
    if(argsMap.codebase) config.griffon.webstart.codebase = argsMap.codebase

    event("PrepackageEnd", [])
}

target(package_zip: "Creates a binary distribution and zips it.") {
    event("PackageStart",["zip"])

    depends(prepackage)

    targetDistDir = config.griffon.dist.zip.dir ?: "${distDir}/zip"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    _copyLaunchScripts()
    _copyAppLibs()
// XXX -- NATIVE
    _copyNativeFiles()
// XXX -- NATIVE
    _copySharedFiles(targetDistDir)
    _copyPackageFiles(targetDistDir)
    _zipDist(targetDistDir, false)

    event("PackageEnd",["zip"])
}

target(package_jar: "Creates a single jar distribution and zips it.") {
    event("PackageStart",["jar"])
 
    // GRIFFON-118
    _skipSigning = true
    depends(prepackage)
    _skipSigning = false

    targetDistDir = config.griffon.dist.jar.dir ?: "${distDir}/jar"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)

    String destFileName = argsMap.name ?: config.griffon.jars.jarName
    if(!destFileName.endsWith(".jar")) destFile += ".jar"
    File destFile = new File(destFileName)
    if(!destFile.isAbsolute()) destFile = new File("${targetDistDir}/${destFile}")
    def libjars = ant.fileset(dir: jardir, includes: "*.jar", excludes: config.griffon.jars.jarName)

    def createJarFile = { jarfile, jars, extra = {} ->
        ant.jar(destfile: jarfile, duplicate:'preserve') {
            manifest {
                attribute(name: "Main-Class", value: griffonApplicationClass)
            }
            fileset(dir: classesDirPath) {
                exclude(name: "Config*.class")
            }
            fileset(dir: i18nDir)
            fileset(dir: resourcesDir)
    
            jars.each {
                zipfileset(src: it.toString(),
                          excludes: "META-INF/*.MF,META-INF/*.SF,META-INF/*.RSA,META-INF/*.DSA")
            }
            extra()
        }
        maybePackAndSign(jarfile)
    }

    createJarFile(destFile, libjars)

// XXX -- NATIVE
    doForAllPlatforms { platformDir, platformOs ->
        def destfile = new File(destFile.absolutePath - '.jar' + "-${platformOs}.jar") 
        createJarFile(destfile, libjars) {
            platformDir.eachFileMatch(~/.*\.jar/) {f ->
                zipfileset(src: f.toString(),
                          excludes: "META-INF/*.MF,META-INF/*.SF,META-INF/*.RSA,META-INF/*.DSA")
            }
            File nativeLibDir = new File(platformDir.absolutePath + File.separator + 'native')
            if(nativeLibDir.exists()) fileset(dir: nativeLibDir)
        }
    }
// XXX -- NATIVE

    _copySharedFiles(targetDistDir)
    _copyPackageFiles(targetDistDir)
    if (!config.griffon.dist.jar.nozip)  _zipDist(targetDistDir)

    event("PackageEnd",["jar"])
}

target(package_applet: "Creates an applet distribution and zips it.") {
    System.setProperty(RunMode.KEY, RunMode.APPLET.name)
    event("PackageStart",["applet"])

    depends(prepackage, generateJNLP)

    targetDistDir = config.griffon.dist.applet.dir ?: "${distDir}/applet"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    ant.copy(todir: targetDistDir) {
        fileset(dir: config.griffon.jars.destDir, excludes: config.griffon.webstart.jnlp )
    }
    _copySharedFiles(targetDistDir)

    doPackageTextReplacement(targetDistDir, "*.jnlp,*.html")

    if (!config.griffon.dist.applet.nozip) _zipDist(targetDistDir)

    event("PackageEnd",["applet"])
}

target(package_webstart: "Creates a webstart distribution and zips it.") {
    System.setProperty(RunMode.KEY, RunMode.WEBSTART.name)
    event("PackageStart",["webstart"])

    depends(prepackage, generateJNLP)

    targetDistDir = config.griffon.dist.webstart.dir ?: "${distDir}/webstart"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    ant.copy(todir: targetDistDir) {
        fileset(dir: config.griffon.jars.destDir, excludes: "${config.griffon.applet.jnlp}, ${config.griffon.applet.html}" )
    }
    _copySharedFiles(targetDistDir)

    doPackageTextReplacement(targetDistDir, "*.jnlp,*.html")

    if (!config.griffon.dist.webstart.nozip) _zipDist(targetDistDir)

    event("PackageEnd",["webstart"])
}

target(_copyAppLibs: "") {
    ant.mkdir(dir: "${targetDistDir}/lib")
    ant.copy(todir: "${targetDistDir}/lib") {
        fileset(dir: config.griffon.jars.destDir, includes: "**/*.jar")
    }
}

target(_copyNativeFiles: "") {
    ant.mkdir(dir: "${targetDistDir}/lib")
    copyNativeLibs(config.griffon.jars.destDir, "${targetDistDir}/lib".toString())
}

target(_copyLaunchScripts: "") {
    def javaOpts = []
    if (config.griffon.memory?.min) {
        javaOpts << "-Xms$config.griffon.memory.min"
    }
    if (config.griffon.memory?.max) {
        javaOpts << "-Xmx$config.griffon.memory.max"
    }
    if (config.griffon.memory?.maxPermSize) {
        javaOpts << "-XX:maxPermSize=$config.griffon.memory.maxPermSize"
    }
//    if (isMacOSX) {
//        javaOpts << "-Xdock:name=$griffonAppName"
//        javaOpts << "-Xdock:icon=${griffonHome}/bin/griffon.icns"
//    }
    if (config.griffon.app?.javaOpts) {
      config.griffon.app?.javaOpts.each { javaOpts << it }
    }
    javaOpts = javaOpts ? javaOpts.join(' ') : ""

    ant.mkdir(dir: "${targetDistDir}/bin")
    ant.copy(todir: "${targetDistDir}/bin") {
        fileset(dir: "${griffonSettings.griffonHome}/src/griffon/templates/dist/bin")
    }
    ant.replace( dir: "${targetDistDir}/bin" ) {
        replacefilter(token: "@app.name@", value: griffonAppName)
        replacefilter(token: "@app.version@", value: griffonAppVersion)
        replacefilter(token: "@app.java.opts@", value: javaOpts)
        replacefilter(token: "@app.main.class@", value: griffonApplicationClass)
    }
    ant.move( file: "${targetDistDir}/bin/app.run",     tofile: "${targetDistDir}/bin/${griffonAppName}" )
    ant.move( file: "${targetDistDir}/bin/app.run.bat", tofile: "${targetDistDir}/bin/${griffonAppName}.bat" )
}

_copySharedFiles = { targetDistDir ->
    def sharedFiles = new File("${basedir}/griffon-app/conf/dist/shared")
    if(sharedFiles.exists()) {
        ant.copy(todir: targetDistDir) {
            fileset(dir: sharedFiles)
        }
    }
}

_copyPackageFiles = { targetDistDir ->
    def packageType = targetDistDir[(targetDistDir.lastIndexOf("/")+1)..-1]
    def additionalFiles = new File("${basedir}/griffon-app/conf/dist/${packageType}")
    if(additionalFiles.exists()) {
        ant.copy(todir: targetDistDir) {
            fileset(dir: additionalFiles)
        }
    }
}

_zipDist = { targetDistDir, usePackageType = true ->
    def packageType = targetDistDir[(targetDistDir.lastIndexOf("/")+1)..-1]
    def suffix = !usePackageType ? "": "-${packageType}"
    def zipFileName = "${targetDistDir}/${griffonAppName}-${griffonAppVersion}${suffix}.zip"
    ant.delete(dir: zipFileName, quiet: true, failOnError: false)
    ant.zip(basedir: targetDistDir, destfile: zipFileName)
}
