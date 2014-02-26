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
import griffon.plugins.domain.exceptions.UnsupportedDomainMethodException;
import griffon.plugins.domain.methods.ListMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public abstract class AbstractListPersistentMethod extends AbstractPersistentStaticMethodInvocation implements ListMethod {
    public AbstractListPersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends GriffonDomain> Object invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments) {
        if (arguments.length == 0) {
            return list(domainClass, Collections.<String, Object>emptyMap());
        } else if (arguments[0] instanceof Map) {
            return list(domainClass, (Map) arguments[0]);
        }
        throw new StaticMethodInvocationException(domainClass.getClazz(), methodName, arguments);
    }

    @Nonnull
    protected <T extends GriffonDomain> Collection<T> list(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull Map<String, Object> options) {
        throw new UnsupportedDomainMethodException();
    }
}