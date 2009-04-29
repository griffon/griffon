/* Copyright 2004-2005 Graeme Rocher
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
package org.codehaus.griffon.plugins

import org.codehaus.griffon.util.BuildSettingsHolder
import org.apache.commons.lang.ArrayUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.util.concurrent.ConcurrentHashMap
import groovy.util.slurpersupport.GPathResult

/**
 * Utility class containing methods that aid in loading and evaluating plug-ins
 *
 * @author Graeme Rocher
 * @since 1.0
 *        <p/>
 *        Created: Nov 29, 2007
 */
public class GriffonPluginUtils {

    static final String WILDCARD = "*";
    public static final GRIFFON_HOME
    static {
        def ant = new AntBuilder()
        ant.property(environment: "env")
        GRIFFON_HOME = ant.antProject.properties."env.GRIFFON_HOME"
    }


    static final COMPARATOR = [compare: { o1, o2 ->
        def result = 0
        if(o1 == '*') result = 1
        else if(o2 == '*') result = -1
        else {
            def nums1 = o1.split(/\./).findAll { it.trim() != ''}*.toInteger()
            def nums2 = o2.split(/\./).findAll { it.trim() != ''}*.toInteger()
            for(i in 0..<nums1.size()) {
                if(nums2.size() > i) {
                    result = nums1[i].compareTo(nums2[i])
                    if(result != 0)break
                }
            }
        }
            result
        },
        equals: { false }] as Comparator

    /**
     * Check if the required version is a valid for the given plugin version
     *
     * @param pluginVersion The plugin version
     * @param requiredVersion The required version
     * @return True if it is valid
     */
    static boolean isValidVersion(String pluginVersion, String requiredVersion) {

        pluginVersion = trimTag(pluginVersion);

       if(requiredVersion.indexOf('>')>-1) {
            def tokens = requiredVersion.split(">")*.trim()
            def newTokens = []
            for(t in tokens) {
                newTokens << trimTag(t)
            }

            tokens << pluginVersion
            tokens = tokens.sort(COMPARATOR)

            if(tokens[1] == pluginVersion) return true

        }
        else if(pluginVersion.equals(trimTag(requiredVersion))) return true;
        return false;
    }

    /**
     * Returns the upper version of a Griffon version number expression in a plugin
     */
    static String getUpperVersion(String pluginVersion) {
        if(pluginVersion.indexOf('>')>-1) {
            def tokens = pluginVersion.split(">")*.trim()
            return tokens[1].trim()
        }
        else {
            return pluginVersion.trim()
        }
    }

    private static trimTag(pluginVersion) {
        def i = pluginVersion.indexOf('-')
        if(i>-1)
            pluginVersion = pluginVersion[0..i-1]
        pluginVersion
    }


    private static final PathMatchingResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver()

    /**
     * A default resolve used if none is specified to the resource resolving methods in this class
     */
    static final DEFAULT_RESOURCE_RESOLVER = { pattern ->
        try {
                return RESOLVER.getResources(pattern)
            }
            catch(Throwable e) {
                 return []  as Resource[]
            }

    }


    /**
     * Gets a list of all the known plugin base directories (directories where plugins are installed to)
     */
    static String[] getPluginBaseDirectories(String pluginDirPath = BuildSettingsHolder.settings?.projectPluginsDir?.path) {
         [ pluginDirPath, BuildSettingsHolder.settings?.globalPluginsDir?.path ] as String[]
    }

    private static pluginDirectoryResources = null
    static synchronized Resource[] getPluginDirectories(String pluginDirPath = BuildSettingsHolder.settings?.projectPluginsDir?.path) {
        if(!pluginDirectoryResources) {
            def dirList = []
            def directoryNamePredicate = {
                it.isDirectory() && (!it.name.startsWith(".") && it.name.indexOf('-')>-1)
            }

            for(pluginBase in getPluginBaseDirectories(pluginDirPath)) {
                List pluginDirs = new File(pluginBase).listFiles().findAll(directoryNamePredicate).collect { new FileSystemResource(it) }
                dirList.addAll( pluginDirs )
            }
            pluginDirectoryResources = dirList as Resource[]
        }
        return pluginDirectoryResources
    }

