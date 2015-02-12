/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.core.artifact;

import griffon.core.ShutdownHandler;
import griffon.core.injection.Injector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Helper class capable of dealing with artifacts and their handlers.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public interface ArtifactManager extends ShutdownHandler {
    List<GriffonClass> EMPTY_GRIFFON_CLASS_LIST = Collections.emptyList();

    /**
     * Registers an ArtifactHandler by type.<p>
     * Should call initialize() on the handler.
     *
     * @param handler an ArtifactHandler
     */
    void registerArtifactHandler(@Nonnull ArtifactHandler handler);

    /**
     * Removes an ArtifactHandler by type.
     *
     * @param handler an ArtifactHandler
     */
    void unregisterArtifactHandler(@Nonnull ArtifactHandler handler);

    /**
     * Reads the artifacts definitions file from the classpath.<p>
     * Should call initialize() on artifact handlers if there are any
     * registered already.
     */
    void loadArtifactMetadata(@Nonnull Injector<?> injector);

    /**
     * Finds an artifact by name and type.<p>
     * Example: findGriffonClass("Book", "controller") will return an
     * artifact class that describes BookController.
     *
     * @param name the name of the artifact, e.g. 'Book'
     * @param type the type of the artifact, e.g. 'controller'
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    @Nullable
    GriffonClass findGriffonClass(@Nonnull String name, @Nonnull String type);

    /**
     * Finds an artifact by class and type.<p>
     * Example: findGriffonClass(BookController, "controller") will return an
     * artifact class that describes BookController.
     *
     * @param clazz the name of the artifact, e.g. com.acme.BookController
     * @param type  the type of the artifact, e.g. 'controller'
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    @Nullable
    GriffonClass findGriffonClass(@Nonnull Class<? extends GriffonArtifact> clazz, @Nonnull String type);

    /**
     * Finds an artifact by class.<p>
     * Example: findGriffonClass(aBookControllerInstance) will return an
     * artifact class that describes BookController.
     *
     * @param artifact an artifact instance
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    @Nullable
    <A extends GriffonArtifact> GriffonClass findGriffonClass(@Nonnull A artifact);

    /**
     * Finds an artifact by class.<p>
     * Example: findGriffonClass(BookController) will return an
     * artifact class that describes BookController.
     *
     * @param clazz a Class instance
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    @Nullable
    GriffonClass findGriffonClass(@Nonnull Class<? extends GriffonArtifact> clazz);

    /**
     * Finds an artifact by name.<p>
     * Example: findGriffonClass("BookController") will return an
     * artifact class that describes BookController.
     *
     * @param fqClassName full qualified class name
     * @return the GriffonClass associated with the artifact is there's a match, null otherwise.
     */
    @Nullable
    GriffonClass findGriffonClass(@Nonnull String fqClassName);

    /**
     * Finds all artifacts of an specific type.<p>
     * Example: getClassesOfType("controller") will return all
     * artifact classes that describe controllers.
     *
     * @param type an artifact type, e.g. 'controller'
     * @return a List of matching artifacts or an empty List if no match. Never returns null.
     */
    @Nonnull
    List<GriffonClass> getClassesOfType(@Nonnull String type);

    /**
     * Finds all supported artifact types.<p>
     *
     * @return a Set of all available artifact types. Never returns null.
     * @since 2.2.0
     */
    @Nonnull
    Set<String> getAllTypes();

    /**
     * Finds all artifact classes.<p>
     *
     * @return a List of all available GriffonClass instances. Never returns null.
     */
    @Nonnull
    List<GriffonClass> getAllClasses();

    /**
     * Creates a new instance of the specified class and type.<br/>
     * Triggers the ApplicationEvent.NEW_INSTANCE with the following parameters
     * <ul>
     * <li>clazz - the Class of the object</li>
     * <li>instance -> the object that was created</li>
     * </ul>
     *
     * @param griffonClass the GriffonClass for which an instance must be created
     * @return a newly instantiated object of type <tt>clazz</tt>. Implementations must be sure
     *         to trigger an event of type ApplicationEvent.NEW_INSTANCE.
     * @throws griffon.exceptions.ArtifactNotFoundException
     *          if there's no artifact configured
     *          matching the given criteria
     */
    @Nonnull
    <A extends GriffonArtifact> A newInstance(@Nonnull GriffonClass griffonClass);

    /**
     * Creates a new instance of the specified class and type.<br/>
     * Triggers the ApplicationEvent.NEW_INSTANCE with the following parameters
     * <ul>
     * <li>clazz - the Class of the object</li>
     * <li>instance -> the object that was created</li>
     * </ul>
     *
     * @param clazz the Class for which an instance must be created
     * @return a newly instantiated object of type <tt>clazz</tt>. Implementations must be sure
     *         to trigger an event of type ApplicationEvent.NEW_INSTANCE.
     * @throws griffon.exceptions.ArtifactNotFoundException
     *          if there's no artifact configured
     *          matching the given criteria
     */
    @Nonnull
    <A extends GriffonArtifact> A newInstance(@Nonnull Class<A> clazz);
}
