import org.codehaus.griffon.util.GriffonNameUtils
import org.codehaus.griffon.util.GriffonUtil
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory
import java.util.regex.Matcher
//import org.apache.xml.serialize.OutputFormat
//import org.apache.xml.serialize.XMLSerializer
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.griffon.commons.DefaultGriffonContext
import org.codehaus.griffon.plugins.DefaultGriffonPluginManager
import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.codehaus.griffon.plugins.PluginManagerHolder
import org.springframework.core.io.Resource

import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.Transformer
import javax.xml.transform.Result
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource
import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import java.util.zip.ZipEntry

/*
* Copyright 2004-2005 the original author or authors.
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
 * Plugin stuff. If included, must be included after "_ClasspathAndEvents".
 *
 * @author Graeme Rocher
 *
 * @since 1.1
 */

includeTargets << griffonScript("_GriffonClean")
includeTargets << griffonScript("_GriffonProxy")

DEFAULT_PLUGIN_DIST = "http://svn.codehaus.org/griffon/plugins"
DEFAULT_PUBLISH_URL = "https://svn.codehaus.org/griffon/plugins"

// Properties
pluginsList = null
//indentingOutputFormat = new OutputFormat("XML", "UTF-8", true)
globalInstall = false
pluginsBase = "${griffonWorkDir}/plugins".toString().replaceAll('\\\\','/')
pluginDiscoveryRepositories = griffonSettings?.config?.griffon?.plugin?.repos?.discovery ?: Collections.emptyMap()
pluginDistributionRepositories = griffonSettings?.config?.griffon?.plugin?.repos?.distribution ?: Collections.emptyMap()
installedPlugins = [] // a list of plugins that have been installed


configureRepository =  { targetRepoURL, String alias = "default" ->
  repositoryName = alias
  pluginsList = null
  pluginsListFile = new File(griffonSettings.griffonWorkDir, "plugins-list-${alias}.xml")

  def namedPluginSVN = pluginDistributionRepositories.find { it.key == alias }?.value
  if(namedPluginSVN) {
    pluginSVN = namedPluginSVN
  }
  else {
    pluginSVN = DEFAULT_PUBLISH_URL
  }
  pluginDistURL = targetRepoURL
  pluginBinaryDistURL = "$targetRepoURL/dist"
  remotePluginList = "$targetRepoURL/.plugin-meta/plugins-list.xml"
}

configureRepository(DEFAULT_PLUGIN_DIST)

configureRepositoryForName = { String targetRepository, type="discovery" ->
    // Works around a bug in Groovy 1.5.6's DOMCategory that means get on Object returns null. Change to "pluginDiscoveryRepositories.targetRepository" when upgrading
    def targetRepoURL = pluginDiscoveryRepositories.find { it.key == targetRepository }?.value

    if(targetRepoURL) {
      configureRepository(targetRepoURL, targetRepository)
    }
    else {
      println "No repository configured for name ${targetRepository}. Set the 'griffon.plugin.repos.${type}.${targetRepository}' variable to the location of the repository."
      exit(1)
    }
}

