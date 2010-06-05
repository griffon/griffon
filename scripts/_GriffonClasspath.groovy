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
import org.codehaus.groovy.control.CompilerConfiguration
import org.springframework.core.io.FileSystemResource
import org.codehaus.griffon.plugins.GriffonPluginUtils

/**
 * Gant script containing the Griffon classpath setup.
 *
 * @author Peter Ledbrook
 *
 * @since 1.1
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey("_griffon_classpath_called")) return
_griffon_classpath_called = true

includeTargets << griffonScript("_GriffonSettings")

classpathSet = false

target(classpath: "Sets the Griffon classpath") {
    setClasspath()
}

/**
 * Obtains all of the plug-in Lib directories
 */
getPluginLibDirs = {
    GriffonPluginUtils.getPluginLibDirectories(pluginsHome, resolveResources)
}

/**
 * Obtains an array of all plug-in JAR files as Spring Resource objects
 */
getPluginJarFiles = {
    GriffonPluginUtils.getPluginJarFiles(pluginsHome, resolveResources)
}

/**
 * Obtains an array of all plug-in test JAR files as Spring Resource objects
 */
getPluginTestFiles = {
    GriffonPluginUtils.getPluginTestFiles(pluginsHome, resolveResources)
}

getJarFiles = {->
    def jarFiles = resolveResources("file:${basedir}/lib/*.jar").toList()
    def pluginJars = getPluginJarFiles()

    for (pluginJar in pluginJars) {
        boolean matches = jarFiles.any {it.file.name == pluginJar.file.name}
        if (!matches) jarFiles.add(pluginJar)
    }

    def userJars = resolveResources("file:${griffonSettings.griffonWorkDir}/lib/*.jar")
    for (userJar in userJars) {
        jarFiles.add(userJar)
    }

// XXX -- NATIVE
    resolveResources("file:${basedir}/lib/${platform}/*.jar").each { platformJar ->
        jarFiles << platformJar
    }
    resolveResources("file:${pluginsHome}/*/lib/${platform}/*.jar").each { platformPluginJar ->
        jarFiles << platformPluginJar
    }
// XXX -- NATIVE

    jarFiles.addAll(getExtraDependencies())

    jarFiles
}

getExtraDependencies = {
    def jarFiles =[]
    if(buildConfig?.griffon?.compiler?.dependencies) {
        def extraDeps = ant.fileScanner(buildConfig.griffon.compiler.dependencies)
        for(jar in extraDeps) {
            jarFiles << new FileSystemResource(jar)
        }
    }
    jarFiles
}

commonClasspath = {
    def griffonDir = resolveResources("file:${basedir}/griffon-app/*")
    for (d in griffonDir) {
        pathelement(location: "${d.file.absolutePath}")
    }

    for (pluginLib in getPluginLibDirs()) {
        if(pluginLib.file.exists()) fileset(dir: pluginLib.file.absolutePath)
    }

// XXX -- NATIVE
    resolveResources("file:${basedir}/lib/${platform}").each { platformLib ->
        if(platformLib.file.exists()) fileset(dir: platformLib.file.absolutePath)
    }
    resolveResources("file:${pluginsHome}/*/lib/${platform}").each { platformPluginLib ->
        if(platformPluginLib.file.exists()) fileset(dir: platformPluginLib.file.absolutePath)
    }
// XXX -- NATIVE
}

compileClasspath = {
    commonClasspath.delegate = delegate
    commonClasspath.call()

    griffonSettings.compileDependencies?.each { File f ->
        file(file: f.absolutePath)
    }
}

testClasspath = {
    commonClasspath.delegate = delegate
    commonClasspath.call()

    pathelement(location: "${classesDir.absolutePath}")
    pathelement(location: "${griffonSettings.testClassesDir}/shared")
    pathelement(location: "${griffonSettings.testResourcesDir}")

    griffonSettings.testDependencies?.each { File f ->
        file(file: f.absolutePath)
    }

    for (pluginTestJar in getPluginTestFiles()) {
        if(pluginTestJar.file.exists()) file(file: pluginTestJar.file.absolutePath)
    }
}

runtimeClasspath = {
    commonClasspath.delegate = delegate
    commonClasspath.call()

    griffonSettings.runtimeDependencies?.each { File f ->
        file(file: f.absolutePath)
    }

    pathelement(location: "${classesDir.absolutePath}")
}

void setClasspath() {
    // Make sure the following code is only executed once.
    if (classpathSet) return

    ant.path(id: "griffon.compile.classpath", compileClasspath)
    ant.path(id: "griffon.test.classpath", testClasspath)
    ant.path(id: "griffon.runtime.classpath", runtimeClasspath)

    def griffonDir = resolveResources("file:${basedir}/griffon-app/*")
    StringBuffer cpath = new StringBuffer("")

    def jarFiles = getJarFiles()

    for (dir in griffonDir) {
        cpath << dir.file.absolutePath << File.pathSeparator
        // Adding the griffon-app folders to the root loader causes re-load issues as
        // root loader returns old class before the griffon GCL attempts to recompile it
        //rootLoader?.addURL(dir.URL)
    }
    cpath << classesDirPath << File.pathSeparator
    for (jar in jarFiles) {
        cpath << jar.file.absolutePath << File.pathSeparator
        addUrlIfNotPresent rootLoader, jar.file
    }

    compConfig = new CompilerConfiguration()
    compConfig.setClasspath(cpath.toString());
    compConfig.sourceEncoding = "UTF-8"

    addUrlIfNotPresent rootLoader, resourcesDirPath
    addUrlIfNotPresent rootLoader, testResourcesDirPath

    classpathSet = true
}

addUrlIfNotPresent = { to, what ->
    if(!to || !what) return
    def urls = to.URLs.toList()
    switch(what.class) {
         case URL:
             if(!urls.contains(what)) {
                 to.addURL(what)
             }
             return
         case String: what = new File(what); break
         case GString: what = new File(what.toString()); break
         case File: break; // ok
         default:
             println "Don't know how to deal with $what as it is not an URL nor a File"
             System.exit(1)
    }

    if(what.directory && !what.exists()) what.mkdirs()
    def url = what.toURI().toURL()
    if(!urls.contains(url)) {
        to.addURL(url)
    }
}
