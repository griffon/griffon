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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;

/**
 * @author Andres Almiray
 */
public interface GriffonDomainProperty {
    String IDENTITY = "id";
    String VERSION = "version";
    String TRANSIENTS = "transients";
    String CONSTRAINTS = "constraints";
    String BELONGS_TO = "belongsTo";
    String HAS_MANY = "hasMany";
    String HAS_ONE = "hasOne";
    String DATE_CREATED = "dateCreated";
    String LAST_UPDATED = "lastUpdated";
    String ERRORS = "errors";
    String PROPERTY_CHANGE_LISTENERS = "propertyChangeListeners";
    Set<String> NON_CONFIGURATIONAL_PROPERTIES = new TreeSet<String>(
        asList(TRANSIENTS, CONSTRAINTS, BELONGS_TO, HAS_MANY, HAS_ONE, ERRORS, PROPERTY_CHANGE_LISTENERS));
    Set<String> STANDARD_DOMAIN_PROPERTIES = new TreeSet<>(asList(IDENTITY, VERSION));

    /**
     * Returns the name of the property
     *
     * @return The property name
     */
    @Nonnull
    String getName();

    /**
     * Returns the type for the domain class
     *
     * @return The property type
     */
    @Nonnull
    Class<?> getType();

    @Nullable
    Object getValue(@Nonnull Object owner);

    void setValue(@Nonnull Object owner, @Nullable Object value);
}
