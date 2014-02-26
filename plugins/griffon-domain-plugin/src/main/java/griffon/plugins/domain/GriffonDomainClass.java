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
package griffon.plugins.domain;

import griffon.core.artifact.GriffonClass;
import griffon.plugins.validation.constraints.ConstrainedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * <p>Represents a persistable Griffon domain class</p>
 *
 * @author Andres Almiray
 */
public interface GriffonDomainClass<T> extends GriffonClass {
    /**
     * "domain"
     */
    String TYPE = "domain";
    /**
     * "" (empty)
     */
    String TRAILING = "";

    /**
     * Returns all of the properties of the domain class
     *
     * @return The domain class properties
     */
    @Nonnull
    GriffonDomainClassProperty[] getProperties();

    /**
     * Returns all of the persistent properties of the domain class
     *
     * @return The domain class' persistent properties
     */
    @Nonnull
    GriffonDomainClassProperty[] getPersistentProperties();

    /**
     * Returns the property for the given name
     *
     * @param name The property for the name
     * @return The domain class property for the given name
     * @throws griffon.plugins.domain.exceptions.InvalidPropertyException
     *
     */
    @Nullable
    GriffonDomainClassProperty getPropertyByName(String name);

    @Nonnull
    Map<String, ConstrainedProperty> getConstrainedProperties();

    @Nonnull
    GriffonDomainProperty getIdentity();

    @Nonnull
    GriffonDomainHandler getDomainHandler();
}