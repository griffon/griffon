/*
 * Copyright 2010-2011 the original author or authors.
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
 * @author Andres Almiray
 */

includeTargets << griffonScript('Init')

ERROR_MESSAGE = """
You need to specify either the direct URL of the archetype or the zip file
For example:
'griffon install-archetype custom-griffon-archetype.zip'
or
'griffon install-archetype http://svn.codehaus.org/griffon/archetypes/custom-griffon-archetype.zip"""

target(installArchetype: "Installs a Griffon application archetype") {
    depends(parseArguments, configureProxy)
    try {
        def archetypeArgs = argsMap['params']

        if(archetypeArgs) {
            def archetypesHome = "${griffonWorkDir}/archetypes"

            def archetypeFile = new File(archetypeArgs[0])
            def urlPattern = ~"^[a-zA-Z][a-zA-Z0-9\\-\\.\\+]*://"
            def archetypeName = null
            if(archetypeArgs[0] =~ urlPattern) {
                def url = new URL(archetypeArgs[0])
                archetypeName = doInstallArchetypeFromURL(url, archetypesHome)
            } else if(archetypeFile.exists() && archetypeFile.name.endsWith("-griffon-archetype.zip" )) {
                archetypeName = doInstallArchetypeZip(archetypeFile, archetypesHome)
            } else {
                event("StatusError", [ERROR_MESSAGE])
                exit(1)
            }
            event("StatusFinal", ["Installed archetype '${archetypeName}' in ${archetypesHome}"])
        } else {
            event("StatusError", [ERROR_MESSAGE])
            exit(1)
        }
    } catch(Exception e) {
        logError("Error installing archetype: ${e.message}", e)
        exit(1)
    }
}
setDefaultTarget(installArchetype)

doInstallArchetypeFromURL = { url, archetypesHome ->
    def slash = url.file.lastIndexOf('/')
    def fullArchetypeName = "${url.file[slash + 1..-23]}"
    if(fullArchetypeName == 'default') {
        event("StatusError", ["Archetype 'default' cannot be overwritten"])
        exit(1)
    }

    def destination = "${archetypesHome}/${fullArchetypeName}"

    if (new File(destination).exists()) {
        if(!confirmInput("Archetype '${fullArchetypeName}' is already installed. Overwrite? [y/n]","${fullArchetypeName}.overwrite")) {
            return
        }
    }
    ant.delete(dir: destination, quiet: true, failOnError: false)

    def downloadDir = "${griffonTmp}/archetype"
    ant.mkdir(dir: downloadDir)
    try {
        ant.get(dest: "${downloadDir}/${fullArchetypeName}.zip",
                src: "${url}",
                verbose: true,
                usetimestamp: true)
        ant.unzip(src: "${downloadDir}/${fullArchetypeName}.zip",
                  dest: destination)
    } finally {
        ant.delete(dir: downloadDir, quiet: true, failOnError: false)
    }

    return fullArchetypeName
}

doInstallArchetypeZip = { zipFile, archetypesHome ->
    def fullArchetypeName = "${zipFile.name[0..-23]}"
    if(fullArchetypeName == 'default') {
        event("StatusError", ["Archetype 'default' cannot be overwritten"])
        exit(1)
    }

    def destination = "${archetypesHome}/${fullArchetypeName}"

    if (new File(destination).exists()) {
        if(!confirmInput("Archetype '${fullArchetypeName}' is already insatlled. Overwrite? [y/n]","${fullArchetypeName}.overwrite")) {
            return
        }
    }
    ant.delete(dir: destination, quiet: true, failOnError: false)
    ant.unzip(src: zipFile, dest: destination)

    return fullArchetypeName
}
