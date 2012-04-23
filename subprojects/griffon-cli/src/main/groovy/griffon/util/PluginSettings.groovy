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

import org.codehaus.gant.GantBinding
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.codehaus.griffon.plugins.PluginInfo
import org.codehaus.griffon.resolve.IvyDependencyManager
import org.springframework.core.io.Resource

import java.util.concurrent.ConcurrentHashMap

import static ArtifactSettings.getArtifactRelease
import static griffon.util.GriffonNameUtils.getHyphenatedName
import static org.apache.commons.lang.ArrayUtils.addAll

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

    Resource[] getProjectPluginDirectories() {
        Resource[] projectPluginDirectories = cache['projectPluginDirectories']
        if (!projectPluginDirectories) {
            projectPluginDirectories = resolveResources("file://${settings.projectPluginsDir.absolutePath}/*")
            cache['projectPluginDirectories'] = projectPluginDirectories
        }
        projectPluginDirectories
    }

    Resource[] getFrameworkPluginDirectories() {
        Resource[] frameworkPluginDirectories = cache['frameworkPluginDirectories']
        if (!frameworkPluginDirectories) {
            frameworkPluginDirectories = resolveResources("file:${settings.griffonHome}/plugins/*")
            cache['frameworkPluginDirectories'] = frameworkPluginDirectories
        }
        frameworkPluginDirectories
    }

    Map<String, Release> getProjectPluginReleases() {
        Map<String, Release> projectPluginReleases = cache['projectPluginReleases']
        if (!projectPluginReleases) {
            projectPluginReleases = [:]
            for (Resource pluginDir : getProjectPluginDirectories()) {
                if (pluginDir.exists()) {
                    Release release = getArtifactRelease(Plugin.TYPE, pluginDir.file)
                    projectPluginReleases[release.artifact.name] = release
                }
            }
            cache['projectPluginReleases'] = projectPluginReleases
        }
        projectPluginReleases
    }

    Map<String, Release> getFrameworkPluginReleases() {
        Map<String, Release> frameworkPluginReleases = cache['frameworkPluginReleases']
        if (!frameworkPluginReleases) {
            frameworkPluginReleases = [:]
            for (Resource pluginDir : getFrameworkPluginDirectories()) {
                if (pluginDir.exists()) {
                    Release release = getArtifactRelease(Plugin.TYPE, pluginDir.file)
                    frameworkPluginReleases[release.artifact.name] = release
                }
            }
            cache['frameworkPluginReleases'] = frameworkPluginReleases
        }
        frameworkPluginReleases
    }

    Map<String, Release> getPluginReleases() {
        Map<String, Release> pluginReleases = cache['pluginReleases']
        if (!pluginReleases) {
            pluginReleases = getFrameworkPluginReleases()
            pluginReleases += getProjectPluginReleases()
            cache['pluginReleases'] = pluginReleases
        }
        pluginReleases
    }

    Map<String, PluginInfo> getProjectPlugins() {
        Map<String, PluginInfo> projectPlugins = cache['projectPlugins']
        if (!projectPlugins) {
            projectPlugins = [:]
            for (Resource pluginDir : getProjectPluginDirectories()) {
                if (pluginDir.exists()) {
                    Release release = getArtifactRelease(Plugin.TYPE, pluginDir.file)
                    projectPlugins[release.artifact.name] = new PluginInfo(
                            release.artifact.name,
                            pluginDir,
                            release
                    )
                }
            }
            cache['projectPluginReleases'] = projectPlugins
        }
        projectPlugins
    }

    Map<String, PluginInfo> getFrameworkPlugins() {
        Map<String, PluginInfo> frameworkPlugins = cache['frameworkPlugins']
        if (!frameworkPlugins) {
            frameworkPlugins = [:]
            for (Resource pluginDir : getFrameworkPluginDirectories()) {
                if (pluginDir.exists()) {
                    Release release = getArtifactRelease(Plugin.TYPE, pluginDir.file)
                    frameworkPlugins[release.artifact.name] = new PluginInfo(
                            release.artifact.name,
                            pluginDir,
                            release
                    )
                }
            }
            cache['frameworkPlugins'] = frameworkPlugins
        }
        frameworkPlugins
    }

    Map<String, PluginInfo> getPlugins() {
        Map<String, PluginInfo> plugins = cache['plugins']
        if (!plugins) {
            plugins = getFrameworkPlugins()
            plugins += getProjectPlugins()
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
            String griffonHome = settings.griffonHome?.absolutePath
            String basedir = settings.baseDir.absolutePath

            def addScripts = {if (!it.file.name.startsWith('_')) scripts << it}

            if (griffonHome) resolveResources("file:${griffonHome}/scripts/*.groovy").each addScripts
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
        String griffonHome = settings.griffonHome?.absolutePath
        String basedir = settings.baseDir.absolutePath

        def addScripts = {if (!it.file.name.startsWith('_')) scripts << it}

        if (griffonHome) resolveResources("file:${griffonHome}/scripts/${scriptName}.groovy").each addScripts
        resolveResources("file:${basedir}/scripts/${scriptName}.groovy").each addScripts
        resolveResources("file:${userHome}/.griffon/scripts/${scriptName}.groovy").each addScripts

        scripts as Resource[]
    }

    Resource getPluginDirForName(String name) {
        String key = getHyphenatedName(name)
        Resource pluginDir = nameToPluginDirMap[key]
        if (!pluginDir) {
            // project plugin
            Resource[] resources = resolveResources("file://${settings.projectPluginsDir.absolutePath}/${name}-*")
            if (resources) pluginDir = resources[0]
            if (!pluginDir.exists()) {
                //framework plugin
                resources = resolveResources("file://${settings.griffonHome}/plugins/${name}-*")
                if (resources) pluginDir = resources[0]
            }
            if (pluginDir) {
                nameToPluginDirMap[key] = pluginDir
            }
        }
        pluginDir
    }

    void doWithProjectPlugins(Closure closure) {
        for (Map.Entry<String, PluginInfo> pluginEntry : getProjectPlugins()) {
            String pluginName = pluginEntry.key
            PluginInfo pluginInfo = pluginEntry.value
            String pluginVersion = pluginInfo.release.version
            File pluginInstallDir = pluginInfo.directory.file
            if (!pluginInstallDir.exists()) return
            closure(pluginName, pluginVersion, pluginInstallDir.canonicalPath)
        }
    }

    void doWithFrameworkPlugins(Closure closure) {
        for (Map.Entry<String, PluginInfo> pluginEntry : getFrameworkPlugins()) {
            String pluginName = pluginEntry.key
            PluginInfo pluginInfo = pluginEntry.value
            String pluginVersion = pluginInfo.release.version
            File pluginInstallDir = pluginInfo.directory.file
            if (!pluginInstallDir.exists()) return
            closure(pluginName, pluginVersion, pluginInstallDir.canonicalPath)
        }
    }

    void resolveAndAddAllPluginDependencies(boolean framework) {
        Map<String, List<File>> configurations = [:]

        // Metadata metadata = Metadata.getInstance(new File("${settings.baseDir}/application.properties"))
        // String projectName = metadata.getApplicationName()
        // if (isBlank(projectName)) projectName = settings.baseDir.name

        // boolean parsedDependencies = false
        def pluginProcessor = { String pluginName, String pluginVersion, String pluginInstallPath ->
            // if (pluginName == projectName) return
            List<File> dependencyDescriptors = [
                    new File("$pluginInstallPath/dependencies.groovy"),
                    new File("$pluginInstallPath/plugin-dependencies.groovy")
            ]

            if (dependencyDescriptors.any {it.exists()}) {
                def callable = settings.pluginDependencyHandler()
                callable.call(new File(pluginInstallPath), pluginName, pluginVersion)
                // parsedDependencies = true
            }
        }

        if (framework) {
            doWithFrameworkPlugins pluginProcessor
        } else {
            doWithProjectPlugins pluginProcessor
        }

        // if (parsedDependencies) {
        IvyDependencyManager dependencyManager = settings.dependencyManager
        for (conf in ['runtime', 'compile', 'test', 'build']) {
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
        // }
    }

    private Resource[] resolveForEachPlugin(String key, Closure closure) {
        Resource[] allResources = new Resource[0]
        for (Map.Entry<String, PluginInfo> pluginEntry : getPlugins().entrySet()) {
            Resource[] resources = closure(pluginEntry.value.directory.file.absolutePath)
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

    Map<String, PluginInfo> getSortedProjectPluginDirectories() {
        getSortedPlugins(getProjectPlugins())
    }

    Map<String, PluginInfo> getSortedFrameworkPluginDirectories() {
        getSortedPlugins(getFrameworkPlugins())
    }

    private Map<String, PluginInfo> getSortedPlugins(Map<String, PluginInfo> plugins) {
        Map noDependencies = [:]
        Map withDependencies = [:]
        List sorted = []
        for (PluginInfo pluginInfo : plugins.values()) {
            Release release = pluginInfo.release
            if (release.dependencies) {
                withDependencies[pluginInfo.name] = [deps: release.dependencies, pluginInfo: pluginInfo]
            } else {
                noDependencies[release.artifact.name] = pluginInfo
                sorted << [name: release.artifact.name, pluginInfo: pluginInfo]
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
            sorted = insert(sorted, [name: name, pluginInfo: values.pluginInfo], index + 1)
        }

        withDependencies.each(resolveDependencies)
        sorted.inject([:]) { map, element ->
            map[element.name] = element.pluginInfo
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
