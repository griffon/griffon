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

package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.ApplicationEvent;
import griffon.core.artifact.*;
import griffon.core.event.EventRouter;
import griffon.core.injection.Binding;
import griffon.core.injection.Injector;
import griffon.core.injection.binder.LinkedBindingBuilder;
import griffon.exceptions.ArtifactNotFoundException;
import org.codehaus.griffon.runtime.core.injection.Bindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the {@code ArtifactManager} interface.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public abstract class AbstractArtifactManager implements ArtifactManager {
    protected static final String ERROR_ARTIFACT_HANDLER_NULL = "Argument 'artifactHandler' cannot be null";
    private static final String ERROR_NAME_BLANK = "Argument 'name' cannot be blank";
    private static final String ERROR_TYPE_BLANK = "Argument 'type' cannot be blank";
    private static final String ERROR_CLASS_NULL = "Argument 'clazz' cannot be null";
    private static final String ERROR_ARTIFACT_NULL = "Argument 'artifact' cannot be null";
    private static final String ERROR_FULLY_QUALIFIED_CLASSNAME_BLANK = "Argument 'fqClassName' cannot be blank";
    private final Map<String, ArtifactInfo[]> artifacts = new ConcurrentHashMap<>();
    private final Map<String, ArtifactHandler> artifactHandlers = new ConcurrentHashMap<>();
    private final Object lock = new Object[0];
    private final EventRouter eventRouter;
    private Injector<?> artifactInjector;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArtifactManager.class);

    @Inject
    public AbstractArtifactManager(@Nonnull EventRouter eventRouter) {
        this.eventRouter = requireNonNull(eventRouter, "Argument 'eventRouter' cannot be null");
    }

    @Nonnull
    protected Map<String, ArtifactInfo[]> getArtifacts() {
        return artifacts;
    }

    @Nonnull
    protected Map<String, ArtifactHandler> getArtifactHandlers() {
        return artifactHandlers;
    }

    public final void loadArtifactMetadata(@Nonnull Injector<?> injector) {
        requireNonNull(injector, "Argument 'injector' cannot be null");
        Map<String, List<ArtifactInfo>> loadedArtifacts = doLoadArtifactMetadata();

        Collection<Binding<?>> bindings = new ArrayList<>();
        synchronized (lock) {
            for (Map.Entry<String, List<ArtifactInfo>> artifactsEntry : loadedArtifacts.entrySet()) {
                String type = artifactsEntry.getKey();
                ArtifactHandler handler = artifactHandlers.get(type);
                if (handler == null) {
                    // TODO No handler was registered for this type
                    return;
                }
                List<ArtifactInfo> list = artifactsEntry.getValue();
                artifacts.put(type, list.toArray(new ArtifactInfo[list.size()]));
                handler.initialize(artifacts.get(type));
                for (ArtifactInfo artifactInfo : list) {
                    Class<GriffonArtifact> clazz = artifactInfo.getClazz();
                    LinkedBindingBuilder<GriffonClass> builder = Bindings.bind(GriffonClass.class)
                        .withClassifier(new ArtifactImpl(clazz));
                    builder.toInstance(handler.newGriffonClassInstance(clazz));
                    bindings.add(builder.getBinding());
                    bindings.add(Bindings.bind(clazz).getBinding());
                }
            }
        }

        for (Binding<?> b : bindings) {
            System.out.println(b);
        }

        artifactInjector = injector.createNestedInjector("artifactInjector", bindings);
    }

    @Override
    @Nonnull
    public <T> T newInstance(@Nonnull Class<T> clazz, @Nonnull String type) {
        GriffonClass griffonClass = findGriffonClass(clazz, type);
        if (griffonClass == null) {
            throw new ArtifactNotFoundException(clazz, type);
        }

        T instance = artifactInjector.getInstance(clazz);
        eventRouter.publish(ApplicationEvent.NEW_INSTANCE.getName(), asList(clazz, type, instance));
        return instance;
    }

    @Nonnull
    protected abstract Map<String, List<ArtifactInfo>> doLoadArtifactMetadata();

    public void registerArtifactHandler(@Nonnull ArtifactHandler artifactHandler) {
        requireNonNull(artifactHandler, ERROR_ARTIFACT_HANDLER_NULL);
        if (LOG.isInfoEnabled()) {
            LOG.info("Registering artifact handler for type '" + artifactHandler.getType() + "': " + artifactHandler);
        }
        synchronized (lock) {
            artifactHandlers.put(artifactHandler.getType(), artifactHandler);
            if (artifacts.get(artifactHandler.getType()) != null)
                artifactHandler.initialize(artifacts.get(artifactHandler.getType()));
        }
    }

    public void unregisterArtifactHandler(@Nonnull ArtifactHandler artifactHandler) {
        requireNonNull(artifactHandler, ERROR_ARTIFACT_HANDLER_NULL);
        if (LOG.isInfoEnabled()) {
            LOG.info("Removing artifact handler for type '" + artifactHandler.getType() + "': " + artifactHandler);
        }
        synchronized (lock) {
            artifactHandlers.remove(artifactHandler.getType());
        }
    }

    @Nullable
    public GriffonClass findGriffonClass(@Nonnull String name, @Nonnull String type) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonBlank(type, ERROR_TYPE_BLANK);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + type + ":" + name);
        }
        synchronized (lock) {
            ArtifactHandler handler = artifactHandlers.get(type);
            return handler != null ? handler.findClassFor(name) : null;
        }
    }

    @Nullable
    public GriffonClass findGriffonClass(@Nonnull Class clazz, @Nonnull String type) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        requireNonBlank(type, ERROR_TYPE_BLANK);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + type + ":" + clazz.getName());
        }
        synchronized (lock) {
            ArtifactHandler handler = artifactHandlers.get(type);
            return handler != null ? handler.getClassFor(clazz) : null;
        }
    }

    @Nullable
    public GriffonClass findGriffonClass(@Nonnull Object artifact) {
        requireNonNull(artifact, ERROR_ARTIFACT_NULL);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + artifact);
        }
        synchronized (lock) {
            return findGriffonClass(artifact.getClass());
        }
    }

    @Nullable
    public GriffonClass findGriffonClass(@Nonnull Class clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + clazz.getName());
        }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("Searching for griffonClass of " + fqClassName);
        }
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
        List<GriffonClass> all = new ArrayList<GriffonClass>();
        synchronized (lock) {
            for (ArtifactHandler handler : artifactHandlers.values()) {
                all.addAll(asList(handler.getClasses()));
            }
        }
        return Collections.unmodifiableList(all);
    }

    protected boolean isClassOfType(String type, Class clazz) {
        for (ArtifactInfo artifactInfo : artifacts.get(type)) {
            if (artifactInfo.getClazz().getName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
