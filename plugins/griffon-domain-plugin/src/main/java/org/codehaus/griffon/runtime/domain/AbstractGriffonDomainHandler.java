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

import griffon.core.GriffonApplication;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.plugins.domain.exceptions.UnsupportedDomainMethodException;
import griffon.plugins.domain.methods.*;
import griffon.plugins.domain.orm.Criterion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public abstract class AbstractGriffonDomainHandler implements GriffonDomainHandler {
    private static final String ERROR_METHOD_NAME_BLANK = "Argument 'methodName' cannot be blank";
    private final Map<String, InstanceMethodInvocation> instanceMethods = new LinkedHashMap<>();
    private final Map<String, StaticMethodInvocation> staticMethods = new LinkedHashMap<>();
    private final GriffonApplication application;

    @Inject
    public AbstractGriffonDomainHandler(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
        instanceMethods.putAll(getInstanceMethods());
        staticMethods.putAll(getStaticMethods());
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    protected abstract Map<String, InstanceMethodInvocation> getInstanceMethods();

    @Nonnull
    protected abstract Map<String, StaticMethodInvocation> getStaticMethods();

    @Nullable
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ConstantConditions"})
    public final <T extends GriffonDomain> T invokeInstanceMethod(@Nonnull T target, @Nonnull String methodName, Object... args) {
        try {
            requireNonNull(target, "Argument 'target' cannot be null");
            requireNonBlank(methodName, ERROR_METHOD_NAME_BLANK);
            requireState(GriffonDomain.class.isAssignableFrom(target.getClass()),
                "Cannot call " + methodName + "() on non-domain class [" + target.getClass().getName() + "]");

            InstanceMethodInvocation method = instanceMethods.get(methodName);
            requireNonNull(method != null,
                "Method " + methodName + " is undefined for domain classes mapped with '" + getMapping() + "'");
            return doInvokeInstanceMethod(method, target, methodName, args);
        } catch (UnsupportedDomainMethodException udme) {
            throw (RuntimeException) sanitize(new UnsupportedOperationException("Domain method " + methodName + " is not supported by mapping '" + getMapping() + "'"));
        } catch (RuntimeException e) {
            Throwable t = sanitize(e);
            throw (RuntimeException) t;
        }
    }

    @Nullable
    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ConstantConditions"})
    public final <T extends GriffonDomain> Object invokeStaticMethod(@Nonnull Class<T> clazz, @Nonnull String methodName, Object... args) {
        try {
            requireNonNull(clazz, "Argument 'class' cannot be null");
            requireNonBlank(methodName, ERROR_METHOD_NAME_BLANK);
            requireState(GriffonDomain.class.isAssignableFrom(clazz),
                "Cannot call " + methodName + "() on non-domain class [" + clazz.getName() + "]");

            StaticMethodInvocation method = staticMethods.get(methodName);
            requireNonNull(method != null,
                "Method " + methodName + " is undefined for domain classes mapped with '" + getMapping() + "'");
            return doInvokeStaticMethod(method, clazz, methodName, args);
        } catch (UnsupportedDomainMethodException udme) {
            throw (RuntimeException) sanitize(new UnsupportedOperationException("Domain method " + methodName + " is not supported by mapping '" + getMapping() + "'"));
        } catch (RuntimeException e) {
            Throwable t = sanitize(e);
            throw (RuntimeException) t;
        }
    }

    @Nullable
    protected <T extends GriffonDomain> T doInvokeInstanceMethod(@Nonnull InstanceMethodInvocation method, @Nonnull T target, @Nonnull String methodName, Object... args) {
        return method.invoke(target, methodName, args);
    }

    @Nullable
    protected <T extends GriffonDomain> Object doInvokeStaticMethod(@Nonnull StaticMethodInvocation method, @Nonnull Class<T> clazz, @Nonnull String methodName, Object... args) {
        return method.invoke(clazz, methodName, args);
    }

    protected final GriffonDomain.Comparator IDENTITY_COMPARATOR = new GriffonDomain.Comparator(GriffonDomainProperty.IDENTITY);

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    protected GriffonDomainProperty identityOf(@Nonnull GriffonDomain target) {
        GriffonDomainClass<?> domainClass = (GriffonDomainClass) target.getGriffonClass();
        return domainClass.getPropertyByName(GriffonDomainProperty.IDENTITY);
    }

    // == CountByMethodHandler

    @Override
    @SuppressWarnings("ConstantConditions")
    public <T extends GriffonDomain> int countBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull Object[] arguments) {
        return (Integer) invokeStaticMethod(clazz, CountByMethod.METHOD_NAME, clause, arguments);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public <T extends GriffonDomain> int countBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull List<Object> arguments) {
        return (Integer) invokeStaticMethod(clazz, CountByMethod.METHOD_NAME, clause, arguments);
    }

    // == CountMethodHandler

    @Override
    @SuppressWarnings("ConstantConditions")
    public <T extends GriffonDomain> int count(@Nonnull Class<T> clazz) {
        return (Integer) invokeStaticMethod(clazz, CountMethod.METHOD_NAME);
    }

    // == CreateMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T create(@Nonnull Class<T> clazz) {
        return (T) invokeStaticMethod(clazz, CreateMethod.METHOD_NAME);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T create(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> attributes) {
        return (T) invokeStaticMethod(clazz, CreateMethod.METHOD_NAME, attributes);
    }

    // == DeleteMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public <T extends GriffonDomain> T delete(@Nonnull T instance) {
        return invokeInstanceMethod(instance, DeleteMethod.METHOD_NAME);
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public <T extends GriffonDomain> T delete(@Nonnull T instance, @Nonnull Map<String, Object> options) {
        return invokeInstanceMethod(instance, DeleteMethod.METHOD_NAME, options);
    }

    // == ExistsMethodHandler

    @Override
    @SuppressWarnings("ConstantConditions")
    public <T extends GriffonDomain> boolean exists(@Nonnull Class<T> clazz, @Nonnull Object key) {
        return (Boolean) invokeStaticMethod(clazz, ExistsMethod.METHOD_NAME, key);
    }

    // == FindAllByMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAllBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull Object[] arguments) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllByMethod.METHOD_NAME, clause, arguments);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAllBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull List<Object> arguments) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllByMethod.METHOD_NAME, clause, arguments);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAllBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull Object[] arguments, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllByMethod.METHOD_NAME, clause, arguments, options);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAllBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull List<Object> arguments, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllByMethod.METHOD_NAME, clause, arguments, options);
    }

    // == FindAllMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAll(@Nonnull Class<T> clazz, @Nonnull Object example) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllMethod.METHOD_NAME, example);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAll(@Nonnull Class<T> clazz, @Nonnull Object example, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllMethod.METHOD_NAME, example, options);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAll(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllMethod.METHOD_NAME, params);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAll(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllMethod.METHOD_NAME, params, options);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAll(@Nonnull Class<T> clazz, @Nonnull Criterion criterion) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllMethod.METHOD_NAME, criterion);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAll(@Nonnull Class<T> clazz, @Nonnull Criterion criterion, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllMethod.METHOD_NAME, criterion, options);
    }

    // == FindAllWhereMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAllWhere(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllWhereMethod.METHOD_NAME, params);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> findAllWhere(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, FindAllWhereMethod.METHOD_NAME, params, options);
    }

    // == FindByMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull Object[] arguments) {
        return (T) invokeStaticMethod(clazz, FindByMethod.METHOD_NAME, clause, arguments);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull List<Object> arguments) {
        return (T) invokeStaticMethod(clazz, FindByMethod.METHOD_NAME, clause, arguments);
    }

    // == FindMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T find(@Nonnull Class<T> clazz, @Nonnull Object example) {
        return (T) invokeStaticMethod(clazz, FindMethod.METHOD_NAME, example);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T find(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (T) invokeStaticMethod(clazz, FindMethod.METHOD_NAME, params);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T find(@Nonnull Class<T> clazz, @Nonnull Criterion criterion) {
        return (T) invokeStaticMethod(clazz, FindMethod.METHOD_NAME, criterion);
    }

    // == FindOrCreateByMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrCreateBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull Object[] arguments) {
        return (T) invokeStaticMethod(clazz, FindOrCreateByMethod.METHOD_NAME, clause, arguments);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrCreateBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull List<Object> arguments) {
        return (T) invokeStaticMethod(clazz, FindOrCreateByMethod.METHOD_NAME, clause, arguments);
    }

    // == FindOrCreateWhereMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrCreateWhere(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (T) invokeStaticMethod(clazz, FindOrCreateWhereMethod.METHOD_NAME, params);
    }

    // == FindOrSaveByMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrSaveBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull Object[] arguments) {
        return (T) invokeStaticMethod(clazz, FindOrSaveByMethod.METHOD_NAME, clause, arguments);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrSaveBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull List<Object> arguments) {
        return (T) invokeStaticMethod(clazz, FindOrSaveByMethod.METHOD_NAME, clause, arguments);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrSaveBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull Object[] arguments, @Nonnull Map<String, Object> options) {
        return (T) invokeStaticMethod(clazz, FindOrSaveByMethod.METHOD_NAME, clause, arguments, options);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrSaveBy(@Nonnull Class<T> clazz, @Nonnull String clause, @Nonnull List<Object> arguments, @Nonnull Map<String, Object> options) {
        return (T) invokeStaticMethod(clazz, FindOrSaveByMethod.METHOD_NAME, clause, arguments, options);
    }

    // == FindOrSaveWhereMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrSaveWhere(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (T) invokeStaticMethod(clazz, FindOrSaveWhereMethod.METHOD_NAME, params);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findOrSaveWhere(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params, @Nonnull Map<String, Object> options) {
        return (T) invokeStaticMethod(clazz, FindOrSaveWhereMethod.METHOD_NAME, params, options);
    }

    // == FindWhereMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T findWhere(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (T) invokeStaticMethod(clazz, FindWhereMethod.METHOD_NAME, params);
    }

    // == FirstMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T first(@Nonnull Class<T> clazz) {
        return (T) invokeStaticMethod(clazz, FirstMethod.METHOD_NAME);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T first(@Nonnull Class<T> clazz, @Nonnull String propertyName) {
        return (T) invokeStaticMethod(clazz, FirstMethod.METHOD_NAME, propertyName);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T first(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (T) invokeStaticMethod(clazz, FirstMethod.METHOD_NAME, params);
    }

    // == GetAllMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> getAll(@Nonnull Class<T> clazz) {
        return (Collection<T>) invokeStaticMethod(clazz, GetAllMethod.METHOD_NAME);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> getAll(@Nonnull Class<T> clazz, @Nonnull Object[] keys) {
        return (Collection<T>) invokeStaticMethod(clazz, GetAllMethod.METHOD_NAME, keys);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> getAll(@Nonnull Class<T> clazz, @Nonnull List<Object> keys) {
        return (Collection<T>) invokeStaticMethod(clazz, GetAllMethod.METHOD_NAME, keys);
    }

    // == GetMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T get(@Nonnull Class<T> clazz, @Nonnull Object key) {
        return (T) invokeStaticMethod(clazz, GetMethod.METHOD_NAME, key);
    }

    // == LastMethodHandler

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T last(@Nonnull Class<T> clazz) {
        return (T) invokeStaticMethod(clazz, LastMethod.METHOD_NAME);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T last(@Nonnull Class<T> clazz, @Nonnull String propertyName) {
        return (T) invokeStaticMethod(clazz, LastMethod.METHOD_NAME, propertyName);
    }

    @Nullable
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> T last(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> params) {
        return (T) invokeStaticMethod(clazz, LastMethod.METHOD_NAME, params);
    }

    // == ListMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> list(@Nonnull Class<T> clazz) {
        return (Collection<T>) invokeStaticMethod(clazz, ListMethod.METHOD_NAME);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> list(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, ListMethod.METHOD_NAME, options);
    }

    // == ListOrderByMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> listOrderBy(@Nonnull Class<T> clazz, @Nonnull String propertyName) {
        return (Collection<T>) invokeStaticMethod(clazz, ListOrderByMethod.METHOD_NAME, propertyName);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> listOrderBy(@Nonnull Class<T> clazz, @Nonnull String propertyName, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, ListOrderByMethod.METHOD_NAME, propertyName, options);
    }

    // == SaveMethodHandler

    @Nullable
    @Override
    public <T extends GriffonDomain> T save(@Nonnull T instance) {
        return invokeInstanceMethod(instance, SaveMethod.METHOD_NAME);
    }

    @Nullable
    @Override
    public <T extends GriffonDomain> T save(@Nonnull T instance, @Nonnull Map<String, Object> options) {
        return invokeInstanceMethod(instance, SaveMethod.METHOD_NAME);
    }

    // == WithCriteriaMethodHandler

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> withCriteria(@Nonnull Class<T> clazz, @Nonnull Criterion criterion) {
        return (Collection<T>) invokeStaticMethod(clazz, WithCriteriaMethod.METHOD_NAME, criterion);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public <T extends GriffonDomain> Collection<T> withCriteria(@Nonnull Class<T> clazz, @Nonnull Criterion criterion, @Nonnull Map<String, Object> options) {
        return (Collection<T>) invokeStaticMethod(clazz, WithCriteriaMethod.METHOD_NAME, criterion, options);
    }
}