eachRepository =  { Closure callable ->

    for(entry in pluginDiscoveryRepositories) {

       configureRepositoryForName(entry.key)
       updatePluginsList()
       if(!callable(entry.key, entry.value)) {
         break
       }
    }
}
// Targets
target(resolveDependencies:"Resolve plug-in dependencies") {
    def plugins = metadata.findAll { it.key.startsWith("plugins.")}.collect {
       [
        name:it.key[8..-1],
        version: it.value
       ]
    }
    boolean installedPlugins = false


    for(p in plugins) {
        def name = p.name
        def version = p.version
        def fullName = "$name-$version"
        def pluginLoc = getPluginDirForName(name)
        if(!pluginLoc?.exists()) {
            println "Plugin [${fullName}] not installed, resolving.."

            cacheKnownPlugin(name, version)
            installPluginForName(fullName)
            installedPlugins = true
        }
    }
    if(installedPlugins) {
        resetClasspathAndState()
    }

    def pluginDirs = GriffonPluginUtils.getPluginDirectories(pluginsHome)
    def pluginsToUninstall = pluginDirs.findAll { Resource r -> !plugins.find { plugin -> r.filename == "$plugin.name-$plugin.version" }}

    for(Resource pluginDir in pluginsToUninstall) {
        if(confirmInput("Plugin [${pluginDir.filename}] is installed, but was not found in the application's metadata, do you want to uninstall?") == 'y') {
            uninstallPluginForName(pluginDir.filename)
        }
        else {
            def plugin = GriffonPluginUtils.getMetadataForPlugin(pluginDir.filename)
            registerPluginWithMetadata(plugin.@name.text(), plugin.@version.text())
        }
    }
}
target(loadPlugins:"Loads Griffon' plugins") {
    if(!PluginManagerHolder.pluginManager) { // plugin manager already loaded?
		compConfig.setTargetDirectory(classesDir)
	    def unit = new CompilationUnit ( compConfig , null , new GroovyClassLoader(classLoader) )
		def pluginFiles = getPluginDescriptors()

        for(plugin in pluginFiles) {
            def pluginFile = plugin.file
            def className = pluginFile.name - '.groovy'
	        def classFile = new File("${classesDirPath}/${className}.class")

            if(pluginFile.lastModified() > classFile.lastModified())
	              unit.addSource ( pluginFile )
		}

        try {
            profile("compiling plugins") {
	    		unit.compile ()
			}
			def application
            def pluginClasses = []
            profile("construct plugin manager with ${pluginFiles.inspect()}") {
				for(plugin in pluginFiles) {
				   def className = plugin.file.name - '.groovy'
	               pluginClasses << classLoader.loadClass(className)
				}

                profile("creating plugin manager with classes ${pluginClasses}") {
                    if(griffonContext == null) {
                        griffonContext = new DefaultGriffonContext(new Class[0], new GroovyClassLoader(classLoader))
                    }
                    pluginManager = new DefaultGriffonPluginManager(pluginClasses as Class[], griffonContext)

                    PluginManagerHolder.setPluginManager(pluginManager)
                }
	        }
	        profile("loading plugins") {
				event("PluginLoadStart", [pluginManager])
	            pluginManager.loadPlugins()


                def loadedPlugins = pluginManager.allPlugins?.findAll { pluginClasses.contains(it.instance.getClass()) }*.name
                if(loadedPlugins)
                    event("StatusUpdate", ["Loading with installed plug-ins: ${loadedPlugins}"])

                if(pluginManager.failedLoadPlugins) {
                    event("StatusError", ["Error: The following plug-ins failed to load due to missing dependencies: ${pluginManager.failedLoadPlugins*.name}"])
                    for(p in pluginManager.failedLoadPlugins) {
                        println "- Plugin: $p.name, Dependencies: $p.dependencyNames"
                    }
                    exit(1)
                }

                //pluginManager.doArtefactConfiguration()
                griffonContext.initialise()
                event("PluginLoadEnd", [pluginManager])
            }
	    }
        catch (Exception e) {
            GriffonUtil.deepSanitize(e).printStackTrace()
            event("StatusFinal", [ "Error loading plugin manager: " + e.message ])
			exit(1)
	    }
    }
    else {
        // Add the plugin manager to the binding so that it can be accessed
        // from any target.
        pluginManager = PluginManagerHolder.pluginManager
    }
}

