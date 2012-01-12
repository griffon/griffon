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

compilerPaths = { String classpathId ->
    def excludedPaths = ["resources", "i18n", "conf"] // conf gets special handling

    for (dir in new File("${basedir}/griffon-app").listFiles()) {
        if (!excludedPaths.contains(dir.name) && dir.isDirectory())
            src(path: "$dir")
    }
    // Handle conf/ separately to exclude subdirs/package misunderstandings
    // src(path: "${basedir}/griffon-app/conf")

    File srcMain = new File("${griffonSettings.sourceDir}/main")
    if (!srcMain.exists()) ant.mkdir(dir: srcMain)
    src(path: srcMain)

    additionalSources.each { srcPath ->
        if (new File(srcPath).exists()) src(path: srcPath)
    }

    javac(classpathref: classpathId, encoding: griffonSettings.sourceEncoding, debug: 'yes')
}

compileSources = { destinationDir, classpathId, sources ->
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
    depends(classpath)

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

    profile("Compiling sources to location [$projectMainClassesDir]") {
        // If this is a plugin project, the descriptor is not included
        // in the compiler's source path. So, we manually compile it
        // now.

        String classpathId = 'griffon.compile.classpath'

        compileSrc = compileSources.curry(projectMainClassesDir)
        event('CompileSourcesStart', [])
        compileSrc(classpathId, compilerPaths.curry(classpathId))
        compileSrc(classpathId) {
            src(path: "${basedir}/griffon-app/conf")
            include(name: '*.groovy')
            include(name: '*.java')
            javac(classpathref: classpathId, encoding: griffonSettings.sourceEncoding, debug: 'yes')
        }
        ant.copy(todir: projectMainClassesDir) {
            fileset(dir: "${basedir}/griffon-app/conf") {
                include(name: '*.properties')
                include(name: '*.xml')
            }
        }
        event('CompileSourcesEnd', [])

        // If this is a plugin project, the descriptor is not included
        // in the compiler's source path. So, we manually compile it
        // now.
        if (isPluginProject) {
            if (cliSourceDir.exists()) {
                ant.mkdir(dir: projectCompileClassesDir)
                ant.path(id: 'plugin.cli.compile.classpath') {
                    path(refid: 'griffon.compile.classpath')
                    pathElement(location: projectMainClassesDir)
                }
                compileSources(projectCompileClassesDir, 'plugin.cli.compile.classpath') {
                    src(path: cliSourceDir)
                    javac(classpathref: 'plugin.cli.compile.classpath', encoding: griffonSettings.sourceEncoding, debug: 'yes')
                }
                ant.copy(todir: projectCompileClassesDir) {
                    fileset(dir: "${basedir}/src/cli") {
                        exclude(name: '**/*.java')
                        exclude(name: '**/*.groovy')
                        exclude(name: '**/.svn')
                    }
                }
            }
        }

        if (griffonSettings.isAddonPlugin()) {
            ant.path(id: 'addon.classpath') {
                path(refid: "griffon.compile.classpath")
                pathElement(location: projectMainClassesDir)
            }
            compileSrc('addon.classpath') {
                src(path: basedir)
                include(name: '*GriffonAddon.groovy')
                include(name: '*GriffonAddon.java')
                javac(classpathref: 'addon.classpath', encoding: griffonSettings.sourceEncoding, debug: 'yes')
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
            compileSources(projectTestClassesDir, 'projectTest.classpath') {
                src(path: projectTest)
                javac(classpathref: 'projectTest.classpath', encoding: griffonSettings.sourceEncoding, debug: 'yes')
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
