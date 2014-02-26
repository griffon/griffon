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
package org.codehaus.griffon.runtime.domain;

import griffon.core.GriffonApplication;
import griffon.plugins.domain.GriffonDomainClassProperty;
import griffon.plugins.validation.constraints.ConstraintsEvaluator;
import griffon.util.GriffonClassUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.beans.PropertyDescriptor;

import static griffon.util.GriffonClassUtils.getPropertyDescriptors;
import static org.codehaus.griffon.runtime.domain.GriffonDomainConfigurationUtil.isNotConfigurational;
import static org.codehaus.griffon.runtime.domain.GriffonDomainConfigurationUtil.isTransientProperty;

/**
 * @author Andres Almiray
 */
public class DefaultGriffonDomainClass<T> extends AbstractGriffonDomainClass<T> {
    @Inject
    public DefaultGriffonDomainClass(@Nonnull GriffonApplication application, @Nonnull Class<?> clazz) {
        super(application, clazz);
    }

    protected void initialize() {
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(getClazz());

        populateDomainClassProperties(propertyDescriptors);
    }

    /**
     * Populates the domain class properties map
     *
     * @param propertyDescriptors The property descriptors
     */
    private void populateDomainClassProperties(PropertyDescriptor[] propertyDescriptors) {
        for (PropertyDescriptor descriptor : propertyDescriptors) {

            if (descriptor.getPropertyType() == null) {
                // indexed property
                continue;
            }

            // ignore certain properties
            if (isNotConfigurational(descriptor) && !isTransientProperty(getClazz(), descriptor)) {
                GriffonDomainClassProperty property = new DefaultGriffonDomainClassProperty(this, descriptor);
                domainProperties.put(property.getName(), property);
            }
        }

        ConstraintsEvaluator constraintEvaluator = getApplication().getInjector().getInstance(ConstraintsEvaluator.class);
        constrainedProperties.putAll(constraintEvaluator.evaluate(this));
    }

    @Nonnull
    public String getName() {
        return GriffonClassUtils.getShortClassName(super.getName());
    }
}
