/*
 * Copyright 2011-2013 the original author or authors.
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

import org.codehaus.griffon.artifacts.model.Release

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
class ArtifactDependency {
    private static VERSION_NUMBER_PATTERN = ~/(\d+)\.(\d+)(\.(\d).*?)?/

    final String name
    String version
    Release release

    int major = 0
    int minor = 0
    int revision = 0

    ArtifactRepository repository
    boolean installed
    boolean evicted
    boolean resolved
    boolean conflicted

    List<ArtifactDependency> dependencies = []

    ArtifactDependency(String name) {
        this.name = name
    }

    void setVersion(String version) {
        this.version = version

        if (version) {
            def m = version =~ VERSION_NUMBER_PATTERN
            major = m[0][1].toInteger()
            minor = m[0][2]?.toInteger() ?: 0i
            revision = m[0][4]?.toInteger() ?: 0i
        }
    }

    boolean isSnapshot() {
        version?.endsWith('-SNAPSHOT')
    }

    void updateConflicts() {
        for (dependency in dependencies) {
            dependency.updateConflicts()
            if (dependency.conflicted) conflicted = true
        }
    }

    void printout(int indent, PrintStream out = System.out, boolean includeDependencies = true) {
        out.print(' ' * indent)
        if (!resolved) {
            out.print '? '
        } else if (conflicted) {
            out.print '! '
        } else if (evicted) {
            out.print '- '
        } else if (!installed) {
            out.print '+ '
        } else {
            out.print '. '
        }
        out.print "${name}-${version ? version : '<noversion>'}"
        if (!installed) {
            out.print "${repository ? ' from ' + repository.name : ' not found in any repository'}"
        }
        out.println ''
        if (includeDependencies) {
            for (dependency in dependencies) {
                dependency.printout(indent + 3, out)
            }
        }
    }

    String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        PrintStream out = new PrintStream(baos)
        printout(0, out, false)
        baos.toString()
    }

    static class Key {
        final String name
        final String version

        Key(String name, String version) {
            this.name = name
            this.version = version
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (!(o instanceof Key)) return false

            Key key = (Key) o

            if (name != key.name) return false
            if (version != key.version) return false

            return true
        }

        int hashCode() {
            int result
            result = name.hashCode()
            result = 31 * result + version.hashCode()
            return result
        }

        String toString() {
            "${name}-${version}"
        }
    }
}
