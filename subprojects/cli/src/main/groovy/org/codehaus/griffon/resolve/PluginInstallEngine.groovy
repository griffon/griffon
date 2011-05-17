/*
 * Copyright 2004-2011 the original author or authors.
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
package org.codehaus.griffon.resolve

import griffon.util.PluginBuildSettings
import griffon.util.BuildSettings
import griffon.util.Metadata
import griffon.util.GriffonUtil
import griffon.util.PlatformUtils
import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.springframework.core.io.Resource
import org.codehaus.griffon.cli.CommandLineHelper
import groovy.util.slurpersupport.GPathResult
import org.codehaus.griffon.cli.ScriptExitException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import org.apache.ivy.core.report.ResolveReport
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.module.descriptor.DependencyDescriptor
import org.apache.ivy.core.report.ArtifactDownloadReport

/**
 * This class is used to manage the installation and uninstallation of plugins
 * from a Griffon project
 * 
 * @author Graeme Rocher (Grails 1.3)
 */
class PluginInstallEngine {
    Closure errorHandler = { String msg -> throw new ScriptExitException(msg) }
    Closure eventHandler = { String name, String msg -> println msg }
    Closure pluginScriptRunner
    Closure postInstallEvent
    Closure postUninstallEvent
    /**
     * plugins that were installed in the last execution of installPlugin
     */
    List installedPlugins = []
    def pluginDirVariableStore = [:]
    boolean isInteractive = true
    CommandLineHelper commandLineHelper = new CommandLineHelper(System.out)

    protected Metadata metadata
    protected PluginBuildSettings pluginSettings
    protected BuildSettings settings
    protected applicationPluginsLocation
    protected globalPluginsLocation
    protected ant
    protected PluginResolveEngine resolveEngine

    PluginInstallEngine(BuildSettings settings) {
        this(settings, new PluginBuildSettings(settings), Metadata.current, new AntBuilder())
    }

    PluginInstallEngine(BuildSettings settings, PluginBuildSettings pbs) {
        this(settings, pbs, Metadata.current, new AntBuilder())
    }

    PluginInstallEngine(BuildSettings settings, PluginBuildSettings pbs, Metadata md) {
        this(settings, pbs, md, new AntBuilder())
    }

    PluginInstallEngine(BuildSettings settings, PluginBuildSettings pbs, Metadata md, AntBuilder ant) {
        if(settings == null) throw new IllegalArgumentException("Argument [settings] cannot be null")
        if(pbs == null) throw new IllegalArgumentException("Argument [pbs] cannot be null")
        if(md == null) throw new IllegalArgumentException("Argument [md] cannot be null")
        if(ant== null) throw new IllegalArgumentException("Argument [ant] cannot be null")

        globalPluginsLocation = settings.globalPluginsDir
        applicationPluginsLocation = settings.getProjectPluginsDir()
        pluginSettings = pbs
        this.settings = settings
        this.ant = ant
        this.metadata = md
        this.resolveEngine = new PluginResolveEngine(settings.dependencyManager, settings)
    }

    /**
     * This method will resolve the current dependencies and install any missing plugins or upgrades
     * and remove any plugins that aren't present in the metadata but are installed 
     */
    void resolvePluginDependencies() {
        IvyDependencyManager dependencyManager = settings.dependencyManager

        List pluginDeps = dependencyManager.pluginDependencyDescriptors.collect { DependencyDescriptor dd ->
           dd.getDependencyRevisionId()
        }
        List pluginsToInstall = findMissingOrUpgradePlugins(pluginDeps)
        installPlugins(pluginsToInstall)

        checkPluginsToUninstall(pluginDeps)
    }

    /**
     * Installs a list of plugins
     *
     * @param params A list of plugins defined each by a ModuleRevisionId
     */
    void installPlugins(List<ModuleRevisionId> plugins) {
        if (plugins) {
            eventHandler "StatusUpdate", "Resolving new plugins. Please wait..."
            ResolveReport report = resolveEngine.resolvePlugins(plugins)
            if (report.hasError()) {
                errorHandler "Failed to resolve plugins."
            } else {
                for (ArtifactDownloadReport ar in report.allArtifactsReports) {
                    def arName = ar.artifact.moduleRevisionId.name
                    if (plugins.any { it.name == arName }) {
                        installPlugin ar.localFile
                    }
                }
            }
        }
    }

