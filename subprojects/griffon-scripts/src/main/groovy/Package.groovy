/*
 * Copyright 2004-2013 the original author or authors.
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

import griffon.util.GriffonNameUtils
import griffon.util.PlatformUtils
import griffon.util.RunMode

import static griffon.util.GriffonApplicationUtils.platform

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_package_called')) return
_package_called = true

includeTargets << griffonScript('_GriffonPackage')
includeTargets << griffonScript('_GriffonClean')

ant.taskdef(name: 'fileMerge', classname: 'org.codehaus.griffon.ant.taskdefs.FileMergeTask')

target(name: 'package', description: 'Packages a Griffon project according to its type', prehook: null, posthook: null) {
    if (griffonSettings.isPluginProject()) {
        includeTargets << griffonScript('PackagePlugin')
        depends(packagePlugin)
        return
    } else if (griffonSettings.isArchetypeProject()) {
        includeTargets << griffonScript('PackageArchetype')
        depends(packageArchetype)
        return
    } else {
        depends(packageApplication)
    }
}

target(name: 'packageApplication', description: 'Packages a GriffonApplication', prehook: null, posthook: null) {
    depends(cleanAll, createConfig)

    // create general dist dir
    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    ant.mkdir(dir: distDir)
    packageType = ''

    def makePackage = { type ->
        packageType = type
        debug("Making package $type")
        try {
            System.setProperty(RunMode.KEY, RunMode.CUSTOM.name)
            if (type in ['zip', 'jar', 'applet', 'webstart']) {
                depends("package_" + type)
            } else {
                event('MakePackage', [type])
            }
        } catch (Exception x) {
            ant.echo(message: "Could not handle package type '${type}'")
            ant.echo(message: x.message)
            if (getPropertyValue('griffon.package.abort.onfailure', false)) {
                exit(1)
            }
        }
    }
    if (argsMap.params) {
        argsMap.params.each { type -> makePackage(type) }
    } else if (buildConfig.griffon.packaging) {
        buildConfig.griffon.packaging.each { type -> makePackage(type) }
    } else {
        package_zip()
        package_jar()
        package_applet()
        package_webstart()
    }
}
setDefaultTarget('package')

target(name: 'prepackage', description: "packaging steps all standard packaging options do", prehook: null, posthook: null) {
    createConfig()

    griffonAppletClass = buildConfig.griffon.applet.mainClass
    griffonApplicationClass = buildConfig.griffon.application.mainClass

    packageApp()

    // use absolute path to current dir if codebase is not specified
    if (buildConfig.griffon.webstart.codebase == "CHANGE ME") {
        buildConfig.griffon.webstart.codebase = 'file://' + new File('dist/applet').canonicalFile.absolutePath
    }
    if (argsMap.codebase) buildConfig.griffon.webstart.codebase = argsMap.codebase
}

/*
 * make sure targetDistDir is set before calling this target
 */
target(name: 'create_binary_package', description: "Creates a binary distribution", prehook: null, posthook: null) {
    event("CreateBinaryPackageStart", [packageType])

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
    _copyExternalResources(targetDistDir)

    event("CreateBinaryPackageEnd", [packageType])
}

target(name: 'package_zip', description: "Creates a binary distribution and zips it.", prehook: null, posthook: null) {
    System.setProperty(RunMode.KEY, RunMode.CUSTOM.name)
    packageType = 'zip'
    event("PackageStart", [packageType])

    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    targetDistDir = buildConfig.griffon.dist.zip.dir ?: "${distDir}/zip"
    depends(create_binary_package)
    _zipDist(targetDistDir, false)

    event("PackageEnd", [packageType])
}

target(name: 'package_jar', description: "Creates a single jar distribution and zips it.", prehook: null, posthook: null) {
    System.setProperty(RunMode.KEY, RunMode.CUSTOM.name)
    packageType = 'jar'
    event("PackageStart", [packageType])

    targetDistDir = buildConfig.griffon.dist.jar.dir ?: "${distDir}/jar"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)

    // GRIFFON-118
    _skipSigning = true
    depends(prepackage)
    _skipSigning = false

    String targetPlatform = argsMap.platform && PlatformUtils.PLATFORMS[argsMap.platform] ? argsMap.platform : platform

    String destFileName = argsMap.name ?: buildConfig.griffon.jars.jarName
    if (destFileName.endsWith('.jar')) destFileName -= '.jar'
    if (!destFileName.endsWith('-' + targetPlatform)) destFileName += '-' + targetPlatform
    destFileName += '.jar'
    File destFile = new File(destFileName)
    if (!destFile.isAbsolute()) destFile = new File("${targetDistDir}/${destFile}")
    def libjars = ant.fileset(dir: jardir, includes: '*.jar')
    File mergeDir = new File("${projectWorkDir}/merge")
    String signaturesPattern = 'META-INF/*.MF,META-INF/*.SF,META-INF/*.RSA,META-INF/*.DSA'