target(updatePluginsList:"Updates the plug-in list from the remote plugin-list.xml") {
    depends(configureProxy)
    try {
        println "Reading remote plug-in list ..."
        if(!pluginsListFile.exists())
            readRemotePluginList()

        parsePluginList()
        def localRevision = pluginsList ? new Integer(pluginsList.getAttribute('revision')) : -1
        // extract plugins svn repository revision - used for determining cache up-to-date
        def remoteRevision = 0
        new URL(remotePluginList).withReader { Reader reader ->
            reader.readLine() // skip the first line as it will be the xml dec
            def line = reader.readLine()
            Matcher matcher = line =~ /<plugins revision="(\d+?)">/
            if(matcher) {
                 remoteRevision = matcher.group(1).toInteger()
            }

        }
        profile("Updating plugin list if remote list [$remoteRevision] is newer than local ${localRevision}") {
            if (remoteRevision > localRevision) {
                println "Plugin list out-of-date, retrieving.."
                readRemotePluginList()
            }
        }

    } catch (Exception e) {
        println "Error reading remote plugin list [${e.message}], building locally..."
        updatePluginsListManually()
    }
}


def resetClasspathAndState() {
    GriffonPluginUtils.clearCaches()
    classpathSet = false
    classpath()
    PluginManagerHolder.pluginManager = null
}
def parsePluginList() {
    if(pluginsList == null) {
        profile("Reading local plugin list from $pluginsListFile") {
            def document
            try {
                document = DOMBuilder.parse(new FileReader(pluginsListFile))
            } catch (Exception e) {
                println "Plugin list file corrupt, retrieving again.."
                readRemotePluginList()
                document = DOMBuilder.parse(new FileReader(pluginsListFile))
            }
            pluginsList = document.documentElement
        }
    }
}

def readRemotePluginList() {
    ant.delete(file:pluginsListFile, failonerror:false)
    ant.mkdir(dir:pluginsListFile.parentFile)
    ant.get(src: remotePluginList, dest: pluginsListFile, verbose: "yes")
}

target(updatePluginsListManually: "Updates the plugin list by manually reading each URL, the slow way") {
    depends(configureProxy)
    try {
        def recreateCache = false
        if (!pluginsListFile.exists()) {
            println "Plugins list cache doesn't exist creating.."
            recreateCache = true
        }
        try {
            document = DOMBuilder.parse(new FileReader(pluginsListFile))
        } catch (Exception e) {
            recreateCache = true
            println "Plugins list cache is corrupt [${e.message}]. Re-creating.."
        }
        if (recreateCache) {
            document = DOMBuilder.newInstance().createDocument()
            def root = document.createElement('plugins')
            root.setAttribute('revision', '0')
            document.appendChild(root)
        }

        pluginsList = document.documentElement
        builder = new DOMBuilder(document)

        def localRevision = pluginsList ? new Integer(pluginsList.getAttribute('revision')) : -1
        // extract plugins svn repository revision - used for determining cache up-to-date
        def remoteRevision = 0
        new URL(pluginDistURL).withReader { Reader reader ->
            def line = reader.readLine()
            line.eachMatch(/Revision (.*):/) {
                 remoteRevision = it[1].toInteger()
            }


            if (remoteRevision > localRevision) {
            //if(true) {
                // Plugins list cache is expired, need to update
                event("StatusUpdate", ["Plugins list cache has expired. Updating, please wait"])
                pluginsList.setAttribute('revision', remoteRevision as String)
                // for each plugin directory under Griffon Plugins SVN in form of 'griffon-*'
                while(line=reader.readLine()) {
                    line.eachMatch(/<li><a href="griffon-(.+?)">/) {
                        // extract plugin name
                        def pluginName = it[1][0..-2]


                        // collect information about plugin
                        buildPluginInfo(pluginsList, pluginName)
                    }
                }
            }
        }

//        try {
//          // proceed binary distribution repository (http://plugins.griffon.org/dist/
//          def binaryPluginsList = new URL(pluginBinaryDistURL).text
//          binaryPluginsList.eachMatch(/<a href="griffon-(.+?).zip">/) {
//              buildBinaryPluginInfo(pluginsList, it[1])
//          }
//        }
//        catch(Exception e) {
//           // ignore, binary distributions are supported for backwards compatibility only, so this is ok
//        }
        // update plugins list cache file
        writePluginsFile()
    } catch (Exception e) {
        event("StatusError", ["Unable to list plug-ins, please check you have a valid internet connection: ${e.message}" ])
    }
}

