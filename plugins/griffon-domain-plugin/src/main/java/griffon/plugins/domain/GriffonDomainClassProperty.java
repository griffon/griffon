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

/**
 * A property of a GriffonDomainClass<T> instance
 *
 * @author Andres Almiray
 */
public interface GriffonDomainClassProperty extends GriffonDomainProperty {
    /**
     * Returns the parent domain class of the property instance
     *
     * @return The parent domain class
     */
    @Nonnull
    GriffonDomainClass<?> getDomainClass();

    /**
     * Returns true if the domain class property is a persistent property
     *
     * @return Whether the property is persistent
     */
    boolean isPersistent();
}