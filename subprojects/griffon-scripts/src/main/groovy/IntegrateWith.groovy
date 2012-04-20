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

import griffon.util.GriffonUtil

import static griffon.util.GriffonApplicationUtils.isWindows

/**
 * Command to enable integration of Griffon with external IDEs and build systems
 *
 * @author Graeme Rocher (Grails 1.2)
 * @author Sergey Nebolsin (Grails 1.2)
 */

integrationFiles = new File("${projectWorkDir}/integration-files")

String userHomeRegex = isWindows ? userHome.toString().replace('\\', '\\\\') : userHome.toString()
String griffonHomeRegex = isWindows ? griffonHome.toString().replace('\\', '\\\\') : griffonHome.toString()
String baseDirPath = isWindows ? griffonSettings.baseDir.path.replace('\\', '\\\\') : griffonSettings.baseDir.path

target(name: 'integrateWith', description: "Integrates ", prehook: null, posthook: null) {
    def keys = argsMap.keySet()
    try {
        event("IntegrateWithInit", keys.toList())
        for (key in keys) {
            if (key == 'params' || key == 'file-type') continue
            try {
                def name = GriffonUtil.getClassNameRepresentation(key)
                "integrate${name}"()
            } catch (e) {
                println "Error: failed to integrate [${key}] with Griffon: ${e.message}"
            }
        }
    } finally {
        ant.delete(dir: integrationFiles, failonerror: false)
    }
}

target(name: 'integrateGradle', description: "Integrates Gradle with Griffon", prehook: null, posthook: null) {
    depends unpackSupportFiles
    ant.copy(todir: basedir) {
        fileset(dir: "${integrationFiles}/gradle")
    }
    replaceTokens()
    println "Created Gradle build file."
}

target(name: 'integrateAnt', description: "Integrates Ant with Griffon", prehook: null, posthook: null) {
    depends unpackSupportFiles
    ant.copy(todir: basedir) {
        fileset(dir: "${integrationFiles}/ant")
    }
    replaceTokens()
    println "Created Ant build file."
}

target(name: 'integrateTextmate', description: "Integrates Textmate with Griffon", prehook: null, posthook: null) {
    depends unpackSupportFiles
    ant.copy(todir: basedir) {
        fileset(dir: "${integrationFiles}/textmate")
    }

    ant.move(file: "${basedir}/project.tmproj", tofile: "${basedir}/${griffonAppName}.tmproj", overwrite: true)

    replaceTokens()
    println "Created Textmate project files."
}

target(name: 'integrateEclipse', description: "Integrates Eclipse STS with Griffon", prehook: null, posthook: null) {
    depends unpackSupportFiles

    ant.copy(todir: basedir) {
        fileset(dir: "${integrationFiles}/eclipse")
    }
    ant.move(file: "${basedir}/.launch", tofile: "${basedir}/${griffonAppName}.launch", overwrite: true)

    replaceTokens()
    println "Created Eclipse project files."
}


target(name: 'integrateIdea', description: "Integrates Intellij with Griffon", prehook: null, posthook: null) {
    integrateIntellij()
}

