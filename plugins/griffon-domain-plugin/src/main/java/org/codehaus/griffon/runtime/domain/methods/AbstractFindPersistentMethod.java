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
import griffon.plugins.domain.methods.FindMethod;
import griffon.plugins.domain.orm.Criterion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractFindPersistentMethod extends AbstractPersistentStaticMethodInvocation implements FindMethod {
    public AbstractFindPersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    protected final <T extends GriffonDomain> Object invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull Object[] arguments) {
        if (arguments.length == 0) {
            throw new StaticMethodInvocationException(domainClass.getClazz(), methodName, arguments);
        }
        final Object arg = arguments[0];
        requireNonNull(arg, "Argument cannot be null");
        if (arg instanceof Criterion) {
            if (arguments.length == 1) {
                return findByCriterion(domainClass, (Criterion) arg);
            }
            // TODO throw exception ?
        } /*else if (arg instanceof Closure) {
            return findByCriterion(domainClass, GriffonDomainClassUtils.getInstance().buildCriterion((Closure) arg), Collections.<String, Object>emptyMap());
        } */ else if (arg instanceof Map) {
            if (arguments.length == 1) {
                return findByProperties(domainClass, (Map) arg);
            } /*else if (arguments[1] instanceof Closure) {
                return findByCriterion(domainClass, GriffonDomainClassUtils.getInstance().buildCriterion((Closure) arguments[1]), (Map) arg);
            }*/
        } else if (domainClass.getClazz().isAssignableFrom(arg.getClass())) {
            return findByExample(domainClass, arg);
        }
        throw new StaticMethodInvocationException(domainClass.getClazz(), methodName, arguments);
    }

    @Nullable
    protected <T extends GriffonDomain> T findByProperties(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull Map<String, Object> properties) {
        throw new UnsupportedDomainMethodException();
    }

    @Nullable
    protected <T extends GriffonDomain> T findByExample(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull Object example) {
        throw new UnsupportedDomainMethodException();
    }

    @Nullable
    protected <T extends GriffonDomain> T findByCriterion(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull Criterion criterion) {
        throw new UnsupportedDomainMethodException();
    }
}