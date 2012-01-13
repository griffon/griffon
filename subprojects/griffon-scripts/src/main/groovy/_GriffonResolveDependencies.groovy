/*
* Copyright 2011-2012 the original author or authors.
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

import org.codehaus.griffon.artifacts.ArtifactInstallEngine
import org.codehaus.griffon.artifacts.model.Plugin
import static griffon.util.GriffonApplicationUtils.is64Bit
import static org.codehaus.griffon.artifacts.ArtifactUtils.artifactBase

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_resolve_dependencies_called')) return
_griffon_resolve_dependencies_called = true

includeTargets << griffonScript('_GriffonArtifacts')

runDependencyResolution = true
target('resolveDependencies': '') {
    if (runDependencyResolution) {
        long start = System.currentTimeMillis()
        event 'StatusUpdate', ['Resolving plugin dependencies']
        ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine()
        if (!artifactInstallEngine.resolvePluginDependencies()) {
            exit(1)
        }

        pluginSettings.resolveAndAddAllPluginDependencies()
        long end = System.currentTimeMillis()
        event 'StatusFinal', ["Plugin dependencies resolved in ${end - start} ms."]

// XXX -- NATIVE
        Map<String, List<File>> jars = [:]
        processPlatformLibraries(jars, platform)
        if (is64Bit) {
            processPlatformLibraries(jars, platform[0..-3], false)
        }

        if (jars) {
            List<File> files = []
            jars.values().each { files.addAll it }
            griffonSettings.updateDependenciesFor 'runtime', files
            griffonSettings.updateDependenciesFor 'test', files
        }
// XXX -- NATIVE
    }
}

// XXX -- NATIVE
processPlatformLibraries = { Map<String, List<File>> jars, String platformId, boolean overwrite = true ->
    platformDir = new File("${basedir}/lib/${platformId}")
    resolveResources("file:${platformDir}/*.jar").each { jar ->
        List<File> list = jars.get(platformDir, [])
        if (list && !overwrite) return
        if (!list.contains(jar.file)) list << jar.file
    }
    resolveResources("file:${artifactBase(Plugin.TYPE)}/*/lib/${platformId}/*.jar").each { jar ->
        String pluginDir = jar.file.parentFile.parentFile
        List<File> list = jars.get(pluginDir, [])
        if (list && !overwrite) return
        if (!list.contains(jar.file)) list << jar.file
    }
    jars
}
// XXX -- NATIVE