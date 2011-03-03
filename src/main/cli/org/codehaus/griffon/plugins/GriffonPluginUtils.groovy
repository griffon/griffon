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
package org.codehaus.griffon.plugins

import griffon.util.BuildSettingsHolder
import griffon.util.PluginBuildSettings
import groovy.util.slurpersupport.GPathResult
import org.codehaus.griffon.plugins.metadata.GriffonPlugin
import org.springframework.core.io.Resource

/**
 * Utility class containing methods that aid in loading and evaluating plug-ins
 *
 * @author Graeme Rocher (Grails 1.0)
 */
class GriffonPluginUtils {
    private static final LOCK = new Object()
    static final String WILDCARD = "*";
    static final GRIFFON_HOME
    static {
        try {
            GRIFFON_HOME = System.getenv("GRIFFON_HOME")
        }
        catch (Throwable t) {
            // probably due to permissions error
            GRIFFON_HOME = "UNKNOWN"
        }
    }

    /**
     * Get the name of the a plugin for a particular class.
     */
    static String getPluginName(Class clazz) {
        clazz?.getAnnotation(GriffonPlugin)?.name()
    }

    /**
     * Get the version of the a plugin for a particular class.
     */
    static String getPluginVersion(Class clazz) {
        clazz?.getAnnotation(GriffonPlugin)?.version()
    }

    /**
     * Check if the required version is a valid for the given plugin version
     *
     * @param pluginVersion The plugin version
     * @param requiredVersion The required version
     * @return true if it is valid
     */
    static boolean isValidVersion(String pluginVersion, String requiredVersion) {
        def vc = new VersionComparator()
        pluginVersion = trimTag(pluginVersion);

       if(requiredVersion.indexOf('>')>-1) {
            def tokens = requiredVersion.split(">")*.trim()
            tokens = tokens.collect { trimTag(it) }
            tokens << pluginVersion
            tokens = tokens.sort(vc)

            if(tokens[1] == pluginVersion) return true
        }
        else if(pluginVersion == trimTag(requiredVersion)) return true
        return false
    }

    /**
     * Compare two plugin versions
     *
     * @param pluginVersion The plugin version
     * @param requiredVersion The required version
     * @return 0 if equal; &lt; 0 if pluginVersion is smaller; &gt; 0 if pluginVersion is greater
     */
    static int compareVersions(String pluginVersion, String requiredVersion) {
        def vc = new VersionComparator()
        pluginVersion = trimTag(pluginVersion);

       if(requiredVersion.indexOf('>')>-1) {
            def tokens = requiredVersion.split(">")*.trim()
            tokens = tokens.collect { trimTag(it) }
            tokens << pluginVersion
            tokens = tokens.sort(vc)

            return pluginVersion <=> tokens[1]
        }
        return pluginVersion <=> requiredVersion
    }

    /**
     * Returns the upper version of a Griffon version number expression in a plugin
     */
    static String getUpperVersion(String pluginVersion) {
        return getPluginVersionInternal(pluginVersion,1)
    }

   /**
     * Returns the lower version of a Griffon version number expression in a plugin
     */
    static String getLowerVersion(String pluginVersion) {
        return getPluginVersionInternal(pluginVersion,0)
    }

    static boolean supportsAtLeastVersion(String pluginVersion, String requiredVersion) {
        def lowerVersion = GriffonPluginUtils.getLowerVersion(pluginVersion)
        lowerVersion != '*' && GriffonPluginUtils.isValidVersion(lowerVersion, "$requiredVersion > *")
    }

    private static getPluginVersionInternal(String pluginVersion, index) {
        if (pluginVersion.indexOf('>') > -1) {
            def tokens = pluginVersion.split(">")*.trim()
            return tokens[index].trim()
        }
        else {
            return pluginVersion.trim()
        }
    }

    private static trimTag(String pluginVersion) {
        def i = pluginVersion.indexOf('-')
        if (i >- 1) {
            pluginVersion = pluginVersion[0..i-1]
        }

        def copy = pluginVersion.reverse()
        for(c in copy) {
            if(c =~ /[a-zA-Z]/) pluginVersion = pluginVersion[0..-2]
        }
        def tokens = pluginVersion.split(/\./)

        return tokens.findAll { it ==~ /\d+/ || it =='*'}.join(".")
    }