// Utility Closures

/**
 * Reads a plugin.xml descriptor for the given plugin name
 */
readPluginXmlMetadata = { String pluginName ->
    def pluginDir = getPluginDirForName(pluginName)?.file
    new XmlSlurper().parse(new File("${pluginDir}/plugin.xml"))
}

/**
 * Reads all installed plugin descriptors returning a list
 */
readAllPluginXmlMetadata = {->
    getPluginXmlMetadata().collect { new XmlSlurper().parse(it.file) }
}

getPluginXmlMetadata = {
    GriffonPluginUtils.getPluginXmlMetadata(pluginsHome, resolveResources)
}
/**
 * Obtains the directory for the given plugin name
 */
getPluginDirForName = { String pluginName ->
    GriffonPluginUtils.getPluginDirForName(pluginsHome, pluginName)
}
/** Obtains all of the plugin directories */
getPluginDirectories = {->
    GriffonPluginUtils.getPluginDirectories(pluginsHome)
}
/**
 * Obtains an array of all plug-in source files as Spring Resource objects
 */
getPluginSourceFiles = {
    GriffonPluginUtils.getPluginSourceFiles(pluginsHome, resolveResources)
}
/**
 * Obtains an array of all the plug-in provides Gant scripts
 */
getPluginScripts = {
    GriffonPluginUtils.getPluginScripts(pluginsHome,resolveResources)
}
/**
 * Gets a list of all scripts known to the application (excluding private scripts starting with _)
 */
getAllScripts = {
    GriffonPluginUtils.getAvailableScripts(griffonHome, pluginsHome, basedir, griffonSettings.griffonWorkDir.path, resolveResources)
}
/**
 * Obtains a list of all Griffon plug-in descriptor classes
 */
getPluginDescriptors = {
    GriffonPluginUtils.getPluginDescriptors(basedir,pluginsHome,resolveResources)
}
/**
 * Gets the base plugin descriptor
 */
getBasePluginDescriptor = {
    GriffonPluginUtils.getBasePluginDescriptor(basedir)
}
/**
 * Runs a script contained within a plugin
 */
runPluginScript = { File scriptFile, fullPluginName, msg ->
    if (scriptFile.exists()) {
        event("StatusUpdate", ["Executing ${fullPluginName} plugin $msg"])
        // instrumenting plugin scripts adding 'pluginBasedir' variable
        def instrumentedInstallScript = "def pluginBasedir = '${pluginsHome}/${fullPluginName}'\n".toString().replaceAll('\\\\','/') + scriptFile.text
        // we are using text form of script here to prevent Gant caching
        includeTargets << instrumentedInstallScript
    }
}
// TODO review usage on installer-plugin
pluginScript = { pluginName, scriptName ->
   def pluginHome = getPluginDirForName(pluginName).file
   def scriptFile = new File(pluginHome,"/scripts/${scriptName}.groovy")
   return scriptFile.exists() ? scriptFile : null
}

// this is one is better than the previous as it will only include
// the script if the plugin is available. Works on application
// and plugin project
includePluginScript = { pluginName, scriptName ->
   def pluginHome = getPluginDirForName(pluginName)?.file
   if(!pluginHome) return
   def scriptFile = new File(pluginHome,"/scripts/${scriptName}.groovy")
   if(scriptFile.exists()) inclueTargets << scriptFile
}

/**
 * Downloads a remote plug-in zip into the plugins dir
 */
downloadRemotePlugin = { url, pluginsBase ->
    def slash = url.file.lastIndexOf('/')
    def fullPluginName = "${url.file[slash + 8..-5]}"
    ant.get(dest: "${pluginsBase}/griffon-${fullPluginName}.zip",
            src: "${url}",
            verbose: true,
            usetimestamp: true)

    return fullPluginName
}


