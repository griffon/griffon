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
package org.codehaus.griffon.runtime.validation.constraints;

import griffon.core.GriffonApplication;
import griffon.exceptions.PropertyException;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import griffon.plugins.validation.constraints.ConstrainedPropertyAssembler;
import griffon.plugins.validation.constraints.ConstraintDef;
import org.codehaus.griffon.runtime.core.artifact.ClassPropertyFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultConstrainedPropertyAssembler implements ConstrainedPropertyAssembler {
    private final Logger LOG = LoggerFactory.getLogger(ConstrainedPropertyAssembler.class);
    protected static final String SHARED_CONSTRAINT = "shared";
    protected static final String IMPORT_FROM_CONSTRAINT = "importFrom";

    protected Map<String, ConstrainedProperty> constrainedProperties = new LinkedHashMap<>();
    protected Map<String, String> sharedConstraints = new LinkedHashMap<>();
    protected int order = 1;
    protected Class<?> targetClass;
    protected ClassPropertyFetcher classPropertyFetcher;
    protected final GriffonApplication application;

    @Inject
    public DefaultConstrainedPropertyAssembler(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
    }

    @Nonnull
    protected GriffonApplication getApplication() {
        return application;
    }

    @Override
    public void setTargetClass(@Nonnull Class<?> targetClass) {
        this.targetClass = requireNonNull(targetClass, "Argument 'targetClass' cannot be null");
        classPropertyFetcher = ClassPropertyFetcher.forClass(targetClass);
    }

    @Override
    public void assemble(@Nonnull Map<String, List<ConstraintDef>> constraints) {
        requireNonNull(constraints, "Argument 'constraints' cannot be null");
        for (Map.Entry<String, List<ConstraintDef>> entry : constraints.entrySet()) {
            assembleConstraint(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void assemble(@Nonnull Object constraints) {
        throw new UnsupportedOperationException(getClass().getName() +
            " does not support constraints of type " +
            constraints.getClass().getName());
    }

    @Nonnull
    @Override
    public Map<String, ConstrainedProperty> getConstrainedProperties() {
        return constrainedProperties;
    }

    @Nullable
    public String getSharedConstraint(@Nonnull String propertyName) {
        return sharedConstraints.get(requireNonBlank(propertyName, "Argument 'propertyName' cannot be blank"));
    }

    protected void assembleConstraint(@Nonnull String property, @Nonnull List<ConstraintDef> constraints) {
        ConstrainedProperty cp;
        if (constrainedProperties.containsKey(property)) {
            cp = constrainedProperties.get(property);
        } else {
            Class<?> propertyType = classPropertyFetcher.getPropertyType(property);
            if (propertyType == null) {
                throw new PropertyException(targetClass, property);
            }
            cp = new ConstrainedProperty(targetClass, property, propertyType);
            cp.setMessageSource(application.getMessageSource());
            cp.setOrder(order++);
            constrainedProperties.put(property, cp);
        }

        if (cp.getPropertyType() == null) {
            return;
        }

        for (ConstraintDef constraintDef : constraints) {
            String constraintName = constraintDef.getName();
            Object value = constraintDef.getValue();
            if (SHARED_CONSTRAINT.equals(constraintName)) {
                if (value != null) {
                    sharedConstraints.put(property, value.toString());
                }
                continue;
            }
            addConstraint(cp, constraintName, value);
        }
    }

    protected void addConstraint(ConstrainedProperty cp, String constraintName, Object value) {
        if (cp.supportsContraint(constraintName)) {
            cp.applyConstraint(constraintName, value);
        } else {
            if (ConstrainedProperty.hasRegisteredConstraint(constraintName)) {
                // constraint is registered but doesn't support this property's type
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Property [" + cp.getPropertyName() + "] of class " +
                        targetClass.getName() + " has type [" + cp.getPropertyType().getName() +
                        "] and doesn't support constraint [" + constraintName +
                        "]. This constraint will not be checked during validation.");
                }
            } else {
                // in the case where the constraint is not supported we still retain meta data
                // about the constraint in case its needed for other things
                cp.addMetaConstraint(constraintName, value);
            }
        }
    }
}
