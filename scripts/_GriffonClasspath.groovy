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
import org.codehaus.groovy.control.CompilerConfiguration
import org.springframework.core.io.Resource
import org.springframework.core.io.FileSystemResource
//import org.codehaus.griffon.plugins.GriffonPluginUtils

/**
 * Gant script containing the Griffon classpath setup.
 *
 * @author Peter Ledbrook
 *
 * @since 1.1
 */

includeTargets << griffonScript("_Settings")

classpathSet = false

target(classpath: "Sets the Griffon classpath") {
    setClasspath()
}

/**
 * Obtains all of the plug-in Lib directories
 */
getPluginLibDirs = {
    return new Resource[0]
//    GriffonPluginUtils.getPluginLibDirectories(pluginsDirPath, resolveResources)
}

/**
 * Obtains an array of all plug-in JAR files as Spring Resource objects
 */
//getPluginJarFiles = {
//    GriffonPluginUtils.getPluginJarFiles(pluginsDirPath, resolveResources)
//}

getJarFiles = {->
    def jarFiles = resolveResources("file:${basedir}/lib/*.jar").toList()
    //def pluginJars = getPluginJarFiles()

//    for (pluginJar in pluginJars) {
//        boolean matches = jarFiles.any {it.file.name == pluginJar.file.name}
//        if (!matches) jarFiles.add(pluginJar)
//    }

    def userJars = resolveResources("file:${userHome}/.griffon/lib/*.jar")
    for (userJar in userJars) {
        jarFiles.add(userJar)
    }

	jarFiles.addAll(getExtraDependencies())

    jarFiles
}

getExtraDependencies = {
	def jarFiles =[]
	if(preInitConfig?.griffon?.compiler?.dependencies) {
        def extraDeps = Ant.fileScanner(preInitConfig.griffon.compiler.dependencies)
		for(jar in extraDeps) {
            jarFiles << new FileSystemResource(jar)
		}
	}
	jarFiles
}

populateRootLoader = {rootLoader, jarFiles ->
	for(jar in getExtraDependencies()) {
    	rootLoader?.addURL(jar.URL)
	}
    rootLoader?.addURL(new File("${basedir}/web-app/WEB-INF/classes").toURI().toURL())
    rootLoader?.addURL(new File("${basedir}/web-app/WEB-INF").toURI().toURL())
}

griffonClasspath = {pluginLibs, griffonDir ->
    pathelement(location: "${classesDir.absolutePath}")
    pathelement(location: "${basedir}")
    pathelement(location: "${basedir}/test/unit")
    pathelement(location: "${basedir}/test/integration")
    pathelement(location: "${basedir}/web-app")
    for (pluginLib in pluginLibs) {
        fileset(dir: pluginLib.file.absolutePath)
    }
    fileset(dir: "${griffonHome}/lib")
    fileset(dir: "${griffonHome}/dist")
    if (new File("${basedir}/lib").exists()) {
        fileset(dir: "${basedir}/lib")
    }
    for (d in griffonDir) {
        pathelement(location: "${d.file.absolutePath}")
    }

	if(preInitConfig.griffon.compiler.dependencies) {
		def callable = preInitConfig.griffon.compiler.dependencies
		callable.delegate = delegate
		callable.resolveStrategy = Closure.DELEGATE_FIRST
		callable()
	}
}

void setClasspath() {
    // Make sure the following code is only executed once.
    if (classpathSet) return

    def griffonDir = resolveResources("file:${basedir}/griffon-app/*")

    Ant.path(id: "griffon.classpath", griffonClasspath.curry(getPluginLibDirs(), griffonDir))
    StringBuffer cpath = new StringBuffer("")

    def jarFiles = getJarFiles()


    for (dir in griffonDir) {
        cpath << dir.file.absolutePath << File.pathSeparator
        // Adding the griffon-app folders to the root loader causes re-load issues as
        // root loader returns old class before the griffon GCL attempts to recompile it
        //rootLoader?.addURL(dir.URL)
    }
    cpath << classesDirPath << File.pathSeparator
    cpath << "${basedir}/web-app/WEB-INF"
    for (jar in jarFiles) {
        cpath << jar.file.absolutePath << File.pathSeparator
    }


    compConfig = new CompilerConfiguration()
    compConfig.setClasspath(cpath.toString());
    compConfig.sourceEncoding = "UTF-8"
    rootLoader = getClass().classLoader.rootLoader
    populateRootLoader(rootLoader, jarFiles)

    rootLoader?.addURL(new File("${basedir}/griffon-app/conf/hibernate").toURI().toURL())
    rootLoader?.addURL(new File("${basedir}/src/java").toURI().toURL())

    // The resources directory must be created before it is added to
    // the root loader, otherwise it is quietly ignored. In other words,
    // if the directory is created after its path has been added to the
    // root loader, it will not be included in the classpath.
    def resourcesDir = new File(resourcesDirPath)
    if (!resourcesDir.exists()) {
        resourcesDir.mkdirs()
    }
    rootLoader?.addURL(resourcesDir.toURI().toURL())

    parentLoader = getClass().getClassLoader()
    classpathSet = true
}
