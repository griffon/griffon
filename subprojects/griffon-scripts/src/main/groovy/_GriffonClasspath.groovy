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

import griffon.util.PlatformUtils
import static griffon.util.GriffonApplicationUtils.is64Bit

/**
 * Gant script containing the Griffon classpath setup.
 *
 * @author Peter Ledbrook (Grails 1.1)
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_classpath_called')) return
_griffon_classpath_called = true

classpathSet = false

target(name: 'classpath', description: "Sets the Griffon classpath", prehook: null, posthook: null) {
    setClasspath()
}

commonClasspath = {
    def griffonDir = resolveResources("file:${basedir}/griffon-app/*")
    for (d in griffonDir) {
        debug "  ${d.file.absolutePath}"
        pathelement(location: "${d.file.absolutePath}")
    }

    if (projectMainClassesDir.exists()) {
        pathelement(location: "${projectMainClassesDir.absolutePath}")
        debug "  ${projectMainClassesDir.absolutePath}"
    }

    def pluginLibDirs = pluginSettings.pluginLibDirectories.findAll {it.exists()}
    /*
    for (pluginLib in pluginLibDirs) {
        debug "  ${pluginLib.file.absolutePath}"
        fileset(dir: pluginLib.file.absolutePath)
    }
    */

// XXX -- NATIVE
    def localPlatformLibAdded = false
    platformDir = new File("${basedir}/lib/${PlatformUtils.platform}")
    if (platformDir.exists()) {
        debug "  ${platformDir.absolutePath}"
        fileset(dir: platformDir.absolutePath)
        localPlatformLibAdded = true
    }
    resolveResources("file:${pluginsHome}/*/lib/${PlatformUtils.platform}").each { platformPluginLib ->
        if (platformPluginLib.file.exists()) {
            debug "  ${platformPluginLib.file.absolutePath}"
            fileset(dir: platformPluginLib.file.absolutePath)
        }
    }
    def localPlatformNativeAdded = false
    def platformLibDir = new File("${basedir}/lib/${PlatformUtils.platform}/native")
    if (platformLibDir.exists()) {
        debug "  ${platformLibDir.absolutePath}"
        fileset(dir: platformLibDir.absolutePath)
        localPlatformNativeAdded = true
    }
    for (pluginLibDir in pluginLibDirs) {
        platformLibDir = new File("${pluginLibDir}/lib/${PlatformUtils.platform}/native")
        if (platformLibDir.exists()) {
            debug "  ${platformLibDir.absolutePath}"
            fileset(dir: platformLibDir.absolutePath)
        }
    }

    if (is64Bit) {
        platformDir = new File("${basedir}/lib/${PlatformUtils.platform[0..-3]}")
        if (!localPlatformLibAdded && platformDir.exists()) {
            debug "  ${platformDir.absolutePath}"
            fileset(dir: platformDir.absolutePath)
        }
        resolveResources("file:${pluginsHome}/*/lib/${PlatformUtils.platform[0..-3]}").each { platformPluginLib ->
            if (platformPluginLib.file.exists()) {
                debug "  ${platformPluginLib.file.absolutePath}"
                fileset(dir: platformPluginLib.file.absolutePath)
            }
        }
        platformLibDir = new File("${basedir}/lib/${PlatformUtils.platform[0..-3]}/native")
        if (!localPlatformNativeAdded && platformLibDir.exists()) {
            debug "  ${platformLibDir.absolutePath}"
            fileset(dir: platformLibDir.absolutePath)
        }
        for (pluginLibDir in pluginLibDirs) {
            platformLibDir = new File("${pluginLibDir}/lib/${PlatformUtils.platform[0..-3]}/native")
            if (platformLibDir.exists()) {
                debug "  ${platformLibDir.absolutePath}"
                fileset(dir: platformLibDir.absolutePath)
            }
        }
    }
// XXX -- NATIVE
}

