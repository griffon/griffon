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

import griffon.core.formatters.DateFormatter;
import griffon.core.formatters.ParseException;
import griffon.plugins.validation.Errors;

import javax.annotation.Nonnull;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Objects.requireNonNull;

/**
 * Validates a Date based on a format.
 *
 * @author Antony Jones (Grails)
 * @author Andres Almiray
 */
public class DateConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "date";
    public static final String DEFAULT_INVALID_DATE_MESSAGE_CODE = "default.not.date.message";
    public static final String DEFAULT_INVALID_DATE_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] is not a valid date given the format [{3}]";

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        String value = propertyValue.toString();
        if (isBlank(value)) {
            return;
        }

        try {
            new DateFormatter(String.valueOf(constraintParameter)).parse(value);
        } catch (ParseException pe) {
            Object[] args = new Object[]{constraintPropertyName, constraintOwningClass, propertyValue, constraintParameter};
            rejectValue(target, errors, DEFAULT_INVALID_DATE_MESSAGE_CODE,
                NOT_PREFIX + VALIDATION_DSL_NAME + INVALID_SUFFIX, args);
        }
    }

    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof CharSequence, "Parameter for constraint [" +
            VALIDATION_DSL_NAME + "] of property [" +
            constraintPropertyName + "] of class [" +
            constraintOwningClass + "] must be a date-formatting String " +
            "(see http://download.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html)");

        super.setParameter(constraintParameter);
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return CharSequence.class.isAssignableFrom(type);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }
}