    /**
     * Installs a plugin for the given name and optional version
     *
     * @param name The plugin name
     * @param version The plugin version (optional)
     * @param globalInstall Whether to install globally or not (optional)
     */
    void installPlugin(String name, String version = null, boolean globalInstall = false) {
        installedPlugins.clear()
        def pluginZip = resolveEngine.resolvePluginZip(name, version)

        if(!pluginZip) {
            errorHandler "Plugin not found for name [$name] and version [${version ?: 'not specified'}]"
        }

        try {
            (name, version) = readMetadataFromZip(pluginZip.absolutePath)
            installPluginZipInternal(name, version, pluginZip, globalInstall)
        } catch (e) {
            errorHandler "Error installing plugin: ${e.message}"
        }
    }

    /**
     * Installs a plugin from the given ZIP file
     *
     * @param zipFile The plugin zip file
     * @param globalInstall Whether it is a global install or not (optional)
     */
    void installPlugin(File zipFile, boolean globalInstall = false, boolean overwrite = false) {
        if(zipFile.exists()) {
            def (name, version) = readMetadataFromZip(zipFile.absolutePath)
            installPluginZipInternal name, version, zipFile, globalInstall, overwrite
        }
        else {
            errorHandler "Plugin zip not found at location: ${zipFile.absolutePath}"
        }
    }

    /**
     * Installs a plugin from the given URL
     *
     * @param zipURL The zip URL
     * @param globalInstall Whether it is a global install or not (optional)
     */
    void installPlugin(URL zipURL, boolean globalInstall = false) {
        def s = zipURL.toString()
        def filename = s[s.lastIndexOf("/")..-1]
        def file = File.createTempFile(filename[0..-4], ".zip")
        file.deleteOnExit()
        eventHandler "StatusUpdate", "Downloading zip ${zipURL}. Please wait..."
        try {
            ant.get(src: zipURL, dest: file, verbose:"on")
        }
        catch (e) {
            errorHandler "Error downloading plugin ${zipURL}: ${e.message}"
        }
        installPlugin(file, globalInstall, true)
    }

    private String getPluginVersion(path) {
        (path =~ /^[\w][\w\.-]*-([0-9][\w\.]*)$/)[0][1]
    }