compileClasspath = {
    debug "=== Compile Classpath ==="
    commonClasspath.delegate = delegate
    commonClasspath.call()

    def dependencies = griffonSettings.compileDependencies
    if (dependencies) {
        for (File f in dependencies) {
            if (f) {
                debug "  ${f.absolutePath}"
                pathelement(location: f.absolutePath)
            }
        }
    }
}

testClasspath = {
    debug "=== Test Classpath ==="
    commonClasspath.delegate = delegate
    commonClasspath.call()

    def dependencies = griffonSettings.testDependencies
    if (dependencies) {
        for (File f in dependencies) {
            if (f) {
                debug "  ${f.absolutePath}"
                pathelement(location: f.absolutePath)
            }
        }
    }

    if (projectTestClassesDir.exists()) {
        pathelement(location: projectTestClassesDir)
        debug "  ${projectTestClassesDir}"
    }
    if (griffonSettings.testResourcesDir.exists()) {
        pathelement(location: "${griffonSettings.testResourcesDir}")
        debug "  ${griffonSettings.testResourcesDir}"
    }
    if (cliSourceDir.exists()) {
        pathelement(location: projectCompileClassesDir)
        debug "  $projectCompileClassesDir"
    }

    /*
    for (pluginTestJar in getPluginTestFiles()) {
        if (pluginTestJar.file.exists()) {
            debug "  ${pluginTestJar.file.absolutePath}"
            file(file: pluginTestJar.file.absolutePath)
        }
    }
    */
}

runtimeClasspath = {
    debug "=== Runtime Classpath ==="
    commonClasspath.delegate = delegate
    commonClasspath.call()

    def dependencies = griffonSettings.runtimeDependencies
    if (dependencies) {
        for (File f in dependencies) {
            if (f) {
                debug "  ${f.absolutePath}"
                pathelement(location: f.absolutePath)
            }
        }
    }
}

/**
 * Converts an Ant path into a list of URLs.
 */
classpathToUrls = { String classpathId ->
    def propName = 'converted.classpath'
    ant.pathconvert(refid: classpathId, dirsep: '/', pathsep: ':', property: propName)

    return ant.project.properties.get(propName).split(':').collect { new File(it).toURI().toURL() }
}

void setClasspath() {
    // Make sure the following code is only executed once.
    if (classpathSet) return

    projectCompileClassesDir = new File("${classesDir.absolutePath}/compile")
    projectMainClassesDir = new File("${classesDir.absolutePath}/main")
    projectTestClassesDir = new File("${classesDir.absolutePath}/test")

    if (isApplicationProject || isPluginProject) {
        [
                projectCompileClassesDir,
                projectMainClassesDir,
                projectTestClassesDir,
                griffonSettings.testClassesDir,
                griffonSettings.testResourcesDir,
                griffonSettings.resourcesDir
        ].each { dir ->
            if (!dir.exists()) ant.mkdir(dir: dir)
            addUrlIfNotPresent rootLoader, dir
        }
    }

    ant.path(id: 'griffon.compile.classpath', compileClasspath)
    ant.path(id: 'griffon.test.classpath', testClasspath)
    ant.path(id: 'griffon.runtime.classpath', runtimeClasspath)

    /*
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
    cpath << testResourcesDirPath << File.pathSeparator

    // We need to set up this configuration so that we can compile the
    // plugin descriptors, which lurk in the root of the plugin's project
    // directory.
    compConfig = new CompilerConfiguration()
    compConfig.setClasspath(cpath.toString());
    compConfig.sourceEncoding = "UTF-8"

    if (isApplicationProject || isPluginProject) {
        // if(!resourcesDir.exists()) ant.mkdir(dir: resourcesDirPath)
        // if(!griffonSettings.testResourcesDir.exists()) ant.mkdir(dir: griffonSettings.testResourcesDir)
        addUrlIfNotPresent rootLoader, resourcesDirPath
        addUrlIfNotPresent rootLoader, griffonSettings.testResourcesDir
    }
    */

    classpathSet = true
}

dirNotEmpty = { Map args ->
    if (!args.dir.exists()) return false
    return ant.fileset(args).size() > 0
}