/**
 * Caches a local plugin into the plugins directory
 */
cacheLocalPlugin = { pluginFile ->
    fullPluginName = "${pluginFile.name[8..-5]}"
    String zipLocation = "${pluginsBase}/griffon-${fullPluginName}.zip"
    ant.copy(file: pluginFile, tofile: zipLocation)
    readMetadataFromZip(zipLocation, pluginFile)


}

private readMetadataFromZip(String zipLocation, pluginFile) {
    def zipFile = new ZipFile(zipLocation)

    ZipEntry entry = zipFile.entries().find {ZipEntry entry -> entry.name == 'plugin.xml'}

    if (entry) {
        pluginXml = new XmlSlurper().parse(zipFile.getInputStream(entry))
        currentPluginName = pluginXml.'@name'.text()
        currentPluginRelease = pluginXml.'@version'.text()
        fullPluginName = "$currentPluginName-$currentPluginRelease"
    }
    else {
        cleanupPluginInstallAndExit("Plug-in $pluginFile is not a valid Grails plugin. No plugin.xml descriptor found!")
    }
}

/**
 * Searches the downloaded plugin-list.xml files for each repository for a plugin that matches the given name
 */
findPlugin =  { pluginName ->
  pluginName = pluginName?.toLowerCase()

  if(pluginName) {
    def plugin = pluginsList.'plugin'.find { it.'@name' == pluginName }

    if(!plugin) {
       eachRepository { name, url ->
          plugin = pluginsList.'plugin'.find { it.'@name' == pluginName }
          (!plugin)
       }
    }
    return plugin
  }
}
/**
 * Stores a plug-in from the plug-in central repo into the local plugin cache
 */
cacheKnownPlugin = { String pluginName, String pluginRelease ->
    if(!pluginsList) {
        updatePluginsList()
    }
    def pluginDistName
    def plugin
    def fullPluginName
    try {
      use(DOMCategory) {
          plugin = findPlugin(pluginName)

          if (plugin) {
              pluginRelease = pluginRelease ? pluginRelease : plugin.'@latest-release'
              if (pluginRelease) {
                  def release = plugin.'release'.find {rel -> rel.'@version' == pluginRelease }
                  if (release) {
                      pluginDistName = release.'file'.text()
                  } else {
                      cleanupPluginInstallAndExit("Release ${pluginRelease} was not found for this plugin. Type 'griffon plugin-info ${pluginName}'")
                  }
              } else {
                  cleanupPluginInstallAndExit("Latest release information is not available for plugin '${pluginName}', specify concrete release to install")
              }
          } else {
              cleanupPluginInstallAndExit("Plugin '${pluginName}' was not found in repository. If it is not stored in a configured repository you will need to install it manually. Type 'griffon list-plugins' to find out what plugins are available.")
          }


          def pluginCacheFileName = "${pluginsBase}/griffon-${plugin.'@name'}-${pluginRelease}.zip"
          if (!new File(pluginCacheFileName).exists() || pluginRelease.endsWith("SNAPSHOT")) {
              ant.mkdir(dir:pluginsBase)
              ant.get(dest: pluginCacheFileName,
                      src: "${pluginDistName}",
                      verbose: true)
          }
          fullPluginName = "$pluginName-$pluginRelease"

          ant.copy(file:"${pluginsBase}/griffon-${fullPluginName}.zip",tofile:"${pluginsHome}/griffon-${fullPluginName}.zip")
      }
      return fullPluginName
    }
    finally {
       configureRepository(DEFAULT_PLUGIN_DIST)
    }
}


cleanupPluginInstallAndExit = { message ->
  event("StatusError", [message])
  for(pluginDir in installedPlugins) {
    ant.delete(dir:pluginDir, failonerror:false)
  }
  exit(1)
}

/**
 * Uninstalls a plugin for the given name and version
 */
