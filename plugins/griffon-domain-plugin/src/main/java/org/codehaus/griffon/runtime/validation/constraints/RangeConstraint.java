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

import griffon.core.editors.PropertyEditorResolver;
import griffon.plugins.validation.Errors;
import griffon.types.Range;

import javax.annotation.Nonnull;
import java.beans.PropertyEditor;

import static griffon.util.GriffonClassUtils.isAssignableOrConvertibleFrom;
import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * Validates a range.
 *
 * @author Graeme Rocher
 */
public class RangeConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "range";
    public static final String DEFAULT_INVALID_RANGE_MESSAGE_CODE = "default.invalid.range.message";
    public static final String DEFAULT_INVALID_RANGE_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] does not fall within the valid range from [{3}] to [{4}]";

    private Range range;

    /**
     * @return Returns the range.
     */
    public Range getRange() {
        return range;
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
        requireState(constraintParameter instanceof Range, "Parameter for constraint [" +
            VALIDATION_DSL_NAME + "] of property [" +
            constraintPropertyName + "] of class [" +
            constraintOwningClass + "] must be a of type [groovy.lang.Range]");

        range = (Range) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        if (propertyValue == null || !range.getType().isAssignableFrom(propertyValue.getClass())) {
            return;
        }

        Object[] args = new Object[]{constraintPropertyName, constraintOwningClass,
            propertyValue, range.getFrom(), range.getTo()};

        Comparable from = range.getFrom();
        Comparable to = range.getTo();

        // Upgrade the numbers to Long, so all integer types can be compared.
        if (from instanceof Number) {
            from = ((Number) from).longValue();
        }
        if (to instanceof Number) {
            to = ((Number) to).longValue();
        }
        propertyValue = convertValue(propertyValue);

        if (from.compareTo(propertyValue) > 0) {
            rejectValue(target, errors, DEFAULT_INVALID_RANGE_MESSAGE_CODE,
                VALIDATION_DSL_NAME + TOOSMALL_SUFFIX, args);
        } else if (to.compareTo(propertyValue) < 0) {
            rejectValue(target, errors, DEFAULT_INVALID_RANGE_MESSAGE_CODE,
                VALIDATION_DSL_NAME + TOOBIG_SUFFIX, args);
        }
    }

    private long convertValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        PropertyEditor longPropertyEditor = PropertyEditorResolver.findEditor(Long.TYPE);
        PropertyEditor valuePropertyEditor = PropertyEditorResolver.findEditor(value.getClass());

        if (valuePropertyEditor == null) {
            throw new IllegalArgumentException("Cannot convert value [" +
                value + "] of type [" +
                value.getClass().getName() + "] to long");
        }

        valuePropertyEditor.setValue(value);
        longPropertyEditor.setAsText(valuePropertyEditor.getAsText());
        return (Long) longPropertyEditor.getValue();
    }
}