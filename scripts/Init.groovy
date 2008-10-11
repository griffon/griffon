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

import org.codehaus.groovy.griffon.commons.GriffonClassUtils as GCU
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.control.*
import org.springframework.core.io.FileSystemResource
import org.codehaus.groovy.griffon.commons.GriffonContext

Ant.property(environment: "env")

servletVersion = System.getProperty("servlet.version") ? System.getProperty("servlet.version") : "2.4"

griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"
Ant.property(file: "${griffonHome}/build.properties")

griffonVersion = Ant.antProject.properties.'griffon.version'
griffonEnv = System.getProperty("griffon.env")
defaultEnv = System.getProperty("griffon.default.env") == "true" as boolean
serverPort = System.getProperty('server.port') ? System.getProperty('server.port').toInteger() : 8080
serverPortHttps = System.getProperty('server.port.https') ? System.getProperty('server.port.https').toInteger() : 8443
serverHost = System.getProperty('server.host') ? System.getProperty('server.host') : null
enableJndi = System.getProperty('enable.jndi') == "true" as boolean
basedir = System.getProperty("base.dir")
baseFile = new File(basedir).canonicalFile
isPluginProject = baseFile.listFiles().find { it.name.endsWith("GriffonPlugin.groovy") }
baseName = baseFile.name
userHome = Ant.antProject.properties."user.home"
griffonApp = null
eventsClassLoader = new GroovyClassLoader(getClass().classLoader)


// common directories and paths
griffonWorkDir = System.getProperty(GriffonContext.WORK_DIR) ?: "${userHome}/.griffon/${griffonVersion}"
griffonTmp = "${griffonWorkDir}/tmp"
projectWorkDir = "${griffonWorkDir}/projects/${baseName}"
classesDirPath = System.getProperty(GriffonContext.PROJECT_CLASSES_DIR) ?: "$projectWorkDir/classes"
resourcesDirPath = System.getProperty(GriffonContext.PROJECT_RESOURCES_DIR) ?: "$projectWorkDir/resources"
testDirPath = System.getProperty(GriffonContext.PROJECT_TEST_CLASSES_DIR) ?: "$projectWorkDir/test-classes"

// reset system properties just in case they didn't exist
System.setProperty(GriffonContext.WORK_DIR, griffonWorkDir)
System.setProperty(GriffonContext.PROJECT_CLASSES_DIR, classesDirPath)
System.setProperty(GriffonContext.PROJECT_TEST_CLASSES_DIR, testDirPath)
System.setProperty(GriffonContext.PROJECT_RESOURCES_DIR, resourcesDirPath)


classesDir = new File(classesDirPath)
System.setProperty("griffon.classes.dir", classesDirPath)


resolver = new PathMatchingResourcePatternResolver()
griffonAppName = null
griffonAppVersion = null
appGriffonVersion = null
shouldPackageTemplates = false
hooksLoaded = false
classpathSet = false
enableProfile = System.getProperty("griffon.script.profile") as boolean
config = new ConfigObject()

// A map of events to lists of handlers. The handlers provided by plugin
// and application Events scripts are put in here.
globalEventHooks = [
    StatusFinal: [ {message -> println message } ],
    StatusUpdate: [ {message -> println message + ' ...' } ],
    StatusError: [ {message -> System.err.println message } ],
    CreatedArtefact: [ {artefactType, artefactName -> println "Created $artefactType for $artefactName" } ]
]

// Get App's metadata if there is any
if (new File("${basedir}/application.properties").exists()) {
    // We know we have an app
    Ant.property(file: "${basedir}/application.properties")

    def props = Ant.antProject.properties
    griffonAppName = props.'app.name'
    griffonAppVersion = props.'app.version'
    appGriffonVersion = props.'app.griffon.version'
    servletVersion = props.'app.servlet.version' ? props.'app.servlet.version' : servletVersion
}

