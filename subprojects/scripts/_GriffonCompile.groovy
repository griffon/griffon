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

import org.codehaus.groovy.runtime.StackTraceUtils

/**
 * Gant script that compiles Groovy and Java files in the src tree
 *
 * @author Graeme Rocher (Grails 0.4)
 */
includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("_GriffonArgParsing")
includeTargets << griffonScript("_PluginDependencies")

ant.taskdef (name: 'groovyc', classname : 'org.codehaus.groovy.ant.Groovyc')
ant.taskdef (name: 'griffonc', classname : 'org.codehaus.griffon.compiler.GriffonCompiler')
ant.path(id: "griffon.compile.classpath", compileClasspath)

compilerPaths = { String classpathId ->
    def excludedPaths = ["resources", "i18n", "conf"] // conf gets special handling

    for(dir in new File("${basedir}/griffon-app").listFiles()) {
        if(!excludedPaths.contains(dir.name) && dir.isDirectory())
            src(path: "$dir")
    }
    // Handle conf/ separately to exclude subdirs/package misunderstandings
    // src(path: "${basedir}/griffon-app/conf")

    def srcMain = new File("${griffonSettings.sourceDir}/main")
    if(srcMain.exists()) src(path: srcMain)
    javac(classpathref:classpathId, encoding:"UTF-8", debug:"yes")
}

target(setCompilerSettings: "Updates the compile build settings based on args") {
    depends(parseArguments)
    if (argsMap.containsKey('verboseCompile')) {
        griffonSettings.verboseCompile = argsMap.verboseCompile as boolean
    }
}

compileSources = { destinationDir, classpathId, sources ->
    if(argsMap.compileTrace) {
        println('-'*80)
        println "[GRIFFON] compiling to ${destinationDir}"
        println "[GRIFFON] '${classpathId}' entries"
        ant.project.getReference(classpathId).list().each{println("  $it")}
        println('-'*80)
    }

    try {
        if(destinationDir instanceof String) destinationDir = new File(destinationDir)
        ant.griffonc(destdir: destinationDir,
                    projectName: griffonAppName,
                    basedir: griffonSettings.baseDir.path,
                    verbose: (argsMap.verboseCompiler? true : false),
                    classpathref: classpathId,
                    encoding:"UTF-8", sources)
        addUrlIfNotPresent classLoader, destinationDir
    }
    catch(Exception e) {
        if(argsMap.verboseCompile) {
            StackTraceUtils.deepSanitize(e)
            e.printStackTrace(System.err)
        }
        event("StatusFinal", ["Compilation error: ${e.message}"])
        exit(1)
    }
}

target(compile: "Implementation of compilation phase") {
    depends(compilePlugins)

    def classesDirPath = new File(griffonSettings.classesDir.path)
    ant.mkdir(dir:classesDirPath)

    profile("Compiling sources to location [$classesDirPath]") {
        String classpathId = "griffon.compile.classpath"

        compileSrc = compileSources.curry(classesDirPath)

        compileSrc(classpathId, compilerPaths.curry(classpathId))
        compileSrc(classpathId) {
            src(path: "${basedir}/griffon-app/conf")
            include(name: '*.groovy')
        }
        ant.copy(todir: classesDirPath) {
            fileset(dir: "${basedir}/griffon-app/conf") {
                include(name: '*.properties')
                include(name: '*.xml')
            }
        }
        addUrlIfNotPresent classLoader, griffonSettings.pluginClassesDir

        // If this is a plugin project, the descriptor is not included
        // in the compiler's source path. So, we manually compile it
        // now.
        if (isPluginProject) {
            def pluginFile = new File("${basedir}").list().find{ it =~ /GriffonPlugin\.groovy/ }
            compileSrc(classpathId) {
                src(path: "$basedir")
                include(name:'*GriffonPlugin.groovy')
            }
            resolvePluginClasspathDependencies(loadPluginClass(pluginFile))

            if(cliSourceDir.exists()) {
                ant.mkdir(dir: cliClassesDir)
                ant.path(id:'plugin.cli.compile.classpath') {
                    path(refid: 'griffon.compile.classpath')
                    pathElement(location: classesDirPath)
                }
                compileSources(cliClassesDir, 'plugin.cli.compile.classpath') {
                    src(path: cliSourceDir)
                    javac(classpathref: 'plugin.cli.compile.classpath', debug: 'yes')
                }
                ant.copy(todir: cliClassesDir) {
                    fileset(dir: "${basedir}/src/cli") {
                        exclude(name: '**/*.java')
                        exclude(name: '**/*.groovy')
                        exclude(name: '**/.svn')
                    }
                }
            }
        }

        if(new File("${basedir}").list().grep{ it =~ /GriffonAddon\.groovy/ }){
            ant.path(id:'addon.classpath') {
                path(refid: "griffon.compile.classpath")
                pathElement(location: classesDirPath)
            }
            compileSrc('addon.classpath') {
                src(path: "$basedir")
                include(name:'*GriffonAddon.groovy')
            }
        }
    }

    compileSharedTests()
}

