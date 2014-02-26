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
import griffon.plugins.domain.methods.FindAllWhereMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Andres Almiray
 */
public abstract class AbstractFindAllWherePersistentMethod extends AbstractPersistentStaticMethodInvocation implements FindAllWhereMethod {
    public AbstractFindAllWherePersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends GriffonDomain> Object invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments) {
        if (arguments.length == 1) {
            final Object arg1 = arguments[0];
            if (arg1 instanceof Map) {
                return findByParams(domainClass, (Map) arg1, Collections.<String, Object>emptyMap());
            }
        } else if (arguments.length == 2) {
            final Object arg1 = arguments[0];
            final Object arg2 = arguments[1];
            if (arg1 instanceof Map && arg2 instanceof Map) {
                return findByParams(domainClass, (Map) arg1, (Map) arg2);
            }
        }
        throw new StaticMethodInvocationException(domainClass.getClazz(), methodName, arguments);
    }

    @Nonnull
    protected <T extends GriffonDomain> Collection<T> findByParams(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull Map<String, Object> params, @Nonnull Map<String, Object> options) {
        throw new UnsupportedDomainMethodException();
    }
}