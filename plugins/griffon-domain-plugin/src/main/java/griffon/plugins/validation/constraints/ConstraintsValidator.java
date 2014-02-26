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
package griffon.plugins.validation.constraints;

import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainClassProperty;
import griffon.plugins.validation.Validateable;
import griffon.util.GriffonClassUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public final class ConstraintsValidator {
    private static final String ERROR_VALIDATEABLE_NULL = "Argument 'validateable' cannot be null";

    private ConstraintsValidator() {

    }

    public static boolean evaluate(@Nonnull Validateable validateable, @Nonnull List<String> properties) {
        requireNonNull(properties, "Argument 'properties' cannot be null");
        return evaluate(validateable, properties.toArray(new String[properties.size()]));
    }

    public static boolean evaluate(@Nonnull Validateable validateable, @Nullable String... properties) {
        requireNonNull(validateable, ERROR_VALIDATEABLE_NULL);
        Map<String, ConstrainedProperty> constrainedProperties = new LinkedHashMap<>();

        if (properties == null || properties.length == 0) {
            constrainedProperties.putAll(validateable.constrainedProperties());
        } else {
            for (String property : properties) {
                constrainedProperties.put(property, validateable.constrainedProperties().get(property));
            }
        }

        for (Map.Entry<String, ConstrainedProperty> entry : constrainedProperties.entrySet()) {
            ConstrainedProperty constrainedProperty = entry.getValue();
            constrainedProperty.validate(validateable, getPropertyValue(validateable, entry.getKey()), validateable.getErrors());
        }

        return !validateable.getErrors().hasErrors();
    }

    @Nullable
    private static Object getPropertyValue(@Nonnull Validateable validateable, @Nonnull String propertyName) {
        requireNonNull(validateable, ERROR_VALIDATEABLE_NULL);
        requireNonBlank(propertyName, "Argument 'propertyName' cannot be blank");
        if (validateable instanceof GriffonDomain) {
            GriffonDomainClass<?> griffonDomainClass = (GriffonDomainClass) ((GriffonDomain) validateable).getGriffonClass();
            for (GriffonDomainClassProperty property : griffonDomainClass.getPersistentProperties()) {
                if (property.getName().equals(propertyName)) {
                    return property.getValue(validateable);
                }
            }
        } else {
            return GriffonClassUtils.getPropertyValue(validateable, propertyName);
        }
        return null;
    }
}
