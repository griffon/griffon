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

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Collection;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;


/**
 * Validates maximum size or length of the property, for strings and arrays
 * this is the length and collections the size.
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class MaxSizeConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "maxSize";
    public static final String DEFAULT_INVALID_MAX_SIZE_MESSAGE_CODE = "default.invalid.max.size.message";
    public static final String DEFAULT_INVALID_MAX_SIZE_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] exceeds the maximum size of [{3}]";

    private int maxSize;

    /**
     * @return Returns the maxSize.
     */
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof Integer, "Parameter for constraint [" +
            VALIDATION_DSL_NAME + "] of property [" +
            constraintPropertyName + "] of class [" + constraintOwningClass +
            "] must be a of type [java.lang.Integer]");

        maxSize = (Integer) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return CharSequence.class.isAssignableFrom(type) ||
            Collection.class.isAssignableFrom(type) ||
            type.isArray();
    }

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        int length;
        if (propertyValue.getClass().isArray()) {
            length = Array.getLength(propertyValue);
        } else if (propertyValue instanceof Collection<?>) {
            length = ((Collection<?>) propertyValue).size();
        } else { // String
            length = String.valueOf(propertyValue).length();
        }

        if (length > maxSize) {
            Object[] args = {constraintPropertyName, constraintOwningClass, propertyValue, maxSize};
            rejectValue(target, errors, DEFAULT_INVALID_MAX_SIZE_MESSAGE_CODE,
                VALIDATION_DSL_NAME + EXCEEDED_SUFFIX, args);
        }
    }
}
