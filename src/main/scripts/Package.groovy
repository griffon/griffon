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

/**
 * Gant script that packages a Griffon application (note: does not create WAR)
 *
 * @author Graeme Rocher (Grails 0.4)
 */

import griffon.util.RunMode

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_package_called")) return
_package_called = true

includeTargets << griffonScript("_GriffonPackage")

target('default': "Packages a Griffon application.") {
    depends(checkVersion, parseArguments, createConfig)

    // create general dist dir
    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    ant.mkdir(dir: distDir)
    packageType = ''

    if(argsMap.params) {
        def internal = ['zip', 'jar', 'applet', 'webstart']
        argsMap.params.each { type ->
            packageType = type
	        debug("Making package $type")
            try {
                System.setProperty(RunMode.KEY, RunMode.CUSTOM.name)
                if(type in internal) {
                    depends("package_"+type)
                } else {
                    event("MakePackage",[type])
                }
            } catch(Exception x) {
                ant.echo(message: "Could not handle package type '${type}'")
                ant.echo(message: x.message)
            }
        }
    } else {
        package_zip()
        package_jar()
        package_applet()
        package_webstart()
    }
}

target(prepackage: "packaging steps all standard packaging options do") {
    event("PrepackageStart", [])

    createConfig()

    griffonAppletClass = buildConfig.griffon.applet.mainClass ?: defaultGriffonAppletClass
    griffonApplicationClass = buildConfig.griffon.application.mainClass ?: defaultGriffonApplicationClass

    packageApp()

    // make codebase relative
    if(buildConfig.griffon.webstart.codebase == "CHANGE ME") buildConfig.griffon.webstart.codebase = "file:./"
    if(argsMap.codebase) buildConfig.griffon.webstart.codebase = argsMap.codebase

    event("PrepackageEnd", [])
}

/*
 * make sure targetDistDir is set before calling this target
 */
target(create_binary_package: "Creates a binary distribution") {
    event("CreateBinaryPackageStart",[packageType])

    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)

    depends(prepackage)

    _copyLaunchScripts()
    _copyAppLibs()
// XXX -- NATIVE
    _copyNativeFiles()
// XXX -- NATIVE
    _copySharedFiles(targetDistDir)
    _copyPackageFiles(targetDistDir)

    event("CreateBinaryPackageEnd",[packageType])
}

target(package_zip: "Creates a binary distribution and zips it.") {
    System.setProperty(RunMode.KEY, RunMode.CUSTOM.name)
    packageType = 'zip'
    event("PackageStart",[packageType])

    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    targetDistDir = buildConfig.griffon.dist.zip.dir ?: "${distDir}/zip"
    depends(create_binary_package)
    _zipDist(targetDistDir, false)

    event("PackageEnd",[packageType])
}

