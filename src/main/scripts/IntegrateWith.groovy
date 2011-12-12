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

import griffon.util.GriffonUtil
import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.resolve.IvyNode
import org.codehaus.griffon.resolve.IvyDependencyManager

/**
 * Command to enable integration of Griffon with external IDEs and build systems
 *
 * @author Graeme Rocher (Grails 1.2)
 * @author Sergey Nebolsin (Grails 1.2)
 */
includeTargets << griffonScript("_GriffonInit")

integrationFiles = new File("${projectWorkDir}/integration-files")

target(integrateWith:"Integrates ") {
    depends(parseArguments)

    def keys = argsMap.keySet()
    try {
        event("IntegrateWithInit", keys.toList())
        for(key in keys) {
            if(key == 'params') continue
            try {
                def name = GriffonUtil.getClassNameRepresentation(key)
                "integrate${name}"()
            }
            catch (e) {
                println "Error: failed to integrate [${key}] with Griffon: ${e.message}"
                exit 1
            }
        }
    } finally {
        ant.delete(dir:integrationFiles, failonerror:false)
    }
}

target(integrateAnt:"Integrates Ant with Griffon") {
   depends unpackSupportFiles
   ant.copy(todir:basedir) {
       fileset(dir:"${integrationFiles}/ant")
   }
   replaceTokens()
   println "Created Ant build file."
}

target(integrateTextmate:"Integrates Textmate with Griffon") {
   depends unpackSupportFiles
   ant.copy(todir:basedir) {
       fileset(dir:"${integrationFiles}/textmate")
   }

   ant.move(file: "${basedir}/project.tmproj", tofile: "${basedir}/${griffonAppName}.tmproj", overwrite: true)

   replaceTokens()
   println "Created Textmate project files."
}

target(integrateEclipse:"Integrates Eclipse STS with Griffon") {
    depends unpackSupportFiles

    ant.copy(todir:basedir) {
        fileset(dir:"${integrationFiles}/eclipse")
    }
    ant.move(file: "${basedir}/.launch", tofile: "${basedir}/${griffonAppName}.launch", overwrite: true)

    replaceTokens()
    println "Created Eclipse project files."
}

target(integrateIntellij:"Integrates Intellij with Griffon") {
    depends unpackSupportFiles

    ant.copy(todir:basedir) {
        fileset(dir:"${integrationFiles}/intellij")
    }
    def griffonIdeaVersion = griffonVersion.replace('-' as char, '_' as char)
                                           .replace('.' as char, '_' as char)
    ant.move(file: "${basedir}/ideaGriffonProject.iml", tofile: "${basedir}/${griffonAppName}.iml", overwrite: true)
    ant.move(file: "${basedir}/ideaGriffonProjectFixes.iml", tofile: "${basedir}/${griffonAppName}-griffonPluginFixes.iml", overwrite: true)
    ant.move(file: "${basedir}/.idea/libraries/griffon.xml",
           tofile: "${basedir}/.idea/libraries/griffon_${griffonIdeaVersion}.xml", overwrite: true)

    replaceTokens()
    println "Created IntelliJ project files."
}

