/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT c;pWARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.util

import java.util.concurrent.ConcurrentHashMap
import org.codehaus.gant.GantBinding
import org.codehaus.griffon.artifacts.InstallArtifactException
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.codehaus.griffon.resolve.IvyDependencyManager
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import static griffon.util.GriffonNameUtils.getHyphenatedName
import static org.apache.commons.lang.ArrayUtils.addAll
import static org.codehaus.griffon.artifacts.ArtifactUtils.*

/**
 * Common utilities for dealing with plugins.
 *
 * @author Andres Almiray
 * @since 0.9.5
 */
class PluginSettings {
    final BuildSettings settings
    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>()
    private Map<String, Resource> nameToPluginDirMap = new ConcurrentHashMap<String, Resource>()

    PluginSettings(BuildSettings settings) {
        this.settings = settings
    }

    void initBinding(GantBinding binding) {
        final PluginSettings self = this
        binding.setVariable('getPluginDirForName') { String pluginName ->
            self.getPluginDirForName(pluginName)
        }
    }

    void clearCaches() {
        cache.clear()
        nameToPluginDirMap.clear()
    }

    Resource[] getPluginDirectories() {
        Resource[] pluginDirectories = cache['pluginDirectories']
        if (!pluginDirectories) {
            pluginDirectories = findAllArtifactDirsForType(Plugin.TYPE)
            cache['pluginDirectories'] = pluginDirectories
        }
        pluginDirectories
    }

    Map<String, Release> getPlugins() {
        Map<String, Release> plugins = cache['plugins']
        if (!plugins) {
            plugins = [:]
            getPluginDirectories().each { Resource pluginDir ->
                Release release = getArtifactRelease(Plugin.TYPE, pluginDir.file)
                plugins[release.artifact.name] = release
            }
            cache['plugins'] = plugins
        }
        plugins
    }

    Resource[] getPluginScripts() {
        resolveForEachPlugin('pluginScripts') { pluginDir ->
            resolveResources("file://${pluginDir}/scripts/*.groovy")
        }
    }

    Resource[] getPluginLibDirectories() {
        resolveForEachPlugin('pluginLibs') { pluginDir ->
            resolveResources("file:${pluginDir}/lib")
        }
    }

    Resource[] getAvailableScripts() {
        Resource[] availableScripts = cache['availableScripts']
        if (!availableScripts) {
            List<Resource> scripts = []
            String userHome = System.getProperty("user.home")
            String griffonHome = settings.griffonHome.absolutePath
            String basedir = settings.baseDir.absolutePath

            def addScripts = {if (!it.file.name.startsWith('_')) scripts << it}

            resolveResources("file:${griffonHome}/scripts/*.groovy").each addScripts
            resolveResources("file:${basedir}/scripts/*.groovy").each addScripts
            resolveResources("file:${userHome}/.griffon/scripts/*.groovy").each addScripts
            pluginScripts.each addScripts

            availableScripts = scripts as Resource[]
            cache['availableScripts'] = availableScripts
        }
        availableScripts
    }

    Resource[] getAvailableScripts(String scriptName) {
        List<Resource> scripts = []
        String userHome = System.getProperty("user.home")
        String griffonHome = settings.griffonHome.absolutePath
        String basedir = settings.baseDir.absolutePath
        String pluginHome = settings.projectPluginsDir.absolutePath

        def addScripts = {if (!it.file.name.startsWith('_')) scripts << it}

        resolveResources("file:${griffonHome}/scripts/${scriptName}/*.groovy").each addScripts
        resolveResources("file:${basedir}/scripts/${scriptName}/*.groovy").each addScripts
        resolveResources("file:${userHome}/.griffon/${scriptName}/scripts/*.groovy").each addScripts
        resolveResources("file:${pluginHome}/scripts/${scriptName}/*.groovy").each addScripts

        scripts as Resource[]
    }

