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
import javax.annotation.Nullable;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * Validates not null.
 *
 * @author Graeme Rocher (Grails 0.4)
 * @author Sergey Nebolsin (Grails 0.4)
 */
public class NullableConstraint extends AbstractVetoingConstraint {
    public static final String VALIDATION_DSL_NAME = "nullable";
    public static final String DEFAULT_NULL_MESSAGE_CODE = "default.null.message";
    public static final String DEFAULT_NULL_MESSAGE = "Property [{0}] of class [{1}] cannot be null";

    private boolean nullable;

    public boolean isNullable() {
        return nullable;
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return !type.isPrimitive();
    }

    @Override
    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof Boolean, "Parameter for constraint [" + VALIDATION_DSL_NAME +
            "] of property [" + constraintPropertyName + "] of class [" +
            constraintOwningClass + "] must be a boolean value");

        nullable = (Boolean) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    protected boolean skipNullValues() {
        return false;
    }

    @Override
    protected boolean processValidateWithVetoing(@Nonnull Object target, @Nullable Object propertyValue, @Nonnull Errors errors) {
        if (propertyValue == null) {
            if (!nullable) {
                Object[] args = new Object[]{constraintPropertyName, constraintOwningClass};
                rejectValue(target, errors, DEFAULT_NULL_MESSAGE_CODE,
                    VALIDATION_DSL_NAME, args);
                // null value is caught by 'blank' constraint, no addition validation needed
                return true;
            }
        }
        return false;
    }
}
