/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.domain;

import griffon.core.GriffonApplication;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainClassProperty;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.plugins.domain.exceptions.InvalidPropertyException;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import griffon.transform.Domain;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.util.AnnotationUtils.named;
import static griffon.util.GriffonClassUtils.requireState;

/**
 * Base implementation of the {@code GriffonDomainClass} interface
 *
 * @author Andres Almiray
 */
public abstract class AbstractGriffonDomainClass<T> extends AbstractGriffonClass implements GriffonDomainClass<T> {
    protected Map<String, GriffonDomainClassProperty> domainProperties = new LinkedHashMap<>();
    protected Map<String, ConstrainedProperty> constrainedProperties = new LinkedHashMap<>();
    protected GriffonDomainHandler domainHandler;

    @Inject
    public AbstractGriffonDomainClass(@Nonnull GriffonApplication application, @Nonnull Class<?> clazz) {
        super(application, clazz, TYPE, TRAILING);
        domainHandler = resolveDomainHandler(clazz);
        initialize();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private GriffonDomainHandler resolveDomainHandler(@Nonnull Class<?> clazz) {
        final Domain domain = clazz.getAnnotation(Domain.class);
        requireState(domain != null, "Class '" + clazz.getName() + "' must be annotated with " + Domain.class.getName());
        String implementation = domain.value();
        return getApplication().getInjector().getInstance(GriffonDomainHandler.class, named(implementation));
    }

    protected abstract void initialize();

    @Nonnull
    public GriffonDomainClassProperty[] getProperties() {
        return domainProperties.values().toArray(new GriffonDomainClassProperty[domainProperties.size()]);
    }

    @Nonnull
    @SuppressWarnings("SuspiciousToArrayCall")
    public GriffonDomainClassProperty[] getPersistentProperties() {
        List<GriffonDomainProperty> persistent = new ArrayList<>();
        for (GriffonDomainClassProperty property : domainProperties.values()) {
            if (property.isPersistent()) {
                persistent.add(property);
            }
        }
        return persistent.toArray(new GriffonDomainClassProperty[persistent.size()]);
    }

    @Nullable
    public GriffonDomainClassProperty getPropertyByName(String name) {
        if (!domainProperties.containsKey(name)) {
            throw new InvalidPropertyException(getClazz(), name);
        }
        return domainProperties.get(name);
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public GriffonDomainProperty getIdentity() {
        return getPropertyByName(GriffonDomainProperty.IDENTITY);
    }

    @Nonnull
    public GriffonDomainHandler getDomainHandler() {
        return domainHandler;
    }

    @Nonnull
    public Map<String, ConstrainedProperty> getConstrainedProperties() {
        return constrainedProperties;
    }
}