    Resource getPluginDirForName(String name) {
        String key = getHyphenatedName(name)
        Resource pluginDir = nameToPluginDirMap[key]
        if (!pluginDir) {
            File file = findArtifactDirForName(Plugin.TYPE, name)
            if (file) {
                pluginDir = new FileSystemResource(file)
                nameToPluginDirMap[key] = pluginDir
            }
        }
        pluginDir
    }

    void doWithPlugins(Closure closure) {
        getPlugins().each { pluginName, release ->
            String pluginVersion = release.version
            String pluginInstallPath = getInstallPathFor(Plugin.TYPE, pluginName, pluginVersion)
            closure(pluginName, pluginVersion, pluginInstallPath)
        }
    }

    void resolveAndAddAllPluginDependencies() {
        Map<String, List<File>> configurations = [:]

        doWithPlugins { String pluginName, String pluginVersion, String pluginInstallPath ->
            List<File> dependencyDescriptors = [
                    new File("$pluginInstallPath/dependencies.groovy"),
                    new File("$pluginInstallPath/plugin-dependencies.groovy")
            ]

            if (dependencyDescriptors.any {it.exists()}) {
                def callable = settings.pluginDependencyHandler()
                callable.call(new File(pluginInstallPath), pluginName, pluginVersion)
            }
        }

        IvyDependencyManager dependencyManager = settings.dependencyManager
        for (conf in ['compile', 'build', 'test', 'runtime']) {
            def resolveReport = dependencyManager.resolveDependencies(IvyDependencyManager."${conf.toUpperCase()}_CONFIGURATION")
            if (resolveReport.hasError()) {
                throw new IllegalStateException("Some dependencies failed to be resolved.")
            } else {
                configurations.get(conf, []).addAll(resolveReport.allArtifactsReports.localFile)
            }
        }

        configurations.each { String conf, List<File> dependencies ->
            settings.updateDependenciesFor conf, dependencies
        }

        // Finally copy over test into build
        settings.updateDependenciesFor 'build', settings.getTestDependencies()
    }

    private Resource[] resolveForEachPlugin(String key, Closure closure) {
        Resource[] allResources = new Resource[0]
        getPluginDirectories().each { pluginDir ->
            Resource[] resources = closure(pluginDir.file.absolutePath)
            if (resources) {
                allResources = addAll(allResources, resources) as Resource[]
            }
        }
        allResources
    }

    private Resource[] resolveResources(String pattern) {
        settings.resolveResourcesClosure(pattern)
    }

    private Resource[] resolveResources(String key, String pattern) {
        Resource[] resources = cache[key]
        if (!resources) {
            resources = resolveResources(pattern)
            cache[key] = resources
        }
        resources
    }

    static Map<String, Resource> getSortedPluginDirectories() {
        Map noDependencies = [:]
        Map withDependencies = [:]
        List sorted = []
        findAllArtifactDirsForType(Plugin.TYPE).each { Resource r ->
            Release release = getArtifactRelease(Plugin.TYPE, r.file)
            if (release.dependencies) {
                withDependencies[release.artifact.name] = [deps: release.dependencies, dir: r]
            } else {
                noDependencies[release.artifact.name] = r
                sorted << [name: release.artifact.name, dir: r]
            }
        }

        def resolveDependencies
        resolveDependencies = { name, values ->
            if (sorted.find { it.name == name }) return
            values.deps.each { entry ->
                if (withDependencies[entry.name]) resolveDependencies(entry.name, withDependencies[entry.name])
            }
            def index = -1
            values.deps.each { entry ->
                index = Math.max(index, sorted.indexOf(sorted.find {it.name == entry.name}))
            }
            sorted = insert(sorted, [name: name, dir: values.dir], index + 1)
        }

        withDependencies.each(resolveDependencies)
        sorted.inject([:]) { map, element ->
            map[element.name] = element.dir
            map
        }
    }

    private static List insert(List list, obj, int index) {
        int size = list.size()
        if (index >= size) {
            list[index] = obj
            return list
        } else {
            def head = list[0..<index]
            def tail = list[index..-1]
            return head + [obj] + tail
        }
    }
}
