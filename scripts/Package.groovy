/*
* Copyright 2004-2009 the original author or authors.
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
import static griffon.util.GriffonApplicationUtils.isMacOSX

includeTargets << griffonScript("_GriffonPackage")

target (_package: "Packages a Griffon application. Note: To create WAR use 'griffon war'") {
     depends(checkVersion, parseArguments)

     distDir = "${basedir}/dist"
     ant.mkdir(dir: distDir)

     if(!argsMap.params) {
          package_zip()
          package_jar()
          package_applet()
          package_webstart()
     } else {
        // TODO make sure additional package_* targets are
        // available before the following loop
        argsMap.params.each { type ->
           try {
               depends("package_"+type)
           } catch(MissingPropertyException x) {
               ant.echo(message: "Don't know how to handle package type '${type}'")
               ant.echo(message: x.message)
           }
        }
     }
}

target(package_zip: "") {
    event("PackageStart",["zip"])

    packageApp()
    targetDistDir = "${distDir}/zip"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    _copyLaunchScripts()
    _copyAppLibs()
    ant.copy(todir: "${targetDistDir}") {
        fileset(dir: "${basedir}/griffon-app/conf/dist/shared")
    }
    def zipFileName = "${targetDistDir}/${griffonAppName}-${griffonAppVersion}.zip"
    ant.delete(dir: zipFileName, quiet: true, failOnError: false)
    ant.zip(basedir: "${targetDistDir}", destfile: zipFileName)

    event("PackageEnd",["zip"])
}

target(package_jar: "") {
    event("PackageStart",["jar"])

    packageApp()
    targetDistDir = "${distDir}/jar"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    destFileName = argsMap.name ?: config.griffon.jars.jarName
    if(!destFileName.endsWith(".jar")) destFileName += ".jar"
    destFileName = new File(destFileName)
    if(!destFileName.isAbsolute()) destFileName = new File("${targetDistDir}/${destFileName}")

    def libjars = ant.fileset(dir: jardir, includes: "*.jar", excludes: config.griffon.jars.jarName)
    ant.jar(destfile: destFileName) {
        manifest {
            attribute(name: "Main-Class", value: griffonApplicationClass)
        }
        fileset(dir: classesDirPath) {
            exclude(name: "Config*.class")
        }
        fileset(dir: i18nDir)
        fileset(dir: resourcesDir)

        libjars.each {
            zipfileset(src: it.toString(),
                      excludes: "META-INF/*.MF,META-INF/*.SF,META-INF/*.RSA,META-INF/*.DSA")
        }
    }

    event("PackageEnd",["jar"])
}

target(package_applet: "") {
    makeJNLP = true
    event("PackageStart",["applet"])

    createConfig()
    // make codebase relative
    config.griffon.webstart.codebase = ""
    packageApp()
    targetDistDir = "${distDir}/applet"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    ant.copy(todir: targetDistDir) {
        fileset(dir: "${basedir}/staging", excludes: config.griffon.webstart.jnlp )
    }
    def zipFileName = "${targetDistDir}/${griffonAppName}-${griffonAppVersion}-applet.zip"
    ant.delete(dir: zipFileName, quiet: true, failOnError: false)
    ant.zip(basedir: "${targetDistDir}", destfile: zipFileName)

    event("PackageEnd",["applet"])
}

target(package_webstart: "") {
    makeJNLP = true
    event("PackageStart",["webstart"])

    createConfig()
    // make codebase relative
    config.griffon.webstart.codebase = ""
    packageApp()
    targetDistDir = "${distDir}/webstart"
    ant.delete(dir: targetDistDir, quiet: true, failOnError: false)
    ant.mkdir(dir: targetDistDir)
    ant.copy(todir: targetDistDir) {
        fileset(dir: "${basedir}/staging", excludes: "${config.griffon.applet.jnlp}, ${config.griffon.applet.html}" )
    }
    def zipFileName = "${targetDistDir}/${griffonAppName}-${griffonAppVersion}-webstart.zip"
    ant.delete(dir: zipFileName, quiet: true, failOnError: false)
    ant.zip(basedir: "${targetDistDir}", destfile: zipFileName)


    event("PackageEnd",["webstart"])
}

target(_copyAppLibs: "") {
    ant.mkdir(dir: "${targetDistDir}/lib")
    ant.copy( todir: "${targetDistDir}/lib" ) {
        fileset( dir: "${basedir}/staging", includes: "**/*.jar" )
    }
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
    if (isMacOSX) {
        javaOpts << "-Xdock:name=$griffonAppName"
// TODO setup griffon.icns relative to app dir
//         javaOpts << "-Xdock:icon=${griffonHome}/bin/griffon.icns"
    }
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

setDefaultTarget(_package)