target(name: 'integrateIntellij', description: "Integrates Intellij with Griffon", prehook: null, posthook: null) {
    depends unpackSupportFiles

    ant.copy(todir: basedir) {
        fileset(dir: "${integrationFiles}/intellij")
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

target(name: 'replaceTokens', description: "Replaces any tokens in the files", prehook: null, posthook: null) {
    def appKey = griffonAppName.replaceAll(/\s/, '.').toLowerCase()
    ant.replace(dir: basedir, includes: "*.*") {
        replacefilter(token: "@griffon.intellij.libs@", value: intellijGriffonJars())
        replacefilter(token: "@griffon.eclipse.libs@", value: eclipseGriffonJars())
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@groovy.version@", value: griffonSettings.groovyVersion)
        replacefilter(token: "@ant.version@", value: griffonSettings.antVersion)
        replacefilter(token: "@slf4j.version@", value: griffonSettings.slf4jVersion)
        replacefilter(token: "@spring.version@", value: griffonSettings.springVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.app.version@", value: griffonAppVersion ?: '0.1')
        def paths = pluginPaths()
        replacefilter(token: "@griffon.intellij.addons@", value: intellijAddonsFixes(paths.jars))
        replacefilter(token: "@griffon.intellij.javadoc@", value: intellijJavadocFixes(paths.javadoc))
        replacefilter(token: "@griffon.intellij.sources@", value: intellijSourcesFixes(paths.sources))
        replacefilter(token: "@java.sdk@", value: getPropertyValue('idea.java.sdk', '1.6'))
    }
    def ideaDir = new File("${basedir}/.idea")
    if (ideaDir.exists()) {
        ant.replace(dir: ideaDir) {
            replacefilter(token: "@griffon.intellij.libs@", value: intellijGriffonJars())
            replacefilter(token: "@griffon.version@", value: griffonVersion)
            replacefilter(token: "@griffon.project.name@", value: griffonAppName)
            replacefilter(token: "@java.sdk@", value: getPropertyValue('idea.java.sdk', '1.6'))
        }
    }
}

target(name: 'unpackSupportFiles', description: "Unpacks the support files", prehook: null, posthook: null) {
    if (!integrationFiles.exists()) {
        griffonUnpack(dest: integrationFiles.path, src: "griffon-integration-files.jar")
    }
}

setDefaultTarget("integrateWith")

// Generates Eclipse .classpath entries for the Griffon distribution
// JARs. This only works if $GRIFFON_HOME is set.
eclipseGriffonJars = {args ->
    def jars = []
    new File("${griffonHome}/dist").eachFileMatch(~/^griffon-.*\.jar/) {file ->
        jars << "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/dist/${file.name}\" />"
    }
    new File("${griffonHome}/lib").eachFileMatch(~/.*\.jar/) {file ->
        if (!file.name.startsWith("gant")) {
            jars << "<classpathentry kind=\"var\" path=\"GRIFFON_HOME/lib/${file.name}\" />"
        }
    }
    jars.join('\n')
}

griffonJars = {->
    def jars = []
    new File("${griffonHome}/dist").eachFileMatch(~/^griffon-.*\.jar/) {file ->
        jars << file
    }
    new File("${griffonHome}/lib").eachFileMatch(~/.*\.jar/) {file ->
        if (!file.name.startsWith("gant")) {
            jars << file
        }
    }
    jars
}

intellijGriffonJars = {->
    def builder = new StringBuilder()
    for (jarFile in griffonJars()) {
        jarFile = normalizeFilePath(jarFile)
        builder << "          <root url=\"jar://${jarFile}!/\" />\n"
    }
    return builder.toString()
}

intellijAddonsFixes = { List paths ->
    def builder = new StringBuilder()
    for (def plugin: paths) {
        builder << "          <root url=\"jar://${plugin}!/\" />\n"
    }
    return builder.toString()
}

intellijJavadocFixes = { List paths ->
    def builder = new StringBuilder()
    for (def plugin: paths) {
        builder << "          <root url=\"jar://${plugin}!/\" />\n"
    }
    return builder.toString()
}

intellijSourcesFixes = { List paths ->
    def builder = new StringBuilder()
    for (def plugin: paths) {
        builder << "          <root url=\"jar://${plugin}!/\" />\n"
    }
    return builder.toString()
}

normalizeFilePath = { file ->
    String path = file.absolutePath
    path = path.replaceFirst(~/$griffonHomeRegex/, '\\$GRIFFON_HOME\\$')
    path = path.replaceFirst(~/$userHomeRegex/, '\\$USER_HOME\\$')
    path.replaceFirst(~/${baseDirPath}(\\|\/)/, '')
}

pluginPaths = {
    def visitedDependencies = []
    def exclusions = griffonJars().name

    def plugins = [jars: [], javadoc: [], sources: []]
    def visitDependencies = {List dependencies ->
        dependencies.each { File f ->
            if ((f.name in exclusions) || visitedDependencies.contains(f)) return
            visitedDependencies << f
            String path = normalizeFilePath(f)
            plugins.jars << path
        }
    }

    visitDependencies(griffonSettings.runtimeDependencies)
    visitDependencies(griffonSettings.testDependencies)
    visitDependencies(griffonSettings.compileDependencies)
    visitDependencies(griffonSettings.buildDependencies)

    pluginSettings.doWithProjectPlugins {String name, String version, String path ->
        def pluginDir = new File(path, 'dist')
        def javadoc = new File(pluginDir, "griffon-$name-$version-javadoc.jar")
        if (javadoc.exists()) plugins.javadoc << normalizeFilePath(javadoc)
        def sources = new File(pluginDir, "griffon-$name-$version-sources.jar")
        if (sources.exists()) plugins.sources << normalizeFilePath(sources)
    }

    // TODO: Add support for linked plugins
    plugins
}
