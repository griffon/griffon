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
import griffon.types.IntRange;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Collection;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;


/**
 * Validates size of the property, for strings and arrays
 * this is the length, collections the size and numbers the value.
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class SizeConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "size";
    public static final String DEFAULT_INVALID_SIZE_MESSAGE_CODE = "default.invalid.size.message";
    public static final String DEFAULT_INVALID_SIZE_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] does not fall within the valid size range from [{3}] to [{4}]";

    private IntRange range;

    /**
     * @return Returns the range.
     */
    public IntRange getRange() {
        return range;
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return CharSequence.class.isAssignableFrom(type) ||
            Collection.class.isAssignableFrom(type) ||
            type.isArray();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof IntRange, "Parameter for constraint [" +
            VALIDATION_DSL_NAME + "] of property [" +
            constraintPropertyName + "] of class [" + constraintOwningClass +
            "] must be a of type [groovy.lang.IntRange]");

        range = (IntRange) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        Object[] args = {constraintPropertyName, constraintOwningClass, propertyValue,
            range.getFrom(), range.getTo()};

        int size;
        if (propertyValue.getClass().isArray()) {
            size = Array.getLength(propertyValue);
        } else if (propertyValue instanceof Collection<?>) {
            size = ((Collection<?>) propertyValue).size();
        } else { // String
            size = String.valueOf(propertyValue).length();
        }

        if (!range.contains(size)) {
            if (range.getFrom().compareTo(size) == 1) {
                rejectValue(args, errors, target, TOOSMALL_SUFFIX);
            } else {
                rejectValue(args, errors, target, TOOBIG_SUFFIX);
            }
        }
    }

    private void rejectValue(Object[] args, Errors errors, Object target, String suffix) {
        rejectValue(target, errors, DEFAULT_INVALID_SIZE_MESSAGE_CODE,
            VALIDATION_DSL_NAME + suffix, args);
    }
}