// If no app name property (upgraded/new/edited project) default to basedir
if (!griffonAppName) {
    griffonAppName = baseName
}
if(griffonAppName.indexOf('/') >-1)
    appClassName = griffonAppName[griffonAppName.lastIndexOf('/')..-1]
else
    appClassName = GCU.getClassNameRepresentation(griffonAppName)


configSlurper = new ConfigSlurper(griffonEnv)
configSlurper.setBinding(griffonHome:griffonHome, appName:griffonAppName, appVersion:griffonAppVersion, userHome:userHome, basedir:basedir, servletVersion:servletVersion)


profile = {String name, Closure callable ->
    if (enableProfile) {
        def now = System.currentTimeMillis()
        println "Profiling [$name] start"
        callable()
        def then = System.currentTimeMillis() - now
        println "Profiling [$name] finish. Took $then ms"
    }
    else {
        callable()
    }
}

// Send a scripting event notification to any and all event hooks in plugins/user scripts
event = {String name, def args ->
    if (!hooksLoaded) {
        hooksLoaded = true
        setClasspath()
        loadEventHooks()
        // Give scripts a chance to modify classpath
        event('SetClasspath', [getClass().classLoader.rootLoader])
    }

    globalEventHooks[name].each() { handler ->
        try {
            handler.delegate = this
            handler(* args)
        } catch (MissingPropertyException e) {
        }
    }
}

/* Standard handlers not handled by Init
eventSetClasspath = { rootLoader ->
    // Called when root classloader is being configured
}
eventCreatedFile = { fileName ->
    // Called when any file is created in the project tree, that is to be part of the project source (not regenerated)
}
eventPluginInstalled = { pluginName ->
    // Called when a plugin is installed
}
eventExiting = { returnCode ->
    // Called when the Gant scripting is about to end
}
*/

loadEventHooks = {
    // Look for user script
    def f = new File(userHome, ".griffon/scripts/Events.groovy")
    if (f.exists()) {
        println "Found user events script"
        loadEventScript(f)
    }

    // Look for app-supplied scripts
    f = new File(basedir, "scripts/Events.groovy")
    if (f.exists()) {
        println "Found application events script"
        loadEventScript(f)
    }

    // Look for plugin-supplied scripts
    def pluginsDir = new File(basedir, "plugins")
    if (pluginsDir.exists()) {
        pluginsDir.eachDir() {
            f = new File(it, "scripts/Events.groovy")
            if (f.exists()) {
                println "Found events script in plugin ${it.name}"
                loadEventScript(f)
            }
        }
    }

//     pluginsDir = new File("${griffonHome}/plugins")
//     if (pluginsDir.exists()) {
//         pluginsDir.eachDir { pluginInstallDir ->
//             def latestVersionFile = new File(pluginInstallDir,"latest")
//             if (!latestVersionFile.exists()) return
//             def latestPluginVersion = latestVersionFile.text.trim()
//             f = new File("${pluginInstallDir}/${latestPluginVersion}/scripts/Events.groovy")
//             if (f.exists()) {
//                 println "Found events script in plugin ${pluginInstallDir.name}"
//                 loadEventScript(f)
//             }
//         }
//     }
}

