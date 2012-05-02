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

import org.codehaus.griffon.artifacts.model.Plugin

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

// XXX -- NATIVE
    def processPlatformDir = { platformId ->
        platformDir = new File("${basedir}/lib/${platformId}")
        if (platformDir.exists()) {
            debug "  ${platformDir.absolutePath}"
            fileset(dir: platformDir.absolutePath)
        }
        resolveResources("file:${artifactSettings.artifactBase(Plugin.TYPE)}/*/lib/${platformId}").each { dir ->
            if (dir.file.exists()) {
                debug "  ${dir.file.absolutePath}"
                fileset(dir: dir.file.absolutePath)
            }
        }
    }

    def processNativeDir = { platformId ->
        def nativeDir = new File("${basedir}/lib/${platformId}/native")
        if (nativeDir.exists()) {
            debug "  ${nativeDir.absolutePath}"
            fileset(dir: nativeDir.absolutePath)
        }
        resolveResources("file:${artifactSettings.artifactBase(Plugin.TYPE)}/*/lib/${platformId}/native").each { dir ->
            if (dir.exists()) {
                debug "  ${dir.file.absolutePath}"
                fileset(dir: dir.file.absolutePath)
            }
        }
    }

    processPlatformDir platform
    processNativeDir platform
    if (is64Bit) {
        processPlatformDir platform[0..-3]
        processNativeDir platform[0..-3]
    }

    Map<String, List<File>> jars = [:]
    processPlatformLibraries(jars, platform)
    if (is64Bit) {
        processPlatformLibraries(jars, platform[0..-3], false)
    }

    if (jars) {
        List<File> files = []
        jars.values().each { list ->
            list.each { f ->
                debug "  ${f.absolutePath}"
                pathelement(location: f.absolutePath)
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
        pathelement(location: projectCliClassesDir)
        debug "  $projectCliClassesDir"
    }
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

buildClasspath = {
    debug "=== Build Classpath ==="

    def dependencies = griffonSettings.buildDependencies
    if (dependencies) {
        for (File f in dependencies) {
            if (f) {
                debug "  ${f.absolutePath}"
                pathelement(location: f.absolutePath)
            }
        }
    }

    commonClasspath.delegate = delegate
    commonClasspath.call()
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

    ant.path(id: 'griffon.compile.classpath', compileClasspath)
    ant.path(id: 'griffon.test.classpath', testClasspath)
    ant.path(id: 'griffon.runtime.classpath', runtimeClasspath)
    ant.path(id: 'griffon.build.classpath', buildClasspath)

    classpathSet = true
}

