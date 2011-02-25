/*
 * Copyright 2008-2011 the original author or authors.
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

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import griffon.util.GriffonUtil

/**
 * Gant script for creating Griffon artifacts of all sorts.
 *
 * @author Peter Ledbrook (Grails 1.1)
 */

import griffon.util.GriffonNameUtils

includeTargets << griffonScript("_GriffonPackage")
includeTargets << griffonScript("_GriffonArgParsing")

rootPackage = null
replaceNonag = false

createArtifact = { Map args = [:] ->
    resolveArchetype()
    resolveFileType()

    def suffix = args['suffix']
    def type = args['type']
    def artifactPath = args['path']
    if(args['fileType']) fileType = args['fileType']
    def lineTerminator = args["lineTerminator"] ?: (fileType != '.groovy'? ';' : '')
    
    def typeProperty = GriffonNameUtils.uncapitalize(type)
    type = argsMap[typeProperty] && templateExists(argsMap[typeProperty], fileType) ? argsMap[typeProperty] : type

    ant.mkdir(dir: "${basedir}/${artifactPath}")

    def name = purgeRedundantArtifactSuffix(args.name, suffix)
    // Extract the package name if one is given.
    def (artifactPkg, artifactName) = extractArtifactName(name)

    // Convert the package into a file path.
    def pkgPath = artifactPkg.replace('.' as char, '/' as char)

    // Make sure that the package path exists! Otherwise we won't
    // be able to create a file there.
    ant.mkdir(dir: "${basedir}/${artifactPath}/${pkgPath}")

    // Future use of 'pkgPath' requires a trailing slash.
    pkgPath += '/'

    // Convert the given name into class name and property name
    // representations.
    packageName = artifactPkg
    className = GriffonUtil.getClassNameRepresentation(artifactName)
    propertyName = GriffonUtil.getPropertyNameRepresentation(artifactName)
    artifactFile = "${basedir}/${artifactPath}/${pkgPath}${className}${suffix}${fileType}"
    defaultArtifactFile = "${basedir}/${artifactPath}/${pkgPath}${className}${suffix}.groovy"

    templateFile = resolveTemplate(type, fileType)
    if(!templateFile?.exists() && fileType != '.groovy') {
        templateFile = resolveTemplate(type, '.groovy')
        lineTerminator = ''
    }

    if(!templateFile?.exists()) {
        event('StatusFinal', ["Could not locate a suitable template for $artifactFile"])
        exit(1)
    }

    def similarFiles = resolveResources("file:${basedir}/${artifactPath}/${pkgPath}${className}${suffix}.*")
    if(similarFiles) {
        def fileSuffix = similarFiles[0].file.name.substring(similarFiles[0].file.name.lastIndexOf('.'))
        if(fileSuffix == fileType) {
            if(!replaceNonag && !confirmInput("${type} ${className}${suffix}${fileType} already exists. Overwrite? [y/n]","${artifactName}.${suffix}.overwrite")) {
                return
            }        
        } else {
            if(!replaceNonag && !confirmInput("${type} ${className}${suffix} already exists with type ${fileSuffix}. Rename? [y/n]","${artifactName}.${suffix}.rename")) {
                return
            }
            // WATCH OUT!! can cause problems with VCS systems
            ant.mkdir(dir: "${basedir}/renamed")
            ant.move(tofile: "${basedir}/renamed/${className}${suffix}${fileSuffix}", file: similarFiles[0].file)        
        }
    } else if(replaceNonag) {
        event('StatusFinal', ["${basedir}/${artifactPath}/${pkgPath}${className}${suffix} cannot be replaced because it does not exist."])
        exit(1)    
    }

    copyGriffonResource(artifactFile, templateFile)
    ant.replace(file: artifactFile) {
        replacefilter(token: "@artifact.name@", value: "${className}${suffix}" )
        replacefilter(token: "@artifact.name.plain@", value: className )
        replacefilter(token: "@artifact.suffix@", value: suffix )
        replacefilter(token: "@griffon.app.class.name@", value: appClassName )
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.project.key@", value: griffonAppName.replaceAll( /\s/, '.' ).toLowerCase())
    }
    if (artifactPkg) {
        ant.replace(file: artifactFile, token: "@artifact.package@", value: "package ${artifactPkg}${lineTerminator}\n\n")
    } else {
        ant.replace(file: artifactFile, token: "@artifact.package@", value: "")
    }

    if (args["superClass"]) {
        ant.replace(file: artifactFile, token: "@artifact.superclass@", value: args["superClass"])
    }

    event("CreatedFile", [artifactFile])
    event("CreatedArtefact", [type, className])
}

templateExists = { type, fileType ->
    def templateFile = resolveTemplate(type, fileType)
    if(!templateFile?.exists() && fileType != '.groovy') {
        templateFile = resolveTemplate(type, '.groovy')
    }
    templateFile.exists()
}

