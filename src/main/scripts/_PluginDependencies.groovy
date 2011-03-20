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

import griffon.util.GriffonUtil

import groovy.xml.MarkupBuilder
import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.griffon.commons.DefaultGriffonContext
import org.codehaus.griffon.documentation.DocumentationContext
import org.codehaus.griffon.documentation.DocumentedMethod
import org.codehaus.griffon.documentation.DocumentedProperty
import org.codehaus.griffon.plugins.DefaultGriffonPluginManager
import org.codehaus.griffon.plugins.PluginManagerHolder
import org.codehaus.griffon.resolve.IvyDependencyManager
import org.springframework.core.io.Resource
import griffon.util.Metadata
import org.codehaus.griffon.resolve.GriffonRepoResolver
import org.codehaus.griffon.resolve.PluginInstallEngine
import org.codehaus.griffon.plugins.GriffonPluginManager
import org.codehaus.griffon.plugins.GriffonPluginUtils

/**
 * Plugin stuff. If included, must be included after "_ClasspathAndEvents".
 *
 * @author Graeme Rocher (Griffon 1.1)
 */
if (getBinding().variables.containsKey("_plugin_dependencies_called")) return
_plugin_dependencies_called = true

includeTargets << griffonScript("_GriffonClean")
includeTargets << griffonScript("_GriffonCompile")
includeTargets << griffonScript("_GriffonProxy")

// Properties
pluginsList = null
globalInstall = false
pluginsBase = "${griffonWorkDir}/plugins".toString().replaceAll('\\\\','/')

// Targets
target(resolveDependencies:"Resolve plugin dependencies") {
    depends(parseArguments, initInplacePlugins)

    def installEngine = createPluginInstallEngine(metadata)
    installEngine.resolvePluginDependencies()
}

target(initInplacePlugins: "Generates the plugin.xml descriptors for inplace plugins.") {
    depends(classpath)
}

loadPluginClass = { String pluginFile ->
    try {
        // Rather than compiling the descriptor via Ant, we just load
        // the Groovy file into a GroovyClassLoader. We add the classes
        // directory to the class loader in case it didn't exist before
        // the associated plugin's sources were compiled.
        def gcl = new GroovyClassLoader(classLoader)
        addUrlIfNotPresent gcl, classesDir

        def pluginClassName = pluginFile.endsWith('.groovy') ? pluginFile[0..-8] : pluginFile
        return gcl.loadClass(pluginClassName).newInstance()
    }
    catch (Throwable t) {
        event("StatusError", [ t.message])
        t.printStackTrace(System.out)
        ant.fail("Cannot instantiate plugin file")
    }
}

resolvePluginClasspathDependencies = { plugin ->
    def plugins = [] 
    if(plugin.metaClass.hasProperty(plugin,'dependsOn')) {
        for(dep in plugin.dependsOn) {
            plugins << [name: dep.key, version: dep.value.toString()]
        }
    }
    _resolveDependencies(plugins) { pluginName, pluginVersion, pluginDir ->
        def pluginFile = pluginDir.list().find{ it =~ /GriffonPlugin\.groovy/ }
        addUrlIfNotPresent classLoader, pluginDir
        resolvePluginClasspathDependencies(loadPluginClass(pluginFile))
    }
}

_resolveDependencies = { List plugins, callback = null ->
    boolean installedPlugins = false

    for(p in plugins) {
        def name = p.name
        def version = p.version
        def fullName = "$name-$version"
        def pluginLoc = getPluginDirForName(name)
        if(!pluginLoc?.exists()) {
            println "Plugin [${fullName}] not installed, resolving.."
            installPluginForName(name, version)
            if(callback) callback(name, version, getPluginDirForName(name).file)
            installedPlugins = true
        }
    }
    if(installedPlugins) {
        resetClasspathAndState()
    }
}

def resetClasspathAndState() {
    GriffonPluginUtils.clearCaches()
    classpathSet = false
    classpath()
    PluginManagerHolder.pluginManager = null
}