    /**
     * Returns a new PluginBuildSettings instance
     */
    static PluginBuildSettings newPluginBuildSettings() {
        new PluginBuildSettings(BuildSettingsHolder.settings, PluginManagerHolder.getPluginManager())
    }

    private static instance
    /**
     * Returns a cached PluginBuildSettings instance.
     */
    static PluginBuildSettings getPluginBuildSettings() {
        synchronized(LOCK) {
            if (instance == null) {
                instance = newPluginBuildSettings()
            }
            return instance
        }
    }

    static setPluginBuildSettings(PluginBuildSettings settings) {
        synchronized(LOCK) {
           instance = settings
        }
    }

    /**
     * Returns an array of PluginInfo objects
     */
    static GriffonPluginInfo[] getPluginInfos(String pluginDirPath = BuildSettingsHolder.settings?.projectPluginsDir?.path) {
        return getPluginBuildSettings().getPluginInfos()
    }

    /**
     * Returns only the PluginInfo objects that support the current Environment and BuildScope
     *
     * @see griffon.util.Environment
     * @see griffon.util.BuildScope
     */
    static GriffonPluginInfo[] getSupportedPluginInfos(String pluginDirPath = BuildSettingsHolder.settings?.projectPluginsDir?.path) {
        final PluginBuildSettings settings = getPluginBuildSettings()
        if (!settings.pluginManager) {
            settings.pluginManager = PluginManagerHolder.currentPluginManager()
        }
        return settings.getSupportedPluginInfos()
    }

    /**
     * All the known plugin base directories (directories where plugins are installed to).
     */
    static List<String> getPluginBaseDirectories(String pluginDirPath) {
        getPluginBuildSettings().getPluginBaseDirectories()
    }

    /**
     * All the known plugin base directories (directories where plugins are installed to).
     */
    static List<String> getPluginBaseDirectories() {
        getPluginBuildSettings().getPluginBaseDirectories()
    }

    static Resource[] getPluginDirectories() {
        getPluginBuildSettings().getPluginDirectories()
    }

    static Resource[] getPluginDirectories(String pluginDirPath) {
        getPluginBuildSettings().getPluginDirectories()
    }

    /**
     * All plugin directories in both the given path and the global "plugins" directory together.
     */
    static List<Resource> getImplicitPluginDirectories(String pluginDirPath = BuildSettingsHolder.settings?.projectPluginsDir?.path) {
        getPluginBuildSettings().getImplicitPluginDirectories()
    }

    static boolean isGlobalPluginLocation(Resource pluginDir) {
        getPluginBuildSettings().isGlobalPluginLocation(pluginDir)
    }

    /**
     * All artefact resources (all Groovy files contained within the griffon-app directory of plugins or applications).
     */
    static Resource[] getArtefactResources(String basedir) {
        getPluginBuildSettings().getArtefactResources()
    }

    /**
     * All artefacts in the given application or plugin directory as Spring resources.
     */
    static Resource[] getArtefactResourcesForOne(String projectDir) {
        getPluginBuildSettings().getArtefactResourcesForOne(projectDir)
    }

    /**
     * The Plugin metadata XML files used to describe the plugins provided resources.
     */
    static Resource[] getPluginXmlMetadata(String pluginsDirPath) {
        getPluginBuildSettings().getPluginXmlMetadata()
    }

    /**
     * All Gant scripts that are availabe for execution in a Griffon application.
     */
    static Resource[] getAvailableScripts(String griffonHome, String pluginDirPath, String basedir) {
        getPluginBuildSettings().getAvailableScripts()
    }

    /**
     * Plug-in provided Gant scripts available to a Griffon application.
     */
    static Resource[] getPluginScripts(String pluginDirPath) {
        getPluginBuildSettings().getPluginScripts()
    }

    /**
     * All plugin provided resource bundles.
     */
    static Resource[] getPluginResourceBundles(String pluginDirPath) {
        getPluginBuildSettings().getPluginResourceBundles()
    }

    /**
     * All plug-in provided source files (Java and Groovy).
     */
    static Resource[] getPluginSourceFiles(String pluginsDirPath) {
        getPluginBuildSettings().getPluginSourceFiles()
    }

