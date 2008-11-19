/*
* Copyright 2004-2009 the original author or authors.
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
import org.codehaus.griffon.commons.GriffonClassUtils as GCU

import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory
//import org.apache.xml.serialize.OutputFormat
//import org.apache.xml.serialize.XMLSerializer
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.griffon.commons.DefaultGriffonContext
import org.codehaus.griffon.plugins.DefaultGriffonPluginManager
import org.codehaus.griffon.plugins.GriffonPluginUtils
import org.codehaus.griffon.plugins.PluginManagerHolder
import java.util.regex.Matcher
import org.codehaus.griffon.commons.GriffonUtil
import javax.xml.transform.TransformerFactory
import javax.xml.transform.Transformer
import javax.xml.transform.Result
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource


/**
 * Plugin stuff. If included, must be included after "_ClasspathAndEvents".
 *
 * @author Graeme Rocher
 *
 * @since 1.1
 */

includeTargets << griffonScript("_GriffonEvents")
includeTargets << griffonScript("_GriffonProxy")

DEFAULT_PLUGIN_DIST = "http://svn.codehaus.org/griffon/plugins"
//DEFAULT_PLUGIN_DIST = new File("C:/svn/codehaus.org/griffon/plugins").toURI().toASCIIString()
BINARY_PLUGIN_DIST = "http://plugins.griffon.org/dist"
//BINARY_PLUGIN_DIST = "${DEFAULT_PLUGIN_DIST}/dist"
REMOTE_PLUGIN_LIST = "${DEFAULT_PLUGIN_DIST}/.plugin-meta/plugins-list.xml"

// Properties
pluginsListFile = new File("${pluginsHome}/plugins-list.xml")
pluginsList = null
//indentingOutputFormat = new OutputFormat("XML", "UTF-8", true)
globalInstall = false
pluginsBase = "${griffonWorkDir}/plugins".toString().replaceAll('\\\\','/')


