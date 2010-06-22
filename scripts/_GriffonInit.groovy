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
* Gant script that handles general initialization of a Griffon applications
*
* @author Graeme Rocher (Grails 0.4)
*/

import groovy.grape.Grape
import griffon.util.Metadata

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_init_called")) return
_init_called = true

Grape.enableAutoDownload = true

// add includes
includeTargets << griffonScript("_GriffonArgParsing")
includeTargets << griffonScript("_PluginDependencies")


// Generates Eclipse .classpath entries for all the Griffon dependencies,
// i.e. a string containing a "<classpath entry ..>" element for each
// of Griffon' library JARs. This only works if $GRIFFON_HOME is set.
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


target(createStructure: "Creates the application directory structure") {
    ant.sequential {
        mkdir(dir: "${basedir}/griffon-app")
        mkdir(dir: "${basedir}/griffon-app/conf")
        mkdir(dir: "${basedir}/griffon-app/conf/keys")
        mkdir(dir: "${basedir}/griffon-app/conf/webstart")
        mkdir(dir: "${basedir}/griffon-app/conf/dist")
        mkdir(dir: "${basedir}/griffon-app/conf/dist/applet")
        mkdir(dir: "${basedir}/griffon-app/conf/dist/jar")
        mkdir(dir: "${basedir}/griffon-app/conf/dist/shared")
        mkdir(dir: "${basedir}/griffon-app/conf/dist/webstart")
        mkdir(dir: "${basedir}/griffon-app/conf/dist/zip")
        mkdir(dir: "${basedir}/griffon-app/conf/metainf")
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
    def entries = [ "app.name": "$griffonAppName", "app.griffon.version": "$griffonVersion" ]
    if (griffonAppVersion) {
        entries["app.version"] = "$griffonAppVersion"
    }
    updateMetadata(entries)

    // Make sure if this is a new project that we update the var to include version
    appGriffonVersion = griffonVersion
}

target( launderIDESupportFiles: "Updates the IDE support files (Eclipse, TextMate etc.), changing file names and replacing tokens in files where appropriate.") {
    // do nothing. deprecated target

}

target(init: "main init target") {
    depends(createStructure, updateAppProperties)

    griffonUnpack(dest: basedir, src: "griffon-shared-files.jar")
    griffonUnpack(dest: basedir, src: "griffon-app-files.jar")

    classpath()

    // Create a message bundle to get the user started.
    touch(file: "${basedir}/griffon-app/i18n/messages.properties")

	// Set the default version number for the application
    updateMetadata("app.version": griffonAppVersion ?: "0.1")
}