    private static allArtefactResources = null
    /**
     * Obtains a reference to all artefact resources (all Groovy files contained within the griffon-app directory of plugins or applications)
     */
    static synchronized Resource[] getArtefactResources(String basedir, Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!allArtefactResources) {
            basedir = new File(basedir).getCanonicalFile().getAbsolutePath()
            def resources = resourceResolver("file:${basedir}/griffon-app/**/**.groovy")

            resources = resolvePluginResourcesAndAdd(resources) { pluginDir ->
                resourceResolver("file:${pluginDir}/*/griffon-app/**/**.groovy")
            }

            allArtefactResources = resources
        }
        return allArtefactResources
    }

    private static allPluginXmlMetadata = null
    /**
     * Obtains a Resource array of the Plugin metadata XML files used to describe the plugins provided resources
     */
    static synchronized Resource[] getPluginXmlMetadata( String pluginsDirPath,
                                            Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!allPluginXmlMetadata) {
            allPluginXmlMetadata = new Resource[0]
            allPluginXmlMetadata = resolvePluginResourcesAndAdd(allPluginXmlMetadata, pluginsDirPath) { pluginDir ->
                resourceResolver("file:${pluginDir}/*/plugin.xml")
            }
        }
        return allPluginXmlMetadata
    }

    /**
     * Takes a Resource[] and optional pluginsDirPath and goes through each plugin directory. It will then used the provided
     * resolving resolving closures to attempt to resolve a new set of resources to add to the original passed array.
     *
     * A new array is then returned that contains any additiona plugin resources that were resolved by the expression passed
     * in the closure
     */
    private static resolvePluginResourcesAndAdd(Resource[] originalResources, String pluginsDirPath = BuildSettingsHolder.settings?.projectPluginsDir?.path, Closure resolver) {
        String[] pluginDirs = getPluginBaseDirectories(pluginsDirPath)
        for (dir in pluginDirs) {
            def newResources = dir ? resolver(dir) : null
            if(newResources) {
                originalResources = ArrayUtils.addAll(originalResources, newResources)
            }
        }
        return originalResources
    }

    private static availableScripts = null

    /**
     * Obtains an array of all Gant scripts that are availabe for execution in a Griffon application
     */
    static synchronized Resource[] getAvailableScripts(String griffonHome,
                                          String pluginDirPath,
                                          String basedir,
                                          String griffonWorkDir,
                                          Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!availableScripts) {

            def scripts = []
            def userHome = System.getProperty("user.home")
            resourceResolver("file:${griffonHome}/scripts/**.groovy").each { if (!it.file.name.startsWith('_')) scripts << it }
            resourceResolver("file:${basedir}/scripts/*.groovy").each { if (!it.file.name.startsWith('_')) scripts << it }
            getPluginScripts(pluginDirPath).each { if (!it.file.name.startsWith('_')) scripts << it }
            resourceResolver("file:${griffonWorkDir}/scripts/*.groovy").each { if (!it.file.name.startsWith('_')) scripts << it }
            availableScripts = scripts as Resource[]
        }
        return availableScripts
    }

    private static pluginScripts = null
    /**
     * Obtains an array of plug-in provided Gant scripts available to a Griffon application
     */
    static synchronized Resource[] getPluginScripts(String pluginDirPath,Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!pluginScripts) {
            pluginScripts = new Resource[0]
            pluginScripts = resolvePluginResourcesAndAdd(pluginScripts, pluginDirPath) { pluginDir ->
                resourceResolver("file:${pluginDir}/*/scripts/*.groovy")
            }
        }
        return pluginScripts
    }

    private static Resource[] sourceFiles = null
    /**
     * Obtains an array of all plug-in provided source files (Java and Groovy)
     */
    static synchronized Resource[] getPluginSourceFiles(String pluginsDirPath,Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!sourceFiles) {
            sourceFiles = new Resource[0]
            sourceFiles = resolvePluginResourcesAndAdd(sourceFiles, pluginsDirPath) { pluginDir ->
                Resource[] pluginSourceFiles = resourceResolver("file:${pluginDir}/*/griffon-app/*")
                pluginSourceFiles = ArrayUtils.addAll(pluginSourceFiles,resourceResolver("file:${pluginDir}/*/src/java"))
                pluginSourceFiles = ArrayUtils.addAll(pluginSourceFiles,resourceResolver("file:${pluginDir}/*/src/groovy"))
                return pluginSourceFiles
            }
        }
        return sourceFiles
    }

    private static Resource[] jarFiles= null
    /**
     * Obtains an array of all plug-in provided JAR files
     */
    static synchronized Resource[] getPluginJarFiles(String pluginsDirPath,Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!jarFiles) {
            jarFiles = new Resource[0]
            jarFiles = resolvePluginResourcesAndAdd(jarFiles, pluginsDirPath) { pluginDir ->
                resourceResolver("file:${pluginDir}/*/lib/*.jar")
            }
        }
        return jarFiles
    }

    private static Resource[] pluginDescriptors = null


    /**
     * Obtains an array of all plug-in descriptors (the root classes that end with *GriffonPlugin.groovy)
     */
    static synchronized Resource[] getPluginDescriptors(String basedir,
                                                        String pluginsDirPath,
                                                        Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!pluginDescriptors) {

            Resource basePlugin
            basePlugin = getBasePluginDescriptor(basedir)


            pluginDescriptors = basePlugin ? [basePlugin] as Resource[] : [] as Resource[]

            pluginDescriptors = resolvePluginResourcesAndAdd(pluginDescriptors, pluginsDirPath) { pluginDir ->
                resourceResolver("file:${pluginDir}/*/*GriffonPlugin.groovy")
            }

        }
        return pluginDescriptors
    }

    private static Resource basePluginDescriptor = null
    static synchronized Resource getBasePluginDescriptor(String basedir) {
        if(!basePluginDescriptor) {
            File baseFile = new File(basedir).getCanonicalFile()
            File basePluginFile = baseFile.listFiles().find { it.name.endsWith("GriffonPlugin.groovy")}

            if (basePluginFile?.exists()) {
                basePluginDescriptor = new FileSystemResource(basePluginFile)
            }
        }
        return basePluginDescriptor
    }

    private static Resource[] pluginLibs = null

    /**
     * Obtains an array of all plug-in lib directories
     */
    static synchronized Resource[] getPluginLibDirectories(String pluginsDirPath,
                                                            Closure resourceResolver = DEFAULT_RESOURCE_RESOLVER) {
        if(!pluginLibs) {
            pluginLibs = new Resource[0]
            pluginLibs = resolvePluginResourcesAndAdd(pluginLibs, pluginsDirPath) { pluginDir ->
                resourceResolver("file:${pluginDir}/*/lib")
            }

        }
        return pluginLibs
    }

    /**
     * Obtains the path to the globa plugins directory
     */
    static String getGlobalPluginsPath() { BuildSettingsHolder.settings?.globalPluginsDir?.path }

    private static Map pluginToDirNameMap = new ConcurrentHashMap()

    /**
     * Obtains a plugin directory for the given name
     */
    static Resource getPluginDirForName(String pluginName) {
        getPluginDirForName(BuildSettingsHolder.settings?.projectPluginsDir?.path, pluginName)
    }


    private static Map pluginMetaDataMap = new ConcurrentHashMap()
    /**
     * Returns XML about the plugin
     */
    static getMetadataForPlugin(String pluginName) {
        if(pluginMetaDataMap[pluginName]) return pluginMetaDataMap[pluginName]
        Resource pluginDir = getPluginDirForName(BuildSettingsHolder.settings?.projectPluginsDir?.path, pluginName)
        try {
            GPathResult result = new XmlSlurper().parse(new File("$pluginDir.file.absolutePath/plugin.xml"))
            pluginMetaDataMap[pluginName] = result
            return result
        }
        catch (e) {
            return null
        }
    }
    /**
     * Obtains a plugin directory for the given name
     */
    static Resource getPluginDirForName(String pluginsDirPath, String pluginName) {
        Resource pluginResource = pluginToDirNameMap[pluginName]
        if(!pluginResource) {

            try {
                def directoryNamePredicate = {
                    it.isDirectory() && (it.name == pluginName || it.name.startsWith("$pluginName-"))
                }

                String[] pluginDirs = getPluginBaseDirectories(pluginsDirPath)
                File pluginFile
                for(pluginDir in pluginDirs) {
                    pluginFile = new File("${pluginDir}").listFiles().find(directoryNamePredicate)
                    if(pluginFile) break
                }

                pluginResource =  pluginFile ? new FileSystemResource(pluginFile) : null
                if(pluginResource) {
                    pluginToDirNameMap[pluginName] = pluginResource
                }
            } catch (IOException e) {
                // ignore
                return null
            }
        }
        return pluginResource
    }

    /**
     * Clears cached resolved resources
     */
    static synchronized clearCaches() {
        pluginToDirNameMap.clear()
        pluginDescriptors = null
        pluginLibs = null
        pluginScripts = null
        basePluginDescriptor = null
        jarFiles = null
        sourceFiles = null
        allArtefactResources = null
        availableScripts = null
    }



}