/**
 * Compiles the sources for a single in-place plugin. A bit of a hack,
 * but any other solution would require too much refactoring to make it
 * into the 1.2 release.
 */
compileInplacePlugin = { File pluginDir ->
    def classesDirPath = griffonSettings.classesDir.path
    ant.mkdir(dir: classesDirPath)

    profile("Compiling inplace plugin sources to location [$classesDirPath]") {
        // First compile the plugins so that we can exclude any
        // classes that might conflict with the project's.
        def classpathId = "griffon.compile.classpath"
        def pluginResources = pluginSettings.getPluginSourceFiles(pluginDir)
        
        if (pluginResources) {
            // Only perform the compilation if there are some plugins
            // installed or otherwise referenced.
            try {
                compileSources(classesDirPath, classpathId) {
                    for(File dir in pluginResources.file) {
                        if (dir.exists() && dir.isDirectory()) {
                            src(path: dir.absolutePath)
                        }
                    }
                    exclude(name: "**/BuildConfig.groovy")
                    exclude(name: "**/Config.groovy")
                    javac(classpathref: classpathId, encoding: "UTF-8", debug: "yes")
                }
            }
            catch (e) {
                println "Failed to compile plugin at location [$pluginDir] with error: ${e.message}"
                exit 1
            }
        }
    }
}

doWithPlugins = { callback = null ->
    if(!callback) return

    def plugins = metadata.findAll { it.key.startsWith("plugins.")}.collect {
       [
        name:it.key[8..-1],
        version: it.value
       ]
    }

    for(p in plugins) {
        def name = p.name
        def version = p.version
        // def fullName = "$name-$version"
        def pluginDir = getPluginDirForName(name)
        if(!pluginDir) installPluginForName(name)
    }

    // read again as the list might have been updated
    metadata.findAll { it.key.startsWith("plugins.")}.collect {
        name = it.key[8..-1]
        version = it.value
        def pluginDir = getPluginDirForName(name)
        callback(name, version, pluginDir.file)
    }
}

/**
 * Generates the 'plugin.xml' file for a plugin. Returns an instance
 * of the plugin descriptor.
 */
