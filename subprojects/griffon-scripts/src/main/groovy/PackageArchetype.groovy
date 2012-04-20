/*
* Copyright 2010-2012 the original author or authors.
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

import griffon.util.ArtifactSettings
import org.codehaus.griffon.artifacts.model.Archetype

/**
 * @author Andres Almiray
 */

if (getBinding().variables.containsKey('_griffon_package_archetype_called')) return
_griffon_package_archetype_called = true

includeTargets << griffonScript('_GriffonPackageArtifact')

target(name: 'packageArchetype',
       description: 'Packages a Griffon archetype',
       prehook: null, posthook: null) {
    archetypeDescriptor = ArtifactSettings.getArchetypeDescriptor(basedir)
    if (!archetypeDescriptor?.exists()) {
        event('StatusFinal', ['Current directory does not appear to be a Griffon archetype project.'])
        exit(1)
    }

    artifactInfo = loadArtifactInfo(Archetype.TYPE, archetypeDescriptor)
    packageArtifact()
}

setDefaultTarget(packageArchetype)

target(name: 'package_archetype', description: '',
       prehook: null, posthook: null) {
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
}

target(name: 'post_package_archetype', description: '',
        prehook: null, posthook: null) {
    // empty
}