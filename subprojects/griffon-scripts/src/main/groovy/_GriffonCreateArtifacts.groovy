/*
 * Copyright 2008-2012 the original author or authors.
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
 * Gant script for creating Griffon artifacts of all sorts.
 *
 * @author Peter Ledbrook (Grails 1.1)
 */

import griffon.util.GriffonNameUtils
import griffon.util.GriffonUtil
import griffon.util.Metadata
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Plugin
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource

import java.util.regex.Matcher
import java.util.regex.Pattern

includeTargets << griffonScript('_GriffonPackage')

defaultPackageName = ''
replaceNonag = false
allowDuplicate = false
artifactNameVersionPattern = Pattern.compile("^([\\w][\\w\\.-]*)-([0-9][\\w\\.-]*)\$")

createArtifact = { Map args = [:] ->
    resolveArchetype()
    resolveFileType()

    def suffix = args['suffix']
    def type = args['type']
    def template = args['template'] ?: type
    def artifactPath = args['path']
    if (args['fileType']) fileType = args['fileType']
    if (!fileType.startsWith('.')) fileType = '.' + fileType
    def lineTerminator = args['lineTerminator'] ?: (fileType == '.groovy' ? '' : ';')

    def typeProperty = GriffonNameUtils.uncapitalize(type)
    template = argsMap[typeProperty] && templateExists(argsMap[typeProperty], fileType) ? argsMap[typeProperty] : template

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

    fullyQualifiedClassName = "${packageName ? packageName + '.' : ''}$className${suffix}"

    def originalFileType = fileType
    templateFile = resolveTemplate(template, fileType)
    if (!templateFile?.exists() && fileType != '.groovy') {
        templateFile = resolveTemplate(template, '.groovy')
        fileType = '.groovy'
        lineTerminator = ''
    }

    artifactFile = new File("${basedir}/${artifactPath}/${pkgPath}${className}${suffix}${fileType}")
    defaultArtifactFile = new File("${basedir}/${artifactPath}/${pkgPath}${className}${suffix}.groovy")

    if (!templateFile?.exists()) {
        event('StatusFinal', ["Could not locate a suitable template for $artifactFile"])
        exit(1)
    }

    def similarFiles = resolveResources("file:${basedir}/${artifactPath}/${pkgPath}${className}${suffix}.*")
    if (similarFiles) {
        def fileSuffix = similarFiles[0].file.name.substring(similarFiles[0].file.name.lastIndexOf('.'))
        if (fileSuffix == fileType) {
            if (!replaceNonag && !confirmInput("${type} ${className}${suffix}${fileType} already exists. Overwrite?", "${artifactName}.${suffix}.overwrite")) {
                return
            }
        } else if (!allowDuplicate) {
            if (!replaceNonag && !confirmInput("${type} ${className}${suffix} already exists with type ${fileSuffix}. Rename?", "${artifactName}.${suffix}.rename")) {
                return
            }
            // WATCH OUT!! can cause problems with VCS systems
            ant.mkdir(dir: "${basedir}/renamed")
            ant.move(tofile: "${basedir}/renamed/${className}${suffix}${fileSuffix}", file: similarFiles[0].file)
        }
    } else if (replaceNonag) {
        event('StatusFinal', ["${basedir}/${artifactPath}/${pkgPath}${className}${suffix} cannot be replaced because it does not exist."])
        exit(1)
    }

    copyGriffonResource(artifactFile, templateFile)
    ant.replace(file: artifactFile) {
        replacefilter(token: "@artifact.name@", value: "${className}${suffix}")
        replacefilter(token: "@artifact.name.lowercase@", value: "${className}${suffix}".toLowerCase())
        replacefilter(token: "@artifact.name.plain@", value: className)
        replacefilter(token: "@artifact.name.plain.lowercase@", value: className.toLowerCase())
        replacefilter(token: "@artifact.suffix@", value: suffix)
        replacefilter(token: "@griffon.app.class.name@", value: appClassName)
        replacefilter(token: "@griffon.version@", value: griffonVersion)
        replacefilter(token: "@griffon.project.name@", value: griffonAppName)
        replacefilter(token: "@griffon.project.key@", value: griffonAppName.replaceAll(/\s/, '.').toLowerCase())
    }
    if (artifactPkg) {
        ant.replace(file: artifactFile, token: "@artifact.package@", value: "package ${artifactPkg}${lineTerminator}\n\n")
    } else {
        ant.replace(file: artifactFile, token: "@artifact.package@", value: "")
    }

    if (args["superClass"]) {
        ant.replace(file: artifactFile, token: "@artifact.superclass@", value: args["superClass"])
    }

    event('CreatedFile', [artifactFile])
    event('CreatedArtefact', [type, className])
    fileType = originalFileType
}

