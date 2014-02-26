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

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * A constraint that validates the property against a supplied regular expression.
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class MatchesConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "matches";
    public static final String DEFAULT_DOESNT_MATCH_MESSAGE_CODE = "default.doesnt.match.message";
    public static final String DEFAULT_DOESNT_MATCH_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] does not match the required pattern [{3}]";

    private String regex;

    /**
     * @return Returns the regex.
     */
    public String getRegex() {
        return regex;
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return CharSequence.class.isAssignableFrom(type);
    }

    @Override
    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof CharSequence,
            "Parameter for constraint [" + VALIDATION_DSL_NAME + "] of property ["
                + constraintPropertyName + "] of class [" + constraintOwningClass
                + "] must be of type [java.lang.String]");

        regex = String.valueOf(constraintParameter);
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        if (!propertyValue.toString().matches(regex)) {
            Object[] args = new Object[]{constraintPropertyName, constraintOwningClass, propertyValue, regex};
            rejectValue(target, errors, DEFAULT_DOESNT_MATCH_MESSAGE_CODE,
                VALIDATION_DSL_NAME + INVALID_SUFFIX, args);
        }
    }
}
