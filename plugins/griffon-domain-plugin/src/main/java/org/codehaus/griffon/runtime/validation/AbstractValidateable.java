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
package org.codehaus.griffon.runtime.validation;

import griffon.core.GriffonApplication;
import griffon.plugins.validation.Errors;
import griffon.plugins.validation.Validateable;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import griffon.plugins.validation.constraints.ConstraintsEvaluator;
import griffon.plugins.validation.constraints.ConstraintsValidator;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractValidateable implements Validateable {
    private final GriffonApplication application;
    private final Errors errors;
    private final Map<String, ConstrainedProperty> constrainedProperties = new LinkedHashMap<>();

    @Inject
    public AbstractValidateable(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
        this.errors = new DefaultErrors(getClass());
        ConstraintsEvaluator constraintsEvaluator = application.getInjector().getInstance(ConstraintsEvaluator.class);
        constrainedProperties.putAll(constraintsEvaluator.evaluate(getClass()));
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    public boolean validate(String... properties) {
        return ConstraintsValidator.evaluate(this, properties);
    }

    public boolean validate(@Nonnull List<String> properties) {
        return ConstraintsValidator.evaluate(this, properties);
    }

    @Nonnull
    public Errors getErrors() {
        return errors;
    }

    @Nonnull
    public Map<String, ConstrainedProperty> constrainedProperties() {
        return constrainedProperties;
    }
}
