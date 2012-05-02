/*
 * Copyright 2004-2012 the original author or authors.
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
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils
import griffon.util.*

import static org.codehaus.griffon.cli.CommandLineConstants.KEY_INTERACTIVE_MODE

/**
 * Gant script containing build variables.
 *
 * @author Peter Ledbrook (Grails 1.1)
 */

// args = System.getProperty('griffon.cli.args')

// Set up the Griffon environment for this script.
if (!System.getProperty('griffon.env.set')) {
    if (griffonSettings.defaultEnv && getBinding().variables.containsKey('scriptEnv')) {
        griffonEnv = scriptEnv
        griffonSettings.griffonEnv = griffonEnv
        System.setProperty(Environment.KEY, griffonEnv)
        System.setProperty(Environment.DEFAULT, "")
    }
    println "Environment set to ${griffonEnv}"
    System.setProperty('griffon.env.set', 'true')
}

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

if (griffonAppName.indexOf('/') > -1) {
    appClassName = griffonAppName[griffonAppName.lastIndexOf('/')..-1]
} else {
    appClassName = GriffonUtil.getClassNameRepresentation(griffonAppName)
}

// Prepare a configuration file parser based on the current environment.
configSlurper = new ConfigSlurper(griffonEnv)
configSlurper.setBinding(
        griffonHome: griffonHome,
        appName: griffonAppName,
        appVersion: griffonAppVersion,
        userHome: userHome,
        basedir: basedir)

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_settings_called")) return true
_settings_called = true

projectCliClassesDir = new File("${classesDir.absolutePath}/cli")
projectMainClassesDir = new File("${classesDir.absolutePath}/main")
projectTestClassesDir = new File("${classesDir.absolutePath}/test")

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
System.setProperty(KEY_INTERACTIVE_MODE, "true")

buildProps = buildConfig.toProperties()
enableProfile = getPropertyValue("griffon.script.profile", false).toBoolean()
// pluginsHome = artifactBase(Plugin.TYPE)
// pluginsBase = pluginsHome.toString().replaceAll('\\\\', '/')
// archetypesBase = artifactBase(Archetype.TYPE)
archetypeName = ''
archetypeVersion = ''

// Other useful properties.
classesDir = griffonSettings.classesDir
griffonApp = null
isApplicationProject = metadataFile.exists()
isPluginProject = griffonSettings.isPluginProject()
isAddonPlugin = griffonSettings.isAddonPlugin()
isArchetypeProject = griffonSettings.isArchetypeProject()

// Pattern that matches artefacts in the 'griffon-app' directory.
// Note that the capturing group matches any package directory
// structure.
artefactPattern = /\S+?\/griffon-app\/\S+?\/(\S+?)\.groovy/

makeJNLP = false
_skipSigning = false // GRIFFON-118
defaultAppletWidth = 320 // GRIFFON-127
defaultAppletHeight = 240 // GRIFFON-127

applicationConfigFile = new File(basedir, 'griffon-app/conf/Application.groovy')
builderConfigFile = new File(basedir, 'griffon-app/conf/Builder.groovy')
configFile = new File(basedir, 'griffon-app/conf/Config.groovy')

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
        ant.delete(file: "${dir}/${src}", failonerror: false)
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
    event('Exiting', [it])
    // Prevent system.exit during unit/integration testing
    if (System.getProperty('griffon.cli.testing') || System.getProperty('griffon.disable.exit')) {
        throw new ScriptExitException(it)
    }
    System.exit(it)
}

