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

import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.codehaus.griffon.artifacts.VersionComparator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import org.codehaus.griffon.artifacts.model.*

import static griffon.util.GriffonNameUtils.capitalize
import static griffon.util.GriffonNameUtils.isBlank

/**
 * Common utilities for dealing with artifacts such as plugins and archetypes.
 *
 * @author Andres Almiray
 * @since 0.9.5
 */
class ArtifactSettings {
    private static final Logger LOG = LoggerFactory.getLogger(ArtifactSettings)
    static final String PLUGIN_DESCRIPTOR_SUFFIX = 'GriffonPlugin.groovy'
    static final String ARCHETYPE_DESCRIPTOR_SUFFIX = 'GriffonArchetype.groovy'
    static final String ADDON_DESCRIPTOR_SUFFIX = 'GriffonAddon.groovy'
    static final String ADDON_DESCRIPTOR_SUFFIX_JAVA = 'GriffonAddon.java'
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
    private static final Pattern ARTIFACT_NAME_VERSION_PATTERN = Pattern.compile('([a-zA-Z0-9\\-/\\._+=]+?)-([0-9][a-zA-Z0-9\\-/\\.,\\]\\[\\(\\)_+=]+)')

    final BuildSettings settings

    ArtifactSettings(BuildSettings settings) {
        this.settings = settings
    }

    Resource[] resolveResources(String pattern) {
        try {
            return settings.resolveResourcesClosure(pattern)
        }
        catch (Throwable e) {
            return [] as Resource[]
        }
    }

    /**
     * Finds all artifacts of the given type that are installed.
     *
     * @param type one of <tt>Archetype.TYPE</tt> or <tt>Plugin.TYPE</tt>.
     * @return
     */
    Map<String, String> getInstalledArtifacts(String type, boolean framework = false) {
        Map artifacts = [:]

        for (resource in resolveResources("file://${artifactBase(type, framework)}/*/${type}.json")) {
            Release release = Release.makeFromFile(type, resource.file)
            artifacts[release.artifact.name] = release.version
        }

        // TODO LEGACY - remove this code before 1.0
        // legacy plugins
        if (type == Plugin.TYPE) {
            for (resource in resolveResources("file://${artifactBase(type, framework)}/*/plugin.xml")) {
                Release release = Release.makeFromFile(type, resource.file)
                if (artifacts[release.artifact.name]) continue
                artifacts[release.artifact.name] = release.version
            }
        }

        artifacts
    }

    Map<String, Release> getInstalledReleases(String type, boolean framework = false) {
        Map<String, Release> artifacts = [:]

        for (resource in resolveResources("file://${artifactBase(type, framework)}/*/${type}.json")) {
            Release release = Release.makeFromFile(type, resource.file)
            artifacts[release.artifact.name] = release
        }

        // TODO LEGACY - remove this code before 1.0
        // legacy plugins
        if (type == Plugin.TYPE) {
            for (resource in resolveResources("file://${artifactBase(type, framework)}/*/plugin.xml")) {
                Release release = Release.makeFromFile(type, resource.file)
                if (artifacts[release.artifact.name]) continue
                artifacts[release.artifact.name] = release
            }
        }

        artifacts
    }

    Release getInstalledRelease(String type, String name, boolean framework = false) {
        Resource[] resources = resolveResources("file://${artifactBase(type, framework)}/${name}-*/${type}.json")
        for (resource in resources) {
            Matcher matcher = ARTIFACT_NAME_VERSION_PATTERN.matcher(resource.file.name)
            if (matcher[0][1] == name) return Release.makeFromFile(type, resource.file)
        }
        return null
    }

    Release getInstalledRelease(String type, String name, String version, boolean framework = false) {
        Resource[] resources = resolveResources("file://${artifactBase(type, framework)}/${name}-${version}/${type}.json")
        if (resources[0]?.file?.exists()) {
            return Release.makeFromFile(type, resources[0].file)
        }
        return null
    }

    /**
     * Finds all artifacts of the given type that are registered with the project's metadata.
     *
     * @param type one of <tt>Archetype.TYPE</tt> or <tt>Plugin.TYPE</tt>.
     * @return
     */
    static Map<String, String> getRegisteredArtifacts(String type, Metadata metadata = Metadata.current) {
        Map artifacts = [:]

        switch (type) {
            case Archetype.TYPE:
                String property = metadata.propertyNames().find {it.startsWith('archetype.')}
                if (property) {
                    String name = property - 'archetype.'
                    String version = metadata[property]
                    artifacts[name] = version
                }
                break
            case Plugin.TYPE:
                metadata.propertyNames().grep {it.startsWith('plugins.')}.each { property ->
                    String name = property - 'plugins.'
                    String version = metadata[property]
                    artifacts[name] = version
                }
                break
        }

        artifacts
    }

    File findArtifactDirForName(String type, String name, boolean framework = false) {
        if (LOG.debugEnabled) {
            LOG.debug("Searching dir matching file://${artifactBase(type, framework)}/${name}-*")
        }
        Resource[] resources = resolveResources("file://${artifactBase(type, framework)}/${name}-*")
        for (resource in resources) {
            Matcher matcher = ARTIFACT_NAME_VERSION_PATTERN.matcher(resource.file.name)
            if (matcher[0][1] == name) return resource.file
        }
        return null
    }

    File[] findAllArtifactDirsForName(String type, String name, boolean framework = false) {
        if (LOG.debugEnabled) {
            LOG.debug("Searching all dirs matching file://${artifactBase(type, framework)}/${name}-*")
        }
        Resource[] resources = resolveResources("file://${artifactBase(type, framework)}/${name}-*")
        if (resources) {
            List<File> files = []
            for (resource in resources) {
                Matcher matcher = ARTIFACT_NAME_VERSION_PATTERN.matcher(resource.file.name)
                if (matcher[0][1] == name) files << resource.file
            }
            return files as File[]
        }
        return new File[0]
    }

