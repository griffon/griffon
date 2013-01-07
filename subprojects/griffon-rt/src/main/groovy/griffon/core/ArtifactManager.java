/*
 * Copyright 2009-2013 the original author or authors.
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

package griffon.core;

import java.util.Collections;
import java.util.List;

/**
 * Helper class capable of dealing with artifacts and their handlers.
 *
 * @author Andres Almiray
 */
public interface ArtifactManager extends ApplicationHandler {
    GriffonClass[] EMPTY_GRIFFON_CLASS_ARRAY = new GriffonClass[0];
    List<GriffonClass> EMPTY_GRIFFON_CLASS_LIST = Collections.<GriffonClass>emptyList();

    /**
     * Registers an ArtifactHandler by type.<p>
     * Should call initialize() on the handler.
     *
     * @param handler an ArtifactHandler
     */
    void registerArtifactHandler(ArtifactHandler handler);

    /**
     * Removes an ArtifactHandler by type.
     *
     * @param handler an ArtifactHandler
     */
    void unregisterArtifactHandler(ArtifactHandler handler);

    /**
     * Reads the artifacts definitions file from the classpath.<p>
     * Should call initialize() on artifact handlers if there are any
     * registered already.
     */
    void loadArtifactMetadata();

    /**
     * Finds an artifact by name and type.<p>
     * Example: findGriffonClass("Book", "controller") will return an
     * artifact class that describes BookController.
     *
     * @param name the name of the artifact, e.g. 'Book'
     * @param type the type of the artifact, e.g. 'controller'
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    GriffonClass findGriffonClass(String name, String type);

    /**
     * Finds an artifact by class and type.<p>
     * Example: findGriffonClass(BookController, "controller") will return an
     * artifact class that describes BookController.
     *
     * @param clazz the name of the artifact, e.g. com.acme.BookController
     * @param type  the type of the artifact, e.g. 'controller'
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    GriffonClass findGriffonClass(Class clazz, String type);

    /**
     * Finds an artifact by class.<p>
     * Example: findGriffonClass(aBookControllerInstance) will return an
     * artifact class that describes BookController.
     *
     * @param obj an artifact instance
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    GriffonClass findGriffonClass(Object obj);

    /**
     * Finds an artifact by class.<p>
     * Example: findGriffonClass(BookController) will return an
     * artifact class that describes BookController.
     *
     * @param clazz a Class instance
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    GriffonClass findGriffonClass(Class clazz);

    /**
     * Finds an artifact by name.<p>
     * Example: findGriffonClass("BookController") will return an
     * artifact class that describes BookController.
     *
     * @param fqnClassName full qualified class name
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    GriffonClass findGriffonClass(String fqnClassName);

    /**
     * Finds all artifacts of an specific type.<p>
     * Example: getClassesOfType("controller") will return all
     * artifact classes that describe controllers.
     *
     * @param type an artifact type, e.g. 'controller'
     * @return a List of matching artifacts or an empty List if no match. Never returns null.
     */
    List<GriffonClass> getClassesOfType(String type);

    /**
     * Finds all artifact classes.<p>
     *
     * @return a List of all available GriffonClass instances. Never returns null.
     */
    List<GriffonClass> getAllClasses();
}
