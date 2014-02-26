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
import java.util.List;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * A constraint that validates the property is contained within the supplied list.
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class InListConstraint extends AbstractConstraint {
    public static final String VALIDATION_DSL_NAME = "inList";
    public static final String DEFAULT_NOT_INLIST_MESSAGE_CODE = "default.not.inlist.message";
    public static final String DEFAULT_NOT_IN_LIST_MESSAGE = "Property [{0}] of class [{1}] with value [{2}] is not contained within the list [{3}]";

    private List<?> list;

    /**
     * @return Returns the list.
     */
    public List<?> getList() {
        return list;
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        requireNonNull(type, "Argument 'type' cannot be null");
        return true;
    }

    @Override
    public void setParameter(@Nonnull Object constraintParameter) {
        requireState(constraintParameter instanceof List<?>, "Parameter for constraint [" +
            VALIDATION_DSL_NAME + "] of property [" +
            constraintPropertyName + "] of class [" + constraintOwningClass +
            "] must implement the interface [java.util.List]");

        list = (List<?>) constraintParameter;
        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        // Check that the list contains the given value. If not, add an error.
        if (!list.contains(propertyValue)) {
            Object[] args = new Object[]{constraintPropertyName, constraintOwningClass, propertyValue, list};
            rejectValue(target, errors, DEFAULT_NOT_INLIST_MESSAGE_CODE,
                NOT_PREFIX + VALIDATION_DSL_NAME, args);
        }
    }
}