generatePluginXml = { File descriptor, boolean compilePlugin = true ->
    // Load the plugin descriptor class and instantiate it so we can
    // access its properties.
    Class pluginClass
    def plugin
    
    if(compilePlugin) {
        try {
            // Rather than compiling the descriptor via Ant, we just load
            // the Groovy file into a GroovyClassLoader. We add the classes
            // directory to the class loader in case it didn't exist before
            // the associated plugin's sources were compiled.
            def gcl = new GroovyClassLoader(classLoader)
            addUrlIfNotPresent gcl, classesDir

            pluginClass = gcl.parseClass(descriptor)
            plugin = pluginClass.newInstance()
        }
        catch (Throwable t) {
            event("StatusError", [t.message])
            t.printStackTrace(System.out)
            ant.fail("Cannot instantiate plugin file")
        }
    }

    // Work out what the name of the plugin is from the name of the
    // descriptor file.
    pluginName = GriffonUtil.getPluginName(descriptor.name)
    def pluginBaseDir = descriptor.parentFile
    // Remove the existing 'plugin.xml' if there is one.
    def pluginXml = new File(pluginBaseDir, "plugin.xml")
    pluginXml.delete()

    // Use MarkupBuilder with indenting to generate the file.
    def writer = new IndentPrinter(new PrintWriter(new FileWriter(pluginXml)))
    def xml = new MarkupBuilder(writer)

    // Write the content!
    def props = ['author','authorEmail','title','description','documentation','license']
    def resourceList = pluginSettings.getArtefactResourcesForOne(descriptor.parentFile.absolutePath)

    def rcComparator = [ compare: {a, b -> a.URI <=> b.URI } ] as Comparator
    Arrays.sort(resourceList, rcComparator)

    pluginGriffonVersion = "${GriffonUtil.griffonVersion} > *"
    def pluginProps = compilePlugin ? plugin.properties : pluginSettings.getPluginInfo(pluginBaseDir.absolutePath)
    if(pluginProps["griffonVersion"]) {
        pluginGriffonVersion = pluginProps["griffonVersion"]
    }
 
    def jdk = pluginProps.jdk ?: '1.5'

    xml.plugin(name:"${pluginName}", version:"${pluginProps.version}", griffonVersion:pluginGriffonVersion, jdk: jdk) {
        for( p in props) {
            if( pluginProps[p] ) "${p}"(pluginProps[p].toString().trim())
        }
        if(pluginProps.toolkits) {
            toolkits(pluginProps.toolkits.join(','))
        }
        if(pluginProps.platforms) {
            platforms(pluginProps.platforms.join(','))
        }
        xml.resources {
            for(r in resourceList) {
                 def matcher = r.URL.toString() =~ artefactPattern
                 def name = matcher[0][1].replaceAll('/', /\./)
                 xml.resource(name)
            }
        }
        dependencies {        
            if(pluginProps["dependsOn"]) {
                for(d in pluginProps.dependsOn) {
                    delegate.plugin(name:d.key, version:d.value)
                }
            } 
        }

/*
        def docContext = DocumentationContext.instance
        if(docContext) {
            behavior {
                for(DocumentedMethod m in docContext.methods) {
                    method(name:m.name, artefact:m.artefact, type:m.type?.name) {
                        description m.text
                        if(m.arguments) {
                            for(arg in m.arguments) {
                                argument type:arg.name
                            }
                        }
                    }
                }
                for(DocumentedMethod m in docContext.staticMethods) {
                    'static-method'(name:m.name, artefact:m.artefact, type:m.type?.name) {
                        description m.text
                        if(m.arguments) {
                            for(arg in m.arguments) {
                                argument type:arg.name
                            }
                        }
                    }
                }
                for(DocumentedProperty p in docContext.properties) {
                    property(name:p.name, type:p?.type?.name, artefact:p.artefact) {
                        description p.text
                    }
                }
            }            
        }
*/
    }

    return plugin
}

target(loadPlugins:"Loads Griffon' plugins") {
    if(PluginManagerHolder.pluginManager) { // plugin manager already loaded?
        // Add the plugin manager to the binding so that it can be accessed
        // from any target.
        pluginManager = PluginManagerHolder.pluginManager
    } else {
        compConfig.setTargetDirectory(classesDir)
        def unit = new CompilationUnit ( compConfig , null , new GroovyClassLoader(classLoader) )
        def pluginFiles = pluginSettings.pluginDescriptors

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
                    pluginSettings.pluginManager = pluginManager
                }
            }
            profile("loading plugins") {
                event("PluginLoadStart", [pluginManager])
                pluginManager.loadPlugins()
                def baseDescriptor = pluginSettings.basePluginDescriptor
                if(baseDescriptor) {                    
                    def baseName = FilenameUtils.getBaseName(baseDescriptor.filename)
                    def plugin = pluginManager.getGriffonPluginForClassName(baseName)
                    if(plugin) {
                        plugin.basePlugin = true
                    }
                }
                if(pluginManager.failedLoadPlugins) {
                    event("StatusError", ["Error: The following plugins failed to load due to missing dependencies: ${pluginManager.failedLoadPlugins*.name}"])
                    for(p in pluginManager.failedLoadPlugins) {
                        println "- Plugin: $p.name, Dependencies: $p.dependencyNames"
                    }
                    exit(1)
                }

                //pluginManager.doArtefactConfiguration()
                griffonContext.initialise()
                event("PluginLoadEnd", [pluginManager])
            }
        } catch (Exception e) {
            GriffonUtil.deepSanitize(e).printStackTrace()
            event("StatusFinal", [ "Error loading plugin manager: " + e.message ])
            exit(1)
        }
    }
}

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
    pluginSettings.pluginXmlMetadata.findAll { it.file.exists() }.collect { new XmlSlurper().parse(it.file) }
}

/**
 * Obtains the directory for the given plugin name
 */