target(package_jar: "Creates a single jar distribution and zips it.") {
    System.setProperty(RunMode.KEY, RunMode.CUSTOM.name)
    packageType = 'jar'
    event("PackageStart",[packageType])
 
    // GRIFFON-118
    _skipSigning = true
    depends(prepackage)
    _skipSigning = false

    targetDistDir = buildConfig.griffon.dist.jar.dir ?: "${distDir}/jar"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)

    String destFileName = argsMap.name ?: buildConfig.griffon.jars.jarName
    if(!destFileName.endsWith(".jar")) destFile += ".jar"
    File destFile = new File(destFileName)
    if(!destFile.isAbsolute()) destFile = new File("${targetDistDir}/${destFile}")
    def libjars = ant.fileset(dir: jardir, includes: "*.jar", excludes: buildConfig.griffon.jars.jarName)

    def createJarFile = { jarfile, jars, extra = {} ->
        ant.jar(destfile: jarfile, duplicate:'preserve') {
            manifest {
                attribute(name: "Main-Class", value: griffonApplicationClass)
            }
            fileset(dir: classesDirPath) {
                exclude(name: "BuildConfig*.class")
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
    if (!buildConfig.griffon.dist.jar.nozip)  _zipDist(targetDistDir)

    event("PackageEnd",[packageType])
}

target(package_applet: "Creates an applet distribution and zips it.") {
    System.setProperty(RunMode.KEY, RunMode.APPLET.name)
    packageType = 'applet'
    event("PackageStart",[packageType])

    depends(prepackage, generateJNLP)

    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    targetDistDir = buildConfig.griffon.dist.applet.dir ?: "${distDir}/applet"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    ant.copy(todir: targetDistDir) {
        fileset(dir: buildConfig.griffon.jars.destDir, excludes: buildConfig.griffon.webstart.jnlp )
    }
    _copySharedFiles(targetDistDir)

    doPackageTextReplacement(targetDistDir, "*.jnlp,*.html")

    if (!buildConfig.griffon.dist.applet.nozip) _zipDist(targetDistDir)

    event("PackageEnd",[packageType])
}

target(package_webstart: "Creates a webstart distribution and zips it.") {
    System.setProperty(RunMode.KEY, RunMode.WEBSTART.name)
    packageType = 'webstart'
    event("PackageStart",[packageType])

    depends(prepackage, generateJNLP)

    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    targetDistDir = buildConfig.griffon.dist.webstart.dir ?: "${distDir}/webstart"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    ant.copy(todir: targetDistDir) {
        fileset(dir: buildConfig.griffon.jars.destDir, excludes: "${buildConfig.griffon.applet.jnlp}, ${buildConfig.griffon.applet.html}" )
    }
    _copySharedFiles(targetDistDir)

    doPackageTextReplacement(targetDistDir, "*.jnlp,*.html")

    if (!buildConfig.griffon.dist.webstart.nozip) _zipDist(targetDistDir)

    event("PackageEnd",[packageType])
}

target(_copyAppLibs: "") {
    ant.mkdir(dir: "${targetDistDir}/lib")
    ant.copy(todir: "${targetDistDir}/lib") {
        fileset(dir: buildConfig.griffon.jars.destDir, includes: "**/*.jar")
    }
}

target(_copyNativeFiles: "") {
    ant.mkdir(dir: "${targetDistDir}/lib")
    copyNativeLibs(buildConfig.griffon.jars.destDir, "${targetDistDir}/lib".toString())
}

target(_copyLaunchScripts: "") {
    def javaOpts = []
    if (buildConfig.griffon.memory?.min) {
        javaOpts << "-Xms$buildConfig.griffon.memory.min"
    }
    if (buildConfig.griffon.memory?.max) {
        javaOpts << "-Xmx$buildConfig.griffon.memory.max"
    }
    if (buildConfig.griffon.memory?.maxPermSize) {
        javaOpts << "-XX:maxPermSize=$buildConfig.griffon.memory.maxPermSize"
    }
    if (buildConfig.griffon.app?.javaOpts) {
      buildConfig.griffon.app?.javaOpts.each { javaOpts << it }
    }
    javaOpts = javaOpts ? javaOpts.join(' ') : ""

    ant.mkdir(dir: "${targetDistDir}")
    ant.copy(todir: "${targetDistDir}") {
        fileset(dir: "${griffonSettings.griffonHome}/src/griffon/templates/dist")
    }
    ant.replace( dir: "${targetDistDir}/bin" ) {
        replacefilter(token: "@app.name@", value: griffonAppName)
        replacefilter(token: "@app.version@", value: griffonAppVersion)
        replacefilter(token: "@app.java.opts@", value: javaOpts)
        replacefilter(token: "@app.main.class@", value: griffonApplicationClass)
    }
    ant.move(file: "${targetDistDir}/bin/app.run",     tofile: "${targetDistDir}/bin/${griffonAppName}")
    ant.move(file: "${targetDistDir}/bin/app.run.bat", tofile: "${targetDistDir}/bin/${griffonAppName}.bat")
    ant.chmod(dir: "${targetDistDir}/bin", excludes: '*.bat', perm: 'ugo+x')
}

_copyFiles = { srcdir, path ->
    def files = new File(srcdir in File ? srcdir.absolutePath : srcdir, path)
    if(files.exists()) {
        ant.copy(todir: targetDistDir) {
            fileset(dir: files)
        }
    }
}

_copySharedFiles = { targetDistDir ->
    def sharedFilesPath = 'griffon-app/conf/dist/shared'
    _copyFiles(basedir, sharedFilesPath)
    doWithPlugins { pluginName, pluginVersion, pluginDir ->
        _copyFiles(pluginDir, sharedFilesPath)
    }
}

_copyPackageFiles = { targetDistDir ->
    def packageFilesPath = 'griffon-app/conf/dist/' + packageType
    _copyFiles(basedir, packageFilesPath)
    doWithPlugins { pluginName, pluginVersion, pluginDir ->
        _copyFiles(pluginDir, packageFilesPath)
    }
}

_zipDist = { targetDistDir, usePackageType = true ->
    def suffix = !usePackageType ? "": "-${packageType}"
    def zipFileName = "${targetDistDir}/${griffonAppName}-${griffonAppVersion}${suffix}.zip"
    ant.delete(file: zipFileName, quiet: true, failOnError: false)
    ant.zip(basedir: targetDistDir, destfile: zipFileName)
}