uninstallPluginForName = { name, version=null ->

    String pluginKey = "plugins.$name"
    metadata.remove(pluginKey)
    metadataFile.withOutputStream { out ->
        metadata.store out,'utf-8'
    }


    def pluginDir
    if(name && version) {
        pluginDir = new File("${pluginsHome}/$name-$version")
    }
    else {
        pluginDir = getPluginDirForName(name)?.file
    }
    if(pluginDir?.exists()) {

        def uninstallScript = new File("${pluginDir}/scripts/_Uninstall.groovy")
        runPluginScript(uninstallScript, pluginDir.name, "uninstall script")

        ant.delete(dir:pluginDir, failonerror:true)
        resetClasspathAndState()
    }
    else {
        event("StatusError", ["No plug-in [$name${version ? '-' + version : ''}] installed, cannot uninstall"])
    }
}
/**
 * Installs a plug-in for the given name and optional version
 */
installPluginForName = { String fullPluginName ->

    if (fullPluginName) {
        event("InstallPluginStart", [fullPluginName])
        def pluginInstallPath = "${globalInstall ? globalPluginsDirPath : pluginsHome}/${fullPluginName}"
        installedPlugins << pluginInstallPath
        ant.delete(dir: pluginInstallPath, failonerror: false)
        ant.mkdir(dir: pluginInstallPath)
        ant.unzip(dest: pluginInstallPath, src: "${pluginsBase}/griffon-${fullPluginName}.zip")


        def pluginXmlFile = new File("${pluginInstallPath}/plugin.xml")
        if (!pluginXmlFile.exists()) {
            ant.fail("Plug-in $fullPluginName is not a valid Griffon plug-in. No plugin.xml descriptor found!")
        }
        def pluginXml = new XmlSlurper().parse(pluginXmlFile)
        def pluginName = pluginXml.@name.toString()
        def pluginVersion = pluginXml.@version.toString()
        // Add the plugin's directory to the binding so that any event
        // handlers in the plugin have access to it. Normally, this
        // variable is added in GriffonScriptRunner, but this plugin
        // hasn't been installed by that point.
        binding.setVariable("${pluginName}PluginDir", new File(pluginInstallPath).absoluteFile)

        def pluginJdkVersion = pluginXml.@jdk?.toString()
        if (pluginJdkVersion) {
            pluginJdkVersion = new BigDecimal(pluginJdkVersion)
            def javaVersion = new BigDecimal(System.getProperty("java.version")[0..2])
            if (pluginJdkVersion > javaVersion) {
                ant.delete(dir: "${pluginsDirPath}/${fullPluginName}", quiet: true, failOnError: false)
                clean()

                pluginInstallFail("Failed to install plug-in [${fullPluginName}]. Required Jdk version is ${pluginJdkVersion}, current one is ${javaVersion}", [name:pluginName])
            }
        }

        def dependencies = [:]

        for (dep in pluginXml.dependencies.plugin) {
            def depName = dep.@name.toString()
            String depVersion = dep.@version.toString()
//            if(isCorePlugin(depName)) {
//                def griffonVersion = GriffonUtil.getGriffonVersion()
//                if(!GriffonPluginUtils.isValidVersion(depVersion, griffonVersion))
//                    cleanupPluginInstallAndExit("Plug-in requires version [$depVersion] of Griffon core, but installed version is [${griffonVersion}]. Please upgrade your Griffon installation and try again.")
//            }
//            else {
                dependencies[depName] = depVersion
                def depPluginDir = getPluginDirForName(depName)?.file
                if (!depPluginDir?.exists()) {
                    event("StatusUpdate", ["Plugin dependency [$depName] not found. Attempting to resolve..."])
                    // recursively install dependent plug-ins
                    def upperVersion =  GriffonPluginUtils.getUpperVersion(depVersion)
                    def release = cacheKnownPlugin(depName, upperVersion == '*' ? null : upperVersion)

                    ant.copy(file:"${pluginsBase}/griffon-${release}.zip",tofile:"${pluginsDirPath}/griffon-${release}.zip")

                    installPluginForName(release)
                    dependencies.remove(depName)
                }
                else  {
                    def dependency = readPluginXmlMetadata(depName?.toString())
                    if (!GriffonPluginUtils.isValidVersion(dependency.@version.toString(), depVersion)) {
                        cleanupPluginInstallAndExit("Plug-in requires version [$depVersion] of plug-in [$depName], but installed version is [${dependency.version}]. Please upgrade this plug-in and try again.")
                    }
                    else {
                        dependencies.remove(depName)
                    }
                }
            }
//        }

        if (dependencies) {
            ant.delete(dir: "${pluginsHome}/${fullPluginName}", quiet: true, failOnError: false)
            clean()

            cleanupPluginInstallAndExit("Failed to install plug-in [${fullPluginName}]. Missing dependencies: ${dependencies.inspect()}")

        }
        else {
            def pluginJars = resolveResources("file:${pluginsHome}/${fullPluginName}/lib/*.jar")
            for(jar in pluginJars) {
                rootLoader.addURL(jar.URL)
            }
            // proceed _Install.groovy plugin script if exists
            def installScript = new File("${pluginsHome}/${fullPluginName}/scripts/_Install.groovy")
            runPluginScript(installScript, fullPluginName, "post-install script")

            registerPluginWithMetadata(pluginName, pluginVersion)


            def providedScripts = resolveResources("file:${pluginsHome}/${fullPluginName}/scripts/*.groovy").findAll { !it.filename.startsWith('_')}
            event("StatusFinal", ["Plugin ${fullPluginName} installed"])
            if (providedScripts) {
                println "Plug-in provides the following new scripts:"
                println "------------------------------------------"
                providedScripts.file.each {file ->
                    def scriptName = GriffonNameUtils.getScriptName(file.name)
                    println "griffon ${scriptName}"
                }
            }

            File pluginEvents = new File("${pluginsDirPath}/${fullPluginName}/scripts/_Events.groovy")
            if (pluginEvents.exists()) {
                println "Found events script in plugin ${pluginName}"
                loadEventScript(pluginEvents)
            }

            event("PluginInstalled", [fullPluginName])
        }

    }
}