    protected void installPluginZipInternal(String name, String version, File pluginZip, boolean globalInstall = false, boolean overwrite = false) {

        def fullPluginName = "$name-$version"
        def pluginInstallPath = "${globalInstall ? globalPluginsLocation : applicationPluginsLocation}/${fullPluginName}"

        assertNoExistingInlinePlugin(name)

        def abort = checkExistingPluginInstall(name, version)

        if (abort && !overwrite) {
            registerPluginWithMetadata(name, version)
            return
        }

        eventHandler "StatusUpdate", "Installing zip ${pluginZip}..."

        installedPlugins << pluginInstallPath

        if (isNotInlinePluginLocation(new File(pluginInstallPath))) {
            ant.delete(dir: pluginInstallPath, failonerror: false)
            ant.mkdir(dir: pluginInstallPath)
            ant.unzip(dest: pluginInstallPath, src: pluginZip)
            ant.delete(file: "${pluginInstallPath}/application.properties", quiet: true, failonerror: false)
            eventHandler "StatusUpdate", "Installed plugin ${fullPluginName} to location ${pluginInstallPath}."
        } else {
            errorHandler "Cannot install plugin. Plugin install would override inline plugin configuration which is not allowed."
        }

        def pluginXmlFile = new File("${pluginInstallPath}/plugin.xml")
        if (!pluginXmlFile.exists()) {
            errorHandler("Plugin $fullPluginName is not a valid Griffon plugin. No plugin.xml descriptor found!")
        }
        def pluginXml = new XmlSlurper().parse(pluginXmlFile)
        def pluginName = pluginXml.@name.toString()
        def pluginVersion = pluginXml.@version.toString()
        def pluginGriffonVersion = pluginXml.@griffonVersion.toString()
        assertGriffonVersionValid(fullPluginName, pluginGriffonVersion)

        def pluginJdkVersion = pluginXml.@jdk?.toString()
        if (pluginJdkVersion) {
            pluginJdkVersion = new BigDecimal(pluginJdkVersion)
            def javaVersion = new BigDecimal(System.getProperty("java.version")[0..2])
            if (pluginJdkVersion > javaVersion) {
                cleanupPluginInstallAndExit(pluginInstallPath, "Failed to install plug-in [${fullPluginName}]. Required Jdk version is ${pluginJdkVersion}, current one is ${javaVersion}")
            }
        }

        def supportedPlatforms = pluginXml.platforms?.text()
        if (supportedPlatforms) {
            if (!(PlatformUtils.isCompatible(supportedPlatforms.split(',')))) {
                cleanupPluginInstallAndExit(pluginInstallPath, "Failed to install plug-in [${fullPluginName}]. Required platforms are [${supportedPlatforms}], current one is ${PlatformUtils.platform}")
            }
        }

        def supportedToolkits = pluginXml.toolkits?.text()
        if (supportedToolkits) {
            supportedToolkits = supportedToolkits.split(',').toList()
            def installedToolkits = metadata.'app.toolkits'
            if(!installedToolkits) installedToolkits = 'swing'
            installedToolkits = installedToolkits.split(',').toList()
            def unsupportedToolkits = installedToolkits - supportedToolkits
            if(unsupportedToolkits) {
                def pluginsToUninstall = [:]
                metadata.propertyNames().grep{ it.startsWith('plugins.') }.each { p ->
                    String pluginVersion1 = metadata[p]
                    String pluginName1 = p - 'plugins.'
                    def supportedToolkitsByPlugin = readPluginXmlMetadata(pluginName1).toolkits?.text()
                    // skip if property is not set
                    if(!supportedToolkitsByPlugin) return
                    supportedToolkitsByPlugin = supportedToolkitsByPlugin.split(',').toList()
                    if(supportedToolkitsByPlugin.intersect(supportedToolkits)) return 
                    if(supportedToolkitsByPlugin.intersect(unsupportedToolkits)) {
                        pluginsToUninstall[pluginName1] = pluginVersion1
                    }
                }
                if(pluginsToUninstall) {
                    def uninstallPluginsMessage = """Current application has ${installedToolkits} as supported UI toolkits.
    The plugin [${fullPluginName}] requires any of the following toolkits: ${supportedToolkits}
    Installing this plugin will uninstall all plugins that require ${unsupportedToolkits}.
    Plugins to be uninstalled:
    """
                    pluginsToUninstall.each { n, v -> uninstallPluginsMessage += "    ${n}-${v}\n" }
    
                    commandLineHelper.askAndDo("""${uninstallPluginsMessage}
    Do you wish to proceed?\n""", {
                        pluginsToUninstall.each { n, v ->
                            uninstallPlugin(n, v)
                            eventHandler("StatusUpdate", ["The plugin ${n}-${v} has been uninstalled from the current application."])
                        }
                    },{
                        cleanupPluginInstallAndExit(pluginInstallPath, "Installation of [${fullPluginName}] was canceled.")
                    })
                }
            }
        }

        // Add the plugin's directory to the binding so that any event
        // handlers in the plugin have access to it. Normally, this
        // variable is added in GriffonScriptRunner, but this plugin
        // hasn't been installed by that point.
        pluginDirVariableStore["${GriffonUtil.getPropertyNameForLowerCaseHyphenSeparatedName(pluginName)}PluginDir"] = new File(pluginInstallPath).absoluteFile
        pluginDirVariableStore["${GriffonUtil.getPropertyNameForLowerCaseHyphenSeparatedName(pluginName)}PluginVersion"] = getPluginVersion(new File(pluginInstallPath).name)

        def dependencies = processPluginDependencies(pluginName,pluginXml)

        // if there are any unprocessed dependencies, bail out
        if (dependencies) {
            cleanupPluginInstallAndExit(pluginInstallPath, "Failed to install plugin [${fullPluginName}]. Missing dependencies: ${dependencies.inspect()}")
        } else {
            resolvePluginJarDependencies(fullPluginName, pluginInstallPath)
            def license = pluginXml.license?.text() ?: '<UNKNOWN>'
            println "Plugin license for $fullPluginName is '$license'"

            // proceed _Install.groovy plugin script if exists
            if(!settings.isPluginProject()) {
            def installScript = new File("${pluginInstallPath}/scripts/_Install.groovy")
                runPluginScript(installScript, fullPluginName, "post-install script")
            }

            registerPluginWithMetadata(pluginName, pluginVersion)

            displayNewScripts(fullPluginName, pluginInstallPath)

            postInstall(pluginInstallPath)
            eventHandler("PluginInstalled", fullPluginName)
        }
    }

    private void cleanupPluginInstallAndExit(dir, message) {
        ant.delete(dir: dir, quiet: true, failOnError: false)
        errorHandler(message)
    }

