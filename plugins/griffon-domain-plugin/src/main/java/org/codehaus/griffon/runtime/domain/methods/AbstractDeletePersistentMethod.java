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

import griffon.exceptions.InstanceMethodInvocationException;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.exceptions.UnsupportedDomainMethodException;
import griffon.plugins.domain.methods.DeleteMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractDeletePersistentMethod extends AbstractPersistentInstanceMethodInvocation implements DeleteMethod {
    public AbstractDeletePersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends GriffonDomain> T invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull T target, @Nonnull String methodName, @Nonnull Object[] arguments) {
        requireNonNull(target, "Argument 'target' cannot be null");

        Map<String, Object> params = new LinkedHashMap<>();

        if (arguments.length == 1 && arguments[0] instanceof Map) {
            params = (Map) arguments[0];
        } else if (arguments.length != 0) {
            throw new InstanceMethodInvocationException(domainClass.getClazz(), methodName, arguments);
        }

        target.beforeDelete();
        T entity = delete(domainClass, target, params);
        target.afterDelete();
        return entity;
    }

    @Nonnull
    protected <T extends GriffonDomain> T delete(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull T target, @Nonnull Map<String, Object> params) {
        throw new UnsupportedDomainMethodException();
    }
}