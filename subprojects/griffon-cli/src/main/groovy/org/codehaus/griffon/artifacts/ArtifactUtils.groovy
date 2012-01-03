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
 * WITHOUT c;pWARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.artifacts

import griffon.util.BuildSettingsHolder
import griffon.util.GriffonUtil
import groovy.json.JsonSlurper
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import static griffon.util.GriffonNameUtils.capitalize
import org.codehaus.griffon.artifacts.model.*

/**
 * Common utilities for dealing with artifacts such as artifacts and archetypes.
 *
 * @author Andres Almiray
 * @since 0.9.5
 */
class ArtifactUtils {
    static final String PLUGIN_DESCRIPTOR_SUFFIX = 'GriffonPlugin.groovy'
    static final String ARCHETYPE_DESCRIPTOR_SUFFIX = 'GriffonArchetype.groovy'
    static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
    private static final PathMatchingResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver()

    static Resource[] resolveResources(String pattern) {
        try {
            return RESOLVER.getResources(pattern)
        }
        catch (Throwable e) {
            return [] as Resource[]
        }
    }

    static File findArtifactDirForName(String type, String name) {
        String basedir = artifactBase(type)
        Resource[] resources = resolveResources("file://${basedir}/${name}-*")
        if (resources) {
            if (resources.length == 1) {
                return resources[0].file
            } else {
                throw new IllegalArgumentException("Multiple installations of ${type} '${name}' exist in ${basedir}: ${resources.file.name}")
            }
        } else {
            return null
        }
    }

    static File[] findAllArtifactDirsForName(String type, String name) {
        String basedir = artifactBase(type)
        Resource[] resources = resolveResources("file://${basedir}/${name}-*")
        if (resources) {
            return (resources.file) as File[]
        } else {
            return new File[0]
        }
    }

    static Release getReleaseFromMetadata(String type, String name, String version = null) {
        File file = null
        if (version) {
            file = new File("${artifactBase(type)}/${name}-${version}/${type}.json")
            if (!file.exists()) {
                throw new IllegalArgumentException("${capitalize(type)} ${name}-${version} is not installed.")
            }
        } else {
            file = findArtifactDirForName(type, name)
            if (!file || !file.exists()) {
                throw new IllegalArgumentException("${capitalize(type)} ${name}-${version} is not installed.")
            }
        }
        Release.makeFromJSON(type, new JsonSlurper().parseText(file.text))
    }

    static String artifactBase(String type) {
        switch (type) {
            case Plugin.TYPE:
                return pluginsBase()
            case Archetype.TYPE:
                return archetypesBase()
        }
    }

    static String pluginsBase() {
        BuildSettingsHolder.settings.projectPluginsDir.absolutePath
    }

    static String archetypesBase() {
        new File("${BuildSettingsHolder.settings.griffonWorkDir}/archetypes/").absolutePath
    }

    static Resource getArtifactDescriptor(String type, String dir) {
        switch (type) {
            case Plugin.TYPE:
                return getPluginDescriptor(dir)
            case Archetype.TYPE:
                return getArchetypeDescriptor(dir)
        }
    }

    static Resource getArtifactDescriptor(String type, Resource dir) {
        switch (type) {
            case Plugin.TYPE:
                return getPluginDescriptor(dir)
            case Archetype.TYPE:
                return getArchetypeDescriptor(dir)
        }
    }

    static String getArtifactNameFromDescriptor(String type, String dir) {
        switch (type) {
            case Plugin.TYPE:
                return getPluginNameFromDescriptor(dir)
            case Archetype.TYPE:
                return getArchetypeNameFromDescriptor(dir)
        }
    }

    static String getArtifactNameFromDescriptor(String type, Resource dir) {
        switch (type) {
            case Plugin.TYPE:
                return getPluginNameFromDescriptor(dir)
            case Archetype.TYPE:
                return getArchetypeNameFromDescriptor(dir)
        }
    }

    static Resource getPluginDescriptor(String dir) {
        getPluginDescriptor(new FileSystemResource(dir))
    }

    static Resource getPluginDescriptor(Resource dir) {
        File f = dir?.file?.listFiles()?.find { it.name.endsWith(PLUGIN_DESCRIPTOR_SUFFIX) }
        f ? new FileSystemResource(f) : null
    }

    static Resource getArchetypeDescriptor(String dir) {
        getArchetypeDescriptor(new FileSystemResource(dir))
    }

    static Resource getArchetypeDescriptor(Resource dir) {
        File f = dir?.file?.listFiles()?.find { it.name.endsWith(ARCHETYPE_DESCRIPTOR_SUFFIX) }
        f ? new FileSystemResource(f) : null
    }

    static String getPluginNameFromDescriptor(Resource artifactDescriptor) {
        return getPluginNameFromDescriptor(artifactDescriptor.file.name)
    }

    static String getPluginNameFromDescriptor(String fileName) {
        String artifactName = fileName.endsWith(PLUGIN_DESCRIPTOR_SUFFIX) ? fileName[0..-(PLUGIN_DESCRIPTOR_SUFFIX.length() + 1)] : fileName
        GriffonUtil.getHyphenatedName(artifactName)
    }

    static String getArchetypeNameFromDescriptor(Resource archetypeDescriptor) {
        return getArchetypeNameFromDescriptor(archetypeDescriptor.file.name)
    }