def registerPluginWithMetadata(String pluginName, pluginVersion) {
    metadata['plugins.' + pluginName] = pluginVersion
    metadataFile.withOutputStream {out ->
        metadata.store out, 'utf-8'
    }
}


def buildReleaseInfo(root, pluginName, releasePath, releaseTag ) {
    if (releaseTag == '..' || releaseTag == 'LATEST_RELEASE') return
    def releaseNode = root.'release'.find {it.'@tag' == releaseTag && it.'&type' == 'svn'}
    if (releaseNode) {
        if (releaseTag != 'trunk') return
        else root.removeChild(releaseNode)
    }
    try {
        def properties = ['title', 'author', 'authorEmail', 'description', 'documentation']
        def releaseDescriptor = parseRemoteXML("${releasePath}/${releaseTag}/plugin.xml").documentElement
        def version = releaseDescriptor.'@version'
        if (releaseTag == 'trunk' && !(version.endsWith('SNAPSHOT'))) return
        def releaseContent = new URL("${releasePath}/${releaseTag}/").text
        // we don't want to proceed release if zip distribution for this release is not published
        if (releaseContent.indexOf("griffon-${pluginName}-${version}.zip") < 0) return
        releaseNode = builder.createNode('release', [tag: releaseTag, version: version, type: 'svn'])
        root.appendChild(releaseNode)
        properties.each {
            if (releaseDescriptor."${it}") {
                releaseNode.appendChild(builder.createNode(it, releaseDescriptor."${it}".text()))
            }
        }
        releaseNode.appendChild(builder.createNode('file', "${releasePath}/${releaseTag}/griffon-${pluginName}-${version}.zip"))
    } catch (Exception e) {
        // no plugin release info available
    }
}



