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

/**
 * @author Andres Almiray
 */

// No point doing this stuff more than once.
if (getBinding().variables.containsKey('_griffon_resolve_dependencies_called')) return
_griffon_resolve_dependencies_called = true

includeTargets << griffonScript('_GriffonArtifacts')

runDependencyResolution = true
runFrameworkDependencyResolution = true

target(name: 'resolveDependencies', description: 'Resolves project and plugin dependencies',
        prehook: null, posthook: null) {
    if (!griffonSettings.isGriffonProject()) return

    if (runDependencyResolution) {
        long start = System.currentTimeMillis()
        event 'StatusUpdate', ['Resolving plugin dependencies']
        ArtifactInstallEngine artifactInstallEngine = createArtifactInstallEngine()
        if (!artifactInstallEngine.resolvePluginDependencies()) {
            exit(1)
        }

        pluginSettings.clearCaches()
        pluginSettings.resolveAndAddAllPluginDependencies(false)

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
        if(projectCliClassesDir.exists()) {
            addUrlIfNotPresent classLoader, projectCliClassesDir
            addUrlIfNotPresent rootLoader, projectCliClassesDir
        }

        long end = System.currentTimeMillis()
        event 'StatusFinal', ["Plugin dependencies resolved in ${end - start} ms."]

        runDependencyResolution = false
        // reset appName
        if (metadata) griffonAppName = metadata.getApplicationName()
    }
}

target(name: 'resolveFrameworkDependencies', description: 'Resolves framework plugin dependencies',
        prehook: null, posthook: null) {
    // if (griffonSettings.isGriffonProject()) return

    if (runFrameworkDependencyResolution) {
        long start = System.currentTimeMillis()
        event 'StatusUpdate', ['Resolving framework plugin dependencies']
        pluginSettings.clearCaches()
        pluginSettings.resolveAndAddAllPluginDependencies(true)
        long end = System.currentTimeMillis()
        event 'StatusFinal', ["Framework plugin dependencies resolved in ${end - start} ms."]

        runFrameworkDependencyResolution = false
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
    resolveResources("file:${artifactSettings.artifactBase(Plugin.TYPE)}/*/lib/${platformId}/*.jar").each { jar ->
        String pluginDir = jar.file.parentFile.parentFile
        List<File> list = jars.get(pluginDir, [])
        if (list && !overwrite) return
        if (!list.contains(jar.file)) list << jar.file
    }
    jars
}
// XXX -- NATIVE