// XXX -- NATIVE
    File platformDir = new File("${jardir}/${targetPlatform}")
// XXX -- NATIVE

    def jarfileAggregator = {
        libjars.each { jar ->
            zipfileset(src: jar.toString(), excludes: signaturesPattern)
        }
// XXX -- NATIVE
        if (platformDir.exists()) {
            platformDir.eachFileMatch(~/.*\.jar/) {f ->
                zipfileset(src: f.toString(), excludes: signaturesPattern)
            }
        }
// XXX -- NATIVE
    }

    ant.fileMerge(dir: mergeDir, applicationName: griffonAppName, jarfileAggregator)

    mergeManifest()
    ant.jar(destfile: destFile, duplicate: 'preserve') {
        manifest {
            manifestMap.each { k, v ->
                attribute(name: k, value: v)
            }
        }
        fileset(dir: mergeDir)
        jarfileAggregator()
// XXX -- NATIVE
        File nativeLibDir = new File(platformDir.canonicalPath + File.separator + 'native')
        if (nativeLibDir.exists()) fileset(dir: nativeLibDir)
// XXX -- NATIVE
    }
    ant.delete(dir: mergeDir)
    maybePackAndSign(destFile)

    _copySharedFiles(targetDistDir)
    _copyPackageFiles(targetDistDir)
    if (!buildConfig.griffon.dist.jar.nozip) _zipDist(targetDistDir)

    event("PackageEnd", [packageType])
}

target(name: 'package_applet', description: "Creates an applet distribution and zips it.", prehook: null, posthook: null) {
    System.setProperty(RunMode.KEY, RunMode.APPLET.name)
    packageType = 'applet'
    event("PackageStart", [packageType])

    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    targetDistDir = buildConfig.griffon.dist.applet.dir ?: "${distDir}/applet"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)

    depends(prepackage)
    generateJNLP()

    ant.copy(todir: targetDistDir, overwrite: true) {
        fileset(dir: buildConfig.griffon.jars.destDir, excludes: buildConfig.griffon.webstart.jnlp)
    }
    _copySharedFiles(targetDistDir)

    doPackageTextReplacement(targetDistDir, "*.jnlp,*.html")

    signJNLP()

    if (!buildConfig.griffon.dist.applet.nozip) _zipDist(targetDistDir)

    event("PackageEnd", [packageType])
}

target(name: 'package_webstart', description: "Creates a webstart distribution and zips it.", prehook: null, posthook: null) {
    System.setProperty(RunMode.KEY, RunMode.WEBSTART.name)
    packageType = 'webstart'
    event("PackageStart", [packageType])

    distDir = buildConfig.griffon.dist.dir ?: "${basedir}/dist"
    targetDistDir = buildConfig.griffon.dist.webstart.dir ?: "${distDir}/webstart"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)

    depends(prepackage)
    generateJNLP()

    ant.copy(todir: targetDistDir, overwrite: true) {
        fileset(dir: buildConfig.griffon.jars.destDir, excludes: "${buildConfig.griffon.applet.jnlp}, ${buildConfig.griffon.applet.html}")
    }
    _copySharedFiles(targetDistDir)

    doPackageTextReplacement(targetDistDir, "*.jnlp,*.html")

    signJNLP()

    if (!buildConfig.griffon.dist.webstart.nozip) _zipDist(targetDistDir)

    event("PackageEnd", [packageType])
}

signJNLP = {
    if (!buildConfig.griffon.jnlp.sign) return

    File jnlpFile = packageType == 'webstart' ?
        new File(buildConfig.griffon.webstart.jnlp ?: 'application.jnlp') :
        new File(buildConfig.griffon.applet.jnlp ?: 'applet.jnlp')
    File jnlpUpdateDir = new File("${projectTargetDir}/jnlp")
    File jnlpInf = new File(jnlpUpdateDir, 'JNLP-INF')

    File tmpDir = new File(System.getProperty('java.io.tmpdir'))

    ant.delete(dir: jnlpInf, quiet: true, failonerror: false)
    ant.mkdir(dir: jnlpInf)
    ant.copy(file: mainJarFile,
        todir: tmpDir)
    ant.copy(file: "${targetDistDir}/${jnlpFile}",
        tofile: "${jnlpInf}/APPLICATION.JNLP")
    ant.jar(destfile: "${tmpDir}/${mainJarFile.name}", update: true, filesonly: true) {
        fileset(dir: jnlpUpdateDir)
    }
    griffonCopyDist("${tmpDir}/${mainJarFile.name}".toString(), targetDistDir, true)
    ant.delete(file: "${tmpDir}/${mainJarFile.name}", quiet: true, failonerror: false)
}

