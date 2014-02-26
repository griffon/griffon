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
 * Validates not equal to something.
 */
public class NotEqualConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "notEqual";
    public static final String DEFAULT_NOT_EQUAL_MESSAGE_CODE = "default.not.equal.message";
    public static final String DEFAULT_NOT_EQUAL_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] cannot equal [{3}]";

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return true;
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    public void setParameter(@Nonnull Object constraintParameter) {
        requireNonNull(constraintParameter, "Parameter for constraint [" + VALIDATION_DSL_NAME +
            "] of property [" + constraintPropertyName + "] of class [" +
            constraintOwningClass + "] cannot be null");

        Class<?> propertyClass = GriffonClassUtils.getPropertyType(constraintOwningClass, constraintPropertyName);
        // TODO: Find an alternative way to do the UrlMapping check!
        requireState(isAssignableOrConvertibleFrom(constraintParameter.getClass(), propertyClass),
            "Parameter for constraint [" +
                VALIDATION_DSL_NAME + "] of property [" +
                constraintPropertyName + "] of class [" + constraintOwningClass +
                "] must be the same type as property: [" + propertyClass.getName() + "]");

        super.setParameter(constraintParameter);
    }

    /**
     * @return Returns the notEqualTo.
     */
    public Object getNotEqualTo() {
        return constraintParameter;
    }

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        if (constraintParameter.equals(propertyValue)) {
            Object[] args = new Object[]{constraintPropertyName, constraintOwningClass, propertyValue, constraintParameter};
            rejectValue(target, errors, DEFAULT_NOT_EQUAL_MESSAGE_CODE,
                VALIDATION_DSL_NAME, args);
        }
    }
}
