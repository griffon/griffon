/*
 * Copyright 2009-2010 the original author or authors.
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

package griffon.core

/**
 * Helper class capable of dealing with artifacts and their handlers.
 *
 * @author Andres Almiray
 */
@Singleton
class ArtifactManager {
    GriffonApplication app
    private final Map artifacts = [:]
    private final Map artifactHandlers = [:]

    private static final ArtifactInfo[] EMPTY_ARTIFACT_INFO_ARRAY = new ArtifactInfo[0]
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0]

    /**
     * Registers an ArtifactHandler by type.<p>
     * Will call initialize() on the handler.
     */
    synchronized void registerArtifactHandler(ArtifactHandler handler) {
        if(!handler) return
        handler.app = app
        artifactHandlers[handler.type] = handler
        if(artifacts[handler.type]) handler.initialize(artifacts[handler.type])
    }

    /**
     * Removes an ArtifactHandler by type.
     */
    synchronized void unregisterArtifactHandler(ArtifactHandler handler) {
        if(!handler) return
        artifactHandlers.remove(handler.type)
    }

    /**
     * Reads the artifacts definitions file from the classpath.<p>
     * Will call initialize() on artifact handlers if there are any
     * registered already.
     */
    synchronized void loadArtifactMetadata() {
        def config = new ConfigSlurper().parse(app.class.getResource("/artifacts.properties"))
        config.each { type, classes -> 
            artifacts[type] = classes.split(",").collect([]){
                new ArtifactInfo(app, getClass().classLoader.loadClass(it), type)
            } as ArtifactInfo[]
            artifactHandlers[type]?.initialize(artifacts[type])
        }
    }

    /**
     * Retrieves an artifact metadata by class
     */
    synchronized ArtifactInfo getArtifactInfo(Class clazz) {
        if(!clazz) return null
        for(handler in artifactHandlers.values()) {
            ArtifactInfo result = handler.artifacts.find{ it.klass.name == clazz.name }
            if(result) return result
        }
        return null
    }

    /**
     * Retrieves an artifact metadata by class name
     */
    synchronized ArtifactInfo getArtifactInfo(String className) {
        if(!className) return null
        for(handler in artifactHandlers.values()) {
            // String suffix = handler.type[0].toUpperCase() + handler.type[1..-1]
            // if(className.endsWith(suffix)) return handler.artifacts.find{ it.klass.name == className }
            ArtifactInfo result = handler.artifacts.find{ it.klass.name == className }
            if(result) return result
        }
        return null
    }

    /**
     * Find and artifact by name and type.<p>
     * Example: getArtifactInfo("Book", "controller") will return an
     * artifact that describes BookController.
     */
    synchronized ArtifactInfo getArtifactInfo(String artifactName, String type) {
        if(!artifactName || !type) return null
        return artifactHandlers[type]?.findArtifact(artifactName)
    }

    /**
     * Returns all available artifacts of a particular type.<p>
     * Never returns null
     */
    ArtifactInfo[] getArtifactsOfType(String type) {
        artifacts[type] ?: EMPTY_ARTIFACT_INFO_ARRAY
    }

    /**
     * Returns all available classes of a particular type.<p>
     * Never returns null
     */
    Class[] getClassesOfType(String type) {
        if(artifacts.containsKey(type)) {
            return artifacts[type].toList().klass
        }
        return EMPTY_CLASS_ARRAY
    }

    ArtifactInfo[] getAllArtifacts() {
        List all = []
        artifacts.each { all.addAll(it.value.toList()) }
        return all as ArtifactInfo[]
    }

    Class[] getAllClasses() {
        List all = []
        artifacts.each { all.addAll(it.value.toList().klass) }
        return all as Class[]
    }

    def methodMissing(String methodName, args) {
        def artifactType = methodName =~ /^get(\w+)Artifacts$/
        if(artifactType) {
            artifactType = normalize(artifactType)
        
            if(!args && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&getArtifactsOfType.curry(artifactType)
                return getArtifactsOfType(artifactType)
            }
            return EMPTY_ARTIFACT_INFO_ARRAY
        }

        artifactType = methodName =~ /^get(\w+)Artifact$/
        if(artifactType) {
            artifactType = normalize(artifactType)
        
            if(args?.size() == 1 && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&getArtifactOfType.curry(artifactType)
                return getArtifactOfType(artifactType, args[0])
            }
            return EMPTY_ARTIFACT_INFO_ARRAY
        }

        artifactType = methodName =~ /^is(\w+)Artifact$/
        if(artifactType) {
            artifactType = normalize(artifactType)
        
            if(args?.size() == 1 && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&isClassOfType.curry(artifactType)
                return isClassOfType(artifactType, args[0])
            }
            return EMPTY_CLASS_ARRAY
        }

        artifactType = methodName =~ /^get(\w+)Classes$/
        if(artifactType) {
            artifactType = normalize(artifactType)

            if(!args && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&getClassesOfType.curry(artifactType)
                return getClassesOfType(artifactType)
            }
            return EMPTY_CLASS_ARRAY
        }

        artifactType = methodName =~ /^is(\w+)Class$/
        if(artifactType) {
            artifactType = normalize(artifactType)

            if(args?.size() == 1 && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&isClassOfType.curry(artifactType)
                return isClassOfType(artifactType, args[0])
            }
            return false
        }

        throw new MissingMethodException(methodName, ArtifactManager, args)
    }

    def propertyMissing(String propertyName) {
        def artifactType = propertyName =~ /^(\w+)Artifacts$/
        if(artifactType) {
            artifactType = artifactType[0][1]
            if(artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$propertyName" = getArtifactsOfType(artifactType)
                return getArtifactsOfType(artifactType)
            }
            return EMPTY_ARTIFACT_INFO_ARRAY
        }

        artifactType = propertyName =~ /^(\w+)Classes$/
        if(artifactType) {
            artifactType = artifactType[0][1]
            if(artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$propertyName" = getClassesOfType(artifactType)
                return getClassesOfType(artifactType)
            }
            return EMPTY_CLASS_ARRAY
        }
        throw new MissingPropertyException(propertyName, Object)
    }

    private synchronized ArtifactInfo getArtifactOfType(String type, Class klass) {
        artifactHandlers[type]?.artifacts.find { it.klass.name == klass.name }
    }

    private synchronized ArtifactInfo getArtifactOfType(String type, String className) {
        artifactHandlers[type]?.artifacts.find { it.klass.simpleName == className }
    }

    private synchronized boolean isClassOfType(String type, Class klass) {
        getArtifactOfType(type, klass) ? true : false
    }

    private String normalize(input) {
        input = input[0][1]
        input[0].toLowerCase() + input[1..-1]
    }
}
