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

import griffon.util.Metadata
import org.codehaus.griffon.util.BuildSettings
import org.codehaus.griffon.util.GriffonNameUtils
import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.FileCopyUtils

// XXX -- NATIVE
import static griffon.util.GriffonApplicationUtils.isLinux
import static griffon.util.GriffonApplicationUtils.isSolaris
import static griffon.util.GriffonApplicationUtils.isMacOSX
import static griffon.util.GriffonApplicationUtils.is64Bit
// XXX -- NATIVE

/**
 * Gant script containing build variables.
 *
 * @author Peter Ledbrook
 *
 * @since 1.1
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_settings_called")) return
_settings_called = true

// Read build properties for Griffon into ant properties.
if (griffonSettings.griffonHome) {
    ant.property(file: "${griffonHome}/build.properties")
}
else {
    ant.property(resource: "build.properties")
}

// Set up various build settings. System properties take precedence
// over those defined in BuildSettings, which in turn take precedence
// over the defaults.
buildProps = buildConfig.toProperties()
// enableJndi = getPropertyValue("enable.jndi", false).toBoolean()
enableProfile = getPropertyValue("griffon.script.profile", false).toBoolean()
// serverPort = getPropertyValue("server.port", 8080).toInteger()
// serverPortHttps = getPropertyValue("server.port.https", 8443).toInteger()
// serverHost = getPropertyValue("server.host", null)
pluginsHome = griffonSettings.projectPluginsDir.path

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
    appClassName = GriffonNameUtils.getClassNameRepresentation(griffonAppName)


// Other useful properties.
args = System.getProperty("griffon.cli.args")
classesDir = griffonSettings.classesDir
griffonApp = null
griffonContext = null
griffonTmp = "${griffonSettings.griffonWorkDir}/tmp"
isPluginProject = griffonSettings.baseDir.listFiles().find { it.name.endsWith("GriffonPlugin.groovy") }
isAddonPlugin = griffonSettings.baseDir.listFiles().find { it.name.endsWith("GriffonAddon.groovy") }

shouldPackageTemplates = false
config = new ConfigObject()
//scaffoldDir = "${basedir}/web-app/WEB-INF/templates/scaffolding"
configFile = new File("${basedir}/griffon-app/conf/Config.groovy")
applicationFile = new File("${basedir}/griffon-app/conf/Application.groovy")
//webXmlFile = new File("${resourcesDirPath}/web.xml")

// Pattern that matches artefacts in the 'griffon-app' directory.
// Note that the capturing group matches any package directory
// structure.
artefactPattern = /\S+?\/griffon-app\/\S+?\/(\S+?)\.groovy/

defaultGriffonApplicationClass = "griffon.application.SwingApplication"
defaultGriffonAppletClass = "griffon.applet.SwingApplet"
makeJNLP = false
_skipSigning = false // GRIFFON-118
defaultAppletWidth = 240 // GRIFFON-127
defaultAppletHeight = 320 // GRIFFON-127

// Set up the Griffon environment for this script.
if (!System.getProperty("griffon.env.set")) {
    if (griffonSettings.defaultEnv && getBinding().variables.containsKey("scriptEnv")) {
        griffonEnv = scriptEnv
        griffonSettings.griffonEnv = griffonEnv
        System.setProperty(BuildSettings.ENVIRONMENT, griffonEnv)
        System.setProperty(BuildSettings.ENVIRONMENT_DEFAULT, "")
    }
    println "Environment set to ${griffonEnv}"
    System.setProperty("griffon.env.set", "true")
}

// Prepare a configuration file parser based on the current environment.
configSlurper = new ConfigSlurper(griffonEnv)
configSlurper.setBinding(griffonHome:griffonHome,
                         appName:griffonAppName,
                         appVersion:griffonAppVersion,
                         userHome:userHome,
                         basedir:basedir/*,
                         servletVersion:servletVersion*/)

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