def writePluginsFile() {
    pluginsListFile.getParentFile().mkdirs()
//    XMLSerializer serializer = new XMLSerializer(new FileWriter(pluginsListFile), indentingOutputFormat);
//    serializer.serialize(document)
    TransformerFactory tFactory = TransformerFactory.newInstance();
    tFactory.setAttribute("indent-number", 4);
    Transformer transformer = tFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(pluginsListFile),"UTF-8"));
    transformer.transform(new DOMSource(document), result);
}

def parseRemoteXML(url) {
    DOMBuilder.parse(new URL(url).openStream().newReader())
}



def buildBinaryPluginInfo(root, pluginName ){
    // split plugin name in form of 'plugin-name-0.1' to name ('plugin-name') and version ('0.1')
    def matcher = (pluginName =~ /^([^\d]+)-(.++)/)
    // convert to new plugin naming convention (MyPlugin -> my-plugin)
    def name = GriffonNameUtils.getScriptName(matcher[0][1])
    def release = matcher[0][2]
    use(DOMCategory) {
        def pluginNode = root.'plugin'.find {it.'@name' == name}
        if (!pluginNode) {
            pluginNode = builder.'plugin'(name: name)
            root.appendChild(pluginNode)
        }
        def releaseNode = pluginNode.'release'.find {it.'@version' == release && it.'@type' == 'zip'}
        // SVN releases have higher precedence than binary releases
        if (pluginNode.'release'.find {it.'@version' == release && it.'@type' == 'svn'}) {
            if (releaseNode) pluginNode.removeChild(releaseNode)
            return
        }
//        if (!releaseNode) {
//            releaseNode = builder.'release'(type: 'zip', version: release) {
//                title("This is a zip release, no info available for it")
//                file("${pluginBinaryDistURL}/griffon-${pluginName}.zip")
//            }
//            pluginNode.appendChild(releaseNode)
//        }
    }
}


def buildPluginInfo(root, pluginName) {
    use(DOMCategory) {
        def pluginNode = root.'plugin'.find {it.'@name' == pluginName}
        if (!pluginNode) {
            pluginNode = builder.'plugin'(name: pluginName)
            root.appendChild(pluginNode)
        }

        def localRelease = pluginNode.'@latest-release'
        def latestRelease = null
        try {
            new URL("${pluginDistURL}/griffon-${pluginName}/tags/LATEST_RELEASE/plugin.xml").withReader {Reader reader ->
                def line = reader.readLine()
                line.eachMatch (/.+?version='(.+?)'.+/) {
                    latestRelease = it[1]
                }
            }
        } catch (Exception e) {
            // ignore
        }

        if(!localRelease || !latestRelease || localRelease != latestRelease) {

            event("StatusUpdate", ["Reading [$pluginName] plug-in info"])

            // proceed tagged releases
            try {
                def releaseTagsList = new URL("${pluginDistURL}/griffon-${pluginName}/tags/").text
                releaseTagsList.eachMatch(/<li><a href="(.+?)">/) {
                    def releaseTag = it[1][0..-2]
                    buildReleaseInfo(pluginNode, pluginName, "${pluginDistURL}/griffon-${pluginName}/tags", releaseTag)
                }
            } catch (Exception e) {
                // no plugin release info available
            }

            // proceed trunk release
            try {
                buildReleaseInfo(pluginNode, pluginName, "${pluginDistURL}/griffon-${pluginName}", "trunk")
            } catch (Exception e) {
                // no plugin release info available
            }

            if (latestRelease && pluginNode.'release'.find {it.'@version' == latestRelease}) pluginNode.setAttribute('latest-release', latestRelease as String)
        }
    }
}

//// TODO: temporary until we refactor Griffon core into real plugins
//CORE_PLUGINS = ['core', 'i18n','converters','mimeTypes', 'hibernate','controllers','webflow', 'dataSource', 'domainClass', 'filters']
//boolean isCorePlugin(name) {
//    CORE_PLUGINS.contains(name)
//}
