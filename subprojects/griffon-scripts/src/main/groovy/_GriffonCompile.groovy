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

import org.codehaus.groovy.runtime.StackTraceUtils

/**
 * Gant script that compiles Groovy and Java files in the src tree
 *
 * @author Graeme Rocher (Grails 0.4)
 */

includeTargets << griffonScript('_GriffonClasspath')

ant.taskdef(name: 'groovyc', classname: 'org.codehaus.groovy.ant.Groovyc')
ant.taskdef(name: 'griffonc', classname: 'org.codehaus.griffon.compiler.GriffonCompiler')

additionalSources = []

compilerOptions = { Map options ->
    if (griffonSettings.sourceEncoding != null) options.encoding = griffonSettings.sourceEncoding
    if (griffonSettings.compilerDebug != null) options.debug = griffonSettings.compilerDebug
    if (griffonSettings.compilerSourceLevel != null) options.source = griffonSettings.compilerSourceLevel
    if (griffonSettings.compilerTargetLevel != null) options.target = griffonSettings.compilerTargetLevel
    debug "Javac options $options"
    options
}

compilerPaths = { String classpathId ->
    def excludedPaths = ["resources", "i18n", "conf"] // conf gets special handling

    for (dir in new File("${basedir}/griffon-app").listFiles()) {
        if (!excludedPaths.contains(dir.name) && dir.isDirectory())
            src(path: "$dir")
    }
    // Handle conf/ separately to exclude subdirs/package misunderstandings
    // src(path: "${basedir}/griffon-app/conf")

    src(path: new File("${griffonSettings.sourceDir}/main"))

    additionalSources.each { srcPath ->
        if (new File(srcPath).exists()) src(path: srcPath)
    }

    javac(compilerOptions(classpathref: classpathId))
}

compileProjectSources = { destinationDir, classpathId, sources ->
    if (argsMap.compileTrace) {
        println('-' * 80)
        println "[GRIFFON] compiling to ${destinationDir}"
        println "[GRIFFON] '${classpathId}' entries"
        ant.project.getReference(classpathId).list().each {println("  $it")}
        println('-' * 80)
    }

    try {
        if (destinationDir instanceof String) destinationDir = new File(destinationDir)
        ant.griffonc(destdir: destinationDir,
                projectName: griffonAppName,
                basedir: griffonSettings.baseDir.path,
                verbose: (argsMap.verboseCompile ? true : false),
                classpathref: classpathId,
                encoding: griffonSettings.sourceEncoding, sources)
        addUrlIfNotPresent classLoader, destinationDir
    }
    catch (Exception e) {
        if (argsMap.verboseCompile) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace(System.err)
        }
        event("StatusFinal", ["Compilation error: ${e.message}"])
        exit(1)
    }
}

target(name: 'compile', description: "Implementation of compilation phase",
        prehook: null, posthook: null) {
    def compileDependencies = [classpath]
    if (argsMap.clean) {
        includeTargets << griffonScript('_GriffonClean')
        compileDependencies = [clean] + compileDependencies
    }
    depends(* compileDependencies)

    if (isApplicationProject || isPluginProject) {
        [
                projectCliClassesDir,
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

    File srcMain = new File("${griffonSettings.sourceDir}/main")
    if (!srcMain.exists()) ant.mkdir(dir: srcMain)

    profile("Compiling sources to location [$projectMainClassesDir]") {
        // If this is a plugin project, the descriptor is not included
        // in the compiler's source path. So, we manually compile it
        // now.

        String classpathId = 'griffon.compile.classpath'

        event('CompileSourcesStart', [])
        compileProjectSources(projectMainClassesDir, classpathId, compilerPaths.curry(classpathId))
        compileProjectSources(projectMainClassesDir, classpathId) {
            src(path: "${basedir}/griffon-app/conf")
            include(name: '*.groovy')
            include(name: '*.java')
            exclude(name: 'BuildConfig.groovy')
            javac(compilerOptions(classpathref: classpathId))
        }
        ant.copy(todir: projectMainClassesDir) {
            fileset(dir: "${basedir}/griffon-app/conf") {
                include(name: '*.properties')
                include(name: '*.xml')
            }
        }
        event('CompileSourcesEnd', [])

        // if (isPluginProject) {
        if (cliSourceDir.exists()) {
            ant.mkdir(dir: projectCliClassesDir)
            ant.path(id: 'plugin.cli.compile.classpath') {
                path(refid: 'griffon.compile.classpath')
                pathElement(location: projectMainClassesDir)
            }
            compileProjectSources(projectCliClassesDir, 'plugin.cli.compile.classpath') {
                src(path: cliSourceDir)
                javac(compilerOptions(classpathref: 'plugin.cli.compile.classpath'))
            }
            ant.copy(todir: projectCliClassesDir) {
                fileset(dir: "${basedir}/src/cli") {
                    exclude(name: '**/*.java')
                    exclude(name: '**/*.groovy')
                    exclude(name: '**/.svn')
                }
            }
        }
        // }

        if (griffonSettings.isAddonPlugin()) {
            ant.path(id: 'addon.classpath') {
                path(refid: "griffon.compile.classpath")
                pathElement(location: projectMainClassesDir)
            }
            compileProjectSources(projectMainClassesDir, 'addon.classpath') {
                src(path: basedir)
                include(name: '*GriffonAddon.groovy')
                include(name: '*GriffonAddon.java')
                javac(compilerOptions(classpathref: 'addon.classpath'))
            }
        }
    }

    compileProjectTests()
}

target(name: 'compileProjectTests', description: "Compiles shared test sources",
        prehook: null, posthook: null) {
    compileProjectTestSrc(basedir)
    def metainfDir = new File("${basedir}/griffon-app/conf/metainf")
    boolean hasMetainf = hasFiles(dir: metainfDir, excludes: '**/*.svn/**, **/CVS/**')
    if (hasMetainf) {
        def metaResourcesDir = new File("${testResourcesDirPath}/META-INF")
        ant.copy(todir: metaResourcesDir) {
            fileset(dir: metainfDir)
        }
        addUrlIfNotPresent classLoader, testResourcesDirPath
    }
}

compileProjectTestSrc = { rootDir ->
    def projectTest = new File("${rootDir}/src/test")
    boolean hasProjectTest = hasFiles(dir: projectTest, includes: '**/*.groovy, **/*.java')

    if (hasProjectTest) {
        profile("Compiling project test sources to location [$projectTestClassesDir]") {
            ant.path(id: 'projectTest.classpath') {
                path(refid: "griffon.compile.classpath")
                pathElement(location: projectMainClassesDir)
                pathElement(location: projectTestClassesDir)
            }
            compileProjectSources(projectTestClassesDir, 'projectTest.classpath') {
                src(path: projectTest)
                javac(compilerOptions(classpathref: 'projectTest.classpath'))
            }
        }
    }
    def testResources = new File("${basedir}/test/resources")
    boolean hasTestResources = hasFiles(dir: testResources, excludes: '**/*.svn/**, **/CVS/**')
    if (hasTestResources) {
        ant.mkdir(dir: testResourcesDirPath)
        ant.copy(todir: testResourcesDirPath) {
            fileset(dir: testResources)
        }
        addUrlIfNotPresent classLoader, testResourcesDirPath
    }
}
