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

import org.codehaus.griffon.cli.ScriptExitException
import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.FileCopyUtils
import griffon.util.*

/**
 * Gant script containing build variables.
 *
 * @author Peter Ledbrook (Grails 1.1)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_settings_called")) return true
_settings_called = true

/**
 * Resolves the value for a given property name. It first looks for a
 * system property, then in the BuildSettings configuration, and finally
 * uses the given default value if other options are exhausted.
 */
getPropertyValue = { String propName, defaultValue ->
    // First check whether we have a system property with the given name.
    def value = System.getProperty(propName)
    if (value != null) return value

    // Now try the BuildSettings settings.
    value = buildProps[propName]

    // Return the BuildSettings value if there is one, otherwise use the
    // default.
    return value != null ? value : defaultValue
}

// Set up various build settings. System properties take precedence
// over those defined in BuildSettings, which in turn take precedence
// over the defaults.
isInteractive = true
buildProps = buildConfig.toProperties()
enableProfile = getPropertyValue("griffon.script.profile", false).toBoolean()
pluginsHome = griffonSettings.projectPluginsDir.path

// Used to find out about plugins used by this app. The plugin manager
// is configured later when its created (see _PluginDependencies).
// pluginSettings = new PluginBuildSettings(griffonSettings)

// While some code still relies on GriffonPluginUtils, make sure it
// uses the same PluginBuildSettings instance as the scripts.
GriffonPluginUtils.pluginBuildSettings = pluginSettings

// Load the application metadata (application.properties)
metadataFile = new File("${basedir}/application.properties")
metadata = metadataFile.exists() ? Metadata.getInstance(metadataFile) : Metadata.current

griffonAppName = metadata.getApplicationName()
griffonAppVersion = metadata.getApplicationVersion()
appGriffonVersion = metadata.getGriffonVersion()

// If no app name property (upgraded/new/edited project) default to basedir.
if (!griffonAppName) {
    griffonAppName = griffonSettings.baseDir.name
}

if(griffonAppName.indexOf('/') >-1)
    appClassName = griffonAppName[griffonAppName.lastIndexOf('/')..-1]
else
    appClassName = GriffonUtil.getClassNameRepresentation(griffonAppName)

// Other useful properties.
args = System.getProperty("griffon.cli.args")
classesDir = griffonSettings.classesDir
griffonApp = null
griffonContext = null
isApplicationProject = metadataFile.exists()
isPluginProject = griffonSettings.isPluginProject()
isAddonPlugin = griffonSettings.isAddonPlugin()

shouldPackageTemplates = false

// Pattern that matches artefacts in the 'griffon-app' directory.
// Note that the capturing group matches any package directory
// structure.
artefactPattern = /\S+?\/griffon-app\/\S+?\/(\S+?)\.groovy/

defaultGriffonApplicationClass = isAddonPlugin ? 'griffon.test.mock.MockGriffonApplication' : 'griffon.swing.SwingApplication'
defaultGriffonAppletClass = 'griffon.swing.SwingApplet'
makeJNLP = false
_skipSigning = false // GRIFFON-118
defaultAppletWidth = 320 // GRIFFON-127
defaultAppletHeight = 240 // GRIFFON-127

// Set up the Griffon environment for this script.
if (!System.getProperty("griffon.env.set")) {
    if (griffonSettings.defaultEnv && getBinding().variables.containsKey("scriptEnv")) {
        griffonEnv = scriptEnv
        griffonSettings.griffonEnv = griffonEnv
        System.setProperty(Environment.KEY, griffonEnv)
        System.setProperty(Environment.DEFAULT, "")
    }
    println "Environment set to ${griffonEnv}"
    System.setProperty("griffon.env.set", "true")
}
if(getBinding().variables.containsKey("scriptScope")) {
    buildScope = (scriptScope instanceof BuildScope) ? scriptScope : BuildScope.valueOf(scriptScope.toString().toUpperCase());
    buildScope.enable()
}
else {
    buildScope = BuildScope.ALL
    buildScope.enable()
}

// Prepare a configuration file parser based on the current environment.
configSlurper = new ConfigSlurper(griffonEnv)
configSlurper.setBinding(griffonHome:griffonHome,
                         appName:griffonAppName,
                         appVersion:griffonAppVersion,
                         userHome:userHome,
                         basedir:basedir)

applicationConfigFile = new File(basedir, 'griffon-app/conf/Application.groovy')
builderConfigFile = new File(basedir, 'griffon-app/conf/Builder.groovy')
configFile = new File(basedir, 'griffon-app/conf/Config.groovy')

// Ant path based on the class loader for the scripts. This basically
// includes all the Griffon JARs, the plugin libraries, and any JARs
// provided by the application. Useful for task definitions.
ant.path(id: "core.classpath") {
    classLoader.URLs.each { URL url ->
        pathelement(location: url.file)
    }
}

// a resolver that doesn't throw exceptions when resolving resources
resolver = new PathMatchingResourcePatternResolver()

resolveResources = {String pattern ->
    try {
        return resolver.getResources(pattern)
    }
    catch (Throwable e) {
        return [] as Resource[]
    }
}

// Closure that returns a Spring Resource - either from $GRIFFON_HOME
// if that is set, or from the classpath.
griffonResource = {String path ->
    if (griffonSettings.griffonHome) {
        return new FileSystemResource("${griffonSettings.griffonHome}/$path")
    }
    return new ClassPathResource(path)
}

