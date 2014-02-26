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
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Validates a url.
 *
 * @author Graeme Rocher
 * @since 0.4
 */
public class UrlConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "url";
    public static final String DEFAULT_INVALID_URL_MESSAGE_CODE = "default.invalid.url.message";
    public static final String DEFAULT_INVALID_URL_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] is not a valid URL";

    private boolean url;
    private UrlValidator validator;

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return CharSequence.class.isAssignableFrom(type);
    }

    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public void setParameter(@Nonnull Object constraintParameter) {
        RegexValidator domainValidator = null;

        if (constraintParameter instanceof Boolean) {
            url = (Boolean) constraintParameter;
        } else if (constraintParameter instanceof String) {
            url = true;
            domainValidator = new RegexValidator((String) constraintParameter);
        } else if (constraintParameter instanceof List<?>) {
            url = true;
            List<?> regexpList = (List<?>) constraintParameter;
            domainValidator = new RegexValidator(regexpList.toArray(new String[regexpList.size()]));
        } else {
            throw new IllegalArgumentException("Parameter for constraint [" + VALIDATION_DSL_NAME +
                "] of property [" + constraintPropertyName + "] of class [" +
                constraintOwningClass + "] must be a boolean, string, or list value");
        }

        validator = new UrlValidator(domainValidator,
            UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_2_SLASHES);

        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        if (!url) {
            return;
        }

        if (!validator.isValid(propertyValue.toString())) {
            Object[] args = new Object[]{constraintPropertyName, constraintOwningClass, propertyValue};
            rejectValue(target, errors, DEFAULT_INVALID_URL_MESSAGE_CODE,
                VALIDATION_DSL_NAME + INVALID_SUFFIX, args);
        }
    }
}