    /**
     * Reads plugin metadata from a plugin zip file and returns a list containing the plugin name, version and
     * XML metadata. Designed for use with Groovy's multiple assignment operator
     *
     * @param zipLocation The zip location
     * @return A list
     */
    List readMetadataFromZip(String zipLocation) {
        try {
            def zipFile = new ZipFile(zipLocation)
            ZipEntry entry = zipFile.entries().find {ZipEntry entry -> entry.name == 'plugin.xml'}
            if (entry) {
                def pluginXml = new XmlSlurper().parse(zipFile.getInputStream(entry))
                def name = pluginXml.'@name'.text()
                def release = pluginXml.'@version'.text()
                return [name, release, pluginXml]
            }

            errorHandler("Plugin $zipLocation is not a valid Griffon plugin. No plugin.xml descriptor found!")
        }
        catch (e) {
            errorHandler("Error reading plugin zip [$zipLocation]. The plugin zip file may be corrupt.")
        }
    }

    /**
     * Checks an existing plugin path to install and returns true if the installation should be aborted or false if it should continue
     * 
     * If an error occurs the errorHandler is invoked.
     * 
     * @param name The plugin name
     * @param version The plugin version
     * @return True if the installation should be aborted
     */
    protected boolean checkExistingPluginInstall(String name, version) {
        Resource currentInstall = pluginSettings.getPluginDirForName(name)
        
        if (currentInstall?.exists()) {
            
            PluginBuildSettings pluginSettings = pluginSettings
            def pluginDir = currentInstall.file.canonicalFile
            def pluginInfo = pluginSettings.getPluginInfo(pluginDir.absolutePath)
            // if the versions are the same no need to continue
            if(version == pluginInfo?.version) return true
            
            if (pluginSettings.isInlinePluginLocation(currentInstall)) {
                errorHandler("The plugin you are trying to install [$name-${version}] is already configured as an inplace plugin in griffon-app/conf/BuildConfig.groovy. You cannot overwrite inplace plugins.");
                return true
            } else if (!isInteractive || commandLineHelper.confirmInput("You currently already have a version of the plugin installed [$pluginDir.name]. Do you want to upgrade this version?")) {
                ant.delete(dir: currentInstall.file)
            } else {
                errorHandler("StatusUpdate", "Plugin $name-$version install aborted")
                return true
            }
        }
        return false
    }

    protected void assertNoExistingInlinePlugin(String name) {
        def pluginReference = settings.config.griffon.plugin.location[name]
        if (pluginReference) {
            errorHandler("""\
Plugin [$name] is aliased as [griffon.plugin.location.$name] to the location [$pluginReference] in griffon-app/conf/BuildConfig.groovy.
You cannot upgrade a plugin that is configured via BuildConfig.groovy, remove the configuration to continue.""");
        }
    }

    protected void displayNewScripts(pluginName, installPath) {
        def providedScripts = new File("${installPath}/scripts").listFiles().findAll { !it.name.startsWith('_') && it.name.endsWith(".groovy")}
        eventHandler("StatusFinal", "Plugin ${pluginName} installed")
        if (providedScripts) {
            println "Plugin provides the following new scripts:"
            println "------------------------------------------"
            providedScripts.each { File file ->
                def scriptName = GriffonUtil.getScriptName(file.name)
                println "griffon ${scriptName}"
            }
        }
    }

    protected void resolvePluginJarDependencies(pluginName, pluginInstallPath) {
        def pluginDependencyDescriptor = new File("$pluginInstallPath/dependencies.groovy")
        if (pluginDependencyDescriptor.exists()) {
            eventHandler "StatusUpdate", "Resolving plugin JAR dependencies"
            def callable = settings.pluginDependencyHandler()
            callable.call(new File("$pluginInstallPath"))
            IvyDependencyManager dependencyManager = settings.dependencyManager
            dependencyManager.resetGriffonPluginsResolver()
            def resolveReport = dependencyManager.resolveDependencies(IvyDependencyManager.RUNTIME_CONFIGURATION)
            if (resolveReport.hasError()) {
                errorHandler("Failed to install plugin [${pluginName}]. Plugin has missing JAR dependencies.")
            } else {
                addJarsToRootLoader resolveReport.allArtifactsReports.localFile        
            }
        }
        def pluginJars = new File("${pluginInstallPath}/lib").listFiles().findAll { it.name.endsWith(".jar")}
        addJarsToRootLoader(pluginJars)
    }

    protected def addJarsToRootLoader(Collection pluginJars) {
        def loader = getClass().classLoader.rootLoader
        for (File jar in pluginJars) {
            loader.addURL(jar.toURI().toURL())
        }
    }

