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

import griffon.plugins.validation.Errors;
import griffon.util.GriffonClassUtils;

import javax.annotation.Nonnull;

import static griffon.util.GriffonClassUtils.isAssignableOrConvertibleFrom;
import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * Implements a maximum value constraint.
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class MaxConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "max";
    public static final String DEFAULT_INVALID_MAX_MESSAGE_CODE = "default.invalid.max.message";
    public static final String DEFAULT_INVALID_MAX_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] exceeds maximum value [{3}]";

    private Comparable maxValue;

    /**
     * @return Returns the maxValue.
     */
    public Comparable getMaxValue() {
        return maxValue;
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return Comparable.class.isAssignableFrom(type) ||
            isAssignableOrConvertibleFrom(Number.class, type);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void setParameter(@Nonnull Object constraintParameter) {
        requireNonNull(constraintParameter, "Parameter for constraint [" +
            VALIDATION_DSL_NAME + "] of property [" +
            constraintPropertyName + "] of class [" + constraintOwningClass + "] cannot be null");

        requireState(constraintParameter instanceof Comparable<?> || constraintParameter.getClass().isPrimitive(),
            "Parameter for constraint [" +
                VALIDATION_DSL_NAME + "] of property [" + constraintPropertyName +
                "] of class [" + constraintOwningClass + "] must implement the interface [java.lang.Comparable]");

        Class<?> propertyClass = GriffonClassUtils.getPropertyType(constraintOwningClass, constraintPropertyName);
        requireState(isAssignableOrConvertibleFrom(constraintParameter.getClass(), propertyClass),
            "Parameter for constraint [" +
                VALIDATION_DSL_NAME + "] of property [" +
                constraintPropertyName + "] of class [" + constraintOwningClass +
                "] must be the same type as property: [" + propertyClass.getName() + "]");

        maxValue = (Comparable<?>) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        if (maxValue.compareTo(propertyValue) < 0) {
            Object[] args = new Object[]{constraintPropertyName, constraintOwningClass, propertyValue, maxValue};
            rejectValue(target, errors, DEFAULT_INVALID_MAX_MESSAGE_CODE,
                VALIDATION_DSL_NAME + EXCEEDED_SUFFIX, args);
        }
    }
}
