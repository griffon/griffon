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
import griffon.plugins.domain.exceptions.UnsupportedDomainMethodException;
import griffon.plugins.domain.methods.ListOrderByMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static griffon.util.GriffonNameUtils.uncapitalize;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractListOrderByPersistentMethod extends AbstractPersistentStaticDynamicMethodInvocation implements ListOrderByMethod {
    private static final String METHOD_PATTERN = "(" + METHOD_NAME + ")([A-Z]\\w*)";

    protected AbstractListOrderByPersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler, Pattern.compile(METHOD_PATTERN));
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends GriffonDomain> Object invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments) {
        String propertyName = (String) arguments[0];
        Map<String, Object> params = new LinkedHashMap<>();

        requireNonBlank(propertyName, "Argument 'propertyName' cannot be blank");
        propertyName = uncapitalize(propertyName);

        GriffonDomainProperty prop = domainClass.getPropertyByName(propertyName);
        requireNonNull(prop, "Property " + propertyName + " doesn't exist for '" + domainClass.getClazz().getName() + "'");

        if (arguments.length == 2) {
            if (arguments[1] instanceof Map) {
                params = (Map<String, Object>) arguments[1];
            } else {
                throw new StaticMethodInvocationException(domainClass.getClazz(), methodName, arguments);
            }
        }
        if (!params.containsKey(ORDER)) params.put(ORDER, ASC);
        return listOrderBy(domainClass, propertyName, params);
    }

    @Nonnull
    protected <T extends GriffonDomain> Collection<T> listOrderBy(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String propertyName, @Nonnull Map<String, Object> params) {
        throw new UnsupportedDomainMethodException();
    }
}