templateExists = { template, fileType ->
    def templateFile = resolveTemplate(template, fileType)
    if (!templateFile?.exists() && fileType != '.groovy') {
        templateFile = resolveTemplate(template, '.groovy')
    }
    templateFile.exists()
}

resolveTemplate = { template, fileSuffix ->
    // first check for presence of template in application
    def templateFile = new FileSystemResource("${basedir}/src/templates/artifacts/${template}${fileSuffix}")
    if (!templateFile.exists()) {
        // now check for template provided by plugins
        def pluginTemplateFiles = resolveResources("file:${artifactSettings.artifactBase(Plugin.TYPE)}/*/src/templates/artifacts/${template}${fileSuffix}")
        if (pluginTemplateFiles) {
            templateFile = pluginTemplateFiles[0]
        }
        if (!templateFile.exists()) {
            // now check for template provided by an archetype
            templateFile = new FileSystemResource("${griffonWorkDir}/archetypes/${archetypeName}-${archetypeVersion}/templates/artifacts/${template}${fileSuffix}")
            if (!templateFile.exists()) {
                // now check for template provided by a bundled archetype
                templateFile = new ClassPathResource("archetypes/${archetypeName}/templates/artifacts/${template}${fileSuffix}")
                if (!templateFile.exists()) {
                    // template not found in archetypes, use default template
                    templateFile = new ClassPathResource("archetypes/default/templates/artifacts/${template}${fileSuffix}")
                }
            }
        }
    }
    debug("template ${templateFile} exists=${templateFile.exists()}")
    templateFile
}

createDefaultPackage = {
    if (!defaultPackageName) defaultPackageName = buildConfig.app.defaultPackageName
    if (!defaultPackageName) {
        if (griffonAppName.indexOf('.') != -1) {
            int pos = griffonAppName.lastIndexOf('.')
            defaultPackageName = griffonAppName[0..<pos].toLowerCase()
            griffonAppName = griffonAppName[(pos + 1)..-1]
        } else {
            defaultPackageName = (buildConfig.griffon.project.groupId ?: griffonAppName).replace('-', '.').toLowerCase()
        }
    }
    defaultPackageName
}

doCreateIntegrationTest = { Map args = [:] ->
    def superClass = args["superClass"] ?: "GriffonUnitTestCase"
    createArtifact(name: args["name"], suffix: "${args['suffix']}Tests", type: "IntegrationTests", path: "test/integration", superClass: superClass)
}

