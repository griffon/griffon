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
import org.codehaus.griffon.commons.GriffonClassUtils as GCU

import org.codehaus.griffon.commons.GriffonContext
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.Resource

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

// Read build properties for Griffon into Ant properties.
if (griffonHome) {
    ant.property(file: "${griffonHome}/build.properties")
}
else {
    ant.property(resource: "build.properties")
}

// Set up various build settings. System properties take precedence
// over those defined in PreInit, which in turn take precedence over
// the defaults.
enableJndi = getPropertyValue("enable.jndi", false).toBoolean()
enableProfile = getPropertyValue("griffon.script.profile", false).toBoolean()
serverPort = getPropertyValue("server.port", 8080).toInteger()
serverPortHttps = getPropertyValue("server.port.https", 8443).toInteger()
serverHost = getPropertyValue("server.host", null)
pluginsHome = pluginsDirPath

// Load the application metadata (application.properties)
griffonAppName = null
griffonAppVersion = null
appGriffonVersion = null
metadata = new Properties()
metadataFile = new File("${basedir}/application.properties")

// Get App's metadata if there is any.
if (metadataFile.exists()) {
    // We know we have an app
    metadataFile.withInputStream { input ->
        metadata.load input
    }

    def props = metadata
    griffonAppName = props.'app.name'
    griffonAppVersion = props.'app.version'
    appGriffonVersion = props.'app.griffon.version'
}

// If no app name property (upgraded/new/edited project) default to basedir.
if (!griffonAppName) {
    griffonAppName = baseName
}

if(griffonAppName.indexOf('/') >-1)
    appClassName = griffonAppName[griffonAppName.lastIndexOf('/')..-1]
else
    appClassName = GCU.getClassNameRepresentation(griffonAppName)


// Other useful properties.
args = System.getProperty("griffon.cli.args")
classesDir = new File(classesDirPath)
griffonApp = null
griffonContext = null
griffonTmp = "${griffonWorkDir}/tmp"
isPluginProject = baseFile.listFiles().find { it.name.endsWith("GriffonPlugin.groovy") }

// Pattern that matches artefacts in the 'griffon-app' directory.
// Note that the capturing group matches any package directory
// structure.
artefactPattern = /\S+?\/griffon-app\/\S+?\/(\S+?)\.groovy/

// Set up the Griffon environment for this script.
if(!System.getProperty("griffon.env.set")) {
    if(defaultEnv) {
        try {
            griffonEnv = getProperty("scriptEnv")
            System.setProperty(GriffonContext.ENVIRONMENT, griffonEnv)
            System.setProperty(GriffonContext.ENVIRONMENT_DEFAULT, "")
        }
        catch (MissingPropertyException mpe) {
            //ignore, ok
        }
    }
    println "Environment set to ${griffonEnv}"
    System.setProperty("griffon.env.set", "true")
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
 * system property, then in the PreInit configuration, and finally uses
 * the given default value if other options are exhausted.
 */
def getPropertyValue(String propName, defaultValue) {
    // First check whether we have a system property with the given name.
    def value = System.getProperty(propName)
    if (value != null) return value

    // Now try the PreInit settings.
    value = preInitProperties[propName]

    // Return the PreInit value if there is one, otherwise use the
    // default.
    return value != null ? value : defaultValue
}