target(name: '_copyAppLibs', description: "", prehook: null, posthook: null) {
    ant.mkdir(dir: "${targetDistDir}/lib")
    ant.copy(todir: "${targetDistDir}/lib", overwrite: true) {
        fileset(dir: buildConfig.griffon.jars.destDir, includes: "**/*.jar")
    }
}

target(name: '_copyNativeFiles', description: "", prehook: null, posthook: null) {
    ant.mkdir(dir: "${targetDistDir}/lib")
    copyNativeLibs(buildConfig.griffon.jars.destDir, "${targetDistDir}/lib".toString())
}

target(name: '_copyLaunchScripts', description: "", prehook: null, posthook: null) {
    def javaOpts = []
    if (buildConfig.griffon.memory?.min) {
        javaOpts << "-Xms$buildConfig.griffon.memory.min"
    }
    if (buildConfig.griffon.memory?.max) {
        javaOpts << "-Xmx$buildConfig.griffon.memory.max"
    }
    if (buildConfig.griffon.memory?.minPermSize && buildConfig.griffon.memory?.maxPermSize) {
        javaOpts << "-XX:MaxPermSize=$buildConfig.griffon.memory.maxPermSize"
        javaOpts << "-XX:PermSize=$buildConfig.griffon.memory.minPermSize"
    }
    if (buildConfig.griffon.app?.javaOpts) {
        buildConfig.griffon.app.javaOpts.each { javaOpts << it }
    }
    javaOpts = javaOpts ? javaOpts.join(' ') : ""

    ant.mkdir(dir: "${targetDistDir}")
    griffonUnpack(dest: targetDistDir, src: 'griffon-dist-files.jar')
    ant.delete(file: "${targetDistDir}/META-INF", quiet: true)
    ant.copy(file: resolveApplicationIcnsFile(), tofile: "${targetDistDir}/${griffonAppName}.icns")

    ant.replace(dir: "${targetDistDir}/bin") {
        replacefilter(token: "@app.name@", value: GriffonNameUtils.capitalize(griffonAppName))
        replacefilter(token: "@app.version@", value: griffonAppVersion)
        replacefilter(token: "@app.java.opts@", value: javaOpts)
        replacefilter(token: "@app.main.class@", value: griffonApplicationClass)
    }
    ant.move(file: "${targetDistDir}/bin/app.run", tofile: "${targetDistDir}/bin/${griffonAppName}")
    ant.move(file: "${targetDistDir}/bin/app.run.bat", tofile: "${targetDistDir}/bin/${griffonAppName}.bat")
    ant.chmod(dir: "${targetDistDir}/bin", excludes: '*.bat', perm: 'ugo+x')
}

_copyFiles = { srcdir, path ->
    def files = new File(srcdir in File ? srcdir.absolutePath : srcdir, path)
    if (files.exists()) {
        ant.copy(todir: targetDistDir, overwrite: true) {
            fileset(dir: files)
        }
    }
}

_copySharedFiles = { targetDistDir ->
    def sharedFilesPath = 'griffon-app/conf/dist/shared'
    _copyFiles(basedir, sharedFilesPath)
    pluginSettings.doWithProjectPlugins { pluginName, pluginVersion, pluginDir ->
        _copyFiles(pluginDir, sharedFilesPath)
    }
}

_copyPackageFiles = { targetDistDir ->
    def packageFilesPath = 'griffon-app/conf/dist/' + packageType
    _copyFiles(basedir, packageFilesPath)
    pluginSettings.doWithProjectPlugins { pluginName, pluginVersion, pluginDir ->
        _copyFiles(pluginDir, packageFilesPath)
    }
}

_copyExternalResources = { targetDistDir ->
    File targetResourcesDir = new File("${targetDistDir}/resources")
    targetResourcesDir.mkdirs()
    File sourceResourcesDir = new File("${basedir}/griffon-app/resources-external")
    if (sourceResourcesDir.exists()) {
        ant.copy(todir: targetResourcesDir, overwrite: true) {
            fileset(dir: sourceResourcesDir, excludes: '**/*.properties')
        }
        if (buildConfig.griffon.enable.native2ascii) {
            ant.native2ascii(src: sourceResourcesDir,
                dest: targetResourcesDir,
                includes: '**/*.properties',
                encoding: 'UTF-8')
        } else {
            ant.copy(todir: targetResourcesDir, overwrite: true) {
                fileset(dir: sourceResourcesDir, includes: '**/*.properties')
            }
        }
    }
}

_zipDist = { targetDistDir, usePackageType = true ->
    def suffix = usePackageType ? "-${packageType}" : ""
    def zipFileName = "${targetDistDir}/${griffonAppName}-${griffonAppVersion}${suffix}.zip"
    ant.delete(file: zipFileName, quiet: true, failOnError: false)
    ant.zip(basedir: targetDistDir, destfile: zipFileName)
}