printFramed = { message, c = '*', padded = false ->
    def pieces = message.split('\n').collect { it.replace('\t', ' ') }
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

confirmInput = { String message, String code = '' ->
    if (!isInteractive) {
        if (griffonSettings.defaultAnswerNonInteractive.equalsIgnoreCase('y')) {
            return true
        } else if (griffonSettings.defaultAnswerNonInteractive.equalsIgnoreCase('n')) {
            return false
        }
        // no default answer means we must bail out immediately
        println("Cannot ask for input when --non-interactive flag is passed. You need to check the value of the 'isInteractive' variable before asking for input")
        exit(1)
    }
    code = code ? code : 'confirm.message' + System.currentTimeMillis()
    ant.input(message: message, addproperty: code, validargs: 'y,n')
    ant.antProject.properties[code].toLowerCase() == 'y'
}

askAndDo = { message, yesCallback = null, noCallback = null ->
    confirmInput(message) ? yesCallback?.call() : noCallback?.call()
}

askAndDoNoNag = { message, yesCallback = null, noCallback = null ->
    nonagYes = 'y'.equalsIgnoreCase(argsMap.nonag) ?: false
    nonagNo = 'n'.equalsIgnoreCase(argsMap.nonag) ?: false

    if (nonagNo) return
    boolean proceed = nonagYes
    if (!proceed) {
        proceed = confirmInput(message)
    }
    proceed ? yesCallback?.call() : noCallback?.call()
}

/**
 * Modifies the application's metadata, as stored in the "application.properties"
 * file. If it doesn't exist, the file is created.
 */
updateMetadata = { Map entries, File file = null ->
    if (!file) file = metadataFile
    if (!file.exists()) {
        ant.propertyfile(
                file: file,
                comment: "Do not edit app.griffon.* properties, they may change automatically. " +
                        "DO NOT put application configuration in here, it is not the right place!")
    }
    Metadata meta = file == metadataFile ? Metadata.current : Metadata.getInstance(file)

    // Convert GStrings to Strings.
    Map stringifiedEntries = [:]
    entries.each { key, value -> stringifiedEntries[key.toString()] = value.toString() }

    meta.putAll(stringifiedEntries)
    meta.persist()

    if (file.absolutePath == metadataFile.absolutePath) {
        Metadata.reload()
    }
}

doForAllPlatforms = { callback ->
    PlatformUtils.PLATFORMS.each { platformKey, platformValue ->
        File platformDir = new File(jardir, platformKey)
        if (callback && platformDir.exists()) {
            callback(platformDir, platformKey)
        }
    }
}

logError = { String message, Throwable t ->
    if (t) {
        GriffonUtil.deepSanitize(t)
        t.printStackTrace()
        event 'StatusError', ["$message: ${t.message}"]
    }
    else {
        event 'StatusError', message
    }
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
    File pluginDescriptor = griffonSettings.isPluginProject()
    if(!pluginDescriptor) return false
    String pluginDescriptorName = GriffonUtil.getClassNameRepresentation(pluginName) + 'GriffonPlugin.groovy'
    pluginDescriptorName == pluginDescriptor.name
}

cliSourceDir = new File("${basedir}/src/cli")
cliSourceDirPath = cliSourceDir.absolutePath

hasFiles = { Map params ->
    params.dir = params.dir as File
    params.dir.exists() ? ant.fileset(params).size() > 0 : false
}

hasJavaOrGroovySources = { dir ->
    hasFiles(dir: dir, includes: '**/*.groovy **/*.java')
}

addUrlIfNotPresent = { to, what ->
    if (!to || !what) return
    def urls = to.URLs.toList()
    switch (what.class) {
        case URL: what = new File(what.toURI()); break
        case String: what = new File(what); break
        case GString: what = new File(what.toString()); break
        case File: break; // ok
        default:
            println "Don't know how to deal with $what as it is not an URL nor a File"
            System.exit(1)
    }

    if (what.directory && !what.exists()) what.mkdirs()
    def url = what.toURI().toURL()
    if (!urls.contains(url) && (what.directory || !urls.find {it.path.endsWith(what.name)})) {
        to.addURL(url)
    }
}

// --== ARTIFACT STUFF ==--

loadArtifactDescriptorClass = { String artifactFile ->
    try {
        // Rather than compiling the descriptor via Ant, we just load
        // the Groovy file into a GroovyClassLoader. We add the classes
        // directory to the class loader in case it didn't exist before
        // the associated descriptor's sources were compiled.
        def gcl = new GroovyClassLoader(classLoader)
        gcl.addURL(griffonSettings.baseDir.toURI().toURL())
        String artifactClassName = artifactFile.endsWith('.groovy') ? artifactFile[0..-8] : artifactFile
        return gcl.loadClass(artifactClassName).newInstance()
    }
    catch (Throwable t) {
        event('StatusError', [t.message])
        GriffonUtil.sanitize(t).printStackTrace(System.out)
        ant.fail('Cannot instantiate artifact file')
    }
}

projectType = 'app'

target(createStructure: "Creates the application directory structure") {
    ant.sequential {
        mkdir(dir: "${basedir}/griffon-app")
        mkdir(dir: "${basedir}/griffon-app/conf")
        if (projectType != 'archetype') {
            if (projectType == 'app') {
                mkdir(dir: "${basedir}/griffon-app/conf/keys")
                mkdir(dir: "${basedir}/griffon-app/conf/webstart")
                mkdir(dir: "${basedir}/griffon-app/conf/dist")
                mkdir(dir: "${basedir}/griffon-app/conf/dist/applet")
                mkdir(dir: "${basedir}/griffon-app/conf/dist/jar")
                mkdir(dir: "${basedir}/griffon-app/conf/dist/shared")
                mkdir(dir: "${basedir}/griffon-app/conf/dist/webstart")
                mkdir(dir: "${basedir}/griffon-app/conf/dist/zip")
            }
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
}

target(checkVersion: "Stops build if app expects different Griffon version") {
    if (metadataFile?.exists()) {
        if (appGriffonVersion != griffonVersion) {
            println "Application expects griffon version [$appGriffonVersion], but GRIFFON_HOME is version " +
                    "[$griffonVersion] - use the correct Griffon version or run 'griffon upgrade' if this Griffon " +
                    "version is newer than the version your application expects."
            exit(1)
        }
    } else {
        // Griffon has always had version numbers, this is an error state
        println "Application is an unknown Griffon version, please run: griffon upgrade"
        exit(1)
    }
}


target(updateAppProperties: "Updates default application.properties") {
    def entries = [
            'app.name': GriffonNameUtils.getPropertyName(griffonAppName),
            'app.griffon.version': griffonVersion
    ]
    if (griffonAppVersion) {
        entries['app.version'] = griffonAppVersion
    }
    updateMetadata(entries)

    // Make sure if this is a new project that we update the var to include version
    appGriffonVersion = griffonVersion
}

buildConfig.griffon.application.mainClass = buildConfig.griffon.application.mainClass ?: 'griffon.test.mock.MockGriffonApplication'

resetDependencyResolution = {
    pluginSettings.clearCaches()
    runDependencyResolution = true
    runFrameworkDependencyResolution = true
    classpathSet = false
}