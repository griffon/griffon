/*
 * Copyright 2009 the original author or authors.
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

import griffon.util.IGriffonApplication

/**
 * Helper class capable of dealing with artefacts and their handlers.
 *
 * @author Andres Almiray (aalmiray)
 */
@Singleton
class ArtefactManager {
    IGriffonApplication app
    private final Map artefacts = [:]
    private final Map artefactHandlers = [:]

    /**
     * Registers an ArtefactHandler by type.<p>
     * Will call initialize() on the handler.
     */
    synchronized void registerArtefactHandler(ArtefactHandler handler) {
        if(!handler) return
        handler.app = app
        artefactHandlers[handler.type] = handler
        if(artefacts[handler.type]) handler.initialize(artefacts[handler.type])
    }

    /**
     * Removes an ArtefactHandler by type.
     */
    synchronized void unregisterArtefactHandler(ArtefactHandler handler) {
        if(!handler) return
        artefactHandlers.remove(handler.type)
    }

    /**
     * Reads the artefacts definitions file from the classpath.<p>
     * Will call initialize() on artefact handlers if there are any
     * registered already.
     */
    synchronized void loadArtefactMetadata() {
        def config = new ConfigSlurper().parse(app.class.getResource("/artefacts.properties"))
        config.each { type, classes -> 
            artefacts[type] = classes.split(",").collect([]){
                new ArtefactInfo(app, getClass().classLoader.loadClass(it), type)
            } as ArtefactInfo[]
            artefactHandlers[type]?.initialize(artefacts[type])
        }
    }

    /**
     * Retrieves an artefact metadata by class name
     */
    synchronized ArtefactInfo getArtefactInfo(String className) {
        if(!className) return null
        for(handler in artefactHandlers.values()) {
            String suffix = handler.type[0].toUpperCase() + handler.type[1..-1]
            if(className.endsWith(suffix)) return handler.artefacts.find{ it.klass.name == className }
        }
        return null
    }

    /**
     * Find and artefact by name and type.<p>
     * Example: getArtefactInfo("Book", "controller") will return an
     * artefact that describes BookController.
     */
    synchronized ArtefactInfo getArtefactInfo(String artefactName, String type) {
        if(!artefactName || !type) return null
        return artefactHandlers[type]?.findArtefact(artefactName)
    }

    /**
     * Returns all available artefacts of a particular type.<p>
     * Never returns null
     */
    synchronized ArtefactInfo[] getArtefactsOfType(String type) {
        artefacts[type] ?: new ArtefactInfo[0]
    }
}
