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

import org.codehaus.griffon.util.GriffonNameUtils

/**
 * Gant script that compiles Groovy and Java files in the src tree
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonInit")
includeTargets << griffonScript("_GriffonArgParsing")
includeTargets << griffonScript("_PluginDependencies")

ant.taskdef (name: 'groovyc', classname : 'org.codehaus.groovy.ant.Groovyc' /*classname : 'org.codehaus.griffon.compiler.GriffonCompiler'*/)
ant.path(id: "griffon.compile.classpath", compileClasspath)

compilerPaths = { String classpathId, boolean compilingTests ->

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
    if(compilingTests) {
        src(path:"${basedir}/test/unit")
        src(path:"${basedir}/test/integration")
    }
}

compileSources = { classpathId, sources ->
    try {
        ant.groovyc(destdir:classesDirPath,
                    classpathref:classpathId,
                    encoding:"UTF-8", sources)
    }
    catch(Exception e) {
        event("StatusFinal", ["Compilation error: ${e.message}"])
        exit(1)
    }
}

target(compile : "Compiles application sources") {
    ant.mkdir(dir:classesDirPath)
    event("CompileStart", ['source'])
    depends(parseArguments, resolveDependencies)

    profile("Compiling sources to location [$classesDirPath]") {

        String classpathId = "griffon.compile.classpath"

        if(isPluginProject) {
            def pluginFile = new File("${basedir}").list().find{ it =~ /GriffonPlugin\.groovy/ }
            compileSources(classpathId) {
                src(path:"${basedir}")
                include(name:'*GriffonPlugin.groovy')
            }
            classLoader.addURL(classesDir.toURI().toURL())
            resolvePluginClasspathDependencies(loadPluginClass(pluginFile))
        }

        compileSources(classpathId, compilerPaths.curry(classpathId, false))

        if( new File("${basedir}").list().grep{ it =~ /GriffonAddon\.groovy/ } ){
            ant.path(id:'addonPath') {
                compileClasspath.delegate = delegate
                compileClasspath.call()
                pathElement(location:classesDirPath)
            }
            compileSources('addonPath') {
                src(path:"${basedir}")
                include(name:'*GriffonAddon.groovy')
            }
        }

        classLoader.addURL(classesDir.toURI().toURL())

        event("CompileEnd", ['source'])
    }
}
