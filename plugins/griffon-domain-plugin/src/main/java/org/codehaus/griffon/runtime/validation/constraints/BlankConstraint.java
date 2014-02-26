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
import griffon.util.GriffonNameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * A Constraint that validates a string is not blank.
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class BlankConstraint extends AbstractVetoingConstraint {
    public static final String VALIDATION_DSL_NAME = "blank";
    public static final String DEFAULT_BLANK_MESSAGE_CODE = "default.blank.message";
    public static final String DEFAULT_BLANK_MESSAGE = "Property [{0}] of class [{1}] cannot be blank";

    private boolean blank;

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return CharSequence.class.isAssignableFrom(type);
    }

    @Nonnull
    @Override
    public Object getParameter() {
        return blank;
    }

    public boolean isBlank() {
        return blank;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof Boolean,
            "Parameter for constraint [" + VALIDATION_DSL_NAME + "] of property [" +
                constraintPropertyName + "] of class [" + constraintOwningClass +
                "] must be a boolean value");

        blank = (Boolean) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    protected boolean skipBlankValues() {
        return false;
    }

    @Override
    protected boolean processValidateWithVetoing(@Nonnull Object target, @Nullable Object propertyValue, @Nonnull Errors errors) {
        if (propertyValue instanceof CharSequence && GriffonNameUtils.isBlank((String.valueOf(propertyValue)))) {
            if (!blank) {
                Object[] args = new Object[]{constraintPropertyName, constraintOwningClass};
                rejectValue(target, errors,
                    DEFAULT_BLANK_MESSAGE_CODE,
                    VALIDATION_DSL_NAME, args);
                // empty string is caught by 'blank' constraint, no addition validation needed
                return true;
            }
        }
        return false;
    }
}