getPluginDirForName = { String pluginName ->
    pluginSettings.getPluginDirForName(pluginName)
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

readMetadataFromZip = { String zipLocation, pluginFile=zipLocation ->
    def installEngine = createPluginInstallEngine(metadata)
    installEngine.readMetadataFromZip(zipLocation)
}

// this is one is better than the previous as it will only include
// the script if the plugin is available. Works on application
// and plugin project
// includePluginScript = { pluginName, scriptName ->
//    def pluginHome = getPluginDirForName(pluginName)?.file
//    if(!pluginHome) return
//    def scriptFile = new File(pluginHome,"/scripts/${scriptName}.groovy")
//    if(scriptFile.exists()) includeTargets << scriptFile
// }

/**
 * Uninstalls a plugin for the given name and version
 */
uninstallPluginForName = { name, version=null ->
    def pluginInstallEngine = createPluginInstallEngine(metadata)
    pluginInstallEngine.uninstallPlugin name, version

}
/**
 * Installs a plugin for the given name and optional version
 */
installPluginForName = { String name, String version = null, boolean globalInstall = false ->
    PluginInstallEngine pluginInstallEngine = createPluginInstallEngine(metadata)
    if (name) {
        event("InstallPluginStart", ["$name-$version"])
        pluginInstallEngine.installPlugin(name, version, globalInstall)       
    }
}

private PluginInstallEngine createPluginInstallEngine(Metadata md) {
    def pluginInstallEngine = new PluginInstallEngine(griffonSettings, pluginSettings, md, ant)
    pluginInstallEngine.eventHandler = { eventName, msg -> event(eventName, [msg]) }
    pluginInstallEngine.errorHandler = { msg ->
        event("StatusError", [msg])
        for (pluginDir in pluginInstallEngine.installedPlugins) {
            if (pluginInstallEngine.isNotInlinePluginLocation(new File(pluginDir.toString()))) {
                ant.delete(dir: pluginDir, failonerror: false)
            }
        }
        exit(1)
    }
    pluginInstallEngine.postUninstallEvent = {
        resetClasspath()
    }

    pluginInstallEngine.postInstallEvent = { pluginInstallPath ->
        File pluginEvents = new File("${pluginInstallPath}/scripts/_Events.groovy")
         if (pluginEvents.exists()) {
             eventListener.loadEventsScript(pluginEvents)
         }
        resetClasspath()
    }
    pluginInstallEngine.isInteractive = isInteractive
    pluginInstallEngine.pluginDirVariableStore = binding
    pluginInstallEngine.pluginScriptRunner = runPluginScript
    return pluginInstallEngine
}

protected GriffonPluginManager resetClasspath() {
    classpathSet = false
    classpath()
    PluginManagerHolder.pluginManager = null
}

doInstallPluginFromURL = { URL url, Metadata md = metadata ->
    withPluginInstall {
        def installEngine = createPluginInstallEngine(md)
        installEngine.installPlugin url, globalInstall
    }
}

doInstallPluginZip = { File file, Metadata md = metadata ->
    withPluginInstall {
        def installEngine = createPluginInstallEngine(md)
        installEngine.installPlugin file, globalInstall, true
    }
}

doInstallPlugin = { pluginName, pluginVersion = null, Metadata md = metadata ->
    withPluginInstall {
        def installEngine = createPluginInstallEngine(md)
        installEngine.installPlugin pluginName, pluginVersion, globalInstall
    }
}

eachRepository = { Closure callable ->
    IvyDependencyManager dependencyManager = griffonSettings.dependencyManager
    for(resolver in dependencyManager.chainResolver.resolvers) {
        if(resolver instanceof GriffonRepoResolver) {
            pluginsList = resolver.getPluginList(new File("${griffonWorkDir}/plugins-list-${resolver.name}.xml"))
            callable(resolver.name, resolver.repositoryRoot)
        }
    }
}

private withPluginInstall(Closure callable) {
    try {
        fullPluginName = callable.call()
    }
    catch (e) {
        logError("Error installing plugin: ${e.message}", e)
        exit(1)
    }
}
