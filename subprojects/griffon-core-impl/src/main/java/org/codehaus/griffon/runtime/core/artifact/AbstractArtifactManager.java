/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.ArtifactManager;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonView;
import griffon.core.injection.Injector;
import griffon.core.threading.UIThreadManager;
import griffon.exceptions.ArtifactHandlerNotFoundException;
import griffon.exceptions.ArtifactNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code ArtifactManager} interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractArtifactManager implements ArtifactManager {
    protected static final String ERROR_ARTIFACT_HANDLER_NULL = "Argument 'artifactHandler' must not be null";
    private static final String ERROR_NAME_BLANK = "Argument 'name' must not be blank";
    private static final String ERROR_TYPE_BLANK = "Argument 'type' must not be blank";
    private static final String ERROR_CLASS_NULL = "Argument 'clazz' must not be null";
    private static final String ERROR_ARTIFACT_NULL = "Argument 'artifact' must not be null";
    private static final String ERROR_FULLY_QUALIFIED_CLASSNAME_BLANK = "Argument 'fqClassName' must not be blank";
    private static final Logger LOG = LoggerFactory.getLogger(AbstractArtifactManager.class);
    private final Map<String, Class<? extends GriffonArtifact>[]> artifacts = new ConcurrentHashMap<>();
    private final Map<String, ArtifactHandler> artifactHandlers = new ConcurrentHashMap<>();
    private final Object lock = new Object[0];

    @Inject
    private Provider<Injector> injectorProvider;

    @Inject
    private UIThreadManager uiThreadManager;

    @Nonnull
    protected Map<String, ArtifactHandler> getArtifactHandlers() {
        return artifactHandlers;
    }

    @SuppressWarnings("unchecked")
    public final void loadArtifactMetadata() {
        Map<String, List<Class<? extends GriffonArtifact>>> loadedArtifacts = doLoadArtifactMetadata();

        synchronized (lock) {
            for (Map.Entry<String, List<Class<? extends GriffonArtifact>>> artifactsEntry : loadedArtifacts.entrySet()) {
                String type = artifactsEntry.getKey();
                ArtifactHandler handler = artifactHandlers.get(type);
                if (handler == null) {
                    throw new ArtifactHandlerNotFoundException(type);
                }
                List<Class<? extends GriffonArtifact>> list = artifactsEntry.getValue();
                artifacts.put(type, list.toArray(new Class[list.size()]));
                handler.initialize(artifacts.get(type));
            }
        }
    }

    @Nonnull
    @Override
    public Set<String> getAllTypes() {
        return Collections.unmodifiableSet(artifactHandlers.keySet());
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public <A extends GriffonArtifact> A newInstance(@Nonnull GriffonClass griffonClass) {
        try {
            requireNonNull(griffonClass, "Argument 'griffonClass' must not be null");
        } catch (RuntimeException re) {
            throw new ArtifactNotFoundException(re);
        }

        return newInstance((Class<A>) griffonClass.getClazz());
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public <A extends GriffonArtifact> A newInstance(@Nonnull final Class<A> clazz) {
        if (findGriffonClass(clazz) == null) {
            throw new ArtifactNotFoundException(clazz);
        }

        if (GriffonView.class.isAssignableFrom(clazz)) {
            return uiThreadManager.runInsideUISync(() -> (A) injectorProvider.get().getInstance(clazz));
        }

        return (A) injectorProvider.get().getInstance(clazz);
    }

    @Nonnull
    protected abstract Map<String, List<Class<? extends GriffonArtifact>>> doLoadArtifactMetadata();

    public void registerArtifactHandler(@Nonnull ArtifactHandler artifactHandler) {
        requireNonNull(artifactHandler, ERROR_ARTIFACT_HANDLER_NULL);
        LOG.debug("Registering artifact handler for type '{}': {}", artifactHandler.getType(), artifactHandler);
        synchronized (lock) {
            artifactHandlers.put(artifactHandler.getType(), artifactHandler);
        }
    }

    public void unregisterArtifactHandler(@Nonnull ArtifactHandler artifactHandler) {
        requireNonNull(artifactHandler, ERROR_ARTIFACT_HANDLER_NULL);
        LOG.debug("Removing artifact handler for type '{}': {}", artifactHandler.getType(), artifactHandler);
        synchronized (lock) {
            artifactHandlers.remove(artifactHandler.getType());
        }
    }

    protected boolean isArtifactTypeSupported(@Nonnull String type) {
        requireNonBlank(type, ERROR_TYPE_BLANK);
        return artifactHandlers.get(type) != null;
    }

    @Nullable
    public GriffonClass findGriffonClass(@Nonnull String name, @Nonnull String type) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonBlank(type, ERROR_TYPE_BLANK);
        LOG.debug("Searching for griffonClass of {}:{}", type, name);
        synchronized (lock) {
            ArtifactHandler handler = artifactHandlers.get(type);
            return handler != null ? handler.findClassFor(name) : null;
        }
    }

    @Nullable
    @SuppressWarnings({"unchecked", "rawtypes"})
    public GriffonClass findGriffonClass(@Nonnull Class<? extends GriffonArtifact> clazz, @Nonnull String type) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        requireNonBlank(type, ERROR_TYPE_BLANK);
        LOG.debug("Searching for griffonClass of {}:{}", type, clazz.getName());
        synchronized (lock) {
            ArtifactHandler handler = artifactHandlers.get(type);
            return handler != null ? handler.getClassFor(clazz) : null;
        }
    }

    @Nullable
    public <A extends GriffonArtifact> GriffonClass findGriffonClass(@Nonnull A artifact) {
        requireNonNull(artifact, ERROR_ARTIFACT_NULL);
        synchronized (lock) {
            return findGriffonClass(artifact.getTypeClass());
        }
    }

    @Nullable
    @SuppressWarnings({"unchecked", "rawtypes"})
    public GriffonClass findGriffonClass(@Nonnull Class<? extends GriffonArtifact> clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        LOG.debug("Searching for griffonClass of {}", clazz.getName());
        synchronized (lock) {
            for (ArtifactHandler handler : artifactHandlers.values()) {
                GriffonClass griffonClass = handler.getClassFor(clazz);
                if (griffonClass != null) return griffonClass;
            }
        }
        return null;
    }

    @Nullable
    public GriffonClass findGriffonClass(@Nonnull String fqClassName) {
        requireNonBlank(fqClassName, ERROR_FULLY_QUALIFIED_CLASSNAME_BLANK);
        LOG.debug("Searching for griffonClass of {}", fqClassName);
        synchronized (lock) {
            for (ArtifactHandler handler : artifactHandlers.values()) {
                GriffonClass griffonClass = handler.getClassFor(fqClassName);
                if (griffonClass != null) return griffonClass;
            }
        }
        return null;
    }

    @Nonnull
    public List<GriffonClass> getClassesOfType(@Nonnull String type) {
        requireNonBlank(type, ERROR_TYPE_BLANK);
        synchronized (lock) {
            if (artifacts.containsKey(type)) {
                return asList(artifactHandlers.get(type).getClasses());
            }
        }
        return EMPTY_GRIFFON_CLASS_LIST;
    }

    @Nonnull
    public List<GriffonClass> getAllClasses() {
        List<GriffonClass> all = new ArrayList<>();
        synchronized (lock) {
            for (ArtifactHandler handler : artifactHandlers.values()) {
                all.addAll(asList(handler.getClasses()));
            }
        }
        return Collections.unmodifiableList(all);
    }

    protected <A extends GriffonArtifact> boolean isClassOfType(@Nonnull String type, @Nonnull Class<A> clazz) {
        for (Class<? extends GriffonArtifact> klass : artifacts.get(type)) {
            if (klass.getName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
