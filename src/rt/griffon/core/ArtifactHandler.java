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

package griffon.core;

/**
 * <p>The ArtefactHandler interface's purpose is to allow the analysis of conventions within a Griffon application.
 * An artifact is represented by the GriffonClass interface and this interface provides methods that allow artefacts to
 * be identified, created and initialized.
 *
 * @author Andres Almiray
 */
public interface ArtifactHandler {
    /**
     * Get the tye of artifact this handler processes.
     */
    String getType();

    /**
     * Get the trailing suffix that identifies the artifact.<p>
     * May be empty but non-null.
     */
    String getTrailing();

    /**
     * Returns true if the target Class is a class artifact
     * handled by this object.
     */
    boolean isArtifact(Class clazz);

    /**
     * Returns true if the target GriffonClass is a class artifact
     * handled by this object.
     */
    boolean isArtifact(GriffonClass clazz);

    /**
     * Initializes the handler with a collection of all available
     * artifacts this handler can process.<p>
     * This is a good time to pre-emptively instantiate beans or
     * perform additional checks on artifacts.
     */
    void initialize(ArtifactInfo[] artifacts);

    /**
     * Returns the set of all artifact classes this handler manages.
     */
    GriffonClass[] getClasses();

    /**
     * Finds an artifact by its property name.<p>
     * Examples: findClassfor("fooService") returns an artifact class
     * that can handle FooService.<p>
     * 
     * Should {@code propertyName} contain any dots then the portion
     * after te last dor will be considered only.
     */
    GriffonClass findClassFor(String propertyName);

    /**
     * Finds an artifact if the target {@code clazz} is handled by this
     * ArtifactHandler.<p>
     *
     * @param clazz a class object, i.e, BookController
     * @return a GriffonClass that can handle the target class or null
     * if the clazz is not handled by this ArtifactHandler.
     */
    GriffonClass getClassFor(Class clazz);

    /**
     * Finds an artifact by class name if it represents a class that 
     * is handled by this ArtifactHandler.<p>
     *
     * @param fqnClassName a full qualified class name, i.e, "book.BookController"
     * @return a GriffonClass that can handle the target class or null
     * if the clazz is not handled by this ArtifactHandler.
     */
    GriffonClass getClassFor(String fqnClassName);

    /**
     * Reference to the current {@code GriffonApplication}
     */
    GriffonApplication getApp();
}
