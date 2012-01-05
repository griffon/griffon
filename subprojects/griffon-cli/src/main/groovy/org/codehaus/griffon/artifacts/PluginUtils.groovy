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

import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release
import org.springframework.core.io.Resource
import static org.codehaus.griffon.artifacts.ArtifactUtils.findAllArtifactDirsForType
import static org.codehaus.griffon.artifacts.ArtifactUtils.getArtifactRelease

/**
 * Common utilities for dealing with plugins.
 *
 * @author Andres Almiray
 * @since 0.9.5
 */
class PluginUtils {
    static Resource[] getSortedPluginDirectories() {
        Map noDependencies = [:]
        Map withDependencies = [:]
        List sorted = []
        findAllArtifactDirsForType(Plugin.TYPE).each { Resource r ->
            Release release = getArtifactRelease(Plugin.TYPE, r.file)
            if (release.dependencies) {
                withDependencies[release.artifact.name] = [deps: release.dependencies, dir: r]
            } else {
                noDependencies[release.artifact.name] = r
                sorted << [name: release.artifact.name, dir: r]
            }
        }

        def resolveDependencies
        resolveDependencies = { name, values ->
            if (sorted.find { it.name == name }) return
            values.deps.each { entry ->
                if (withDependencies[entry.name]) resolveDependencies(entry.name, withDependencies[entry.name])
            }
            def index = -1
            values.deps.each { entry ->
                index = Math.max(index, sorted.indexOf(sorted.find {it.name == entry.name}))
            }
            sorted = insert(sorted, [name: name, dir: values.dir], index + 1)
        }

        withDependencies.each(resolveDependencies)
        sorted.collect([]) {it.dir} as Resource[]
    }

    private static List insert(List list, obj, int index) {
        int size = list.size()
        if (index >= size) {
            list[index] = obj
            return list
        } else {
            def head = list[0..<index]
            def tail = list[index..-1]
            return head + [obj] + tail
        }
    }
}