    static String getArchetypeNameFromDescriptor(String fileName) {
        String artifactName = fileName.endsWith(ARCHETYPE_DESCRIPTOR_SUFFIX) ? fileName[0..-(ARCHETYPE_DESCRIPTOR_SUFFIX.length() + 1)] : fileName
        GriffonUtil.getHyphenatedName(artifactName)
    }

    static Artifact parseArtifact(String type, json) {
        switch (type) {
            case Plugin.TYPE:
                return parsePluginFromJSON(json)
            case Archetype.TYPE:
                return parseArchetypeFromJSON(json)
        }
    }

    static Archetype parseArchetypeFromJSON(json) {
        Archetype archetype = new Archetype(
                name: json.name,
                title: json.title,
                description: json.description,
                license: json.license,
                source: json.source,
                authors: json.authors.collect([]) { author ->
                    new Author(name: author.name, email: author.email)
                },
                releases: json.releases ? json.releases.collect([]) {parseReleaseFromJSON(it)} : []
        )
        archetype.releases.each { it.artifact = archetype }
        archetype
    }

    static Plugin parsePluginFromJSON(json) {
        Plugin plugin = new Plugin(
                name: json.name,
                title: json.title,
                description: json.description,
                license: json.license,
                source: json.source,
                authors: json.authors.collect([]) { author ->
                    new Author(name: author.name, email: author.email)
                },
                releases: json.releases ? json.releases.collect([]) {parseReleaseFromJSON(it)} : [],
                toolkits: json.toolkits.collect([]) { toolkit ->
                    Toolkit.findByName(toolkit)
                },
                platforms: json.platforms.collect([]) { platform ->
                    Platform.findByName(platform)
                }
        )
        plugin.releases.each { it.artifact = plugin }
        plugin
    }

    static Release parseReleaseFromJSON(json) {
        new Release(
                version: json.version,
                griffonVersion: json.griffonVersion,
                checksum: json.checksum,
                date: json.date ? Date.parse(TIMESTAMP_FORMAT, json.date) : null,
                dependencies: json.dependencies.collect([]) { dep ->
                    [name: dep.name, version: dep.version]
                },
        )
    }

    /**
     * Check if the required version is a valid for the given artifact version
     *
     * @param artifactVersion The artifact version
     * @param requiredVersion The required version
     * @return true if it is valid
     */
    static boolean isValidVersion(String artifactVersion, String requiredVersion) {
        def vc = new VersionComparator()
        artifactVersion = trimTag(artifactVersion);

        if (requiredVersion.indexOf('>') > -1) {
            def tokens = requiredVersion.split('>')*.trim()
            tokens = tokens.collect { trimTag(it) }
            tokens << artifactVersion
            tokens = tokens.sort(vc)

            if (tokens[1] == artifactVersion) return true
        }
        else if (artifactVersion == trimTag(requiredVersion)) return true
        return false
    }

    /**
     * Compare two artifact versions
     *
     * @param artifactVersion The artifact version
     * @param requiredVersion The required version
     * @return 0 if equal; &lt; 0 if artifactVersion is smaller; &gt; 0 if artifactVersion is greater
     */
    static int compareVersions(String artifactVersion, String requiredVersion) {
        def vc = new VersionComparator()
        artifactVersion = trimTag(artifactVersion);

        if (requiredVersion.indexOf('>') > -1) {
            def tokens = requiredVersion.split('>')*.trim()
            tokens = tokens.collect { trimTag(it) }
            tokens << artifactVersion
            tokens = tokens.sort(vc)

            return artifactVersion <=> tokens[1]
        }
        return artifactVersion <=> requiredVersion
    }

    /**
     * Returns the upper version of a Griffon version number expression in a artifact
     */
    static String getUpperVersion(String artifactVersion) {
        return getArtifactVersionInternal(artifactVersion, 1)
    }

    /**
     * Returns the lower version of a Griffon version number expression in a artifact
     */
    static String getLowerVersion(String artifactVersion) {
        return getArtifactVersionInternal(artifactVersion, 0)
    }

    static boolean supportsAtLeastVersion(String artifactVersion, String requiredVersion) {
        def lowerVersion = getLowerVersion(artifactVersion)
        lowerVersion != '*' && isValidVersion(lowerVersion, "$requiredVersion > *")
    }

    private static getArtifactVersionInternal(String artifactVersion, index) {
        if (artifactVersion.indexOf('>') > -1) {
            def tokens = artifactVersion.split(">")*.trim()
            return tokens[index].trim()
        }
        else {
            return artifactVersion.trim()
        }
    }

    private static trimTag(String artifactVersion) {
        def i = artifactVersion.indexOf('-')
        if (i > -1) {
            artifactVersion = artifactVersion[0..i - 1]
        }

        def copy = artifactVersion.reverse()
        for (c in copy) {
            if (c =~ /[a-zA-Z]/) artifactVersion = artifactVersion[0..-2]
        }
        def tokens = artifactVersion.split(/\./)

        return tokens.findAll { it ==~ /\d+/ || it == '*'}.join(".")
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
                    if (i == (nums1.size() - 1) && bigRight) {
                        if (nums2[i + 1] != 0)
                            result = -1; break
                    }
                }
                else if (bigLeft) {
                    if (nums1[i] != 0)
                        result = 1; break
                }
            }
        }
        result
    }

    boolean equals(obj) { false }

    int hashCode() { System.identityHashCode(this) }
}
