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

import groovy.json.JsonBuilder
import org.codehaus.griffon.artifacts.ArtifactUtils
import org.codehaus.griffon.artifacts.model.Archetype

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('_PackageArtifact')

target(packageArchetype: 'Packages a Griffon archetype') {
    depends(parseArguments, checkVersion)

    archetypeDescriptor = ArtifactUtils.getArchetypeDescriptor(basedir)
    if (!archetypeDescriptor?.exists()) {
        event('StatusFinal', ['Current directory does not appear to be a Griffon archetype project.'])
        exit(1)
    }

    archetypeInfo = loadArtifactInfo(Archetype.TYPE, archetypeDescriptor)

    if (packageForRelease) {
        checkLicense(Archetype.TYPE)
    }

    zipArchetype(archetypeInfo)
}

setDefaultTarget(packageArchetype)

zipArchetype = { Map artifactInfo ->
    artifactPackageDirPath = "${projectTargetDir}/package"
    ant.delete(dir: artifactPackageDirPath, quiet: true, failOnError: false)
    ant.mkdir(dir: artifactPackageDirPath)
    JsonBuilder builder = new JsonBuilder()
    builder.call(artifactInfo)

    new File(artifactPackageDirPath, 'archetype.json').text = builder.toString()
    ant.copy(todir: artifactPackageDirPath) {
        fileset(dir: basedir, includes: 'LICENSE*, application.groovy')
    }
    ant.copy(todir: "${artifactPackageDirPath}/griffon-app") {
        fileset(dir: "${basedir}/griffon-app", excludes: '**/conf/BuildConfig.groovy')
    }
    File templatesDir = new File(basedir, 'templates')
    if (templatesDir.exists()) {
        ant.copy(todir: "${artifactPackageDirPath}/templates") {
            fileset(dir: templatesDir, excludes: '**/.git/**, **/.svn/**, **/CVS/**')
        }
    }

    artifactZipFileName = "griffon-${artifactInfo.name}-${artifactInfo.version}.zip"
    ant.delete(file: "${artifactPackageDirPath}/${artifactZipFileName}", quiet: true, failOnError: false)
    ant.zip(destfile: "${artifactPackageDirPath}/${artifactZipFileName}", basedir: artifactPackageDirPath)
}