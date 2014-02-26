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
import griffon.plugins.validation.constraints.VetoingConstraint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * TODO: write javadoc
 *
 * @author Sergey Nebolsin (<a href="mailto:nebolsin@gmail.com"/>)
 */
public abstract class AbstractVetoingConstraint extends AbstractConstraint implements VetoingConstraint {
    public boolean validateWithVetoing(@Nonnull Object target, @Nullable Object propertyValue, @Nonnull Errors errors) {
        checkState();
        return !(propertyValue == null && skipNullValues()) &&
            processValidateWithVetoing(target, propertyValue, errors);
    }

    @Override
    protected void processValidate(@Nonnull Object target, @Nullable Object propertyValue, @Nonnull Errors errors) {
        processValidateWithVetoing(target, propertyValue, errors);
    }

    protected abstract boolean processValidateWithVetoing(@Nonnull Object target, @Nullable Object propertyValue, @Nonnull Errors errors);
}
