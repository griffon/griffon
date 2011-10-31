/*
 * Copyright 2009-2011 the original author or authors.
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

package org.codehaus.griffon.runtime.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import griffon.core.*

/**
 * Base implementation of the {@code ArtifactManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
abstract class AbstractArtifactManager implements ArtifactManager {
    final GriffonApplication app

    private final Map<String, ArtifactInfo[]> artifacts = [:]
    private final Map<String, ArtifactHandler> artifactHandlers = [:]
    private final Object lock = new Object()

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArtifactManager)

    AbstractArtifactManager(GriffonApplication app) {
        this.app = app
    }

    protected Map<String, ArtifactInfo[]> getArtifacts() {
        artifacts
    }

    protected Map<String, ArtifactHandler> getArtifactHandlers() {
        artifactHandlers
    }

    final void loadArtifactMetadata() {
        Map<String, List<ArtifactInfo>> loadedArtifacts = doLoadArtifactMetadata()

        synchronized (lock) {
            loadedArtifacts.each { type, list ->
                artifacts[type] = (list as ArtifactInfo[])
                artifactHandlers[type]?.initialize(artifacts[type])
            }
        }
    }

    // commented out generics because it fails with groovy 1.8.1
    protected abstract Map/*<String, List<ArtifactInfo>>*/ doLoadArtifactMetadata()

    void registerArtifactHandler(ArtifactHandler handler) {
        if (!handler) return
        if (LOG.infoEnabled) LOG.info("Registering artifact handler for type '${handler.type}': $handler")
        synchronized (lock) {
            artifactHandlers[handler.type] = handler
            if (artifacts[handler.type]) handler.initialize(artifacts[handler.type])
        }
    }

    void unregisterArtifactHandler(ArtifactHandler handler) {
        if (!handler) return
        if (LOG.infoEnabled) LOG.info("Removing artifact handler for type '${handler.type}': $handler")
        synchronized (lock) {
            artifactHandlers.remove(handler.type)
        }
    }

    GriffonClass findGriffonClass(String name, String type) {
        if (!name || !type) return null
        synchronized (lock) {
            return artifactHandlers[type]?.findClassFor(name)
        }
    }

    GriffonClass findGriffonClass(Class clazz, String type) {
        if (!clazz || !type) return null
        synchronized (lock) {
            return artifactHandlers[type]?.getClassFor(clazz)
        }
    }

    GriffonClass findGriffonClass(Object obj) {
        if (obj == null) return null
        synchronized (lock) {
            return findGriffonClass(obj.getClass())
        }
    }

    GriffonClass findGriffonClass(Class clazz) {
        if (!clazz) return null
        synchronized (lock) {
            for (handler in artifactHandlers.values()) {
                GriffonClass griffonClass = handler.getClassFor(clazz)
                if (griffonClass) return griffonClass
            }
        }
        return null
    }

    GriffonClass findGriffonClass(String fqnClassName) {
        if (!fqnClassName) return null
        synchronized (lock) {
            for (handler in artifactHandlers.values()) {
                GriffonClass griffonClass = handler.getClassFor(fqnClassName)
                if (griffonClass) return griffonClass
            }
        }
        return null
    }

    List<GriffonClass> getClassesOfType(String type) {
        synchronized (lock) {
            if (artifacts.containsKey(type)) {
                return artifactHandlers[type].classes.toList()
            }
        }
        return EMPTY_GRIFFON_CLASS_LIST
    }

    List<GriffonClass> getAllClasses() {
        List<GriffonClass> all = []
        synchronized (lock) {
            artifactHandlers.each { k, h -> all.addAll(h.getClasses().toList()) }
        }
        return Collections.unmodifiableList(all)
    }

    /**
     * Adds dynamic handlers for querying artifact classes.<p>
     * The following patterns are recognized<ul>
     * <li>getXXXClasses</li>
     * <li>isXXXClass</li>
     * </ul>
     * where {@code XXX} stands for the name of an artifact, like
     * "Controller" or "Service".
     */
    def methodMissing(String methodName, args) {
        def artifactType = methodName =~ /^get(\w+)Classes$/
        if (artifactType) {
            artifactType = normalize(artifactType)

            if (!args && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&getClassesOfType.curry(artifactType)
                return getClassesOfType(artifactType)
            }
            return EMPTY_GRIFFON_CLASS_ARRAY
        }

        artifactType = methodName =~ /^is(\w+)Class$/
        if (artifactType) {
            artifactType = normalize(artifactType)

            if (args?.size() == 1 && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&isClassOfType.curry(artifactType)
                return isClassOfType(artifactType, args[0])
            }
            return false
        }

        throw new MissingMethodException(methodName, ArtifactManager, args)
    }

    /**
     * Adds dynamic handlers for querying artifact classes.<p>
     * The following patterns are recognized<ul>
     * <li>xXXClasses</li>
     * </ul>
     * where {@code xXX} stands for the name of an artifact, like
     * "controller" or "service".
     */
    def propertyMissing(String propertyName) {
        def artifactType = propertyName =~ /^(\w+)Classes$/
        if (artifactType) {
            artifactType = artifactType[0][1]
            if (artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$propertyName" = getClassesOfType(artifactType)
                return getClassesOfType(artifactType)
            }
            return EMPTY_GRIFFON_CLASS_ARRAY
        }

        throw new MissingPropertyException(propertyName, Object)
    }

    protected ArtifactInfo getArtifactOfType(String type, Class clazz) {
        synchronized (lock) {
            artifactHandlers[type]?.artifacts.find { it.clazz.name == clazz.name }
        }
    }

    protected boolean isClassOfType(String type, Class clazz) {
        getArtifactOfType(type, clazz) ? true : false
    }

    private String normalize(input) {
        input = input[0][1]
        input[0].toLowerCase() + input[1..-1]
    }
}
