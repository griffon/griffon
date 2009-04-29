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

/**
 * Gant script that compiles Groovy and Java files in the src tree
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonInit")

ant.taskdef (name: 'groovyc', classname : 'org.codehaus.groovy.ant.Groovyc' /*classname : 'org.codehaus.groovy.griffon.compiler.GriffonCompiler'*/)
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
    // This stops resources.groovy becoming "spring.resources"
//    src(path: "${basedir}/griffon-app/conf/spring")

	excludedPaths.remove("conf")
    for(dir in pluginResources.file) {
        if(!excludedPaths.contains(dir.name) && dir.isDirectory()) {
            src(path:"${dir}")
        }
     }


//    src(path:"${basedir}/src/groovy")
//    src(path:"${basedir}/src/java")
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

target(compile : "Implementation of compilation phase") {
    ant.mkdir(dir:classesDirPath)
    event("CompileStart", ['source'])
    depends(resolveDependencies)

    profile("Compiling sources to location [$classesDirPath]") {

        String classpathId = "griffon.compile.classpath"
        compileSources(classpathId, compilerPaths.curry(classpathId, false))
//         try {
//             String classpathId = "griffon.compile.classpath"
//             ant.groovyc(destdir:classesDirPath,
// //                    projectName:baseName,
//                     classpathref:classpathId,
//                     encoding:"UTF-8",
//                     compilerPaths.curry(classpathId, false))
//         }
//         catch(Exception e) {
//             event("StatusFinal", ["Compilation error: ${e.message}"])
//             exit(1)
//         }

        // TODO review
        // compile *GriffonPlugin.groovy if it exists
//         try {
//            if( new File("${basedir}").list().grep{ it =~ /GriffonPlugin\.groovy/ } ){
//                String classpathId = "griffon.compile.classpath"
//                ant.groovyc(destdir:classesDirPath,
// //                    projectName:baseName,
//                        classpathref:classpathId,
//                        encoding:"UTF-8") {
//                   src(path:"${basedir}")
//                   include(name:'*GriffonPlugin.groovy')
//               }
//            }
//         }
//         catch(Exception e) {
//             event("StatusFinal", ["Compilation error: ${e.message}"])
//             exit(1)
//         }
        if( new File("${basedir}").list().grep{ it =~ /GriffonPlugin\.groovy/ } ){
            compileSources(classpathId) {
                src(path:"${basedir}")
                include(name:'*GriffonPlugin.groovy')
            }
        }

        classLoader.addURL(classesDir.toURI().toURL())

        event("CompileEnd", ['source'])
    }

}