void loadEventScript(theFile) {
    try {
        // Load up the given events script.
        def script = eventsClassLoader.parseClass(theFile).newInstance()

        // Pass the global binding to the script.
        script.binding = getBinding()

        // Execute the script.
        script.run()

        // The binding should now contain the event hooks provided by
        // script, so we remove them and add them to the 'eventHooks'
        // map.
        def entriesToRemove = []
        script.binding.variables.each {key, value ->
            // Check whether this binding variable is an event hook.
            def m = key =~ /event([A-Z]\w*)/
            if (m.matches()) {
                // It is, so add the hook to the global map of event
                // hooks.
                def eventName = m[0][1]
                def hooks = globalEventHooks[eventName]
                if (hooks == null) {
                    hooks = []
                    globalEventHooks[eventName] = hooks
                }

                hooks << value

                // This entry should now be removed from the global
                // binding.
                entriesToRemove << key
            }
        }

        // Remove the event hooks from the global binding.
        entriesToRemove.each { script.binding.variables.remove(it) }
    } catch (Throwable t) {
        println "Unable to load event script $theFile: ${t.message}"
        t.printStackTrace()
    }
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




// a resolver that doesn't throw exceptions when resolving resources
resolveResources = {String pattern ->
    try {
        return resolver.getResources(pattern)
    }
    catch (Throwable e) {
        return []
    }
}

getGriffonLibs = {
    def result = ''
    (new File("${griffonHome}/lib")).eachFileMatch(~/.*\.jar/) {file ->
        if (!file.name.startsWith("gant-")) {
            result += "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/lib/${file.name}\" />\n\n"
        }
    }
    result
}
getGriffonJar = {args ->
    result = ''
    (new File("${griffonHome}/dist")).eachFileMatch(~/^griffon-.*\.jar/) {file ->
        result += "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/dist/${file.name}\" />\n\n"
    }
    result
}

args = System.getProperty("griffon.cli.args")

argsMap = [params: []]

target(parseArguments: "Parse the arguments passed on the command line") {
    args?.tokenize()?.each {token ->
        def nameValueSwitch = token =~ "--?(.*)=(.*)"
        if (nameValueSwitch.matches()) { // this token is a name/value pair (ex: --foo=bar or -z=qux)
            argsMap[nameValueSwitch[0][1]] = nameValueSwitch[0][2]
        }
        else {
            def nameOnlySwitch = token =~ "--?(.*)"
            if (nameOnlySwitch.matches()) {  // this token is just a switch (ex: -force or --help)
                argsMap[nameOnlySwitch[0][1]] = true
            }
            else { // single item tokens, append in order to an array of params
                argsMap["params"] << token
            }
        }
    }
    event("StatusUpdate", ["Done parsing arguments: $argsMap"])
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
//        mkdir(dir: "${basedir}/griffon-app/services")
//        mkdir(dir: "${basedir}/griffon-app/domain")
//        mkdir(dir: "${basedir}/griffon-app/taglib")
//        mkdir(dir: "${basedir}/griffon-app/utils")
//        mkdir(dir: "${basedir}/griffon-app/views/layouts")
//        mkdir(dir: "${basedir}/test")
//        mkdir(dir: "${basedir}/test/unit")
//        mkdir(dir: "${basedir}/test/integration")
//        mkdir(dir: "${basedir}/web-app")
//        mkdir(dir: "${basedir}/web-app/js")
//        mkdir(dir: "${basedir}/web-app/css")
//        mkdir(dir: "${basedir}/web-app/images")
//        mkdir(dir: "${basedir}/web-app/META-INF")
//        mkdir(dir: "${basedir}/griffon-app/conf/spring")
//        mkdir(dir: "${basedir}/griffon-app/conf/hibernate")
    }
}

target(checkVersion: "Stops build if app expects different Griffon version") {
//    if (new File("${basedir}/application.properties").exists()) {
//        if (appGriffonVersion != griffonVersion) {
//            event("StatusFinal", ["Application expects griffon version [$appGriffonVersion], but GRIFFON_HOME is version " +
//                    "[$griffonVersion] - use the correct Griffon version or run 'griffon upgrade' if this Griffon " +
//                    "version is newer than the version your application expects."])
//            exit(1)
//        }
//    } else {
//        // We know this is pre-0.5 application
//        event("StatusFinal", ["Application is pre-Griffon 0.5, please run: griffon upgrade"])
//        exit(1)
//    }
}