    /**
     * All plug-in provided JAR files.
     */
    static Resource[] getPluginJarFiles(String pluginsDirPath) {
        getPluginBuildSettings().getPluginJarFiles()
    }

    /**
     * All plug-in provided Test JAR files.
     */
    static Resource[] getPluginTestFiles(String pluginsDirPath) {
        getPluginBuildSettings().getPluginTestFiles()
    }

    /**
     * All plug-in descriptors (the root classes that end with *GriffonPlugin.groovy).
     */
    static Resource[] getPluginDescriptors(String basedir, String pluginsDirPath) {
        getPluginBuildSettings().getPluginDescriptors()
    }

    static Resource getBasePluginDescriptor(String basedir) {
        getPluginBuildSettings().getBasePluginDescriptor(basedir)
    }

    /**
     * Returns the descriptor location for the given plugin directory. The descriptor is the Groovy
     * file that ends with *GriffonPlugin.groovy.
     */
    static Resource getDescriptorForPlugin(Resource pluginDir) {
        getPluginBuildSettings().getDescriptorForPlugin(pluginDir)
    }

    /**
     * All plug-in lib directories.
     */
    static Resource[] getPluginLibDirectories(String pluginsDirPath) {
        getPluginBuildSettings().getPluginLibDirectories()
    }

    /**
     * All plugin i18n directories.
     */
    static Resource[] getPluginI18nDirectories(String pluginsDirPath = BuildSettingsHolder.settings?.projectPluginsDir?.path) {
        getPluginBuildSettings().getPluginI18nDirectories()
    }

    /**
     * The path to the global plugins directory.
     */
    static String getGlobalPluginsPath() {
        getPluginBuildSettings().getGlobalPluginsPath()
    }

    /**
     * Obtains a plugin directory for the given name.
     */
    static Resource getPluginDirForName(String pluginName) {
        getPluginBuildSettings().getPluginDirForName(pluginName)
    }

    /**
     * Returns XML about the plugin.
     */
    static GPathResult getMetadataForPlugin(String pluginName) {
        getPluginBuildSettings().getMetadataForPlugin(pluginName)
    }

    /**
     * Returns XML metadata for the plugin.
     */
    static GPathResult getMetadataForPlugin(Resource pluginDir) {
        getPluginBuildSettings().getMetadataForPlugin(pluginDir)
    }

    /**
     * Obtains a plugin directory for the given name.
     */
    static Resource getPluginDirForName(String pluginsDirPath, String pluginName) {
        getPluginBuildSettings().getPluginDirForName(pluginName)
    }

    /**
     * Clears cached resolved resources
     */
    static void clearCaches() {
        synchronized(LOCK) {
            getPluginBuildSettings().clearCache()
            instance = null
        }
    }
}

class VersionComparator implements Comparator {
    int compare(o1, o2) {
        int result = 0
        if (o1 == '*') {
            result = 1
        }
        else if (o2 == '*') {
            result = -1
        }
        else {
            def nums1
            try {
                def tokens = o1.split(/\./)
                tokens = tokens.findAll { it.trim() ==~ /\d+/ }
                nums1 = tokens*.toInteger()
            }
            catch (NumberFormatException e) {
                throw new InvalidVersionException("Cannot compare versions, left side [$o1] is invalid: ${e.message}")
            }
            def nums2
            try {
                def tokens = o2.split(/\./)
                tokens = tokens.findAll { it.trim() ==~ /\d+/ }
                nums2 = tokens*.toInteger()
            }
            catch (NumberFormatException e) {
                throw new InvalidVersionException("Cannot compare versions, right side [$o2] is invalid: ${e.message}")
            }
            boolean bigRight = nums2.size() > nums1.size()
            boolean bigLeft = nums1.size() > nums2.size()
            for (i in 0..<nums1.size()) {
                if (nums2.size() > i) {
                    result = nums1[i] <=> nums2[i]
                    if (result != 0) {
                        break
                    }
                    if(i == (nums1.size()-1) && bigRight) {
                       if(nums2[i+1] != 0)
                           result = -1; break
                    }
                }
                else if(bigLeft){
                   if(nums1[i] != 0)
                        result = 1; break
                }
            }
        }
        result
    }

    boolean equals(obj) { false }
    int hashCode() { super.hashCode() }
}
