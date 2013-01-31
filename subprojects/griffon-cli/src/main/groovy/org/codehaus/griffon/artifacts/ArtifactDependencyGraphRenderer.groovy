/*
 * Copyright 2013 the original author or authors.
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

package org.codehaus.griffon.artifacts

import griffon.util.BuildSettings
import griffon.util.Metadata
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.fusesource.jansi.Ansi

import static org.codehaus.griffon.cli.CommandLineConstants.KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY
import static org.fusesource.jansi.Ansi.Color.*

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
class ArtifactDependencyGraphRenderer {
    private static final String TOP_LEVEL_PREFIX = "+--- "
    private static final String INITIAL_TRANSITIVE_PREFIX = '|    '
    private static final String PADDING = "     "
    private static final String TRANSITIVE_PREFIX = '\\--- '

    boolean ansiEnabled = true
    boolean framework = false
    private final BuildSettings settings

    ArtifactDependencyGraphRenderer(BuildSettings settings) {
        this.settings = settings
    }

    void render(List<ArtifactDependency> dependencies) {
        Map<String, Release> installedReleases = settings.artifactSettings.getInstalledReleases(Plugin.TYPE, framework)
        def pw = new PrintWriter(new OutputStreamWriter(System.out))

        pw.println()
        if (ansiEnabled) {
            pw.println(new Ansi().a(Ansi.Attribute.INTENSITY_BOLD).fg(GREEN)
                .a(Metadata.current.getApplicationName())
                .fg(DEFAULT).a(Ansi.Attribute.INTENSITY_BOLD_OFF))
        } else {
            pw.println(Metadata.current.getApplicationName());
        }
        for (ArtifactDependency dependency : dependencies) {
            renderGraph(dependency, dependency.version, pw, 0, installedReleases)
        }
        if (ansiEnabled) {
            pw.print(new Ansi().a(Ansi.Attribute.INTENSITY_BOLD_OFF).fg(DEFAULT))
        }
        pw.println()
        pw.flush()
    }

    private renderGraph(ArtifactDependency dependency, String version, PrintWriter writer, int depth, Map<String, Release> installedReleases) {
        if (depth == 0) {
            def prefix = TOP_LEVEL_PREFIX
            writeDependency(writer, prefix, dependency, version)
        } else {
            if (ansiEnabled) {
                writer.print(new Ansi().a(Ansi.Attribute.INTENSITY_BOLD).fg(YELLOW).a(INITIAL_TRANSITIVE_PREFIX).fg(DEFAULT).a(Ansi.Attribute.INTENSITY_BOLD_OFF))
            } else {
                writer.print(INITIAL_TRANSITIVE_PREFIX)
            }

            if (depth > 1) {
                for (num in 1..(depth - 1)) {
                    writer.print(PADDING)
                }
            }

            writeDependency(writer, TRANSITIVE_PREFIX, dependency, version)
        }
        Release release = installedReleases[dependency.name]
        for (ArtifactDependency child : dependency.dependencies) {
            String v = '<unknown>'
            if (release) {
                v = release.dependencies.find{ it.name == child.name }?.version ?: child.version
            } else {
                v = child.version
            }
            renderGraph(child, v, writer, depth + 1, installedReleases)
        }
    }

    private void writeDependency(PrintWriter writer, String prefix, ArtifactDependency dependency, String version) {
        if (ansiEnabled) {
            Ansi.Color color = getColor(dependency, version)
            writer.println(new Ansi().a(Ansi.Attribute.INTENSITY_BOLD)
                .fg(YELLOW).a(prefix)
                .fg(color).a(getPrefix(dependency, version))
                .fg(color).a(toString(dependency, version))
                .fg(DEFAULT).a(Ansi.Attribute.INTENSITY_BOLD_OFF))
        } else {
            writer.println("$prefix${getPrefix(dependency, version)}${toString(dependency, version)}")
        }
    }

    private static Ansi.Color getColor(ArtifactDependency dependency, String version) {
        if (!dependency.resolved) {
            return YELLOW
        } else if (dependency.conflicted) {
            return RED
        } else if (dependency.evicted || dependency.version != version) {
            return MAGENTA
        } else if (!dependency.installed) {
            return GREEN
        }
        return WHITE
    }

    private static String getPrefix(ArtifactDependency dependency, String version) {
        if (!dependency.resolved) {
            return '? '
        } else if (dependency.conflicted) {
            return '! '
        } else if (dependency.evicted || dependency.version != version) {
            return '- '
        } else if (!dependency.installed) {
            return '+ '
        }
        return '. '
    }

    private String toString(ArtifactDependency dependency, String version) {
        StringBuilder b = new StringBuilder(dependency.name)
            .append('-')
            .append(version)
        if (dependency.repository) {
            b.append(' [')
                .append(dependency.repository.name)
                .append(']')
        } else if (dependency.installed) {
            String localRepository = settings.getConfigValue(KEY_DEFAULT_INSTALL_ARTIFACT_REPOSITORY, ArtifactRepository.DEFAULT_LOCAL_NAME)
            b.append(' [')
                .append(localRepository)
                .append(']')
        }
        b.toString()
    }
}