target(setupEnvironment: "Sets up the Griffon environment for this script") {

    if(!System.getProperty("griffon.env.set")) {

        def defaultEnv = System.getProperty(GriffonContext.ENVIRONMENT_DEFAULT) as boolean
        if(defaultEnv) {
            def customEnv
            try {
                customEnv = getProperty("scriptEnv")
            } catch (MissingPropertyException mpe) {
                //ignore, ok
            }
            if(customEnv) {
                System.setProperty(GriffonContext.ENVIRONMENT, customEnv)
                System.setProperty(GriffonContext.ENVIRONMENT_DEFAULT, "")
            }
        }
        println "Environment set to ${System.getProperty(GriffonContext.ENVIRONMENT)}"
        System.setProperty("griffon.env.set", "true")
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

target(copyBasics: "Copies the basic resources required for a Griffon app to function") {
    def libs = getGriffonLibs()
    def jars = getGriffonJar()

    Ant.sequential {
        copy(todir: "${basedir}") {
            fileset(dir: "${griffonHome}/src/griffon/templates/ide-support/eclipse",
                    includes: "*.*",
                    excludes: ".launch")
        }
        replace(dir: "${basedir}", includes: "*.*",
                token: "@griffon.libs@", value: "${libs}")
        replace(dir: "${basedir}", includes: "*.*",
                token: "@griffon.jar@", value: "${jars}")
        replace(dir: "${basedir}", includes: "*.*",
                token: "@griffon.project.name@", value: "${griffonAppName}")

//        copy(todir: "${basedir}/web-app/WEB-INF") {
//            fileset(dir: "${griffonHome}/src/war/WEB-INF") {
//                include(name: "applicationContext.xml")
//                exclude(name: "log4j.properties")
//                include(name: "sitemesh.xml")
//            }
//        }

//        copy(todir: "${basedir}/web-app/WEB-INF/tld") {
//            fileset(dir: "${griffonHome}/src/war/WEB-INF/tld/${servletVersion}")
//            fileset(dir: "${griffonHome}/src/war/WEB-INF/tld", includes: "spring.tld")
//            fileset(dir: "${griffonHome}/src/war/WEB-INF/tld", includes: "griffon.tld")
//        }
    }
}
target(init: "main init target") {
    depends(createStructure, copyBasics)

    Ant.sequential {
//        copy(todir: "${basedir}/web-app") {
//            fileset(dir: "${griffonHome}/src/war") {
//                include(name: "**/**")
//                exclude(name: "WEB-INF/**")
//            }
//        }

        copy(todir: "${basedir}/griffon-app") {
            fileset(dir: "${griffonHome}/src/griffon/griffon-app",  includes: "**/**", excludes: "**/taglib/**, **/utils/**")
        }

        touch(file: "${basedir}/griffon-app/i18n/messages.properties")
    }
}

target("default": "Initializes a Griffon application. Warning: This target will overwrite artifacts,use the 'upgrade' target for upgrades.") {
    depends(init)
}

target(createArtifact: "Creates a specific Griffon artifact") {
    depends(promptForName)

    Ant.mkdir(dir: "${basedir}/${artifactPath}")

    // Extract the package name if one is given.
    def name = args
    def pkg = null
    def pos = args.lastIndexOf('.')
    if (pos != -1) {
        pkg = name[0..<pos]
        name = name[(pos + 1)..-1]
    }

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
        // now check for template provided by local plugins
        def pluginTemplateFiles = resolveResources("plugins/*/src/templates/artifacts/${artifactName}.groovy")
        if (pluginTemplateFiles) {
            templateFile = pluginTemplateFiles[0].path
        } else {
            // now check for template provided by framework plugins
//             def found = false
//             def basePluginsDir = new File("${griffonHome}/plugins")
//             if (basePluginsDir.exists()) {
//                 for( pluginInstallDir in basePluginsDir.list()) {
//                     pluginInstallDir = new File(basePluginsDir,pluginInstallDir)
//                     def latestVersionFile = new File(pluginInstallDir,"latest")
//                     if (!latestVersionFile.exists()) continue
//                     def latestPluginVersion = latestVersionFile.text.trim()
//                     def artifactTemplate = new File("${pluginInstallDir}/${latestPluginVersion}/src/templates/artifacts/${artifactName}.groovy")
//                     if(artifactTemplate.exists()) {
//                         templateFile = artifactTemplate
//                         found = true
//                         break
//                     }
//                 }
//             }
//             if (!found) {
                // template not found, use default template
                templateFile = "${griffonHome}/src/griffon/templates/artifacts/${artifactName}.groovy"
//             }
        }
    }
    Ant.copy(file: templateFile, tofile: artifactFile, overwrite: true)

    Ant.replace(file: artifactFile) {
        replaceFilter(token: "@artifact.name@", value: "${className}${typeName}")
        replaceFilter(token: "@artifact.package@", value: (pkg?"package ${pkg}\n\n":""))
        replacefilter(token: "@griffon.project.name@", value:"${griffonAppName}" )
    }

    event("CreatedFile", [artifactFile])
    event("CreatedArtefact", [typeName, className])
}

target(promptForName: "Prompts the user for the name of the Artifact if it isn't specified as an argument") {
    if (!args) {
        Ant.input(addProperty: "artifact.name", message: "${typeName} name not specified. Please enter:")
        args = Ant.antProject.properties."artifact.name"
    }
}

target(classpath: "Sets the Griffon classpath") {
    setClasspath()
}

griffonClasspath = {pluginLibs, griffonDir ->
    pathelement(location: "${classesDir.absolutePath}")
    pathelement(location: "${basedir}")
    pathelement(location: "${basedir}/test/unit")
    pathelement(location: "${basedir}/test/integration")
    pathelement(location: "${basedir}/web-app")
//    pathelement(location: "${basedir}/web-app/WEB-INF")
//    pathelement(location: "${basedir}/web-app/WEB-INF/classes")
    for (pluginLib in pluginLibs) {
        fileset(dir: pluginLib.file.absolutePath)
    }
//    if (new File("${basedir}/web-app/WEB-INF/lib").exists()) {
//        fileset(dir: "${basedir}/web-app/WEB-INF/lib")
//    }
    fileset(dir: "${griffonHome}/lib")
    fileset(dir: "${griffonHome}/dist")
    if (new File("${basedir}/lib").exists()) {
        fileset(dir: "${basedir}/lib")
    }
    for (d in griffonDir) {
        pathelement(location: "${d.file.absolutePath}")
    }

    if(config.griffon.compiler.dependencies) {
        def callable = config.griffon.compiler.dependencies
        callable.delegate = delegate
        callable.resolveStrategy = Closure.DELEGATE_FIRST
        callable()
    }
}
void setClasspath() {
    if (classpathSet) return

    def preInitFile = new File("./griffon-app/conf/PreInit.groovy")
    if(preInitFile.exists()) {
        config = configSlurper.parse(preInitFile.toURL())
        config.setConfigFile(preInitFile.toURL())
    }


    def griffonDir = resolveResources("file:${basedir}/griffon-app/*")
    def pluginLibs = resolveResources("file:${basedir}/plugins/*/lib")

    Ant.path(id: "griffon.classpath", griffonClasspath.curry(pluginLibs, griffonDir))
    StringBuffer cpath = new StringBuffer("")

    def jarFiles = getJarFiles()


    for (dir in griffonDir) {
        cpath << dir.file.absolutePath << File.pathSeparator
        // Adding the griffon-app folders to the root loader causes re-load issues as
        // root loader returns old class before the griffon GCL attempts to recompile it
        //rootLoader?.addURL(dir.URL)
    }
    cpath << classesDirPath << File.pathSeparator
//    cpath << "${basedir}/web-app/WEB-INF"
    for (jar in jarFiles) {
        cpath << jar.file.absolutePath << File.pathSeparator
    }


    compConfig = new CompilerConfiguration()
    compConfig.setClasspath(cpath.toString());
    compConfig.sourceEncoding = "UTF-8"
    rootLoader = getClass().classLoader.rootLoader
    populateRootLoader(rootLoader, jarFiles)

    //rootLoader?.addURL(new File("${basedir}/griffon-app/conf/hibernate").toURL())
    rootLoader?.addURL(new File("${basedir}/src/java").toURL())
    rootLoader?.addURL(new File("${basedir}/src/java").toURL())

    // The resources directory must be created before it is added to
    // the root loader, otherwise it is quietly ignored. In other words,
    // if the directory is created after its path has been added to the
    // root loader, it will not be included in the classpath.
    def resourcesDir = new File(resourcesDirPath)
    if (!resourcesDir.exists()) {
        resourcesDir.mkdirs()
    }
    rootLoader?.addURL(resourcesDir.toURL())

    parentLoader = getClass().getClassLoader()
    classpathSet = true

    event('SetClasspath', [rootLoader])
}

getJarFiles = {->
    def jarFiles = resolveResources("file:${basedir}/lib/*.jar").toList()
    def pluginJars = resolveResources("file:${basedir}/plugins/*/lib/*.jar")
    for (pluginJar in pluginJars) {
        boolean matches = jarFiles.any {it.file.name == pluginJar.file.name}
        if (!matches) jarFiles.add(pluginJar)
    }

    def userJars = resolveResources("file:${userHome}/.griffon/lib/*.jar")
    for (userJar in userJars) {
        jarFiles.add(userJar)
    }

    jarFiles.addAll(getExtraDependencies())

    jarFiles
}

getExtraDependencies = {
    def jarFiles =[]
    if(config?.griffon?.compiler?.dependencies) {
        def extraDeps = Ant.fileScanner(config.griffon.compiler.dependencies)
        for(jar in extraDeps) {
            jarFiles << new FileSystemResource(jar)
        }
    }
    jarFiles
}

populateRootLoader = {rootLoader, jarFiles ->
    for(jar in getExtraDependencies()) {
        rootLoader?.addURL(jar.URL)
    }
//    rootLoader?.addURL(new File("${basedir}/web-app/WEB-INF/classes").toURL())
//    rootLoader?.addURL(new File("${basedir}/web-app/WEB-INF").toURL())
}

target(configureProxy: "The implementation target") {
    proxySettings = ""
    def scriptFile = new File("${userHome}/.griffon/scripts/ProxyConfig.groovy")
    if (scriptFile.exists()) {
        includeTargets << scriptFile.text
        if (proxyConfig.proxyHost) {
            // Let's configure proxy...
            def proxyHost = proxyConfig.proxyHost
            def proxyPort = proxyConfig.proxyPort ? proxyConfig.proxyPort : '80'
            def proxyUser = proxyConfig.proxyUser ? proxyConfig.proxyUser : ''
            def proxyPassword = proxyConfig.proxyPassword ? proxyConfig.proxyPassword : ''
            println "Configured HTTP proxy: ${proxyHost}:${proxyPort}${proxyConfig.proxyUser ? '(' + proxyUser + ')' : ''}"
            // ... for Ant. We can remove this line with Ant 1.7.0 as it uses system properties.
            Ant.setproxy(proxyhost: proxyHost, proxyport: proxyPort, proxyuser: proxyUser, proxypassword: proxyPassword)

            proxySettings += "-Dproxy.host=$proxyHost "
            proxySettings += "-Dproxy.port=$proxyPort "
            proxySettings += "-Dproxy.user=$proxyUser "
            proxySettings += "-Dproxy.password=$proxyPassword "

            // ... for all other code
            System.properties.putAll(["http.proxyHost": proxyHost, "http.proxyPort": proxyPort, "http.proxyUserName": proxyUser, "http.proxyPassword": proxyPassword])
        }
    }
}


setupEnvironment()
