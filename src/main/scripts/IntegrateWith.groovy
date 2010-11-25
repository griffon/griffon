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

import griffon.util.GriffonUtil

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
       fileset(dir:integrationFiles, includes:"*.xml")
   }
   replaceTokens()
   println "Created Ant and Ivy builds files."
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
    ant.move(file: "${basedir}/ideaGriffonProject.iml", tofile: "${basedir}/${griffonAppName}.iml", overwrite: true)
    ant.move(file: "${basedir}/ideaGriffonProject.ipr", tofile: "${basedir}/${griffonAppName}.ipr", overwrite: true)
    ant.move(file: "${basedir}/ideaGriffonProject.iws", tofile: "${basedir}/${griffonAppName}.iws", overwrite: true)

    replaceTokens()
    println "Created IntelliJ project files."
}

target(replaceTokens:"Replaces any tokens in the files") {
    def appKey = griffonAppName.replaceAll( /\s/, '.' ).toLowerCase()
    ant.replace(dir:"${basedir}", includes:"*.*") {
        replacefilter(token: "@griffon.intellij.libs@", value: intellijClasspathLibs())
        replacefilter(token: "@griffon.libs@", value: eclipseClasspathLibs())
        replacefilter(token: "@griffon.jar@", value: eclipseClasspathGriffonJars())
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
    }
}

target(unpackSupportFiles:"Unpacks the support files") {
    if(!integrationFiles.exists()) {
        griffonUnpack(dest: integrationFiles.path, src: "griffon-integration-files.jar")
    }
}

setDefaultTarget("integrateWith")

intellijClasspathLibs = {
    def builder = new StringBuilder()
    if (griffonHome) {
        (new File("${griffonHome}/lib")).eachFileMatch(~/.*\.jar/) {file ->
            if (!file.name.startsWith("gant-")) {
                builder << "<root url=\"jar://${griffonHome}/lib/${file.name}!/\" />\n\n"
            }
        }
        (new File("${griffonHome}/dist")).eachFileMatch(~/^griffon-.*\.jar/) {file ->
            builder << "<root url=\"jar://${griffonHome}/dist/${file.name}!/\" />\n\n"
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
