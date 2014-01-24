/*
 * Copyright 2009-2014 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.GriffonArtifact;
import griffon.core.artifact.GriffonClass;
import griffon.core.injection.Binding;
import org.codehaus.griffon.runtime.core.injection.Bindings;
import org.codehaus.griffon.runtime.core.injection.LinkedBindingBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

import static griffon.util.AnnotationUtils.typed;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Base implementation of the ArtifactHandler interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractArtifactHandler<A extends GriffonArtifact> implements ArtifactHandler<A> {
    protected static final String ERROR_CLASS_NULL = "Argument 'class' cannot be null";
    private final Class<A> artifactType;
    private final String type;
    private final String trailing;
    private final GriffonApplication application;

    private GriffonClass[] griffonClasses = new GriffonClass[0];
    private final Map<String, GriffonClass> classesByName = new TreeMap<>();

    @Inject
    public AbstractArtifactHandler(@Nonnull GriffonApplication application, @Nonnull Class<A> artifactType, @Nonnull String type, @Nonnull String trailing) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
        this.artifactType = requireNonNull(artifactType, "Argument 'artifactType' cannot be null");
        this.type = requireNonBlank(type, "Argument 'type' cannot be blank");
        this.trailing = requireNonNull(trailing, "Argument 'trailing' cannot be null");
    }

    @Nonnull
    public Class<A> getArtifactType() {
        return artifactType;
    }

    @Nonnull
    public String getType() {
        return type;
    }

    @Nonnull
    public String getTrailing() {
        return trailing;
    }

    @Nonnull
    public Collection<Binding<?>> initialize(@Nonnull Class<A>[] classes) {
        griffonClasses = new GriffonClass[classes.length];
        List<Binding<?>> bindings = new ArrayList<>();
        for (int i = 0; i < classes.length; i++) {
            Class<A> klass = classes[i];
            GriffonClass griffonClass = newGriffonClassInstance(klass);
            griffonClasses[i] = griffonClass;
            classesByName.put(klass.getName(), griffonClass);
            createBindings(bindings, klass, griffonClass);
        }
        return bindings;
    }

    protected void createBindings(@Nonnull List<Binding<?>> bindings, @Nonnull Class<A> clazz, @Nonnull GriffonClass griffonClass) {
        LinkedBindingBuilder<GriffonClass> builder = Bindings.bind(GriffonClass.class)
            .withClassifier(typed(clazz));
        builder.toInstance(griffonClass);
        bindings.add(builder.getBinding());
        bindings.add(Bindings.bind(clazz).getBinding());
    }

    @Nonnull
    public Map<String, GriffonClass> getClassesByName() {
        return Collections.unmodifiableMap(classesByName);
    }

    /**
     * Returns true if the target Class is a class artifact
     * handled by this object.<p>
     * This implementation performs an equality check on class.name
     */
    public boolean isArtifact(@Nonnull Class<A> clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        return classesByName.get(clazz.getName()) != null;
    }

    public boolean isArtifact(@Nonnull GriffonClass clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        for (GriffonClass griffonClass : griffonClasses) {
            if (griffonClass.equals(clazz)) return true;
        }
        return false;
    }

    @Nonnull
    public GriffonClass[] getClasses() {
        return griffonClasses;
    }

    @Nullable
    public GriffonClass getClassFor(@Nonnull Class<A> clazz) {
        requireNonNull(clazz, ERROR_CLASS_NULL);
        return getClassFor(clazz.getName());
    }

    @Nullable
    public GriffonClass getClassFor(@Nonnull String fqnClassName) {
        requireNonBlank(fqnClassName, "Argument 'fqnClassName' cannot be blank");
        return classesByName.get(fqnClassName);
    }

    @Nullable
    public GriffonClass findClassFor(@Nonnull String propertyName) {
        requireNonBlank(propertyName, "Argument 'propertyName' cannot be blank");

        String simpleName = propertyName;

        int lastDot = propertyName.lastIndexOf(".");
        if (lastDot > -1) {
            simpleName = simpleName.substring(lastDot + 1);
        }

        if (simpleName.length() == 1) {
            simpleName = simpleName.toUpperCase();
        } else {
            simpleName = simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);
        }

        if (!simpleName.endsWith(trailing)) {
            simpleName += trailing;
        }

        for (GriffonClass griffonClass : griffonClasses) {
            if (griffonClass.getClazz().getSimpleName().equals(simpleName)) {
                return griffonClass;
            }
        }

        return null;
    }

    @Nonnull
    protected GriffonApplication getApplication() {
        return application;
    }
}
