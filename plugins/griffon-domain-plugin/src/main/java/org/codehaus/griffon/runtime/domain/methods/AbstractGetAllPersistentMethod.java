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

import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.exceptions.UnsupportedDomainMethodException;
import griffon.plugins.domain.methods.GetAllMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author Andres Almiray
 */
public abstract class AbstractGetAllPersistentMethod extends AbstractPersistentStaticMethodInvocation implements GetAllMethod {
    public AbstractGetAllPersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends GriffonDomain> Object invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments) {
        if (arguments.length == 0) {
            return getAll(domainClass);
        }

        final Object arg = arguments[0];
        if (arg instanceof List) {
            return getAllByIdentities(domainClass, (List) arg);
        } else if (arg instanceof Object[]) {
            return getAllByIdentities(domainClass, (Object[]) arg);
        } else {
            return getAllByIdentities(domainClass, arguments);
        }
    }

    @Nonnull
    protected <T extends GriffonDomain> Collection<T> getAll(@Nonnull GriffonDomainClass<T> domainClass) {
        throw new UnsupportedDomainMethodException();
    }

    @Nonnull
    protected <T extends GriffonDomain> Collection<T> getAllByIdentities(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull List<Object> identities) {
        throw new UnsupportedDomainMethodException();
    }

    @Nonnull
    protected <T extends GriffonDomain> Collection<T> getAllByIdentities(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull Object[] identities) {
        throw new UnsupportedDomainMethodException();
    }
}