doCreateUnitTest = { Map args = [:] ->
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
    if (!suffix) return name
    def newName = name
    if (name && name =~ /.+$suffix$/) {
        newName = name.replaceAll(/$suffix$/, '')
    }

    // Also remove variations that start with the same sequence of letters
    // as the suffix UNLESS that would remove the entire artifact name.
    if (name == newName) {
        for (int i = name.length() - 1; i >= 0; i--) {
            def str = name[i..-1]
            if (suffix.startsWith(str) && !(newName - str).endsWith(".")) {
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
        if (artifactPkg.startsWith("~")) {
            artifactPkg = artifactPkg.replace("~", createDefaultPackage())
        }
    } else {
        argsMap['skip-package-prompt'] = argsMap['skip-package-prompt'] ?: argsMap.skipPackagePrompt
        artifactPkg = argsMap['skip-package-prompt'] ? '' : createDefaultPackage()
    }

    return [artifactPkg, artifactName]
}

target(name: 'resolveArchetype', description: '', prehook: null, posthook: null) {
    Map archetypeProps = [:]
    if (!archetypeName && !archetypeVersion) {
        String archetype = argsMap.archetype ?: ''
        if (archetype == 'default') {
            archetypeName = 'default'
            archetypeVersion = GriffonUtil.getGriffonVersion()
        } else if (archetype) {
            Matcher matcher = artifactNameVersionPattern.matcher(archetype)
            if (matcher.find()) {
                archetypeName = matcher.group(1)
                archetypeVersion = matcher.group(2)
                archetypeProps.name = archetypeName
                archetypeProps.version = archetypeVersion
            } else {
                archetypeProps.name = archetype
                File file = artifactSettings.findArtifactDirForName(Archetype.TYPE, archetype)
                if (file) {
                    matcher = artifactNameVersionPattern.matcher(file.name)
                    matcher.find()
                    archetypeName = matcher.group(1)
                    archetypeVersion = matcher.group(2)
                    archetypeProps.version = archetypeVersion
                }
            }
            checkArchetype(archetypeProps)
        }
    }

    if (!archetypeName && !archetypeVersion) {
        Map<String, String> archetype = Metadata.current.getArchetype()
        if (archetype) {
            archetypeName = archetype.name
            archetypeVersion = archetype.version
            archetypeProps.name = archetypeName
            archetypeProps.version = archetypeVersion
            checkArchetype(archetypeProps)
        }
    }

    if (!archetypeName && !archetypeVersion) {
        archetypeName = 'default'
        archetypeVersion = GriffonUtil.getGriffonVersion()
    }
}

target(name: 'resolveFileType', description: '', prehook: null, posthook: null) {
    argsMap['file-type'] = argsMap['file-type'] ?: argsMap.fileType
    def cfileType = buildConfig.app.fileType
    if (!cfileType && cfileType instanceof ConfigObject) cfileType = null
    fileType = argsMap['file-type'] ?: (cfileType ?: 'groovy')
    if (!fileType.startsWith('.')) fileType = '.' + fileType
}

checkArchetype = { Map archetypeProps ->
    if (archetypeProps.name == 'default') return
    def archetypeDir = new FileSystemResource("${griffonWorkDir}/archetypes/${archetypeProps.name}-${archetypeProps.version}")
    if (!archetypeDir.exists()) {
        event 'StatusUpdate', ["Archetype ${archetypeProps.name}${archetypeProps.version ? '-' + archetypeProps.version : ''} is not intalled"]
        def paramsCopy = []
        paramsCopy.addAll(argsMap.params)
        argsMap.params = [archetypeProps.name, archetypeProps.version]
        installArtifact(Archetype.TYPE)
        argsMap.params = paramsCopy
        if (!archetypeDir.exists()) {
            def v = archetypeProps.version
            event 'StatusError', ["Archetype ${archetypeProps.name}${v ? '-' + v : ''} could not be resolved."]
            exit(1)
        }
    }
}

loadArchetypeFor = { type = 'application' ->
    resolveArchetype()

    def gcl = new GroovyClassLoader(classLoader)
    if (archetypeName == 'default') {
        archetypeFile = new ClassPathResource("archetypes/${archetypeName}/${type}.groovy")
        includeTargets << gcl.parseClass(archetypeFile.getURL().text)
        return
    }

    archetypeFile = new FileSystemResource("${griffonWorkDir}/archetypes/${archetypeName}-${archetypeVersion}/${type}.groovy")
    archetypeDirPath = "${griffonWorkDir}/archetypes/${archetypeName}-${archetypeVersion}"

    if (archetypeFile.exists()) {
        try {
            if (archetypeFile instanceof FileSystemResource) {
                includeTargets << gcl.parseClass(archetypeFile.file)
            } else if (archetypeFile instanceof ClassPathResource) {
                includeTargets << gcl.parseClass(archetypeFile.getURL().text)
            } else {
                throw new IllegalArgumentException("Don't know how to deal with $archetypeFile")
            }
        } catch (Exception e) {
            logError("An error ocurred while parsing archetype ${archetypeName}-${archetypeVersion}.", e)
            event 'StatusError', ["Archetype ${archetypeName}-${archetypeVersion} could not be resolved."]
            exit(1)
        }
    } else {
        event 'StatusError', ["Archetype ${archetypeName}-${archetypeVersion} could not be resolved."]
        exit(1)
    }
}
