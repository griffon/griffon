
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

/**
* Gant script that handles general initialization of a Griffon applications
*
* @author Graeme Rocher
*
* @since 0.4
*/

import org.codehaus.griffon.util.GriffonUtil
import org.springframework.core.io.FileSystemResource
import org.codehaus.griffon.util.GriffonNameUtils

// add includes
includeTargets << griffonScript("_GriffonArgParsing")
includeTargets << griffonScript("_PluginDependencies")


extractArtifactName = {args ->
    def name = args
    def pkg = null
    def pos = args.lastIndexOf('.')
    if (pos != -1) {
        pkg = name[0..<pos]
        name = name[(pos + 1)..-1]
    }
    return [pkg, name]
}

exit = {
    event("Exiting", [it])
    // Prevent system.exit during unit/integration testing
    if (System.getProperty("griffon.cli.testing")) {
        throw new RuntimeException("Gant script exited")
    } else {
        System.exit(it)
    }
}


// Generates Eclipse .classpath entries for all the Griffon dependencies,
// i.e. a string containing a "<classpath entry ..>" element for each
// of Griffon' library JARs. This only works if $Griffon_HOME is set.
eclipseClasspathLibs = {
    def result = ''
    if (griffonHome) {
        (new File("${griffonHome}/lib")).eachFileMatch(~/.*\.jar/) {file ->
            if (!file.name.startsWith("gant-")) {
                result += "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/lib/${file.name}\" />\n\n"
            }
        }
    }
    result
}

// Generates Eclipse .classpath entries for the Griffon distribution
// JARs. This only works if $Griffon_HOME is set.
eclipseClasspathGriffonJars = {args ->
    result = ''
    if (griffonHome) {
        (new File("${griffonHome}/dist")).eachFileMatch(~/^griffon-.*\.jar/) {file ->
            result += "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/dist/${file.name}\" />\n\n"
        }
    }
    result
}

confirmInput = {String message ->
    ant.input(message: message, addproperty: "confirm.message", validargs: "y,n")
    ant.antProject.properties."confirm.message"
}

target(createStructure: "Creates the application directory structure") {
    ant.sequential {
        mkdir(dir: "${basedir}/griffon-app")
        mkdir(dir: "${basedir}/griffon-app/conf")
        mkdir(dir: "${basedir}/griffon-app/conf/keys")
        mkdir(dir: "${basedir}/griffon-app/conf/webstart")
        mkdir(dir: "${basedir}/griffon-app/controllers")
        mkdir(dir: "${basedir}/griffon-app/i18n")
        mkdir(dir: "${basedir}/griffon-app/lifecycle")
        mkdir(dir: "${basedir}/griffon-app/models")
        mkdir(dir: "${basedir}/griffon-app/resources")
        mkdir(dir: "${basedir}/griffon-app/views")
        mkdir(dir: "${basedir}/lib")
        mkdir(dir: "${basedir}/scripts")
        mkdir(dir: "${basedir}/src")
        mkdir(dir: "${basedir}/src/main")
        mkdir(dir: "${basedir}/test")
        mkdir(dir: "${basedir}/test/integration")
        mkdir(dir: "${basedir}/test/unit")
    }
}

target(checkVersion: "Stops build if app expects different Griffon version") {
    if (metadataFile.exists()) {
        if (appGriffonVersion != griffonVersion) {
            event("StatusFinal", ["Application expects griffon version [$appGriffonVersion], but GRIFFON_HOME is version " +
                    "[$griffonVersion] - use the correct Griffon version or run 'griffon upgrade' if this Griffon " +
                    "version is newer than the version your application expects."])
            exit(1)
        }
    } else {
        // Griffon has always had version numbers, this is an error state
        event("StatusFinal", ["Application is an unknown Griffon version, please run: griffon upgrade"])
        exit(1)
    }
}


target(updateAppProperties: "Updates default application.properties") {
    ant.propertyfile(file: metadataFile,
            comment: "Do not edit app.griffon.* properties, they may change automatically. " +
                    "DO NOT put application configuration in here, it is not the right place!") {
        entry(key: "app.name", value: "$griffonAppName")
        entry(key: "app.griffon.version", value: "$griffonVersion")
        if (griffonAppVersion) {
            entry(key: "app.version", value: "$griffonAppVersion")
        }
    }
    // Make sure if this is a new project that we update the var to include version
    appGriffonVersion = griffonVersion
}

target( launderIDESupportFiles: "Updates the IDE support files (Eclipse, TextMate etc.), changing file names and replacing tokens in files where appropriate.") {
    event("updateIDESupportFilesStart", [])
    ant.move(file: "${basedir}/.launch", tofile: "${basedir}/${griffonAppName}.launch", overwrite: true)
    ant.move(file: "${basedir}/project.tmproj", tofile: "${basedir}/${griffonAppName}.tmproj", overwrite: true)

    def appKey = griffonAppName.replaceAll( /\s/, '.' ).toLowerCase()
    ant.replace(dir:"${basedir}", includes:"*.*") {
        replacefilter(token: "@griffon.eclipse.libs@", value: eclipseClasspathLibs())
        replacefilter(token: "@griffon.eclipse.jar@", value: eclipseClasspathGriffonJars())
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.project.key@", value: appKey)
    }
    event("updateIDESupportFilesEnd", [])
}

target(init: "main init target") {
    depends(createStructure, updateAppProperties)

    griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
    griffonUnpack(dest: basedir, src: "griffon-app-files.jar")
    launderIDESupportFiles()

    classpath()

    // Create a message bundle to get the user started.
    touch(file: "${basedir}/griffon-app/i18n/messages.properties")

	// Set the default version number for the application
    ant.propertyfile(file:"${basedir}/application.properties") {
        entry(key:"app.version", value: griffonAppVersion ?: "0.1")
//        entry(key:"app.servlet.version", value:servletVersion)
    }
}

logError = { String message, Throwable t ->
    GriffonUtil.deepSanitize(t)
    t.printStackTrace()
    event("StatusError", ["$message: ${t.message}"])
}

logErrorAndExit = { String message, Throwable t ->
    logError(message, t)
    exit(1)
}