// Closure that returns a Spring Resource - either from $Griffon_HOME
// if that is set, or from the classpath.
griffonResource = {String path ->
    if (griffonSettings.griffonHome) {
        return new FileSystemResource("${griffonSettings.griffonHome}/$path")
    }
    else {
        return new ClassPathResource(path)
    }
}

// Closure that copies a Spring resource to the file system.
copyGriffonResource = { String targetFile, Resource resource ->
    FileCopyUtils.copy(resource.inputStream, new FileOutputStream(targetFile))
}

// Copies a set of resources to a given directory. The set is specified
// by an Ant-style path-matching pattern.
copyGriffonResources = { String destDir, String pattern ->
    new File(destDir).mkdirs()
    Resource[] resources = resolveResources("classpath:${pattern}")
    resources.each { Resource res ->
        if (res.readable) {
            copyGriffonResource("${destDir}/${res.filename}", res)
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
    ant.copy(todir: dir) {
        javaresource(name: src)
    }

    // Now unjar it, excluding the META-INF directory.
    ant.unjar(dest: dir, src: "${dir}/${src}", overwrite: overwriteOption) {
        patternset {
            exclude(name: "META-INF/**")
        }
    }

    // Don't need the JAR file any more, so remove it.
    ant.delete(file: "${dir}/${src}")
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
 * Resolves the value for a given property name. It first looks for a
 * system property, then in the BuildSettings configuration, and finally
 * uses the given default value if other options are exhausted.
 */
def getPropertyValue(String propName, defaultValue) {
    // First check whether we have a system property with the given name.
    def value = System.getProperty(propName)
    if (value != null) return value

    // Now try the BuildSettings settings.
    value = buildProps[propName]

    // Return the BuildSettings value if there is one, otherwise use the
    // default.
    return value != null ? value : defaultValue
}

// XXX -- NATIVE

platform = 'windows'
if(isSolaris) platform = 'solaris'
else if(isLinux) platform = 'linux'
else if(isMacOSX) platform = 'macosx'
if(is64Bit) platform += '64'

PLATFORMS = [
    windows: [
        nativelib: '.dll',
        webstartName: 'Windows',
        archs: ['x86']],
    linux: [
        nativelib: '.so',
        webstartName: 'Linux',
        archs: ['i386', 'x86']],
    macosx: [
        nativelib: '.jnilib',
        webstartName: 'Mac OS X',
        archs: ['i386', 'ppc']],
    solaris: [
        nativelib: '.so',
        webstartName: 'SunOS',
        archs: ['x86', 'sparc', 'sparcv9']],
    windows64: [
        nativelib: '.dll',
        webstartName: 'Windows',
        archs: ['amd64', 'x86_64']],
    linux64: [
        nativelib: '.so',
        webstartName: 'Linux',
        archs: ['amd64', 'x86_64']],
    macosx64: [
        nativelib: '.jnilib',
        webstartName: 'Mac OS X',
        archs: ['x86_64']],
    solaris64: [
        nativelib: '.so',
        webstartName: 'SunOS',
        archs: ['amd64', 'x86_64']]
]

doForAllPlatforms = { callback ->
    PLATFORMS.each { platformKey, platformValue ->
        def platformDir = new File(jardir, platformKey)
        if(callback && platformDir.exists()) {
            callback(platformDir, platformKey)
        }
    }
}

// XXX -- NATIVE

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

confirmInput = { String message ->
    def propName = "confirm.message" + System.currentTimeMillis()
    ant.input(message: message, addproperty: propName, validargs: "y,n")
    ant.antProject.properties[propName].toLowerCase() == 'y'
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
updateMetadata = { Map entries ->
    if (!metadataFile.exists()) {
        ant.propertyfile(
                file: metadataFile,
                comment: "Do not edit app.griffon.* properties, they may change automatically. " +
                        "DO NOT put application configuration in here, it is not the right place!")
        metadata = Metadata.getInstance(metadataFile)
    }

    // Convert GStrings to Strings.
    def stringifiedEntries = [:]
    entries.each { key, value -> stringifiedEntries[key.toString()] = value.toString() }

    metadata.putAll(stringifiedEntries)
    metadata.persist()
}

