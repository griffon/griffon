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

/**
 * Gant script that compiles Groovy and Java files in the src tree
 *
 * @author Graeme Rocher (Grails 0.4)
 */

includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("_GriffonArgParsing")
includeTargets << griffonScript("_PluginDependencies")

ant.taskdef (name: 'groovyc', classname : 'org.codehaus.groovy.ant.Groovyc' /*classname : 'org.codehaus.griffon.compiler.GriffonCompiler'*/)
ant.path(id: "griffon.compile.classpath", compileClasspath)

compilerPaths = { String classpathId ->
    def excludedPaths = ["resources", "i18n", "conf"] // conf gets special handling
    def pluginResources = getPluginSourceFiles()

    for(dir in new File("${basedir}/griffon-app").listFiles()) {
        if(!excludedPaths.contains(dir.name) && dir.isDirectory())
            src(path:"${dir}")
    }
    // Handle conf/ separately to exclude subdirs/package misunderstandings
    src(path: "${basedir}/griffon-app/conf")

    excludedPaths.remove("conf")
    for(dir in pluginResources.file) {
        if(!excludedPaths.contains(dir.name) && dir.isDirectory()) {
            src(path:"${dir}")
        }
    }

    src(path:"${basedir}/src/main")
    javac(classpathref:classpathId, debug:"yes", target: '1.5')
}

compileSources = { destinationDir, classpathId, sources ->
    if(argsMap.verboseCompile) {
        println('-'*80)
        println "[GRIFFON] compiling to ${destinationDir}"
        println "[GRIFFON] '${classpathId}' entries"
        ant.project.getReference(classpathId).list().each{println("  $it")}
        println('-'*80)
    }

    try {
        if(destinationDir instanceof String) destinationDir = new File(destinationDir)
        ant.groovyc(destdir: destinationDir,
                    classpathref: classpathId,
                    encoding:"UTF-8", sources)
        addUrlIfNotPresent classLoader, destinationDir
    }
    catch(Exception e) {
        event("StatusFinal", ["Compilation error: ${e.message}"])
        exit(1)
    }
}

target(compile : "Compiles application sources") {
    ant.mkdir(dir: classesDirPath)
    event("CompileStart", ['source'])
    depends(parseArguments, resolveDependencies)

    profile("Compiling sources to location [$classesDirPath]") {
        String classpathId = "griffon.compile.classpath"

        compileSrc = compileSources.curry(classesDirPath)

        if(isPluginProject) {
            def pluginFile = new File("${basedir}").list().find{ it =~ /GriffonPlugin\.groovy/ }
            compileSrc(classpathId) {
                src(path:"${basedir}")
                include(name:'*GriffonPlugin.groovy')
            }
            resolvePluginClasspathDependencies(loadPluginClass(pluginFile))
        }

        compileSrc(classpathId, compilerPaths.curry(classpathId))

        if(new File("${basedir}").list().grep{ it =~ /GriffonAddon\.groovy/ }){
            ant.path(id:'addon.classpath') {
                path(refid: "griffon.compile.classpath")
                pathElement(location: classesDirPath)
            }
            compileSrc('addon.classpath') {
                src(path:"${basedir}")
                include(name:'*GriffonAddon.groovy')
            }
        }
    }

    compileSharedTests()

    event("CompileEnd", ['source'])
}

target(compileSharedTests : "Compiles shared test sources") {
    for(pluginDir in getPluginDirectories().file) {
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
    def testShared = new File("${rootDir}/test/shared")
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