target(compilePlugins: "Compiles source files of all referenced plugins.") {
    depends(setCompilerSettings, resolveDependencies)

    def classesDirPath = pluginClassesDirPath
    ant.mkdir(dir: classesDirPath)

    profile("Compiling sources to location [$classesDirPath]") {
        // First compile the plugins so that we can exclude any
        // classes that might conflict with the project's.
        def classpathId = "griffon.compile.classpath"
        def pluginResources = pluginSettings.pluginSourceFiles
        def excludedPaths = ["i18n"] // conf gets special handling
        pluginResources = pluginResources.findAll {
            !excludedPaths.contains(it.file.name) && it.file.isDirectory()
        }

        if (pluginResources) {
            // Only perform the compilation if there are some plugins
            // installed or otherwise referenced.
            compileSources(classesDirPath, classpathId) {
                for(dir in pluginResources.file) {
                    src(path: "$dir")
                }
                exclude(name: "**/BuildConfig.groovy")
                exclude(name: "**/Config.groovy")
                javac(classpathref:classpathId, encoding:"UTF-8", debug:"yes")
            }
            for(dir in pluginResources.file) {
                compileSharedTestSrc(dir)
            }
        }
    }
}

target(compileSharedTests : "Compiles shared test sources") {
    for(pluginDir in pluginSettings.pluginDirectories.file) {
        def pluginDistDir = new File(pluginDir, 'dist')
        if(pluginDistDir.exists()) {
            ant.fileset(dir: pluginDistDir, includes: '*-test.jar').each { jar ->
                addUrlIfNotPresent classLoader, jar.file
            }
        }
    }
    compileSharedTestSrc(basedir)
    def metainfDir = new File("${basedir}/griffon-app/conf/metainf")
    boolean hasMetainf = metainfDir.exists() ? ant.fileset(dir: metainfDir, excludes: '**/*.svn/**, **/CVS/**').size() > 0 : false
    if(hasMetainf) {
        def metaResourcesDir = new File("${testResourcesDirPath}/META-INF")
        ant.copy(todir: metaResourcesDir) {
            fileset(dir: metainfDir)
        }
        addUrlIfNotPresent classLoader, testResourcesDirPath
    }
}

compileSharedTestSrc = { rootDir ->
    def testShared = new File("${rootDir}/src/test")
    boolean hasTestShared = testShared.exists() ? ant.fileset(dir: testShared, includes: '**/*.groovy, **/*.java').size() > 0 : false

    if(hasTestShared) {
        def testSharedDir = new File(griffonSettings.testClassesDir, 'shared')
        ant.mkdir(dir: testSharedDir)
        profile("Compiling shared test sources to location [$testSharedDir]") {
            ant.path(id:'testShared.classpath') {
                path(refid: "griffon.compile.classpath")
                pathElement(location: classesDir)
                pathElement(location: testSharedDir)
            }
            compileSources(testSharedDir, 'testShared.classpath') {
                src(path: testShared)
                javac(classpathref: 'testShared.classpath', debug:"yes", target: '1.5')
            }
        }
    } 
    def testResources = new File("${basedir}/test/resources")
    boolean hasTestResources = testResources.exists() ? ant.fileset(dir: testResources, excludes: '**/*.svn/**, **/CVS/**').size() > 0 : false
    if(hasTestResources) {
        ant.mkdir(dir: testResourcesDirPath)
        ant.copy(todir: testResourcesDirPath) {
            fileset(dir: testResources)
        }
        addUrlIfNotPresent classLoader, testResourcesDirPath
    }
}
