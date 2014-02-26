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
import griffon.core.artifact.GriffonClass;
import griffon.exceptions.GriffonException;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainClassProperty;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.plugins.validation.constraints.*;
import griffon.util.GriffonClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultConstraintsEvaluator implements ConstraintsEvaluator {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultConstraintsEvaluator.class);

    private final GriffonApplication application;

    @Inject
    public DefaultConstraintsEvaluator(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
    }

    @Nonnull
    @Override
    public Map<String, ConstrainedProperty> evaluate(@Nonnull Class<?> cls) {
        return evaluateConstraints(cls, null);
    }

    @Nonnull
    @Override
    public Map<String, ConstrainedProperty> evaluate(@Nonnull GriffonDomainClass<?> cls) {
        return evaluate(cls.getClazz(), cls.getPersistentProperties());
    }

    @Nonnull
    @Override
    public Map<String, ConstrainedProperty> evaluate(@Nonnull Object object, @Nonnull GriffonDomainClassProperty[] properties) {
        return evaluateConstraints(object.getClass(), properties);
    }

    @Nonnull
    @Override
    public Map<String, ConstrainedProperty> evaluate(@Nonnull Class<?> cls, @Nonnull GriffonDomainClassProperty[] properties) {
        return evaluateConstraints(cls, properties);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    protected Map<String, ConstrainedProperty> evaluateConstraints(
        final Class<?> theClass,
        GriffonDomainClassProperty[] properties) {

        Map<String, Object> defaultConstraints = ConstraintUtils.getDefaultConstraints(application);

        // boolean javaEntity = theClass.isAnnotationPresent(Domain.class);
        LinkedList<?> classChain = getSuperClassChain(theClass);
        Class<?> clazz;

        ConstrainedPropertyAssembler builder = application.getInjector().getInstance(ConstrainedPropertyAssembler.class);
        builder.setTargetClass(theClass);

        // Evaluate all the constraints closures in the inheritance chain
        for (Object aClassChain : classChain) {
            clazz = (Class<?>) aClassChain;
            Object constraintsProperty = GriffonClassUtils.getStaticPropertyValue(clazz, PROPERTY_NAME);

            if (constraintsProperty instanceof Map) {
                Map<String, List<ConstraintDef>> constraints = (Map<String, List<ConstraintDef>>) constraintsProperty;
                builder.assemble(constraints);
            } else if (constraintsProperty != null) {
                builder.assemble(constraintsProperty);
            } else {
                LOG.debug("User-defined constraints not found on class [" + clazz.getName() + "]");
            }
        }

        Map<String, ConstrainedProperty> constrainedProperties = builder.getConstrainedProperties();
        if (properties != null && !(constrainedProperties.isEmpty() /*&& javaEntity*/)) {
            for (GriffonDomainClassProperty p : properties) {
                // assume no formula issues if Hibernate isn't available to avoid CNFE
                // if (canPropertyBeConstrained(p)) {
                /*if (p.isDerived()) {
                  if (constrainedProperties.remove(p.getName()) != null) {
                      LOG.warn("Derived properties may not be constrained. Property [" + p.getName() + "] of domain class " + theClass.getName() + " will not be checked during validation.");
                  }
              } else {*/
                final String propertyName = p.getName();
                ConstrainedProperty cp = constrainedProperties.get(propertyName);
                if (cp == null) {
                    cp = new ConstrainedProperty(p.getDomainClass().getClazz(), propertyName, p.getType());
                    cp.setOrder(constrainedProperties.size() + 1);
                    cp.setMessageSource(application.getMessageSource());
                    constrainedProperties.put(propertyName, cp);
                }
                // Make sure all fields are required by default, unless
                // specified otherwise by the constraints
                // If the field is a Java entity annotated with @Entity skip this
                applyDefaultConstraints(propertyName, p, cp, defaultConstraints);
                //}
                // }
            }
        }
        if (properties == null || properties.length == 0) {
            // harvest all properties from class, excluding those we may already have
            // marked as constrained or those that cannot be constrained
            for (PropertyDescriptor pd : GriffonClassUtils.getPropertyDescriptors(theClass)) {
                String propertyName = pd.getName();
                if (constrainedProperties.containsKey(propertyName) || !isConstrainableProperty(propertyName))
                    continue;
                ConstrainedProperty cp = new ConstrainedProperty(theClass, propertyName, pd.getPropertyType());
                cp.setOrder(constrainedProperties.size() + 1);
                cp.setMessageSource(application.getMessageSource());
                constrainedProperties.put(propertyName, cp);
            }
        }

        Set<Map.Entry<String, ConstrainedProperty>> entrySet = constrainedProperties.entrySet();
        for (Map.Entry<String, ConstrainedProperty> entry : entrySet) {
            ConstrainedProperty constrainedProperty = entry.getValue();
            if (!constrainedProperty
                .hasAppliedConstraint(NullableConstraint.VALIDATION_DSL_NAME) &&
                isConstrainableProperty(entry.getKey())) {
                applyDefaultNullableConstraint(constrainedProperty);
            }
        }

        applySharedConstraints(builder, constrainedProperties, defaultConstraints);

        return constrainedProperties;
    }

    @SuppressWarnings("unchecked")
    protected void applySharedConstraints(
        ConstrainedPropertyAssembler constrainedPropertyBuilder,
        Map<String, ConstrainedProperty> constrainedProperties, Map<String, Object> defaultConstraints) {
        for (Map.Entry<String, ConstrainedProperty> entry : constrainedProperties.entrySet()) {
            String propertyName = entry.getKey();
            ConstrainedProperty constrainedProperty = entry.getValue();
            String sharedConstraintReference = constrainedPropertyBuilder.getSharedConstraint(propertyName);
            if (sharedConstraintReference != null && defaultConstraints != null) {
                Object o = defaultConstraints.get(sharedConstraintReference);
                if (o instanceof Map) {
                    Map<String, Object> constraintsWithinSharedConstraint = (Map) o;
                    for (Map.Entry<String, Object> e : constraintsWithinSharedConstraint.entrySet()) {
                        constrainedProperty.applyConstraint(e.getKey(), e.getValue());
                    }
                } else {
                    throw new GriffonException("Property [" + constrainedProperty.getOwningClass().getName() + '.' + propertyName +
                        "] references shared constraint [" + sharedConstraintReference + ":" + o + "], which doesn't exist!");
                }
            }
        }
    }

    @Nonnull
    protected static LinkedList<?> getSuperClassChain(@Nonnull Class<?> theClass) {
        LinkedList<Class<?>> classChain = new LinkedList<>();
        Class<?> clazz = theClass;
        while (clazz != Object.class && clazz != null) {
            classChain.addFirst(clazz);
            clazz = clazz.getSuperclass();
        }
        return classChain;
    }

    @SuppressWarnings("unchecked")
    protected void applyDefaultConstraints(String propertyName, GriffonDomainClassProperty p,
                                           ConstrainedProperty cp,/*, @SuppressWarnings("hiding") Map<String, Object> defaultConstraints*/Map<String, Object> defaultConstraints) {
        if (defaultConstraints != null && !defaultConstraints.isEmpty()) {
            if (defaultConstraints.containsKey("*")) {
                final Object o = defaultConstraints.get("*");
                if (o instanceof Map) {
                    Map<String, Object> globalConstraints = (Map<String, Object>) o;
                    applyMapOfConstraints(globalConstraints, propertyName, p, cp);
                }
            }
        }

        if (canApplyNullableConstraint(propertyName, p, cp)) {
            applyDefaultNullableConstraint(p, cp);
        }
    }

    protected void applyDefaultNullableConstraint(GriffonDomainProperty p,
                                                  ConstrainedProperty cp) {
        applyDefaultNullableConstraint(cp);
    }

    protected void applyDefaultNullableConstraint(ConstrainedProperty cp) {
        if (!cp.hasAppliedConstraint(NullableConstraint.VALIDATION_DSL_NAME) &&
            isConstrainableProperty(cp.getPropertyName())) {
            return;
        }
        boolean isCollection = Collection.class.isAssignableFrom(cp.getPropertyType()) || Map.class.isAssignableFrom(cp.getPropertyType());
        cp.applyConstraint(NullableConstraint.VALIDATION_DSL_NAME, isCollection);
    }

    protected boolean canApplyNullableConstraint(String propertyName, GriffonDomainClassProperty property, ConstrainedProperty constrainedProperty) {
        return property != null &&
            !constrainedProperty.hasAppliedConstraint(NullableConstraint.VALIDATION_DSL_NAME) &&
            isConstrainableProperty(propertyName);
    }

    protected void applyMapOfConstraints(Map<String, Object> constraints, String propertyName, GriffonDomainClassProperty p, ConstrainedProperty cp) {
        for (Map.Entry<String, Object> entry : constraints.entrySet()) {
            String constraintName = entry.getKey();
            Object constrainingValue = entry.getValue();
            if (!cp.hasAppliedConstraint(constraintName) && cp.supportsContraint(constraintName)) {
                if (NullableConstraint.VALIDATION_DSL_NAME.equals(constraintName)) {
                    if (isConstrainableProperty(p, propertyName)) {
                        cp.applyConstraint(constraintName, constrainingValue);
                    }
                } else {
                    cp.applyConstraint(constraintName, constrainingValue);
                }
            }
        }
    }

    protected boolean isConstrainableProperty(String propertyName) {
        return !propertyName.equals("errors") &&
            !GriffonClass.STANDARD_PROPERTIES.contains(propertyName) &&
            !propertyName.equals(GriffonDomainProperty.DATE_CREATED) &&
            !propertyName.equals(GriffonDomainProperty.LAST_UPDATED) &&
            !GriffonDomainProperty.VERSION.equals(propertyName) &&
            !GriffonDomainProperty.IDENTITY.equals(propertyName);
    }

    protected boolean isConstrainableProperty(GriffonDomainProperty p, String propertyName) {
        return isConstrainableProperty(propertyName) /*&&
                !((p.isOneToOne() || p.isManyToOne()) && p.isCircular())*/;
    }
}
