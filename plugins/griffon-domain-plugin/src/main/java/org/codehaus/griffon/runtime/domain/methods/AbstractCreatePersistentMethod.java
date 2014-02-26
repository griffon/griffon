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
package org.codehaus.griffon.runtime.domain.methods;

import griffon.exceptions.StaticMethodInvocationException;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.plugins.domain.methods.CreateMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static griffon.util.GriffonClassUtils.requireState;
import static java.lang.reflect.Modifier.isAbstract;

/**
 * @author Andres Almiray
 */
public abstract class AbstractCreatePersistentMethod extends AbstractPersistentStaticMethodInvocation implements CreateMethod {
    public AbstractCreatePersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends GriffonDomain> Object invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments) {
        requireState(!isAbstract(domainClass.getClazz().getModifiers()), "Cannot invoke " + METHOD_NAME + " on an abstract class!");
        if (arguments.length == 0) {
            return createInstance(domainClass);
        } else if (arguments[0] instanceof Map) {
            return create(domainClass, (Map) arguments[0]);
        }
        throw new StaticMethodInvocationException(domainClass.getClazz(), methodName, arguments);
    }

    @Nonnull
    protected <T extends GriffonDomain> GriffonDomain create(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull Map<String, Object> props) {
        GriffonDomain instance = createInstance(domainClass);
        applyProperties(domainClass, instance, props);
        return instance;
    }

    @Nonnull
    private <T extends GriffonDomain> GriffonDomain createInstance(@Nonnull GriffonDomainClass<T> domainClass) {
        return (GriffonDomain) getGriffonDomainHandler().getApplication().getArtifactManager().newInstance(domainClass);
    }

    private <T extends GriffonDomain> void applyProperties(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull GriffonDomain instance, @Nonnull Map<String, Object> props) {
        for (GriffonDomainProperty property : domainClass.getProperties()) {
            Object value = props.get(property.getName());
            if (value != null) property.setValue(instance, value);
        }
    }
}