target(replaceTokens:"Replaces any tokens in the files") {
    def appKey = griffonAppName.replaceAll( /\s/, '.' ).toLowerCase()
    ant.replace(dir: basedir, includes:"*.*") {
        replacefilter(token: "@griffon.intellij.libs@", value: intellijClasspathLibs())
        // replacefilter(token: "@griffon.libs@", value: eclipseClasspathLibs())
        // replacefilter(token: "@griffon.jar@", value: eclipseClasspathGriffonJars())
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@groovy.version@", value: griffonSettings.groovyVersion)
        replacefilter(token: "@ant.version@", value: griffonSettings.antVersion)
        replacefilter(token: "@slf4j.version@", value: griffonSettings.slf4jVersion)
        replacefilter(token: "@spring.version@", value: griffonSettings.springVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        def paths = pluginPaths()
        replacefilter(token: "@griffon.intellij.addons@", value: intellijAddonsFixes(paths))
        replacefilter(token: "@griffon.intellij.javadoc@", value: intellijJavadocFixes(paths))
        replacefilter(token: "@griffon.intellij.sources@", value: intellijSourcesFixes(paths))
        replacefilter(token: "@griffon.intellij.addon.jars@", value: intellijAddonJarsFixes(paths))
        replacefilter(token: "@griffon.intellij.dependencies@", value: intellijDependenciesFixes(dependencyPaths()))
    }
    def ideaDir = new File("${basedir}/.idea")
    if(ideaDir.exists()) {
        ant.replace(dir: ideaDir) {
            replacefilter(token: "@griffon.intellij.libs@", value: intellijClasspathLibs())
            replacefilter(token: "@griffon.version@", value: griffonVersion)
            replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        }
    }
}

target(unpackSupportFiles:"Unpacks the support files") {
    if(!integrationFiles.exists()) {
        griffonUnpack(dest: integrationFiles.path, src: "griffon-integration-files.jar")
    }
}

setDefaultTarget("integrateWith")

griffonLibs = {
    def libs = [] as Set
    if (griffonHome) {
        (new File("${griffonHome}/dist")).eachFileMatch(~/^griffon-.*\.jar/) {file -> libs << file.name }
        (new File("${griffonHome}/lib")).eachFileMatch(~/.*\.jar/) {file ->if (!file.name.startsWith("gant-")) libs << file.name }
    }
    return libs
}

intellijClasspathLibs = {
    def builder = new StringBuilder()
    if (griffonHome) {
        (new File("${griffonHome}/dist")).eachFileMatch(~/^griffon-.*\.jar/) {file ->
            builder << "      <root url=\"jar://${griffonHome}/dist/${file.name}!/\" />\n"
        }
        (new File("${griffonHome}/lib")).eachFileMatch(~/.*\.jar/) {file ->
            if (!file.name.startsWith("gant-")) {
                builder << "      <root url=\"jar://${griffonHome}/lib/${file.name}!/\" />\n"
            }
        }
    }

    return builder.toString()
}

// Generates Eclipse .classpath entries for the Griffon distribution
// JARs. This only works if $GRIFFON_HOME is set.
eclipseClasspathGriffonJars = {args ->
    result = ''
    if (griffonHome) {
        (new File("${griffonHome}/dist")).eachFileMatch(~/^griffon-.*\.jar/) {file ->
            result += "    <classpathentry kind=\"var\" path=\"GRIFFON_HOME/dist/${file.name}\" />\n"
        }
    }
    result
}

intellijAddonsFixes = { List paths ->
    def builder = new StringBuilder()
    File userHome = new File(System.properties.get('user.home'))
    for(def plugin: paths) {
        if(new File(userHome, plugin.addon).exists())
            builder << "          <root url=\"file://\$USER_HOME\$/${plugin.addon}\" />\n"
    }
    return builder.toString()
}

intellijDependenciesFixes = { List paths ->
    def builder = new StringBuilder()
    File userHome = new File(System.properties.get('user.home'))
    for(def plugin: paths) {
        if(new File(userHome, plugin).exists())
            builder << "          <root url=\"jar://\$USER_HOME\$/${plugin}!/\" />\n"
    }
    return builder.toString()
}

intellijJavadocFixes = { List paths ->
    def builder = new StringBuilder()
    File userHome = new File(System.properties.get('user.home'))
    for(def plugin: paths) {
        if(new File(userHome, plugin.javadoc).exists())
            builder << "          <root url=\"jar://\$USER_HOME\$/${plugin.javadoc}!/\" />\n"
    }
    return builder.toString()
}

intellijSourcesFixes = { List paths ->
    def builder = new StringBuilder()
    File userHome = new File(System.properties.get('user.home'))
    for(def plugin: paths) {
        if(new File(userHome, plugin.sources).exists())
            builder << "          <root url=\"jar://\$USER_HOME\$/${plugin.sources}!/\" />\n"
    }
    return builder.toString()
}

intellijAddonJarsFixes = { List paths ->
    def builder = new StringBuilder()
    File userHome = new File(System.properties.get('user.home'))
    for(def plugin: paths) {
        if(new File(userHome, plugin.addon).exists())
            builder << "        <jarDirectory url=\"file://\$USER_HOME\$/${plugin.addon}\" recursive=\"false\" />\n"
    }
    return builder.toString()
}

pluginPaths = {
    IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
    def pDeps = dependencyManager.resolvePluginDependencies()
    def localPlugins = [] as Set
    if (dependencyManager.resolveErrors) {
	pDeps.allProblemMessages.findAll{it.startsWith('unresolved dependency: ')}.each{def m = it =~ /.*#(.*);(.*):/ ; localPlugins << [artifact: m[0][1], revision: m[0][2]]}
        println "Warning: There was an error resolving plugin JAR dependencies. This is acceptable if all of this plugins are installed local only: ${localPlugins}"
    }
    def plugins = [ ]
    if (pDeps) {
        for (IvyNode dep: pDeps.dependencies) {
            try {
                for (Artifact artifact: dep.allArtifacts) {
                    def attr = artifact.attributes
                    plugins << [
                        addon: ".griffon/${griffonVersion}/projects/${griffonAppName}/plugins/${attr.artifact}-${attr.revision}/addon",
                        javadoc : ".griffon/${griffonVersion}/projects/${griffonAppName}/plugins/${attr.artifact}-${attr.revision}/dist/griffon-${attr.artifact}-${attr.revision}-javadoc.jar",
                        sources : ".griffon/${griffonVersion}/projects/${griffonAppName}/plugins/${attr.artifact}-${attr.revision}/dist/griffon-${attr.artifact}-${attr.revision}-sources.jar",
                    ]
                }
            } catch (e) {}
        }
    }
    for(def attr: localPlugins) {
        plugins << [
            addon: ".griffon/${griffonVersion}/projects/${griffonAppName}/plugins/${attr.artifact}-${attr.revision}/addon",
            javadoc : ".griffon/${griffonVersion}/projects/${griffonAppName}/plugins/${attr.artifact}-${attr.revision}/dist/griffon-${attr.artifact}-${attr.revision}-javadoc.jar",
            sources : ".griffon/${griffonVersion}/projects/${griffonAppName}/plugins/${attr.artifact}-${attr.revision}/dist/griffon-${attr.artifact}-${attr.revision}-sources.jar",
        ]
    }
    plugins
}

dependencyPaths = {
    IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
    def deps = dependencyManager.resolveDependencies()
    if (dependencyManager.resolveErrors) {
        println "Error: There was an error resolving plugin JAR dependencies"
        exit 1
    }
    def locations = []
    Set libs = griffonLibs()
    if (deps) {
        for (IvyNode dep: deps.dependencies) {
            try {
                for (Artifact artifact: dep.allArtifacts) {
                    def attr = artifact.attributes
                    if(attr.organisation != 'org.codehaus.griffon.plugins') {
                        def name = "${attr.artifact}-${attr.revision}.${attr.ext}".toString()
                        if(! libs.contains(name))
                            locations << ".ivy2/cache/${attr.organisation}/${attr.module}/${attr.type}s/${name}"
                    }
                }
            } catch (e) {}
        }
    }
    locations
}
