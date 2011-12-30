import groovy.json.JsonBuilder
import org.codehaus.griffon.artifacts.ArtifactUtils
import org.springframework.core.io.Resource

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

target(packageArchetype: 'Packages a Griffon archetype') {
    depends(parseArguments, checkVersion)
    Resource archetypeDescriptor = ArtifactUtils.getArchetypeDescriptor(basedir)
    if (!archetypeDescriptor?.exists()) {
        event('StatusFinal', ['Current directory does not appear to be a Griffon archetype project.'])
        exit(1)
    }

    Map archetypeInfo = loadArchetypeInfo(archetypeDescriptor)
    zipArchetype(archetypeInfo)
}

setDefaultTarget(packageArchetype)

loadArchetypeInfo = { Resource archetypeDescriptor ->
    def descriptorInstance = loadArtifactDescriptorClass(archetypeDescriptor.file.name)

    [
            name: ArtifactUtils.getArchetypeNameFromDescriptor(archetypeDescriptor),
            title: descriptorInstance.title,
            license: descriptorInstance.license,
            version: descriptorInstance.version,
            griffonVersion: descriptorInstance.griffonVersion,
            description: descriptorInstance.description.trim(),
            authors: descriptorInstance.authors
    ]
}

zipArchetype = { Map archetypeInfo ->
    archetypePackageDirPath = "${projectTargetDir}/package"
    ant.delete(dir: archetypePackageDirPath, quiet: true, failOnError: false)
    ant.mkdir(dir: archetypePackageDirPath)
    JsonBuilder builder = new JsonBuilder()
    builder.call(archetypeInfo)

    new File(archetypePackageDirPath, 'archetype.json').text = builder.toString()
    ant.copy(file: "${basedir}/application.groovy", todir: archetypePackageDirPath)
    File templatesDir = new File(basedir, 'templates')
    if (templatesDir.exists()) {
        ant.copy(todir: "${archetypePackageDirPath}/templates") {
            fileset(dir: templatesDir, excludes: '**/.git/**, **/.svn/**, **/CVS/**')
        }
    }

    archetypeZipFileName = "griffon-${archetypeInfo.name}-${archetypeInfo.version}.zip"
    ant.delete(file: "${archetypePackageDirPath}/${archetypeZipFileName}", quiet: true, failOnError: false)
    ant.zip(destfile: "${archetypePackageDirPath}/${archetypeZipFileName}", basedir: archetypePackageDirPath)
}