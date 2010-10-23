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

import org.codehaus.griffon.runtime.util.GriffonApplicationHelper

/**
 * Helper class capable of dealing with artifacts and their handlers.
 *
 * @author Andres Almiray
 */
class ArtifactManager {
    GriffonApplication app

    private final Map<String, ArtifactInfo[]> artifacts = [:]
    private final Map<String, ArtifactHandler> artifactHandlers = [:]

    private static final ArtifactManager INSTANCE
    private static final GriffonClass[] EMPTY_GRIFFON_CLASS_ARRAY = new GriffonClass[0]
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0]

    static {
        INSTANCE = new ArtifactManager()
    }

    static ArtifactManager getInstance() {
        INSTANCE
    }

    /**
     * Registers an ArtifactHandler by type.<p>
     * Will call initialize() on the handler.
     */
    synchronized void registerArtifactHandler(ArtifactHandler handler) {
        if(!handler) return
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
        Enumeration urls = app.class.classLoader.getResources('META-INF/griffon-artifacts.properties')
        Map<String, List<ArtifactInfo>> _artifacts = [:]
        urls.each { url ->
            def config = new ConfigSlurper().parse(url)
            config.each { type, classes -> 
                List<ArtifactInfo> artifactList = _artifacts.get(type, [])
                classes.split(',').collect(artifactList) {
                    new ArtifactInfo(GriffonApplicationHelper.loadClass(app, it), type)
                }
            }
        }

        _artifacts.each { type, list ->
            artifacts[type] = (list as ArtifactInfo[])
            artifactHandlers[type]?.initialize(artifacts[type])
        }
    }

    /**
     * Finds an artifact by name and type.<p>
     * Example: findGriffonClass("Book", "controller") will return an
     * artifact class that describes BookController.
     */
    synchronized GriffonClass findGriffonClass(String name, String type) {
        if(!name || !type) return null
        return artifactHandlers[type]?.findClassFor(name)
    }

    /**
     * Finds an artifact by class and type.<p>
     * Example: findGriffonClass(BookController, "controller") will return an
     * artifact class that describes BookController.
     */
    synchronized GriffonClass findGriffonClass(Class clazz, String type) {
        if(!clazz || !type) return null
        return artifactHandlers[type]?.getClassFor(clazz)
    }

    /**
     * Finds an artifact by class.<p>
     * Example: findGriffonClass(aBookControllerInstance) will return an
     * artifact class that describes BookController.
     */
    synchronized GriffonClass findGriffonClass(Object obj) {
        if(obj == null) return null
        return findGriffonClass(obj.getClass())
    }

    /**
     * Finds an artifact by class.<p>
     * Example: findGriffonClass(BookController) will return an
     * artifact class that describes BookController.
     */
    synchronized GriffonClass findGriffonClass(Class clazz) {
        if(!clazz) return null
        for(handler in artifactHandlers.values()) {
            GriffonClass griffonClass = handler.getClassFor(clazz)
            if(griffonClass) return griffonClass
        }
        return null
    }

    /**
     * Finds an artifact by name.<p>
     * Example: findGriffonClass("BookController") will return an
     * artifact class that describes BookController.
     */
    synchronized GriffonClass findGriffonClass(String fqnClassName) {
        if(!fqnClassName) return null
        for(handler in artifactHandlers.values()) {
            GriffonClass griffonClass = handler.getClassFor(fqnClassName)
            if(griffonClass) return griffonClass
        }
        return null
    }

    /**
     * Finds all artifacts of an specific type.<p>
     * Example: getClassesOfType("controller") will return all
     * artifact classes that describe controllers.
     */
    synchronized GriffonClass[] getClassesOfType(String type) {
        if(artifacts.containsKey(type)) {
            return artifactHandlers[type].classes
        }
        return EMPTY_GRIFFON_CLASS_ARRAY
    }

    /**
     * Finds all artifact classes.<p>
     */
    synchronized GriffonClass[] getAllClasses() {
        List all = []
        artifactHandlers.each { k, h -> all.addAll(h.getClasses().toList()) }
        return all as GriffonClass[]
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
        if(artifactType) {
            artifactType = normalize(artifactType)

            if(!args && artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$methodName" = this.&getClassesOfType.curry(artifactType)
                return getClassesOfType(artifactType)
            }
            return EMPTY_GRIFFON_CLASS_ARRAY
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
        if(artifactType) {
            artifactType = artifactType[0][1]
            if(artifacts.containsKey(artifactType)) {
                ArtifactManager.metaClass."$propertyName" = getClassesOfType(artifactType)
                return getClassesOfType(artifactType)
            }
            return EMPTY_GRIFFON_CLASS_ARRAY
        }

        throw new MissingPropertyException(propertyName, Object)
    }

    private synchronized ArtifactInfo getArtifactOfType(String type, Class clazz) {
        artifactHandlers[type]?.artifacts.find { it.clazz.name == clazz.name }
    }

    private synchronized boolean isClassOfType(String type, Class clazz) {
        getArtifactOfType(type, clazz) ? true : false
    }

    private String normalize(input) {
        input = input[0][1]
        input[0].toLowerCase() + input[1..-1]
    }
}
