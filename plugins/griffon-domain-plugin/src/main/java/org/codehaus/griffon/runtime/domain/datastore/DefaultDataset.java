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
package org.codehaus.griffon.runtime.domain.datastore;

import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainClassProperty;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.plugins.domain.datastore.Dataset;
import griffon.plugins.domain.orm.Criterion;
import griffon.plugins.domain.orm.CriterionEvaluator;
import griffon.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static griffon.plugins.domain.GriffonDomainProperty.IDENTITY;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultDataset<T extends GriffonDomain> implements Dataset<T> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataset.class);
    private static final String ERROR_ENTITY_NULL = "Argument 'entity' cannot be null";
    private static final String ERROR_OPTIONS_NULL = "Argument 'options' cannot be null";
    private static final String ERROR_EXAMPLE_NULL = "Argument 'example' cannot be null";
    private static final String ERROR_PARAMS_NULL = "Argument 'params' cannot be null";
    private static final String ERROR_CRITERION_NULL = "Argument 'criterion' cannot be null";

    private final Map<Object, T> ROWS = Collections.synchronizedSortedMap(new TreeMap<Object, T>());
    private final GriffonDomainClass<T> domainClass;
    private final String name;
    private final CriterionEvaluator criterionEvaluator;
    private final AtomicLong identitySequence = new AtomicLong(0);

    public DefaultDataset(@Nonnull GriffonDomainClass<T> domainClass, @Nonnull CriterionEvaluator criterionEvaluator) {
        this.domainClass = requireNonNull(domainClass, "Argument 'domainClass' cannot be null");
        this.criterionEvaluator = requireNonNull(criterionEvaluator, "Argument 'criterionEvaluator' cannot be null");
        this.name = domainClass.getName();
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public Long nextId() {
        return identitySequence.incrementAndGet();
    }

    @Override
    @Nonnull
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public T save(@Nonnull T entity) {
        requireNonNull(entity, ERROR_ENTITY_NULL);
        GriffonDomainClass<T> griffonClass = (GriffonDomainClass) entity.getGriffonClass();
        GriffonDomainProperty identity = griffonClass.getPropertyByName(IDENTITY);
        requireNonNull(identity, "Cannot save " + entity + " because it does not have an " + IDENTITY + " property.");

        Object identityValue = identity.getValue(entity);
        if (ROWS.containsKey(identityValue)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Updating entity with id = " + identityValue);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Saving entity with id = " + identityValue);
            }
        }
        ROWS.put(identityValue, entity);
        return entity;
    }

    @Override
    @Nonnull
    public T remove(@Nonnull T entity) {
        requireNonNull(entity, ERROR_ENTITY_NULL);
        GriffonDomainClass<T> griffonClass = domainClassOf(entity);
        GriffonDomainProperty identity = griffonClass.getIdentity();
        requireNonNull(identity, "Cannot remove " + entity + " because it does not have an " + IDENTITY + " property.");

        Object identityValue = identity.getValue(entity);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing entity with id = " + identityValue);
        }
        ROWS.remove(identityValue);
        return entity;
    }

    @Override
    @Nonnull
    public List<T> list() {
        List<T> entities = new ArrayList<T>();
        entities.addAll(ROWS.values());
        return entities;
    }

    @Override
    @Nonnull
    public List<T> list(@Nonnull Map<String, Object> options) {
        requireNonNull(options, ERROR_OPTIONS_NULL);
        int max = determineMax(options);
        int offset = determineOffset(options);
        String sort = determineSort(options);
        GriffonDomain.Comparator.Order order = determineOrder(options);

        List<T> entities = new ArrayList<>();
        synchronized (ROWS) {
            int size = ROWS.size();
            Iterator<T> iterator = ROWS.values().iterator();
            if (offset < size) {
                int count = 0;
                for (int row = 0; row < size && count < max; row++) {
                    T entity = iterator.next();
                    if (row < offset) continue;
                    entities.add(entity);
                    count++;
                }
            }
        }

        Collections.sort(entities, new GriffonDomain.Comparator(sort, order));
        return entities;
    }

    private int determineMax(@Nonnull Map<String, Object> options) {
        requireNonNull(options, ERROR_OPTIONS_NULL);
        Integer value = (Integer) options.get(KEY_MAX);
        return value != null ? value : Integer.MAX_VALUE;
    }

    private int determineOffset(@Nonnull Map<String, Object> options) {
        requireNonNull(options, ERROR_OPTIONS_NULL);
        Integer value = (Integer) options.get(KEY_OFFSET);
        return value != null ? value : 0;
    }

    private String determineSort(@Nonnull Map<String, Object> options) {
        requireNonNull(options, ERROR_OPTIONS_NULL);
        Object value = options.get(KEY_SORT);
        return value != null ? String.valueOf(value) : IDENTITY;
    }

    @Nonnull
    private GriffonDomain.Comparator.Order determineOrder(@Nonnull Map<String, Object> options) {
        requireNonNull(options, ERROR_OPTIONS_NULL);
        Object value = options.get(KEY_ORDER);
        String order = value != null ? String.valueOf(value) : "asc";
        return GriffonDomain.Comparator.Order.valueOf(order.toUpperCase());
    }

    @Override
    @Nullable
    public T fetch(@Nonnull Object identity) {
        requireNonNull(identity, "Cannot fetch entity because supplied identity is null");

        GriffonDomainProperty property = domainClass.getPropertyByName(IDENTITY);
        synchronized (ROWS) {
            for (T entity : ROWS.values()) {
                Object propertyValue = property.getValue(entity);
                if (TypeUtils.equals(identity, propertyValue)) {
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    @Nonnull
    public List<T> query(@Nonnull Object example) {
        requireNonNull(example, ERROR_EXAMPLE_NULL);
        Map<String, Object> params = new LinkedHashMap<>();
        if (domainClass.getClazz().isAssignableFrom(example.getClass())) {
            for (GriffonDomainProperty property : domainClassOf((GriffonDomain) example).getPersistentProperties()) {
                Object value = property.getValue((GriffonDomain) example);
                if (value != null) {
                    params.put(property.getName(), value);
                }
            }
        }
        return query(params);
    }

    @Override
    @Nonnull
    public List<T> query(@Nonnull Object example, @Nonnull Map<String, Object> options) {
        requireNonNull(example, ERROR_EXAMPLE_NULL);
        requireNonNull(options, ERROR_OPTIONS_NULL);
        Map<String, Object> params = new LinkedHashMap<>();
        if (domainClass.getClazz().isAssignableFrom(example.getClass())) {
            for (GriffonDomainProperty property : domainClassOf((GriffonDomain) example).getPersistentProperties()) {
                Object value = property.getValue((GriffonDomain) example);
                if (value != null) {
                    params.put(property.getName(), value);
                }
            }
        }
        return query(params, options);
    }

    @Override
    @Nonnull
    public List<T> query(@Nonnull Map<String, Object> params) {
        requireNonNull(params, ERROR_PARAMS_NULL);
        List<T> entities = new ArrayList<T>();
        if (params.isEmpty()) {
            return entities;
        }

        List<GriffonDomainClassProperty> properties = new ArrayList<>();
        for (String propertyName : params.keySet()) {
            GriffonDomainClassProperty property = domainClass.getPropertyByName(propertyName);
            requireNonNull(property, "Property " + propertyName + " is not a persistent property of " + domainClass.getClazz());
            properties.add(property);
        }

        synchronized (ROWS) {
            for (T entity : ROWS.values()) {
                boolean allMatch = true;
                for (GriffonDomainClassProperty property : properties) {
                    Object exampleValue = params.get(property.getName());
                    Object propertyValue = property.getValue(entity);
                    allMatch &= TypeUtils.equals(exampleValue, propertyValue);
                }
                if (allMatch) {
                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    @Override
    @Nonnull
    public List<T> query(@Nonnull Map<String, Object> params, @Nonnull Map<String, Object> options) {
        requireNonNull(params, ERROR_PARAMS_NULL);
        requireNonNull(options, ERROR_OPTIONS_NULL);
        List<T> entities = new ArrayList<T>();
        if (params.isEmpty()) {
            return entities;
        }

        List<GriffonDomainClassProperty> properties = new ArrayList<>();
        for (String propertyName : params.keySet()) {
            GriffonDomainClassProperty property = domainClass.getPropertyByName(propertyName);
            requireNonNull(property, "Property " + propertyName + " is not a persistent property of " + domainClass.getClazz());
            properties.add(property);
        }

        int max = determineMax(options);
        String sort = determineSort(options);
        GriffonDomain.Comparator.Order order = determineOrder(options);

        int count = 0;
        synchronized (ROWS) {
            for (T entity : ROWS.values()) {
                boolean allMatch = true;
                for (GriffonDomainClassProperty property : properties) {
                    Object exampleValue = params.get(property.getName());
                    Object propertyValue = property.getValue(entity);
                    allMatch &= TypeUtils.equals(exampleValue, propertyValue);
                }
                if (allMatch) {
                    entities.add(entity);
                    if (++count >= max) break;
                }
            }
        }
        Collections.sort(entities, new GriffonDomain.Comparator(sort, order));

        return entities;
    }

    @Override
    @Nonnull
    public List<T> query(@Nonnull Criterion criterion) {
        requireNonNull(criterion, ERROR_CRITERION_NULL);
        List<T> entities = new ArrayList<>();

        synchronized (ROWS) {
            for (T entity : ROWS.values()) {
                if (criterionEvaluator.eval(entity, criterion)) {
                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    @Override
    @Nonnull
    public List<T> query(@Nonnull Criterion criterion, @Nonnull Map<String, Object> options) {
        requireNonNull(criterion, ERROR_CRITERION_NULL);
        requireNonNull(options, ERROR_OPTIONS_NULL);
        List<T> entities = new ArrayList<>();

        int max = determineMax(options);
        String sort = determineSort(options);
        GriffonDomain.Comparator.Order order = determineOrder(options);

        int count = 0;
        synchronized (ROWS) {
            for (T entity : ROWS.values()) {
                if (criterionEvaluator.eval(entity, criterion)) {
                    entities.add(entity);
                    if (++count >= max) break;
                }
            }
        }
        Collections.sort(entities, new GriffonDomain.Comparator(sort, order));

        return entities;
    }

    @Override
    @Nullable
    public T first(@Nonnull Object example) {
        requireNonNull(example, ERROR_EXAMPLE_NULL);
        Map<String, Object> params = new LinkedHashMap<>();
        if (domainClass.getClazz().isAssignableFrom(example.getClass())) {
            for (GriffonDomainProperty property : domainClassOf((GriffonDomain) example).getPersistentProperties()) {
                Object value = property.getValue((GriffonDomain) example);
                if (value != null) {
                    params.put(property.getName(), value);
                }
            }
        }
        return first(params);
    }

    @Override
    @Nullable
    public T first(@Nonnull Map<String, Object> params) {
        requireNonNull(params, ERROR_PARAMS_NULL);
        if (params.isEmpty()) {
            return null;
        }

        List<GriffonDomainClassProperty> properties = new ArrayList<>();
        for (String propertyName : params.keySet()) {
            GriffonDomainClassProperty property = domainClass.getPropertyByName(propertyName);
            requireNonNull(property, "Property " + propertyName + " is not a persistent property of " + domainClass.getClazz());
            properties.add(property);
        }

        synchronized (ROWS) {
            for (T entity : ROWS.values()) {
                boolean allMatch = true;
                for (GriffonDomainClassProperty property : properties) {
                    Object exampleValue = params.get(property.getName());
                    Object propertyValue = property.getValue(entity);
                    allMatch &= TypeUtils.equals(exampleValue, propertyValue);
                }
                if (allMatch) {
                    return entity;
                }
            }
        }

        return null;
    }

    @Override
    @Nullable
    public T first(@Nonnull Criterion criterion) {
        requireNonNull(criterion, ERROR_CRITERION_NULL);

        synchronized (ROWS) {
            for (T entity : ROWS.values()) {
                if (criterionEvaluator.eval(entity, criterion)) {
                    return entity;
                }
            }
        }

        return null;
    }

    @Override
    public int size() {
        return ROWS.size();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private GriffonDomainClass<T> domainClassOf(@Nonnull GriffonDomain entity) {
        requireNonNull(entity, ERROR_ENTITY_NULL);
        return (GriffonDomainClass) entity.getGriffonClass();
    }
}