// Targets
target(resolveDependencies:"Resolve plug-in dependencies") {
    def plugins = metadata.findAll { it.key.startsWith("plugins.")}
    boolean installedPlugins = false
    for(p in plugins) {
        def name = p.key[8..-1]
        def version = p.value
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
        classpathSet = false
        classpath()
        at PluginManagerHolder.pluginManager = null
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
                    if(griffonApp == null) {
                        griffonApp = new DefaultGriffonContext(new Class[0], new GroovyClassLoader(classLoader))
                    }
                    pluginManager = new DefaultGriffonPluginManager(pluginClasses as Class[], griffonApp)

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
                griffonApp.initialise()
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
    try {
        println "Reading remote plug-in list ..."
        println pluginsListFile
        if(!pluginsListFile.exists())
            readRemotePluginList()

        parsePluginList()
        def localRevision = pluginsList ? new Integer(pluginsList.getAttribute('revision')) : -1
        // extract plugins svn repository revision - used for determining cache up-to-date
        def remoteRevision = 0
        new URL(REMOTE_PLUGIN_LIST).withReader { Reader reader ->
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
        GriffonUtil.deepSanitize(e)
        e.printStackTrace()
        println "Error reading remote plugin list [${e.message}], building locally..."
        updatePluginsListManually()
    }
}

def parsePluginList() {
    if(pluginsList == null) {
        profile("Reading local plugin list from $pluginsListFile") {
            try {
                document = DOMBuilder.parse(new FileReader(pluginsListFile))
            } catch (Exception e) {
                println "Plugin list file corrupt, retrieving again.."
                readRemotePluginList()
            }
            pluginsList = document.documentElement
        }
    }
}

def readRemotePluginList() {
    ant.delete(file:pluginsListFile, failonerror:false)
    ant.mkdir(dir:pluginsListFile.parentFile)
    ant.get(src: REMOTE_PLUGIN_LIST, dest: pluginsListFile, verbose: "yes")
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
        new URL(DEFAULT_PLUGIN_DIST).withReader { Reader reader ->
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

        // proceed binary distribution repository (http://plugins.griffon.org/dist/
        def binaryPluginsList = new URL(BINARY_PLUGIN_DIST).text
        binaryPluginsList.eachMatch(/<a href="griffon-(.+?).zip">/) {
            buildBinaryPluginInfo(pluginsList, it[1])
        }
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
    GriffonPluginUtils.getPluginXmlMetadata(pluginsDirPath, resolveResources)
}
/**
 * Obtains the directory for the given plugin name
 */
getPluginDirForName = { String pluginName ->
    GriffonPluginUtils.getPluginDirForName(pluginsDirPath, pluginName)
}
/**
 * Obtains an array of all plug-in source files as Spring Resource objects
 */
getPluginSourceFiles = {
    GriffonPluginUtils.getPluginSourceFiles(pluginsDirPath, resolveResources)
}
/**
 * Obtains an array of all the plug-in provides Gant scripts
 */
getPluginScripts = {
    GriffonPluginUtils.getPluginScripts(pluginsDirPath,resolveResources)
}
/**
 * Gets a list of all scripts known to the application (excluding private scripts starting with _)
 */
getAllScripts = {
    GriffonPluginUtils.getAvailableScripts(griffonHome,pluginsDirPath, basedir, resolveResources)
}
/**
 * Obtains a list of all Griffon plug-in descriptor classes
 */
getPluginDescriptors = {
    GriffonPluginUtils.getPluginDescriptors(basedir,pluginsDirPath,resolveResources)
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
        def instrumentedInstallScript = "def pluginBasedir = '${pluginsDirPath}/${fullPluginName}'\n" + scriptFile.text
        // we are using text form of script here to prevent Gant caching
        includeTargets << instrumentedInstallScript
    }
}
/**
 * Downloads a remote plug-in zip into the plugins dir
 */
downloadRemotePlugin = { url, pluginsBase ->
    def slash = url.file.lastIndexOf('/')
    def fullPluginName = "${url.file[slash + 8..-5]}"
    Ant.get(dest: "${pluginsBase}/griffon-${fullPluginName}.zip",
            src: "${url}",
            verbose: true,
            usetimestamp: true)

    return fullPluginName
}

/**
 * Event triggered on plugin installation failure
 */
pluginInstallFail = {String msg, plugin ->
    println "Plug-in ${plugin.name} failed to install: $msg"
    event("PluginInstallFailed", [ "Plugin ${fullPluginName} failed to install"])
    exit(1)
}

/**
 * Caches a local plugin into the plugins directory
 */
cacheLocalPlugin = { pluginFile ->
    fullPluginName = "${pluginFile.name[8..-5]}"
    Ant.copy(file: pluginFile, tofile: "${pluginsBase}/griffon-${fullPluginName}.zip")
    Ant.copy(file: pluginFile, tofile: "${pluginsDirPath}/griffon-${fullPluginName}.zip")
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
    use(DOMCategory) {
        plugin = pluginsList.'plugin'.find { it.'@name'.toLowerCase() == pluginName.toLowerCase() }

        if (plugin) {
            pluginRelease = pluginRelease ? pluginRelease : plugin.'@latest-release'
            if (pluginRelease) {
                def release = plugin.'release'.find {rel -> rel.'@version' == pluginRelease }
                if (release) {
                    pluginDistName = release.'file'.text()
                } else {
                    event("StatusError", ["Release ${pluginRelease} was not found for this plugin. Type 'griffon plugin-info ${pluginName}'"])
                    exit(1)
                }
            } else {
                event("StatusError", ["Latest release information is not available for plugin '${pluginName}', specify concrete release to install"])
                exit(1)
            }
        } else {
            event("StatusError", ["Plugin '${pluginName}' was not found in repository. If it is not stored in a configured repository you will need to install it manually. Type 'griffon list-plugins' to find out what plugins are available."])
            exit(1)
        }

        def pluginCacheFileName = "${pluginsHome}/${plugin.'@name'}/griffon-${plugin.'@name'}-${pluginRelease}.zip"
        if (!new File(pluginCacheFileName).exists() || pluginRelease.endsWith("SNAPSHOT")) {
            Ant.mkdir(dir: "${pluginsHome}/${pluginName}")
            Ant.get(dest: pluginCacheFileName,
                    src: "${pluginDistName}",
                    verbose: true)
        }
        def fullPluginName = "$pluginName-$pluginRelease"

        Ant.copy(file:"${pluginsHome}/${pluginName}/griffon-${fullPluginName}.zip",tofile:"${pluginsDirPath}/griffon-${fullPluginName}.zip")
        return fullPluginName
    }
}
/**
 * Installs a plug-in for the given name and optional version
 */
installPluginForName = { String fullPluginName ->

    if (fullPluginName) {
        event("InstallPluginStart", [fullPluginName])
        def pluginInstallPath = "${globalInstall ? globalPluginsDirPath : pluginsDirPath}/${fullPluginName}"
        Ant.delete(dir: pluginInstallPath, failonerror: false)
        Ant.mkdir(dir: pluginInstallPath)
        Ant.unzip(dest: pluginInstallPath, src: "${pluginsDirPath}/griffon-${fullPluginName}.zip")


        def pluginXmlFile = new File("${pluginInstallPath}/plugin.xml")
        if (!pluginXmlFile.exists()) {
            Ant.fail("Plug-in $fullPluginName is not a valid Griffon plug-in. No plugin.xml descriptor found!")
        }
        def pluginXml = new XmlSlurper().parse(pluginXmlFile)
        def pluginName = pluginXml.@name.toString()
        def pluginVersion = pluginXml.@version.toString()
        // Add the plugin's directory to the binding so that any event
        // handlers in the plugin have access to it. Normally, this
        // variable is added in GriffonScriptRunner, but this plugin
        // hasn't been installed by that point.
        binding.setVariable("${pluginName}PluginDir", new File(pluginInstallPath).absoluteFile)

        def dependencies = [:]

        for (dep in pluginXml.dependencies.plugin) {
            def depName = dep.@name.toString()
            String depVersion = dep.@version.toString()
            dependencies[depName] = depVersion
            def depPluginDir = getPluginDirForName(depName)?.file
            if (!depPluginDir?.exists()) {
                event("StatusUpdate", ["Plugin dependency [$depName] not found. Attempting to resolve..."])
                // recursively install dependent plug-ins
                def upperVersion =  GriffonPluginUtils.getUpperVersion(depVersion)
                def release = cacheKnownPlugin(depName, upperVersion == '*' ? null : upperVersion)

                Ant.copy(file:"${pluginsHome}/${depName}/griffon-${release}.zip",tofile:"${pluginsDirPath}/griffon-${release}.zip")

                installPluginForName(release)
                dependencies.remove(depName)
            }
            else {
                def dependency = readPluginXmlMetadata(depName?.toString())
                if (!GriffonPluginUtils.isValidVersion(dependency.@version.toString(), depVersion)) {
                    pluginInstallFail("Plug-in requires version [$depVersion] of plug-in [$depName], but installed version is [${dependency.version}]. Please upgrade this plug-in and try again.", plugin)
                }
                else {
                    dependencies.remove(depName)
                }
            }
        }

        if (dependencies) {
            Ant.delete(dir: "${pluginsDirPath}/${fullPluginName}", quiet: true, failOnError: false)
            clean()

            pluginInstallFail("Failed to install plug-in [${fullPluginName}]. Missing dependencies: ${dependencies.inspect()}", plugin)

        }
        else {
            def pluginJars = resolveResources("file:${pluginsDirPath}/${fullPluginName}/lib/*.jar")
            for(jar in pluginJars) {
                rootLoader.addURL(jar.URL)
            }
            // proceed _Install.groovy plugin script if exists
            def installScript = new File("${pluginsDirPath}/${fullPluginName}/scripts/_Install.groovy")
            runPluginScript(installScript, fullPluginName, "post-install script")

            File appPropsFile = new File("${basedir}/application.properties")

            metadata['plugins.'+pluginName]=pluginVersion
            appPropsFile.withOutputStream { out ->
                metadata.store out, 'utf-8'
            }


            def providedScripts = resolveResources("file:${pluginsDirPath}/${fullPluginName}/scripts/*.groovy").findAll { !it.filename.startsWith('_')}
            event("StatusFinal", ["Plugin ${fullPluginName} installed"])
            if (providedScripts) {
                println "Plug-in provides the following new scripts:"
                println "------------------------------------------"
                providedScripts.file.each {file ->
                    def scriptName = GCU.getScriptName(file.name)
                    println "griffon ${scriptName}"
                }
            }

            event("PluginInstalled", [fullPluginName])
        }

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
    TransformerFactory tFactory = TransformerFactory.newInstance();
    tFactory.setAttribute("indent-number", 4);
    Transformer transformer = tFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    Result result = new StreamResult(new OutputStreamWriter(new FileWriter(pluginsListFile),"UTF-8"));
    transformer.transform(new DOMSource(document), result);
}

def parseRemoteXML(url) {
    DOMBuilder.parse(new URL(url).openStream().newReader())
}



def buildBinaryPluginInfo(root, pluginName ){
    // split plugin name in form of 'plugin-name-0.1' to name ('plugin-name') and version ('0.1')
    def matcher = (pluginName =~ /^([^\d]+)-(.++)/)
    // convert to new plugin naming convention (MyPlugin -> my-plugin)
    def name = GCU.getScriptName(matcher[0][1])
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
        if (!releaseNode) {
            releaseNode = builder.'release'(type: 'zip', version: release) {
                title("This is a zip release, no info available for it")
                file("${BINARY_PLUGIN_DIST}/griffon-${pluginName}.zip")
            }
            pluginNode.appendChild(releaseNode)
        }
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
            new URL("${DEFAULT_PLUGIN_DIST}/griffon-${pluginName}/tags/LATEST_RELEASE/plugin.xml").withReader {Reader reader ->
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
                def releaseTagsList = new URL("${DEFAULT_PLUGIN_DIST}/griffon-${pluginName}/tags/").text
                releaseTagsList.eachMatch(/<li><a href="(.+?)">/) {
                    def releaseTag = it[1][0..-2]
                    buildReleaseInfo(pluginNode, pluginName, "${DEFAULT_PLUGIN_DIST}/griffon-${pluginName}/tags", releaseTag)
                }
            } catch (Exception e) {
                // no plugin release info available
            }

            // proceed trunk release
            try {
                buildReleaseInfo(pluginNode, pluginName, "${DEFAULT_PLUGIN_DIST}/griffon-${pluginName}", "trunk")
            } catch (Exception e) {
                // no plugin release info available
            }

            if (latestRelease && pluginNode.'release'.find {it.'@version' == latestRelease}) pluginNode.setAttribute('latest-release', latestRelease as String)
        }
    }
}