    protected Map processPluginDependencies(String pluginName, GPathResult pluginXml) {
        Map dependencies = [:]
        for (dep in pluginXml.dependencies.plugin) {
            def depName = dep.@name.toString()
            String depVersion = dep.@version.toString()
            dependencies[depName] = depVersion
            def depDirName = GriffonUtil.getScriptName(depName)
            def manager = settings.dependencyManager

            if(manager.isExcludedFromPlugin(pluginName, depDirName)) {
                dependencies.remove(depName)
            } else {
                def depPluginDir = pluginSettings.getPluginDirForName(depName)?.file
                if (depPluginDir?.exists()) {
                    def dependency = readPluginXmlMetadata(depName)
                    def dependencyVersion = dependency.@version.toString()
                    if (GriffonPluginUtils.compareVersions(dependencyVersion, depVersion) < 0) {
                        if(System.getProperty('griffon.plugin.force.updates') == 'true') {
                            installPlugin(depName, depVersion)
                            dependencies.remove(depName)
                        } else {
                            errorHandler("Plugin requires version [$depVersion] of plugin [$depName], but installed version is [${dependencyVersion}]. Please upgrade this plugin and try again.")
                        }
                    } else {
                        dependencies.remove(depName)
                    }
                } else {
                    eventHandler("StatusUpdate", "Plugin dependency [$depName] not found. Attempting to resolve...")
                    // recursively install dependent plugins
                    def upperVersion = GriffonPluginUtils.getUpperVersion(depVersion)
                    def installVersion = upperVersion
                    if (installVersion == '*') {
                        installVersion = settings.defaultPluginSet.contains(depName) ? GriffonUtil.getGriffonVersion() : null
                    }

                    // recurse
                    installPlugin(depName, installVersion)

                    dependencies.remove(depName)
                }
            }
        }
        return dependencies
    }

    protected readPluginXmlMetadata(String pluginDirName) {
        def pluginDir = pluginSettings.getPluginDirForName(pluginDirName)?.file
        new XmlSlurper().parse(new File("${pluginDir}/plugin.xml"))
    }

    private assertGriffonVersionValid(String pluginName, String griffonVersion) {
        if (griffonVersion) {
            if (!GriffonPluginUtils.isValidVersion(GriffonUtil.griffonVersion, griffonVersion)) {
                errorHandler("Plugin $pluginName requires version [${griffonVersion}] of Griffon which your current Griffon installation does not meet (current version is ${GriffonUtil.griffonVersion}). Please try install a different version of the plugin or Griffon.")
            }
        }
    }

    /**
     * Uninstalls a plugin for the given name and optional version
     *
     * @param name The plugin name
     * @param version The version
     */
    void uninstallPlugin(String name, String version = null) {
        try {
            String pluginKey = "plugins.$name"
            metadata.remove(pluginKey)
            metadata.persist()
            def pluginDir
            if(name && version) {
                pluginDir = new File("${applicationPluginsLocation}/$name-$version")
            } else {
                pluginDir = pluginSettings.getPluginDirForName(name)?.file
            }
            if(pluginDir?.exists()) {
                if(!settings.isPluginProject()) {
                    def uninstallScript = new File("${pluginDir}/scripts/_Uninstall.groovy")
                    runPluginScript(uninstallScript, pluginDir.name, "uninstall script")
                }
                if (isNotInlinePluginLocation(pluginDir)) {
                    ant.delete(dir:pluginDir, failonerror:true)
                }
                postUninstall()
                eventHandler "PluginUninstalled", "Uninstalled plugin [${name}]."
            } else {
                errorHandler("No plugin [$name${version ? '-' + version : ''}] installed, cannot uninstall")
            }
        }
        catch (e) {
            e.printStackTrace()
            errorHandler("An error occured installing the plugin [$name${version ? '-' + version : ''}]: ${e.message}")
        }
    }

    /**
     * Registers a plugin name and version as installed according to the plugin metadata
     * @param pluginName the name of the plugin
     * @param pluginVersion the version of the plugin
     */
    void registerPluginWithMetadata(String pluginName, pluginVersion) {
        IvyDependencyManager dependencyManager = settings.getDependencyManager()
        
        // only register in metadata if not specified in BuildConfig.groovy
        if(dependencyManager.metadataRegisteredPluginNames?.contains(pluginName)) {
            metadata['plugins.' + pluginName] = pluginVersion
            metadata.persist()            
        } else {
            if(!dependencyManager.pluginDependencyNames?.contains(pluginName)) {
                metadata['plugins.' + pluginName] = pluginVersion
                metadata.persist()                            
            }
        }
    }