// Closure that copies a Spring resource to the file system.
copyGriffonResource = { targetFile, Resource resource, boolean overwrite = true ->
    def file = new File(targetFile.toString())
    if (overwrite || !file.exists()) {
        FileCopyUtils.copy(resource.inputStream, new FileOutputStream(file))
    }
}

// Copies a set of resources to a given directory. The set is specified
// by an Ant-style path-matching pattern.
copyGriffonResources = { destDir, pattern, boolean overwrite = true ->
    new File(destDir.toString()).mkdirs()
    Resource[] resources = resolveResources("classpath*:${pattern}")
    resources.each { Resource res ->
        if (res.readable) {
            copyGriffonResource("${destDir}/${res.filename}", res, overwrite)
        }
    }
}

// Closure for unpacking a JAR file that's on the classpath.
griffonUnpack = {Map args ->
    def dir = args["dest"] ?: "."
    def src = args["src"]
    def overwriteOption = args["overwrite"] == null ? true : args["overwrite"]

    // Can't unjar a file from within a JAR, so we copy it to
    // the destination directory first.
    try {
        ant.copy(todir: dir) {
            javaresource(name: src)
        }

        // Now unjar it, excluding the META-INF directory.
        ant.unjar(dest: dir, src: "${dir}/${src}", overwrite: overwriteOption) {
            patternset {
                exclude(name: "META-INF/**")
            }
        }
    }
    finally {
        // Don't need the JAR file any more, so remove it.
        ant.delete(file: "${dir}/${src}", failonerror:false)
    }
}

/**
 * Times the execution of a closure, which can include a target. For
 * example,
 *
 *   profile("compile", compile)
 *
 * where 'compile' is the target.
 */
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

/**
 * Exits the build immediately with a given exit code.
 */
exit = {
    event("Exiting", [it])
    // Prevent system.exit during unit/integration testing
    if (System.getProperty("griffon.cli.testing") || System.getProperty("griffon.disable.exit")) {
        throw new ScriptExitException(it)
    }
    System.exit(it)
}

printFramed = { message, c = '*', padded = false ->
    def pieces = message.split('\n').collect { it.replace('\t',' ') }
    def length = pieces*.size().max() + 4
    def frame = c * length
    def result = pieces.collect {
        def blank = ' ' * (length - 4 - it.size())
        "${c} ${it}${blank} ${c}\n"
    }.join()
    result = "${frame}\n${result}${frame}\n"
    if (padded) result = "\n${result}\n"
    print result
}

confirmInput = { String message, String code = ""->
    if(!isInteractive) {
        println("Cannot ask for input when --non-interactive flag is passed. You need to check the value of the 'isInteractive' variable before asking for input")
        exit(1)
    }
    code = code ? code : "confirm.message" + System.currentTimeMillis()
    ant.input(message: message, addproperty: code, validargs: "y,n")
    ant.antProject.properties[code].toLowerCase() == 'y'
}

askAndDo = { message, yesCallback = null, noCallback = null ->
    confirmInput(message) ? yesCallback?.call() : noCallback?.call()
}

askAndDoNoNag = { message, yesCallback = null, noCallback = null ->
    parseArguments()

    nonagYes = 'y'.equalsIgnoreCase(argsMap.nonag) ?: false
    nonagNo = 'n'.equalsIgnoreCase(argsMap.nonag) ?: false

    if(nonagNo) return
    boolean proceed = nonagYes
    if(!proceed) {
        proceed = confirmInput(message)
    }
    proceed ? yesCallback?.call() : noCallback?.call()
}

/**
 * Modifies the application's metadata, as stored in the "application.properties"
 * file. If it doesn't exist, the file is created.
 */
updateMetadata = { Map entries, file = null ->
    if(!file) file = metadataFile
    if (!file.exists()) {
        ant.propertyfile(
                file: file,
                comment: "Do not edit app.griffon.* properties, they may change automatically. " +
                        "DO NOT put application configuration in here, it is not the right place!")
    }
    def meta = Metadata.getInstance(file)

    // Convert GStrings to Strings.
    def stringifiedEntries = [:]
    entries.each { key, value -> stringifiedEntries[key.toString()] = value.toString() }

    meta.putAll(stringifiedEntries)
    meta.persist()

    if(file.absolutePath == metadataFile.absolutePath) {
        metadata.reload()
    }
}

doForAllPlatforms = { callback ->
    PlatformUtils.PLATFORMS.each { platformKey, platformValue ->
        def platformDir = new File(jardir, platformKey)
        if(callback && platformDir.exists()) {
            callback(platformDir, platformKey)
        }
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

isDebugEnabled = {
    griffonSettings.debugEnabled
}

debug = { msg ->
    griffonSettings.debug(msg)
}

compilingPlugin = { pluginName ->
    getPluginDirForName(pluginName)?.file?.canonicalPath == basedir
}

cliSourceDir = new File("${basedir}/src/cli")
cliSourceDirPath = cliSourceDir.absolutePath
cliClassesDir = new File("${griffonSettings.projectWorkDir}/cli-classes")
cliClassesDirPath = cliClassesDir.absolutePath
hasCliSources = cliSourceDir.exists()
if(hasCliSources) {
    ant.mkdir(dir: cliClassesDirPath)
}

hasFiles = { Map params ->
    params.dir = params.dir as File
    params.dir.exists() ? ant.fileset(params).size() > 0 : false
}

hasJavaOrGroovySources = { dir ->
    hasFiles(dir: dir, includes: '**/*.groovy **/*.java')
}

includeTargets << griffonScript("_GriffonArgParsing")
includeTargets << griffonScript("_GriffonEvents")
