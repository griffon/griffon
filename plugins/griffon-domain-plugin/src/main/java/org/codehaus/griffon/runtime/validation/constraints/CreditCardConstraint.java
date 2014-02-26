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
import org.apache.commons.validator.routines.CreditCardValidator;

import javax.annotation.Nonnull;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * Validates a credit card number.
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class CreditCardConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "creditCard";
    public static final String DEFAULT_INVALID_CREDIT_CARD_MESSAGE_CODE = "default.invalid.creditCard.message";
    public static final String DEFAULT_INVALID_CREDIT_CARD_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] is not a valid credit card number";

    private boolean creditCard;

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        if (!creditCard) {
            return;
        }

        CreditCardValidator validator = new CreditCardValidator();

        if (!validator.isValid(String.valueOf(propertyValue))) {
            Object[] args = new Object[]{constraintPropertyName, constraintOwningClass, propertyValue};
            rejectValue(target, errors, DEFAULT_INVALID_CREDIT_CARD_MESSAGE_CODE,
                VALIDATION_DSL_NAME + INVALID_SUFFIX, args);
        }
    }

    @Override
    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof Boolean,
            "Parameter for constraint [" +
                VALIDATION_DSL_NAME + "] of property [" +
                constraintPropertyName + "] of class [" +
                constraintOwningClass + "] must be a boolean value");

        creditCard = (Boolean) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return CharSequence.class.isAssignableFrom(type);
    }
}
