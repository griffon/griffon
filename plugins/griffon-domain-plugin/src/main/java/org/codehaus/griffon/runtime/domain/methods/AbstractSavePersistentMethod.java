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
import griffon.plugins.domain.Event;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.exceptions.UnsupportedDomainMethodException;
import griffon.plugins.domain.methods.SaveMethod;
import griffon.plugins.validation.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractSavePersistentMethod extends AbstractPersistentInstanceMethodInvocation implements SaveMethod {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSavePersistentMethod.class);
    private static final String FAIL_ON_ERROR_CONFIG_KEY = "griffon.domain.failOnError";
    public static final String FAIL_ON_ERROR = "failOnError";
    public static final String VALIDATE = "validate";

    public AbstractSavePersistentMethod(@Nonnull GriffonDomainHandler griffonDomainHandler) {
        super(griffonDomainHandler);
    }

    @Nullable
    @Override
    protected final <T extends GriffonDomain> T invokeInternal(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull T target, @Nonnull String methodName, @Nonnull Object[] arguments) {
        requireNonNull(target, "Argument 'target' cannot be null");

        LOG.trace("{}.{}()",target.getClass().getName(), METHOD_NAME);
        Map<String, Object> params = validate(domainClass, methodName, target, arguments);
        if (params == null) return null;

        T entity = null;
        if (shouldInsert(domainClass, target, arguments)) {
            LOG.trace(">> {}.{}()",target.getClass().getName(), Event.beforeInsert.name());
            target.beforeInsert();
            LOG.trace("<< {}.{}()",target.getClass().getName(), Event.beforeInsert.name());
            entity = insert(domainClass, target, arguments, params);
            target.onSave();
            LOG.trace(">> {}.{}()",target.getClass().getName(), Event.afterInsert.name());
            target.afterInsert();
            LOG.trace("<< {}.{}()",target.getClass().getName(), Event.afterInsert.name());
        } else {
            LOG.trace(">> {}.{}()",target.getClass().getName(), Event.beforeUpdate.name());
            target.beforeUpdate();
            LOG.trace("<< {}.{}()",target.getClass().getName(), Event.beforeUpdate.name());
            entity = save(domainClass, target, arguments, params);
            target.onSave();
            LOG.trace(">> {}.{}()",target.getClass().getName(), Event.afterUpdate.name());
            target.afterUpdate();
            LOG.trace("<< {}.{}()",target.getClass().getName(), Event.afterUpdate.name());
        }

        return entity;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected <T extends GriffonDomain> Map<String, Object> validate(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull String methodName, @Nonnull T target, @Nonnull Object[] arguments) {
        LOG.trace("{}.{}()",target.getClass().getName(), "validate");
        Map<String, Object> params = new LinkedHashMap<>();
        if (arguments.length == 1) {
            if (arguments[0] instanceof Map) {
                params.putAll((Map) arguments[0]);
            } else {
                throw new InstanceMethodInvocationException(domainClass.getClazz(), methodName, arguments);
            }
        } else if (arguments.length > 0) {
            throw new InstanceMethodInvocationException(domainClass.getClazz(), methodName, arguments);
        }

        boolean validate = getConfigValueAsBoolean(params, VALIDATE, true);
        boolean failOnError = getConfiguration().getAsBoolean(FAIL_ON_ERROR_CONFIG_KEY, false);
        if (params.containsKey(FAIL_ON_ERROR)) {
            failOnError = getConfigValueAsBoolean(params, FAIL_ON_ERROR);
        }
        params.put(VALIDATE, validate);
        params.put(FAIL_ON_ERROR, failOnError);

        if (validate) {
            target.getErrors().clearAllErrors();
            if (!target.validate()) {
                if (failOnError) {
                    throw new ValidationException("An instance of " + target.getClass() + " failed validation");
                } else {
                    return null;
                }
            }
        }
        return params;
    }

    protected abstract <T extends GriffonDomain> boolean shouldInsert(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull T target, @Nonnull Object[] arguments);

    @Nullable
    protected <T extends GriffonDomain> T insert(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull T target, @Nonnull Object[] arguments, @Nonnull Map<String, Object> params) {
        throw new UnsupportedDomainMethodException();
    }

    @Nullable
    protected <T extends GriffonDomain> T save(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull T target, @Nonnull Object[] arguments, @Nonnull Map<String, Object> params) {
        throw new UnsupportedDomainMethodException();
    }
}