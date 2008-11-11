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
* Gant script that handles general initialization of a Griffon applications
*
* @author Graeme Rocher
*
* @since 0.4
*/

import org.codehaus.griffon.commons.GriffonClassUtils as GCU
import org.codehaus.griffon.commons.GriffonUtil

// add includes
includeTargets << griffonScript("_Settings")
includeTargets << griffonScript("_GriffonArgParsing")
includeTargets << griffonScript("_PluginDependencies")
includeTargets << griffonScript("_PackagePlugins")

shouldPackageTemplates = false
config = new ConfigObject()
configFile = new File("${basedir}/griffon-app/conf/Config.groovy")

configSlurper = new ConfigSlurper(griffonEnv)
configSlurper.setBinding(griffonHome:griffonHome,
                         appName:griffonAppName,
                         appVersion:griffonAppVersion,
                         userHome:userHome,
                         basedir:basedir)

exit = {
    event("Exiting", [it])
    // Prevent system.exit during unit/integration testing
    if (System.getProperty("griffon.cli.testing")) {
        throw new RuntimeException("Gant script exited")
    } else {
        System.exit(it)
    }
}


confirmInput = {String message ->
    Ant.input(message: message, addproperty: "confirm.message", validargs: "y,n")
    Ant.antProject.properties."confirm.message"
}

target(createStructure: "Creates the application directory structure") {
    Ant.sequential {
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
    if (new File("${basedir}/application.properties").exists()) {
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
    Ant.propertyfile(file: "${basedir}/application.properties",
            comment: "Do not edit app.griffon.* properties, they may change automatically. " +
                    "DO NOT put application configuration in here, it is not the right place!") {
        entry(key: "app.name", value: "$griffonAppName")
        entry(key: "app.griffon.version", value: "$griffonVersion")
    }
    // Make sure if this is a new project that we update the var to include version
    appGriffonVersion = griffonVersion
}

standardGriffonFilters = {
    replacefilter(token: "@griffon.app.class.name@", value:appClassName )
    replacefilter(token: "@griffon.version@", value: griffonVersion)
    replacefilter(token: "@griffon.project.name@", value: griffonAppName)
    replacefilter(token: "@griffon.project.key@", value: griffonAppName.replaceAll( /\s/, '.' ).toLowerCase())
}

target(init: "main init target") {
    depends(createStructure)
}

defaultTarget("Initializes a Griffon application. Warning: This target will overwrite artifacts,use the 'upgrade' target for upgrades.") {
    depends(init)
}

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

target(createArtifact: "Creates a specific Griffon artifact") {
    depends(promptForName)

    Ant.mkdir(dir: "${basedir}/${artifactPath}")

    // Extract the package name if one is given.
    def (pkg, name) = extractArtifactName(args)


    // Convert the package into a file path.
    def pkgPath = ''
    if (pkg) {
        pkgPath = pkg.replace('.' as char, '/' as char)

        // Make sure that the package path exists! Otherwise we won't
        // be able to create a file there.
        Ant.mkdir(dir: "${basedir}/${artifactPath}/${pkgPath}")

        // Future use of 'pkgPath' requires a trailing slash.
        pkgPath += '/'
    }

    // Convert the given name into class name and property name
    // representations.
    className = GCU.getClassNameRepresentation(name)
    propertyName = GCU.getPropertyNameRepresentation(name)
    artifactFile = "${basedir}/${artifactPath}/${pkgPath}${className}${typeName}.groovy"


    if (new File(artifactFile).exists()) {
        Ant.input(addProperty: "${name}.${typeName}.overwrite", message: "${artifactName} ${className}${typeName}.groovy already exists. Overwrite? [y/n]")
        if (Ant.antProject.properties."${name}.${typeName}.overwrite" == "n")
            return
    }

    // first check for presence of template in application
    templateFile = "${basedir}/src/templates/artifacts/${artifactName}.groovy"
    if (!new File(templateFile).exists()) {
        // now check for template provided by plugins
        def pluginTemplateFiles = resolveResources("file:${pluginsDirPath}/*/src/templates/artifacts/${artifactName}.groovy")
        if (pluginTemplateFiles) {
            templateFile = pluginTemplateFiles[0].path
        } else {
            // template not found in application, use default template
            templateFile = "${griffonHome}/src/griffon/templates/artifacts/${artifactName}.groovy"
        }
    }

    Ant.copy(file: templateFile, tofile: artifactFile, overwrite: true)
    Ant.replace(file: artifactFile, standardGriffonFilters)
    def artName = "${className}${typeName}"
    def artPkg
    if (pkg) {
        artPkg = "package ${pkg}\n\n"
    } else {
        artPkg = ""
    }
    Ant.replace(file: artifactFile) {
        replacefilter(token: "@artifact.name@", value: artName)
        replacefilter(token: "@artifact.key@", value: artName.toLowerCase())
        replacefilter(token: "@artifact.package@", value: artPkg)
    }

    // When creating a domain class, "typename" is empty. So, in order
    // to make the status message sensible, we have to pass something
    // else in.
    event("CreatedFile", [artifactFile])
    event("CreatedArtefact", [ artifactName ?: "Domain Class", className])
}


target(promptForName: "Prompts the user for the name of the Artifact if it isn't specified as an argument") {
    if (!args) {
        Ant.input(addProperty: "artifact.name", message: "${typeName} name not specified. Please enter:")
        args = Ant.antProject.properties."artifact.name"
    }
}

logError = { String message, Throwable t ->
    GriffonUtil.deepSanitize(t)
    t.printStackTrace()
    event("StatusError", ["$message: ${t.message}"])
}