resolveTemplate = { type, fileSuffix ->
    // first check for presence of template in application
    def templateFile = new FileSystemResource("${basedir}/src/templates/artifacts/${type}${fileSuffix}")
    if (!templateFile.exists()) {
        // now check for template provided by plugins
        def pluginTemplateFiles = resolveResources("file:${pluginsHome}/*/src/templates/artifacts/${type}${fileSuffix}")
        if (pluginTemplateFiles) {
            templateFile = pluginTemplateFiles[0]
        }
        if (!templateFile.exists()) {
            // now check for template provided by an archetype
            templateFile = new FileSystemResource("${griffonWorkDir}/archetypes/${archetype}/templates/artifacts/${type}${fileSuffix}")
            if (!templateFile.exists()) {
                // now check for template provided by a provided archetype
                templateFile = griffonResource("archetypes/${archetype}/templates/artifacts/${type}${fileSuffix}")
                if (!templateFile.exists()) {
                    // template not found in archetypes, use default template
                    templateFile = new ClassPathResource("archetypes/default/templates/artifacts/${type}${fileSuffix}")
                }
            }
        }
    }
    templateFile
}

createRootPackage = {
    if(rootPackage == null) {
        rootPackage = (buildConfig.griffon.project.groupId ?: griffonAppName).replace('-','.').toLowerCase()    
    }
    return rootPackage
}

createIntegrationTest = { Map args = [:] ->
    def superClass = args["superClass"] ?: "GriffonUnitTestCase"
    createArtifact(name: args["name"], suffix: "${args['suffix']}Tests", type: "IntegrationTests", path: "test/integration", superClass: superClass)
}

createUnitTest = { Map args = [:] ->
    def superClass = args["superClass"] ?: "GriffonUnitTestCase"
    createArtifact(name: args["name"], suffix: "${args['suffix']}Tests", type: "Tests", path: "test/unit", superClass: superClass)
}

promptForName = { Map args = [:] ->
    if (!argsMap["params"]) {
        ant.input(addProperty: "artifact.name", message: "${args["type"]} name not specified. Please enter:")
        argsMap["params"] << ant.antProject.properties."artifact.name"
    }
}

purgeRedundantArtifactSuffix = { name, suffix ->
    if(!suffix) return name
    def newName = name
    if(name && name =~ /.+$suffix$/) {
        newName = name.replaceAll(/$suffix$/, '')
    }

    if(name == newName) {
        // def pos = newName.lastIndexOf('.')
        // if(pos > -1) newName = newName[pos+1..-1]
        for(int i = name.length() - 1; i >= 0; i--) {
            def str = name[i..-1]
            if(suffix.startsWith(str)/* && (newName - str)*/) {
                newName -= str
                break
            }
        }
    }

    return newName
}

extractArtifactName = { name ->
    def artifactName = name
    def artifactPkg = null
    def pos = artifactName.lastIndexOf('.')
    if (pos != -1) {
        artifactPkg = artifactName[0..<pos]
        artifactName = artifactName[(pos + 1)..-1]
        if(artifactPkg.startsWith("~")) {
            artifactPkg = artifactPkg.replace("~", createRootPackage())
        }
    } else {
        artifactPkg = argsMap.skipPackagePrompt ? '' : createRootPackage()
    }

    return [artifactPkg, artifactName]
}

target(resolveArchetype: '') {
    archetype = buildConfig.app.archetype
    if(!archetype && archetype instanceof ConfigObject) archetype = null
    archetype = archetype ?: argsMap.archetype
    archetype = archetype ?: 'default'
}

target(resolveFileType: '') {
    def cfileType = buildConfig.app.fileType
    if(!cfileType && cfileType instanceof ConfigObject) cfileType = null
    fileType = argsMap.fileType ?: (cfileType ?: 'groovy')
    if(!fileType.startsWith('.')) fileType = '.'+fileType
}

loadArchetypeFor = { type = 'application' ->
    resolveArchetype()

    def gcl = new GroovyClassLoader(classLoader)
    def archetypeFile = new FileSystemResource("${griffonWorkDir}/archetypes/${archetype}/${type}.groovy")
    if(!archetypeFile.exists()) {
        archetypeFile = griffonResource("archetypes/${archetype}/${type}.groovy")
    }

    if(archetypeFile.exists()) {
        try {
            includeTargets << gcl.parseClass(archetypeFile.file) 
        } catch(Exception e) {
            logError("An error ocurred while parsing archetype ${archetype}. Using 'default' archetype instead.", e)
            archetype = 'default'
            archetypeFile = new ClassPathResource("archetypes/default/${type}.groovy")
            includeTargets << gcl.parseClass(archetypeFile.getURL().text) 
        }
    } else {
        archetype = 'default'
        archetypeFile = new ClassPathResource("archetypes/default/${type}.groovy")
        includeTargets << gcl.parseClass(archetypeFile.getURL().text) 
    }
}