    Resource[] findAllArtifactDirsForType(String type, boolean framework = false) {
        resolveResources("file://${artifactBase(type, framework)}/*")
    }

    File getInstallPathFor(String type, String name, String version, boolean framework = false) {
        new File("${artifactBase(type, framework)}/${name}-${version}")
    }

    boolean isArtifactInstalled(String type, String name, String version, boolean framework = false) {
        getInstallPathFor(type, name, version, framework).exists()
    }

    Release getReleaseFromMetadata(String type, String name, String version = null, boolean framework = false) {
        File file = null
        if (version) {
            file = new File("${artifactBase(type, framework)}/${name}-${version}/${type}.json")
            if (!file.exists()) {
                throw new IllegalArgumentException("${capitalize(type)} ${name}-${version} is not installed.")
            }
        } else {
            file = findArtifactDirForName(type, name, framework)
            if (!file || !file.exists()) {
                throw new IllegalArgumentException("${capitalize(type)} ${name}-${version} is not installed.")
            }
        }
        Release.makeFromJSON(type, new JsonSlurper().parseText(file.text))
    }

    String artifactBase(String type, boolean framework = false) {
        switch (type) {
            case Plugin.TYPE:
                return pluginsBase(framework)
            case Archetype.TYPE:
                return archetypesBase()
        }
    }

    private String pluginsBase(boolean framework) {
        framework ? new File("${settings.griffonHome}/plugins") : settings.projectPluginsDir.absolutePath
    }

    private String archetypesBase() {
        new File("${settings.griffonWorkDir}/archetypes/").absolutePath
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

    static Release getArtifactRelease(String type, String dir) {
        getArtifactRelease(type, new File(dir))
    }

    static Release getArtifactRelease(String type, File dir) {
        Release.makeFromFile(type, new File("${dir}/${type}.json"))
    }

    static Artifact parseArtifactFromJSON(String type, json) {
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
                documentation: json.documentation,
                authors: json.authors.collect([]) { author ->
                    new Author(name: author.name, email: author.email)
                },
                releases: json.releases ? json.releases.collect([]) {parseReleaseFromJSON(it)} : []
        )
        if (json.release) {
            if (!archetype.releases) archetype.releases = []
            archetype.releases << parseReleaseFromJSON(json.release)
        }
        archetype.releases.each { it.artifact = archetype }
        archetype.releases.sort()
        archetype
    }

    static Plugin parsePluginFromJSON(json) {
        Plugin plugin = new Plugin(
                name: json.name,
                title: json.title,
                description: json.description,
                license: json.license,
                source: json.source,
                documentation: json.documentation,
                authors: json.authors.collect([]) { author ->
                    new Author(name: author.name, email: author.email)
                },
                releases: json.releases ? json.releases.collect([]) {parseReleaseFromJSON(it)} : [],
                toolkits: json.toolkits.collect([]) { toolkit ->
                    Toolkit.findByName(toolkit)
                },
                platforms: json.platforms.collect([]) { platform ->
                    Platform.findByName(platform)
                },
                framework: json.framework ?: false
        )
        if (json.release) {
            if (!plugin.releases) plugin.releases = []
            plugin.releases << parseReleaseFromJSON(json.release)
        }
        plugin.releases.each { it.artifact = plugin }
        plugin.releases.sort()
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
                }
        )
    }


    static Artifact parseArtifactFromXML(String type, xml) {
        switch (type) {
            case Plugin.TYPE:
                return parsePluginFromXML(xml)
            case Archetype.TYPE:
                return null
        }
    }

    static Release parseReleaseFromXML(xml) {
        new Release(
                version: xml.@version.text(),
                griffonVersion: xml.@griffonVersion?.text() ?: '0.3 < *',
                date: new Date(),
                dependencies: (xml.dependencies?.plugin ?: [:]).collect([]) { plugin ->
                    [name: plugin.@name.text(), version: plugin.@version.text()]
                }
        )
    }

    static Plugin parsePluginFromXML(xml) {
        new Plugin(
                name: xml.@name.text(),
                title: xml.title.text(),
                description: xml.description?.text() ?: '',
                license: xml.license?.text() ?: '<UNKNOWN>',
                source: '',
                documentation: '',
                authors: [
                        new Author(
                                name: xml.author?.text() ?: '',
                                email: xml.authorEmail?.text() ?: ''
                        )
                ],
                toolkits: (xml.toolkits?.text()?.split(',') ?: []).inject([]) { l, toolkit ->
                    if (!isBlank(toolkit)) l << Toolkit.findByName(toolkit)
                    l
                },
                platforms: (xml.platforms?.text()?.split(',') ?: []).inject([]) { l, platform ->
                    if (!isBlank(platform)) l << Platform.findByName(platform)
                    l
                },
                framework: false
        )
    }

    static Release createReleaseFromMetadata(String type, File file) {
        ZipFile zipFile = new ZipFile(file.absolutePath)
        ZipEntry artifactEntry = zipFile.getEntry(type + '.json')
        if (artifactEntry == null) {
            throw new IllegalArgumentException("Not a valid griffon artifact of type $type: missing ${type}.json")
        }

        try {
            def json = new JsonSlurper().parseText(zipFile.getInputStream(artifactEntry).text)
            Release.makeFromJSON(type, json)
        } catch (JsonException e) {
            throw new IllegalArgumentException("Can't parse ${type}.json", e)
        }
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