    private void runPluginScript( File scriptFile, fullPluginName, msg ) {
        if(pluginScriptRunner != null) {
            if(pluginScriptRunner.maximumNumberOfParameters < 3) {
                throw new IllegalStateException("The [pluginScriptRunner] closure property must accept at least 3 arguments")
            } else {
                pluginScriptRunner.call(scriptFile, fullPluginName, msg)
            }
        }
    }

    /**
     * Check to see if the plugin directory is in plugins home.
     */
    boolean isNotInlinePluginLocation(File pluginDir) {
      // insure all the directory is in the pluginsHome
      checkPluginPathWithPluginDir(applicationPluginsLocation, pluginDir) ||
              checkPluginPathWithPluginDir(globalPluginsLocation, pluginDir)
    }

    protected postUninstall() {
        // Update the cached dependencies in griffonSettings, and add new jars to the root loader
        resolveDependenciesAgain()
        pluginSettings.clearCache()
        postUninstallEvent?.call()
    }

    protected postInstall(String pluginInstallPath) {
        // Update the cached dependencies in griffonSettings, and add new jars to the root loader
        pluginSettings.clearCache()
        postInstallEvent?.call(pluginInstallPath)
    }

    private checkPluginPathWithPluginDir (File pluginsHome, File pluginDir) {
        def absPluginsHome = pluginsHome.absolutePath
        pluginDir.absolutePath.startsWith(absPluginsHome)
    }

    private void resolveDependenciesAgain() {
        for (type in ['compile', 'build', 'test', 'runtime']) {
            def existing = settings."${type}Dependencies"
            def all = settings.dependencyManager.resolveDependencies(IvyDependencyManager."${type.toUpperCase()}_CONFIGURATION").allArtifactsReports.localFile
            def toAdd = all - existing
            if (toAdd) {
                existing.addAll(toAdd)
                if (type in ['build', 'test']) {
                    toAdd.each {
                        settings.rootLoader.addURL(it.toURL())
                    }
                }
            }
        }
    }

    protected void checkPluginsToUninstall(List<DependencyDescriptor> pluginDeps) {
        // Find out which plugins are in the search path but not in the
        // metadata. We only check on the plugins in the project's "plugins"
        // directory and the global "plugins" dir. Plugins loaded via an
        // explicit path should be left alone.
        def pluginDirs = pluginSettings.implicitPluginDirectories
        def pluginsToUninstall = pluginDirs.findAll { Resource r ->
            !pluginDeps.find {  ModuleRevisionId plugin ->
                r.filename ==~ "$plugin.name-.+"
            }
        }

        for (Resource pluginDir in pluginsToUninstall) {
            if (pluginSettings.isGlobalPluginLocation(pluginDir)) {
                registerMetadataForPluginLocation(pluginDir)
            } else {
                if (!isInteractive || commandLineHelper.confirmInput("Plugin [${pluginDir.filename}] is installed, but was not found in the application's metadata, do you want to uninstall?")) {
                    uninstallPlugin(pluginDir.filename)
                } else {
                    registerMetadataForPluginLocation(pluginDir)
                }
            }
        }
    }

    protected List<ModuleRevisionId> findMissingOrUpgradePlugins(List pluginDeps) {
        def pluginsToInstall = []
        for (p in pluginDeps) {
            def name = p.name
            def version = p.revision
            def fullName = "$name-$version"
            def pluginLoc = pluginSettings.getPluginDirForName(name)
            if (!pluginLoc?.exists()) {
                eventHandler "StatusUpdate", "Plugin [${fullName}] not installed."
                pluginsToInstall << p
            } else if (pluginLoc) {
                def dirName = pluginLoc.file.canonicalFile.name
                PluginBuildSettings settings = pluginSettings
                if (!dirName.endsWith(version) && !settings.isInlinePluginLocation(pluginLoc)) {
                    // only print message if the version doesn't start with "latest." since we have
                    // to do a check for a new version when there version is specified as "latest.integration" etc.
                    if(!version.startsWith("latest."))
                        eventHandler "StatusUpdate", "Upgrading plugin [$dirName] to [${fullName}]."
                    pluginsToInstall << p
                }
            }

        }
        return pluginsToInstall
    }

    private registerMetadataForPluginLocation(Resource pluginDir) {
        def plugin = pluginSettings.getMetadataForPlugin(pluginDir.filename)
        registerPluginWithMetadata(plugin.@name.text(), plugin.@version.text())
    }
}
