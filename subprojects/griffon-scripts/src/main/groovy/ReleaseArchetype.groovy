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

import griffon.util.MD5
import groovy.json.JsonBuilder
import org.codehaus.griffon.artifacts.ArtifactUtils
import org.springframework.core.io.Resource

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('Init')
includeTargets << griffonScript('PackageArchetype')

target(releaseArchetype: 'Publishes a Griffon archetype release') {
    depends(packageArchetype)
    Resource archetypeDescriptor = ArtifactUtils.getArchetypeDescriptor(basedir)
    if (!archetypeDescriptor?.exists()) {
        event('StatusFinal', ['Current directory does not appear to be a Griffon archetype project.'])
        exit(1)
    }

    Map archetypeInfo = loadArchetypeInfo(archetypeDescriptor)
    createArchetypeRelease(archetypeInfo)
}

setDefaultTarget(releaseArchetype)

createArchetypeRelease = { Map archetypeInfo ->
    archetypeReleaseDirPath = "${projectTargetDir}/release"
    ant.delete(dir: archetypeReleaseDirPath, quiet: true, failOnError: false)
    ant.mkdir(dir: archetypeReleaseDirPath)
    String archetypeZipChecksumFileName = new File("${archetypeReleaseDirPath}/${archetypeZipFileName}.md5")
    String checksum = MD5.encode(new File("${archetypePackageDirPath}/${archetypeZipFileName}").bytes)
    new File(archetypeZipChecksumFileName).text = checksum
    archetypeInfo.checksum = checksum
    archetypeInfo.type = 'archetype'
    archetypeInfo.comment = argsMap.comment

    JsonBuilder builder = new JsonBuilder()
    builder.call(archetypeInfo)

    new File(archetypeReleaseDirPath, 'archetype.json').text = builder.toString()
    ant.delete(file: "${archetypeReleaseDirPath}/${archetypeZipFileName}", quiet: true, failOnError: false)

    ant.zip(destfile: "${archetypeReleaseDirPath}/${archetypeZipFileName}", filesonly: true) {
        fileset(dir: archetypeReleaseDirPath)
        zipfileset(dir: archetypePackageDirPath,
                includes: archetypeZipFileName,
                fullpath: archetypeZipFileName)
    }
}