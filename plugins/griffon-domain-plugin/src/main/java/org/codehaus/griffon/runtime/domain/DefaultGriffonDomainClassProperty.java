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

import griffon.plugins.domain.GriffonDomainClass;

import java.beans.PropertyDescriptor;

/**
 * Represents a property of a domain class and contains meta information about the
 * properties relationships, naming conventions and type.
 *
 * @author Andres Almiray
 */
public class DefaultGriffonDomainClassProperty extends AbstractGriffonDomainClassProperty {
    private boolean persistent = true;

    public DefaultGriffonDomainClassProperty(GriffonDomainClass<?> domainClass, PropertyDescriptor propertyDescriptor) {
        super(domainClass, propertyDescriptor);
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public String toString() {
        return "GriffonDomainClassProperty{" +
            "name=" + getName() +
            ", type=" + getType() +
            ", domainClass=" + getDomainClass() +
            ", persistent=" + persistent +
            '}';
    }
}
